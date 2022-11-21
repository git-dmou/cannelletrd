package fr.solunea.thaleia.plugins.cannelle.messages;

import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import fr.solunea.thaleia.webapp.utils.MessagesUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Session;

import java.util.Arrays;
import java.util.Locale;

/**
 * Un message dont la valeur est stockée dans les Paramètres.
 */
public class ParameteredMessage {

    private final static Logger logger = Logger.getLogger(ParameteredMessage.class);
    /**
     * Le nom du paramètre qui contient le message décrivant une erreur liée à
     * l'enregistrement de l'écran qui est situé à l'endroit {0} dans le fichier
     * d'entrée.
     */
    public static final String STORE_SCREEN_ERROR = "store.screen.error";

    /**
     * Recherche la valeur de la chaîne localisée dans le fichier .properties
     *
     * @param resourceKey le code du message à rechercher
     * @param parameters  si besoin, des paramètres pour appliquer des interprétations
     *                    de valeurs dans la chaîne de caractères ${0}, ${1}...
     * @return le message d'erreur localisé
     */
    public static String getMessage(String resourceKey, Parameters params, Object... parameters) {

        logger.debug("Recherche du message '" + resourceKey + "' avec les paramètres : " + Arrays.toString(parameters));

        Locale locale = Session.exists() ? ThaleiaSession.get().getLocale() : Locale.getDefault();

        String value = params.getValue(resourceKey);
        value = MessagesUtils.formatMessage(value, locale, parameters);

        logger.debug("Valeur retrouvée : " + value);
        return value;

    }

}
