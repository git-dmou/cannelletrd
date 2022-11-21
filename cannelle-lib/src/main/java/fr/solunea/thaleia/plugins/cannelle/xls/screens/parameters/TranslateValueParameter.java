package fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters;

import fr.solunea.thaleia.utils.DetailedException;

/**
 * Un paramètre décrivant le contenu d'un champ Un champ dans le fichier Excel
 * correspond à une liste déroulante Par exemple, le champ Score possède deux
 * options : 'Oui' et 'Non' Ces options sont définies dans le fichier de
 * configuration
 */
public class TranslateValueParameter extends AbstractScreenParameter {

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
