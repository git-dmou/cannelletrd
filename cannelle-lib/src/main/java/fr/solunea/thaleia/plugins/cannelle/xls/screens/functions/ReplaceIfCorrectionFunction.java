package fr.solunea.thaleia.plugins.cannelle.xls.screens.functions;

import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.IScreenParameter;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.IllustrationQruParameter;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.QruParameter;
import fr.solunea.thaleia.utils.DetailedException;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Identique à ReplaceIfFonction, mais effectue la condition sur la CORRECTION
 * du screenParameter dont le nom est value1 / value2.
 * 
 */
public class ReplaceIfCorrectionFunction extends ReplaceIfFunction {

	private static final Logger logger = Logger
			.getLogger(ReplaceIfCorrectionFunction.class);

	public ReplaceIfCorrectionFunction(String condition, String value1,
			String value2, String steps, String zones, String lines,
			String toReplace, String replaceValue) {
		super(condition, value1, value2, steps, zones, lines, toReplace,
				replaceValue);
	}

	/**
	 * Remplace le contenu de value1 et value2 par la VALEUR du paramètre dont
	 * le nom est dans value1 et value2.
	 * 
	 * @throws DetailedException
	 */
	@Override
	protected void updateValues(
			Map<String, IScreenParameter> screenParametersMap,
			Parameters parameters) throws DetailedException {

		logger.debug("Analyse des valeurs pour le lancement de replaceIfCorrection : value1="
				+ getValue1()
				+ " value2="
				+ getValue2()
				+ " (replaceValue='"
				+ getReplaceValue() + "')");

		// value1 et value2 contiennent le NOM du paramètre.
		// Il faut rechercher leur valeur de correction.
		// On remplace donc le nom du paramètre par la valeur de leur correction
		// (VRAI ou FAUX)?

		if ("".equals(getValue1())) {
			// Si value1 est vide, elle n'a pas besoin d'être remplacée
			// par la valeur de ce paramètre

		} else {
			IScreenParameter screenParameterValue1 = screenParametersMap
					.get(getValue1());

			// Il faut que le ScreenParameter soit un QruParameter
			if (screenParameterValue1 instanceof QruParameter) {
				String trueValue = parameters
						.getValue(Parameters.EXCEL_BOOLEAN_TRUE_TEXT_VALUE);
				String falseValue = parameters
						.getValue(Parameters.EXCEL_BOOLEAN_FALSE_TEXT_VALUE);

				if (((QruParameter) screenParameterValue1).isCorrect()) {
					setValue1(trueValue);
				} else {
					setValue1(falseValue);
				}

			}
			else if (screenParameterValue1 instanceof IllustrationQruParameter) {
				logger.debug("Traitement d'un IllustrationQruParameter : " + getValue1());
				try {
					if(((IllustrationQruParameter) screenParameterValue1).getResponse().equals(this.condition)) {
						setValue1(((IllustrationQruParameter) screenParameterValue1).getResponse());
					}
				} catch (Exception e) {
					// Rien : pour les items optionnels.
				}
			}
			else {
				throw new DetailedException("Le paramètre '" + getValue1()
						+ "' défini pour la fonction de remplacement de "
						+ "correction n'est pas un paramètre de QRU"
						+ " : il n'y a pas de correction à tester !");
			}

		}

		if ("".equals(getValue2())) {
			// Si value1 est vide, elle n'a pas besoin d'être remplacée
			// par la valeur de ce paramètre

		} else {
			IScreenParameter screenParameterValue2 = screenParametersMap
					.get(getValue2());

			// Il faut que le ScreenParameter soit un QruParameter
			if (screenParameterValue2 instanceof QruParameter) {
				String trueValue = parameters
						.getValue(Parameters.EXCEL_BOOLEAN_TRUE_TEXT_VALUE);
				String falseValue = parameters
						.getValue(Parameters.EXCEL_BOOLEAN_FALSE_TEXT_VALUE);

				if (((QruParameter) screenParameterValue2).isCorrect()) {
					setValue2(trueValue);
				} else {
					setValue2(falseValue);
				}

			}
			else if (screenParameterValue2 instanceof IllustrationQruParameter) {
				logger.debug("Traitement d'un IllustrationQruParameter : " + getValue2());
				try {
					if(((IllustrationQruParameter) screenParameterValue2).getResponse().equals(this.condition)) {
						setValue2(((IllustrationQruParameter) screenParameterValue2).getResponse());
					}
				} catch (Exception e) {
					// Rien : pour les items optionnels.
				}
			}
			else {
				throw new DetailedException("Le paramètre '" + getValue2()
						+ "' défini pour la fonction de remplacement de "
						+ "correction n'est pas un paramètre de QRU"
						+ " : il n'y a pas de correction à tester !");
			}

		}

	}
}
