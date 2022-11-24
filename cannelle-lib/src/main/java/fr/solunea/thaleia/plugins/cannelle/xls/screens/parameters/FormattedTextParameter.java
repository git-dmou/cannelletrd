package fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters;

import java.util.Optional;

/**
 * Un contenu textuel dont on récupère le formattage Xls, pour le traduire en
 * formattage HTML.
 *
 */
public class FormattedTextParameter extends TextParameter {
	
	@Override
	public String getValue() {
		// On remplace les retours à la ligne par l'équivalent en HTML, car le
		// \n ne pourrs pas être stocké dans les écrans en JSON.
		return value.replace("\n", "<br/>");
	}
	
	@Override
	public boolean parseTextFormatInHTML() {
		return true;
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
