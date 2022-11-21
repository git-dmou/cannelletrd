package fr.solunea.thaleia.plugins.cannelle.parsers.ppt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.poi.xslf.usermodel.XMLSlideShow;

import fr.solunea.thaleia.plugins.cannelle.messages.LocalizedMessages;
import fr.solunea.thaleia.plugins.cannelle.service.ParametersAwareService;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;

public class AbstractPptParserService extends ParametersAwareService {

	public AbstractPptParserService(Parameters parameters,
			ResourcesHandler resourcesHandler) {
		super(parameters, resourcesHandler);
		// TODO Auto-generated constructor stub
	}

	protected static final Logger logger = Logger
			.getLogger(PptModuleParserService.class);
	private File pptFile;

	public File getPptFile() {
		return pptFile;
	}

	public void setPptFile(File pptFile) {
		this.pptFile = pptFile;
	}

	protected XMLSlideShow getPptSlideShow() throws DetailedException,
			FileNotFoundException, IOException {
		// On initialise le fichier Ppt avec le fichier transmis
		logger.debug("Initialisation du fichier PPT");
		setPptFile(getPptFileFromResourcesHandler());
		return new XMLSlideShow(new FileInputStream(pptFile.getAbsolutePath()));
	}

	/**
	 * Initialise le paramètre pptFile avec le fichier PPT contenus dans les
	 * ressources transmises au service
	 * 
	 * @param resourcesHandler
	 * @return
	 * @throws DetailedException
	 */
	protected File getPptFileFromResourcesHandler() throws DetailedException {
		File pptFile;
		String[] extensions = { "ppt", "pptx" };

		boolean recursive = true;
		// On parcours les ressources et on liste tous les fichiers Ppt
		Collection<File> pptFiles = getResourcesHandler().getUploadedFiles()
				.listFiles(extensions, recursive);
		logger.debug("Trouvé " + pptFiles.size()
				+ " fichier(s) PPT dans les fichiers uploadés.");

		if (pptFiles.isEmpty()) {
			// Le message localisé correspondant
			String message = LocalizedMessages.getMessage(
					LocalizedMessages.NO_XLS_ERROR, new Object[] {});
			// On demande sa présentation
			ThaleiaSession.get().addError(message);

			throw new DetailedException(
					"Aucun fichier PPT n'a été trouvé dans l'archive !");

		} else if (pptFiles.size() > 1) {
			// Le message localisé correspondant
			String message = LocalizedMessages.getMessage(
					LocalizedMessages.MULTIPLE_XLS_ERROR, new Object[] {});
			// On demande sa présentation
			ThaleiaSession.get().addError(message);

			throw new DetailedException(
					"Plusieurs fichiers PPT ont été trouvés dans l'archive !");
		} else {
			// On prend le premier fichier ppt trouvé.
			pptFile = pptFiles.iterator().next();
			logger.debug("On utilise le fichier '" + pptFile.getAbsolutePath()
					+ "' comme spécifications.");
		}
		return pptFile;
	}
}
