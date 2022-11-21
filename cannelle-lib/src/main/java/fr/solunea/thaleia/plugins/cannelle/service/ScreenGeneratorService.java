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
     * Enregistre ces écrans dans Thaleia.
     */
    List<ContentVersion> storeScreens(List<ParsedObject<IContent>> screenDefinitions, Locale locale,
                                      ContentTypeDao contentTypeDao, ContentPropertyService contentPropertyService,
                                      User author) throws DetailedException {
        try {
            List<ContentVersion> result = new ArrayList<>();

            // On s'assure que le ContentType nécessaire existe
            ContentType contentType = prepareContentType(getContentTypeName(), Parameters.CONTENT_PROPERTY_SCREEN,
                    false, contentTypeDao);

            // On s'assure que les ContentProperty demandée dans les paramètres
            // sont bien définies en base, pour pouvoir les remplir ensuite
            prepareContentProperties(Parameters.CONTENT_PROPERTY_SCREEN + "properties", contentType);

            for (ParsedObject<IContent> screenParsedObject : screenDefinitions) {
                IContent screen = screenParsedObject.getObject();
                String location = screenParsedObject.getOriginalLocation();

                if (!(screen instanceof A7Content)) {
                    throw new DetailedException(
                            "Ce générateur ne prend en entrée que des " + A7Content.class.getName() + " !");
                }

                // Création de l'écran en base
                ContentVersion contentVersion;
                try {
                    logger.debug("Enregistrement de l'écran dans la base");
                    contentVersion = storeScreenAsContentVersion((A7Content) screen, CONTENT_PROPERTY_A7CONTENT,
                            contentType, locale, author);

                    // On conserve une référence sur cet écran ajouté
                    result.add(contentVersion);

                } catch (DetailedException e) {
                    // Le message localisé correspondant. Il n'est pas dans les
                    // properties de cannell-lib (LocalizedMessages), car il
                    // dépend du type de fichier dans lequel le plugin a cherché
                    // (Xls, Ppt,...). Donc il est dans les .properties.
                    String message = ParameteredMessage.getMessage(ParameteredMessage.STORE_SCREEN_ERROR,
                            getParameters(), location);

                    // On demande sa présentation
                    ThaleiaSession.get().addError(message);

                    throw new DetailedException(e).addMessage("L'écran n'a pas pu être stocké en base : " + message);
                }

                // Stockage des propriétés en ContentProperties
                try {
                    logger.debug("Enregistrement des ContentProperties : " + screen.getProperties());

                    contentPropertyService.storePropertiesAsContentProperties(contentType, contentVersion, locale,
                            screen.getProperties(), getResourcesHandler().getUploadedFiles().getExpandedDir());

                } catch (DetailedException e) {
                    // Le message localisé correspondant
                    String message = LocalizedMessages.getMessage(LocalizedMessages.STORE_SCREEN_PROPERTIES_ERROR,
                            location);

                    // On demande sa présentation
                    ThaleiaSession.get().addError(message);

                    e.addMessage("Les propriétés " + screen.getProperties().toString()
                            + " n'ont pas pu être créées en tant que ContentProperties dans la base de données.");
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
                    throw new DetailedException(e).addMessage("Impossible de supprimer le répertoire temporaire "
                            + "où sont stockés les définitions des écrans : " + screenDir.getAbsolutePath());
                }
            }

            return result;

        } catch (Exception e) {
            throw new DetailedException(e).addMessage("Les écrans n'ont pas pu être générés d'après leur description.");
        }
    }

    @Override
    protected String getContentTypeName() {
        return getParameters().getValue(Parameters.CONTENT_PROPERTY_SCREEN + Parameters.CONTENT_PROPERTY_NAME);
    }

    /**
     * Enregistre cet écran en base, les binaires associés, et renvoie l'objet
     * ContentVersion correspondant.
     *
     * @param binaryPropertyName le nom de la propriété à laquelle associer le binaire de
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
                        "Impossible de retrouver l'identifiant de cet écran ! " + "Les propriétés existantes sont : "
                                + screen.getProperties());
            }

            logger.debug("On insère dans la base de données le contenu " + screenId + " avec les fichiers "
                    + screen.getMainContent().getFile().getParentFile().getAbsolutePath());

            // On enregistre ce contenu en base.
            // RMAR : on cherche à pas à mettre à jour d'éventuels versions de
            // ces écrans. Cela pose problème quand on nomme deux écrans "q1"
            // dans deux questionnaires différents : ils vont se mettre à jour
            // mutuellement.
            // On crée donc systématiquement une nouvelle version des écrans.
            ContentVersion result = contentService.saveContent(domain, author, contentType, screenId, false, false,
                    false);

            // On REMPLACE la VALEUR de la ContentProperty de cette version qui
            // référence le binaire : actuellement la valeur est celle de la
            // version précédente

            // On recherche la propriété qui porte le binaire de l'écran
            ContentProperty binaryContentProperty = contentPropertyDao.findByName(binaryPropertyName);

            // On supprime la (les) valeur(s) existante(s)
            contentPropertyValueDao.deleteValues(result, binaryContentProperty, locale);

            // On crée une nouvelle valeur pour cette propriété
            ContentPropertyValue contentPropertyValue = contentPropertyValueDao.get();
            contentPropertyValue.setProperty(binaryContentProperty);
            contentPropertyValue.setLocale(locale);
            contentPropertyValue.setContentVersion(result);

            // On archive dans un Zip les fichiers de l'écran : c'est ce Zip
            // que l'on stocke comme binaire.
            String zipPath = screen.getZipPath() + File.separator + result.getContentIdentifier().concat(".zip");
            logger.debug("Préparation de l'archive " + zipPath
                    + " pour le binaire de l'écran, avec les fichiers contenus dans ");
            File zipFiles = ZipUtils.toZip(screen.getZipPath(), zipPath);

            // Enregistrement de la propriété et copie du binaire.
            contentPropertyValueDao.setFile(contentPropertyValue, zipFiles, "");
            contentPropertyValueDao.save(contentPropertyValue);

            return result;

        } catch (DetailedException e) {
            e.addMessage("Impossible d'importer un écran.");
            throw e;
        }

    }
}
