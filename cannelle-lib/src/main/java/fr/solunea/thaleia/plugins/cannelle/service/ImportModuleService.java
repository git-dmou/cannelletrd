package fr.solunea.thaleia.plugins.cannelle.service;

import fr.solunea.thaleia.model.*;
import fr.solunea.thaleia.model.dao.*;
import fr.solunea.thaleia.plugins.cannelle.contents.IContent;
import fr.solunea.thaleia.plugins.cannelle.contents.parsing.ParsedObject;
import fr.solunea.thaleia.plugins.cannelle.messages.LocalizedMessages;
import fr.solunea.thaleia.plugins.cannelle.parsers.IModuleParserService;
import fr.solunea.thaleia.plugins.cannelle.parsers.IScreenParserService;
import fr.solunea.thaleia.plugins.cannelle.parsers.ModuleParserServiceFactory;
import fr.solunea.thaleia.plugins.cannelle.parsers.ScreenParserServiceFactory;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.service.ContentPropertyService;
import fr.solunea.thaleia.service.ContentService;
import fr.solunea.thaleia.service.TemplatedMailsService;
import fr.solunea.thaleia.service.utils.Configuration;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.utils.LogUtils;
import fr.solunea.thaleia.webapp.ThaleiaApplication;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImportModuleService extends ParametersAwareService {

    private static final Logger logger = Logger.getLogger(ImportModuleService.class);
    private final Configuration configuration;

    ScreenGeneratorService screenGeneratorService;
    ContentService contentService;
    ContentPropertyService contentPropertyService;

    public ImportModuleService(Parameters parameters, ResourcesHandler resourcesHandler, Configuration configuration,
                               ICayenneContextService context) throws DetailedException {
        super(parameters, resourcesHandler);

        // L'objet d'analyse des propri??t??s pour la g??n??ration des??crans
        screenGeneratorService = new ScreenGeneratorService(getParameters(), getResourcesHandler(), configuration, context);
        contentService = new ContentService(context, configuration);
        contentPropertyService = new ContentPropertyService(context, configuration);
        this.configuration = configuration;
    }

    /**
     * @param uploadedFile     : fichier zip contenant le fichier Excel et les m??dias
     * @param errorKeys        si une erreur de traitement appara??t, alors la m??thode
     *                         va ajouter dans ce tableau la CLE de l'erreur, par exemple
     *                         "xls.format.error". Ce sera au composant appelant de retrouver le
     *                         libell?? localis?? correspondant ?? ce code.
     * @param sourceFileLocale la locale du fichier source, qui n'est pas forc??ment la locale dans laquelle les
     */
    public ContentVersion importModule(File uploadedFile, List<String> errorKeys, Locale sourceFileLocale, User user, String origLanguage, String targetLanguage) throws DetailedException {

        ContentVersion module = null;
        List<ContentVersion> screens = new ArrayList<>();
        ContentProperty uploadedFileProperty = null;
        // On fabrique un contexte Cayenne dans lequel travailler.
        ObjectContext context = ThaleiaApplication.get().contextService.getNewContext();
        LocaleDao localeDao = new LocaleDao(context);
        ContentTypeDao contentTypeDao = new ContentTypeDao(context);
        ContentPropertyDao contentPropertyDao = new ContentPropertyDao(context);
        ContentPropertyValueDao contentPropertyValueDao = new ContentPropertyValueDao(configuration.getLocalDataDir().getAbsolutePath(),
                configuration.getBinaryPropertyType(), context);
        // On transf??re les param??tres dans ce contexte
        Locale localSourceFileLocale = context.localObject(sourceFileLocale);
        User localUser = context.localObject(user);

        if (localUser == null) {
            logger.info("La session a expir??.");
            errorKeys.add("session.expired");
            throw new DetailedException("La session a expir??.");
        }

        try {
            // R??cup??ration des propri??t??s du module d??crites dans le
            // fichier d'entr??e (Xls, Ppt..).
            Map<String, String> moduleProperties;
            // Objet d'analyse ce des propri??t??s
            ModuleGeneratorService moduleGeneratorService;
            try {
                IModuleParserService moduleParserService = new ModuleParserServiceFactory().getObject(getParameters()
                        , getResourcesHandler());

                moduleProperties = moduleParserService.getModuleProperties(origLanguage, targetLanguage);

                // L'objet d'analyse des propri??t??s pour la g??n??ration du module
                moduleGeneratorService = new ModuleGeneratorService(getParameters(), getResourcesHandler());

            } catch (DetailedException e) {
                throw new DetailedException(e).addMessage(
                        "Impossible de r??cup??rer les propri??t??s du module ?? g??n??rer" + ".");
            }

            // La locale du contenu qui doit ??tre g??n??r??
            Locale locale;
            try {
                locale = moduleGeneratorService.getLocale(moduleProperties, localeDao);
            } catch (DetailedException e) {
                errorKeys.add("locale.not.found");
                throw e.addMessage("Impossible de retrouver la locale demand??e pour cet import.");
            }

            // R??cup??ration du nom (localis?? si possible dans la langue du contenu g??n??r??) du plugin et de sa
            // version, pour ensuite le stocker comme propri??t??s des ??crans g??n??r??s.
            // Cette partie n'est pas maintenue avec l'extension des locales support??es, mais ceci ne
            // semble pas poser de probl??me.
            String pluginName;
            if (Parameters.LOCALE_EN.equals(locale.getName())) {
                pluginName = getParameters().getValue(Parameters.PLUGIN_NAME + "." + Parameters.LOCALE_EN);
            } else {
                pluginName = getParameters().getValue(Parameters.PLUGIN_NAME + "." + Parameters.LOCALE_FR);
            }
            String pluginVersion = getParameters().getValue(Parameters.PLUGIN_VERSION);

            // On rajoute le nom et la version du plugin comme propri??t??s
            moduleProperties.put(getParameters().getValue(Parameters.PLUGIN_NAME_PROPERTY), pluginName);
            moduleProperties.put(getParameters().getValue(Parameters.PLUGIN_VERSION_PROPERTY), pluginVersion);

            // Cr??ation du module
            try {
                logger.debug("Cr??ation du module.");
                Domain domain = localUser.getDomain();

                // G??n??ration du module
                module = moduleGeneratorService.createModule(localUser, domain, moduleProperties, contentService,
                        contentPropertyService, contentTypeDao, localeDao);

            } catch (DetailedException e) {
                errorKeys.add("module.creation.error");
                throw new DetailedException(e).addMessage("Le module n'a pas pu ??tre cr????.");
            }

            // Cr??ation des ??crans
            try {
                // R??cup??ration du parser d'??crans
                IScreenParserService screenParserService;
                if (targetLanguage.equals("")) {
                    screenParserService = new ScreenParserServiceFactory(Parameters.SCREEN_PARSER_IMPLEMENTATION).getObject(getParameters(), getResourcesHandler());
                } else {
                    screenParserService = new ScreenParserServiceFactory(Parameters.SCREEN_PARSER_TRANSLATOR_IMPLEMENTATION).getObject(getParameters(), getResourcesHandler(), origLanguage, targetLanguage);

                }

                //TODO traitement des fichiers al??atoire

                // R??cup??ration du parser d'??crans
                IScreenParserService screenRandomParserService =
                        new ScreenParserServiceFactory(Parameters.SCREEN_RANDOM_PARSER_IMPLEMENTATION).getObject(getParameters(), getResourcesHandler());

                // R??cup??ration de la d??finition des ??crans dans le Excel
                // On utilise la locale du fichier Excel (qui n'est pas forc??ment la locale des contenus ?? g??n??rer)
                List<ParsedObject<IContent>> screenDefinitions = screenParserService.getScreens(localSourceFileLocale,
                        localUser, moduleProperties.get("Identifiant"));

                // R??cup??ration de la d??finition des ??crans pour le tirage al??atoire
                // On utilise la locale du fichier Excel (qui n'est pas forc??ment la locale des contenus ?? g??n??rer)
                try {
                    screenDefinitions.addAll(screenRandomParserService.getScreens(localSourceFileLocale,
                            localUser, moduleProperties.get("Identifiant")));
                } catch (DetailedException e) {
                    logger.debug("Absence de la feuille des ??crans de tirage al??atoire");
                }
                // On ajoute comme propri??t?? de chaque ??cran le nom et la
                // version du plugin
                for (ParsedObject<IContent> screenParsedObject : screenDefinitions) {
                    IContent screen = screenParsedObject.getObject();
                    screen.addProperty(getParameters().getValue(Parameters.PLUGIN_NAME_PROPERTY), pluginName);
                    screen.addProperty(getParameters().getValue(Parameters.PLUGIN_VERSION_PROPERTY), pluginVersion);
                }

                // G??n??ration des ??crans.
                screens = screenGeneratorService.storeScreens(screenDefinitions, locale, contentTypeDao,
                        contentPropertyService, localUser);

            } catch (DetailedException e) {
                errorKeys.add("screens.creation.error");
                throw new DetailedException(e).addMessage("Les ??crans ne peuvent pas ??tre g??n??r??s.");
            }

            // On affecte les contenus import??s ?? ce module
            try {
                logger.debug("Affectation des contenus.");
                contentService.setChildren(module, screens, false);

            } catch (DetailedException e) {
                errorKeys.add("content.storage.error");
                throw new DetailedException(e).addMessage("Les contenus n'ont pas pu ??tre associ?? au module.");
            }

            // On stocke le fichier qui a ??t?? import?? dans une propri??t?? du
            // module
            uploadedFileProperty = contentPropertyDao.findByName("SourceFile");
            if (uploadedFileProperty != null) {
                try {
                    logger.debug("Stockage du fichier upload??.");
                    // On r??cup??re la valeur de cette propri??t?? (pour cette
                    // propri??t??, de ce module, pour cette locale)
                    List<ContentPropertyValue> uploadedFilePropertyValues = contentPropertyValueDao.find(module,
                            uploadedFileProperty, locale);
                    ContentPropertyValue uploadedFilePropertyValue;
                    if (uploadedFilePropertyValues.isEmpty()) {
                        // Elle n'existe pas encore : on la cr????
                        uploadedFilePropertyValue = contentPropertyValueDao.get();
                        uploadedFilePropertyValue.setLocale(locale);
                        uploadedFilePropertyValue.setContentVersion(module);
                        uploadedFilePropertyValue.setProperty(uploadedFileProperty);
                    } else {
                        // Elle existe d??j?? : on la r??cup??re
                        uploadedFilePropertyValue = uploadedFilePropertyValues.get(0);
                    }
                    // On fixe sa valeur avec ce binaire.
                    contentPropertyValueDao.setFile(uploadedFilePropertyValue, uploadedFile, uploadedFile.getName());
                    contentPropertyValueDao.save(uploadedFilePropertyValue);

                } catch (DetailedException e) {
                    errorKeys.add("content.storage.error");
                    throw new DetailedException(e).addMessage("Le fichier upload?? n'a pas pu ??tre associ?? au module.");
                }
            } else {
                logger.debug("Pas de propri??t?? pour le stockage du fichier upload??.");
            }

            context.commitChanges();
            return user.getObjectContext().localObject(module);

        } catch (Exception e) {
            // On supprime les objets ??ventuellement partiellement cr????s (pas que les objets du contexte Cayenne, maius aussi les ??ventuels fichiers cr????s)
            safeDelete(contentService, module);
            safeDelete(contentService, screens.toArray());
            safeDelete(contentService, uploadedFileProperty);

            // On pr??pare le corps de l'email de rapport d'erreur
            StringBuilder details = new StringBuilder(
                    "Une erreur a eu lieu lors de l'import dans Cannelle d'un " + "fichier par " + "l'utilisateur "
                            + localUser.getLogin() + " (" + localUser.getName() + ") sur l'instance "
                            + ThaleiaApplication.get().getApplicationRootUrl());
            // On met de c??t?? le fichier upload??
            File destination =
                    ThaleiaApplication.get().getTempFilesService().getLonglifeTempFile(uploadedFile.getName());
            details.append("<br><br>Le fichier upload?? sera conserv?? jusqu'?? cette nuit sur le serveur ?? cet emplacement : ").append(destination.getAbsolutePath());
            details.append("<br><br>Messages d'erreurs pr??sent??s ?? l'utilisateur :");
            for (String error : errorKeys) {
                details.append("<br>").append(LocalizedMessages.getMessage(error));
            }
            details.append("<br><br>Message d'erreur technique : ").append(e);
            details.append("<br>Pile d'appel : ").append(LogUtils.getStackTrace(e.getStackTrace()));
            details.append("<br><br>Exception : ").append(e);
            try {
                FileUtils.copyFile(uploadedFile, destination);
            } catch (IOException ex) {
                logger.warn("Impossible de copier le fichier upload?? qui a d??clench?? un rapport d'erreur : " + ex);
            }

            new TemplatedMailsService(ThaleiaSession.get().getContextService().getContextSingleton()).sendErrorReport("cannelle.import.error.report.email",
                    "cannelle.import.error.report.subject", details.toString());

            throw new DetailedException(e).addMessage("Une erreur est survenue durant la cr??ation du module.");
        }
    }

    private void safeDelete(ContentService contentService, Object... objects) {
        if (objects == null) {
            return;
        }
        try {
            for (Object object : objects) {
                if (object != null) {
                    if (ContentVersion.class.isAssignableFrom(object.getClass())) {
                        contentService.deleteContentVersion((ContentVersion) object);
                    } else if (CayenneDataObject.class.isAssignableFrom(object.getClass())) {
                        ((CayenneDataObject) object).getObjectContext().deleteObject(object);
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Impossible de supprimer l'objet : " + e);
        }
    }
}
