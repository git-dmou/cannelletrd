/**
 * Le code source, le matériel préparatoire et la documentation de ce
 * logiciel sont la propriété exclusive de la société Solunea, au titre
 * du droit de propriété intellectuelle. Ces éléments ont fait l'objet 
 * de dépôts probatoires.
 *  
 * À défaut d'accord préalable écrit de Solunea, vous ne devez pas
 * utiliser, copier, modifier, traduire, créer une œuvre dérivée,
 * transmettre, vendre ou distribuer, de manière directe ou indirecte,
 * inverser la conception ou l'assemblage ou tenter de trouver le code
 * source (sauf cas prévus par la loi), ou transférer tout droit relatif
 * audit logiciel.
 *  
 * Solunea
 * SARL - N° SIRET 48795234300027
 *
 */
package fr.solunea.thaleia.plugins.cannelle.packager.act.specific;

import java.util.List;

import org.apache.log4j.Logger;

import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.plugins.cannelle.contents.ExportedModule;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.utils.DetailedException;

/**
 * @author RMAR
 * 
 *         On va mettre à jour (remplacer les valeurs) des paramètres
 *         'difficulty_level_1' et 'difficulty_level_2' en y listant les
 *         identifiants de contenus, en fonction de leur attribut 'difficulty'
 *         attendu comme attribut de la balises content. <br/>
 * <br/>
 *         On ne tient en compte que les valeurs de difficulté 1 et 2. <br/>
 *         Les valeurs sont de la forme : {@code 14[,]21[,]22 } <br/>
 *         Tout se fait dans le ACT : le module est ignoré.
 */
public class Difficulty extends AbstractSpecificTreatment {

	private static final Logger logger = Logger.getLogger(Difficulty.class);

	public Difficulty(ResourcesHandler resourcesHandler, ExportedModule module,
			Parameters parameters, String specificParamsStartsWith,
			Locale locale) throws DetailedException {
		super(resourcesHandler, module, parameters, specificParamsStartsWith,
				locale);
	}

	@Override
	public void run() throws DetailedException {

		try {
			logger.debug("Recherche des contenus pour la difficulté 1...");
			List<String> contentsDiff1 = SpecificUtils.getInstance()
					.getContents(getModule().getAct(), "difficulty", "1");
			logger.debug("Création de la balise de valeur de paramètre pour la difficulté 1...");
			String diff1Value = SpecificUtils.getInstance().formatContentsList(
					contentsDiff1);
			logger.debug("Valeur de paramètre pour la difficulté 1 : '"
					+ diff1Value + "'");
			getModule().getAct().setProperty("difficulty_level_1", diff1Value,
					false);

			logger.debug("Recherche des contenus pour la difficulté 2...");
			List<String> contentsDiff2 = SpecificUtils.getInstance()
					.getContents(getModule().getAct(), "difficulty", "2");
			logger.debug("Création de la balise de valeur de paramètre pour la difficulté 2...");
			String diff2Value = SpecificUtils.getInstance().formatContentsList(
					contentsDiff2);
			logger.debug("Valeur de paramètre pour la difficulté 2 : '"
					+ diff2Value + "'");
			getModule().getAct().setProperty("difficulty_level_2", diff2Value,
					false);

			logger.debug("Enregistrement des modifications dans le ACT...");
			getModule().getAct().commit();
		} catch (DetailedException e) {
			e.addMessage("Impossible d'effectuer le traitement spécifique '"
					+ this.getClass().getName() + "'.");
			throw e;
		}
	}

}
