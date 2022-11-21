package fr.solunea.thaleia.plugins.cannelle.packager.act.container;

import fr.solunea.thaleia.model.User;
import fr.solunea.thaleia.plugins.cannelle.contents.ExportedModule;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.service.utils.export.ExportFormat;
import fr.solunea.thaleia.utils.DetailedException;

public interface IContainer {

    /**
     * Ajoute les fichiers du conteneur aux fichiers du ACT, et les configure si
     * besoin en fonction des paramètres de requête.
     */
    void addFiles(Parameters parameters, ExportedModule module, ExportFormat format, User user) throws DetailedException;

    /**
     * Transforme le conteneur en exécutable.
     */
    void transformToExe(Parameters parameters, ExportedModule module) throws DetailedException;

}
