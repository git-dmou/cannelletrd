package fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters;

public class DictionaryValue {

	private String identifier;
	private String valueToTranslate;
	private String value;

	/**
	 * Entrée dans le dictionnaire d'options
	 * 
	 * @param identifier
	 *            : identifiant du paramètre (xls.parser.options.score.active
	 *            par exemple)
	 * @param valueToTranslate
	 *            : valeur à convertir ('Oui')
	 * @param value
	 *            : valeur réelle ('1')
	 */
	public DictionaryValue(String identifier, String valueToTranslate,
			String value) {
		this.identifier = identifier;
		this.valueToTranslate = valueToTranslate;
		this.value = value;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getValueToTranslate() {
		return valueToTranslate;
	}

	public void setValueToTranslate(String valueToTranslate) {
		this.valueToTranslate = valueToTranslate;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
