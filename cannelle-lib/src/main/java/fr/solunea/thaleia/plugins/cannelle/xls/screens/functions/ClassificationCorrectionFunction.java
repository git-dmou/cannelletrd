package fr.solunea.thaleia.plugins.cannelle.xls.screens.functions;

import fr.solunea.thaleia.plugins.cannelle.contents.A7File;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.ClassificationParameter;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.IScreenParameter;
import fr.solunea.thaleia.service.utils.FilesUtils;
import fr.solunea.thaleia.utils.DetailedException;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Utilise une table de propositions (nom de proposition/id) et de catégories
 * (nom de catégorie/id) pour être capable de remplacer l'id de la catégorie
 * correcte pour chaque proposition.
 * 
 */
public class ClassificationCorrectionFunction extends AbstractFunction {

	private static final Logger logger = Logger.getLogger(ClassificationCorrectionFunction.class);

	/**
	 * Nom de proposition / Id
	 */
	private Map<String, String> propositions;
	/**
	 * Nom de catégorie / Id
	 */
	private Map<String, String> categories;

	@Override
	public void run(Map<String, IScreenParameter> screenParametersMap, Parameters parameters, String assetsDirName,
			ResourcesHandler resourcesHandler, A7File a7) throws DetailedException {

		// On recherche les paramètres qui contiennent les noms des propositions
		// Ils DOIVENT être des ClassificationParameter
		for (String propositionName : propositions.keySet()) {
			try {
				ClassificationParameter propositionParameter = (ClassificationParameter) screenParametersMap
						.get(propositionName);
				if (propositionParameter == null) {
					throw new Exception("Il n'existe pas.");
				}
				// On a une proposition
				// La correction de la proposition
				String propositionCorrection = propositions.get(propositionName);
				// On recherche sa catégorie
				String categoryName = propositionParameter.getCategorie();
				// On recherche l'id de cette catégorie
				String categoryId = categories.get(categoryName);

				// On procède au remplacement de la correction de la proposition
				// dans le modèle ("anchor_item_1) par l'id de cette catégorie
				// dans le modèle ("anchor_5").
				FilesUtils.replaceAllInFile(propositionCorrection, categoryId, a7.getFile(), A7File.getEncoding());

			} catch (Exception e) {
				logger.debug("Impossible de retrouver le ClassificationParameter '" + propositionName + "' : " + e);
			}
		}

	}

	public ClassificationCorrectionFunction(Map<String, String> propositions, Map<String, String> categories) {
		this.propositions = propositions;
		this.categories = categories;
	}

}
