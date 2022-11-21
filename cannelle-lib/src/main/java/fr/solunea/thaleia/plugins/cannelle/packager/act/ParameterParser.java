package fr.solunea.thaleia.plugins.cannelle.packager.act;

import org.apache.log4j.Logger;

import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.plugins.cannelle.contents.AbstractVersionedContent;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;

public class ParameterParser {

	static final Logger logger = Logger.getLogger(ParameterParser.class);

	/**
	 * @param value
	 * @param screen
	 * @param parameters
	 * @locale la locale pour laquelle on demande le résultat
	 * @return value, ou une valeur remplacée en dynamique en fonction de
	 *         l'écran, si un mot-clé est reconnu (@@content_id,
	 * @@property_title, etc.)
	 */
	public static String parseParameter(String value,
			AbstractVersionedContent screen, Locale locale,
			Parameters parameters) {

		String result = value;

		if (value.startsWith("@@")) {

			if ("@@content_id".equals(value)) {
				result = screen.getIdentifier();

			} else if (value.startsWith("@@property_")) {
				// La propriété demandée est :
				String property = value.substring("@@property_".length(),
						value.length());

				// On la cherche dans les properties de l'écran
				try {
					result = ((AbstractVersionedContent) screen)
							.getContentVersionPropertyValue(property, locale, "");
					logger.debug("Recherche de la propriété property '"
							+ property + "' pour la locale '"
							+ locale.getName() + "' : trouvé '" + result + "'");

				} catch (Exception e) {
					// Cast impossible : on ignore.
					logger.debug("Impossible de trouver la ContentProperty '"
							+ property + "' pour l'objet " + screen + ": "
							+ e.toString());
				}
			}
		}

		return result;

	}

}
