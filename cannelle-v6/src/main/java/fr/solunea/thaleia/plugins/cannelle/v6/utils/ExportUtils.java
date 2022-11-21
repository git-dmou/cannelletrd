package fr.solunea.thaleia.plugins.cannelle.v6.utils;

import fr.solunea.thaleia.model.ContentVersion;
import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.model.User;
import fr.solunea.thaleia.plugins.cannelle.service.ExportModuleService;
import fr.solunea.thaleia.plugins.cannelle.service.ResourceHandlerService;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.v6.CannelleV6Plugin;
import fr.solunea.thaleia.service.utils.export.ExportFormat;
import fr.solunea.thaleia.utils.DetailedException;

import java.io.File;
import java.util.List;

public class ExportUtils {

    /**
     * @param errorMessagesKeys la liste des messages d'erreur dans laquelle ajouter les clés
     *                          des messages des erreurs produites pendant l'export.
     * @param user
     * @return le fichier exporté
     */
    public static File exportModule(ContentVersion version, Locale locale, List<String> errorMessagesKeys, ExportFormat format, User user) throws DetailedException {

        // Initialisation des paramètres de traitement
        Parameters parameters;
        try {
            parameters = new CannelleV6Plugin().getParameters();

        } catch (DetailedException e) {
            throw e.addMessage("Impossible d'initialiser les paramètres.");
        }

        // Initialisation de l'accès aux ressources
        ResourcesHandler resourcesHandler;
        try {
            // Initialisation du RessourcesHandler, avec la
            // bonne configuration du plugin à utiliser (vanilla
            // etc.)
            ResourceHandlerService resourceHandlerService = new ResourceHandlerService(parameters,
                    CannelleV6Plugin.class);
            resourcesHandler = resourceHandlerService.getResourcesHandler(version, locale);

        } catch (DetailedException e) {
            throw e.addMessage("Impossible de préparer l'accès aux ressources.");
        }

        // Le service d'export
        ExportModuleService exportModuleService = new ExportModuleService(parameters, resourcesHandler);

        return exportModuleService.exportModule(version, locale, errorMessagesKeys, format, user);
    }
}
