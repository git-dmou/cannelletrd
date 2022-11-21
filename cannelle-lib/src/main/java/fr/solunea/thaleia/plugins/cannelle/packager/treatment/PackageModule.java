package fr.solunea.thaleia.plugins.cannelle.packager.treatment;

import fr.solunea.thaleia.model.User;
import fr.solunea.thaleia.plugins.cannelle.contents.ExportedModule;
import fr.solunea.thaleia.plugins.cannelle.packager.IFormatPackager;
import fr.solunea.thaleia.plugins.cannelle.packager.PackageFormaterFactory;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.service.utils.export.ExportFormat;
import fr.solunea.thaleia.utils.DetailedException;

public class PackageModule extends Treatment {

    public PackageModule(ExportedModule module) {
        super(module);
    }

    @Override
    public void execute(Parameters parameters, ResourcesHandler resourcesHandler, ExportFormat exportFormat,
                        User user) throws DetailedException {

        // Recherche du type de paquetage demandé
        String format = parameters.getValue(Parameters.EXPORT_FORMAT);

        IFormatPackager packager = PackageFormaterFactory.getInstance().getPackager(format, parameters,
                resourcesHandler);
        packager.run(module, exportFormat, user);
    }

}
