package fr.solunea.thaleia.plugins.cannelle.packager.act.container;

import java.io.File;

import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.utils.DetailedException;

public abstract class AbstractContainer implements IContainer {

	/**
	 * L'accès aux ressources du répertoire où sont les fichiers de
	 * configuration du plugin
	 */
	final private ResourcesHandler resourcesHandler;

	/**
	 * @param resourcesHandler
	 *            L'accès aux ressources du répertoire où sont les fichiers de
	 *            configuration du plugin
	 * @throws DetailedException
	 */
	protected AbstractContainer(ResourcesHandler resourcesHandler)
			throws DetailedException {

		if (resourcesHandler == null) {
			throw new DetailedException("L'accès aux ressources est nul !");
		}

		this.resourcesHandler = resourcesHandler;
	}

	protected ResourcesHandler getResourcesHandler() {
		return this.resourcesHandler;
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
