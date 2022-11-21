package fr.solunea.thaleia.plugins.cannelle.parsers.xls;

import fr.solunea.thaleia.plugins.cannelle.messages.LocalizedMessages;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.utils.XlsUtils;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.functions.*;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Analyse un fichier de description des remplacements, pour en extraire la
 * définition des fonctions de remplacement à effectuer.
 * 
 */
public class XlsScreenModelParserService extends AbstractXlsParserService {

	private static final Logger logger = Logger.getLogger(XlsScreenModelParserService.class);
	public XlsScreenModelParserService(Parameters parameters, ResourcesHandler resourcesHandler) {
		super(parameters, resourcesHandler);
	}

	/**
	 * @return le nom de la feuille du fichier qui doit être parsée.
	 * @throws DetailedException
	 */
	@Override
	protected String getParsedSheetName(Parameters parameters) throws DetailedException {
		return parameters.getValue(Parameters.PARSED_SHEET_MODEL_NAME);
	}

	/**
	 * Parcours le fichier Excel de modèle contenant les propriétés de
	 * remplacement du a7 et retourne la liste des fonctions à appliquer sous
	 * forme d'objet Function
	 * 
	 * @param xlsModel
	 * @return
	 * @throws DetailedException
	 */
	public List<AbstractFunction> parseFunctions(File xlsModel, File a7File) throws DetailedException {

		// Ouverture de la feuille
		Sheet sheet = openSheetToParse(xlsModel);

		if (sheet == null) {
			String sheetName = getParsedSheetName(getParameters());

			// Le message localisé correspondant
			String message = LocalizedMessages.getMessage(LocalizedMessages.SHEET_NOT_FOUND_ERROR,
					new Object[] { sheetName });

			// On demande sa présentation
			ThaleiaSession.get().addError(message);

			throw new DetailedException("La feuille '" + sheetName + "' n'a pas été trouvée dans le fichier Excel.");
		}

		List<AbstractFunction> result = new ArrayList<AbstractFunction>();
		int lineNumberParameter;

		// On parcoure l'intégralité de la feuille
		lineNumberParameter = sheet.getLastRowNum();

		// On parcourt tous les paramètres dans la colonne
		// column, de la ligne 1 à la dernière ligne

		String lineIdentifier = "";

		int fisrtLine = new Integer(getParameters().getValue(Parameters.FUNCTION_FIRSTLINE)).intValue();

		logger.debug("Recherche des fonctions de remplacement définies dans la feuille '"
				+ getParsedSheetName(getParameters()) + "' du fichier " + xlsModel.getAbsolutePath() + " des lignes "
				+ fisrtLine + " à " + lineNumberParameter);

		// Parcours de toutes les lignes du bloc de cellules
		for (int line = fisrtLine; line <= lineNumberParameter; line++) {
			logger.debug("Analyse de la ligne " + line + ".");

			// Si le contenu de la première colonne n'est pas vide
			if (!("".equals(getCellString(sheet, line, 0)))) {

				// Si le contenu de la première colonne correspond à une
				// fonction
				lineIdentifier = getCellString(sheet, line, 0);

				// Replace
				if (getParameters().getValue(Parameters.FUNCTION_REPLACE + ".name").equals(lineIdentifier)) {

					String toReplace = getCellString(sheet, line,
							Integer.parseInt(getParameters().getValue(Parameters.FUNCTION_REPLACE + ".toReplace")));
					String replaceValue = getCellString(sheet, line,
							Integer.parseInt(getParameters().getValue(Parameters.FUNCTION_REPLACE + ".replaceValue")));

					// Si la nouvelle valeur est vide, on ne procède pas au
					// remplacement.
					boolean replaceValueIfEmpty = false;

					logger.debug(
							"Récupéré la fonction de remplacement de '" + toReplace + "' par '" + replaceValue + "'.");
					result.add(new ReplaceFunction(toReplace, replaceValue, replaceValueIfEmpty));
				}

				// ReplaceIf
				else if (getParameters().getValue(Parameters.FUNCTION_REPLACE_IF + ".name").equals(lineIdentifier)) {

					String condition = getCellString(sheet, line,
							Integer.parseInt(getParameters().getValue(Parameters.FUNCTION_REPLACE_IF + ".condition")));
					String value1 = getCellString(sheet, line,
							Integer.parseInt(getParameters().getValue(Parameters.FUNCTION_REPLACE_IF + ".value1")));
					String value2 = getCellString(sheet, line,
							Integer.parseInt(getParameters().getValue(Parameters.FUNCTION_REPLACE_IF + ".value2")));
					String steps = getCellString(sheet, line,
							Integer.parseInt(getParameters().getValue(Parameters.FUNCTION_REPLACE_IF + ".steps")));
					String zones = getCellString(sheet, line,
							Integer.parseInt(getParameters().getValue(Parameters.FUNCTION_REPLACE_IF + ".zones")));
					String lines = getCellString(sheet, line,
							Integer.parseInt(getParameters().getValue(Parameters.FUNCTION_REPLACE_IF + ".lines")));
					String toReplace = getCellString(sheet, line,
							Integer.parseInt(getParameters().getValue(Parameters.FUNCTION_REPLACE_IF + ".toReplace")));
					String replaceValue = getCellString(sheet, line, Integer
							.parseInt(getParameters().getValue(Parameters.FUNCTION_REPLACE_IF + ".replaceValue")));

					logger.debug("Récupéré la fonction de remplacementSi : condition='" + condition + "' value1='"
							+ value1 + "' value2=" + value2 + " steps=" + steps + " zones=" + zones + " lines=" + lines
							+ " toReplace=" + toReplace + " replaceValue=" + replaceValue);

					if ("".equals(value1) && "".equals(value2)) {
						throw new DetailedException("Au moins une valeur doit être analysée.");
					} else {
						result.add(new ReplaceIfFunction(condition, value1, value2, steps, zones, lines, toReplace,
								replaceValue));
					}
				}

				// ReplaceIfCorrection
				else if (getParameters().getValue(Parameters.FUNCTION_REPLACE_IF_CORRECTION + ".name")
						.equals(lineIdentifier)) {

					String condition = getCellString(sheet, line, Integer.parseInt(
							getParameters().getValue(Parameters.FUNCTION_REPLACE_IF_CORRECTION + ".condition")));
					String value1 = getCellString(sheet, line, Integer
							.parseInt(getParameters().getValue(Parameters.FUNCTION_REPLACE_IF_CORRECTION + ".value1")));
					String value2 = getCellString(sheet, line, Integer
							.parseInt(getParameters().getValue(Parameters.FUNCTION_REPLACE_IF_CORRECTION + ".value2")));
					String steps = getCellString(sheet, line, Integer
							.parseInt(getParameters().getValue(Parameters.FUNCTION_REPLACE_IF_CORRECTION + ".steps")));
					String zones = getCellString(sheet, line, Integer
							.parseInt(getParameters().getValue(Parameters.FUNCTION_REPLACE_IF_CORRECTION + ".zones")));
					String lines = getCellString(sheet, line, Integer
							.parseInt(getParameters().getValue(Parameters.FUNCTION_REPLACE_IF_CORRECTION + ".lines")));
					String toReplace = getCellString(sheet, line, Integer.parseInt(
							getParameters().getValue(Parameters.FUNCTION_REPLACE_IF_CORRECTION + ".toReplace")));
					String replaceValue = getCellString(sheet, line, Integer.parseInt(
							getParameters().getValue(Parameters.FUNCTION_REPLACE_IF_CORRECTION + ".replaceValue")));

					logger.debug("Récupéré la fonction de remplacementSiCorrection : condition='" + condition
							+ "' value1='" + value1 + "' value2='" + value2 + "' steps='" + steps + "' zones='" + zones
							+ "' lines='" + lines + "' toReplace='" + toReplace + "' replaceValue='" + replaceValue
							+ "'");

					if ("".equals(value1) && "".equals(value2)) {
						throw new DetailedException("Au moins une valeur doit être analysée.");
					} else {
						result.add(new ReplaceIfCorrectionFunction(condition, value1, value2, steps, zones, lines,
								toReplace, replaceValue));
					}
				}

				// ReplaceAlways
				else if (getParameters().getValue(Parameters.FUNCTION_REPLACE_ALWAYS + ".name")
						.equals(lineIdentifier)) {

					String steps = getCellString(sheet, line,
							Integer.parseInt(getParameters().getValue(Parameters.FUNCTION_REPLACE_ALWAYS + ".steps")));
					String zones = getCellString(sheet, line,
							Integer.parseInt(getParameters().getValue(Parameters.FUNCTION_REPLACE_ALWAYS + ".zones")));
					String lines = getCellString(sheet, line,
							Integer.parseInt(getParameters().getValue(Parameters.FUNCTION_REPLACE_ALWAYS + ".lines")));
					String toReplace = getCellString(sheet, line, Integer
							.parseInt(getParameters().getValue(Parameters.FUNCTION_REPLACE_ALWAYS + ".toReplace")));
					String replaceValue = getCellString(sheet, line, Integer
							.parseInt(getParameters().getValue(Parameters.FUNCTION_REPLACE_IF + ".replaceValue")));

					logger.debug("Récupéré la fonction de remplacementToujours :" + " steps=" + steps + " zones="
							+ zones + " lines=" + lines + " toReplace=" + toReplace + " replaceValue=" + replaceValue);

					result.add(new ReplaceAlwaysFunction(steps, zones, lines, toReplace, replaceValue));
				}
				// ReplacePair
				else if (getParameters().getValue(Parameters.FUNCTION_REPLACE_PAIR + ".name").equals(lineIdentifier)) {

					String pairName = getCellString(sheet, line,
							Integer.parseInt(getParameters().getValue(Parameters.FUNCTION_REPLACE_PAIR + ".pairName")));
					String replaceValueLeft = getCellString(sheet, line, Integer.parseInt(
							getParameters().getValue(Parameters.FUNCTION_REPLACE_PAIR + ".replaceValueLeft")));

					String replaceValueRight = getCellString(sheet, line, Integer.parseInt(
							getParameters().getValue(Parameters.FUNCTION_REPLACE_PAIR + ".replaceValueRight")));

					logger.debug("Récupéré la fonction de remplacement de paire pour '" + pairName + "' par gauche='"
							+ replaceValueLeft + "' et droite='" + replaceValueRight + "'.");
					result.add(new ReplacePairFunction(pairName, replaceValueLeft, replaceValueRight, a7File));
				}
				// ReplaceMixedPair
				else if (getParameters().getValue(Parameters.FUNCTION_REPLACE_MIXED_PAIR + ".name").equals(lineIdentifier)) {

					String pairName = getCellString(sheet, line,
							Integer.parseInt(getParameters().getValue(Parameters.FUNCTION_REPLACE_MIXED_PAIR + ".pairName")));
					String replaceValueLeft = getCellString(sheet, line, Integer.parseInt(
							getParameters().getValue(Parameters.FUNCTION_REPLACE_MIXED_PAIR + ".replaceValueLeft")));

					String replaceValueRight = getCellString(sheet, line, Integer.parseInt(
							getParameters().getValue(Parameters.FUNCTION_REPLACE_MIXED_PAIR + ".replaceValueRight")));

					logger.debug("Récupéré la fonction de remplacement de paire pour '" + pairName + "' par gauche='"
							+ replaceValueLeft + "' et droite='" + replaceValueRight + "'.");
					result.add(new ReplaceMixedPairFunction(pairName, replaceValueLeft, replaceValueRight, a7File));
				}

				// replaceUrlOrFilePair
				else if (getParameters().getValue(Parameters.FUNCTION_REPLACE_URLORFILE_PAIR + ".name").equals(lineIdentifier)) {

					String pairName = getCellString(sheet, line,
							Integer.parseInt(getParameters().getValue(Parameters.FUNCTION_REPLACE_URLORFILE_PAIR + ".pairName")));
					String replaceValueLeft = getCellString(sheet, line, Integer.parseInt(
							getParameters().getValue(Parameters.FUNCTION_REPLACE_URLORFILE_PAIR + ".replaceValueLeft")));

					String replaceValueRight = getCellString(sheet, line, Integer.parseInt(
							getParameters().getValue(Parameters.FUNCTION_REPLACE_URLORFILE_PAIR + ".replaceValueRight")));

					logger.debug("Récupéré la fonction de remplacement de paire pour '" + pairName + "' par gauche='"
							+ replaceValueLeft + "' et droite='" + replaceValueRight + "'.");
					result.add(new ReplaceUrlOrFilePairFunction(pairName, replaceValueLeft, replaceValueRight, a7File));
				}

				// Couples proposition / catégorie (version A7)
				else if (getParameters().getValue(Parameters.FUNCTION_COUPLES + ".name").equals(lineIdentifier)) {

					String coupleSeparator = getParameters().getValue(Parameters.FUNCTION_COUPLES + ".coupleSeparator");
					String couplesSeparator = getParameters()
							.getValue(Parameters.FUNCTION_COUPLES + ".couplesSeparator");

					// La colonne où chercher les noms de propositions
					int propositionsNamesColumn = Integer
							.parseInt(getParameters().getValue(Parameters.FUNCTION_COUPLES + ".propositionsNames.col"));
					// Le nombre de lignes sous le nom de la fonction où
					// commence la liste des noms de propositions.
					int propositionsNamesLineOffset = Integer.parseInt(
							getParameters().getValue(Parameters.FUNCTION_COUPLES + ".propositionsNames.lineoffest"));
					// Le nombre de noms de proposition
					int propositionsNames = Integer.parseInt(
							getParameters().getValue(Parameters.FUNCTION_COUPLES + ".propositionsNames.number"));
					// La colonne où chercher les index de propositions
					int propositionsIndexes = Integer.parseInt(
							getParameters().getValue(Parameters.FUNCTION_COUPLES + ".propositionsIndexes.col"));
					Map<String, String> propositions = new HashMap<String, String>();
					for (int i = line + propositionsNamesLineOffset; i < line + propositionsNamesLineOffset
							+ propositionsNames; i++) {
						String propositionName = getCellString(sheet, i, propositionsNamesColumn);
						String propositionIndex = getCellString(sheet, i, propositionsIndexes);
						propositions.put(propositionName, propositionIndex);
					}

					// La colonne où chercher les noms de catégories
					int categoriesNamesColumn = Integer
							.parseInt(getParameters().getValue(Parameters.FUNCTION_COUPLES + ".categoriesNames.col"));
					// Le nombre de lignes sous le nom de la fonction où
					// commence la liste des noms de catégories.
					int categoriesNamesLineOffset = Integer.parseInt(
							getParameters().getValue(Parameters.FUNCTION_COUPLES + ".categoriesNames.lineoffest"));
					// Le nombre de noms de catégories
					int categoriesNames = Integer.parseInt(
							getParameters().getValue(Parameters.FUNCTION_COUPLES + ".categoriesNames.number"));
					// La colonne où chercher les index de catégories
					int categoriesIndexes = Integer
							.parseInt(getParameters().getValue(Parameters.FUNCTION_COUPLES + ".categoriesIndexes.col"));
					Map<String, String> categories = new HashMap<String, String>();
					for (int i = line + categoriesNamesLineOffset; i < line + categoriesNamesLineOffset
							+ categoriesNames; i++) {
						String categoryName = getCellString(sheet, i, categoriesNamesColumn);
						String categoryIndex = getCellString(sheet, i, categoriesIndexes);
						categories.put(categoryName, categoryIndex);
					}

					// La chaîne de caractères à remplacer dans le modèle pour y
					// placer le tableau des couples
					String replace = getParameters().getValue(Parameters.FUNCTION_COUPLES + ".replace");

					result.add(
							new CouplesFunction(coupleSeparator, couplesSeparator, propositions, categories, replace));
				}

				// Correction de la classification HTML
				else if (getParameters().getValue(Parameters.CLASSIFICATION_COUPLES + ".name").equals(lineIdentifier)) {

					// La colonne où chercher les noms de propositions
					int propositionsNamesColumn = Integer.parseInt(
							getParameters().getValue(Parameters.CLASSIFICATION_COUPLES + ".propositionsNames.col"));
					// Le nombre de lignes sous le nom de la fonction où
					// commence la liste des noms de propositions.
					int propositionsNamesLineOffset = Integer.parseInt(getParameters()
							.getValue(Parameters.CLASSIFICATION_COUPLES + ".propositionsNames.lineoffest"));
					// Le nombre de noms de proposition
					int propositionsNames = Integer.parseInt(
							getParameters().getValue(Parameters.CLASSIFICATION_COUPLES + ".propositionsNames.number"));
					// La colonne où chercher les corrections des propositions
					int propositionsCorrections = Integer.parseInt(getParameters()
							.getValue(Parameters.CLASSIFICATION_COUPLES + ".propositionsCorrections.col"));
					Map<String, String> propositions = new HashMap<String, String>();
					for (int i = line + propositionsNamesLineOffset; i < line + propositionsNamesLineOffset
							+ propositionsNames; i++) {
						String propositionName = getCellString(sheet, i, propositionsNamesColumn);
						String propositionCorrection = getCellString(sheet, i, propositionsCorrections);
						propositions.put(propositionName, propositionCorrection);
					}

					// La colonne où chercher les noms de catégories
					int categoriesNamesColumn = Integer.parseInt(
							getParameters().getValue(Parameters.CLASSIFICATION_COUPLES + ".categoriesNames.col"));
					// Le nombre de lignes sous le nom de la fonction où
					// commence la liste des noms de catégories.
					int categoriesNamesLineOffset = Integer.parseInt(getParameters()
							.getValue(Parameters.CLASSIFICATION_COUPLES + ".categoriesNames.lineoffest"));
					// Le nombre de noms de catégories
					int categoriesNames = Integer.parseInt(
							getParameters().getValue(Parameters.CLASSIFICATION_COUPLES + ".categoriesNames.number"));
					// La colonne où chercher les ids de catégories
					int categoriesIds = Integer.parseInt(
							getParameters().getValue(Parameters.CLASSIFICATION_COUPLES + ".categoriesIds.col"));
					Map<String, String> categories = new HashMap<String, String>();
					for (int i = line + categoriesNamesLineOffset; i < line + categoriesNamesLineOffset
							+ categoriesNames; i++) {
						String categoryName = getCellString(sheet, i, categoriesNamesColumn);
						String categoryId = getCellString(sheet, i, categoriesIds);
						categories.put(categoryName, categoryId);
					}

					result.add(new ClassificationCorrectionFunction(propositions, categories));
				}

				else {
					logger.debug("Pas de fonction de remplacement.");
				}
			}
		}
		return result;
	}

	/**
	 * @param sheet
	 * @param line
	 *            la ligne dans laquelle se trouve la cellule
	 * @param column
	 *            la colonne dans laquelle se trouve la cellule
	 * @return une chaîne de caractère représentant le contenu de la cellule
	 * @throws DetailedException
	 */
	private String getCellString(Sheet sheet, int line, int column) throws DetailedException {
		try {
			Cell cell;
			try {
				cell = sheet.getRow(line).getCell(column);

			} catch (NullPointerException e) {
				logger.debug("Impossible de retrouver la valeur du paramètre à la ligne " + line + " et colonne "
						+ column + " : " + e.toString() + " : la cellule est supposée vide.");
				return "";
			}

			return XlsUtils.getStringValue(cell, getParameters().getValue(Parameters.EXCEL_BOOLEAN_TRUE_TEXT_VALUE),
					getParameters().getValue(Parameters.EXCEL_BOOLEAN_FALSE_TEXT_VALUE));

		} catch (Exception e) {
			throw new DetailedException(e).addMessage("Impossible de retrouver la valeur du paramètre à la ligne "
					+ line + " et colonne " + column + " : " + e.toString());
		}
	}

}
