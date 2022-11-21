package fr.solunea.thaleia.plugins.cannelle.packager.treatment;

import fr.solunea.thaleia.model.ContentProperty;
import fr.solunea.thaleia.model.ContentPropertyValue;
import fr.solunea.thaleia.model.ContentVersion;
import fr.solunea.thaleia.model.User;
import fr.solunea.thaleia.model.dao.ContentPropertyDao;
import fr.solunea.thaleia.model.dao.ContentPropertyValueDao;
import fr.solunea.thaleia.plugins.cannelle.contents.A7Screen;
import fr.solunea.thaleia.plugins.cannelle.contents.ExportedModule;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.service.ContentService;
import fr.solunea.thaleia.service.utils.export.ExportFormat;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.ThaleiaApplication;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Récupère tous les écrans du module, et leur binaire associé.
 */
public class RetrieveA7Screens extends Treatment {

    /**
     * Le nom de la ContentProperty qui correspond à un fichier de type A7.
     */
    public static final String A7_CONTENT = "A7Content";

    public RetrieveA7Screens(ExportedModule module) {
        super(module);
    }

    @Override
    public void execute(Parameters parameters, ResourcesHandler resourcesHandler, ExportFormat exportFormat, User user) throws
            DetailedException {
        try {
            List<A7Screen> a7List = new ArrayList<>();

            // Les objets d'appels à Thaleia
            ContentPropertyValueDao contentPropertyValueDao = new ContentPropertyValueDao(ThaleiaApplication.get().getConfiguration().getLocalDataDir().getAbsolutePath(), ThaleiaApplication.get().getConfiguration().getBinaryPropertyType(), user.getObjectContext());
            ContentService contentService = ThaleiaSession.get().getContentService();

            // On recherche la propriété qui correspond au fichier A7 d'un
            // contenu.
            ContentProperty contentPropertyA7File = new ContentPropertyDao(user.getObjectContext()).findByName(A7_CONTENT);

            // On récupère les contenus du module définis en base
            List<fr.solunea.thaleia.model.Content> contents = contentService.getContentsFromModule(user, module.getVersion());

            for (fr.solunea.thaleia.model.Content content : contents) {

                // On recupère la dernière version de chacun des contenus
                ContentVersion contentVersion = content.getLastVersion();

                logger.debug("La dernière version de ce contenu est " + contentVersion.getContentIdentifier());

                List<ContentPropertyValue> contentPropertyValueA7Files = contentPropertyValueDao.find(contentVersion,
                        contentPropertyA7File, module.getLocale());
                if (contentPropertyValueA7Files.isEmpty()) {
                    throw new DetailedException("Le contenu '" + contentVersion.getContentIdentifier() + "' n'a "
                            + "aucune propriété de type a7 ('" + contentPropertyA7File.getName() + "') pour la locale "
                            + "" + module.getLocale() + " !");
                }

                ContentPropertyValue contentPropertyValueA7File = contentPropertyValueDao.find(contentVersion,
                        contentPropertyA7File, module.getLocale()).get(0);

                // On recherche le fichier Zip qui contient les binaires de ce
                // content (fichier a7, médias, etc.) dans la locale de l'export
                File zipContentBinary = contentPropertyValueDao.getFile(contentPropertyValueA7File);

                // On instancie un nouveau ScreenContent avec le contenu et ses
                // fichiers

                A7Screen screenContent = new A7Screen(contentVersion, // NOPMD
                        zipContentBinary);

                a7List.add(screenContent);
            }

            module.setScreens(a7List);

        } catch (Exception e) {
            throw new DetailedException(e).addMessage("Impossible de récupérer les écrans du module.");
        }
    }

}
