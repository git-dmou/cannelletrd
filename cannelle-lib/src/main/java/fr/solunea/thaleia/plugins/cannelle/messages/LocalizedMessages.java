package fr.solunea.thaleia.plugins.cannelle.messages;

import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import fr.solunea.thaleia.webapp.utils.MessagesUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Session;
import org.apache.wicket.resource.loader.ClassStringResourceLoader;

import java.util.Arrays;
import java.util.Locale;

public class LocalizedMessages {

    private final static Logger logger = Logger.getLogger(LocalizedMessages.class);

    /**
     * Erreur levée lors de la fonction de remplacement d'une valeur dans un
     * écran, lors de sa génération depuis un modèle.
     */
    public static final String REPLACE_FUNCTION_ON_SCREEN_ERROR = "replace.function.on.screen.error";
    /**
     * Erreur levée durant la recherche d'un paramètre, qui n'est pas trouvé.
     */
    public static final String PARAMETER_NOT_FOUND_ERROR = "parameter.not.found.error";

    /**
     * Erreur levée si un fichier n'est pas trouvé dans l'archive envoyée.
     */
    public static final String MEDIA_FILE_NOT_FOUND_ERROR = "media.not.found.error";

    /**
     * Erreur levée si l'identifiant d'écran existe déjà.
     */
    public static final String DUPLICATE_SCREEN_ID_ERROR = "duplicate.screen.id";

    /**
     * Erreur levée si l'identifiant d'écran n'est pas défini.
     */
    public static final String NULL_SCREEN_ID_ERROR = "null.screen.id";

    /**
     * Erreur levée si l'écran ne peut pas être enregistré dans les écrans
     * Thaleia.
     */
    public static final String STORE_SCREEN_ERROR = "store.screen.error";

    /**
     * Erreur levée si l'enregistrement des propriétés de cet écran a échoué.
     */
    public static final String STORE_SCREEN_PROPERTIES_ERROR = "store.screen.properties.error";

    /**
     * Erreur levée si erreur de génération de l'écran.
     */
    public static final String SCREEN_GENERATION_ERROR = "screen.generation.error";

    /**
     * Erreur levée si aucun fichier Excel n'est trouvé dans l'archive.
     */
    public static final String NO_XLS_ERROR = "no.xls.error";

    /**
     * Erreur levée si plusieurs fichiers Excel ont été trouvés dans l'archive.
     */
    public static final String MULTIPLE_XLS_ERROR = "mulitple.xls.error";

    /**
     * Erreur levée si une feuille nécessaire au traitement n'a pas été trouvée
     * dans le fichier Excel.
     */
    public static final String SHEET_NOT_FOUND_ERROR = "sheet.not.found.error";

    /**
     * Erreur levée si une ressource porte le même nom que le fichier XLS
     */
    public static final String MULTIPLE_SHEET_NAME_ERROR = "multiple.sheet.name.error";

    /**
     * Erreur levée si un fichier média doit être placé dans un écran, mais n'a
     * pas été fourni dans l'archive.
     */
    public static final String FILE_NOT_FOUND_ERROR = "file.not.found.error";

    /**
     * Erreur levée une propriété utilisée pour les remplacements dans un modèle
     * d'écran n'a pas été définie, ce qui ne permet pas d'effectuer le
     * remplacement.
     */
    public static final String REPLACE_VALUE_IN_SCREEN_ERROR = "replace.value.undefined";

    /**
     * Erreur levée si un média est décrit par une URL externe, mais qui n'est
     * pas identifée comme telle lors du traitement de l'écran : elle est donc
     * traitée comme un nom de média, mais ce média n'existe pas.
     */
    public static final String URL_ERROR = "url.not.recognized";

    /**
     * Erreur levée si une question de QRU/QRM est associée à une correction qui
     * n'est pas reconnue parmi les corrections possibles. Par exemple, on
     * attend "Vrai" ou "Faux", et on a la correction "Correct".
     */
    public static final String QRM_CORRECTION_NOT_VALID = "qrm.correction.not.valid";

    /**
     * Erreur levée si un fichier est associé à une ressource du module, mais
     * n'a pas été trouvé dans l'archive.
     */
    public static final String RESOURCE_FILE_NOT_FOUND = "resource.file.not.found";
    public static final String FILENAME_ERROR = "filename.error";
    public static final String MODULE_PROPERTIES_ERROR = "module.properties.error" ;

    /**
     * Erreur levée quand un écran d'activité et le module ont le même identifiant.
     */
    public static final String MODULE_ID_AND_SCREEN_ID_ERROR ="duplicate.screenAndModule.id";

    /**
     * Recherche la valeur de la chaîne localisée dans le fichier
     * LocalizedMessages_XX.properties (où XX est la locale de la session).
     *
     * @param resourceKey le code du message à rechercher
     * @param parameters  si besoin, des paramètres pour appliquer des interprétations de valeurs dans la chaîne de
     *                    caractères ${0}, ${1}...
     * @return le message d'erreur localisé
     */
    public static String getMessage(String resourceKey, Object... parameters) {

        ClassStringResourceLoader loader = new ClassStringResourceLoader(LocalizedMessages.class);

        Locale locale = Session.exists() ? ThaleiaSession.get().getLocale() : Locale.getDefault();

        logger.debug("Recherche du message '" + resourceKey + "' avec les paramètres : " + Arrays.toString
                (parameters) + " "
                + "pour la " +
                "locale " + locale.toString() + " et le style " + ThaleiaSession.get().getStyle());

        String value;
        String defaultValue = "";

        value = loader.loadStringResource(LocalizedMessages.class, resourceKey, locale, ThaleiaSession.get().getStyle(), "");
        if (value == null) {
            value = defaultValue;
        }

        value = MessagesUtils.formatMessage(value, locale, parameters);

        logger.debug("Valeur retrouvée : " + value);
        return value;

    }

}
