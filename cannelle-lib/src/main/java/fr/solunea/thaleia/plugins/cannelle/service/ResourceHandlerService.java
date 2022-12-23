package fr.solunea.thaleia.plugins.cannelle.service;

import fr.solunea.thaleia.model.ContentProperty;
import fr.solunea.thaleia.model.ContentVersion;
import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.model.dao.ContentPropertyDao;
import fr.solunea.thaleia.model.dao.ContentPropertyValueDao;
import fr.solunea.thaleia.plugins.IPluginImplementation;
import fr.solunea.thaleia.plugins.cannelle.parsers.IModuleParserService;
import fr.solunea.thaleia.plugins.cannelle.parsers.ModuleParserServiceFactory;
import fr.solunea.thaleia.plugins.cannelle.utils.EmptyPackagedFiles;
import fr.solunea.thaleia.plugins.cannelle.utils.PackagedFiles;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.ThaleiaApplication;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Permet d'obtenir un ResourceHandler (objet d'accès aux ressources) qui pointe
 * sur la bonne configuration.
 */
public class ResourceHandlerService extends ParametersAwareService {

    // private static final Logger logger = Logger
    // .getLogger(ResourceHandlerService.class);

    private Class<? extends IPluginImplementation> pluginClass;

    /**
     * @param parameters
     * @param pluginClass la classe de plugin à associer avec ces resources.
     * @throws DetailedException
     */
    public ResourceHandlerService(Parameters parameters,
                                  Class<? extends IPluginImplementation> pluginClass)
            throws DetailedException {

        // Initialisation avec un resourcesHandler temporaire

        // On fabrique un ResourceHandler temporaire, car si quelqu'un
        // appelle ce service, c'est précisément pour retrouver la valeur de la
        // configuration. On ne peut donc pas instancier un
        // ResourcesHandler en lui transmettant la configuration à utiliser.
        // Normalement, dans l'utilisation de ce ResourcesHandler, il ne sera
        // pas nécessaire d'utiliser cette information ; de toutes façons, si
        // c'était le cas, le chemin ne pointerait sur aucun fichier.

        super(parameters, new ResourcesHandler(new PackagedFiles(),
                "undefined", pluginClass));

        this.pluginClass = pluginClass;
    }

    /**
     * Pour sélectionner la configuration à utiliser pour instancier ce
     * ResourceHandler, on ouvre le fichier Excel dans les fichiers tranmsis, et
     * on l'analyse pour retrouver le nom de la configuration à utiliser.
     *
     * @param packagedFiles
     * @return
     * @throws DetailedException
     */
    public ResourcesHandler getResourcesHandler(PackagedFiles packagedFiles)
            throws DetailedException {

        // On fixe un nouveau ResourceHandler pour que cet objet puisse ouvrir
        // les packagedFiles, afin d'y retrouver le fichier Excel à analyser.
        // Pourtant, on ne connait pas la configuration à utiliser (on la fixe
        // donc à undefined), mais elle n'est pas nécessaire pour les
        // traitements suivants.
        setResourcesHandler(new ResourcesHandler(packagedFiles, "undefined",
                pluginClass));

        // On Recherche la valeur du paramètre
        // Parameters.PACKAGER_CONF_PACKAGE_DIR// Cette valeur indiquera soit le
        // nom de la configuration à utiliser,
        // soit la propriété à rechercher dans le fichier Excel.
        String configuration = getConfigurationInParameters();
        if (configuration.startsWith("@@")) {

            // La valeur demandée est une valeur dynamique : @@XX
            // Par exemple, si la valeur est "@@GraphicTheme", alors on ouvre le
            // Excel, et on recherche la propriété "Thème graphique" indiquée
            // dans la feuille de présentation du module.

            // On supprime les @@
            String configurationParamName = configuration.substring(
                    "@@".length(), configuration.length());

            // On recherche la valeur de cette propriété dans le fichier Excel.

            // Récupération des propriétés du module décrites dans la feuille du
            // fichier Xls.
            Map<String, String> moduleProperties = new HashMap<String, String>();
            try {
                IModuleParserService moduleParserService = new ModuleParserServiceFactory()
                        .getObject(getParameters(), getResourcesHandler());

                moduleProperties = moduleParserService.getModuleProperties("","");

                // On recherche la propriété qui porte le nom demandé
                String configurationName = moduleProperties
                        .get(configurationParamName);

                if (configurationName == null) {
                    throw new DetailedException(
                            "La configuration demandée est la valeur de la propriété '"
                                    + configurationParamName
                                    + "', mais celle-ci n'est pas définie dans le fichier Excel !");
                }

                return new ResourcesHandler(packagedFiles, configurationName,
                        pluginClass);

            } catch (DetailedException e) {
                throw new DetailedException(e)
                        .addMessage("Impossible de construire un accès aux ressources de traitement"
                                + " d'après la configuration indiquée dans le fichier Excel.");
            }

        } else {
            // Pas de valeur dynamique : on renvoie un ResourcesHandler pointant
            // sur le nom de la configuration trouvée.
            return new ResourcesHandler(packagedFiles, configuration,
                    pluginClass);
        }

    }

    /**
     * @return dans les paramètres, la valeur de la configuration demandée
     * (valeur du paramètre Parameters.PACKAGER_CONF_PACKAGE_DIR)
     */
    private String getConfigurationInParameters() {
        return getParameters().getValue(Parameters.PACKAGER_CONF_PACKAGE_DIR);
    }

    /**
     * Pour sélectionner la configuration à utiliser pour instancier le
     * ResourceHandler, on analyse les propriétés de la version.
     *
     * @param version
     * @param locale  la locale dans laquelle on veut obtenir la configuration
     *                associée à cette version. Il s'agit de la locale dans laquelle
     *                on va rechercher la valeur de la propriété qui est décrite
     *                dans le paramètre Parameters.PACKAGER_CONF_PACKAGE_DIR, si ce
     *                paramètre fait référence à une ContentProperty (@@XX) . Cette
     *                locale n'est pas utilisée si
     *                Parameters.PACKAGER_CONF_PACKAGE_DIR est une valeur en dur (ne
     *                commençant pas par @@)
     * @return
     * @throws DetailedException
     */
    public ResourcesHandler getResourcesHandler(ContentVersion version,
                                                Locale locale) throws DetailedException {

        PackagedFiles packagedFiles;
        try {
            // On fixe un nouveau ResourceHandler temporaire pour que cet objet
            // puisse ouvrir analyser les propriétés.
            // Pourtant, on ne connait pas la configuration à utiliser (on la
            // fixe donc à undefined), mais elle n'est pas nécessaire pour les
            // traitements suivants.
            File tempDir = ThaleiaApplication.get().getTempFilesService()
                    .getTempDir();
            packagedFiles = new EmptyPackagedFiles();

            setResourcesHandler(new ResourcesHandler(packagedFiles,
                    "undefined", pluginClass));

        } catch (Exception e) {
            throw new DetailedException(e)
                    .addMessage("Impossible de préparer l'accès aux ressources.");
        }

        // On Recherche la valeur du paramètre
        // Parameters.PACKAGER_CONF_PACKAGE_DIR// Cette valeur indiquera soit le
        // nom de la configuration à utiliser,
        // soit la propriété à rechercher dans le fichier Excel.
        String configuration = getConfigurationInParameters();
        if (configuration.startsWith("@@")) {

            // La valeur demandée est une valeur dynamique : @@XX
            // Cette valeur est le nom de la ContentProperty à rechercher dans
            // la version.
            // Par exemple, si la valeur est "@@GraphicTheme", on recherche la
            // valeur de cette ContentProperty.

            // On supprime les @@
            String contentPropertyName = configuration.substring("@@".length(),
                    configuration.length());

            // On recherche la ContentProperty qui porte ce nom
            ContentPropertyDao contentPropertyDao = new ContentPropertyDao(version.getObjectContext());
            ContentProperty contentProperty = contentPropertyDao.findByName(contentPropertyName);

            if (contentProperty == null) {
                throw new DetailedException(
                        "La configuration demandée est la valeur de la propriété '"
                                + contentPropertyName + "', pour le module "
                                + version
                                + " mais celle-ci n'est pas définie !");
            }

            // On recherche la valeur de cette propriété
            String contentPropertyValue = version.getPropertyValue(contentProperty, locale, "", new ContentPropertyValueDao(ThaleiaApplication.get().getConfiguration().getLocalDataDir().getAbsolutePath(), ThaleiaApplication.get().getConfiguration().getBinaryPropertyType(), version.getObjectContext()));

            return new ResourcesHandler(packagedFiles, contentPropertyValue,
                    pluginClass);

        } else {
            // Pas de valeur dynamique : on renvoie un ResourcesHandler pointant
            // sur le nom de la configuration trouvée.
            return new ResourcesHandler(packagedFiles, configuration,
                    pluginClass);
        }

    }
}
