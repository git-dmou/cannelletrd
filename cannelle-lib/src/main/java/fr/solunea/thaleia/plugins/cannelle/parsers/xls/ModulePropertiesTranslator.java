package fr.solunea.thaleia.plugins.cannelle.parsers.xls;

import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.utils.DetailedException;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Analyse les paramètres de type "xls.parser.module.properties.X.label" et
 * "xls.parser.module.properties.X.contentProperty" pour traduire un nm de
 * propriété en nom de ContentPropery.
 * 
 */
public class ModulePropertiesTranslator {

	private static final String XLS_PARSER_MODULE_PROPERTIES = "xls.parser.module.properties";
	private static final Logger logger = Logger.getLogger(ModulePropertiesTranslator.class);

	private Map<String, String> translations;

	private final Parameters parameters;

	public ModulePropertiesTranslator(Parameters parameters) {
		this.parameters = parameters;
	}

	/**
	 * @param parameters
	 * @return
	 */
	public static ModulePropertiesTranslator getInstance(Parameters parameters) {
		return new ModulePropertiesTranslator(parameters);
	}

	/**
	 * @param key
	 * @param defaultValue
	 * @return
	 * @throws DetailedException
	 */
	public String translate(String key, String defaultValue) throws DetailedException {
		String result = getTranslations().get(key);
		if (result == null) {
			return defaultValue;
		} else {
			return result;
		}
	}

	/**
	 * @return les traduction de nom de propriétés en nom de ContentProperties
	 *         définies dans les paramètres.
	 * @throws DetailedException
	 */
	private Map<String, String> getTranslations() throws DetailedException {
		if (translations == null) {
			translations = new HashMap<String, String>();

			// On recherche le nombre de propriétés
			int max;
			try {
				max = parameters.getMaxIndexForKey(XLS_PARSER_MODULE_PROPERTIES);
			} catch (DetailedException e) {
				throw new DetailedException(e).addMessage("Impossible de retrouver le nombre de propriétés de type '"
						+ XLS_PARSER_MODULE_PROPERTIES + "'");
			}

			// On stocke les couples name / contentproperty
			for (int i = 0; i <= max; i++) {
				String propertyRoot = XLS_PARSER_MODULE_PROPERTIES + "." + i;
				String labelProperty = propertyRoot + ".label";
				String contentpropertyProperty = propertyRoot + ".contentProperty";
				String label = parameters.getValue(labelProperty);
				String contentproperty = parameters.getValue(contentpropertyProperty);
				translations.put(label, contentproperty);
			}

			logger.debug("Traductions de propriétés retrouvées : ");
			for (String key : translations.keySet()) {
				logger.debug(key + " = " + translations.get(key));
			}
		}

		return translations;
	}
}
