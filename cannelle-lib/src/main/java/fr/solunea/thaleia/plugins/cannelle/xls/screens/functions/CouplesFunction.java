package fr.solunea.thaleia.plugins.cannelle.xls.screens.functions;

import fr.solunea.thaleia.plugins.cannelle.contents.A7File;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.ClassificationParameter;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.IScreenParameter;
import fr.solunea.thaleia.service.utils.FilesUtils;
import fr.solunea.thaleia.utils.DetailedException;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilise une table de propositions (nom de proposition/index) et de catégories
 * (catégorie/index) pour être capable de générer un tableau de bonne réponses,
 * sous forme de couples proposition.catégorie.
 * 
 */
public class CouplesFunction extends AbstractFunction {

	private static final Logger logger = Logger
			.getLogger(CouplesFunction.class);

	private String coupleSeparator;
	private String couplesSeparator;
	/**
	 * Nom de proposition / Index
	 */
	private Map<String, String> propositions;
	/**
	 * Nom de catégorie / Index
	 */
	private Map<String, String> categories;
	private String replace;

	@Override
	public void run(Map<String, IScreenParameter> screenParametersMap,
			Parameters parameters, String assetsDirName,
			ResourcesHandler resourcesHandler, A7File a7)
			throws DetailedException {

		// La liste des couples
		Couples couples = new Couples();

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
				// L'index de la proposition
				String propositionIndex = propositions.get(propositionName);
				// On recherche sa catégorie
				String categoryName = propositionParameter.getCategorie();
				// On recherche l'index de cette catégorie
				String categoryIndex = categories.get(categoryName);
				// On ajoute le couple si la catégorie existe
				if (categoryIndex != null && !categoryIndex.isEmpty()) {
					couples.add(propositionIndex, categoryIndex);
				}

			} catch (Exception e) {
				logger.debug("Impossible de retrouver le ClassificationParameter '"
						+ propositionName + "' : " + e);
			}
		}

		logger.debug("Couples définissant la correction = "
				+ couples.toString());

		// On procède au remplacement de la définition des couples
		FilesUtils.replaceAllInFile(replace, couples.toString(), a7.getFile(),
				A7File.getEncoding());

	}

	public CouplesFunction(String coupleSeparator, String couplesSeparator,
			Map<String, String> propositions, Map<String, String> categories,
			String replace) {
		this.coupleSeparator = coupleSeparator;
		this.couplesSeparator = couplesSeparator;
		this.propositions = propositions;
		this.categories = categories;
		this.replace = replace;
	}

	private class Couples {

		private Map<String, String> couples = new HashMap<String, String>();

		public void add(String left, String right) {
			couples.put(left, right);
		}

		@Override
		public String toString() {
			StringBuffer result = new StringBuffer();

			boolean firstCouple = true;

			for (String left : couples.keySet()) {
				String right = couples.get(left);

				if (firstCouple) {
					firstCouple = false;
				} else {
					result.append(couplesSeparator);
				}

				result.append(left);
				result.append(coupleSeparator);
				result.append(right);

			}
			return result.toString();
		}

	}

}
