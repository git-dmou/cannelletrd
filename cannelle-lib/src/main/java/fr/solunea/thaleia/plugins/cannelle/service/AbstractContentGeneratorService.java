package fr.solunea.thaleia.plugins.cannelle.service;

import fr.solunea.thaleia.model.ApplicationParameter;
import fr.solunea.thaleia.model.ContentType;
import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.model.dao.ApplicationParameterDao;
import fr.solunea.thaleia.model.dao.ContentTypeDao;
import fr.solunea.thaleia.model.dao.LocaleDao;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.service.ContentPropertyService;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractContentGeneratorService extends ParametersAwareService {

    private static final Logger logger = Logger.getLogger(AbstractContentGeneratorService.class);

    private static final String TRUE = "true";

    public AbstractContentGeneratorService(Parameters parameters, ResourcesHandler resourcesHandler) {
        super(parameters, resourcesHandler);
    }

    /**
     * @return le nom du ContentType demandé pour les contenus à générer.
     */
    protected abstract String getContentTypeName();

    /**
     * @param contentTypeName           le nom du contentType dont il faut assurer l'existence. Par
     *                                  exemple "module_cayenne" ou "screen_cayenne"
     * @param contentTypePropertyPrefix Dans le fichier properties, préfixe permettant d'identifier
     *                                  les propriétés d'un ContentProperty de type contentTypeName.
     *                                  Par exemple "content.module." ou "content.screen."
     * @param isModule                  indique si on doit associer les ContentProperties d'un module,
     *                                  sinon ce seront celles d'un écran.
     */
    protected ContentType prepareContentType(String contentTypeName, String contentTypePropertyPrefix,
											 boolean isModule, ContentTypeDao contentTypeDao) throws DetailedException {

        ContentType contentType;

        // Vérification de l'existence
        if (contentTypeDao.existsWithName(contentTypeName, null)) {

            // Le contentType existe déjà.
            contentType = contentTypeDao.findByName(contentTypeName);

        } else {
            // Création de ce contentType
            try {
                logger.debug("Création du contentType '" + contentTypeName + "'");

                contentType = contentTypeDao.get();
                contentType.setName(contentTypeName);
                contentType.setIsModuleType(isModule);
                contentTypeDao.save(contentType);

                // On ajoute les intitulé FR et EN du ContentType dans la bdd
                ApplicationParameterDao applicationParameterDao = new ApplicationParameterDao(contentType.getObjectContext());

                ApplicationParameter applicationParameterFR = applicationParameterDao.get();
                applicationParameterFR.setName(
                        ContentTypeDao.CONTENT_TYPE_PARAM + contentTypeName + "." + Parameters.LOCALE_FR);
                applicationParameterFR.setValue(getParameters().getValue(
                        contentTypePropertyPrefix + Parameters.LOCALE_FR));
                applicationParameterDao.save(applicationParameterFR);
                logger.debug("Ajout de l'intitulé fr '" + applicationParameterFR.getValue() + "' pour ce ContentType.");

                ApplicationParameter applicationParameterEN = applicationParameterDao.get();
                applicationParameterEN.setName(
                        ContentTypeDao.CONTENT_TYPE_PARAM + contentTypeName + "." + Parameters.LOCALE_EN);
                applicationParameterEN.setValue(getParameters().getValue(
                        contentTypePropertyPrefix + Parameters.LOCALE_EN));
                applicationParameterDao.save(applicationParameterEN);
                logger.debug("Ajout de l'intitulé en '" + applicationParameterEN.getValue() + "' pour ce ContentType.");

            } catch (DetailedException e) {
                e.addMessage("Impossible de créer le ContentType '" + contentTypeName + "'. ");
                throw e;
            }

        }

        return contentType;
    }

    /**
     * @param prefix S'assure de l'existence des toutes les ContentProperties pour
     *               ce ContentType, définies avec ce préfixe dans les paramètres,
     *               de type [prefixe].name, [prefixe].type, [prefixe].hidden, etc.
     */
    protected void prepareContentProperties(String prefix, ContentType contentType) throws DetailedException {

        int propertiesMaxIndex;
        try {
            propertiesMaxIndex = getParameters().getMaxIndexForKey(prefix);
        } catch (DetailedException e) {
            throw new DetailedException(e).addMessage("Impossible de retrouver le nombre de propriétés paramétrées.");
        }

        ContentPropertyService contentPropertyService = ThaleiaSession.get().getContentPropertyService();

        LocaleDao localeDao = new LocaleDao(contentType.getObjectContext());
        Locale fr = localeDao.findByName("fr");
        Locale en = localeDao.findByName("en");

        for (int i = 0; i <= propertiesMaxIndex; i++) {

            // Value = identifiant du ContentType (Title, CommunicationSCORM
            // etc.)
            String contentPropertyName = getParameters().getValue(prefix + "." + i + ".name");

            // TitleFR
            String titleFR = getParameters().getValue(prefix + "." + i + ".fr");
            logger.debug(
                    "Recherche de l'intitulé fr de la propriété '" + contentPropertyName + "' : " + prefix + "." + i
                            + ".fr = " + titleFR);

            // TitleEN
            String titleEN = getParameters().getValue(prefix + "." + i + ".en");
            logger.debug(
                    "Recherche de l'intitulé en de la propriété '" + contentPropertyName + "' : " + prefix + "." + i
                            + ".en = " + titleEN);

            Map<Locale, String> names = new HashMap<>(2);
            names.put(fr, titleFR);
            names.put(en, titleEN);

            // Type
            String valueTypeName = getParameters().getValue(prefix + "." + i + ".type");

            // Attribut hidden
            String hiddenString = getParameters().getValue(prefix + "." + i + ".hidden");
            boolean hidden = false;
            if (hiddenString.equalsIgnoreCase(TRUE)) {
                hidden = true;
            }

            // Création en base
            contentPropertyService.createContentProperty(contentPropertyName, names, valueTypeName, hidden, contentType);
        }

    }
}
