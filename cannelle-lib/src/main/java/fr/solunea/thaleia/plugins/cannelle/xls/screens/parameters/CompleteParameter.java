package fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters;

import fr.solunea.thaleia.utils.DetailedException;

/**
 * Un paramètre décrivant le contenu d'une zone de texte à présenter : zone de
 * texte, titre, etc.
 * 
 */
public class CompleteParameter extends AbstractScreenParameter {

	@Override
	public void isValid() throws DetailedException {

		// Vérification de la présence d'une valeur, même vide
		if (getValue() == null) {
			throw new DetailedException("La valeur du paramètre de type '"
					+ this.getClass().getName() + "' n'a pas été initialisée !");
		}

	}
	
	@Override
	public boolean valueIsAFileName() {
		return false;
	}

}
