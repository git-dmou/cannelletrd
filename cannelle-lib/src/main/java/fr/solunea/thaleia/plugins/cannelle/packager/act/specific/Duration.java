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
import fr.solunea.thaleia.service.utils.scorm.ScormUtils;
import fr.solunea.thaleia.utils.DetailedException;

/**
 * Dans la chaîne de remplacement, interprète la valeur à remplacer en secondes,
 * et la formatte en heures minutes secondes : (X heures X minutes X secondes Si
 * 
 * @@actDuration, alors la valeur en secondes prise sera celle décrite dans le
 *                Act.
 * 
 */
public class Duration extends AbstractSpecificTreatment {

	private static final Logger logger = Logger.getLogger(Duration.class);

	public Duration(ResourcesHandler resourcesHandler, ExportedModule module,
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
			String replace = getSpecificValue("replace");
			// On interprète la chaîne reçue
			if ("@@actDuration".equals(replace)) {
				replace = ScormUtils.secondsToString(getModule().getAct()
						.getDuration(), getModule().getLocale());
			} else {
				replace = ScormUtils.secondsToString(new Integer(replace),
						getModule().getLocale());
			}

			// L'encodage des caractères du fichier
			String encoding = getSpecificValue("fileencoding");

			if (file.exists()) {
				logger.debug("Fichier traité : '" + file.getAbsolutePath()
						+ "', remplacement de " + find + " par " + replace);
				FilesUtils.replaceAllInFile(find, replace, file, encoding);

			} else {
				throw new DetailedException(
						"Impossible de modifier la valeur '"
								+ find
								+ "' par '"
								+ replace
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
}
