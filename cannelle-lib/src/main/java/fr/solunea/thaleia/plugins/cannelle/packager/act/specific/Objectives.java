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

import java.io.File;

import org.apache.log4j.Logger;

import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.plugins.cannelle.contents.ExportedModule;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.service.utils.FilesUtils;
import fr.solunea.thaleia.utils.DetailedException;

/**
 * Applique la note de passage (paramètre 'format.act.update.masteryscore') au
 * manifest et à l'écran de bilan.
 * 
 */
public class Objectives extends AbstractSpecificTreatment {

	private static final Logger logger = Logger.getLogger(Objectives.class);

	public Objectives(ResourcesHandler resourcesHandler, ExportedModule module,
			Parameters parameters, String specificParamsStartsWith,
			Locale locale) throws DetailedException {
		super(resourcesHandler, module, parameters, specificParamsStartsWith,
				locale);
	}

	@Override
	public void run() throws DetailedException {
		try {
			logger.debug("Mise à jour de l'écran de bilan...");

			// Fichier à traiter
			String filename = getSpecificValue("filename");
			File file = new File(getModule().getAct().getActFile()
					.getParentFile()
					+ File.separator + filename);
			// Chaîne à rechercher
			String find = getSpecificValue("find");
			// Chaîne de remplacement
			String replaceBy = getSpecificValue("replace");
			// Dans la chaîne de remplacement, on interprète les intervalles à
			// écrire
			replaceBy = "<unlocalized id=\"cps.certif.objectif\">"
					+ parseObjectives() + "</unlocalized>";
			// L'encodage des caractères du fichier
			String encoding = getSpecificValue("fileencoding");

			if (file.exists()) {
				logger.debug("Fichier traité : '" + file.getAbsolutePath()
						+ "'");
				FilesUtils.replaceAllInFile(find, replaceBy, file, encoding);

			} else {
				throw new DetailedException(
						"Impossible de modifier la valeur '"
								+ find
								+ "' par '"
								+ replaceBy
								+ "' dans le fichier '"
								+ file.getAbsolutePath()
								+ "' : il n'existe pas dans le paquet exporté !");
			}

		} catch (DetailedException e) {
			e.addMessage("Impossible d'effectuer le traitement spécifique '"
					+ this.getClass().getName() + "'.");
			throw e;
		}
	}

	private String parseObjectives() {
		// TODO cette méthode me semble un peu pas terrible quand même...
		String result = "";
		String objectif = "";
		for (int i = 3; i < 11; i++) {
			objectif = getParameters().getValue(
					Parameters.FORMAT_ACT_UPDATE_PROPERTY + "." + i + ".value");
			if (!"".equals(objectif)) {
				result = result + " -" + objectif + "[,]";
			}
		}
		return result;
	}
}
