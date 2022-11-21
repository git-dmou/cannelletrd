package fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters;

import fr.solunea.thaleia.utils.DetailedException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Value contient la valeur gauche ("value") = valeur gauche du fichier Excel,
 * Response contient la valeur droite ("correct").
 * 
 */
public class TrioMixedParameter extends AbstractScreenParameter {

	public static final String CENTER_OFFSET_COL = "center.offset.col";
	public static final String CENTER_OFFSET_LINE = "center.offset.line";
	public static final String RIGHT_OFFSET_COL = "right.offset.col";
	public static final String RIGHT_OFFSET_LINE = "right.offset.line";

	private String center;
	private String right;

	/**
	 * @return une string correspondant à la réponse à la proposition
	 */
	public String getCenterValue() {
		return center;
	}
	public String getRightValue() {
		return right;
	}

	public void setCenterValue(String center) {
		this.center = center;
	}
	public void setRightValue(String right) {
		this.right = right;
	}

	@Override
	public void isValid() throws DetailedException {

		if (getValue() == null) {
			throw new DetailedException("La valeur du paramètre de type '"
					+ this.getClass().getName() + "' n'a pas été initialisée !");
		}
		if (center == null) {
			throw new DetailedException("La valeur du paramètre de type '"
					+ this.getClass().getName() + "' n'a pas été initialisée !");
		}
		if (right == null) {
			throw new DetailedException("La valeur du paramètre de type '"
					+ this.getClass().getName() + "' n'a pas été initialisée !");
		}
	}

	public boolean rightValueIsAFileName() {
		return testForFilename(getRightValue());
	}
	public boolean leftValueIsAFileName() {
		return testForFilename(getValue());
	}
	public boolean centerValueIsAFileName() {
		return testForFilename(getCenterValue());
	}

	/* J'ai besoin de cette fonction dans la classe abstraite afin de pouvoir m'en servir dans ClassificationParameter.java
	private boolean testForFileName(String value){
		Pattern pattern = Pattern.compile("^[\\w,\\s-]+\\.[A-Za-z]{3}$");
		Matcher matcher = pattern.matcher(value);
		int matched = 0;
		while(matcher.find()) matched++;
		return matched > 0;
	}*/

	@Override
	public boolean valueIsAFileName() {
		return false;
	}

}
