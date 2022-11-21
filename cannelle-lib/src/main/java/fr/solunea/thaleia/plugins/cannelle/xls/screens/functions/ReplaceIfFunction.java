package fr.solunea.thaleia.plugins.cannelle.xls.screens.functions;

import fr.solunea.thaleia.plugins.cannelle.contents.A7File;
import fr.solunea.thaleia.plugins.cannelle.messages.LocalizedMessages;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.IScreenParameter;
import fr.solunea.thaleia.service.utils.FilesUtils;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Si une valeur (ou si 2 sont définies, alors si les deux valeurs) remplissent
 * la condition, alors on procède aux traitements.
 * 
 */
public class ReplaceIfFunction extends AbstractFunction {

	private static final Logger logger = Logger
			.getLogger(ReplaceIfFunction.class);

	private static final String INT_SEPARATOR = ";";
	private static final String LINES_SEPARATOR = "\\|\\|";
	protected String condition;
	private String value1;
	private String value2;
	private final List<String> steps;
	private final List<String> zones;
	private final List<String> lines;
	private final String toReplace;
	protected final String replaceValue;

	public ReplaceIfFunction(String condition, String value1, String value2,
			String steps, String zones, String lines, String toReplace,
			String replaceValue) {
		this.condition = condition;
		this.value1 = value1;
		this.value2 = value2;
		this.steps = translateStringToList(steps, INT_SEPARATOR);
		this.zones = translateStringToList(zones, INT_SEPARATOR);
		this.lines = translateStringToList(lines, LINES_SEPARATOR);
		this.toReplace = toReplace;
		this.replaceValue = replaceValue;
	}

	/**
	 * Effectue les suppressions et les remplacements
	 */
	protected void deleteAndReplace(A7File a7, Parameters parameters) throws DetailedException {
			deleteSteps(a7);
			deleteZones(a7);
			deleteLines(a7);
		try {
			if (!"".equals(toReplace)) {
				logger.debug("Remplacement de '" + toReplace + "' par '"
						+ replaceValue + "'");
				FilesUtils.replaceAllInFile(toReplace, replaceValue,
						a7.getFile(), A7File.getEncoding());
			} else {
				logger.debug("Pas de remplacement : la condition est vérifiée, mais la valeur à remplacer est vide.");
			}
		} catch (DetailedException e) {
			throw e.addMessage("Impossible d'effectuer le remplacement de '"
					+ toReplace + "' par " + replaceValue + "' dans "
					+ a7.getFile().getAbsolutePath() + ".");
		}
	}

	/**
	 * Effectue les suppressions des étapes demandées.
	 * 
	 * @param a7
	 * 
	 * @throws DetailedException
	 */
	private void deleteSteps(A7File a7) throws DetailedException {

		logger.debug("Suppression des étapes " + steps);

		List<Integer> stepsToDelete = new ArrayList<Integer>();
		for (int i = 0; i < this.steps.size(); i++) {
			try {
				if (steps.get(i).length() > 0) {
					// On traduit les numéros de step en entier
					Integer stepToDelete = new Integer(steps.get(i));
					stepsToDelete.add(stepToDelete);
				} else {
					// Les étapes vides ne sont pas traitées.
				}

			} catch (Exception e) {
				throw new DetailedException(
						"Impossible d'interpréter l'étape '" + steps.get(i)
								+ "' comme une étape : " + e);
			}
		}

		// Si on a demandé de supprimer des étapes
		if (!stepsToDelete.isEmpty()) {

			// Le numéro d'ordre du premier step.
			int fisrtStepIndex = 1;

			// On procède à la suppression
			// On considère que les steps à supprimer sont uniquement dans le
			// premier écran trouvé.
			logger.debug("Suppression des étapes " + stepsToDelete
					+ " à partir de l'index de base " + fisrtStepIndex
					+ " dans le fichier " + a7.getFile().getAbsolutePath());
			a7.deleteSteps(1, 1, stepsToDelete, fisrtStepIndex);
		}
	}

	private void deleteZones(A7File a7) throws DetailedException {
		for (int i = 0; i < getZones().size(); i++) {
			logger.debug("Suppression de la zone " + getZones().get(i));
			a7.deleteZone(getZones().get(i));
		}
	}

	private void deleteLines(A7File a7) throws DetailedException {
		for (int i = 0; i < this.lines.size(); i++) {
			String toDelete = lines.get(i);
			logger.debug("Suppression de la ligne " + toDelete);
			if (!"".equals(toDelete)) {
				FilesUtils.replaceAllInFile(toDelete, "", a7.getFile(),
						A7File.getEncoding());
			}
		}
	}

	private List<String> translateStringToList(String string, String separator) {
		return Arrays.asList(string.split(separator));
	}

	protected String getValue1() {
		if (value1 == null) {
			return "";
		} else {
			return value1;
		}
	}

	protected void setValue1(String value1) {
		this.value1 = value1;
	}

	protected String getValue2() {
		if (value2 == null) {
			return "";
		} else {
			return value2;
		}
	}

	protected void setValue2(String value2) {
		this.value2 = value2;
	}

	private List<String> getZones() {
		return zones;
	}

	protected String getReplaceValue() {
		return replaceValue;
	}

	@Override
	public void run(Map<String, IScreenParameter> screenParametersMap,
			Parameters parameters, String assetsDirName,
			ResourcesHandler resourcesHandler, A7File a7)
			throws DetailedException {

		updateValues(screenParametersMap, parameters);

		// La condition est nulle (attention, la condition peut ête vide)
		if (getCondition() == null) {
			throw new DetailedException(
					"Pas de remplacement : la condition est nulle.");
		}

		// Si les deux cellules du couple remplissent la condition, on traite.
		logger.debug("Test de la condition : condition=" + getCondition()
				+ " value1=" + getValue1() + " value2=" + getValue2());
		boolean replaced = false;

		if (getCondition().equals(getValue1())
				&& getCondition().equals(getValue2())) {
			deleteAndReplace(a7, parameters);
			replaced = true;
		}

		// Attention, dans le cas où la condition que l'on veut tester est ''
		// (vide), mais que les valeurs 1 (et/ou 2) sont vides, mais dans une
		// balises HTML :'<span
		// style=" font-family:Arial; font-size:20px; color:#000000; text-align:LEFT;"></span>'
		// Alors il faut ajouter ce test :
		if ("".equals(getCondition())) {
			String regex = "<span style=\".*\"></span>";
			if ((getValue1().matches(regex) || "".equals(getValue1()))
					&& (getValue2().matches(regex) || "".equals(getValue2()))) {
				deleteAndReplace(a7, parameters);
				replaced = true;
			}
		}

		if (!replaced) {
			logger.debug("Pas de remplacement (condition non remplie).");
		}

	}

	/**
	 * Remplace le contenu de value1 et value2 par la VALEUR du paramètre dont
	 * le nom est dans value1 et value2.
	 * 
	 * @param parameters
	 * 
	 * @throws DetailedException
	 */
	protected void updateValues(
			Map<String, IScreenParameter> screenParametersMap,
			Parameters parameters) throws DetailedException {

		logger.debug("Analyse des valeurs pour le lancement de replaceIf : value1="
				+ getValue1() + " value2=" + getValue2());

		// value1 et value2 contiennent le NOM du paramètre.
		// Il faut rechercher leur valeur.

		if ("".equals(getValue1())) {
			// Si value1 est vide, elle n'a pas besoin d'être remplacée
			// par la valeur de ce paramètre
		} else {
			IScreenParameter screenParameterValue1 = screenParametersMap
					.get(getValue1());
			if (screenParameterValue1 == null) {
				// Le message localisé correspondant
				String message = LocalizedMessages.getMessage(
						LocalizedMessages.REPLACE_VALUE_IN_SCREEN_ERROR,
						new Object[] { getValue1() });

				// On demande sa présentation
				ThaleiaSession.get().addError(message);

				throw new DetailedException("La valeur 1 '" + getValue1()
						+ "' doit être remplacée,"
						+ " mais la valeur de remplacement n'est pas définie !"
						+ " Elle n'a pas été correctement récupérée.");
			}
			setValue1(screenParameterValue1.getValue());
		}

		if ("".equals(getValue2())) {
			// Si value1 est vide, elle n'a pas besoin d'être remplacée
			// par la valeur de ce paramètre
		} else {
			IScreenParameter screenParameterValue2 = screenParametersMap
					.get(getValue2());
			if (screenParameterValue2 == null) {

				throw new DetailedException("La valeur 2 '" + getValue2()
						+ "' doit être remplacée,"
						+ " mais la valeur de remplacement n'est pas définie !"
						+ " Elle n'a pas été correctement récupérée.");
			}
			setValue2(screenParameterValue2.getValue());
		}

	}

	public String getCondition() {
		return condition;
	}

}
