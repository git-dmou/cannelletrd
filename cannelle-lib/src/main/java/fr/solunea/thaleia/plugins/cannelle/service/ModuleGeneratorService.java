package fr.solunea.thaleia.plugins.cannelle.service;

import fr.solunea.thaleia.model.*;
import fr.solunea.thaleia.model.dao.ContentTypeDao;
import fr.solunea.thaleia.model.dao.LocaleDao;
import fr.solunea.thaleia.plugins.cannelle.messages.LocalizedMessages;
import fr.solunea.thaleia.plugins.cannelle.parsers.IdentifierTranslator;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.service.ContentPropertyService;
import fr.solunea.thaleia.service.ContentService;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import org.apache.cayenne.ObjectContext;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Interprète des couples clé / valeur pour générer un module.
 */
public class ModuleGeneratorService extends AbstractContentGeneratorService {

    static final Logger logger = Logger.getLogger(ModuleGeneratorService.class);

    public ModuleGeneratorService(Parameters parameters, ResourcesHandler resourcesHandler) {
        super(parameters, resourcesHandler);
    }

    /**
     * @return le ContentType demandé pour les modules.
     */
    public ContentType getModuleContentType(ObjectContext context) throws DetailedException {

        // Le nom du ContentType pour les modules.
        String contentTypeName = getContentTypeName();
        if (contentTypeName == null) {
            throw new DetailedException("Le nom du contentType pour les modules '" + Parameters.CONTENT_PROPERTY_MODULE
                    + Parameters.CONTENT_PROPERTY_NAME + "' n'a pas été renseigné dans les " + "paramètres.");
        }

        ContentTypeDao contentTypeDao = new ContentTypeDao(context);
        ContentType result = contentTypeDao.findByName(contentTypeName);

        if (result == null) {
            throw new DetailedException(
                    "Le contentType '" + contentTypeName + "' n'est pas reconnu par cette " + "instance de Thaleia.");
        }

        return result;
    }

    @Override
    protected String getContentTypeName() {
        return getParameters().getValue(Parameters.CONTENT_PROPERTY_MODULE + Parameters.CONTENT_PROPERTY_NAME);
    }

    /**
     * Récupération des informations du module (titre etc.) et création d'un
     * objet ContentVersion associé.
     *
     * @return la version du module nouvellement créé
     */
    public ContentVersion createModule(User author, Domain domain, Map<String, String> moduleProperties,
                                       ContentService contentService, ContentPropertyService contentPropertyService,
                                       ContentTypeDao contentTypeDao, LocaleDao localeDao) throws DetailedException {

        try {
            String identifier = IdentifierTranslator.parseIdentifier(moduleProperties, getParameters(),
                    "xls.parser" + ".module.", true);

            // On s'assure que le ContentType nécessaire existe
            logger.debug("On s'assure que le ContentType " + getContentTypeName() + " existe...");
            ContentType contentType = prepareContentType(getContentTypeName(), Parameters.CONTENT_PROPERTY_MODULE,
                    true, contentTypeDao);
            logger.debug("Le ContentType " + getContentTypeName() + " existe !");

            // On s'assure que les ContentProperty demandée dans les paramètres
            // sont bien définies en base, pour pouvoir les remplir ensuite
            logger.debug("On s'assure que les ContentProperty demandée dans les paramètres sont bien définies en "
                    + "base...");
            prepareContentProperties(Parameters.CONTENT_PROPERTY_MODULE + "properties", contentType);
            logger.debug("OLes ContentProperty demandée dans les paramètres sont bien définies en " + "base !");

            // Création du module en base
            ContentVersion contentVersion;
            try {
                logger.debug("Enregistrement du module dans la base");
                contentVersion = contentService.saveContent(domain, author, contentType, identifier, true, false, true);

            } catch (DetailedException e) {
                e.addMessage("Le module '" + identifier + "' n'a pas pu être créé.");
                throw e;
            }

            // Stockage des propriétés en ContentProperties
            try {
                Locale locale = getLocale(moduleProperties, localeDao);
                logger.debug("Création des ContentProperties " + moduleProperties);
                contentPropertyService.storePropertiesAsContentProperties(contentType, contentVersion, locale,
                        moduleProperties, getResourcesHandler().getUploadedFiles().getExpandedDir());

            } catch (DetailedException e) {
                // Le message localisé correspondant
                String message = LocalizedMessages.getMessage(LocalizedMessages.MODULE_PROPERTIES_ERROR);
                // On demande sa présentation
                ThaleiaSession.get().addError(message);

                e.addMessage("Les propriétés " + moduleProperties.toString()
                        + " n'ont pas pu être créées en tant que ContentProperties dans la base de données.");
                throw e;
            }

            return contentVersion;

        } catch (DetailedException e) {
            e.addMessage("Une erreur est survenue durant la création du module.");
            throw e;
        }

    }

    /**
     * @return la locale demandée dans ces paramètres
     */
    public Locale getLocale(Map<String, String> moduleProperties, LocaleDao localeDao) throws DetailedException {

        // Récupération de la locale des contenus à générer : il s'agit de la
        // valeur indiquée dans les paramètres.
        String localeName = moduleProperties.get(getParameters().getValue(Parameters.XLS_CONTENT_LANGAGE_TITLE));
        if (localeName == null) {
            throw new DetailedException("Le nom de la langue '" + Parameters.XLS_CONTENT_LANGAGE_TITLE + "' n'a pas "
                    + "été renseigné dans le fichier Excel.");
        }

        // On fait la correspondance entre cette valeur, et le nom des locales
        // telles qu'enregistrées dans Thaleia.
        Locale locale = null;
        if (localeName.equalsIgnoreCase(Parameters.LOCALE_EN_NAME_EN)
                || localeName.equalsIgnoreCase(Parameters.LOCALE_EN_NAME_FR)) {
            locale = localeDao.findByName(Parameters.LOCALE_EN);

        } else if (localeName.equalsIgnoreCase(Parameters.LOCALE_FR_NAME_FR)
                || localeName.equalsIgnoreCase(Parameters.LOCALE_FR_NAME_EN)) {
            locale = localeDao.findByName(Parameters.LOCALE_FR);

        } else if (localeName.equalsIgnoreCase(Parameters.LOCALE_ES_NAME_ES)) {
            locale = localeDao.findByName(Parameters.LOCALE_ES);

        } else if (localeName.equalsIgnoreCase(Parameters.LOCALE_NL_NAME_NL)) {
            locale = localeDao.findByName(Parameters.LOCALE_NL);

        } else if (localeName.equalsIgnoreCase(Parameters.LOCALE_DE_NAME_DE)) {
            locale = localeDao.findByName(Parameters.LOCALE_DE);

        } else if (localeName.equalsIgnoreCase(Parameters.LOCALE_IT_NAME_IT)) {
            locale = localeDao.findByName(Parameters.LOCALE_IT);

        } else if (localeName.equalsIgnoreCase(Parameters.LOCALE_ZH_NAME_ZH)) {
            locale = localeDao.findByName(Parameters.LOCALE_ZH);

        } else if (localeName.equalsIgnoreCase(Parameters.LOCALE_PT_NAME_PT)) {
            locale = localeDao.findByName(Parameters.LOCALE_PT);

        } else if (localeName.equalsIgnoreCase(Parameters.LOCALE_TR_NAME_TR)) {
            locale = localeDao.findByName(Parameters.LOCALE_TR);

        } else if (localeName.equalsIgnoreCase(Parameters.LOCALE_AR_NAME_AR)) {
            locale = localeDao.findByName(Parameters.LOCALE_AR);

        } else if (localeName.equalsIgnoreCase(Parameters.LOCALE_HE_NAME_HE)) {
            locale = localeDao.findByName(Parameters.LOCALE_HE);

        } else if (localeName.equalsIgnoreCase(Parameters.LOCALE_RU_NAME_RU)) {
            locale = localeDao.findByName(Parameters.LOCALE_RU);

        } else if (localeName.equalsIgnoreCase(Parameters.LOCALE_SK_NAME_SK)) {
            locale = localeDao.findByName(Parameters.LOCALE_SK);

        }

        if (locale == null) {
            throw new DetailedException(
                    "La langue '" + localeName + "' n'est pas reconnue par cette instance de " + "Thaleia.");
        }

        return locale;
    }

}
