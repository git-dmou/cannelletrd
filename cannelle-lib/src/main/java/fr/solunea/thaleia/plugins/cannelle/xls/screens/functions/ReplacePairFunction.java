package fr.solunea.thaleia.plugins.cannelle.xls.screens.functions;

import fr.solunea.thaleia.plugins.cannelle.contents.A7File;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.AssociationParameter;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.IScreenParameter;
import fr.solunea.thaleia.service.utils.FilesUtils;
import fr.solunea.thaleia.utils.DetailedException;

import java.io.File;
import java.util.Map;

public class ReplacePairFunction extends AbstractFunction {

	private final String pairName;
	private final String leftValueToReplace;
	private final String rightValueToReplace;

	private String leftValue = "";
	private String rightValue = "";

	/**
	 * @param pairName
	 *            le nom de la paire dont il faut remplacer les membres gauche
	 *            et droite
	 * @param leftValueToReplace
	 *            la valeur à remplacer dans le modèle pour le membre gauche
	 * @param rightValueToReplace
	 *            la valeur à remplacer dans le modèle pour le membre droit
	 * @param a7File
	 */
	public ReplacePairFunction(String pairName, String leftValueToReplace,
			String rightValueToReplace, File a7File) {
		this.pairName = pairName;
		this.leftValueToReplace = leftValueToReplace;
		this.rightValueToReplace = rightValueToReplace;
	}

	public String getPairName() {
		return pairName;
	}

	public String getLeftValue() {
		return leftValue;
	}

	public void setLeftValue(String leftValue) {
		this.leftValue = leftValue;
	}

	public String getRightValue() {
		return rightValue;
	}

	public void setRightValue(String rightValue) {
		this.rightValue = rightValue;
	}

	@Override
	public void run(Map<String, IScreenParameter> screenParametersMap,
			Parameters parameters, String assetsDirName,
			ResourcesHandler resourcesHandler, A7File a7)
			throws DetailedException {

		// On recherche le paramètre qui contient la paire concernée
		// Cela DOIT être un AssociationParameter
		AssociationParameter pairParameter = (AssociationParameter) screenParametersMap
				.get(getPairName());

		setLeftValue(pairParameter.getValue());
		setRightValue(pairParameter.getResponse());

		// On procède au remplacement de la proposition (membre gauche)
		if (leftValueToReplace != null && leftValue != null) {
			FilesUtils.replaceAllInFile(leftValueToReplace, leftValue,
					a7.getFile(), A7File.getEncoding(), true);
		}

		// On procède au remplacement de la réponse (membre droit)
		if (rightValueToReplace != null && rightValue != null) {
			FilesUtils.replaceAllInFile(rightValueToReplace, rightValue,
					a7.getFile(), A7File.getEncoding(), true);
		}

	}

}
