import fr.solunea.thaleia.plugins.IPluginImplementation;
import fr.solunea.thaleia.plugins.cannelle.utils.PackagedFiles;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.utils.DetailedException;

import java.io.File;


class ResourcesHandlerForTest extends ResourcesHandler {


//    public File tempDirTest;

    /**
     * Utilitaire d'accès aux fichiers utilisés par le plugin (entrée et
     * sortie).
     *
     * @param uploadedFiles Une référence sur le fichier uploadé.
     * @param configuration Le nom de la configuration du plugin à utiliser (vanilla etc.)
     * @param pluginClass
     * @throws DetailedException si les répertoires n'existent pas
     */
    public ResourcesHandlerForTest(PackagedFiles uploadedFiles,
                                   String configuration,
                                   Class<? extends IPluginImplementation> pluginClass) throws DetailedException {
        super(uploadedFiles, configuration, pluginClass);
    }

    // constructeur à utiliser pour les tests pour injecter tempDir et resourcesDir
    public ResourcesHandlerForTest(PackagedFiles uploadedFiles,
                                   String configuration,
                                   Class<? extends IPluginImplementation> pluginClass,
                                   File tempDir,
                                   File resourcesDir) throws DetailedException {
//        super(uploadedFiles,
//                configuration,
//                pluginClass);
        super();
        this.tempDir = tempDir;
        this.resourcesDir = resourcesDir;
        this.uploadedFiles = uploadedFiles;
        this.configuration = configuration;
        this.pluginClass = pluginClass;

        prepareResourcesDir();
    }


    public File initResourcesDir() throws DetailedException {
        return tempDir;
    }

    public File initTempDir() throws DetailedException {
        return resourcesDir;
    }

    protected ClassLoader getClassLoaderHere() {
        return getClass().getClassLoader();
    }

}


