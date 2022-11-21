package fr.solunea.thaleia.plugins.cannelle.packager.act;

import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import fr.solunea.thaleia.plugins.cannelle.contents.A7Screen;

public class ActContentAttributeValueParser {

	private static final Logger logger = Logger
			.getLogger(ActContentAttributeValueParser.class);

	/**
	 * 
	 * Interprète la valeur demandée, selon les critères suivants :
	 * 
	 * {Condition}{valeur si vrai}>{Condition 2}{valeur si
	 * vrai}>...etc...>valeur par défaut. Si la valeur pas défaut n'est pas
	 * donnée, alors la valeur renvoyée est videPar exemple : {@@score=1
	 * }{quiz}>{@@type=bilan
	 * }{bilan} : si score=1, alors
	 * "quiz", sinon si type = bilan, alors "bilan", sinon une valeur vide.
	 * 
	 * @@content_id = l'identifiant du module exporté, unique dans Thaleia.
	 * 
	 * @@property_XXXX = la valeur de la propriété qui se nomme XXX dans
	 *                 Thaleia.
	 * 
	 *                 Sinon, texte libre repris tel quel
	 * @param value
	 * @param content
	 * @param locale
	 *            la locale dans laquelle rechercher les valeurs dynamiques (les
	 *            valeurs de propriétés du contenu).
	 * @return
	 */
	public String parse(String value, A7Screen content,
			fr.solunea.thaleia.model.Locale locale) {

		String checkedValue = value;
		if (value == null) {
			checkedValue = "";
		}

		// Par défaut, on renverra "";
		String defaultValue = "";

		if (checkedValue.matches("\\{.*\\}\\{.*\\}")
				&& checkedValue.indexOf('>') == -1) {
			// Une seule valeur conditionnelle : {Condition}{valeur si vrai}

			// On récupère la condition
			Condition condition = new Condition(checkedValue, content, locale);

			if (condition.isTrue()) {
				// On recherche la valeur si la condition est vraie
				return parseValueInBraces(1, checkedValue, content, locale);

			} else {
				// Pas de valeur si la condition est fausse : on renvoie la
				// valeur par défaut.
				return defaultValue;
			}

		} else if (checkedValue.matches("\\{.*\\}\\{.*\\}>.*")) {
			// Plusieurs valeurs conditionnelles

			// On découpe en tokens séparés par des > :
			StringTokenizer tokenizer = new StringTokenizer(checkedValue, ">");
			while (tokenizer.hasMoreTokens()) {
				// Un token = une valeur conditionnelle : {Condition}{valeur si
				// vrai} OU valeur inconditionnelle ("toto" ou ""@@property")
				String token = tokenizer.nextToken();

				// Si c'est une condition
				if (token.matches("\\{.*\\}\\{.*\\}")) {
					// On récupère la condition
					Condition condition = new Condition(token, content, locale);

					if (condition.isTrue()) {
						// On recherche la valeur si la condition est vraie
						return parseValueInBraces(1, token, content, locale);

					} else {
						// On passe au token suivant

					}

				} else {
					// Ce n'est pas une condition : elle est donc renvoyée sans
					// test, mais elle est interprétée.
					return parseValue(token, content, locale);
				}
			}
			// Si rien n'a été renvoyé après avoir parsé tous les tokens, on
			// renvoie la valeur par défaut
			return defaultValue;

		} else {
			// On analyse la valeur, sans condition
			return parseValue(checkedValue, content, locale);
		}

	}

	/**
	 * Interprète la valeur demandée dans les accolades à la position i (la
	 * première accolade est à l'indice 0).
	 * 
	 * 
	 * @param i
	 * @param string
	 * @param content
	 * @param locale
	 * @return
	 */
	private String parseValueInBraces(int i, String string, A7Screen content,
			fr.solunea.thaleia.model.Locale locale) {
		// On récupère le contenu entre accolades à la position i
		String value = getValueInBraces(i, string);

		return parseValue(value, content, locale);
	}

	/**
	 * 
	 * Interpète la valeur demandée, selon les propriétés de ce contenu, d'après
	 * ces valeurs possibles :
	 * 
	 * @@content_id = l'identifiant du module exporté, unique dans Thaleia.
	 * 
	 * @@property_XXXX = la valeur de la propriété qui se nomme XXX dans
	 *                 Thaleia.
	 * 
	 *                 Sinon la valeur telle quelle.
	 * @param value
	 * @param content
	 * @param locale
	 * @return
	 */
	private String parseValue(String value, A7Screen content,
			fr.solunea.thaleia.model.Locale locale) {
		// On interprète cette valeur
		if (value.startsWith("@@")) {

			if ("@@content_id".equals(value)) {
				// On renvoie l'Id du contenu
				return content.getIdentifier();

			} else {
				// On recherche la propriété qui porte ce nom
				String propertyName = value.substring("@@".length(),
						value.length());
				String result = content.getContentVersionPropertyValue(propertyName,
						locale, "");
				logger.debug("Analyse de la propriété '" + value
						+ "' : valeur calculée = " + result);
				return result;
			}

		} else {
			// On renvoie la valeur telle quelle
			return value;
		}
	}

	/**
	 * Renvoie la chaîne de caractères entre les ièmes accolades.
	 * 
	 * @param i
	 * @param string
	 *            la chaîne dans laquelle on recherche les valeurs entre
	 *            accolades.
	 * @return la valeur de la propriété qui est située dans la ième accolade
	 *         (la première est à l'indice 0).
	 */
	private String getValueInBraces(int i, String string) {
		String defaultValue = "";

		StringTokenizer tokenizer = new StringTokenizer(string, "{");
		int count = 0;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (count == i) {
				// Si on est à l'indice demandé
				// On supprime le dernier } du token
				try {
					if (token.lastIndexOf('}') == -1) {
						return token;
					} else {
						return token.substring(0, token.lastIndexOf('}'));
					}

				} catch (Exception e) {
					// On n'avait pas de token du modèle {.*}
					// On renvoie la valeur par défaut
					return defaultValue;
				}
			} else {
				// On incrémente le compteur et on continuye à chercher
				count++;
			}
		}

		// L'indice demandé n'a pas été trouvé
		return defaultValue;
	}

	private class Condition {

		private boolean isTrue;

		/**
		 * Recherche la première accolade, et y recherche le pattern XXXX=YYY.
		 * Interprète XXXX comme valeur ou propriété, et calcule le résultat de
		 * la condition.
		 * 
		 * @param string
		 * @param content
		 * @param locale
		 */
		public Condition(String string, A7Screen content,
				fr.solunea.thaleia.model.Locale locale) {
			String condition = getValueInBraces(0, string);

			// On recherche un =
			int deuxParams = condition.indexOf('|');
			int equals = condition.indexOf('=');
			if (equals < 0) {
				// Pas de =
				logger.debug("La condition '"
						+ condition
						+ "' ne peut pas être interprétée : on la considère fausse");
				isTrue = false;

			}
			else if(deuxParams<0){
				String left = parseValue(condition.substring(0, equals),
						content, locale);
				String right = parseValue(
						condition.substring(equals + 1, condition.length()),
						content, locale);
				isTrue = compare(left, right);
			}
			else{
				String left = parseValue(condition.substring(0, equals),
						content, locale);
				String right = parseValue(
						condition.substring(equals + 1, deuxParams),
						content, locale);
				String rightright = parseValue(
						condition.substring(deuxParams+1, condition.length()),
						content, locale);
				isTrue = compare(left, right);
					if (isTrue==false){
						isTrue = compare(left, rightright);
					}
			}

		}

		private boolean compare(String left, String right) {
			return left.toLowerCase(Locale.ENGLISH).trim()
					.equals(right.toLowerCase(Locale.ENGLISH).trim());
		}

		public boolean isTrue() {
			return isTrue;
		}

	}
}
