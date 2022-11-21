package fr.solunea.thaleia.plugins.cannelle.parsers;

import fr.solunea.thaleia.plugins.cannelle.service.ModuleGeneratorService;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.service.utils.FilenamesUtils;
import fr.solunea.thaleia.service.utils.MD5;
import fr.solunea.thaleia.utils.DetailedException;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.UUID;

public class IdentifierTranslator {

    /**
     * Le nombre de caractères maximum pour consituer l'identifiant (hors
     * caractères aléatoires)
     */
    private static final int MAX_CARACTERS = 230;

    /**
     * Le nombre de caractères dans la partie aléatoire d'un identifiant.
     */
    private static final int UNIQUE_LENGHT = 12;

    static final Logger logger = Logger.getLogger(ModuleGeneratorService.class);

    /**
     * Regarde les paramètres pour retrouver le mode de génération de l'identifiant, puis utilise les contentProperties
     * pour retrouver les valeurs nécessaires, et fabrique l'identifiant.
     * <p>
     * <p>
     * Si setIdInContentProperties est true, alors : si on doit générer un identifiant, il sera retourné ET enregistré
     * comme valeur de la contentProperty concernée.
     */
    public static String parseIdentifier(Map<String, String> contentProperties, Parameters parameters, String prefix,
                                         boolean setIdInContentProperties) throws DetailedException {

        // On recherche la valeur de la propriété de [prefix.]identifier
        // ( "templates.13.contentgenerator.identifier" ou
        // "xls.parser.module.identifier") : Valeurs possibles :
        // @@XX : le nom de la propriété à utiliser
        // @@XX##generated : le nom de la propriété XX à utiliser, suivi d'une
        // chaîne unique de caractères
        // @@XX##notnull : le nom de la propriété XX à utiliser, si vide alors
        // une chaîne unique de caractères
        // XX##generated : la valeur XX, suivi d'une chaîne unique de caractères
        String propertyName = getIdentifierPropertyName(parameters, prefix, false);

        // On recherche s'il faudra ajouter une chaîne aléatoire
        boolean generatedRequired = false;
        if (propertyName.lastIndexOf("##generated") != -1) {
            // On enlève le '##generated' qui se trouve à la fin de propertyName
            propertyName = propertyName.substring(0, propertyName.lastIndexOf("##generated"));
            generatedRequired = true;
        }

        // On recherche s'il faudra ajouter une chaîne aléatoire si la valeur de
        // la propriété est nulle
        boolean notNull = false;
        if (propertyName.lastIndexOf("##notnull") != -1) {
            // On enlève le '##notnull' qui se trouve à la fin de propertyName
            propertyName = propertyName.substring(0, propertyName.lastIndexOf("##notnull"));
            notNull = true;
        }

        String value = "";
        // Si propertyName commence par @@ c'est qu'on demande une valeur de
        // propriété
        if (propertyName.startsWith("@@")) {
            // On enlève le '@@' au début de propertyName
            propertyName = propertyName.substring("@@".length(), propertyName.length());
            // On recherche la valeur de cette propriété
            logger.debug("Recherche de la propriété '" + propertyName + "'");
            value = contentProperties.get(propertyName);

            if (value == null && !notNull) {
                throw new DetailedException("Le paramètre " + prefix + "identifier = '" + propertyName + "'" + " ne "
                        + "précise pas quel identifiant donner à l'écran. " + "Or la valeur ne doit pas être nulle.");
            } else if (value == null && notNull) {
                // Pas de valeur pour cette propriété, mais on a demandé à ce
                // qu'elle ne soit pas nulle : elle sera donc remplie plus tard.
                // On met une valeur vide pour éviter les NPE.
                value = "";
            }

        } else {
            // On demande juste la valeur.
            value = propertyName;
        }

        // Si demandé, on s'assure que ce n'est pas vide
        if (notNull && value.isEmpty()) {
            value = getGenerated();
        }

        // On normalise la valeur : nombre de caractères et accents.
        if (value != null && value.length() > MAX_CARACTERS) {
            value = value.substring(0, MAX_CARACTERS);
        }
        value = FilenamesUtils.getNormalizeString(value);

        // Si demandé, on ajoute une chaîne aléatoire
        if (generatedRequired) {
            // On ne met un underscore que si l'identifiant a déjà une valeur.
            if (value.isEmpty()) {
                value = getGenerated();
            } else {
                value = value + "_" + getGenerated();
            }

        }

        // Si demandé, on stocke la valeur dans la contentProperty qui contient l'identifiant
        if (setIdInContentProperties) {
            contentProperties.put(propertyName, value);
        }

        logger.debug(
                "Calcul de l'identifiant : '" + propertyName + "'='" + contentProperties.get(propertyName) + "' " + ""
                        + "" + "" + "traduite en '" + value + "'");

        return value;
    }

    /**
     * Retourne le nom de la propriété qui contient l'identifiant
     * Si cleaned = false, le nom est retourné sous forme : @@id##notnull
     * Si cleaned = true, le nom est retourné sous form : id
     */
    public static String getIdentifierPropertyName(Parameters parameters, String prefix, Boolean cleaned) throws
            DetailedException {

        String propertyName = parameters.getValue(prefix + "identifier");

        if (!cleaned) {
            return propertyName;
        }

        if (propertyName.lastIndexOf("##generated") != -1) {
            propertyName = propertyName.substring(0, propertyName.lastIndexOf("##generated"));
        }

        if (propertyName.lastIndexOf("##notnull") != -1) {
            propertyName = propertyName.substring(0, propertyName.lastIndexOf("##notnull"));
        }

        if (propertyName.startsWith("@@")) {
            propertyName = propertyName.substring("@@".length(), propertyName.length());
        }

        return propertyName;
    }

    private static String getGenerated() throws DetailedException {
        try {
            return MD5.hashString(UUID.randomUUID().toString()).substring(0, UNIQUE_LENGHT);
        } catch (Exception e) {
            throw new DetailedException(e).addMessage("Impossible de générer un identifiant aléatoire.");
        }
    }

}
