package fr.solunea.thaleia.plugins.cannelle.xls.screens.generator;

import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.model.User;
import fr.solunea.thaleia.plugins.cannelle.contents.A7Content;
import fr.solunea.thaleia.plugins.cannelle.messages.LocalizedMessages;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.IScreenParameter;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.TextParameter;
import fr.solunea.thaleia.service.CustomizationService;
import fr.solunea.thaleia.service.utils.CopyUtils;
import fr.solunea.thaleia.service.utils.FilesUtils;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.ThaleiaApplication;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScreenGeneratorUtils {

    public static final String SCREEN_FACTORY = "ScreenFactory";
    public static final String CHOICE_MODELE = "Choice_modele";
    /**
     * Le nom du paramètre qui contient le chemin relatif (dans les ressources localisées) du fichier utilisé comme
     * modèle par le contentGenerator. Par exemple : Carroussel.a7
     */
    public static final String MODEL_FILE_NAME_IN_PARAM = "file.model.in";
    /**
     * Le nom du paramètre qui contient le chemin relatif (dans les ressources localisées) du fichier utilisé comme
     * modèle Excel par le contentGenerator si les écrans sont au format A7
     */
    public static final String MODEL_XLS_FILE_NAME_IN_PARAM = "replacements.definition.file";
    /**
     * Le nom du paramètre qui contient le chemin relatif (dans les ressources localisées) du fichier utilisé comme
     * modèle Excel par le contentGenerator si les écrans sont au format HTML
     */
    public static final String MODEL_XLS_FILE_NAME_IN_PARAM_HTML = "replacements.definition.file.html";
    /**
     * Le nom du paramètre qui contient le chemin relatif (dans les ressources localisées) du dossier contenant toutes
     * les ressources
     */
    public static final String RESOURCES_DIR_PARAM = "resources.dir";
    public static final String MODEL_ENCODING_PARAM = "file.model.encoding";
    public static final String ASSETS_DIR_NAME_PARAM = "assets.dir.name";
    private static final Logger logger = Logger.getLogger(ScreenGeneratorUtils.class);

    /**
     * @return la valeur du ScreenParameter dont le nom est dans la clé demandée, tel que défini dans les paramètres.
     * Par exemple, si key = screenId.param.name, alors on va rechercher la valeur du ScreenParameter dont le nom est la
     * valeur du paramètre dont la clé est screenId.param.name.
     */
    public static String getScreenParamaterValue(String key, Parameters parameters,
                                                 List<IScreenParameter> screenParameters, String parametersPrefix,
                                                 String defaultValue) throws DetailedException {

        String result = "";

        // Le nom (valeur du paramètre templates.X.params.Y.name) du
        // ScreenParameter qui contient la valeur demandée.
        String valueParameterName = parameters.getValue(parametersPrefix + key);

        // Le ScreenParameter qui contient la valeur demandée.
        Map<String, IScreenParameter> screenParamsByName = getScreenParam(screenParameters);
        IScreenParameter value = screenParamsByName.get(valueParameterName);

        if (value == null) {
            logger.debug(
                    "Le paramètre '" + valueParameterName + "' n'est pas renseigné : la valeur est fixée par défaut.");
            result = defaultValue;

        } else {
            logger.debug("La valeur du paramètre '" + valueParameterName + "' récupérée est : " + value);

            // On vérifie que ce paramètre est bien un TextParameter
            if (!value.getClass().isAssignableFrom(TextParameter.class)) {
                throw new DetailedException("Le screenParameter '" + valueParameterName + "' n'est pas de la classe '"
                        + TextParameter.class.getName() + "' !");
            }
            TextParameter resultAsTextParameter = (TextParameter) value;

            // On recherche l'identifiant de l'écran à produire.
            result = resultAsTextParameter.getValue();
        }

        return result;
    }

    /**
     * Fabrique un accès aux ScreenParameters par leur nom (templates.X.params.Y.name)
     *
     * @return : une Map contenant tout les noms de paramètres et leur valeur
     */
    public static Map<String, IScreenParameter> getScreenParam(List<IScreenParameter> screenParameters) {
        Map<String, IScreenParameter> screenParamsByName = new HashMap<String, IScreenParameter>();
        for (IScreenParameter screenParameter : screenParameters) {
            // name est le nom de la propriété du screenParameter qui doit
            // contenir son identifiant unique.
            String name = screenParameter.getProperty("name", "");
            screenParamsByName.put(name, screenParameter);
        }
        return (screenParamsByName);
    }

    /**
     * Recherche le fichier a7 modèle, et copie ce fichier et tous ses médias associés dans le répertoire destination
     *
     * @param destination : dossier de destination
     * @return : le Content correspondant au nouveau fichier a7 copié
     */
    public static A7Content createA7ContentFromModel(File destination, ResourcesHandler ressourcesHandler,
                                                     String parametersPrefix, Parameters parameters, Locale locale,
                                                     User user) throws DetailedException {
        String modelDirName = parameters.getValue(parametersPrefix + RESOURCES_DIR_PARAM);

        if (Parameters.CONTENT_FORMAT_HTML.equals(getGeneratedScreensFormat(user))) {
            // On recherche le nom du dossier dans lequel les ressources du
            // modèle sont stockées
            modelDirName = modelDirName + "_html";
        }
        logger.debug("Le nom du dossier contenant les ressources est " + modelDirName);

        // On recherche le préfixe permettant de rechercher le nom du fichier a7
        // modèle
        String modelFilenameKey = parametersPrefix + MODEL_FILE_NAME_IN_PARAM;
        // On récupère le nom du fichier correspondant
        String fileName = parameters.getValue(modelFilenameKey);
        if (Parameters.CONTENT_FORMAT_HTML.equals(getGeneratedScreensFormat(user))) {
            // On recherche le nom du dossier dans lequel les ressources du
            // modèle sont stockées
            fileName = "index.html";
        }
        logger.debug("Le nom du modèle de fichier est " + fileName);

        // On récupère ce fichier dans les ressources
        File a7modelSource = ressourcesHandler.getResourceFile(
                SCREEN_FACTORY + "/" + locale.getName() + "/" + modelDirName + "/" + fileName);

        logger.debug("On a récupéré le fichier " + a7modelSource.getAbsolutePath());

        // On effectue la copie du a7 et ses médias
        try {
            // On instancie le contenu modèle
            A7Content model = A7Content.createFromA7File(a7modelSource);

            // On le copie, avec ses fichiers ailleurs
            logger.debug("On copie le contenu modèle dans " + destination);
            return model.safeCopyTo(destination);

        } catch (DetailedException e) {
            e.addMessage("Impossible de copier le fichier de modèle " + "lors de la génération de l'écran.");
            throw e;
        }
    }

    /**
     * @return le format requis pour la génération des écrans : Parameters.CONTENT_FORMAT_HTML ou
     * Parameters.CONTENT_FORMAT_A7. Cette information est recherchée dans les personnalisations.
     */
    public static String getGeneratedScreensFormat(User user) {
        // On recherche la personnalisation de l'utilisateur
        String screensFormat = null;
        try {
            screensFormat = new CustomizationService(ThaleiaApplication.get().contextService, ThaleiaApplication.get().getConfiguration()).getCustomizationPropertyValue(Parameters.CONTENT_FORMAT, null, user.getDomain());
        } catch (Exception e) {
            logger.warn("Impossible de récupérer le format des contenus à générer dans les personnalisations : " + e);
        }
        if (screensFormat == null) {
            // Le format par défaut, si pas de personnalisation
            // demandée.
            screensFormat = Parameters.CONTENT_FORMAT_HTML;
        }
        return screensFormat;
    }

    /**
     * Recherche le fichier excel modèle, et copie ce fichier dans le répertoire destination
     *
     * @param destination : dossier de destination
     * @return le fichier xls de définition des remplacements
     */
    public static File getXlsFileModel(File destination, ResourcesHandler ressourcesHandler, String parametersPrefix,
                                       Parameters parameters, Locale locale, User user) throws DetailedException {

        // On recherche le préfixe permettant de rechercher le nom du fichier
        // xls modèle en fonction du type d'exercice (HTML ou A7)
        String modelFilenameKey;
        if (!Parameters.CONTENT_FORMAT_HTML.equals(getGeneratedScreensFormat(user))) {
            modelFilenameKey = parametersPrefix + MODEL_XLS_FILE_NAME_IN_PARAM;
        } else {
            modelFilenameKey = parametersPrefix + MODEL_XLS_FILE_NAME_IN_PARAM_HTML;
        }
        // On récupère le nom du fichier correspondant
        String fileName = parameters.getValue(modelFilenameKey);
        logger.debug("Le nom du modèle de fichier est " + fileName);

        if (fileName.isEmpty()) {
            throw new DetailedException("Le modèle de fichier XLS n'a pas été défini dans les propriétés du plugin ! "
                    + "L'écran ne peut pas être généré.");
        }

        // On récupère ce fichier dans un InputStream
        File xlsModelFile = ressourcesHandler.getResourceFile(SCREEN_FACTORY + "/" + locale.getName() + "/" + fileName);

        return xlsModelFile;
    }

    /**
     * 1/ Modifie le fichier a7 pour remplacer toutes les occurences de fileNameToReplace par la valeur filename .<br>
     * 2/ Recherche le fichier portant ce nom dans les fichiers uploadés, et renvoie une exception s'il n'existe pas.
     * S'il existe, le copie dans le sous répertoire demandé (en relatif par rapport au A7). Lors de cette copie, on
     * vérifie qu'un fichier ne porte pas déjà ce nom dans la destination, et si le nom est modifié, alors il est
     * modifié dans le A7 et sur le disque.
     *
     * @param fileNameToReplace la chaîne de caractère à chercher dans le A7, à remplacer par le nom de média.
     * @param filename          le nom de média à placer dans le A7 en remplacement de fileNameToReplace.
     * @param a7File            le fichier A7
     * @param encoding          l'encodage du fichier a7.
     * @param assestsDirName    le nom du sous-répertoire (en relatif depuis le A7) dans lequel copier le fichier. Par
     *                          exemple : "assets".
     */
    public static void safeAddMedia(ResourcesHandler resourcesHandler, Parameters parameters,
                                    String fileNameToReplace, String filename, File a7File, String encoding,
                                    String assestsDirName) throws DetailedException, Exception {

        String safeFilename = filename.trim();

        if (safeFilename.equals("")) {
            logger.debug("La balise filename est vide pour le fichier " + a7File.getAbsolutePath() + " : il est "
                    + "ignoré.");
            return;
        }

        if (safeFilename.indexOf("/") != -1 || safeFilename.indexOf("\\") != -1) {
            throw new DetailedException(
                    "La balise filename est incorrecte pour le fichier " + a7File.getAbsolutePath());
        }

        // On recherche le fichier média dans les fichiers uploadés
        // TODO Va surement générer une erreur si le media porte le même nom que le Excel
        logger.debug("Recherche du fichier '" + safeFilename + "' dans les fichiers uploadés...");
        File sourceMediaFile = resourcesHandler.getUploadedFiles().getFile(safeFilename);
        if (sourceMediaFile == null) {
            // Le message localisé correspondant
            String message = LocalizedMessages.getMessage(LocalizedMessages.MEDIA_FILE_NOT_FOUND_ERROR,
                    new Object[]{safeFilename});

            // On demande sa présentation
            notifiySession(message);

            throw new DetailedException("Fichier '" + safeFilename
                    + "' non trouvé dans les fichiers envoyés et décompressés dans le répertoire "
                    + resourcesHandler.getUploadedFiles().getExpandedDir().getAbsolutePath());
        }

        logger.debug("On a trouvé le fichier '" + sourceMediaFile.getAbsolutePath() + "' dans les fichiers uploadés.");

        // On copie ce média dans le même répertoire que le A7, dans le
        // sous-répertoire demandé
        String destMediaFile = a7File.getParentFile().getAbsolutePath();
        // Si l'écran n'est pas un écran HTML on copie dans le assetsDirName,
        // sinon à la racine
        if (!destMediaFile.endsWith("_html")) {
            destMediaFile += File.separator + assestsDirName;
        }
        logger.debug("On copie ce fichier dans le répertoire " + destMediaFile);
        File mediaFile = CopyUtils.safeCopyInto(sourceMediaFile, new File(destMediaFile), false);

        // Si ce nom a été modifié lors de cette copie, on le prend en compte
        String newFilename = mediaFile.getName();
        logger.debug("Le nom du fichier est : '" + newFilename + "'");

        // On fait maintenant référence à ce nom définitif dans le a7 :
        FilesUtils.replaceAllInFile(fileNameToReplace, newFilename, a7File, encoding);
    }

    protected static void notifiySession(String message) {
        ThaleiaSession.get().addError(message);
    }

}
