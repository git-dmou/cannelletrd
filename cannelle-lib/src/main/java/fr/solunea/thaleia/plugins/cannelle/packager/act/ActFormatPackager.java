package fr.solunea.thaleia.plugins.cannelle.packager.act;

import fr.solunea.thaleia.model.ContentPropertyValue;
import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.model.User;
import fr.solunea.thaleia.model.dao.ContentPropertyValueDao;
import fr.solunea.thaleia.plugins.cannelle.contents.A7Content;
import fr.solunea.thaleia.plugins.cannelle.contents.A7Screen;
import fr.solunea.thaleia.plugins.cannelle.contents.ExportedModule;
import fr.solunea.thaleia.plugins.cannelle.packager.AbstractPackager;
import fr.solunea.thaleia.plugins.cannelle.packager.act.container.ContainerFactory;
import fr.solunea.thaleia.plugins.cannelle.packager.act.container.IContainer;
import fr.solunea.thaleia.plugins.cannelle.packager.act.specific.ISpecificTreatment;
import fr.solunea.thaleia.plugins.cannelle.packager.act.specific.SpecificFactory;
import fr.solunea.thaleia.plugins.cannelle.utils.ModuleResources;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.service.CustomizationService;
import fr.solunea.thaleia.service.utils.FilenamesUtils;
import fr.solunea.thaleia.service.utils.ZipUtils;
import fr.solunea.thaleia.service.utils.export.ExportFormat;
import fr.solunea.thaleia.service.utils.scorm.MasteryScore;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.ThaleiaApplication;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Transforme le module en ACT, et lui associe tous les écrans (dans le ACT, et
 * copie les fichiers de ces écrans). Ajoute un conteneur et une communication
 * scorm.
 */
public class ActFormatPackager extends AbstractPackager {

    /**
     * La valeur par défaut de la note de passage d'un module (exprimée entre 0
     * et 100), si celle-ci n'est pas définie pour l'écran dans Thaleia.
     */
    private static final int DEFAULT_MASTERYSCORE = 80;

    private final static String FORMAT_ACT_CONTENT_ADDBEFORE = "addbefore";
    private final static String FORMAT_ACT_CONTENT_ADDAFTER = "addafter";
    private final static String FORMAT_ACT_CONTENT = "export.format.act.content";
    private final static String FORMAT_ACT_CONTENT_INSERT_CONTENTTAG_BEFORE =
            "export.format.act.content.addbefore" + ".insertnode.tag";
    private final static String FORMAT_ACT_CONTENT_INSERT_CONTENTTAG_AFTER =
            "export.format.act.content.addafter" + ".insertnode.tag";
    private final static String FORMAT_ACT_CONTENT_INSERT_MENUTAG_BEFORE =
            "export.format.act.content.addbefore" + ".insertmenu.tag";
    private final static String FORMAT_ACT_CONTENT_INSERT_MENUTAG_AFTER =
            "export.format.act.content.addafter" + ".insertmenu.tag";
    private final static String FORMAT_ACT_CONTENT_MAIN_NODE_TAG = "export.format.act.content.main.insertnode.tag";
    private final static String FORMAT_ACT_CONTENT_MAIN_MENU_TAG = "export.format.act.content.main.insertmenu.tag";
    private final static String FORMAT_ACT_CONTENT_MAIN_TITLE = "export.format.act.content.main.title";
    private final static String FORMAT_ACT_CONTENT_MAIN_SUBTITLE = "export.format.act.content.main.subtitle";
    private final static String FORMAT_ACT_CONTENT_MAIN_MENUTITLE = "export.format.act.content.main.menutitle";
    private final static String FORMAT_ACT_CONTENT_MAIN_SCORE = "export.format.act.content.main.scoreRollup";
    private final static String FORMAT_ACT_CONTENT_MAIN_PROGRESS = "export.format.act.content.main.progressRollup";
    private final static String FORMAT_ACT_CONTENT_MAIN_MENU_APPEAR = "export.format.act.content.main.appearinmenu";
    private final static String FORMAT_ACT_CONTENT_MAIN_MENU_CLICK = "export.format.act.content.main.clickinmenu";
    private final static String FORMAT_ACT_CONTENT_MAIN_ATTRIBUTES = "export.format.act.content.main.attributes";

    private static final Logger logger = Logger.getLogger(ActFormatPackager.class);

    private static final String TRUE = "true";

    /**
     * Le nom du répertoire du module dans lequel seront placés les fichiers des
     * ressources
     */
    private static final String RESOURCES_DIR = "resources";

    public ActFormatPackager(Parameters parameters, ResourcesHandler resourcesHandler) {
        super(parameters, resourcesHandler);
    }

    @Override
    public void run(ExportedModule module, ExportFormat format, User user) throws DetailedException {

        // Création du fichier ACT dans le répertoire d'export, d'après le
        // modèle.
        File actFile = createActInModuleDestination(module);

        // Modification de l'extension du ACT en .xml
        actFile = renameExtension(actFile);

        // Le fichier Act à modifier
        module.setAct(new Act(actFile));

        // Récupération du masteryScore à fixer dans le ACT
        String masteryScore = module.getMasteryScore(Integer.toString(DEFAULT_MASTERYSCORE));
        module.getAct().setMasteryScore(MasteryScore.getValidScore(masteryScore, DEFAULT_MASTERYSCORE));

        // Récupération dans les paramètres des balises propriétés du Act à
        // mettre à jour
        Properties actProperties = parseActProperties(getParameters(), module);
        // Mise à jour de ces propriétés dans le ACT
        module.getAct().setProperties(actProperties);

        // Mise à jour de la navigation
        module.getAct().setStoreProgression(getParameters().getValue(Parameters.FORMAT_ACT_UPDATE_NAVIGATION_PROGRESSION));
        module.getAct().setScoreOnce(getParameters().getValue(Parameters.FORMAT_ACT_UPDATE_NAVIGATION_SCORE));

        // Ajout des contenus.
        addAllContents(module);

        // Renommage des contenus (normalisation des noms de fichiers et
        // extension en .xml)
        renameContents(module);
        // Ajout des fichiers de conteneur (méta html...)
        addContainerFiles(module, format, user);

        // Ajout des ressources du module (fichiers et URL)
        addResources(module);

        // Application de la personnalisation, si elle existe
        customize(module, user);

        // Traitements spécifiques
        doSpecificActions(module);

        // Si on a demandé une exécution locale, on effectue le traitement de
        // transformation en exe
        // Cette opération doit être la dernière, car après les fichiers (HTML,
        // ACT, A7...) sont paquetés dans un Zip.
        if (module.isLocalExecution()) {
            try {
                logger.debug("Création du conteneur...");
                String containerClassName = getParameters().getValue(Parameters.FORMAT_ACT_CONTAINER_CLASS);
                IContainer container = ContainerFactory.getInstance().getContainer(containerClassName,
                        getResourcesHandler());
                logger.debug("Le conteneur transforme en exécutable...");
                container.transformToExe(getParameters(), module);

            } catch (DetailedException e) {
                e.addMessage("Impossible d'ajouter les fichiers du conteneur.");
                throw e;
            }
        }
    }

    /**
     * Copie un fichier ACT dans le répertoire de destination du module.
     *
     * @return le fichier Act qui a été placé dans le ExportedModule.
     */
    private File createActInModuleDestination(ExportedModule module) throws DetailedException {
        try {
            // Récupération du modèle de fichier ACT à utiliser
            String confRoot = getParameters().getValue(Parameters.PACKAGER_CONF_DIR);
            File actModel = getResourcesHandler().getParametrizedResourceFile(confRoot, getParameters(),
                    Parameters.ACT_MODEL_FILENAME, module.getLocale().getName());

            // Copie du fichier modèle dans le répertoire de destination
            File actFile = new File(module.getDestinationDir().getAbsolutePath() + File.separator + actModel.getName());
            FileUtils.copyFile(actModel, actFile);

            logger.debug("Le modèle pris en compte est : " + actModel + " Fichier : " + actFile.getAbsolutePath());

            return actFile;

        } catch (Exception e) {
            throw new DetailedException(e).addMessage("Impossible de récupérer le modèle de fichier ACT.");
        }
    }

    /**
     * @return le fichier, dont l'extension a été renommée en ".xml".
     */
    private File renameExtension(File actFile) throws DetailedException {
        try {
            String newActFileName = FilenamesUtils.changeExtension(actFile, "xml");
            if (newActFileName.equals(actFile.getName())) {
                return actFile;

            } else {
                // Si le ACT n'a PAS l'extension demandée (.xml), alors on le
                // renomme.
                File destination = new File(actFile.getParentFile() + File.separator + newActFileName);
                FileUtils.moveFile(actFile, destination);
                return destination;
            }

        } catch (Exception e) {
            throw new DetailedException(e).addMessage("Impossible de renommer le fichier ACT.");
        }
    }

    /**
     * Recherche de toutes les propriétés de type
     * export.format.act.update.properties.X.key /
     * export.format.act.update.properties.X.value et renvoie le résultat dans
     * des propriétés de type (valeur de key) / (valeur de value). Ces valeurs
     * sont les balises 'property' à ajouter dans le ACT.
     */
    private Properties parseActProperties(Parameters parameters, ExportedModule module) {
        Properties result = new Properties();

        // Recherche de toutes les propriétés de type
        // export.format.act.update.properties.X.key /
        // export.format.act.update.properties.X.value
        List<String> keys = parameters.getKeysStartingWith(Parameters.FORMAT_ACT_UPDATE_PROPERTY);
        boolean searchNextOrder = true;
        int order = 0;
        while (searchNextOrder) {
            // On recherche la clé format.act.update.properties.[order].key
            String key = Parameters.FORMAT_ACT_UPDATE_PROPERTY + "." + order + ".key";
            if (keys.contains(key)) {
                String value = Parameters.FORMAT_ACT_UPDATE_PROPERTY + "." + order + ".value";
                result.put(parameters.getValue(key), ParameterParser.parseParameter(parameters.getValue(value),
                        module, module.getLocale(), parameters));
                // On cherchera le numéro d'ordre suivant
                order++;
            } else {
                // Rien de trouvé pour ce numéro d'ordre : on ne cherche pas les
                // suivants
                searchNextOrder = false;
            }
        }

        return result;
    }

    /**
     * Ajoute les contenus dans le Act
     */
    private void addAllContents(ExportedModule module) throws DetailedException {
        try {
            // Le mainContent sera-t-il un écran d'introduction ou bien un écran
            // de contenu ?
            boolean mainContentInIntro = isMainContentInIntro(getParameters());
            logger.debug("Le mainContent sera-t-il un écran d'introduction (sinon un écran de contenu) ? "
                    + mainContentInIntro);

            logger.debug("Ajout d'écrans d'introduction...");
            String lastIntroContentId = addContents(getParameters(), FORMAT_ACT_CONTENT_ADDBEFORE, module.getLocale()
                    , module.getAct());

            logger.debug("Ajout d'écrans de contenu...");
            boolean setFirstContentAsMainContent = !mainContentInIntro;
            addMainContent(module.getScreens(), getParameters(), setFirstContentAsMainContent, lastIntroContentId,
                    module.getLocale(), module.getAct());

            logger.debug("Ajout d'écrans de bilan...");
            addContents(getParameters(), FORMAT_ACT_CONTENT_ADDAFTER, module.getLocale(), module.getAct());

            logger.debug("Enregistrement du ACT...");
            module.getAct().commit();

        } catch (DetailedException e) {
            e.addMessage("Impossible de mettre à jour les contenus complémentaires du ACT.");
            throw e;
        }
    }

    private void renameContents(ExportedModule module) throws DetailedException {
        try {
            module.getAct().normalizeFileNames(true, "xml");
        } catch (DetailedException e) {
            e.addMessage("Impossible de mettre à jour les noms des contenus du ACT.");
            throw e;
        }
    }

    private void addContainerFiles(ExportedModule module, ExportFormat format, User user) throws DetailedException {
        try {
            logger.debug("Création du conteneur...");
            String containerClassName = getParameters().getValue(Parameters.FORMAT_ACT_CONTAINER_CLASS);
            IContainer container = ContainerFactory.getInstance().getContainer(containerClassName,
                    getResourcesHandler());
            logger.debug("Copie et configuration des fichiers du conteneur...");
            container.addFiles(getParameters(), module, format, user);

        } catch (DetailedException e) {
            e.addMessage("Impossible d'ajouter les fichiers du conteneur.");
            throw e;
        }

    }

    private void addResources(ExportedModule module) throws DetailedException {
        try {
            addFileResources(module);

            addURLResources(module);

            module.getAct().commit();

        } catch (DetailedException e) {
            e.addMessage("Impossible d'ajouter les ressources du module.");
            throw e;
        }
    }

    private void customize(ExportedModule module, User user) {
        try {
            // Recherche d'un éventuel fichier de personnalisation, pour le
            // domaine
            // de sécurité de l'utilisateur
            String customizationName = ActFormatPackager.class.getName();
            File customizationFile = new CustomizationService(ThaleiaApplication.get().contextService, ThaleiaApplication.get().getConfiguration()).getCustomizationFile(customizationName, null, user.getDomain(), user.getObjectContext());

            logger.debug("Personnalisation à appliquer : " + customizationFile);

            if (customizationFile != null) {
                // On décompresse l'archive dans le répertoire des fichiers du
                // module
                try {
                    logger.debug("Décompression de " + customizationFile.getAbsolutePath() + " dans "
                            + module.getDestinationDir().getAbsolutePath());
                    ZipUtils.doDezip(customizationFile.getAbsolutePath(),
                            module.getDestinationDir().getAbsolutePath(), false, true, null);
                } catch (Exception e) {
                    logger.warn(e);
                }

            } // sinon pas de personnalisation

        } catch (DetailedException e) {
            logger.warn(e);
            // On ne fait rien
        }
    }

    private void doSpecificActions(ExportedModule module) throws DetailedException {
        try {
            logger.debug("Recherche du nombre de classes de traitement spécifique à déclencher...");
            int max = -1;
            try {
                max = getParameters().getMaxIndexForKey(Parameters.FORMAT_ACT_SPECIFIC);
            } catch (DetailedException e) {
                e.addMessage("Pas de traitements spécifiques détectés.");
                logger.debug(e.toString());
                // On sort proprement de la méthode
            }
            logger.debug("Indice maximal des classes de traitement spécifique à déclencher : " + max);

            logger.debug("Parcours des classes de traitement spécifique...");
            for (int order = 0; order <= max; order++) {
                String key = Parameters.FORMAT_ACT_SPECIFIC + "." + order;
                String value = getParameters().getValue(key);
                logger.debug("Retrouvé l'attribut '" + key + "'='" + value
                        + "' décrivant la classe de traitement à lancer.");

                logger.debug("Création de la classe de traitement '" + value + "'...");
                ISpecificTreatment specific = SpecificFactory.getInstance().getSpecific(value, getResourcesHandler(),
                        module, getParameters(),
                        "format.act.container.specific." + order, module.getLocale());

                logger.debug("Lancement de la classe de traitement '" + specific.getClass().getName() + "'...");
                specific.run();
            }

        } catch (DetailedException e) {
            e.addMessage("Impossible d'effectuer les traitement spécifiques.");
            throw e;
        }

    }

    /**
     * @return true si dans les paramètres un (et un seul) écran d'introduction
     * (addBefore) est identifié comme étant le mainContent pour le ACT.
     * False si 0.
     * @throws DetailedException si deux ou plus écrans d'introduction sont identifiés comme
     *                           mainContent.
     */
    private boolean isMainContentInIntro(Parameters parameters) throws DetailedException {
        // On recherche des clés du type :
        // "format.act.content.addbefore.X.maincontent"

        // On recherche le X maximum en commençant par 0
        int max;
        try {
            max = parameters.getMaxIndexForKey(FORMAT_ACT_CONTENT + "." + FORMAT_ACT_CONTENT_ADDBEFORE);
        } catch (DetailedException e) {
            logger.debug(
                    "Pas d'écran trouvé comme étant à ajouter en position '" + FORMAT_ACT_CONTENT_ADDBEFORE + "'.");
            // Pas d'écran d'intro, donc forcément par de mainContent parmi eux.
            // On sort proprement de la méthode, avec résultat faux
            return false;
        }

        // On recherche des clés du type :
        // "format.act.content.addbefore"
        List<String> keys = parameters.getKeysStartingWith(FORMAT_ACT_CONTENT + "." + FORMAT_ACT_CONTENT_ADDBEFORE);

        // On compte le nombre d'occurences d'écran paramétrés comme maincontent
        int count = 0;
        for (int order = 0; order <= max; order++) {
            // On recherche la clé maincontent pour cet indice
            // format.act.content.[position].[order].a7file
            String mainContentkey =
                    FORMAT_ACT_CONTENT + "." + FORMAT_ACT_CONTENT_ADDBEFORE + "." + order + ".maincontent";
            if (keys.contains(mainContentkey)) {
                String mainContent = parameters.getValue(mainContentkey);
                if (mainContent.equalsIgnoreCase(TRUE)) {
                    count = +1;
                }
            }
        }

        if (count == 0) {
            return false;
        } else if (count == 1) {
            return true;
        } else {
            throw new DetailedException("Plus d'un écran d'introduction a été paramétré comme 'maincontent' !");
        }
    }

    /**
     * Récupère les contenus identifié dans les paramètres du type
     * format.act.content.[position] et les ajoute dans le ACT à la position
     * demandée.
     *
     * @param position Parameters.FORMAT_ACT_CONTENT_ADDBEFORE ou
     *                 Parameters.FORMAT_ACT_CONTENT_ADDAFTER
     * @return l'identifiant du dernier content ajouté (= attribut id de sa
     * balise content). "" Si aucun contenu ajouté.
     */
    private String addContents(Parameters parameters, String position, Locale locale, Act act) throws DetailedException {

        try {
            if (position == null) {
                throw new DetailedException("La position demandée est nulle !");
            }

            if (!position.equals(FORMAT_ACT_CONTENT_ADDBEFORE) && !position.equals(FORMAT_ACT_CONTENT_ADDAFTER)) {
                throw new DetailedException("La position '" + position + "' n'est pas implémentée !");
            }

            String result = "";

            // les keys sont du type
            // "format.act.content.[position].X.description",
            // "format.act.content.[position].X.type", ...
            // On recherche le X maximum en commençant par 0
            int max;
            try {
                max = parameters.getMaxIndexForKey(FORMAT_ACT_CONTENT + "." + position);
            } catch (DetailedException e) {
                e.addMessage("Pas d'écran trouvé comme étant à ajouter en position '" + position + "'.");
                logger.info(e.toString());
                // On sort proprement de la méthode
                return result;
            }

            // Le nom des balises où insérer les contenus et les items de menu
            String insertContentTagName = "";
            String insertMenuTagName = "";
            if (position == FORMAT_ACT_CONTENT_ADDBEFORE) {
                insertContentTagName = parameters.getValue(FORMAT_ACT_CONTENT_INSERT_CONTENTTAG_BEFORE);
                insertMenuTagName = parameters.getValue(FORMAT_ACT_CONTENT_INSERT_MENUTAG_BEFORE);
            } else if (position == FORMAT_ACT_CONTENT_ADDAFTER) {
                insertContentTagName = parameters.getValue(FORMAT_ACT_CONTENT_INSERT_CONTENTTAG_AFTER);
                insertMenuTagName = parameters.getValue(FORMAT_ACT_CONTENT_INSERT_MENUTAG_AFTER);
            }

            // On récupère toutes les clés
            List<String> keys = parameters.getKeysStartingWith(FORMAT_ACT_CONTENT + "." + position);

            // ORDRE DE TRAITEMENT :
            // On les parcourt du dernier au premier, pour les ajouter dans le
            // bon ordre puisque l'ajout d'un écran dans le ACT se fait devant
            // les autres : avant la balise d'insertion.
            for (int order = max; order >= 0; order = order - 1) {
                // On recherche la description format
                // format.act.content.[position].[order].a7file
                String a7key = FORMAT_ACT_CONTENT + "." + position + "." + order + ".a7file";
                if (keys.contains(a7key)) {
                    // Un a7 existe pour le numéro d'ordre n°order
                    String confRoot = parameters.getValue(Parameters.PACKAGER_CONF_DIR);
                    File a7File = getResourcesHandler().getParametrizedResourceFile(confRoot, parameters, a7key,
                            locale.getName());

                    // Récupération des paramètres titre, sous-tire, etc. tels
                    // qu'il apparaissent dans les paramètres
                    String titleKey = FORMAT_ACT_CONTENT + "." + position + "." + order + ".title";
                    String title = parameters.getValue(titleKey);
                    String subtitleKey = FORMAT_ACT_CONTENT + "." + position + "." + order + ".subtitle";
                    String subtitle = parameters.getValue(subtitleKey);
                    String menutitleKey = FORMAT_ACT_CONTENT + "." + position + "." + order + ".menutitle";
                    String menutitle = parameters.getValue(menutitleKey);
                    String scoreRollupKey = FORMAT_ACT_CONTENT + "." + position + "." + order + ".scoreRollup";
                    String scoreRollup = parameters.getValue(scoreRollupKey);
                    String progressRollupKey = FORMAT_ACT_CONTENT + "." + position + "." + order + ".progressRollup";
                    String progressRollup = parameters.getValue(progressRollupKey);
                    String appearinmenuKey = FORMAT_ACT_CONTENT + "." + position + "." + order + ".appearinmenu";
                    String appearinmenu = parameters.getValue(appearinmenuKey);
                    String menuClickableKey = FORMAT_ACT_CONTENT + "." + position + "." + order + ".clickinmenu";
                    String menuClickable = parameters.getValue(menuClickableKey);

                    // Instanciation du contenu
                    A7Content content = A7Content.createFromDirectory(a7File.getParentFile());

                    // Les attributs de la balise content à créer
                    Map<String, String> attributes = new HashMap<String, String>();
                    // Dans les paramètres, on recherche les paramètres de la
                    // forme :
                    // format.act.content.[position].X.attributes.[nom de
                    // l'attribut]=[valeur de l'attribut]
                    String attributesParamNameStart =
                            FORMAT_ACT_CONTENT + "." + position + "." + order + ".attributes.";
                    List<String> attritubesParams = parameters.getKeysStartingWith(attributesParamNameStart);
                    for (String attributeParam : attritubesParams) {
                        String attribute = attributeParam.substring(attributesParamNameStart.length(),
                                attributeParam.length());
                        String value = parameters.getValue(attributeParam);
                        logger.debug("Retrouvé l'attribut '" + attribute + "'='" + value
                                + "' à ajouter à la balise content.");
                        attributes.put(attribute, value);
                    }

                    String durationKey = "format.act.content." + position + "." + order + ".attributes.duration";
                    String durationString = parameters.getValue(durationKey);
                    int duration = 0;
                    try {
                        duration = new Integer(durationString).intValue();
                    } catch (Exception e) {
                        logger.info(
                                "Impossible de fixer la durée d'un contenu à '" + durationString + "' : on fixe 0.");
                        duration = 0;
                    }

                    // Ce contenu doit-il être enregistré comme mainContent ?
                    boolean isMainContent = false;
                    String mainContentKey = "format.act.content." + position + "." + order + ".maincontent";
                    String mainContentString = parameters.getValue(mainContentKey);
                    if (mainContentString.equalsIgnoreCase(TRUE)) {
                        isMainContent = true;
                    }

                    String contentid = act.addContent(content, attributes, appearinmenu, title, subtitle, scoreRollup
                            , progressRollup, menutitle, menuClickable, insertContentTagName, insertMenuTagName,
                            duration, isMainContent, "", locale, parameters);

                    // Si result est vide, alors c'est qu'on en est au premier
                    // écran d'intro/bilan ajouté. Donc le DERNIER écran des
                    // écrans d'intro/bilan dans l'ordre de consultation. Donc
                    // c'est cette valeur qu'il faut renvoyer.
                    if (result.length() == 0) {
                        result = contentid;
                    }

                } else {
                    // Rien de trouvé pour ce numéro d'ordre : on journalise
                    // mais on n'arrête pas le traitement
                    logger.info("Impossible d'ajouter l'écran en position '" + position + "' n°" + order
                            + " : la clé requise '" + a7key
                            + "' n'est pas présente ! Vérifiez la cohérence des paramètres.");
                }
            }

            logger.debug("Nettoyage du fichier act...");
            List<String> tagnames = new ArrayList<String>();
            tagnames.add(insertContentTagName);
            tagnames.add(insertMenuTagName);
            act.cleanTags(tagnames);

            return result;

        } catch (DetailedException e) {
            e.addMessage("Impossible d'ajouter les écrans au ACT en position '" + position + "'.");
            throw e;
        }
    }

    /**
     * Ajoute les contenus à l'endroit prévu dans le fichier ACT.
     *
     * @param lastIntroContentId l'identifiant (content id) du dernier des écrans
     *                           d'introduction
     */
    private void addMainContent(List<A7Screen> contents, Parameters parameters, boolean setFirstContentAsMainContent,
                                String lastIntroContentId, Locale locale, Act act) throws DetailedException {
        try {
            if (contents == null) {
                throw new DetailedException("Les contenus demandés sont nuls !");
            }
            logger.debug("Ajout de " + contents.size() + " écrans de contenu...");

            // Récupération du nom de la balise où insérer les contenus dans le
            // contentsTree
            String nodeInsertionTagName = parameters.getValue(FORMAT_ACT_CONTENT_MAIN_NODE_TAG);
            // Récupération du nom de la balise où insérer les contenus dans le
            // menu
            String menuInsertionTagName = parameters.getValue(FORMAT_ACT_CONTENT_MAIN_MENU_TAG);
            // Titre du contenu
            String title = parameters.getValue(FORMAT_ACT_CONTENT_MAIN_TITLE);
            // Sous-Titre du contenu
            String subtitle = parameters.getValue(FORMAT_ACT_CONTENT_MAIN_SUBTITLE);
            // Intitulé du contenu dans le menu
            String menutitle = parameters.getValue(FORMAT_ACT_CONTENT_MAIN_MENUTITLE);
            // Valeur des attributs scoreRollup
            String scoreRollup = parameters.getValue(FORMAT_ACT_CONTENT_MAIN_SCORE);
            // Valeur des attributs progressRollup
            String progressRollup = parameters.getValue(FORMAT_ACT_CONTENT_MAIN_PROGRESS);
            // Les contenus apparaissent-ils dans les menus ?
            String showInMenu = parameters.getValue(FORMAT_ACT_CONTENT_MAIN_MENU_APPEAR);
            // Les contenus sont-ils clickables dans les menus ?
            String menuClickable = parameters.getValue(FORMAT_ACT_CONTENT_MAIN_MENU_CLICK);

            // Les attributs de la balise content à créer
            Map<String, String> attributes = new HashMap<String, String>();
            // Dans les paramètres, on recherche les paramètres de la
            // forme :
            // format.act.content.[position].X.attributes.[nom de
            // l'attribut]=[valeur de l'attribut]
            String attributesParamNameStart = FORMAT_ACT_CONTENT_MAIN_ATTRIBUTES + ".";
            List<String> attritubesParams = parameters.getKeysStartingWith(attributesParamNameStart);
            for (String attributeParam : attritubesParams) {
                String attribute = attributeParam.substring(attributesParamNameStart.length(), attributeParam.length());
                String value = parameters.getValue(attributeParam);
                logger.debug("Retrouvé l'attribut '" + attribute + "'='" + value + "' à ajouter à la balise content.");
                attributes.put(attribute, value);
            }

            act.addContents(contents, attributes, showInMenu, title, subtitle, scoreRollup, progressRollup, menutitle
                    , menuClickable, nodeInsertionTagName, menuInsertionTagName, setFirstContentAsMainContent,
                    lastIntroContentId, parameters, locale);

            logger.debug("Nettoyage du fichier act...");
            List<String> tagnames = new ArrayList<String>();
            tagnames.add(nodeInsertionTagName);
            tagnames.add(menuInsertionTagName);
            act.cleanTags(tagnames);

        } catch (DetailedException e) {
            e.addMessage("Impossible d'ajouter les écrans de contenu au ACT.");
            throw e;
        }

    }

    /**
     * Parcourt toutes les propriétés de ce module qui décrivent des ressources
     * de type fichier, et les inscrit dans le ACT puis copie le binaire
     * correspondant dans le répertoire de ressources du module.
     */
    private void addFileResources(ExportedModule module) throws DetailedException {
        // La liste de toutes les propriétés du module qui décrivent une
        // ressource de type fichier : ce sont des couples de type
        // ResourceXName / ResourceXFile
        Map<String, String> fileResourcesContentPropertiesNames =
                ModuleResources.getFileResourcesContentPropertiesNames();

        // On trie les noms des propriétés, afin d'obtenir les ressources dans l'ordre de leur définition dans le
        // ficheir Excel.
        Set<String> propertyNamesSet = fileResourcesContentPropertiesNames.keySet();
        List<String> propertyNames = new ArrayList<>();
        propertyNames.addAll(propertyNamesSet);
        Collections.sort(propertyNames);

        for (String nameContentPropertiesName : propertyNames) {
            // On recherche la propriété du module qui porte ce nom
            // Exemple : on recherche la valeur de la ContentProperty dont
            // le nom est 'resource1Name'
            String nameContentPropertiesValue = module.getContentVersionPropertyValue(nameContentPropertiesName,
                    module.getLocale(), "");

            if (!"".equals(nameContentPropertiesValue)) {
                // Si cette valeur est définie pour ce module

                // Le nom de la property associée qui décrit le fichier de
                // cette ressource.
                // Exemple : 'resource1File'
                String fileContentPropertiesName = fileResourcesContentPropertiesNames.get(nameContentPropertiesName);

                // On recherche la ContentProperty qui contient le binaire.
                // Exemple : on recherche la ContentProperty du module dont
                // le nom est 'resource1File'
                ContentPropertyValue fileContentPropertiesValue =
                        module.getContentVersionProperty(fileContentPropertiesName, module.getLocale());

                // On récupère le binaire
                ContentPropertyValueDao contentPropertyValueDao = new ContentPropertyValueDao(ThaleiaApplication.get().getConfiguration().getLocalDataDir().getAbsolutePath(),
                ThaleiaApplication.get().getConfiguration().getBinaryPropertyType(), module.getLocale().getObjectContext());
                File binary = contentPropertyValueDao.getFile(fileContentPropertiesValue);

                if (binary != null) {

                    // On ajoute la propriété dans le ACT
                    // on met comme valeur : le nom du répertoire des ressources
                    // + le nom du fichier
                    try {
                        module.getAct().addResource(nameContentPropertiesValue,
                                RESOURCES_DIR + "/" + binary.getName(), binary, true);
                    } catch (DetailedException e) {
                        throw new DetailedException(e).addMessage(
                                "Impossible d'ajouter dans le XML la ressource du module '" + nameContentPropertiesValue
                                        + "' -> '" + RESOURCES_DIR + "/" + fileContentPropertiesValue.getValue()
                                        + "'.");
                    }

                    // On copie le binaire dans le répertoire des ressources du
                    // module
                    // exporté
                    try {
                        // TODO On devrait prendre en compte les noms de fichiers
                        // identiques.

                        // On copie dans un répertoire "resources" à coté du fichier
                        // Act.
                        File resourcesDir = new File(
                                module.getAct().getActFile().getParentFile().getAbsolutePath() + File.separator
                                        + RESOURCES_DIR);
                        FileUtils.copyFileToDirectory(binary, resourcesDir);
                    } catch (IOException e) {
                        throw new DetailedException(e).addMessage(
                                "Impossible de copier le binaire de la ressource du module '"
                                        + fileContentPropertiesValue + "'.");
                    }
                }
            }
        }
    }

    /**
     * Parcourt toutes les propriétés de ce module qui décrivent des ressources
     * de type URL, et les inscrit dans le ACT.
     */
    private void addURLResources(ExportedModule module) throws DetailedException {
        // La liste de toutes les propriétés du module qui décrivent une
        // ressource de type URL : ce sont des couples de type
        // ResourceXName / ResourceXURL
        Map<String, String> urlResourcesContentPropertiesNames =
                ModuleResources.getURLResourcesContentPropertiesNames();

        for (String nameContentPropertiesName : urlResourcesContentPropertiesNames.keySet()) {
            // On recherche la propriété du module qui porte ce nom
            // Exemple : on recherche la valeur de la ContentProperty dont
            // le nom est 'resource1Name'
            String nameContentPropertiesValue = module.getContentVersionPropertyValue(nameContentPropertiesName,
                    module.getLocale(), "");

            if (!"".equals(nameContentPropertiesValue)) {
                // Si cette valeur est définie pour ce module

                // Le nom de la property associée qui décrit l'URL de
                // cette ressource.
                // Exemple : 'resource1URL'
                String urlContentPropertiesName = urlResourcesContentPropertiesNames.get(nameContentPropertiesName);

                // On recherche la ContentProperty qui contient l'URL.
                // Exemple : on recherche la ContentProperty du module dont
                // le nom est 'resource1File'
                ContentPropertyValue urlContentPropertiesValue =
                        module.getContentVersionProperty(urlContentPropertiesName, module.getLocale());

                // On ajoute la propriété dans le ACT
                // on met comme valeur l'URL
                try {
                    module.getAct().addResource(nameContentPropertiesValue, urlContentPropertiesValue.getValue(),
                            null, true);
                } catch (DetailedException e) {
                    throw new DetailedException(e).addMessage(
                            "Impossible d'ajouter dans le XML la ressource du module '" + nameContentPropertiesValue
                                    + "' -> '" + urlContentPropertiesValue.getValue() + "'.");
                }
            }
        }
    }

}
