package fr.solunea.thaleia.plugins.cannelle.service;

import fr.solunea.thaleia.model.*;
import fr.solunea.thaleia.model.dao.ContentPropertyDao;
import fr.solunea.thaleia.model.dao.ContentPropertyValueDao;
import fr.solunea.thaleia.model.dao.ContentTypeDao;
import fr.solunea.thaleia.model.dao.ICayenneContextService;
import fr.solunea.thaleia.plugins.cannelle.contents.A7Content;
import fr.solunea.thaleia.plugins.cannelle.contents.A7File;
import fr.solunea.thaleia.plugins.cannelle.contents.IContent;
import fr.solunea.thaleia.plugins.cannelle.contents.parsing.ParsedObject;
import fr.solunea.thaleia.plugins.cannelle.messages.LocalizedMessages;
import fr.solunea.thaleia.plugins.cannelle.messages.ParameteredMessage;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.service.ContentPropertyService;
import fr.solunea.thaleia.service.ContentService;
import fr.solunea.thaleia.service.utils.Configuration;
import fr.solunea.thaleia.service.utils.ZipUtils;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.ThaleiaApplication;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScreenGeneratorService extends AbstractContentGeneratorService {

    /**
     * Le nom du ContentProperty correspondant au fichier A7
     */
    private static final String CONTENT_PROPERTY_A7CONTENT = "A7Content";

    private static final Logger logger = Logger.getLogger(ScreenGeneratorService.class);
    private final ContentService contentService;

    ScreenGeneratorService(Parameters parameters, ResourcesHandler resourcesHandler, Configuration configuration,
                           ICayenneContextService context) {
        super(parameters, resourcesHandler);
        contentService = new ContentService(context, configuration);
    }

    /**
     * Enregistre ces ??crans dans Thaleia.
     */
    List<ContentVersion> storeScreens(List<ParsedObject<IContent>> screenDefinitions, Locale locale,
                                      ContentTypeDao contentTypeDao, ContentPropertyService contentPropertyService,
                                      User author) throws DetailedException {
        try {
            List<ContentVersion> result = new ArrayList<>();

            // On s'assure que le ContentType n??cessaire existe
            ContentType contentType = prepareContentType(getContentTypeName(), Parameters.CONTENT_PROPERTY_SCREEN,
                    false, contentTypeDao);

            // On s'assure que les ContentProperty demand??e dans les param??tres
            // sont bien d??finies en base, pour pouvoir les remplir ensuite
            prepareContentProperties(Parameters.CONTENT_PROPERTY_SCREEN + "properties", contentType);

            for (ParsedObject<IContent> screenParsedObject : screenDefinitions) {
                IContent screen = screenParsedObject.getObject();
                String location = screenParsedObject.getOriginalLocation();

                if (!(screen instanceof A7Content)) {
                    throw new DetailedException(
                            "Ce g??n??rateur ne prend en entr??e que des " + A7Content.class.getName() + " !");
                }

                // Cr??ation de l'??cran en base
                ContentVersion contentVersion;
                try {
                    logger.debug("Enregistrement de l'??cran dans la base");
                    contentVersion = storeScreenAsContentVersion((A7Content) screen, CONTENT_PROPERTY_A7CONTENT,
                            contentType, locale, author);

                    // On conserve une r??f??rence sur cet ??cran ajout??
                    result.add(contentVersion);

                } catch (DetailedException e) {
                    // Le message localis?? correspondant. Il n'est pas dans les
                    // properties de cannell-lib (LocalizedMessages), car il
                    // d??pend du type de fichier dans lequel le plugin a cherch??
                    // (Xls, Ppt,...). Donc il est dans les .properties.
                    String message = ParameteredMessage.getMessage(ParameteredMessage.STORE_SCREEN_ERROR,
                            getParameters(), location);

                    // On demande sa pr??sentation
                    ThaleiaSession.get().addError(message);

                    throw new DetailedException(e).addMessage("L'??cran n'a pas pu ??tre stock?? en base : " + message);
                }

                // Stockage des propri??t??s en ContentProperties
                try {
                    logger.debug("Enregistrement des ContentProperties : " + screen.getProperties());

                    contentPropertyService.storePropertiesAsContentProperties(contentType, contentVersion, locale,
                            screen.getProperties(), getResourcesHandler().getUploadedFiles().getExpandedDir());

                } catch (DetailedException e) {
                    // Le message localis?? correspondant
                    String message = LocalizedMessages.getMessage(LocalizedMessages.STORE_SCREEN_PROPERTIES_ERROR,
                            location);

                    // On demande sa pr??sentation
                    ThaleiaSession.get().addError(message);

                    e.addMessage("Les propri??t??s " + screen.getProperties().toString()
                            + " n'ont pas pu ??tre cr????es en tant que ContentProperties dans la base de donn??es.");
                    throw e;
                }
            }

            if (!screenDefinitions.isEmpty()) {
                A7Content content = (A7Content) (screenDefinitions.get(0).getObject());
                A7File a7File = content.getMainContent();
                File a7FileMainFile = a7File.getFile();
                File screenDir = a7FileMainFile.getParentFile().getParentFile();
                try {
                    logger.debug("Suppression des fichiers de " + screenDir);
                    FileUtils.deleteQuietly(screenDir);
                    logger.debug("Suppression du dossier " + screenDir);
                    FileUtils.deleteDirectory(screenDir);
                } catch (IOException e) {
                    throw new DetailedException(e).addMessage("Impossible de supprimer le r??pertoire temporaire "
                            + "o?? sont stock??s les d??finitions des ??crans : " + screenDir.getAbsolutePath());
                }
            }

            return result;

        } catch (Exception e) {
            throw new DetailedException(e).addMessage("Les ??crans n'ont pas pu ??tre g??n??r??s d'apr??s leur description.");
        }
    }

    @Override
    protected String getContentTypeName() {
        return getParameters().getValue(Parameters.CONTENT_PROPERTY_SCREEN + Parameters.CONTENT_PROPERTY_NAME);
    }

    /**
     * Enregistre cet ??cran en base, les binaires associ??s, et renvoie l'objet
     * ContentVersion correspondant.
     *
     * @param binaryPropertyName le nom de la propri??t?? ?? laquelle associer le binaire de
     *                           Screen
     */
    private ContentVersion storeScreenAsContentVersion(A7Content screen, String binaryPropertyName,
                                                       ContentType contentType, Locale locale, User author) throws DetailedException {

        try {
            ContentPropertyDao contentPropertyDao = new ContentPropertyDao(contentType.getObjectContext());
            ContentPropertyValueDao contentPropertyValueDao = new ContentPropertyValueDao(ThaleiaApplication.get().getConfiguration().getLocalDataDir().getAbsolutePath(), ThaleiaApplication.get().getConfiguration().getBinaryPropertyType(), author.getObjectContext());
            Domain domain = author.getDomain();

            String screenId = screen.getIdentifier();

            if (screenId == null || "".equals(screenId)) {
                throw new DetailedException(
                        "Impossible de retrouver l'identifiant de cet ??cran ! " + "Les propri??t??s existantes sont : "
                                + screen.getProperties());
            }

            logger.debug("On ins??re dans la base de donn??es le contenu " + screenId + " avec les fichiers "
                    + screen.getMainContent().getFile().getParentFile().getAbsolutePath());

            // On enregistre ce contenu en base.
            // RMAR : on cherche ?? pas ?? mettre ?? jour d'??ventuels versions de
            // ces ??crans. Cela pose probl??me quand on nomme deux ??crans "q1"
            // dans deux questionnaires diff??rents : ils vont se mettre ?? jour
            // mutuellement.
            // On cr??e donc syst??matiquement une nouvelle version des ??crans.
            ContentVersion result = contentService.saveContent(domain, author, contentType, screenId, false, false,
                    false);

            // On REMPLACE la VALEUR de la ContentProperty de cette version qui
            // r??f??rence le binaire : actuellement la valeur est celle de la
            // version pr??c??dente

            // On recherche la propri??t?? qui porte le binaire de l'??cran
            ContentProperty binaryContentProperty = contentPropertyDao.findByName(binaryPropertyName);

            // On supprime la (les) valeur(s) existante(s)
            contentPropertyValueDao.deleteValues(result, binaryContentProperty, locale);

            // On cr??e une nouvelle valeur pour cette propri??t??
            ContentPropertyValue contentPropertyValue = contentPropertyValueDao.get();
            contentPropertyValue.setProperty(binaryContentProperty);
            contentPropertyValue.setLocale(locale);
            contentPropertyValue.setContentVersion(result);

            // On archive dans un Zip les fichiers de l'??cran : c'est ce Zip
            // que l'on stocke comme binaire.
            String zipPath = screen.getZipPath() + File.separator + result.getContentIdentifier().concat(".zip");
            logger.debug("Pr??paration de l'archive " + zipPath
                    + " pour le binaire de l'??cran, avec les fichiers contenus dans ");
            File zipFiles = ZipUtils.toZip(screen.getZipPath(), zipPath);

            // Enregistrement de la propri??t?? et copie du binaire.
            contentPropertyValueDao.setFile(contentPropertyValue, zipFiles, "");
            contentPropertyValueDao.save(contentPropertyValue);

            return result;

        } catch (DetailedException e) {
            e.addMessage("Impossible d'importer un ??cran.");
            throw e;
        }

    }
}
