package fr.solunea.thaleia.plugins.cannelle.packager.treatment;

import fr.solunea.thaleia.model.User;
import fr.solunea.thaleia.plugins.cannelle.contents.ExportedModule;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.service.utils.export.ExportFormat;
import fr.solunea.thaleia.utils.DetailedException;
import org.apache.log4j.Logger;

/**
 * Un traitement sur le module à exporter.
 */
public abstract class Treatment {

    protected final static Logger logger = Logger.getLogger(Treatment.class);

    protected final ExportedModule module;

    public Treatment(ExportedModule module) {
        this.module = module;
    }

    protected ExportedModule getModule() {
        return this.module;
    }

    /**
     * Exécution de la commande.
     */
    public abstract void execute(Parameters parameters, ResourcesHandler resourcesHandler, ExportFormat exportFormat,
                                 User user) throws DetailedException;
}
