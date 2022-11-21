package fr.solunea.thaleia.plugins.cannelle.packager.treatment;

import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.model.User;
import fr.solunea.thaleia.model.dao.LocaleDao;
import fr.solunea.thaleia.plugins.cannelle.contents.ExportedModule;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.service.utils.export.ExportFormat;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;

/**
 * Recherche dans les paramètre d'export la locale demandée, et récupère l'objet
 * en base Thaleia correspondant.
 */
public class RetrieveLocale extends Treatment {

    public RetrieveLocale(ExportedModule module) {
        super(module);
    }

    @Override
    public void execute(Parameters parameters, ResourcesHandler resourcesHandler, ExportFormat exportFormat, User user) throws
			DetailedException {

        // Récupération de la locale demandée
        String localeName = parameters.getValue(Parameters.EXPORT_LANG);
        logger.debug("On recherche la locale '" + localeName + "' en base.");

        Locale locale = new LocaleDao(module.getVersion().getObjectContext()).findByName(localeName);
        if (locale == null) {
            throw new DetailedException("La locale '" + localeName + "' n'existe pas !");
        } else {
            module.setLocale(locale);
        }
    }

}
