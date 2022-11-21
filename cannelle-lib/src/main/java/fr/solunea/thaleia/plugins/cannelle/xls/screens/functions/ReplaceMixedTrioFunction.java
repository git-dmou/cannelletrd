package fr.solunea.thaleia.plugins.cannelle.xls.screens.functions;/*
package fr.solunea.thaleia.plugins.cannelle.xls.screens.functions;

import fr.solunea.thaleia.plugins.cannelle.contents.A7File;
import fr.solunea.thaleia.plugins.cannelle.messages.LocalizedMessages;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.generator.ScreenGeneratorUtils;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.AssociationMixedParameter;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.IScreenParameter;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.TrioMixedParameter;
import fr.solunea.thaleia.service.utils.FilesUtils;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;

import java.io.File;
import java.util.Map;

public class ReplaceMixedTrioFunction extends AbstractFunction {

	private final String pairName;
	private final String leftValueToReplace;
	private final String rightValueToReplace;
	private final String centerValueToReplace;

	private String leftValue = "";
	private String rightValue = "";
	private String centerValue = "";

	*/
/**
	 * @param pairName
	 *            le nom de la paire dont il faut remplacer les membres gauche
	 *            et droite
	 * @param leftValueToReplace
	 *            la valeur à remplacer dans le modèle pour le membre gauche
	 * @param rightValueToReplace
	 *            la valeur à remplacer dans le modèle pour le membre droit
	 * @param a7File
	 *//*

	public ReplaceMixedTrioFunction(String pairName, String leftValueToReplace,
									String centerValueToReplace, String rightValueToReplace, File a7File) {
		this.pairName = pairName;
		this.leftValueToReplace = leftValueToReplace;
		this.rightValueToReplace = rightValueToReplace;
		this.centerValueToReplace = centerValueToReplace;
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

	public String getCenterValue() {
		return centerValue;
	}

	public void setCenterValue(String centerValue) {
		this.centerValue = centerValue;
	}

	@Override
	public void run(Map<String, IScreenParameter> screenParametersMap,
			Parameters parameters, String assetsDirName,
			ResourcesHandler resourcesHandler, A7File a7)
			throws DetailedException {

		// On recherche le paramètre qui contient la paire concernée
		// Cela DOIT être un AssociationParameter
		TrioMixedParameter pairParameter = (TrioMixedParameter) screenParametersMap
				.get(getPairName());

		setLeftValue(pairParameter.getValue());
		setRightValue(pairParameter.getRightValue());
		setCenterValue(pairParameter.getCenterValue());

		// Par defaut et si aucune valeur n'est précisée pour la valeur de droite ou gauche,
		// on remplace par une chaine vide
		if (leftValue == null){
			leftValue = "";
		}
		if (rightValue == null){
			rightValue = "";
		}
		if (centerValue == null){
			centerValue = "";
		}

		// On procède au remplacement de la proposition (membre gauche)
		if (leftValueToReplace != null) {
		    if (pairParameter.leftValueIsAFileName()){
				FilesUtils.replaceAllInFile(leftValueToReplace + "Illus", leftValue,
						a7.getFile(), A7File.getEncoding(), true);
				FilesUtils.replaceAllInFile(leftValueToReplace, "",
						a7.getFile(), A7File.getEncoding(), true);

				checkMediaFilename(leftValue);
				try {
					ScreenGeneratorUtils.safeAddMedia(resourcesHandler, parameters, leftValueToReplace + "Illus", leftValue,
							a7.getFile(), A7File.getEncoding(), assetsDirName);
				} catch (Exception e) {
					// Le message localisé correspondant
					String message = LocalizedMessages.getMessage(LocalizedMessages.FILE_NOT_FOUND_ERROR, "");
					// On demande sa présentation
					ThaleiaSession.get().addError(message);
					throw new DetailedException("Impossible d'associer le fichier '" + leftValue + "' dans "
							+ "l'archive : " + e);
				}
            } else {
				FilesUtils.replaceAllInFile(leftValueToReplace + "IllusAlt", "",
						a7.getFile(), A7File.getEncoding(), true);
				FilesUtils.replaceAllInFile(leftValueToReplace + "Illus", "",
						a7.getFile(), A7File.getEncoding(), true);
				FilesUtils.replaceAllInFile(leftValueToReplace, leftValue,
						a7.getFile(), A7File.getEncoding(), true);
			}
		}

		// On procède au remplacement de la réponse (membre droit)
		if (rightValueToReplace != null) {
			if (pairParameter.rightValueIsAFileName()){
				FilesUtils.replaceAllInFile(rightValueToReplace + "Illus", rightValue,
						a7.getFile(), A7File.getEncoding(), true);
				FilesUtils.replaceAllInFile(rightValueToReplace, "",
						a7.getFile(), A7File.getEncoding(), true);

				checkMediaFilename(rightValue);
				try {
					ScreenGeneratorUtils.safeAddMedia(resourcesHandler, parameters, rightValueToReplace + "Illus", rightValue,
							a7.getFile(), A7File.getEncoding(), assetsDirName);
				} catch (Exception e) {
					// Le message localisé correspondant
					String message = LocalizedMessages.getMessage(LocalizedMessages.FILE_NOT_FOUND_ERROR, "");
					// On demande sa présentation
					ThaleiaSession.get().addError(message);
					throw new DetailedException("Impossible d'associer le fichier '" + rightValue + "' dans "
							+ "l'archive : " + e);
				}
			} else {
				FilesUtils.replaceAllInFile(rightValueToReplace + "IllusAlt", "",
						a7.getFile(), A7File.getEncoding(), true);
				FilesUtils.replaceAllInFile(rightValueToReplace + "Illus", "",
						a7.getFile(), A7File.getEncoding(), true);
				FilesUtils.replaceAllInFile(rightValueToReplace, rightValue,
						a7.getFile(), A7File.getEncoding(), true);
			}
		}

		// On procède au remplacement de la réponse (membre droit)
		if (centerValueToReplace != null) {
			if (pairParameter.centerValueIsAFileName()){
				FilesUtils.replaceAllInFile(centerValueToReplace + "Illus", centerValue,
						a7.getFile(), A7File.getEncoding(), true);
				FilesUtils.replaceAllInFile(centerValueToReplace, "",
						a7.getFile(), A7File.getEncoding(), true);

				checkMediaFilename(centerValue);
				try {
					ScreenGeneratorUtils.safeAddMedia(resourcesHandler, parameters, centerValueToReplace + "Illus", centerValue,
							a7.getFile(), A7File.getEncoding(), assetsDirName);
				} catch (Exception e) {
					// Le message localisé correspondant
					String message = LocalizedMessages.getMessage(LocalizedMessages.FILE_NOT_FOUND_ERROR, "");
					// On demande sa présentation
					ThaleiaSession.get().addError(message);
					throw new DetailedException("Impossible d'associer le fichier '" + centerValue + "' dans "
							+ "l'archive : " + e);
				}
			} else {
				FilesUtils.replaceAllInFile(centerValueToReplace + "IllusAlt", "",
						a7.getFile(), A7File.getEncoding(), true);
				FilesUtils.replaceAllInFile(centerValueToReplace + "Illus", "",
						a7.getFile(), A7File.getEncoding(), true);
				FilesUtils.replaceAllInFile(centerValueToReplace, centerValue,
						a7.getFile(), A7File.getEncoding(), true);
			}
		}
	}
}
*/
