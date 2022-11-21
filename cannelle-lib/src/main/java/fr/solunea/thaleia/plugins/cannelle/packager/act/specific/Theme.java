package fr.solunea.thaleia.plugins.cannelle.packager.act.specific;

import java.util.List;

import org.apache.log4j.Logger;

import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.plugins.cannelle.contents.ExportedModule;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.utils.DetailedException;

/**
 * On va mettre à jour (remplacer les valeurs) des paramètres des thèmes
 * ('simulation', 'forepo+advanced_skill', ...) en y listant les identifiants de
 * contenus, en fonction de leur attribut 'theme' attendu comme attribut de la
 * balises content. <br/>
 * <br/>
 * Les valeurs sont de la forme : {@code 14[,]21[,]22 } <br/>
 * Tout se fait dans le ACT : le module est ignoré.
 * 
 */
public class Theme extends AbstractSpecificTreatment {

	private static final Logger logger = Logger.getLogger(Theme.class);

	public Theme(ResourcesHandler resourcesHandler, ExportedModule module,
			Parameters parameters, String specificParamsStartsWith,
			Locale locale) throws DetailedException {
		super(resourcesHandler, module, parameters, specificParamsStartsWith,
				locale);
	}

	@Override
	public void run() throws DetailedException {
		try {
			runFor("Delivery line 06", "delivery_line");
			runFor("Variation Line", "variation_line");
			runFor("Simulation", "simulation");
			runFor("Netting agreement", "netting_agreement");
			runFor("What if ? / Policy", "what_if_policy");
			runFor("Other credit risk", "other_credit_risk");

			logger.debug("Enregistrement des modifications dans le ACT...");
			getModule().getAct().commit();
		} catch (DetailedException e) {
			e.addMessage("Impossible d'effectuer le traitement spécifique '"
					+ this.getClass().getName() + "'.");
			throw e;
		}

	}

	/**
	 * Effectue le traitement pour l'attribut contentAttributeValue, et stocke
	 * le résultat dans le paramètre parameterAttributeValue.
	 * 
	 * @param contentAttributeValue
	 * @param parameterAttributeValue
	 * @throws DetailedException
	 */
	private void runFor(String contentAttributeValue,
			String parameterAttributeValue) throws DetailedException {
		logger.debug("Recherche des contenus pour le thème '"
				+ contentAttributeValue + "'...");
		List<String> contents = SpecificUtils.getInstance().getContents(
				getModule().getAct(), "theme", contentAttributeValue);
		logger.debug("Création de la balise de valeur de paramètre pour '"
				+ parameterAttributeValue + "'...");
		String value = SpecificUtils.getInstance().formatContentsList(contents);
		logger.debug("Valeur de paramètre pour '" + parameterAttributeValue
				+ "' : '" + value + "'");
		getModule().getAct().setProperty(parameterAttributeValue, value, false);
	}
}
