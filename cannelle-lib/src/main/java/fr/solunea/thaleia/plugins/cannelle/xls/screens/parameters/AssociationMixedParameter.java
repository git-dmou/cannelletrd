package fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters;

import fr.solunea.thaleia.utils.DetailedException;

import java.util.Optional;

/**
 * Value contient la valeur gauche ("value") = valeur gauche du fichier Excel,
 * Response contient la valeur droite ("correct").
 * 
 */
public class AssociationMixedParameter extends AbstractScreenParameter {

	/**
	 * Le nom du paramètre qui contient le décalage de colonnes entre la cellule
	 * qui porte le nom du paramètre, et celle qui contient sa correction.
	 */
	public static final String CORRECT_OFFSET_COL = "correct.offset.col";

	/**
	 * Le nom du paramètre qui contient le décalage de colonnes entre la cellule
	 * qui porte le nom du paramètre, et celle qui contient sa correction.
	 */
	public static final String CORRECT_OFFSET_LINE = "correct.offset.line";

	/**
	 * La valeur de la réponse à la proposition
	 */
	private String response;

	/**
	 * @return une string correspondant à la réponse à la proposition
	 */
	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	@Override
	public void isValid() throws DetailedException {

		// Vérifier la présence d'une valeur, même vide
		if (getValue() == null) {
			throw new DetailedException("La valeur du paramètre de type '"
					+ this.getClass().getName() + "' n'a pas été initialisée !");
		}
		// Vérifier la présence d'une réponse, même vide
		if (response == null) {
			throw new DetailedException("La valeur du paramètre de type '"
					+ this.getClass().getName() + "' n'a pas été initialisée !");
		}
	}

	@Override
	public boolean valueIsAFileName() {
		return false;
	}

//	@Override
//	public Optional<String> getTranslatableValue() {
//		return Optional.of(getValue());
//	}

	@Override
	public Optional<String> getTranslatableValue() {
		String translatableValue = getValue();
		if (translatableValue == null) {
			translatableValue = "";
		}
		return  Optional.of(translatableValue);
	}

	@Override
	public void setTranslatableValue(String value) {
		setValue(value);
	}

}
