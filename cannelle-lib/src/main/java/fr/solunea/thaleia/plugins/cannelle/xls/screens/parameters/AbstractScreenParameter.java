package fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters;

import fr.solunea.thaleia.utils.DetailedException;

import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractScreenParameter implements IScreenParameter {

	/**
	 * Le nom du paramètre qui contient le décalage de colonnes entre la cellule
	 * qui porte le nom du paramètre, et celle qui contient sa valeur.
	 */
	public static final String VALUE_OFFSET_COL = "value.offset.col";

	/**
	 * Le nom du paramètre qui contient le décalage de colonnes entre la cellule
	 * qui porte le nom du paramètre, et celle qui contient sa valeur.
	 */
	public static final String VALUE_OFFSET_LINE = "value.offset.line";

	public static final String IS_OPTIONAL_VALUE = "value.optional";

	public static final String OPTIONAL_DEFAULT_VALUE = "value.default";

	protected String value = "";

	protected String safeKey;

	private Properties properties = new Properties();

	@Override
	public String toString() {
		String name = getProperty("name", "?");
		String value = getValue();
		return this.getClass().getName() + " : " + name + " = " + value;
	}

	@Override
	public void setProperties(Properties properties) throws DetailedException {
		this.properties = properties;
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String getContentPropertyName() {
		return getProperty("contentproperty", null);
	}

	public void setSafeKey(String safeKey) {
		this.safeKey = safeKey;
	}

	public String getSafeKey() {
		return safeKey;
	}
	
	@Override
	public boolean isValueAHtmlText() {
		return false;
	}
	
	@Override
	public boolean parseTextFormatInHTML() {
		return false;
	}

	/**
	 * @param endWith
	 * @param strings
	 * @return la dernière chaîne de la liste qui finit par la chaîne demandée,
	 *         ou null si aucune des chaînes ne finit par la chaîne demandée.
	 */
	protected static String getStringEndingWith(String endWith,
			List<String> strings) {
		String result = null;

		for (String string : strings) {
			if (string.endsWith(endWith)) {
				result = string;
			}
		}

		return result;
	}

	public boolean testForFilename(String value) {
		Pattern pattern = Pattern.compile("^[\\w,\\s-]+\\.[A-Za-z]{3,4}$");
		Matcher matcher = pattern.matcher(value);
		int matched = 0;
		while(matcher.find()) matched++;
		return matched > 0;
	}
}
