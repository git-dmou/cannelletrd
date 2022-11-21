package fr.solunea.thaleia.plugins.cannelle.service;

import java.io.InputStream;

import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.ThaleiaApplication;

public class ParametersAwareService {

	/**
	 * Dans le JAR, le nom du répertoire qui contient les fichiers de paramètres
	 * (*.properties).
	 */
	private static final String DEFAULT_PARAMETERS_DIR = "Properties";

	private Parameters parameters;
	private ResourcesHandler resourcesHandler;

	public ParametersAwareService(Parameters parameters,
			ResourcesHandler resourcesHandler) {
		this.parameters = parameters;
		this.resourcesHandler = resourcesHandler;
	}

	public Parameters getParameters() {
		return this.parameters;
	}

	public ResourcesHandler getResourcesHandler() {
		return resourcesHandler;
	}

	protected void setResourcesHandler(ResourcesHandler resourcesHandler) {
		this.resourcesHandler = resourcesHandler;
	}

	/**
	 * Initialise les paramètres, en prenant en entrée le fichier qui porte le
	 * nom demandé dans le répertoire du plugin qui contient les fichiers de
	 * paramètres.
	 * 
	 * @param parametersFileName
	 *            le nom du fichier de paramètres, par exemple :
	 *            defaultparams_export_cdrom.properties
	 * @throws DetailedException
	 */
	public void setParametersFormFile(String parametersFileName)
			throws DetailedException {
		try {
			InputStream is = ThaleiaApplication
					.get()
					.getApplicationSettings()
					.getClassResolver()
					.getClassLoader()
					.getResourceAsStream(
							DEFAULT_PARAMETERS_DIR + "/" + parametersFileName);

			parameters = new Parameters(is, null);

		} catch (Exception e) {
			throw new DetailedException(e)
					.addMessage("Impossible d'initialiser les paramètres avec le fichier '"
							+ parametersFileName + "'.");
		}
	}
}
