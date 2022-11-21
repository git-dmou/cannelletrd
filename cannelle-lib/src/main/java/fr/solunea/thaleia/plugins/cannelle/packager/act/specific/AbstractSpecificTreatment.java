package fr.solunea.thaleia.plugins.cannelle.packager.act.specific;

import java.io.File;

import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.plugins.cannelle.contents.ExportedModule;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.utils.DetailedException;

public abstract class AbstractSpecificTreatment implements ISpecificTreatment {

	final private ResourcesHandler resourcesHandler;
	final private ExportedModule module;
	final private Parameters parameters;
	final private String specificParamsStartsWith;
	final private Locale locale;

	/**
	 * @param resourcesHandler
	 *            L'accès aux ressources du répertoire où sont les fichiers de
	 *            configuration du plugin
	 * @param act
	 *            Le ACT sur lequel s'effectuera le traitement.
	 * @param module
	 *            Le module sur lequel s'effectuera le traitement.
	 * @param parameters
	 *            Les paramètres de traitement
	 * @param specificParamsStartsWith
	 *            les paramètres destinés à cette instance de traitement
	 *            commencent tous par cette valeur (par exemple :
	 *            'format.act.container.specific.[n° d'ordre].'). Cette chaîne
	 *            sera utilisée par la classe de traitement pour récupérer ses
	 *            propres paramètres.
	 * @throws DetailedException
	 */
	public AbstractSpecificTreatment(ResourcesHandler resourcesHandler,
			ExportedModule module, Parameters parameters,
			String specificParamsStartsWith, Locale locale)
			throws DetailedException {

		// On ne teste pas si resourcesHandler, act et module sont nul, afin de
		// permettre l'utilisation de la classe sans forcément avoir besoin de
		// ces objets.

		this.resourcesHandler = resourcesHandler;
		this.module = module;
		this.parameters = parameters;
		this.specificParamsStartsWith = specificParamsStartsWith;
		this.locale = locale;
	}

	/**
	 * @param specificParam
	 * @return La valeur du paramètre spécifique à cette instance de traitement,
	 *         ou "" si aucune valeur n'est définie. Par exemple :
	 *         {@code getSpecificValue("toto") } renvoie la valeur du paramètre
	 *         {@code format.act.container.specific.[n° d'ordre].toto }
	 */
	protected String getSpecificValue(String specificParam) {
		return parameters.getValue(this.specificParamsStartsWith + "."
				+ specificParam);

	}

	protected String getValue(String specificParam) {
		return parameters.getValue(specificParam);

	}

	protected ResourcesHandler getResourcesHandler() {
		return this.resourcesHandler;
	}

	protected ExportedModule getModule() {
		return this.module;
	}

	protected Parameters getParameters() {
		return this.parameters;
	}

	protected Locale getLocale() {
		return this.locale;
	}

	/**
	 * @param path
	 *            l'url relative du fichier, dans le répertoire des ressources
	 *            (configuration) du plugin
	 * @return le fichier demandé
	 * @throws DetailedException
	 */
	protected File getFile(String path) throws DetailedException {
		return resourcesHandler.getResourceFile(path);
	}

}
