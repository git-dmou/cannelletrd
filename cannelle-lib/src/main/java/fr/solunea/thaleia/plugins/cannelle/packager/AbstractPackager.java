package fr.solunea.thaleia.plugins.cannelle.packager;

import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;

public abstract class AbstractPackager implements IFormatPackager {

	private final Parameters parameters;
	private final ResourcesHandler resourcesHandler;

	public AbstractPackager(Parameters parameters,
			ResourcesHandler resourcesHandler) {
		this.parameters = parameters;
		this.resourcesHandler = resourcesHandler;
	}

	public Parameters getParameters() {
		return parameters;
	}


	public ResourcesHandler getResourcesHandler() {
		return resourcesHandler;
	}

}
