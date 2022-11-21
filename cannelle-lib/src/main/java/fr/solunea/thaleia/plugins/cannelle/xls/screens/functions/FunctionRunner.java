package fr.solunea.thaleia.plugins.cannelle.xls.screens.functions;

import fr.solunea.thaleia.plugins.cannelle.contents.A7File;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.IScreenParameter;
import fr.solunea.thaleia.utils.DetailedException;

import java.util.Map;

/**
 * Permet d'exécuter une fonction selon son type, en lui transmettant les bons
 * paramètres initiaux, et de traiter d'éventuels traitements post-fonction.
 * 
 */
public class FunctionRunner {

	private final Map<String, IScreenParameter> screenParametersMap;
	private final Parameters parameters;
	private final String assetsDirName;
	private final ResourcesHandler resourcesHandler;
	private final A7File a7;

	/**
	 * Exécuteur de fonctions sur ce fichier A7.
	 * 
	 * @param screenParametersMap
	 * @param parameters
	 * @param assetsDirName
	 *            en cas de copie de médias durant l'exécution d'une fonction,
	 *            le nom du répertoire dans lequel stocker les médias d'un A7.
	 * @param resourcesHandler
	 * @param a7
	 *            le A7 à traiter par les fonctions
	 */
	public FunctionRunner(Map<String, IScreenParameter> screenParametersMap,
			Parameters parameters, String assetsDirName,
			ResourcesHandler resourcesHandler, A7File a7) {
		this.screenParametersMap = screenParametersMap;
		this.assetsDirName = assetsDirName;
		this.parameters = parameters;
		this.resourcesHandler = resourcesHandler;
		this.a7 = a7;
	}

	public void run(AbstractFunction function) throws DetailedException {
		function.run(screenParametersMap, parameters, assetsDirName,
				resourcesHandler, a7);
	}

}
