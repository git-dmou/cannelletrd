package fr.solunea.thaleia.plugins.cannelle.packager.act.container;

import fr.solunea.thaleia.model.User;
import fr.solunea.thaleia.plugins.cannelle.contents.A7Screen;
import fr.solunea.thaleia.plugins.cannelle.contents.ExportedModule;
import fr.solunea.thaleia.plugins.cannelle.packager.act.Act;
import fr.solunea.thaleia.plugins.cannelle.packager.act.ActFormatPackager;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.generator.ScreenGeneratorUtils;
import fr.solunea.thaleia.service.CustomizationService;
import fr.solunea.thaleia.service.utils.FilesUtils;
import fr.solunea.thaleia.service.utils.export.ExportFormat;
import fr.solunea.thaleia.service.utils.scorm.AbstractManifest;
import fr.solunea.thaleia.service.utils.scorm.ManifestFactory;
import fr.solunea.thaleia.service.utils.scorm.ScormUtils;
import fr.solunea.thaleia.service.utils.scorm.ScormVersion;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.utils.LogUtils;
import fr.solunea.thaleia.webapp.ThaleiaApplication;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class HtmlContainer extends AbstractContainer {

    private static final Logger logger = Logger.getLogger(HtmlContainer.class);

    private final static String PARAM_CONTAINER_PATH_A7 = "export.format.act.container.path.a7";
    private final static String PARAM_CONTAINER_PATH_HTML = "export.format.act.container.path.html";
    private static final String PARAM_CONTAINER_MANIFEST_MAINFILE = "export.format.act.container.mainfile";
    private static final String PARAM_CONTAINER_MANIFEST_MAINFILE_ENCODING =
            "export.format.act.container.mainfile" + ".encoding";

    /**
     * Ces paramètres ne sont plus recherchés dans les properties, mais dans les
     * personnalisations stockées en base.
     * <p>
     * private final static String PARAM_CONTAINER_MANIFEST_VERSION =
     * "export.format.act.container.manifest.version"; private final static
     * String PARAM_CONTAINER_MANIFEST_FILENAME =
     * "export.format.act.container.manifest.manifest"; private final static
     * String PARAM_CONTAINER_MANIFEST_METADATA =
     * "export.format.act.container.manifest.manifestmetadata";
     */
    private final static String PARAM_CONTAINER_MANIFEST_PATH = "export.format.act.container.manifest.files";

    private final static String PARAM_CONTAINER_MANIFEST_AUTHOR = "export.format.act.container.manifest.author";
    private final static String PARAM_CONTAINER_MANIFEST_DESCRIPTION =
            "export.format.act.container.manifest" + ".description";
    private final static String PARAM_CONTAINER_MANIFEST_MAXLEARNTIME =
            "export.format.act.container.manifest" + ".maxlearningtime";
    private final static String PARAM_CONTAINER_MANIFEST_TYPLEARNTIME =
            "export.format.act.container.manifest" + ".typicallearningtime";
    private static final String PARAM_CONTAINER_MANIFEST_TIMELIMITACTION =
            "export.format.act.container.manifest" + ".timelimitaction";

    private static final String PARAM_CONTAINER_REPLACE = "export.format.act.container.solunea.replace";
    private static final String PARAM_CONTAINER_PATCH = "export.format.act.container.solunea.patch";

    public HtmlContainer(ResourcesHandler resourcesHandler) throws DetailedException {
        super(resourcesHandler);
    }

    @Override
    public void addFiles(Parameters parameters, ExportedModule module, ExportFormat format, User user) throws DetailedException {

        // Copie des fichiers modèle du container dans les fichiers du ACT.
        copyContainerFiles(module.getAct(), parameters, module.getLocale().getName(), module.getAct().isA7Contents(),
                user);

        // Quelle communication Scorm ?
        // On cherche la valeur de la propriété qui contient cette information
        String scormCommunication = module.getScormCommunication("false");

        // TODO améliorer l'inteprétation de cette valeur
        boolean activateScorm = false;
        if (scormCommunication.toLowerCase(Locale.ENGLISH).startsWith("oui")
                || scormCommunication.toLowerCase(Locale.ENGLISH).startsWith("yes")
                || scormCommunication.toLowerCase(Locale.ENGLISH).startsWith("si")
                || scormCommunication.toLowerCase(Locale.ENGLISH).startsWith("ja")) {
            activateScorm = true;
        }

        if (format == ExportFormat.PUBLICATION_PLATFORM_DEFINED) {
            activateScorm = true;
        }

        logger.debug("Doit-on activer la communication SCORM ? Propriété du module ='" + scormCommunication + "' => "
                + "activation de scorm =" + activateScorm);

        // On récupère la version : Scorm 1.2 ou 2004
        String manifestVersion =
                new CustomizationService(ThaleiaApplication.get().contextService, ThaleiaApplication.get().getConfiguration()).getCustomizationPropertyValue(ActFormatPackager.class.getName(), null, user.getDomain());
        if (manifestVersion == null) {
            // La version par défaut, si pas de personnalisation
            // demandée.
            manifestVersion = ManifestFactory.VERSION_2004;
        }

        // Mise à jour de la conf du container
        setCheatCode();
        setActFile(module.getAct(), parameters);

        // Doit on surdéfinir les préférences utilisateur ?
        String forceScorm2004 = parameters.getValue("forceScorm2004");
        if (forceScorm2004 == null) {
            logger.debug("Paramètre forceScorm2004 non défini ! On le considère comme false.");
        } else {
            logger.debug("Paramètre forceScorm2004 = " + forceScorm2004);
            if (forceScorm2004.equals("true")) {
                manifestVersion = ManifestFactory.VERSION_2004;
            }
        }

        if (format == ExportFormat.PUBLICATION_PLATFORM_DEFINED) {
            manifestVersion = ManifestFactory.VERSION_2004;
        }

        logger.debug("Activation de la version SCORM : " + manifestVersion);
        if (ManifestFactory.VERSION_1_2.equals(manifestVersion)) {
            setStandardVersion(module.getAct(), parameters, manifestVersion);
        }

        if (ManifestFactory.CMI5.equals(manifestVersion)) {
            setStandardVersion(module.getAct(), parameters, manifestVersion);
        }

        setTracking(module.getAct(), parameters, activateScorm, user);

        setLocale(module.getAct(), parameters, module.getLocale().getName());

        setFullscreen();

        setAudioOn(module.getAct(), module.getScreens(), parameters);

        // Ajout du manifest
        addManifest(module, parameters, activateScorm, manifestVersion);

        // Application des patches
        patchFiles(module.getAct(), parameters);

        // Remplacement de chaînes de caractères
        replaceValues(module.getAct(), parameters);
    }

    @Override
    public void transformToExe(Parameters parameters, ExportedModule module) throws DetailedException {
        // On effectue le traitement de transformation en exe.
        // Cette opération doit être la dernière, car après les fichiers (HTML,
        // ACT, A7...) sont paquetés dans un Zip.
        patchForLocalExecution(module.getAct(), parameters);
    }

    /**
     * Remplace tous les fichiers par une version exécutable.
     */
    private void patchForLocalExecution(Act act, Parameters parameters) throws DetailedException {
        try {
            // On considère que les fichiers à transformer en exe sont dans le
            // même répertoire que le fichier Act
            File actDir = act.getActFile().getParentFile();

            // On fabrique une version exécutable dans un répertoire temporaire
            File exeDir = ThaleiaApplication.get().getTempFilesService().getTempDir();
            fr.solunea.thaleia.service.utils.export.NodeWebkit.generateExe(actDir, exeDir, "index.html",
                    act.getTitle());

            // On remplace tous les fichiers par la version EXE
            FilesUtils.clearDirectory(actDir);
            try {
                FileUtils.copyDirectory(exeDir, actDir);
            } catch (Exception e) {
                throw new DetailedException(e).addMessage(
                        "Impossible de copier la version exécutable de '" + exeDir.getAbsolutePath() + "' vers '"
                                + actDir.getAbsolutePath() + "'");
            }

        } catch (DetailedException e) {
            throw new DetailedException(e).addMessage(
                    "Impossible de préparer" + " l'activité pour une exécution " + "locale.");
        }
    }

    /**
     * Copie les fichiers modèle du container dans les fichiers du ACT.
     */
    private void copyContainerFiles(Act act, Parameters parameters, String locale, boolean isA7Contents, User user) throws DetailedException {
        File sourceDir;
        try {
            final String confRoot = parameters.getValue(Parameters.PACKAGER_CONF_DIR);

            // On recherche le préfixe permettant de rechercher le nom du
            // fichier
            // xls modèle en fonction du type d'exercice (HTML ou A7)
            if (!Parameters.CONTENT_FORMAT_HTML.equals(ScreenGeneratorUtils.getGeneratedScreensFormat(user))) {
                sourceDir = getResourcesHandler().getParametrizedResourceFile(confRoot, parameters,
                        PARAM_CONTAINER_PATH_A7, locale);
            } else {
                sourceDir = getResourcesHandler().getParametrizedResourceFile(confRoot, parameters,
                        PARAM_CONTAINER_PATH_HTML, locale);
            }

        } catch (DetailedException e) {
            e.addMessage("Impossible de récupérer les fichiers du conteneur " + this.getClass().getName() + ".");
            throw e;
        }

        if (!sourceDir.exists()) {
            throw new DetailedException(
                    "Le répertoire du conteneur '" + sourceDir.getAbsolutePath() + "' n'existe " + "pas !");
        }

        try {
            logger.debug("Récupération des fichiers du container depuis : '" + sourceDir.getAbsolutePath() + "' et "
                    + "copie dans : '" + act.getActFile().getParentFile() + "'...");
            FilesUtils.copyDirectoryContent(sourceDir, act.getActFile().getParentFile());

        } catch (DetailedException e) {
            e.addMessage("Impossible d'ajouter les fichiers du conteneur au répertoire du ACT.");
            throw e;
        }

    }

    private void setCheatCode() {
        // Non implémenté.
    }

    /**
     * Met à jour le nom du fichier ACT à lancer par la méta.
     */
    private void setActFile(Act act, Parameters parameters) throws DetailedException {
        try {
            // L'encodage du fichier à mettre à jour
            String encoding = parameters.getValue(PARAM_CONTAINER_MANIFEST_MAINFILE_ENCODING);

            // Le nom du fichier à mettre à jour
            String mainFileName = parameters.getValue(PARAM_CONTAINER_MANIFEST_MAINFILE);
            // On considère que le fichier à mettre à jour est dans le même
            // répertoire que le fichier Act
            File mainFile = new File(act.getActFile().getParentFile() + File.separator + mainFileName);

            if (!mainFile.exists()) {
                throw new DetailedException("Le fichier de lancement à mettre à jour '" + mainFile.getAbsolutePath()
                        + "' n'a pas été trouvé à coté du fichier Act exporté !");
            }

            String find = "act: \"fichierACT.xml\"";
            String replaceBy = "act: \"" + act.getActFile().getName() + "\"";

            FilesUtils.replaceAllInFile(find, replaceBy, mainFile, encoding);

        } catch (DetailedException e) {
            e.addMessage("Impossible de mettre à jour le nom du fichier ACT.");
            throw e;
        }
    }

    /**
     * Définition de la norme e-Learning utilisée si elle diffère de SCORM 2004 (qui est définie par défaut).
     * @param act
     * @param parameters
     * @param standard
     * @throws DetailedException
     */
    private void setStandardVersion(Act act, Parameters parameters, String standard) throws DetailedException {
        try {
            // L'encodage du fichier à mettre à jour
            String encoding = parameters.getValue(PARAM_CONTAINER_MANIFEST_MAINFILE_ENCODING);

            // Le nom du fichier à mettre à jour
            String mainFileName = parameters.getValue(PARAM_CONTAINER_MANIFEST_MAINFILE);
            // On considère que le fichier à mettre à jour est dans le même
            // répertoire que le fichier Act
            File mainFile = new File(act.getActFile().getParentFile() + File.separator + mainFileName);

            if (!mainFile.exists()) {
                throw new DetailedException("Le fichier de lancement à mettre à jour '" + mainFile.getAbsolutePath()
                        + "' n'a pas été trouvé à coté du fichier Act exporté !");
            }

            String find = "scorm_version: \"scorm_2004\",";
            String replaceBy = "";
            if(standard.equals(ManifestFactory.VERSION_1_2)) {
                replaceBy = "scorm_version: \"scorm_12\",";
            } else if(standard.equals(ManifestFactory.CMI5)) {
                replaceBy = "scorm_version: \"cmi5\",";
            }

            logger.debug("Remplacement de " + find + " par " + replaceBy);
            FilesUtils.replaceAllInFile(find, replaceBy, mainFile, encoding);

        } catch (DetailedException e) {
            e.addMessage("Impossible de mettre à jour le nom du fichier ACT.");
            throw e;
        }
    }

    private void setTracking(Act act, Parameters parameters, boolean scormCommunication, User user) throws DetailedException {

        // Dans le fichier index.html, on règle autour de la line 30 :
        /*
         * this.params = { lang:"fr", scorm: false, scorm_version: "scorm_2004",
         * act: "fichierACT.xml" };
         */

        if (scormCommunication) {
            logger.debug("Activation de la communication Scorm.");

            // L'encodage du fichier à mettre à jour
            final String encoding = parameters.getValue(PARAM_CONTAINER_MANIFEST_MAINFILE_ENCODING);

            // Le nom du fichier à mettre à jour
            final String mainFileName = parameters.getValue(PARAM_CONTAINER_MANIFEST_MAINFILE);
            // On considère que le fichier à mettre à jour est dans le même
            // répertoire que le fichier Act
            final File mainFile = new File(act.getActFile().getParentFile() + File.separator + mainFileName);

            if (!mainFile.exists()) {
                throw new DetailedException("Le fichier de lancement à mettre à jour '" + mainFile.getAbsolutePath()
                        + "' n'a pas été trouvé à coté du fichier Act exporté !");
            }

            FilesUtils.replaceAllInFile("scorm: false,", "scorm: true,", mainFile, encoding);

            // On met également à jour l'option de completion si demandé
            String manifestVersion = ThaleiaSession.get().getCustomizationFilesService().getCustomizationPropertyValue(ActFormatPackager.class.getName(), null, user.getDomain());
            if (ManifestFactory.VERSION_2004_CSOD.equalsIgnoreCase(manifestVersion)) {
                FilesUtils.replaceAllInFile("completionMode: \"completeIfFailed\"",
                        "completionMode: " + "\"incompleteIfFailed\"", mainFile, encoding);
            }

        } else {
            // On laisse en l'état
            logger.debug("Pas d'activation de la communication Scorm.");
        }

        // TODO implémenter la version SHARED_OBJECT lorsqu'elle sera prête coté
        // méta.
    }

    private void setLocale(Act act, Parameters parameters, String locale) throws DetailedException {
        logger.debug("Mise à jour de la locale du module avec la langue : " + locale);
        try {
            // L'encodage du fichier à mettre à jour
            String encoding = parameters.getValue(PARAM_CONTAINER_MANIFEST_MAINFILE_ENCODING);

            // Le nom du fichier à mettre à jour
            String mainFileName = parameters.getValue(PARAM_CONTAINER_MANIFEST_MAINFILE);
            // On considère que le fichier à mettre à jour est dans le même
            // répertoire que le fichier Act
            File mainFile = new File(act.getActFile().getParentFile() + File.separator + mainFileName);

            FilesUtils.replaceAllInFile("lang: \"fr\",", "lang: \"" + locale + "\",", mainFile, encoding);
            FilesUtils.replaceAllInFile("lang:\"fr\",", "lang:\"" + locale + "\",", mainFile, encoding);
        } catch (DetailedException e) {
            e.addMessage("Impossible de mettre à jour la locale du module.");
            throw e;
        }

    }

    private void setFullscreen() {
        // Pas de possibilité de configurer le passage en mode plein écran.
    }

    /**
     * Permet de mettre à jour le point d'entrée du module pour activer les fonctionnalités audio du module si un mp3 ou
     * ogg est présent
     *
     * @param act           le point d'entrée du module (index.html) à mettre à jour
     * @param listA7Screens la liste des écrans à scanner pour récupérer la présence d'un fichier audio
     * @param parameters    les paramètres définissant le module
     */
    private void setAudioOn(Act act, List<A7Screen> listA7Screens, Parameters parameters) throws DetailedException {
        try {
            for (A7Screen a7Screen : listA7Screens) {
                if (a7Screen.getA7Content().isAudible()) {
                    // L'encodage du fichier à mettre à jour
                    String encoding = parameters.getValue(PARAM_CONTAINER_MANIFEST_MAINFILE_ENCODING);

                    // Le nom du fichier à mettre à jour
                    String mainFileName = parameters.getValue(PARAM_CONTAINER_MANIFEST_MAINFILE);
                    // On considère que le fichier à mettre à jour est dans le même
                    // répertoire que le fichier Act
                    File mainFile = new File(act.getActFile().getParentFile() + File.separator + mainFileName);

                    FilesUtils.replaceAllInFile("haveSound: false", "haveSound: true", mainFile, encoding);
                    //Seul un fichier audio est nécessaire pour quitter la boucle, on force la fin afin d'éviter de
                    // générer une exception inutile
                    break;
                }
            }
        } catch (DetailedException e) {
            e.addMessage("Impossible de détecter la présence de son dans le module");
            throw e;
        }
    }

    private void addManifest(ExportedModule module, Parameters parameters, boolean scormCommunication, String manifestVersion) throws DetailedException {
        try {
            Act act = module.getAct();
            // doit-on ajouter un manifest ?
            if (scormCommunication) {

                logger.debug("Ajout d'un manifest Scorm 2004.");

                /**
                 * RMAR /** Cette version du code va rechercher le manifest 1.2
                 * ou 2004 dans le fichier de properties du plugin.
                 * ------------------------------------------------
                 *
                 * String confRoot = parameters
                 * .getValue(Parameters.PACKAGER_CONF_DIR); File sourceDir =
                 * getResourcesHandler() .getParametrizedResourceFile(confRoot,
                 * parameters, PARAM_CONTAINER_MANIFEST_PATH); logger.debug(
                 * "Copie des fichiers modèle de manifest dans le paquet : copie
                 * des fichiers du répertoire '" + sourceDir.getAbsolutePath() +
                 * "' vers '" +
                 * act.getActFile().getParentFile().getAbsolutePath() + "'...");
                 * FilesUtils.copyDirectoryContent(sourceDir, act.getActFile()
                 * .getParentFile());
                 *
                 * logger.debug("Instanciation du manifest...");
                 * AbstractManifest manifest = ManifestFactory .getInstance()
                 * .getManifest( parameters
                 * .getValue(PARAM_CONTAINER_MANIFEST_VERSION),
                 * act.getActFile().getParentFile(), parameters
                 * .getValue(PARAM_CONTAINER_MANIFEST_FILENAME), parameters
                 * .getValue(PARAM_CONTAINER_MANIFEST_METADATA));
                 */

                // On va rechercher soit la version 1.2, soit la version 2004
                // dans un paramètre de personnalisation Thaleia.


                // Les paramètres à prendre en compte : 1.2 ou 2004
                ManifestParams manifestParams = new ManifestParams(manifestVersion);

                parameters.addParameter(PARAM_CONTAINER_MANIFEST_PATH, manifestParams.manifestPath);
                logger.debug("Paramètre " + PARAM_CONTAINER_MANIFEST_PATH + " = " + manifestParams.manifestPath);

                // Copie des fichiers
                File sourceDir = getResourcesHandler().getParametrizedResourceFile(
                        parameters.getValue(Parameters.PACKAGER_CONF_DIR), parameters, PARAM_CONTAINER_MANIFEST_PATH);
                logger.debug(
                        "Copie des fichiers modèle de manifest dans le paquet : copie des fichiers du répertoire" + " '"
                                + sourceDir.getAbsolutePath() + "' vers '"
                                + act.getActFile().getParentFile().getAbsolutePath() + "'...");
                FilesUtils.copyDirectoryContent(sourceDir, act.getActFile().getParentFile());

                logger.debug("Instanciation du manifest...");
                AbstractManifest manifest = ManifestFactory.getInstance().getManifest(manifestParams.scormVersion,
                        act.getActFile().getParentFile(), manifestParams.manifestFilename, manifestParams.manifestMetadata);

                setManifestScormVersion(manifest, manifestVersion);
                setManifestMainResource(manifest, parameters);
                setManifestTitle(manifest, act, parameters);
                setManifestAuthor(manifest, parameters);
                setManifestDescription(manifest, parameters);
                setManifestMasteryScore(manifest, act);
                setManifestMaxLearningTime(manifest, act, parameters);
                setManifestTypicalLearningTime(manifest, act, parameters);
                setManifestTime(manifest);
                setManifestTimeLimitAction(manifest, parameters);
                setDependencies(manifest, act);
                checkCompletionConfiguration(manifest, act);

                // Le cmi5 a besoin de l'identifiant du module
                if(manifestVersion.equals(ManifestFactory.CMI5)) {
                    setManifestIdentifiant(manifest, module);
                }

            } else {
                logger.debug("Pas d'ajout de manifest demandé.");
            }

        } catch (DetailedException e) {
            e.addMessage("Impossible de traiter le manifest.");
            throw e;
        }
    }

    private class ManifestParams {

        ScormVersion scormVersion;
        String manifestPath;
        String manifestFilename;
        String manifestMetadata;

        ManifestParams(String version) {
            switch(version){

                case ManifestFactory.VERSION_1_2:
                    this.scormVersion = ScormVersion.SCORM_12;
                    this.manifestPath = "manifest/scorm1.2";
                    this.manifestFilename = "imsmanifest.xml";
                    this.manifestMetadata = "imsmanifest.xml.xml";
                    break;

                case ManifestFactory.CMI5:
                    this.scormVersion = ScormVersion.CMI_5;
                    this.manifestPath = "manifest/cmi5";
                    this.manifestFilename = "metadata.xml";
                    this.manifestMetadata = "cmi5.xml";
                    break;

                case ManifestFactory.VERSION_2004:
                default:
                    this.scormVersion = ScormVersion.SCORM_2004;
                    this.manifestPath = "manifest/scorm2004";
                    this.manifestFilename = "imsmanifest.xml";
                    this.manifestMetadata = "imsmanifest.xml.xml";
                    break;
            }
        }
    }

    private void setManifestScormVersion(AbstractManifest manifest, String manifestVersion) throws DetailedException {
        if (ManifestFactory.VERSION_2004_4.equals(manifestVersion)) {
            logger.debug("Version du manifest : 2004 4th Edition.");
            manifest.setSchemaVersion("2004 4th Edition");
        }
    }

    /**
     * Effectue la vérification de la définition de la complétion, en fonction du type de contenu (apport ou évaluation)
     */
    private void checkCompletionConfiguration(AbstractManifest manifest,@Nonnull Act act) throws DetailedException {
        if (!act.containsSuccessCalculus()) {
            manifest.setCompletionWithoutSuccessCalculus();
        }
    }

    private void patchFiles(Act act, Parameters parameters) throws DetailedException {
        // On recherche tous les patches à appliquer

        // les keys sont du type
        // "format.act.container.solunea.patch.X.source",
        // "format.act.container.solunea.patch.X.destination",
        // On recherche le X maximum en commençant par 0
        int max;
        try {
            max = parameters.getMaxIndexForKey(PARAM_CONTAINER_PATCH);
            logger.debug(max + " patches à faire dans des fichiers du conteneur.");
        } catch (DetailedException e) {
            e.addMessage("Pas de patches à appliquer dans les fichiers exportés.");
            logger.debug(e.toString());
            // On sort proprement de la méthode
            return;
        }

        // Pour tous les patches
        for (int i = 0; i <= max; i++) {
            logger.debug("Patch n°" + i);
            try {
                // Fichier source
                String sourceKey = PARAM_CONTAINER_PATCH + "." + i + ".source";
                String confRoot = parameters.getValue(Parameters.PACKAGER_CONF_DIR);
                File source = getResourcesHandler().getParametrizedResourceFile(confRoot, parameters, sourceKey);

                // Fichier destination
                String destinationKey = PARAM_CONTAINER_PATCH + "." + i + ".destination";
                String destinationFilename = parameters.getValue(destinationKey);
                File destination = new File(act.getActFile().getParentFile() + File.separator + destinationFilename);

                if (destination.exists()) {
                    logger.debug(
                            "Fichier patché (la destination est remplacée) : '" + destination.getAbsolutePath() + "'");
                } else {
                    logger.debug("Fichier patché (la destination est créée) : '" + destination.getAbsolutePath() + "'");
                }

                FilesUtils.copyBinaryTo(source, destination);

            } catch (Exception e) {
                throw new DetailedException("Erreur durant l'exécution du patch n°" + i + " : " + e + "\n"
                        + LogUtils.getStackTrace(e.getStackTrace()));
            }
        }
    }

    private void replaceValues(Act act, Parameters parameters) throws DetailedException {
        // On recherche tous les remplacements à faire

        // les keys sont du type
        // "format.act.container.solunea.replace.X.file",
        // "format.act.container.solunea.replace.X.find",
        // "format.act.container.solunea.replace.X.replacedby", ...
        // On recherche le X maximum en commençant par 0
        int max;
        try {
            max = parameters.getMaxIndexForKey(PARAM_CONTAINER_REPLACE);
            logger.debug(max + " remplacements à faire dans des fichiers du conteneur.");
        } catch (DetailedException e) {
            e.addMessage("Pas de remplacements de chaînes de caractères à faire dans les fichiers exportés.");
            logger.debug(e.toString());
            // On sort proprement de la méthode
            return;
        }

        // Pour tous les remplacements
        for (int i = 0; i <= max; i++) {
            logger.debug("Remplacement n°" + i);
            // Fichier à traiter
            String filenameKey = PARAM_CONTAINER_REPLACE + "." + i + ".file";
            String filename = parameters.getValue(filenameKey);
            File file = new File(act.getActFile().getParentFile() + File.separator + filename);

            // Chaîne à rechercher
            String find = parameters.getValue(PARAM_CONTAINER_REPLACE + "." + i + ".find");
            // Chaîne de remplacement
            String replaceBy = parameters.getValue(PARAM_CONTAINER_REPLACE + "." + i + ".replaceby");
            // L'encodage des caractères du fichier
            String encoding = parameters.getValue(PARAM_CONTAINER_REPLACE + "." + i + ".fileencoding");

            if (file.exists()) {
                logger.debug("Fichier traité : '" + file.getAbsolutePath() + "'");
                FilesUtils.replaceAllInFile(find, replaceBy, file, encoding);

            } else {
                throw new DetailedException(
                        "Impossible de modifier la valeur '" + find + "' par '" + replaceBy + "' " + "dans le fichier '"
                                + file.getAbsolutePath() + "' : il n'existe pas dans le paquet exporté " + "!");
            }

        }
    }

    private void setManifestMainResource(AbstractManifest manifest, Parameters parameters) throws DetailedException {
        String mainFile = parameters.getValue(PARAM_CONTAINER_MANIFEST_MAINFILE);
        logger.debug("On fixe dans le manifest l'URL de la ressource principale à '" + mainFile + "'...");
        manifest.setMainResourceUrl(mainFile);
    }

    private void setManifestTitle(AbstractManifest manifest, Act act, Parameters parameters) throws DetailedException {
        Locale locale = new Locale(parameters.getValue(Parameters.EXPORT_LANG));
        manifest.setTitle(act.getTitle(), locale);
    }

    private void setManifestAuthor(AbstractManifest manifest, Parameters parameters) throws DetailedException {
        String author = ScormUtils.getVCard(parameters.getValue(PARAM_CONTAINER_MANIFEST_AUTHOR));
        manifest.setAuthor(author);
    }

    private void setManifestDescription(AbstractManifest manifest, Parameters parameters) throws DetailedException {
        Locale locale = new Locale(parameters.getValue(Parameters.EXPORT_LANG));
        manifest.setDescription(parameters.getValue(PARAM_CONTAINER_MANIFEST_DESCRIPTION), locale);
    }

    private void setManifestMasteryScore(AbstractManifest manifest, Act act) throws DetailedException {
        manifest.setMasteryScore(act.getMasteryScore());
    }

    private void setManifestMaxLearningTime(AbstractManifest manifest, Act act, Parameters parameters) throws DetailedException {
        String maxLearningTime = parameters.getValue(PARAM_CONTAINER_MANIFEST_MAXLEARNTIME);

        // On étudie la durée demandéé : @@package_duration, ou une durée fixe
        maxLearningTime = parseLearningTime(maxLearningTime, act);

        manifest.setMaxLearningTime(maxLearningTime);
    }

    private void setManifestTypicalLearningTime(AbstractManifest manifest, Act act, Parameters parameters) throws DetailedException {
        String typicLearningTime = parameters.getValue(PARAM_CONTAINER_MANIFEST_TYPLEARNTIME);

        // On étudie la durée demandéé : @@package_duration, ou une durée fixe
        typicLearningTime = parseLearningTime(typicLearningTime, act);

        manifest.setTypicalLearningTime(typicLearningTime);
    }

    private void setManifestTime(AbstractManifest manifest) throws DetailedException {
        Calendar now = Calendar.getInstance();
        manifest.setTime(ScormUtils.secondsToScormDateTime(now.getTimeInMillis()));
    }

    private void setManifestTimeLimitAction(AbstractManifest manifest, Parameters parameters) throws DetailedException {
        manifest.setTimeLimitAction(parameters.getValue(PARAM_CONTAINER_MANIFEST_TIMELIMITACTION));
    }

    /**
     * Ajoute comme dépendance au manifest tous les fichiers situés dans le
     * repértoire qui contient ce Act.
     */
    private void setDependencies(AbstractManifest manifest, Act act) throws DetailedException {
        try {
            Collection<File> files = FileUtils.listFiles(act.getActFile().getParentFile(), null, true);
            // logger.debug("Ajout de dépendances dans le manifest pour " + files.size() + " fichiers.");

            // On liste tous les fichiers du paquet
            for (File file : files) {
                // L'url relative de ce fichier par rapport à la racine du paquet SCORM
                String rootUrl = act.getActFile().getParentFile().getAbsolutePath();
                // +1 pour ne pas commencer par un "/"
                String resourceUrl = file.getAbsolutePath().substring(rootUrl.length() + 1);
                manifest.addDependency(resourceUrl);
            }

        } catch (DetailedException e) {
            e.addMessage("Impossible d'ajouter les dépendances au manifest.");
            throw e;
        }

    }

    private void setManifestIdentifiant(AbstractManifest manifest, ExportedModule module) throws DetailedException {
        String server = ThaleiaApplication.get().getApplicationParameterDao().getValue("server.url", "");
        manifest.setIdentifier(server, module.getIdentifier());
    }

    private String parseLearningTime(String maxLearningTime, Act act) throws DetailedException {
        String result = maxLearningTime;

        if ("@@package_duration".equals(maxLearningTime)) {
            result = ScormUtils.secondsToScormTime(act.getDuration());

        } else {
            // On renvoie telle quelle la valeur du champ
            // TODO vérifier le format de maxLearningTime
            logger.debug("Le format de maxLearningTime n'est pas vérifié.");
        }

        logger.debug("Durée SCORM calculée pour '" + maxLearningTime + "' : '" + result + "'");
        return result;
    }

}
