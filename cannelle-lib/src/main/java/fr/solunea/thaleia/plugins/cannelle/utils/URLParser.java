package fr.solunea.thaleia.plugins.cannelle.utils;


public class URLParser {

	/**
	 * @param string
	 * @return true si la chaîne est une URL. False sinon
	 */
	public static boolean isAnUrl(String string) {
		// try {
		// new URL(string);
		// return true;
		// } catch (MalformedURLException e) {
		// return false;
		// }

		if (string == null) {
			return false;
		}

		if (string.toLowerCase().startsWith("http://")
				|| string.toLowerCase().startsWith("https://")) {
			return true;
		} else {
			return false;
		}
	}

}
