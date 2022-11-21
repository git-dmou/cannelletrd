package fr.solunea.thaleia.plugins.cannelle.xls.screens.screenparser;

import fr.solunea.thaleia.plugins.cannelle.messages.LocalizedMessages;
import fr.solunea.thaleia.plugins.cannelle.utils.CellsRange;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.XlsUtils;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.*;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.utils.LogUtils;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;

import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation de la recherche des paramètres d'écran de contenu dans le
 * fichier Excel de Canelle. C'est cette classe qui va interpréter les cellules
 * pour initialiser les objets Paramètres qui vont décrire les écrans.
 */
public class CannelleExcelTemplate extends AbstractExcelTemplate {

    private static final Logger logger = Logger.getLogger(CannelleExcelTemplate.class);

    /**
     * L'endroit où la valeur du paramètre doit être cherché dans la cellule Xls
     * : dans la cellule, dans le commentaire, etc.
     */
    private enum CELL_VALUE_LOCATION {
        CONTENT, COMMENT
    }

    @Override
    public List<IScreenParameter> parseScreenParameters(CellsRange cells,  CannelleScreenParameters cannelleScreenParameters)
            throws DetailedException {

        List<IScreenParameter> result = new ArrayList<>();

        int lineNumberParameter = cells.getHeight() - 1;
        // On recherche tous les screenParameters, c'est à dire les
        // paramètres pour cet écran qui sont définis dans le fichier XLS.

        // Parcours des colonnes 0 et 3 seulement : ce sont celles qui
        // contiennent les identifiants des paramètres.
        ArrayList<Integer> columns = new ArrayList<>();
        columns.add(0);
        columns.add(3);

        // Parcours de toutes les lignes du bloc de cellules
        for (int line = 1; line <= lineNumberParameter; line++) {

            // On parcourt tous les paramètres dans la colonne
            // column, de la ligne 1 à la dernière ligne du modèle
            // c'est à dire la taille du bloc de cellule - 1

            for (Integer column : columns) {
                try {
                    // Récupération du titre du paramètre, sur la premiere cellule de la ligne
                    String parameterTitle = getValueFromCell(cells, column, line, getParameters(),
                            CELL_VALUE_LOCATION.CONTENT);

                    // On instancie un nouveau paramètre, portant ce titre,
                    // d'après les paramètres initialisés dans la conf
//                    IScreenParameter parameter = getScreenParameter(parameterTitle);
                    IScreenParameter parameter = cannelleScreenParameters.getScreenParameter(parameterTitle);

                    if (parameter == null) {
                        logger.debug("Le paramètre identifié '" + parameterTitle + "' dans le Xls n'est pas reconnu "
                                + "par le parser " + this.getClass().getName() + " : il est ignoré.");
                    } else {
                        // On recherche la valeur à la colonne :
                        // (templates.0.)params.X.value.offset.col
                        // sur la ligne :
                        // (templates.0.)params.X.value.offset.line
                        // par rapport à la cellule qui contient le nom du paramètre.
                        int colOffset = getStringAsInt(parameter.getProperty(AbstractScreenParameter
                                .VALUE_OFFSET_COL, ""), 0);
                        int lineOffset = getStringAsInt(parameter.getProperty(AbstractScreenParameter
                                .VALUE_OFFSET_LINE, ""), 0);

                        // La paramètre parse ces cellules pour retrouver sa valeur.
                        parameter.setValue(getValueFromCell(cells, column + colOffset, line + lineOffset,
                                getParameters(), CELL_VALUE_LOCATION.CONTENT));

                        // On traite les cas particuliers
                        addValues(parameter, cells, column, line);

                        // On vérifie la validité du paramètre
                        parameter.isValid();

                        logger.debug("La cellule a été interprétée comme le paramètre " + parameter);

                        result.add(parameter);
                    }

                } catch (Exception e) {
                    logger.warn("L'initialisation du paramètre de la ligne " + line + " et à la colonne " + column +
                            " ne s'est pas déroulée correctement " + e.toString() + "\n" + LogUtils.getStackTrace(e
                            .getStackTrace()));

                }
            }
        }

        addStaticParameters(result, cannelleScreenParameters);
        addOptionalParameters(result, cannelleScreenParameters);

        return result;
    }

    /**
     * Ajoute à ces paramètres les paramètres optionnels, avec leur valeur par défaut si elle est vide.
     */
    private void addOptionalParameters(List<IScreenParameter> destination, CannelleScreenParameters cannelleScreenParameters) {
        //On récupère la liste de tous les paramètres optionnels
        List<IScreenParameter> parameters = cannelleScreenParameters.getOptionalParameters();
        logger.debug("Paramètres optionnels récupérés : " + parameters);
        for (IScreenParameter parameter : parameters) {
            //Si les mécaniques de parsing n'ont pas pris en compte ces parametres, on définit une valeur par défaut
            if (parameter.getValue() == null) {
                parameter.setValue(parameter.getProperty(AbstractScreenParameter.OPTIONAL_DEFAULT_VALUE, ""));
                logger.debug("Prise en compte du paramètre optionnel avec sa valeur par défaut : " + parameter);
                destination.add(parameter);
            } else {
                logger.debug("Prise en compte du paramètre optionnel avec sa valeur existante : " + parameter);
                destination.add(parameter);
            }
        }
    }


    /**
     * On ajoute les paramètres statiques définis dans les propriétés, c'est à
     * dire les paramètres dont la valeur n'est pas dans le fichier Excel, mais
     * définie de manière statique dans le fichier de propriétés.
     */
    private void addStaticParameters(List<IScreenParameter> destination, CannelleScreenParameters cannelleScreenParameters) {

        // On parcourt tous les StaticParameters de ce template
        List<StaticParameter> parameters = cannelleScreenParameters.getStaticParameters();
        logger.debug("Ajout des " + parameters.size() + " paramètres statiques.");
        for (StaticParameter parameter : parameters) {
            // Dans le fichier .properties, on a défini un attribut statique
            // sous la forme :
            // templates.X.params.Y.type=fr.solunea.thaleia.plugins.bnpirb.xls.screens.parameters.StaticParameter
            // templates.X.params.Y.name=Type
            // templates.X.params.Y.value=Bilan
            // templates.X.params.Y.contentproperty=Type
            // Il faut donc interpréter la valeur, et l'enregistrer !
            parameter.setValue(parameter.getProperty("value", ""));

            logger.debug("Ajout du paramètre statique : " + parameter.getProperty("name", "") + " = " + parameter
                    .getValue());
            destination.add(parameter);
        }
    }

    /**
     * Traite les paramètres particuliers pour leurs associer d'autres valeurs.
     */
    private void addValues(IScreenParameter parameter, CellsRange cells, Integer column, int line) throws
            DetailedException {

        logger.info("coucou1");
        // Proposition IllustrationQruParameter
        if (IllustrationQruParameter.class.isAssignableFrom(parameter.getClass())) {
            logger.info("coucou2");
            int colOffset = getStringAsInt(parameter.getProperty(IllustrationQruParameter.CORRECT_OFFSET_COL, ""), 0);
            int lineOffset = getStringAsInt(parameter.getProperty(IllustrationQruParameter.CORRECT_OFFSET_LINE, ""), 0);

            // Récupération de la valeur du paramètre (on prend la valeur textuelle tel qu'elle).
            Cell cell = cells.getCell(column + colOffset, line + lineOffset);

            // Assignation du paramètre
            ((IllustrationQruParameter) parameter).setResponse(String.valueOf(cell));

            // La classe IllustrationQruParameter héritant de AssociationParameter il faut quitter la fonction
            // actuelle pour ne pas rentrer dans la condition de AssociationParameter.
            return;
        }

            // Proposition de QRU/QRM : est-elle juste ou fausse ?
        if (QruParameter.class.isAssignableFrom(parameter.getClass())) {
            // On recherche l'information si cette proposition
            // est juste ou fausse
            int colOffset = getStringAsInt(parameter.getProperty(QruParameter.CORRECT_OFFSET_COL, ""), 0);
            int lineOffset = getStringAsInt(parameter.getProperty(QruParameter.CORRECT_OFFSET_LINE, ""), 0);

            String isCorrect = getValueFromCell(cells, column + colOffset, line + lineOffset, getParameters(),
                    CELL_VALUE_LOCATION.CONTENT);

            // Pour traiter les coquetteries d'OpenOffice, on attribue un 1 à
            // TRUE et un 0 à FAUX
            if ("0".equals(isCorrect)) {
                isCorrect = getParameters().getValue(Parameters.EXCEL_BOOLEAN_FALSE_TEXT_VALUE);
            }
            if ("1".equals(isCorrect)) {
                isCorrect = getParameters().getValue(Parameters.EXCEL_BOOLEAN_TRUE_TEXT_VALUE);
            }

            // On vérifie que la valeur de cette cellule est bien une des deux
            // valeurs possibles.
            String[] possibleValues = new String[]{getParameters().getValue(Parameters.EXCEL_BOOLEAN_TRUE_TEXT_VALUE)
                    , getParameters().getValue(Parameters.EXCEL_BOOLEAN_FALSE_TEXT_VALUE)};
            try {
                ifNotEmptyExceptionIfNotExpected(isCorrect, possibleValues);
            } catch (DetailedException e) {
                // On prépare la String des valeurs possibles
                String possibleValuesString = "";
                for (String string : possibleValues) {
                    possibleValuesString = string + " - " + possibleValuesString;
                }
                if (possibleValuesString.endsWith(" - ")) {
                    possibleValuesString = possibleValuesString.substring(0, possibleValuesString.length() - " - "
                            .length());
                }
                // La valeur de la proposition
                String value = getValueFromCell(cells, column + getStringAsInt(parameter.getProperty(QruParameter
                        .VALUE_OFFSET_COL, ""), 0), line + getStringAsInt(parameter.getProperty(QruParameter
                        .VALUE_OFFSET_LINE, ""), 0), getParameters(), CELL_VALUE_LOCATION.CONTENT);
                // Le message localisé correspondant
                String message = LocalizedMessages.getMessage(LocalizedMessages.QRM_CORRECTION_NOT_VALID, new
                        Object[]{isCorrect, possibleValuesString, value});
                // On demande sa présentation
                ThaleiaSession.get().addError(message);
                // On arrête le traitement
                throw e.addMessage("La correction du paramètre de QRU/QRM " + parameter.getProperty(QruParameter
                        .VALUE_OFFSET_COL, "") + " n'est pas reconnue.");
            }

            // La valeur de isCorrect contient soit
            // Parameters.EXCEL_BOOLEAN_TRUE_TEXT_VALUE, soit
            // Parameters.EXCEL_BOOLEAN_FALSE_TEXT_VALUE
            ((QruParameter) parameter).setIsCorrect(getParameters().getValue(Parameters
                    .EXCEL_BOOLEAN_TRUE_TEXT_VALUE).equals(isCorrect));
        }

        // Catégorie de cette proposition
        if (ClassificationParameter.class.isAssignableFrom(parameter.getClass())) {
            // On recherche la catégorie de cette proposition
            int colOffset = getStringAsInt(parameter.getProperty(ClassificationParameter.CORRECT_OFFSET_COL, ""), 0);
            int lineOffset = getStringAsInt(parameter.getProperty(ClassificationParameter.CORRECT_OFFSET_LINE, ""), 0);

            String category = getValueFromCell(cells, column + colOffset, line + lineOffset, getParameters(),
                    CELL_VALUE_LOCATION.CONTENT);

            ((ClassificationParameter) parameter).setCategorie(category);
        }

        // Réponse de cette proposition
        if (AssociationParameter.class.isAssignableFrom(parameter.getClass())) {
            // On recherche la réponse de cette proposition
            int colOffset = getStringAsInt(parameter.getProperty(AssociationParameter.CORRECT_OFFSET_COL, ""), 0);
            int lineOffset = getStringAsInt(parameter.getProperty(AssociationParameter.CORRECT_OFFSET_LINE, ""), 0);

            String response = getValueFromCell(cells, column + colOffset, line + lineOffset, getParameters(),
                    CELL_VALUE_LOCATION.CONTENT);

            ((AssociationParameter) parameter).setResponse(response);
        }

        // Réponse de cette proposition
        if (AssociationMixedParameter.class.isAssignableFrom(parameter.getClass())) {
            // On recherche la réponse de cette proposition
            int colOffset = getStringAsInt(parameter.getProperty(AssociationMixedParameter.CORRECT_OFFSET_COL, ""), 0);
            int lineOffset = getStringAsInt(parameter.getProperty(AssociationMixedParameter.CORRECT_OFFSET_LINE, ""), 0);

            String response = getValueFromCell(cells, column + colOffset, line + lineOffset, getParameters(),
                    CELL_VALUE_LOCATION.CONTENT);

            ((AssociationMixedParameter) parameter).setResponse(response);
        }

        // Réponse de cette proposition
        if (AssociationURLorFileParameter.class.isAssignableFrom(parameter.getClass())) {
            // On recherche la réponse de cette proposition
            int colOffset = getStringAsInt(parameter.getProperty(AssociationURLorFileParameter.CORRECT_OFFSET_COL, ""), 0);
            int lineOffset = getStringAsInt(parameter.getProperty(AssociationURLorFileParameter.CORRECT_OFFSET_LINE, ""), 0);

            String response = getValueFromCell(cells, column + colOffset, line + lineOffset, getParameters(),
                    CELL_VALUE_LOCATION.CONTENT);

            ((AssociationURLorFileParameter) parameter).setResponse(response);
        }

        // Numéro d'ordre de cette proposition
        if (ArrangementParameter.class.isAssignableFrom(parameter.getClass())) {
            // On recherche le n° d'ordre de cette proposition
            int colOffset = getStringAsInt(parameter.getProperty(ArrangementParameter.CORRECT_OFFSET_COL, ""), 0);
            int lineOffset = getStringAsInt(parameter.getProperty(ArrangementParameter.CORRECT_OFFSET_LINE, ""), 0);

            String arrangement = getValueFromCell(cells, column + colOffset, line + lineOffset, getParameters(),
                    CELL_VALUE_LOCATION.CONTENT);

            ((ArrangementParameter) parameter).setArrangement(arrangement);
        }

        // Option à traduire (par exemple 'Oui' à traduire en '1'
        if (TranslateValueParameter.class.isAssignableFrom(parameter.getClass())) {

            String value = getDictionary().getValueByValueToTranslate(parameter.getProperty("name", ""), parameter
                    .getValue());

            parameter.setValue(value);
        }

        // Texte : doit-on ajouter une propriété dont la valeur est à rechercher
        // dans les commentaires ?
        if (TextParameter.class.isAssignableFrom(parameter.getClass())) {
            String commentContentProperty = parameter.getProperty(TextParameter.COMMENT_CONTENT_PROPERTY, "");

            if (!commentContentProperty.isEmpty()) {
                // On recherche la valeur
                // à la colonne :
                // (templates.0.)params.X.value.offset.col
                // sur la ligne :
                // (templates.0.)params.X.value.offset.line
                // par rapport à la cellule qui contient le nom du
                // paramètre.
                int colOffset = getStringAsInt(parameter.getProperty(AbstractScreenParameter.VALUE_OFFSET_COL, ""), 0);
                int lineOffset = getStringAsInt(parameter.getProperty(AbstractScreenParameter.VALUE_OFFSET_LINE, ""),
                        0);

                // On récupère le commentaire
                String value = getValueFromCell(cells, column + colOffset, line + lineOffset, getParameters(),
                        CELL_VALUE_LOCATION.COMMENT);

                // On stocke le nom et la valeur retrouvés
                ((TextParameter) parameter).setCommentPropertyName(commentContentProperty);
                ((TextParameter) parameter).setCommentPropertyValue(value);
            }
        }

        // Doit-on interpréter le formattage Xls ?
        if (parameter.parseTextFormatInHTML()) {

            // La position de la cellule qui contient la valeur
            int colOffset = getStringAsInt(parameter.getProperty(AbstractScreenParameter.VALUE_OFFSET_COL, ""), 0);
            int lineOffset = getStringAsInt(parameter.getProperty(AbstractScreenParameter.VALUE_OFFSET_LINE, ""), 0);

            // La cellule où récupérer le contenu
            Cell cell = cells.getCell(column + colOffset, line + lineOffset);

            // Les représentations des booléens
            String trueString = getParameters().getValue(Parameters.EXCEL_BOOLEAN_TRUE_TEXT_VALUE);
            String falseString = getParameters().getValue(Parameters.EXCEL_BOOLEAN_FALSE_TEXT_VALUE);

            // On formatte en Html
            String formattedValue = XlsUtils.getHtmlFormattedContent(cell, trueString, falseString);

            // On remplace la valeur
            parameter.setValue(formattedValue);
        }

    }

    /**
     * @throws Exception si la valeur n'est pas vide, alors on lève une exception si la valeur n'est égale à une des
     *                   valeurs possibles.
     */
    private void ifNotEmptyExceptionIfNotExpected(String value, String[] expected) throws DetailedException {
        if (value == null) {
            throw new DetailedException("La valeur à tester est nulle !");
        }
        // Si la valeur à tester est vide, alors pas de test
        if (value.isEmpty()) {
            return;
        }

        if (expected == null) {
            throw new DetailedException("Les valeurs possibles sont nulles !");
        }

        boolean valueIsExpected = false;
        for (Object expectedObject : expected) {
            if (value.equals(expectedObject)) {
                valueIsExpected = true;
                // Ce n'est pas la peine de terminer les tests
                return;
            }
        }

        if (!valueIsExpected) {
            throw new DetailedException("La valeur '" + value + "' ne fait pas partie des valeurs attendues.");
        }

    }

    private int getStringAsInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    /**
     * @param cells  : le bloc de cellule à parser
     * @param column : la colonne dans laquelle se trouve la cellule
     * @param line   : la ligne dans laquelle se trouve la cellule
     * @return : une chaîne de caractère représentant le contenu de la cellule
     */
    private String getValueFromCell(CellsRange cells, int column, int line, Parameters parameters,
                                    CELL_VALUE_LOCATION cellValue) throws DetailedException {
        if (cells.getCell(column, line) == null) {
            cells.createCell(column, line);
            return "";

        } else {
            try {
                Cell cell = cells.getCell(column, line);
                String trueString = parameters.getValue(Parameters.EXCEL_BOOLEAN_TRUE_TEXT_VALUE);
                String falseString = parameters.getValue(Parameters.EXCEL_BOOLEAN_FALSE_TEXT_VALUE);
                if (cellValue == CELL_VALUE_LOCATION.CONTENT) {
                    return XlsUtils.getStringValue(cell, trueString, falseString);
                } else if (cellValue == CELL_VALUE_LOCATION.COMMENT) {
                    return XlsUtils.getStringComment(cell);
                } else {
                    return "";
                }

            } catch (DetailedException e) {
                throw new DetailedException(e).addMessage("Impossible de retrouver la valeur du paramètre à la ligne "
                        + "" + "" + "" + "" + line + " et colonne " + column + " : " + e.toString());
            }

        }
    }

    /**
     * @param cells  : le bloc de cellule à parser
     * @param column : la colonne dans laquelle se trouve la cellule
     * @param line   : la ligne dans laquelle se trouve la cellule
     * @return : un booléen représentant le contenu de la cellule
     */
    public boolean getCellStringBoolean(CellsRange cells, int column, int line) throws DetailedException {
        boolean parameterCorrectValueBoolean = false;
        try {
            parameterCorrectValueBoolean = cells.getCell(column, line).getBooleanCellValue();
        } catch (DetailedException e) {
            throw new DetailedException("Impossible de retrouver la valeur du paramètre à la ligne " + line + " : " +
                    e.toString());
        }
        return parameterCorrectValueBoolean;
    }

}
