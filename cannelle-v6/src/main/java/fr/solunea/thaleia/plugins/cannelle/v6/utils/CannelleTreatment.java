package fr.solunea.thaleia.plugins.cannelle.v6.utils;

import fr.solunea.thaleia.model.ContentVersion;
import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.model.User;
import fr.solunea.thaleia.model.dao.LocaleDao;
import fr.solunea.thaleia.plugins.cannelle.parsers.xls.ExcelFileLocaleFinder;
import fr.solunea.thaleia.plugins.cannelle.service.ImportModuleService;
import fr.solunea.thaleia.plugins.cannelle.service.ResourceHandlerService;
import fr.solunea.thaleia.plugins.cannelle.utils.CannellePreviewHelper;
import fr.solunea.thaleia.plugins.cannelle.utils.PackagedFiles;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.v6.CannelleV6Plugin;
import fr.solunea.thaleia.plugins.cannelle.v6.panels.CannelleContentsPanel;
import fr.solunea.thaleia.plugins.welcomev6.messages.LocalizedMessages;
import fr.solunea.thaleia.service.PreviewService;
import fr.solunea.thaleia.service.utils.ZipUtils;
import fr.solunea.thaleia.service.utils.export.ExportFormat;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.utils.LogUtils;
import fr.solunea.thaleia.webapp.Analytics;
import fr.solunea.thaleia.webapp.ThaleiaApplication;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class CannelleTreatment {

    private final static Logger logger = Logger.getLogger(CannelleTreatment.class);
    private static final String EVENT_PROCESS_ERROR = "cannelle.process.error";
    private static final String EVENT_PROCESS_IMPORT = "cannelle.process.import.ok";
    private static final String EVENT_PROCESS_EXPORT = "cannelle.process.export.ok";
    private static final String EVENT_PROCESS_PREVIEW = "cannelle.process.preview.ok";

    /**
     * Procède à l'export de cette ContentVersion, dans le but de la prévisualiser.
     *
     * @return L'URL de prévisualisation.
     */
    public String preview(ContentVersion contentVersion, User user, java.util.Locale locale, String origLanguage, String TargetLanguage) throws DetailedException {
        // Vérification qu'on a bien un module Cannelle
        if (contentVersion.getContentType().getIsModuleType() && contentVersion.getContentType().getName().equals(CannelleContentsPanel.MODULE_CONTENT_TYPE_NAME)) {
            File file;
            List<String> errorMessagesKeys = new ArrayList<>();
            try {
                LocaleDao localeDao = ThaleiaApplication.get().getLocaleDao();
                file = ExportUtils.exportModule(contentVersion, localeDao.getLocale(locale), errorMessagesKeys, ExportFormat.PREVIEW, user);
            } catch (DetailedException e) {
                ThaleiaApplication.get().getEventService().storeEvent(EVENT_PROCESS_ERROR, e.toString(), user);
                throw new DetailedException(getFeedbackMessages(locale, errorMessagesKeys, e).toString());
            }

            // Préparation de l'aperçu
            PreviewService previewService;
            try {
                previewService = ThaleiaSession.get().getPreviewService();
            } catch (SecurityException e) {
                throw new DetailedException(e);
            }
            // L'objet qui va adapter l'archive pour permettre de la prévisualiser
            CannellePreviewHelper previewAdapter = new CannellePreviewHelper() {

                @Override
                public String guessMainFile(File tempDir) {
                    return "index.html";
                }
            };

            String previewUrl = previewService.publishArchive(file, previewAdapter);
            ThaleiaApplication.get().getEventService().storeEvent(EVENT_PROCESS_PREVIEW, contentVersion.getContentIdentifier(), user);

            // L'url est relative par rapport à l'hôte :
            // /nomduwar/preview/12345/index.html
            // Il faut la rendre absolue :
            String serverURL = ThaleiaApplication.get().getApplicationRootUrl().substring(0, ThaleiaApplication.get().getApplicationRootUrl().lastIndexOf("/"));
            return serverURL + previewUrl;

        } else {
            ThaleiaApplication.get().getEventService().storeEvent(EVENT_PROCESS_ERROR, "Preview error.", user);
            throw new DetailedException("Le content version à prévisualiser n'est pas un module Cannelle.");
        }
    }

    /**
     * Procède à l'import de ce fichier source, comme si l'utilisateur l'importait depuis la page de l'outil. En cas
     * d'erreur, les messages normalement présentés dans l'IHM Thaleia sont placés dans l'exception.
     *
     * @param locale la locale pour la présentation des messages d'erreur dans l'exception, si elle est levée.
     */
    public ContentVersion generateFromSource(File input, User user, java.util.Locale locale, String origLanguage, String TargetLanguage) throws Exception {
        return (ContentVersion) process(input, user, locale, false,"","");
    }

    public ContentVersion translateFromSource(File input, User user, java.util.Locale locale, String origLanguage, String targetLanguage) throws Exception {
        return (ContentVersion) process(input, user, locale, false, origLanguage, targetLanguage);
    }

    /**
     * Procède à l'import de ce fichier source, comme si l'utilisateur l'importait depuis la page de l'outil, puis à
     * l'export, comme pour un export depuis la page. En cas d'erreur, les messages normalement présentés dans l'IHM
     * Thaleia sont placés dans l'exception.
     *
     * @param locale la locale pour la présentation des messages d'erreur dans l'exception, si elle est levée.
     */
    public File generateAndExportFromSource(File input, User user, java.util.Locale locale, String origLanguage, String TargetLanguage) throws Exception {
        return (File) process(input, user, locale, true,"","");
    }

    /**
     * Procède à l'export pour une publication, comme pour une publication depuis la page.
     * En cas d'erreur, les messages normalement présentés dans l'IHM Thaleia sont placés dans l'exception.
     * La locale pour la présentation des messages d'erreur dans l'exception, si elle est levée, est en anglais.
     *
     * @param contentLocale la locale dans laquelle exporter le contenu.
     */
    public File publicationExport(ContentVersion contentVersion, User user, java.util.Locale contentLocale, String origLanguage, String TargetLanguage) throws Exception {
        fr.solunea.thaleia.model.Locale localeToPublish = new LocaleDao(ThaleiaApplication.get().contextService.getContextSingleton()).findByName(contentLocale.getLanguage());
        return publicationExport(contentVersion, user, localeToPublish, java.util.Locale.ENGLISH,"","");
    }


    /**
     * Procède à l'export pour une publication, comme pour une publication depuis la page.
     * En cas d'erreur, les messages normalement présentés dans l'IHM Thaleia sont placés dans l'exception.
     *
     * @param messagesLocale la locale pour la présentation des messages d'erreur dans l'exception, si elle est levée.
     * @param contentLocale  la locale dans laquelle exporter le contenu.
     */
    public File publicationExport(ContentVersion contentVersion, User user, Locale contentLocale, java.util.Locale messagesLocale, String origLanguage, String TargetLanguage) throws Exception {
        // La liste des clés de localisation des messages d'erreur à présenter
        List<String> errorMessagesKeys = new ArrayList<>();

        try {
            return ExportUtils.exportModule(contentVersion, contentLocale, errorMessagesKeys,
                    ExportFormat.PUBLICATION_PLATFORM_DEFINED, user);

        } catch (Exception e) {
            StringBuilder messages = getFeedbackMessages(messagesLocale, errorMessagesKeys, e);
            throw new Exception(messages.toString());
        }
    }

    private Object process(File input, User user, java.util.Locale locale, boolean export, String origLanguage, String targetLanguage) throws Exception {
        // La liste des clés de localisation des messages d'erreur à présenter
        List<String> errorMessagesKeys = new ArrayList<>();

        try {
            // Génération du contenu
            //**********************
            logger.debug("Traitement du fichier " + input.getAbsolutePath());

            // On vérifie que ce fichier uploadé est au format
            // attendu
            if (!ZipUtils.isAnArchive(input)) {
                errorMessagesKeys.add("uploaded.notanarchive");
                throw new Exception("Le fichier reçu n'est pas une archive Zip!");
            }

            // Initialisation de l'accès aux ressources
            ResourcesHandler resourcesHandler;
            Parameters parameters;
            Locale sourceFileLocale;
            try {
                // On cherche à deviner la locale du fichier Excel importé
                String localeName = ExcelFileLocaleFinder.parseLocale(input,
                        ThaleiaApplication.get().getTempFilesService().getTempDir());
                sourceFileLocale = new LocaleDao(user.getObjectContext()).findByName(localeName);

                // Les paramètres du traitement, obtenus dans les ressources du plugin.
                CannelleV6Plugin importExportPlugin = new CannelleV6Plugin(sourceFileLocale);
                parameters = importExportPlugin.getParameters();

                // Le fichier uploadé doit être de type PackagedFiles pour être
                // utilisé par le RessourcesHandler
                File tempFile = ThaleiaApplication.get().getTempFilesService().getTempFile();
                PackagedFiles packagedFiles = null;
                try {
                    packagedFiles = new PackagedFiles(input, tempFile);
                } catch (DetailedException e) {
                    errorMessagesKeys.add("zip.error");
                }

                // Initialisation du RessourcesHandler, avec la bonne
                // configuration du plugin à utiliser(vanilla etc.)
                ResourceHandlerService resourceHandlerService = new ResourceHandlerService(parameters,
                        CannelleV6Plugin.class);
                resourcesHandler = resourceHandlerService.getResourcesHandler(packagedFiles);

            } catch (DetailedException e) {
                throw new DetailedException(e).addMessage("Impossible de préparer l'accès aux ressources.");
            }

            // Lancement du traitement d'import
            ImportModuleService importModuleService = new ImportModuleService(parameters, resourcesHandler,
                    ThaleiaApplication.get().getConfiguration(), ThaleiaApplication.get().contextService);
            ContentVersion contentVersion = importModuleService.importModule(input, errorMessagesKeys,
                    sourceFileLocale, user, origLanguage, targetLanguage);

            Analytics.getImplementation().logEvent(CannelleEvent.CannelleContentCreationOk);
            ThaleiaApplication.get().getEventService().storeEvent(EVENT_PROCESS_IMPORT, contentVersion.getContentIdentifier(), user);

            logger.debug("Traitement du fichier ok !");

            if (export) {
                // Traitement de l'export
                //***********************
                File file = ExportUtils.exportModule(contentVersion, sourceFileLocale, errorMessagesKeys,
                        ExportFormat.USER_DEFINED, user);
                ThaleiaApplication.get().getEventService().storeEvent(EVENT_PROCESS_EXPORT, contentVersion.getContentIdentifier(), user);
                return file;
            } else {
                return contentVersion;
            }

        } catch (Exception e) {
            StringBuilder messages = getFeedbackMessages(locale, errorMessagesKeys, e);
            ThaleiaApplication.get().getEventService().storeEvent(EVENT_PROCESS_ERROR, messages.toString(), user);
            throw new Exception(messages.toString());
        }
    }

    private StringBuilder getFeedbackMessages(java.util.Locale locale, List<String> errorMessagesKeys, Exception e) {
        StringBuilder messages = new StringBuilder();

        // Pour faire moins peur, on n'envoie pas sur la page d'erreur.
        logger.warn("Erreur de traitement du fichier uploadé :" + e);
        logger.warn(LogUtils.getStackTrace(e.getStackTrace()));

        Analytics.getImplementation().logEvent(CannelleEvent.CannelleContentCreationError);

        // On présente les erreurs transmises par le traitement
        for (String key : errorMessagesKeys) {
            messages.append(LocalizedMessages.getMessageForLocale(key, locale)).append("\n");
        }

        messages.append(LocalizedMessages.getMessageForLocale("upload.error", locale)).append("\n");
        messages.append(LocalizedMessages.getMessageForLocale("help.on.hub", locale)).append("\n");
        return messages;
    }
}
