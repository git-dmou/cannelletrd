package fr.solunea.thaleia.plugins.cannelle.xls.screens.generator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.IScreenParameter;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.StaticParameter;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.TextParameter;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.TranslateValueParameter;
import fr.solunea.thaleia.utils.DetailedException;

public class AbstractContentGenerator {

	private static final Logger logger = Logger
			.getLogger(AbstractContentGenerator.class);

	private ResourcesHandler resources;
	private Parameters parameters;
	private String parametersPrefix = "";

	public ResourcesHandler getResourcesHandler() throws DetailedException {
		if (resources == null) {
			throw new DetailedException(
					"L'accès au ressources n'a pas été initialisé pour cet objet !");
		}
		return resources;
	}

	public void setResourcesHandler(ResourcesHandler resources) {
		this.resources = resources;
	}

	public Parameters getParameters() {
		return parameters;
	}

	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return Dans les paramètres de ce générateur de contenu, le préfixe des
	 *         clés qui le concernent. Par exemple, si les paramètres
	 *         contiennent la clé
	 *         'templates.1.contentgenerator.class=fr.solunea.Toto', et que le
	 *         contentGenerator a été initialisé dans le template1, alors le
	 *         préfixe est 'templates.1.contentgenerator.'. Ce préfixe doit être
	 *         fixé lors du parsing du fichier Excel.
	 */
	public String getParametersPrefix() {
		return parametersPrefix;
	}

	/**
	 * @param parametersPrefix
	 */
	public void setParametersPrefix(String parametersPrefix) {
		this.parametersPrefix = parametersPrefix;
	}

	/**
	 * Interprète ces ScreenParameters, pour en faire la liste des propriétés de
	 * cet écran, c'est à dire des couples clé / valeurs qu'il faut conserver
	 * pour décrire cet écran.
	 * 
	 * Seuls les ScreenParameters de type Texte sont conservés, et ceux qui
	 * doivent être stockés sous un autre nom sont renommés.
	 * 
	 * @param screenParameters
	 * @return
	 */
	protected Map<String, String> parseScreenParameters(
			List<IScreenParameter> screenParameters) {

		Map<String, String> result = new HashMap<String, String>();

		logger.debug("Analyse de " + screenParameters.size()
				+ " screenParameters pour en faire des propriétés d'écran.");

		for (IScreenParameter screenParameter : screenParameters) {
			if (TextParameter.class
					.isAssignableFrom(screenParameter.getClass())
					|| TranslateValueParameter.class
							.isAssignableFrom(screenParameter.getClass())
					|| StaticParameter.class.isAssignableFrom(screenParameter
							.getClass())) {
				String key = screenParameter.getProperty("name", "");
				String value = screenParameter.getValue();

				String contentPropertyName = screenParameter
						.getContentPropertyName();

				if (contentPropertyName == null) {
					// Si la propriété n'a pas un nom de contentProperty,
					// c'est le nom "name", = celui présenté dans la zone de
					// cellules du Excel qu'on utilise
					result.put(key, value);
					logger.debug("On stocke la propriété de cet écran sous son nom : "
							+ key + " : " + value);

				} else {
					// Si la propriété a un nom de contentProperty, c'est ce
					// nom qu'on utilise
					result.put(contentPropertyName, value);
					logger.debug("On stocke la propriété de cet écran sous le nom de sa ContentProperty : "
							+ contentPropertyName + " : " + value);
				}
			}

			// Si le paramètre contient un commentaire à stocker dans une
			// propriété, on la stocke.
			if (TextParameter.class
					.isAssignableFrom(screenParameter.getClass())) {
				String contentPropertyName = ((TextParameter) screenParameter)
						.getCommentPropertyName();
				String value = ((TextParameter) screenParameter)
						.getCommentPropertyValue();

				if (!contentPropertyName.isEmpty()) {
					result.put(contentPropertyName, value);
					logger.debug("On stocke la propriété (issue d'un commentaire) de cet écran sous son nom : "
							+ contentPropertyName + " : " + value);

				}
			}
		}
		return result;
	}
}
