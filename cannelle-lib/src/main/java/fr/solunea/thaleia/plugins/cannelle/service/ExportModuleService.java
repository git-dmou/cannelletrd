package fr.solunea.thaleia.plugins.cannelle.service;

import fr.solunea.thaleia.model.ContentVersion;
import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.model.User;
import fr.solunea.thaleia.plugins.cannelle.contents.ExportedModule;
import fr.solunea.thaleia.plugins.cannelle.packager.treatment.PackageModule;
import fr.solunea.thaleia.plugins.cannelle.packager.treatment.PrepareExportDirectory;
import fr.solunea.thaleia.plugins.cannelle.packager.treatment.RetrieveA7Screens;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.service.utils.FilenamesUtils;
import fr.solunea.thaleia.service.utils.ZipUtils;
import fr.solunea.thaleia.service.utils.export.ExportFormat;
import fr.solunea.thaleia.utils.DetailedException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.util.encoding.UrlEncoder;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.time.Duration;

import java.io.File;
import java.util.Calendar;
import java.util.List;

public class ExportModuleService extends ParametersAwareService {

    private static final Logger logger = Logger.getLogger(ExportModuleService.class);

    /**
     * Le nom du paramètre qui contient le message d'erreur en cas de problème
     * de préparation du répertoire d'export.
     */
    private static final String ERROR_PREPARE_EXPORT_DIR = "error.prepare.export.dir";

    /**
     * Le nom du paramètre qui contient le message d'erreur en cas de problème
     * de récupération des écrans.
     */
    private static final String ERROR_RETRIEVE_SCREENS = "error.retrieve.screens";

    /**
     * Le nom du paramètre qui contient le message d'erreur si le module demandé
     * ne peut pas être exporté.
     */
    private static final String ERROR_MODULE_CANNOT_BE_EXPORTED = "error.module.cannot.be.exported";

    /**
     * Le nom du paramètre qui contient le message d'erreur si une erreur de
     * paquetage a eu lieu.
     */
    private static final String ERROR_PACKAGING = "error.packaging";

    /**
     * Le nom du paramètre qui contient le message d'erreur pour une erreur
     * d'archivage.
     */
    private static final String ERROR_ARCHIVE = "error.archive";

    public ExportModuleService(Parameters parameters, ResourcesHandler resourcesHandler) {
        super(parameters, resourcesHandler);
    }

    /**
     * Procède à l'export du module, et renvoie la resource permettant de le
     * télécharger.
     */
    public ResourceStreamResource prepareModuleDownload(WebPage page, ContentVersion version, Locale locale,
                                                        List<String> errorMessagesKeys, ExportFormat format,
                                                        User user) throws DetailedException {

        // Export du module = production du binaire qui sera
        // transmis
        final File file = exportModule(version, locale, errorMessagesKeys, format, user);
        logger.debug("Le fichier exporté est préparé pour le téléchargement : " + file.getAbsolutePath());

        // On indique la préparation du package a réussi, et sa
        // taille
        Double size = file.length() / 1000000.0;
        Session.get().info(new StringResourceModel("module.file.size", page, null, new Object[]{size}).getString());

        // Le flux sur le fichier à transférer
        IResourceStream resourceStream = new FileResourceStream(new org.apache.wicket.util.file.File(file));

        // Préparation du nom de l'archive à transmettre
        String contentTitle;
        try {
            contentTitle = file.getName();
            // On supprime l'extension .zip
            if (contentTitle.endsWith(".zip")) {
                contentTitle = contentTitle.substring(0, contentTitle.lastIndexOf(".zip"));
            }
        } catch (Exception e) {
            logger.warn("Erreur de normalisation du nom du fichier à télécharger : " + e);
            contentTitle = "";
        }
        String horodate = DateFormatUtils.format(Calendar.getInstance(), "yyyyMMdd-HHmmss");
        String downloadFilename = contentTitle + "_" + horodate + ".zip";
        String encodedFileName = UrlEncoder.QUERY_INSTANCE.encode(downloadFilename, "UTF-8");
        logger.debug("Nom transmis pour le téléchargement : " + encodedFileName);

        // On prépare une ressource sur le binaire à transmettre

        return new ResourceStreamResource(resourceStream).setContentDisposition(ContentDisposition.ATTACHMENT).setCacheDuration(Duration.NONE).setFileName(encodedFileName);
    }

    public File exportModule(ContentVersion version, Locale locale, List<String> errorMessagesKeys,
                             ExportFormat format, User user) throws DetailedException {

        // logger.debug("Export du module " + version + " pour la locale "
        // + locale);
        if (format == ExportFormat.PUBLICATION_PLATFORM_DEFINED) {
            getParameters().addParameter("forceScorm2004", Boolean.toString(true));
        }

        // Instanciation du module à exporter
        ExportedModule module;
        try {
            module = new ExportedModule(version);
        } catch (DetailedException e) {
            errorMessagesKeys.add(ERROR_MODULE_CANNOT_BE_EXPORTED);
            e.addMessage("Impossible d'exporter ce contenu.");
            throw e;
        }

        // On fixe la locale demandée pour l'export de ce module
        module.setLocale(locale);

        // Préparation du répertoire d'export
        try {
            PrepareExportDirectory step = new PrepareExportDirectory(module);
            step.execute(getParameters(), getResourcesHandler(), format, user);

        } catch (DetailedException e) {
            errorMessagesKeys.add(ERROR_PREPARE_EXPORT_DIR);
            e.addMessage("Impossible de préparer le répertoire d'export.");
            throw e;
        }

        // Récupération des écrans du module
        try {
            RetrieveA7Screens step = new RetrieveA7Screens(module);
            step.execute(getParameters(), getResourcesHandler(), format, user);

        } catch (DetailedException e) {
            errorMessagesKeys.add(ERROR_RETRIEVE_SCREENS);
            e.addMessage("Impossible de récupérer les écrans de ce module.");
            throw e;
        }

        // Paquetage
        try {
            PackageModule step = new PackageModule(module);
            step.execute(getParameters(), getResourcesHandler(), format, user);

        } catch (DetailedException e) {
            errorMessagesKeys.add(ERROR_PACKAGING);
            e.addMessage("Une erreur a eu lieu durant le paquetage.");
            throw e;
        }

        // Compression du résultat
        File result;
        try {
            String resultPath = module.getDestinationDir().getParentFile().getAbsolutePath() + File.separator
                    + FilenamesUtils.getNormalizeString(module.getTitle(module.getIdentifier())) + ".zip";
            result = ZipUtils.toZip(module.getDestinationDir().getAbsolutePath(), resultPath);

        } catch (Exception e) {
            errorMessagesKeys.add(ERROR_ARCHIVE);
            throw new DetailedException(e).addMessage("Une erreur a eu lieu durant la compression.");
        }

        // Suppression des fichiers compressés
        try {
            logger.debug("Suppression du répertoire temporaire : " + module.getDestinationDir().getAbsolutePath());
            FileUtils.deleteDirectory(module.getDestinationDir());

        } catch (Exception e) {
            throw new DetailedException(e).addMessage("Les fichiers temporaires ne peuvent pas être supprimés.");
        }

        return result;
    }

}
