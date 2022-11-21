package fr.solunea.thaleia.plugins.cannelle.xls.screens.functions;

import fr.solunea.thaleia.plugins.cannelle.contents.A7File;
import fr.solunea.thaleia.plugins.cannelle.messages.LocalizedMessages;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.generator.ScreenGeneratorUtils;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.AssociationMixedParameter;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.IScreenParameter;
import fr.solunea.thaleia.service.utils.FilesUtils;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;

import java.io.File;
import java.util.Map;

public class ReplaceMixedPairFunction extends AbstractFunction {

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
	public ReplaceMixedPairFunction(String pairName, String leftValueToReplace,
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
	public void run(Map<String, IScreenParameter> screenParametersMap, Parameters parameters, String assetsDirName, ResourcesHandler resourcesHandler, A7File a7) throws DetailedException {
		// On recherche le paramètre qui contient la paire concernée
		// Cela DOIT être un AssociationParameter
		AssociationMixedParameter mixedPairParameter = (AssociationMixedParameter) screenParametersMap.get(getPairName());

		setLeftValue(mixedPairParameter.getValue());
		setRightValue(mixedPairParameter.getResponse());

		// Par defaut et si aucune valeur n'est précisée pour la valeur de droite ou gauche,
		// on remplace par une chaine vide
		if (leftValue == null){
			leftValue = "";
		}
		if (rightValue == null){
			rightValue = "";
		}
		String left = mixedPairParameter.getValue();
		String right = mixedPairParameter.getResponse();
		String leftIsFileName = resourcesHandler.getUploadedFiles().findStringFile(left);
		String rightIsFileName = resourcesHandler.getUploadedFiles().findStringFile(right);

		// On procède au remplacement de la proposition (membre gauche)
		remplacement(parameters, assetsDirName, resourcesHandler, a7, leftIsFileName, leftValueToReplace, leftValue);

		// On procède au remplacement de la réponse (membre droit)
		remplacement(parameters, assetsDirName, resourcesHandler, a7, rightIsFileName, rightValueToReplace, rightValue);
	}


	private void remplacement(Parameters parameters, String assetsDirName, ResourcesHandler resourcesHandler, A7File a7, String isFileName, String valueToReplace, String value) throws DetailedException {
		if (valueToReplace != null) {
		    if (isFileName!=null){
				replace(parameters, assetsDirName, resourcesHandler, a7, valueToReplace, isFileName);
			} else {
				FilesUtils.replaceAllInFile(valueToReplace + "IllusAlt", "",
						a7.getFile(), A7File.getEncoding(), true);
				FilesUtils.replaceAllInFile(valueToReplace + "Illus", "",
						a7.getFile(), A7File.getEncoding(), true);
				FilesUtils.replaceAllInFile(valueToReplace, value,
						a7.getFile(), A7File.getEncoding(), true);
			}
		}
	}

	private void replace(Parameters parameters, String assetsDirName, ResourcesHandler resourcesHandler, A7File a7, String ValueToReplace, String Value) throws DetailedException {
		FilesUtils.replaceAllInFile(ValueToReplace + "Illus", Value,
				a7.getFile(), A7File.getEncoding(), true);
		FilesUtils.replaceAllInFile(ValueToReplace, "",
				a7.getFile(), A7File.getEncoding(), true);

		checkMediaFilename(Value);
		try {
			ScreenGeneratorUtils.safeAddMedia(resourcesHandler, parameters, ValueToReplace + "Illus", Value,
					a7.getFile(), A7File.getEncoding(), assetsDirName);
		} catch (Exception e) {
			// Le message localisé correspondant
			String message = LocalizedMessages.getMessage(LocalizedMessages.FILE_NOT_FOUND_ERROR, "");
			// On demande sa présentation
			ThaleiaSession.get().addError(message);
			throw new DetailedException("Impossible d'associer le fichier '" + Value + "' dans "
					+ "l'archive : " + e);
		}
	}

}
