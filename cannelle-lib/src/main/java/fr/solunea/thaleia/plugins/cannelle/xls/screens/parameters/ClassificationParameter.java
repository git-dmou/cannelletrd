package fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters;

import fr.solunea.thaleia.utils.DetailedException;

import java.util.Optional;

public class ClassificationParameter extends AbstractScreenParameter {

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
	private String categorie;

	/**
	 * @return une string correspondant à la réponse à la proposition, tel que
	 *         sélectionné dans la liste déroulante des noms de catégories dans
	 *         le fichier Excel.
	 */
	public String getCategorie() {
		return categorie;
	}

	public void setCategorie(String categorie) {
		this.categorie = categorie;
	}

	@Override
	public void isValid() throws DetailedException {

		// Vérifier la présence d'une valeur, même vide
		if (getValue() == null) {
			throw new DetailedException("La valeur du paramètre de type '"
					+ this.getClass().getName() + "' n'a pas été initialisée !");
		}
		// Vérifier la présence d'une réponse, même vide
		if (categorie == null) {
			throw new DetailedException("La valeur du paramètre de type '"
					+ this.getClass().getName() + "' n'a pas été initialisée !");
		}
	}

	/**
	 * On peut mettre des images en vignette dans les propositions de réponse,
	 * il faut donc que l'on puisse dissocier les images du simple texte.
	 */
	@Override
	public boolean valueIsAFileName() {
		return testForFilename(getValue());
	}

	@Override
	public Optional<String> getTranslatableValue() {
		return  Optional.of(getValue());
	}

	@Override
	public void setTranslatableValue(String value) {
		setValue(value);
	}




}
