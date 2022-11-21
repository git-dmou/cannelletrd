package fr.solunea.thaleia.plugins.cannelle.contents;

import fr.solunea.thaleia.model.ContentProperty;
import fr.solunea.thaleia.model.ContentPropertyValue;
import fr.solunea.thaleia.model.ContentVersion;
import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.model.dao.ContentPropertyDao;
import fr.solunea.thaleia.model.dao.ContentPropertyValueDao;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.utils.LogUtils;
import fr.solunea.thaleia.webapp.ThaleiaApplication;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Un objet qui correspond à un ContentVersion dans Thaleia, et sur laquelle on
 * peut obtenir des propriétés stockées dans Thaleia.
 */
public abstract class AbstractVersionedContent extends AbstractContent {

    private static final Logger logger = Logger.getLogger(AbstractVersionedContent.class);

    private final ContentVersion version;

    public AbstractVersionedContent(ContentVersion version) {
        super();
        this.version = version;
    }

    public ContentVersion getVersion() {
        return version;
    }

    /**
     * @return l'identifiant interne à Thaleia de cet écran, tel que stocké dans une contentVersion de Thaleia.
     */
    @Override
    public String getIdentifier() {
        return getVersion().getContentIdentifier();
    }

    @Override
    public void setIdentifier(String id) throws DetailedException {
        throw new DetailedException("Pourquoi modifier l'identifiant interne ?").addMessage(LogUtils.getStackTrace
                (Thread.currentThread().getStackTrace()));
    }

    /**
     * @param propertyName le nom de la ContentProperty, tel que stocké dans Thaleia
     * @param locale       la locale dans laquelle on veut la valeur
     * @param defaultValue la valeur par défaut
     * @return la valeur textuelle de cette ContentProperty, dans cette locale
     */
    public String getContentVersionPropertyValue(String propertyName, Locale locale, String defaultValue) {

        try {
            // On recherche la valeur de cette propriété dans la version.
            ContentPropertyValue value = getContentVersionProperty(propertyName, locale);
            return value.getValue();

        } catch (Exception e) {
            logger.debug("La propriété '" + propertyName + "' n'a pas de valeur définie pour la locale '" + locale
                    .getName() + "' pour la version " + getVersion());
            // On garde la valeur par défaut

        }

        return defaultValue;
    }

    /**
     * @param propertyName le nom de la ContentProperty, tel que stocké dans Thaleia
     * @param locale       la locale dans laquelle on veut la valeur
     * @return la ContentPropertyValue de cette ContentProperty, dans cette locale
     */
    public ContentPropertyValue getContentVersionProperty(String propertyName, Locale locale) {

        try {
            // On recherche la valeur de cette propriété dans la version.
            ContentPropertyValueDao contentPropertyValueDao = new ContentPropertyValueDao(ThaleiaApplication.get().getConfiguration().getLocalDataDir().getAbsolutePath(), ThaleiaApplication.get().getConfiguration().getBinaryPropertyType(), locale.getObjectContext());
            ContentProperty contentProperty = new ContentPropertyDao(locale.getObjectContext()).findByName(propertyName);

            if (contentProperty == null) {
                logger.debug("La ContentProperty dont le nom est '" + propertyName + "' n'a pas été trouvée !");
                return null;
            }

            logger.debug("Recherche des valeurs de la ContentProperty dont le nom est '" + propertyName + "' : " +
                    contentProperty);

            List<ContentPropertyValue> values = contentPropertyValueDao.find(getVersion(), contentProperty, locale);
            if (values.size() > 0) {
                return values.get(0);
            } else {
                return null;
            }

        } catch (Exception e) {
            logger.debug("La propriété '" + propertyName + "' n'a pas de valeur définie pour la locale '" + locale
                    .getName() + "' pour la version " + getVersion());
            // On garde la valeur par défaut

            logger.debug("Valeurs existantes : ");
            for (ContentPropertyValue value : getVersion().getProperties()) {
                logger.debug("locale=" + value.getLocale().getName() + " contentProperty=" + value.getProperty()
                        .getName() + " value=" + value.getValue());
            }
        }

        return null;
    }

}
