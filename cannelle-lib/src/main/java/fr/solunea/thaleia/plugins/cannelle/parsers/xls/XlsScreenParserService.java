package fr.solunea.thaleia.plugins.cannelle.parsers.xls;

import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.model.User;
import fr.solunea.thaleia.plugins.cannelle.contents.IContent;
import fr.solunea.thaleia.plugins.cannelle.contents.parsing.ExcelDefinition;
import fr.solunea.thaleia.plugins.cannelle.contents.parsing.ParsedObject;
import fr.solunea.thaleia.plugins.cannelle.messages.LocalizedMessages;
import fr.solunea.thaleia.plugins.cannelle.parsers.IScreenParserService;
import fr.solunea.thaleia.plugins.cannelle.utils.CellsRange;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.ScreenFactory;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.ScreenFactoryWithTranslation;
import fr.solunea.thaleia.utils.DetailedException;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.List;

/**
 * Retrouve un fichier Xls définissant des écrans, et parse ce Excel pour en
 * extraire les écrans.
 */
public class XlsScreenParserService extends AbstractXlsParserService implements IScreenParserService {

    private static final Logger logger = Logger.getLogger(XlsScreenParserService.class);

    /**
     * Les contenus de cellule qui sont considérés comme des séparateurs.
     */
    private List<String> separators;
    protected ScreenFactory screenFactory;

    private String origLanguage;
    private String targetLanguage;

    public XlsScreenParserService(Parameters parameters, ResourcesHandler resourcesHandler) {
        this(parameters, resourcesHandler, "",  "");
    }

    public XlsScreenParserService(Parameters parameters, ResourcesHandler resourcesHandler, String origLanguage, String targetLanguage) {
        super(parameters, resourcesHandler);
        this.origLanguage = origLanguage;
        this.targetLanguage = targetLanguage;
    }

    /**
     * @return le nom de la feuille du fichier qui doit être parsée.
     */
    @Override
    protected String getParsedSheetName(Parameters parameters) throws DetailedException {
        return parameters.getValue(Parameters.PARSED_SHEET_NAME_CONTENTS);
    }

    /**
     * @return Sur cette feuille, la liste des ensembles de cellules (CellRange)
     * qui décrivent un écran. Les séparateurs de ces blocs de cellules
     * sont soit une cellule "APPORT" soit une cellule "EXERCICE", de
     * largeur 16 colonnes. La châine est le numéro de la première ligne
     * de ce bloc de cellules (la première ligne étant au numéro 1).
     */
    private List<ExcelDefinition> parseBlocs(Sheet sheet, Parameters parameters) throws DetailedException {

        List<ExcelDefinition> result = new ArrayList<>();

        // Identification des cellules séparatrices
        List<Cell> separatorCells;
        try {
            separatorCells = getSeparatorCells(sheet, parameters);

        } catch (DetailedException e) {
            e.addMessage("Impossible d'identifier les séparateurs de blocs de cellule de la feuille XLS.");
            throw e;
        }

        logger.debug("Trouvé " + separatorCells.size() + " écrans décrits dans ce fichier XLS pour ce module !");

        if (separatorCells.isEmpty()) {
            throw new DetailedException("Aucun écran n'a été trouvé dans votre fichier Excel. "
                    + "Vérifier la conformité du fichier avec le fichier de configuration");

        }

        // Déduction des blocs de cellules
        for (int i = 0; i < separatorCells.size(); i++) {
            Cell separator = separatorCells.get(i);
            // Le bloc de cellules va du premier séparateur jusqu'au suivant OU
            // jusqu'à la dernière cellule de la feuille

            // La première ligne du bloc = celle du séparateur
            int thisSeparatorRow = separator.getRowIndex();

            // Quelle est la dernière ligne du bloc ?
            int lastRow;
            if (i == separatorCells.size() - 1) {
                // On est sur le dernier séparateur : le bloc va de lui jusqu'à
                // la fin de la feuille.
                lastRow = sheet.getLastRowNum();
            } else {
                // Le bloc de cellules va de ce séparateur jusqu'au suivant
                // (separatorCells.get(i + 1)).
                // La dernière ligne de ce bloc est juste avant la première du
                // bloc suivant (-1)
                lastRow = separatorCells.get(i + 1).getRowIndex() - 1;
            }

            // On identifie toutes les lignes du bloc : de la première à la
            // dernière
            List<Row> rows = new ArrayList<>();
            int row = thisSeparatorRow;
            while (row != lastRow) { // tant qu'on n'est pas
                // arrivés sur la ligne du
                // prochain séparateur, on
                // ajoute les lignes
                rows.add(sheet.getRow(row));
                // on passe à la ligne suivante
                row++;
            }
            // On ajoute aussi la dernière ligne du bloc
            rows.add(sheet.getRow(row));

            // On stocke le résultat
            CellsRange cellRange = new CellsRange(rows);
            // L'index à présenter l'utilisateur débute à 1, alors que celui
            // fourni par POI débute à 0.
            ExcelDefinition excelDefinition = new ExcelDefinition(cellRange, Integer.toString(thisSeparatorRow + 1));
            result.add(excelDefinition);
        }

        return result;
    }

    /**
     * @return la liste des cellules qui identifient un bloc de cellules
     * décrivant un écran.
     */
    private List<Cell> getSeparatorCells(Sheet sheet, Parameters parameters) throws DetailedException {
        List<Cell> result = new ArrayList<>();

        try {
            // La première ligne
            int firstRowNum = Integer.parseInt(parameters.getValue(Parameters.FIRST_TEMPLATE_POSITION + ".line"));
            // La dernière ligne
            int lastRowNum = sheet.getLastRowNum();
            for (int i = firstRowNum; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);

                if (row != null && row.getCell(0) != null) {
                    // La ligne n'est pas vide

                    // On recherche la première cellule de cette ligne
                    Cell firstCell = row.getCell(row.getFirstCellNum());
                    if (firstCell != null) {
                        // Il y a au moins une cellule sur cette ligne
                        if (firstCell.getCellType() == CellType.STRING) {
                            // Le contenu de la cellule est de type texte
                            String content = firstCell.getStringCellValue();
                            if (content != null) {
                                // La cellule n'est pas vide
                                if (isScreenSeparator(content, parameters)) {
                                    // C'est une cellule de séparation d'écran
                                    result.add(firstCell);
                                }
                            }
                        }
                    }
                }

            }
        } catch (DetailedException e) {
            e.addMessage("Impossible de déterminer les séparateurs de blocs de la feuille.");
            throw e;
        }

        return result;
    }

    /**
     * @return true si la cellule est une cellule définissant un nouvel écran.
     */
    private boolean isScreenSeparator(String content, Parameters parameters) throws DetailedException {
        if (content == null) {
            return false;
        }

        // Tous les séparateurs définis dans la conf
        List<String> separators;
        try {
            separators = getSeparators(parameters);
        } catch (DetailedException e) {
            e.addMessage("Impossible d'identifier une cellule comme séparateur.");
            throw e;
        }

        // Ce contenu fait-il partie des séparateurs définis dans la conf ?
        return separators.contains(content);
    }

    /**
     * @return les valeurs des cellules considérées par la conf comme des
     * séparateurs.
     */
    private List<String> getSeparators(Parameters parameters) throws DetailedException {

        if (separators == null) {

            separators = new ArrayList<>();

            // Le nombre de séparateurs définis dans la conf
            int number;
            try {
                number = parameters.getMaxIndexForKey(Parameters.XLS_PARSER_SEPARATORS);

                if (number >= 0) {
                    // On parcourt tous les paramètres
                    for (int i = 0; i <= number; i++) {
                        String paramName = Parameters.XLS_PARSER_SEPARATORS + "." + i;
                        String paramValue = parameters.getValue(paramName);
                        separators.add(paramValue);
                    }

                } else {
                    throw new DetailedException(
                            "Pas de séparateur trouvé pour le paramètre '" + Parameters.XLS_PARSER_SEPARATORS + "' !");
                }

            } catch (DetailedException e) {
                e.addMessage("Impossible d'obtenir les séparateurs à prendre en compte pour l'analyse de la feuille.");
                throw e;
            }
        }

        return separators;

    }

    /**
     * @return les écrans définis dans le fichier Excel.
     */
    @Override
    public List<ParsedObject<IContent>> getScreens(Locale locale, User user, String moduleID) throws DetailedException {
        List<ExcelDefinition> cellsRanges;

        cellsRanges = getExcelDefinitions();

        // On instancie la fabrique d'écrans
        screenFactory = getScreenFactory(getParameters(), getResourcesHandler(), origLanguage, targetLanguage);

        // Les écrans générés
        List<ParsedObject<IContent>> screens = new ArrayList<>();
        // Les identifiants des écrans générés
        List<String> screensId = new ArrayList<>();

        String screenId;
        // On parcourt les blocs de cellules
        // On vérifie que les identifiants d'exercices existent et sont bien
        // uniques
        for (ExcelDefinition excelDefinition : cellsRanges) {
            String excelLine = excelDefinition.getLocation();
            CellsRange cellsRange = excelDefinition.getCellsRange();

           screenTraitment: {
                // On fabrique l'écran correspondant à ce bloc de cellules
                IContent screen;
               parseScreenAndScreenCreation: {
                   try {
                       screen = getScreen(locale, user, screenFactory, cellsRange);
                   } catch (DetailedException e) {
                       // Le message localisé correspondant
                       String message = getLocalizedMessage(LocalizedMessages.SCREEN_GENERATION_ERROR, excelLine);

                       // On demande sa présentation
                       notifiySession(message);

                       throw new DetailedException(e).addMessage(
                               "Impossible de générer l'écran défini à la ligne " + excelLine);
                   }
               }

               screenId = screen.getIdentifier();

                if (screenId == null || screenId.equals("")) {
                    // Le message localisé correspondant
                    String message = getLocalizedMessage(LocalizedMessages.NULL_SCREEN_ID_ERROR, screenId,
                            excelLine);

                    // On demande sa présentation
                    notifiySession(message);

                    throw new DetailedException("Attention, l'un des écrans ne possède pas d'identifiant. "
                            + "Veuillez vous assurer que tous les exercices ont un identifiant unique.");
                } else {
                    if(screenId.equals(moduleID)) {
                        String message = getLocalizedMessage(LocalizedMessages.MODULE_ID_AND_SCREEN_ID_ERROR,
                                screenId, excelLine);

                        // On demande sa présentation
                        notifiySession(message);

                        throw new DetailedException("Attention, un écran d'activité porte le même identifiant que le module."
                                + "Veuillez choisir un identifiant unique pour chaque écrans s'il vous plaît");
                    } else if (screensId.contains(screenId)) {
                        String message = getLocalizedMessage(LocalizedMessages.DUPLICATE_SCREEN_ID_ERROR,
                                screenId, excelLine);

                        // On demande sa présentation
                        notifiySession(message);

                        throw new DetailedException("Attention, deux exercices portent le même identifiant. "
                                + "Veuillez choisir un identifiant unique pour chaque écrans s'il vous plaît");
                    } else {
                        screensId.add(screenId);
                    }

                    ParsedObject<IContent> parsedObject = new ParsedObject<>(screen, excelLine);
                    screens.add(parsedObject);
                }
            }
        }

        logger.debug("On retourne les écrans suivants : " + screens.toString());
        return screens;
    }

    public IContent getScreen(Locale locale, User user, ScreenFactory screenFactory, CellsRange cellsRange) throws DetailedException {
        return screenFactory.parseScreen(cellsRange, getResourcesHandler(), locale, user);
    }

    public List<ExcelDefinition> getExcelDefinitions() throws DetailedException {
        List<ExcelDefinition> cellsRanges;

        // Ouverture de la feuille
        Sheet sheet = openSheetToParse();

        if (sheet == null) {
            String sheetName = this.getParsedSheetName(getParameters());

            // Le message localisé correspondant
            String message = getLocalizedMessage(LocalizedMessages.SHEET_NOT_FOUND_ERROR, sheetName);

            // On demande sa présentation
            notifiySession(message);

            throw new DetailedException("La feuille '" + sheetName + "' n'a pas été trouvée dans le fichier Excel.");
        }

        // On identifie les blocs de cellules décrivant un écran
        cellsRanges = parseBlocs(sheet, getParameters());

        return cellsRanges;
    }

    public ScreenFactory getScreenFactory(Parameters parameters, ResourcesHandler resourcesHandler) throws DetailedException {
        ScreenFactory screenFactory = new ScreenFactory(parameters, resourcesHandler);
        return screenFactory;
    }
    public ScreenFactory getScreenFactory(Parameters parameters, ResourcesHandler resourcesHandler, String origLanguage, String targetLanguage) throws DetailedException {

        ScreenFactory screenFactory;
        if (targetLanguage.equals("")) {
            screenFactory = new ScreenFactory(parameters, resourcesHandler);
        } else {
            screenFactory = new ScreenFactoryWithTranslation(parameters, resourcesHandler, origLanguage, targetLanguage);
        }
        return screenFactory;
    }

    protected   String getLocalizedMessage(String resourceKey, Object... parameters) {
        String message = LocalizedMessages.getMessage(resourceKey, parameters);
        return message;
    }

//    protected static void notifiySession(String message) {
//        ThaleiaSession.get().addError(message);
//    }
}
