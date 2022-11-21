package fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters;

import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.utils.DetailedException;
import org.apache.log4j.Logger;

import java.util.*;

public class Dictionary {

    private static final Logger logger = Logger.getLogger(Dictionary.class);
    /**
     * L'ensemble de tous les paramètres d'options :
     */
    final private Map<String, ArrayList<DictionaryValue>> dictionary;

    final private Parameters parameters;

    /**
     * Dictionnaire d'entrée qui contient toutes les options et leurs valeurs
     * associées. L'attribut dictionary est un Map avec comme clé le nom du
     * champ ("Score" par exemple) et comme valeur la liste des options
     * correspondantes (['xls.parser.options.score.active', 'Oui' et '1'] etc.)
     * sous forme de DictionaryValue
     */
    public Dictionary(Parameters parameters) throws DetailedException {
        this.dictionary = new HashMap<String, ArrayList<DictionaryValue>>();
        this.parameters = parameters;

        try {
            // Liste de toutes les options possibles pour ce champs
            ArrayList<DictionaryValue> fieldsList = new ArrayList<DictionaryValue>();
            fieldsList.clear();

            // Identifiant du paramètre (ex : xls.parser.options.score.active)
            String parameterId = "";

            // Valeur à traduire de l'option (ex : 'Oui', 'Non')
            String parameterValueToTranslate = "";

            // Valeur correspondante (ex : '1', '0')
            String parameterValue = "";

            // Paramètre contenant les valeurs d'une options :
            // * Son champ (ex : Score)
            // * Sa valeur à traduire (ex : 'Oui')
            // * Sa valeur réelle (ex : '1')
            DictionaryValue parameter = null;

            // On récupère la liste des identifiants de tous les champs
            // Par exemple xls.parser.options.score,
            // xls.parser.options.correction etc.
            List<String> fieldsIdList;
            try {
                fieldsIdList = this.getOptionsIdList();
            } catch (Exception e) {
                throw new DetailedException("La liste des champs à traduire n'a pas pu être récupérée : " + e);
            }

            // Nom du champs ('Score', 'Correction')
            String fieldName = "";

            // Pour chacun de ces identifiants, on va récupérer tous les
            // champs disponibles
            for (int i = 0; i < fieldsIdList.size(); i++) {

                try {
                    fieldName = this.parameters.getValue(fieldsIdList.get(i) + ".name");
                } catch (Exception e) {
                    logger.warn("Le champ " + fieldName + " n'existe pas dans le fichier de configuration.");
                }

                // On parcourt tous les paramètres qui commencent par
                // fieldsIdList.get(i)
                // (ex : xls.parser.options.score, xls.parser.options.correction
                // etc...)
                Iterator<String> parametersIterator = null;
                try {
                    parametersIterator = parameters.getKeysStartingWith(fieldsIdList.get(i)).iterator();
                } catch (Exception e) {
                    logger.debug("Les options du champs " + fieldName + " n'ont pas pu être récupérées");
                }
                while (parametersIterator.hasNext()) {

                    // Identifiant du paramètre en cours d'analyse
                    // ex : (xls.parser.options.score.)(active)
                    try {
                        parameterId = parametersIterator.next();
                    } catch (Exception e) {
                        logger.debug("L'identifiant du paramètre n'a pas pû être récupéré");
                    }

                    // Si l'identifiant est vide, on est sur un paramètre de nom
                    // de champ, on peut l'ignorer
                    // Si l'identifiant se termine par Value, c'est la valeur
                    // réelle, on peut l'ignorer
                    if (!parameterId.endsWith(".name") && !parameterId.endsWith(".value")) {

                        try {
                            // On récupère la valeur à traduire
                            parameterValueToTranslate = this.parameters.getValue(parameterId);
                            // On récupère la valeur réelle correspondante
                            parameterValue = this.parameters.getValue(parameterId + ".value");
                            // On instancie un nouveau paramètre avec les
                            // données
                            // recueillies
                            parameter = new DictionaryValue(parameterId, parameterValueToTranslate, parameterValue);

                        } catch (Exception e) {
                            logger.debug("Une erreur est survenue durant la récupération des informations relative au "
                                    + "paramètre " + parameterId);
                        }

                        // Si le paramètre est valide, on l'ajoute à la liste
                        // des paramètres qu'il faudra ajouter
                        if (parameter != null && isValid(parameter)) {
                            logger.debug(
                                    "Enregistrement de l'option " + parameterValueToTranslate + " (" + parameterValue
                                            + ") au champ " + fieldName);
                            fieldsList.add(parameter);
                        }
                    }
                    parameterId = "";
                    parameterValueToTranslate = "";
                    parameterValue = "";
                }

                // Si la liste n'est pas vide, cela signifie qu'il faut ajouter
                // une nouvelle entrée au dictionnaire
                // contenant la liste de toutes les options possibles pour le
                // champ fieldsIdList.get(i) (par exemple 'Score')
                if (!(fieldsList.isEmpty()) && !("".equals(fieldName))) {

                    logger.debug("Ajout de toutes les options pour le champs " + fieldName);
                    try {
                        this.getDictionary().put(fieldName, fieldsList);

                    } catch (Exception e) {
                        logger.debug("Les options du paramètre " + fieldName + " n'ont pas pu être initialisées");
                    }
                    fieldsList = new ArrayList<DictionaryValue>();
                }
            }
        } catch (Exception e) {
            throw new DetailedException(e).addMessage("Erreur dans l'initialisation du dictionnaire d'options.");
        }
    }

    /**
     * Vérifie que l'entrée que l'on souhaite ajouter au dictionnaire est
     * valide, c'est à dire que le nom et la valeur ne sont pas vide
     *
     * @return un booléen si oui ou non l'entrée est valide
     */
    private boolean isValid(DictionaryValue dictionaryValue) throws DetailedException {
        boolean isValid = true;
        if ("".equals(dictionaryValue.getValueToTranslate())) {
            throw new DetailedException("Le nom de l'option n'a pas été initialisé !");
        } else if ("".equals(dictionaryValue.getValue())) {
            throw new DetailedException("La valeur de l'option n'a pas été initialisée !");
        }
        return isValid;
    }

    /**
     * @param fieldName        : le nom du champ de l'option à rechercher (par exemple
     *                         'Score')
     * @param valueToTranslate : la valeur de l'option à rechercher (par exemple ('Oui')
     * @return : la valeur réelle correspondante à l'option valueToTranslate
     * (par exemple '1'), sinon la valeur donnée en entrée
     */
    public String getValueByValueToTranslate(String fieldName, String valueToTranslate) throws DetailedException {

        // On recherche les valeurs possible pour ce fieldName
        ArrayList<DictionaryValue> values = this.getDictionary().get(fieldName);

        // On les parcourt
        for (DictionaryValue value : values) {
            if (value.getValueToTranslate().equals(valueToTranslate)) {
                logger.debug(
                        "Traduction de la valeur du paramètre '" + fieldName + "' : '" + valueToTranslate + "' -> '"
                                + value.getValue() + "'");
                return value.getValue();
            }
        }

        logger.debug("Pas de traduction pour la valeur  '" + valueToTranslate + "' du paramètre '" + fieldName + "'");
        return valueToTranslate;

    }

    /**
     * @return la liste de tous les identifiants de champs d'options définis
     * dans le fichiers de configuration, c'est à dire toutes les clés
     * de paramètres qui commencent par Parameters.OPTIONS et qui
     * finissent par ".name".
     */
    private List<String> getOptionsIdList() {

        ArrayList<String> operatorsList = new ArrayList<String>();
        String operatorId;

        // logger.debug("Recherche de tous les paramètres débutant par "
        // + Parameters.OPTIONS + " dans le fichier de configuration.");
        Iterator<String> operatorsIterator = this.parameters.getKeysStartingWith(Parameters.OPTIONS).iterator();

        while (operatorsIterator.hasNext()) {
            operatorId = operatorsIterator.next();
            if (!(operatorsList.contains(operatorId)) && operatorId.endsWith("name")) {
                // On enlève ".name" à la fin de cet identifiant, pour ne garder
                // par exemple que xls.parser.options.correction
                operatorsList.add(operatorId.substring(0, operatorId.length() - 5));
            }
        }
        return operatorsList;
    }

    private Map<String, ArrayList<DictionaryValue>> getDictionary() {
        return dictionary;
    }

}
