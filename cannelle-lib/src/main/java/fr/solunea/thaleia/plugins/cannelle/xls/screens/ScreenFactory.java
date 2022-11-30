package fr.solunea.thaleia.plugins.cannelle.xls.screens;

import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.model.User;
import fr.solunea.thaleia.plugins.cannelle.contents.IContent;
import fr.solunea.thaleia.plugins.cannelle.utils.CellsRange;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.v6.uctranslatemodule.CannelleScreenParamTranslator;
import fr.solunea.thaleia.plugins.cannelle.v6.uctranslatemodule.DeeplTranslator;
import fr.solunea.thaleia.plugins.cannelle.v6.uctranslatemodule.ITranslatorAPI;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.generator.IContentGenerator;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.Dictionary;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.IScreenParameter;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.screenparser.AbstractExcelTemplate;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.screenparser.CannelleScreenParameters;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.screenparser.IExcelTemplate;
import fr.solunea.thaleia.service.utils.ClassFactory;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;

import java.io.File;
import java.util.*;

/**
 * Interprète les paramètres de configuration pour instancier les Templates qui
 * vont être utilisés pour produire les Screen. Permet de lancer la génération
 * des contenus par l'analyse du fichier XLS via les templates qui ont été
 * initialisés.
 *
 * @author RMAR
 */
public class ScreenFactory {

    private static final Logger logger = Logger.getLogger(ScreenFactory.class);
    /**
     * Dans le répertoire temporaire du traitement, le nom du répertoire dans
     * lequel seront préparés les fichiers des écrans produits.
     */
    private static final String SCREENS_TEMP_DIR_NAME = "screens";
    /**
     * Les gabarits d'écran, et leur configuration, définis en configuration. La
     * clé est leur intitulé (paramètre templates.X.title, que l'on recherche
     * dans les cellules XLS pour identifier le template).
     */
    final private Map<String, IExcelTemplate> templates;
    /**
     * Les paramètres de configuration.
     */
    final private Parameters parameters;
    protected List<IScreenParameter> screenParameters;
    protected CannelleScreenParameters cannelleScreenParameters;
    private IExcelTemplate template;

    /**
     * Instancie les classes de traitement de templates qui sont définis dans la
     * conf.
     */
    public ScreenFactory(Parameters parameters, ResourcesHandler resourcesHandler) throws DetailedException {
        this.templates = new HashMap<String, IExcelTemplate>();
        this.parameters = parameters;

        Dictionary dictionary = new Dictionary(parameters);

        // On recherche les templates définis dans la conf.
        int templateMaxIndex;
        try {
            logger.debug("Recherche des templates définis par la clé '" + Parameters.TEMPLATES + "'...");
            templateMaxIndex = this.parameters.getMaxIndexForKey(Parameters.TEMPLATES);
            logger.debug("Les index des templates vont de 0 à " + templateMaxIndex + ".");

            for (int i = 0; i <= templateMaxIndex; i++) {
                logger.debug("Analyse du template " + i + "...");
                try {
                    // Instancie le template i
                    AbstractExcelTemplate template = parseTemplateFromParameters(i);

                    // On recherche le titre du modèle demandé
                    String templateTitle = this.parameters.getValue(Parameters.TEMPLATES + "." + i + ".title");
                    logger.debug("Titre du template " + i + " : '" + templateTitle + "'");

                    // On recherche la classe de traitement de génération du
                    // contenu
                    String contentGeneratorClassName = this.parameters.getValue(
                            Parameters.TEMPLATES + "." + i + ".contentgenerator.class");
                    // On instancie l'objet de génération des écrans
                    // correspondant,
                    IContentGenerator contentGenerator = getContentGeneratorFromProperties(contentGeneratorClassName);
                    // On lui transmet l'accès aux ressources
                    contentGenerator.setResourcesHandler(resourcesHandler);
                    // On lui transmet les paramètres
                    contentGenerator.setParameters(parameters);
                    // On lui indique le préxife des paramètres qui le
                    // concernent
                    contentGenerator.setParametersPrefix(Parameters.TEMPLATES + "." + i + ".contentgenerator.");
                    // On l'associe au template
                    template.setContentGenerator(contentGenerator);

                    // On associe le dictionnaire d'options au template
                    template.setDictionary(dictionary);

                    // On transmet les paramètres
                    template.setParameters(parameters);

                    templates.put(templateTitle, template);
                } catch (DetailedException e) {
                    logger.debug("Le template " + i + " est mal configuré : il est ignoré ! : " + e);
                }
            }

        } catch (DetailedException e) {
            e.addMessage("Impossible de retrouver le nombre de gabarits paramétrés.");
            throw e;
        }
    }

    protected IContentGenerator getContentGeneratorFromProperties(String contentGeneratorClassName) throws DetailedException {
        IContentGenerator contentGenerator = ClassFactory.getInstanceOf(contentGeneratorClassName,
                IContentGenerator.class, getClassLoaderHere());
        return contentGenerator;
    }

    /**
     * On se sert du template courant pour instancier un objet CannelleScreenParameters avec les bons screenParameters.
     * @return
     * @param template
     */
    private CannelleScreenParameters prepareCannelleScreenParametersFromTemplate(IExcelTemplate template)
            throws DetailedException {
        logger.debug("On instancie un objet CannelleScreenParameters en se servant du template courant " + template);

        // Il faut le type de template pour récupérer les bons parameters
        CannelleScreenParameters cannelleScreenParameters = new CannelleScreenParameters();

        int paramsMaxIndex = -1;

        try {
            paramsMaxIndex = parameters.getMaxIndexForKey(template.getParamsKey());
            logger.debug("Les index des paramètres de ce template vont de 0 à " + paramsMaxIndex + ".");
        } catch (DetailedException e) {
            logger.info("Le template '" + template.getParamsKey() + "' n'a pas de paramètres à récupérer : " + e.toString());
        }

        try {
            for (int j = 0; j <= paramsMaxIndex; j++) {
                // Titre du paramètre
//                String safeKey =
                String safeKey = template.getParamsKey() + "." + j + ".name";
                String paramName = parameters.getValue(safeKey);
                // Classe du paramètre
                String paramClassname = parameters.getValue(template.getParamsKey() + "." + j + ".type");
                IScreenParameter parameter = (IScreenParameter) ClassFactory.getInstanceOf(paramClassname,
                        IScreenParameter.class, getClassLoaderHere()); // NOPMD

                // On récupère les paramètres propres à ce ScreenParameter pour les lui donner comme propriétés.
                String prefix = template.getParamsKey() + "." + j + ".";
                Properties screenParameterProperties = translateProperties(parameters, prefix);
                // On initialise le paramètre avec les clés qui le concernent
                parameter.setProperties(screenParameterProperties);
                parameter.setSafeKey(safeKey);

                // On stocke ce ScreenParameter
                cannelleScreenParameters.addScreenParameter(paramName, parameter);
//                cannelleScreenParameters.addScreenParameter(, parameter);
            }

        } catch (DetailedException e) {
            e.addMessage("Impossible d'analyser les paramètres du template '" + template.getParamsKey() + "'.");
            throw e;
        }


        return cannelleScreenParameters;
    }

    /**
     * Instancie le template i : recherche le titre, la classe de traitement, et
     * tous ses paramètres (ScreenParameters).
     */
    private AbstractExcelTemplate parseTemplateFromParameters(int i) throws DetailedException {
        AbstractExcelTemplate result = null;
        try {
            // On recherche le nom de la classe de traitement demandée pour ce
            // titre de template
            String templateClassname = parameters.getValue(Parameters.TEMPLATES + "." + i + ".xls.class");

            logger.debug("Classe de traitement du template " + i + " : '" + templateClassname + "'. Instanciation...");

            // On fait le cast car on suppose que la classe ExcelTemplate
            // implémente l'interface ITemplate.
            result = (AbstractExcelTemplate) ClassFactory.getInstanceOf(templateClassname, IExcelTemplate.class,
                    getClassLoaderHere());

            // Recherche des paramètres définis dans le fichier de propriétés,
            // pour chacun des paramètres du template.
            logger.debug("On recherche les paramètres définis dans la conf pour le template " + i);

            // La racine des paramètres du modèle
            String paramsKey = Parameters.TEMPLATES + "." + i + ".params";
            result.setParamsKey(paramsKey);

        } catch (Exception e) {
            throw new DetailedException(e).addMessage(
                    "Impossible d'instancier le template '" + Parameters.TEMPLATES + "." + i + "' .");
        }

        return result;
    }

    protected ClassLoader getClassLoaderHere() throws DetailedException {
        return ThaleiaSession.get().getPluginService().getClassLoader();
    }

    /**
     * On récupère les paramètres qui commencent par une chaîne, pour
     * initialiser des propriétés dont les clés n'ont plus cette chaîne. Par
     * exemple, si les paramètres définissent :
     * <p>
     * <pre>
     * templates.1.contentgenerator.file.a7=ScreenFactory/qruqrm/qru.a7
     * templates.1.contentgenerator.class=fr.solunea.solucms.source.out.a7.QruQrmScreen
     * </pre>
     * <p>
     * alors on aura pour le préfixe 'templates.1.contentgenerator.' les
     * properties :
     * <p>
     * <pre>
     * file=ScreenFactory/qruqrm/qru.a7
     * class=fr.solunea.solucms.source.out.a7.QruQrmScreen
     * </pre>
     *
     * @param prefix le préfixe à supprimer pour les clés des propriétés. Par
     *               exemple 'templates.1.contentgenerator.'
     */
    private Properties translateProperties(Parameters parameters, String prefix) {

        Properties properties = new Properties();

        // On récupère toutes les clés qui commencent par le préfixe
        List<String> parameterParamsKey = parameters.getKeysStartingWith(prefix);

        for (String key : parameterParamsKey) {
            String value = parameters.getValue(key);
            // On transforme la clé complète
            // 'templates.1.contentgenerator.file.a7' en enlevant
            // le préfixe
            key = key.substring(prefix.length(), key.length());
            properties.put(key, value);
        }

        return properties;
    }

    /**
     * Parcourt la zone de cellules XLS et recherche le nom du template à la
     * position Parameters.XLS_PARSER_TEMPLATE_TITLE_COL /
     * Parameters.XLS_PARSER_TEMPLATE_TITLE_LINE, recherche ce template parmi
     * les templates connus, et utilise ce template pour produire les écrans
     * correspondants.
     *
     * @return l'écran correspondant.
     */
    public IContent parseScreen(CellsRange cells, ResourcesHandler resourcesHandler, Locale locale, User user) throws DetailedException {

        prepareTemplate(cells);

        IContent screen = generateScreen(resourcesHandler, locale, user, template);

        return screen;
    }

    private IContent generateScreen(ResourcesHandler resourcesHandler, Locale locale, User user, IExcelTemplate template) throws DetailedException {
        logger.debug("Recherche de la classe d'instanciation des écrans");
        // On recherche la classe d'instanciation des écrans
        IContentGenerator contentGenerator = getContentGenerator(template);
        if (contentGenerator == null) {
            throw new DetailedException("Impossible d'instancier la classe de génération des écrans !");
        }

        logger.debug("Préparation d'un répertoire de destination pour les fichiers de cet écrans");
        // On prépare un répertoire de destination pour les fichiers de cet
        // écran
        File screenDestination = new File(
                resourcesHandler.getTempDir() + File.separator + SCREENS_TEMP_DIR_NAME + File.separator
                        + getScreenDirName());
        logger.debug("Les fichiers seront copiés dans le répertoire " + screenDestination.getAbsolutePath());

        // On instancie les fichiers de l'écran
        logger.debug("Génération des fichiers");
        IContent screen = contentGenerator.generateFiles(screenParameters, screenDestination, locale, user);
        return screen;
    }

    protected String getScreenDirName() {
        return Long.toString(Calendar.getInstance().getTimeInMillis());
    }

    protected IContentGenerator getContentGenerator(IExcelTemplate template) throws DetailedException {
        IContentGenerator contentGenerator = template.getContentGenerator();
        return contentGenerator;
    }

    //    protected IExcelTemplate prepareTemplate(CellsRange cells) throws DetailedException {
    protected List<IScreenParameter> prepareTemplate(CellsRange cells) throws DetailedException {
        logger.debug("Analyse de la zone de cellules : " + cells.toString());

        // On cherche la cellule qui indique le template demandé
        String columnString = parameters.getValue(Parameters.XLS_PARSER_TEMPLATE_TITLE_COL);
        String lineString = parameters.getValue(Parameters.XLS_PARSER_TEMPLATE_TITLE_LINE);

        int column;
        int line;
        try {
            column = new Integer(columnString).intValue();
            line = new Integer(lineString).intValue();

        } catch (NumberFormatException e) {
            throw new DetailedException(e).addMessage("La position du titre du template dans le bloc de cellule de "
                    + "l'écran n'est pas correcte : colonne = '" + columnString + "' ligne ='" + lineString + "'.");
        }

        Cell titleCell = cells.getCell(column, line);
        String templateTitle;
        try {
            templateTitle = titleCell.getStringCellValue();
        } catch (Exception e1) {
            throw new DetailedException(e1).addMessage("Impossible d'interpréter la chaîne de caractères définissant "
                    + "le titre du template à utiliser pour cet écran .");
        }

        logger.debug("Titre du modèle demandé = '" + templateTitle + "'");

        // Recherche du template qui porte ce nom parmi les templates retouvés
        // lors de l'instanciation
        template = templates.get(templateTitle);
        if (template == null) {
            throw new DetailedException("Le template '" + templateTitle + "' n'existe pas dans ce plugin !");
        }


        logger.debug("Récupération des paramètres de génération des écrans");
        // On demande au template la récupération des paramètres de génération
        // des écrans.

        cannelleScreenParameters = prepareCannelleScreenParametersFromTemplate(template);

        screenParameters = template.parseScreenParameters(cells, cannelleScreenParameters);

        //todo: retirer la traduction pour le traitement de genération des Ecran Normal !
        CannelleScreenParamTranslator translator = new CannelleScreenParamTranslator();
        ITranslatorAPI deeplTranslator = new DeeplTranslator();
        translator.from("FR").to("EN").with(deeplTranslator);
        translator.translate(cannelleScreenParameters);


//        return template;
        return screenParameters;
    }
}
