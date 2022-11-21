package fr.solunea.thaleia.plugins.cannelle.packager;

import fr.solunea.thaleia.model.User;
import fr.solunea.thaleia.plugins.cannelle.contents.ExportedModule;
import fr.solunea.thaleia.service.utils.export.ExportFormat;
import fr.solunea.thaleia.utils.DetailedException;

public interface IFormatPackager {

    void run(ExportedModule module, ExportFormat format, User user) throws DetailedException;

}
