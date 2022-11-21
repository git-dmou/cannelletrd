package fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters;

import fr.solunea.thaleia.utils.DetailedException;

public class ArrangementParameter extends AbstractScreenParameter {

	/**
	 * Le nom du paramètre qui contient le décalage de colonnes entre la cellule
	 * qui porte le nom du paramètre, et celle qui contient sa arrangemention.
	 */
	public static final String CORRECT_OFFSET_COL = "correct.offset.col";

	/**
	 * Le nom du paramètre qui contient le décalage de colonnes entre la cellule
	 * qui porte le nom du paramètre, et celle qui contient sa arrangemention.
	 */
	public static final String CORRECT_OFFSET_LINE = "correct.offset.line";

	/**
	 * La valeur de la réponse à la proposition (vrai ou faux)
	 */
	private String arrangement;

	/**
	 * @return une string correspondant à la réponse à la proposition
	 */
	public String getArrangement() {
		return arrangement;
	}

	public void setArrangement(String arrangement) {
		this.arrangement = arrangement;
	}

	@Override
	public void isValid() throws DetailedException {

		// Vérifier la présence d'une valeur, même vide
		if (getValue() == null) {
			throw new DetailedException("La valeur du paramètre de type '"
					+ this.getClass().getName() + "' n'a pas été initialisée !");
		}
		// Vérifier la présence d'une réponse, même vide
		if (arrangement == null) {
			throw new DetailedException("La valeur du paramètre de type '"
					+ this.getClass().getName() + "' n'a pas été initialisée !");
		} else {
			try {
				Integer.parseInt(arrangement);
			} catch (NumberFormatException e) {
				new DetailedException("La valeur du paramètre de type '"
						+ this.getClass().getName()
						+ "' n'a pas été initialisée avec un entier !");
			}
		}
	}

	@Override
	public boolean valueIsAFileName() {
		return false;
	}

}
