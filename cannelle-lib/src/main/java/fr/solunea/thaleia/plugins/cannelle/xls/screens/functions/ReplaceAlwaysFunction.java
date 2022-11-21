package fr.solunea.thaleia.plugins.cannelle.xls.screens.functions;

import fr.solunea.thaleia.plugins.cannelle.contents.A7File;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.IScreenParameter;
import fr.solunea.thaleia.utils.DetailedException;

import java.util.Map;

/**
 * Comme ReplaceIf, mais effectue toujours le traitement, sans vérifier la
 * condition.
 * 
 */
public class ReplaceAlwaysFunction extends ReplaceIfFunction {

	public ReplaceAlwaysFunction(String steps, String zones, String lines,
			String toReplace, String replaceValue) {
		super("", "", "", steps, zones, lines, toReplace, replaceValue);
	}

	@Override
	public void run(Map<String, IScreenParameter> screenParametersMap,
			Parameters parameters, String assetsDirName,
			ResourcesHandler resourcesHandler, A7File a7)
			throws DetailedException {

		// Remplace les nom des paramètres par leur valeur
		updateValues(screenParametersMap, parameters);

		// Pas de test : on exécute dans tous les cas.
		deleteAndReplace(a7, parameters);
	}

}
