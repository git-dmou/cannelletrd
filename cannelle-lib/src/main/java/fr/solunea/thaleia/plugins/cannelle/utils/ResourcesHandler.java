/**
 * Le code source, le matériel préparatoire et la documentation de ce
 * logiciel sont la propriété exclusive de la société Solunea, au titre
 * du droit de propriété intellectuelle. Ces éléments ont fait l'objet
 * de dépôts probatoires.
 * <p>
 * À défaut d'accord préalable écrit de Solunea, vous ne devez pas
 * utiliser, copier, modifier, traduire, créer une œuvre dérivée,
 * transmettre, vendre ou distribuer, de manière directe ou indirecte,
 * inverser la conception ou l'assemblage ou tenter de trouver le code
 * source (sauf cas prévus par la loi), ou transférer tout droit relatif
 * audit logiciel.
 * <p>
 * Solunea
 * SARL - N° SIRET 48795234300027
 */
package fr.solunea.thaleia.plugins.cannelle.utils;

import fr.solunea.thaleia.plugins.IPluginImplementation;
import fr.solunea.thaleia.service.utils.FilenamesUtils;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.utils.LogUtils;
import fr.solunea.thaleia.webapp.ThaleiaApplication;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Met à disposition les ressources du plugin, c'est à dire les fichiers contenu
 * dans le répertoire RESOURCES_DIR_NAME du jar du plugin.
 */
public class ResourcesHandler {

    private static final Logger logger = Logger.getLogger(ResourcesHandler.class);

    /**
     * On n'extrait les fichiers de resource des plugins qu'une fois...
     */
    private static Map<Class<? extends IPluginImplementation>, File> extractedFilesCache = new HashMap<Class<?
            extends IPluginImplementation>, File>();

    /**
     * Dans le jar du plugin, le nom du répertoire (à la racine du jar), qui
     * contient les ressources qui seront accessibles via cette classe. Ce nom
     * de répertoire DOIT se terminer par "/". Ce nom de répertoire doit être
     * unique pour chaque projet, car il est recherché tel quel dans le
     * classpath où sont placé tous les jar, et le premier qui porte ce nom est
     * renvoyé.
     */
    private final static String RESOURCES_DIR_NAME = "Resources_cannelle/";
    protected Class<? extends IPluginImplementation> pluginClass;

    // Un répertoire temporaire
    protected File tempDir;

    // Les fichiers uploadés
    protected PackagedFiles uploadedFiles;

    // Le répertoire des ressources
    protected File resourcesDir;

    /**
     * Le nom de la configuration du plugin à utiliser (vanilla etc.)
     */
    protected String configuration;

    /**
     * Utilitaire d'accès aux fichiers utilisés par le plugin (entrée et
     * sortie).
     *
     * @param uploadedFiles Une référence sur le fichier uploadé.
     * @param configuration Le nom de la configuration du plugin à utiliser (vanilla etc.)
     * @throws DetailedException si les répertoires n'existent pas
     */
    public ResourcesHandler(PackagedFiles uploadedFiles, String configuration, Class<? extends IPluginImplementation>
            pluginClass) throws DetailedException {
        this.tempDir = initTempDir();
        this.resourcesDir = initResourcesDir();
        this.uploadedFiles = uploadedFiles;
        this.configuration = configuration;
        this.pluginClass = pluginClass;

        if (uploadedFiles == null) {
            throw new DetailedException("Le répertoire uploadedFiles est nul !");
        }

        prepareResourcesDir();
    }

    public ResourcesHandler() {

    }

    public File initResourcesDir() throws DetailedException {
        return ThaleiaApplication.get().getTempFilesService().getTempFile();
    }

    public File initTempDir() throws DetailedException {
        return ThaleiaApplication.get().getTempFilesService().getTempFile();
    }

    /**
     * Prépare le répertoire qui va contenir toutes les ressources du plugin.
     * Copie dans le répertoire demandé les resources du plugin, qui sont dans
     * le jar du plugin.
     *
     * @param "destination" le répertoire dans lequel seront copiés les fichiers de
     *                    resrouces.
     */
    protected void prepareResourcesDir() throws DetailedException {

        try {
            // Y-a-t-il déjà eu une décompression des ressources de ce plugin ?
            if (pluginClass != null && extractedFilesCache.get(pluginClass) != null
                    && extractedFilesCache.get(pluginClass).exists()
                    && extractedFilesCache.get(pluginClass).isDirectory()
                    && extractedFilesCache.get(pluginClass).listFiles() != null
                    && extractedFilesCache.get(pluginClass).listFiles().length > 0) {
                logger.debug("On utilise les ressources du plugin déjà décompressées dans "
                        + extractedFilesCache.get(pluginClass).getAbsolutePath());
                // extractedFilesCache.get(pluginClass) est par exemple :
                // [racine locale]\temp\1518703119298_abd7\1518703119298_d28c.tmp
                this.resourcesDir = extractedFilesCache.get(pluginClass);

            } else {
                extractJarFiles();
            }

            // logger.debug("Répertoire des ressources du plugin : "
            // + this.resourcesDir.getAbsolutePath() + " ("
            // + FileUtils.listFiles(this.resourcesDir, null, true).size()
            // + " fichiers)");
            logger.debug("Répertoire des ressources du plugin : " + this.resourcesDir.getAbsolutePath());

        } catch (Exception e) {
            throw new DetailedException("Impossible d'extraire les fichiers resources : " + e + "\n"
                    + LogUtils.getStackTrace(e.getStackTrace()));
        }

    }

    private void extractJarFiles() throws DetailedException, IOException {

//        if (ThaleiaApplication.get().getApplicationSettings().getClassResolver().getClassLoader() == null) {
//            throw new DetailedException("le ClassLoader est nul !");
//
//        } else {


            // On récupère la liste des tous les fichiers contenus dans les répertoires du jar qui
            // contiennent des ressources
            String[] fileNames = listFilesInResource(RESOURCES_DIR_NAME, true);

            logger.debug("Extraction de " + fileNames.length + " fichiers du jar du plugin dans le répertoire "
                    + this.resourcesDir.getAbsolutePath() + " ...");

            // On récupère tous ces fichiers
            int copied = 0;
            for (int i = 0; i < fileNames.length; i++) {
                String fileName = fileNames[i];

                InputStream is = getClassLoaderHere().getResourceAsStream(fileName); // NOPMD

                if (is == null) {
                    logger.debug("Pas de copie de '" + fileName + "' (flux nul).");

                } else {
                    File destinationFile = new File(this.resourcesDir // NOPMD
                            + File.separator + fileName);

                    // logger.debug("Copie de '" + fileName + "' vers '"
                    // + destinationFile.getAbsolutePath() + "'...");

                    FileUtils.copyInputStreamToFile(is, destinationFile);
                    copied++;
                }
            }

            extractedFilesCache.put(pluginClass, this.resourcesDir);

            logger.debug(copied + " fichiers de ressources du plugin copiés dans le répertoire '" + this.resourcesDir
                    + "' ! Taille totale des fichiers copiés = " + FileUtils.sizeOfDirectory(this.resourcesDir)
                    + " octets.");
        }

        protected   ClassLoader getClassLoaderHere() {
        return ThaleiaApplication.get().getApplicationSettings().getClassResolver().getClassLoader();
    }
//    }

    /**
     * @return la liste des tous les fichiers accessibles dans le classloader
     * sous le chemin path.
     */
    private String[] listFilesInResource(String path, boolean excludeDirectories) throws DetailedException {

        logger.debug("Recherche de l'entrée '" + path + "' dans le classpath...");
        // On récupère l'URL sur la resource dans le Jar du plugin : Par exemple
        // :
        // jar:file:D:\Solunea\Thaleia\Configurations\ThaleiaV4\data\plugins\1378115843647-1MgCY\thaleia-plugin
        // -importexport-1.0-SNAPSHOT.jar!/act_files/
//        URL pathUrl = ThaleiaApplication.get().getApplicationSettings().getClassResolver().getClassLoader()
//                .getResource(path); // NOPMD
        URL pathUrl = getClassLoaderHere().getResource(path);

        if (pathUrl == null) {
            throw new DetailedException("L'entrée '" + path + "' n'a pas été retrouvée dans les ressources !");
        } else {
            logger.debug("Url de la ressource : '" + pathUrl + "'");
        }

        if (pathUrl.getProtocol().equals("file")) {
            // Ce n'était pas un jar, mais un fichier : on renvoie la liste de
            // son contenu
            try {
                logger.debug("Fichier retrouvé pour '" + path + "' : " + new File(pathUrl.toURI()).getAbsolutePath());
                return new File(pathUrl.toURI()).list();

            } catch (URISyntaxException e) {
                throw new DetailedException("Impossible de lister les fichiers de la ressrouce '" + path + "' : " + e);
            }
        }

        if (pathUrl.getProtocol().equals("jar")) {
            try {
                logger.debug("Fichier retrouvé pour '" + path + "' : " + new File(pathUrl.toURI()).getAbsolutePath());
            } catch (Exception e1) {
                // rien, c'est juste pour journaliser
            }

            // On extrait le nom du Jar qui contient la ressource
            String jarPath = pathUrl.getPath().substring(5, pathUrl.getPath().indexOf("!"));

            JarFile jar = null;
            // On évite les doublons
            Set<String> result = new HashSet<String>();

            try {
                // On ouvre le Jar en tant qu'archive Zip
                jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));

                // On liste les entrées dans le Zip
                Enumeration<JarEntry> entries = jar.entries();

                while (entries.hasMoreElements()) {
                    String name = entries.nextElement().getName();
                    // On ne garde que les entrées qui sont sous le chemin
                    // demandé
                    if (name.startsWith(path) &&

                            // Si demandé, on ne prend pas en compte les
                            // répertoires
                            // = les entrées qui finissent par "/"
                            (!excludeDirectories || excludeDirectories && !name.endsWith("/"))) {

                        // logger.debug("Prise en compte de l'entrée '" +
                        // name
                        // + "'...");
                        result.add(name);

                    }
                }

            } catch (Exception e) {
                throw new DetailedException("Impossible d'analyser les fichiers de la ressrouce '" + path + "' : " + e);

            } finally {
                IOUtils.closeQuietly(jar);
            }

            return result.toArray(new String[result.size()]);

        } else {
            logger.debug("L'entrée '" + path + "' ne contient pas de ressources !");
            return new String[]{};
        }
    }

    /**
     * @param path le chemin relatif dans le répertoire de ressources du plugin.
     * @return le fichier de resources copié dans le répertoire temporaire
     */
    public File getResourceFile(String path) throws DetailedException {

        logger.debug("Recherche du fichier resource '" + path + "'...");
        File result = null;

        // On recherche dans le répertoire des ressources
        result = new File(resourcesDir.getAbsolutePath() + File.separator + RESOURCES_DIR_NAME + path);

        if (result.exists()) {
            logger.debug("On renvoie le fichier " + result.getAbsolutePath());
            return result;

        } else {
            // Est-ce que le répertoire temporaire des ressources en cache a été vidé ?
            try {
                extractJarFiles();
            } catch (IOException e) {
                throw new DetailedException(e);
            }
            if (result.exists()) {
                logger.debug("On renvoie le fichier " + result.getAbsolutePath());
                return result;
            }

            // Même après une nouvelle extraction : absence du fichier demandé
            throw new DetailedException("Le fichier '" + result.getAbsolutePath() + "' n'existe pas !");
        }
    }

    private String getParametrizedResourcePath(String root, Parameters parameters, String pathKey, String lang) {

        // Le chemin relatif du fichier dans le répertoire
        // act_files/types/vanilla/fr etc.
        String fileRelativePath = parameters.getValue(pathKey);
        fileRelativePath = FilenamesUtils.getNormalizedPath(fileRelativePath);

        String checkedLang = lang;
        if (checkedLang == null) {
            // On prend la langue par défault
            checkedLang = parameters.getValue(Parameters.EXPORT_LANG);
        }

        // On passe le nom de la configuration en lowerCase !
        String lowerCaseConfiguration = configuration.toLowerCase(Locale.FRENCH);

        String resourcePath =
                root + "/" + "types" + "/" + lowerCaseConfiguration + "/" + checkedLang + "/" + fileRelativePath;
        return resourcePath;

    }

    /**
     * @param root
     * @param parameters
     * @param pathKey
     * @return
     * @throws DetailedException
     */
    public File getParametrizedResourceFile(String root, Parameters parameters, String pathKey) throws
            DetailedException {
        return getParametrizedResourceFile(root, parameters, pathKey, null);
    }

    /**
     * @param root
     * @param parameters
     * @param pathKey
     * @param lang
     * @return
     * @throws DetailedException
     */
    public File getParametrizedResourceFile(String root, Parameters parameters, String pathKey, String lang) throws
            DetailedException {

        String path = getParametrizedResourcePath(root, parameters, pathKey, lang);
        File result;
        try {
            result = getResourceFile(path);
        } catch (Exception e) {
            throw new DetailedException(e).addMessage(
                    "Impossible de récupérer le fichier '" + path + "' dans le répertoire de resources du plugin."
                            + e.toString());
        }

        return result;
    }

    public File getTempDir() {
        return tempDir;
    }

    public void setTempDir(File tempDir) {
        this.tempDir = tempDir;
    }

    public PackagedFiles getUploadedFiles() {
        return uploadedFiles;
    }

    public boolean isFileInUploadedFiles(String fileName) {
        return getUploadedFiles().findStringFile(fileName)!=null;
    }

    public void setUploadedFiles(PackagedFiles uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }

}
