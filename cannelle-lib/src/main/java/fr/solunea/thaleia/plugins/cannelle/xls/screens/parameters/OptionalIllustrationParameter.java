package fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters;

import fr.solunea.thaleia.utils.DetailedException;

/**
 * Un paramètre d'illustration (fichier) optionnel.
 * 
 */
public class OptionalIllustrationParameter extends IllustrationParameter {

	@Override
	public void isValid() throws DetailedException {
		// toujours valide, même si non défini
	}

	@Override
	public String getValue() {
		if (super.getValue() == null) {
			return "";
		} else {
			return super.getValue();
		}
	}

}
