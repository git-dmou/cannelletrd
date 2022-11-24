package fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters;

import fr.solunea.thaleia.utils.DetailedException;

import java.util.Optional;

public class QruParameter extends AbstractScreenParameter {

	/**
	 * Le nom du paramètre qui contient le décalage de colonnes entre la cellule
	 * qui porte le nom du paramètre, et celle qui contient sa isCorrection.
	 */
	public static final String CORRECT_OFFSET_COL = "correct.offset.col";

	/**
	 * Le nom du paramètre qui contient le décalage de colonnes entre la cellule
	 * qui porte le nom du paramètre, et celle qui contient sa isCorrection.
	 */
	public static final String CORRECT_OFFSET_LINE = "correct.offset.line";

	/**
	 * La valeur de la réponse à la proposition (vrai ou faux)
	 */
	private boolean correct;

	/**
	 * @return une string correspondant à la réponse à la proposition (vrai ou
	 *         faux)
	 */
	public boolean isCorrect() {
		return correct;
	}

	public void setIsCorrect(boolean isCorrect) {
		this.correct = isCorrect;
	}

	@Override
	public void isValid() throws DetailedException {

		// Vérifier la présence d'une valeur, même vide
		if (getValue() == null) {
			throw new DetailedException("La valeur du paramètre de type '"
					+ this.getClass().getName() + "' n'a pas été initialisée !");
		}
	}
	
	@Override
	public String toString() {
		String result = super.toString();
		return result + " isCorrect = " + isCorrect();
	}
	
	@Override
	public boolean valueIsAFileName() {
		return false;
	}

	@Override
	public Optional<String> getTranslatableValue() {
		return  Optional.of(getValue());
	}


}
