package fr.solunea.thaleia.plugins.cannelle.packager.treatment;

import fr.solunea.thaleia.model.User;
import fr.solunea.thaleia.plugins.cannelle.contents.ExportedModule;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.service.utils.export.ExportFormat;
import fr.solunea.thaleia.utils.DetailedException;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Fabrique le répertoire où sera exporté le module
 */
public class PrepareExportDirectory extends Treatment {

    public PrepareExportDirectory(ExportedModule module) {
        super(module);
    }

    @Override
    public void execute(Parameters parameters, ResourcesHandler resourcesHandler, ExportFormat exportFormat, User user) throws
			DetailedException {

        // Préparation du répertoire temporaire où sera préparé le paquet
        File destinationDir = new File(resourcesHandler.getTempDir().getAbsolutePath() + File.separator
                + getModule().getVersion().getContentIdentifier());

        logger.debug("Création du répertoire de préparation des contenus : " + destinationDir.getAbsolutePath());
        try {
            FileUtils.deleteQuietly(destinationDir);
            FileUtils.forceMkdir(destinationDir);

        } catch (Exception e) {
            throw new DetailedException(e).addMessage("Impossible de créer le répertoire d'export.");
        }

        if (!destinationDir.exists()) {
            throw new DetailedException(
                    "Impossible de créer le répertoire '" + destinationDir.getAbsolutePath() + "' !");
        }

        module.setExportDir(destinationDir);
    }

}
