package fr.solunea.thaleia.plugins.cannelle.contents;

import fr.solunea.thaleia.service.utils.CopyUtils;
import fr.solunea.thaleia.service.utils.FilenamesUtils;
import fr.solunea.thaleia.service.utils.ZipUtils;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.utils.LogUtils;
import fr.solunea.thaleia.webapp.ThaleiaApplication;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import java.io.File;
import java.util.*;

/**
 * Un fichier A7, et ses médias. Il est instancié (décompression de ses
 * fichiers) dans un répertoire temporaire à l'instanciation, dans un répertoire
 * [tempdir]/versions/[id de la version].
 */
@SuppressWarnings("serial")
public final class A7Content extends AbstractContent {
    private static final Logger logger = Logger.getLogger(A7Content.class);

    /**
     * Le répertoire qui contient le A7 et ses médias
     */
    protected File directory;
    protected A7File a7File;
    private boolean isHtmlContent = false;
    private boolean isAudible = false;

    /**
     * @return est-ce que le contenu contient un fichier audio
     */
    public boolean isAudible() {
        return isAudible;
    }

    /**
     * @param zipSource l'archive qui contient le A7, et ses médias.
     */
    public static A7Content createFromArchive(File zipSource) throws DetailedException {

        if (zipSource == null) {
            throw new DetailedException("La zipSource ne doit pas être nulle !");
        }

        if (!zipSource.exists()) {
            throw new DetailedException("La zipSource '" + zipSource.getAbsolutePath() + "' n'existe pas !");
        }

        File dezipedDir;
        // Création d'un répertoire temporaire pour la décompression des
        // fichiers.
        try {
            dezipedDir = ThaleiaApplication.get().getTempFilesService().getTempDir();

        } catch (Exception e) {
            throw new DetailedException(e).addMessage(
                    "Impossible de créer le répertoire " + "temporaire pour la décompression du contenu.");
        }

        // On marque ce répertoire pour être nettoyé par la machine
        // virtuelle Java
        dezipedDir.deleteOnExit();

        // Décompression des fichiers
        try {
            ZipUtils.dezip(zipSource.getAbsolutePath(), dezipedDir.getAbsolutePath(), false);

        } catch (Exception e) {
            throw new DetailedException(e).addMessage("Impossible de décompresser l'archive du contenu.");
        }
        File[] files = dezipedDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getAbsolutePath().endsWith("_html") && file.isDirectory()) {
                    return new A7Content(file);
                }
            }
        }

        return new A7Content(dezipedDir);

    }

    /**
     * @param directory le répertoire qui contient le a7 et ses médias
     */
    protected A7Content(File directory) throws DetailedException {
        super();

        if (!directory.isDirectory()) {
            logger.debug(LogUtils.getStackTrace(Thread.currentThread().getStackTrace()));

            throw new DetailedException("'" + directory + "' n'est pas un répertoire, mais un fichier !");
        }
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                String absolutePath = file.getAbsolutePath();
                if (absolutePath.endsWith(".mp3") || absolutePath.endsWith(".ogg") || absolutePath.endsWith(".wav")) {
                    isAudible = true;
                }
                if (absolutePath.endsWith("html")) {
                    isHtmlContent = true;
                }
            }
        }


        this.directory = directory;
    }

    /**
     * @param directory le répertoire qui contient le A7, et ses médias.
     */
    public static A7Content createFromDirectory(File directory) throws DetailedException {
        return new A7Content(directory);
    }

    /**
     * @param a7File le fichier a7.
     */
    public static A7Content createFromA7File(File a7File) throws DetailedException {
        A7Content result = new A7Content(a7File.getParentFile());

        // On ne recherche pas de a7 dans ce répertoire, mais on instancie
        // directement le A7File, car on connait sur quel fichier a7 il faut
        // initialiser.
        result.a7File = new A7File(a7File);

        return result;
    }

    public File getDirectory() {
        return directory;
    }

    /**
     * @return le fichier A7
     */
    public A7File getMainContent() throws DetailedException {
        String[] extensions;
        if (this.a7File == null) {
            if (!isHtmlContent()) {
                extensions = new String[]{"a7"};
            } else {
                extensions = new String[]{"html"};
            }
            boolean recursive = false;
            Collection<File> a7Files = FileUtils.listFiles(getDirectory(), extensions, recursive);

            if (a7Files.isEmpty()) {
                throw new DetailedException(
                        "Il n'a pas de fichier A7 dans le répertoire '" + getDirectory().getAbsolutePath() + "' !");
            }

            File dezipedA7File = (File) a7Files.toArray()[0];
            logger.debug("Trouvé " + a7Files.size() + " fichier(s) A7 : on ne prend en compte que '"
                    + dezipedA7File.getAbsolutePath() + "'");

            a7File = new A7File(dezipedA7File);
        }

        return a7File;
    }

    /**
     * Copie les binaires de ce A7 dans cette destination. Pour tous les
     * fichiers (A7 et médias), si un fichier existe déjà avec ce nom dans la
     * destination ET une empreinte MD5 différente, alors le nouveau fichier est
     * renommé.
     *
     * @param contentDestination le répertoire de destination
     * @return le contenu copié
     */
    public A7Content safeCopyTo(File contentDestination) throws DetailedException {
        try {
            // Copie du fichier a7
            File sourceFile = this.getMainContent().getFile();
            A7Content copiedContent;
            if (isHtmlContent()) {
                sourceFile = this.getMainContent().getFile().getParentFile();
            }
            File copiedA7 = CopyUtils.safeCopyInto(sourceFile, contentDestination, false);
            if (!copiedA7.isDirectory()) {
                copiedContent = A7Content.createFromA7File(copiedA7);
            } else {
                copiedContent = A7Content.createFromDirectory(copiedA7);
            }

            // Copie des médias associés
            Map<String, File> medias = this.getMedias();
            for (String filenameTag : medias.keySet()) {
                File media = medias.get(filenameTag);

                String normalizedFilename = FilenamesUtils.getNormalizedPath(filenameTag);

                logger.debug("Copie du média identifié par '" + normalizedFilename + "' avec le binaire de '"
                        + media.getAbsolutePath() + "'...");

                File copiedMedia = CopyUtils.safeCopyInto(media, normalizedFilename, contentDestination, false);

                // si le fichier a du être renommé, on met à jour le A7
                if (!copiedMedia.getName().equals(media.getName())) {
                    // On change le nom de fichier, mais pas celui d'éventuels
                    // répertoires avant ce nom de fichier dans la balise
                    // filename
                    String newFilename = normalizedFilename;
                    if (newFilename.contains("/")) {
                        // présence d'un / dans le filename
                        newFilename =
                                newFilename.substring(0, newFilename.lastIndexOf('/') + 1) + copiedMedia.getName();
                    } else {
                        newFilename = copiedMedia.getName();
                    }
                    copiedContent.getMainContent().changeFilename(normalizedFilename, newFilename);
                }
            }
            return copiedContent;

        } catch (DetailedException e) {
            e.addMessage(
                    "Impossible de copier les fichier du contenu '" + this.getMainContent().getFile().getAbsolutePath()
                            + "'.");
            throw e;
        }
    }

    /**
     * Construit le chemin pour le zip de ce contenu, ce chemin n'est pas le même si le contenu est un htmlContent ou
     * non
     */

    public String getZipPath() throws DetailedException {
        if (isHtmlContent()) {
            return getMainContent().getFile().getParentFile().getParentFile().getAbsolutePath();
        } else {
            return getMainContent().getFile().getParentFile().getAbsolutePath();
        }
    }

    /**
     * @return la liste des médias (fichiers réels, décompressés dans le
     * répertoire temporaire de ce A7Content) pour ce contenu.<br/>
     * Clé = contenu de la balise filename / Valeur = fichier binaire
     * stocké dans le répertoire temporaire
     */
    public Map<String, File> getMedias() throws DetailedException {
        Map<String, File> result = new HashMap<String, File>();
        if (isHtmlContent()) {
            return result;
        }
        try {
            List<String> filenames = getMainContent().getFilenames();
            // pour tous les fichiers referencés
            for (String filename : filenames) {

                File asset = new File(getMainContent().getFile().getParentFile()// NOPMD
                        .getAbsolutePath() + File.separator + filename.replaceAll(" ", "\\ "));
                if (!asset.exists()) {
                    throw new Exception("Le fichier asset '" + asset.getAbsolutePath() + "' référencé par le fichier '"
                            + getMainContent().getFile().getAbsolutePath()
                            + "' n'existe pas ! Vérifiez la cohérence du Zip.");
                }
                logger.debug("Trouvé le fichier référencé : '" + asset.getAbsolutePath() + "'");
                result.put(filename, asset);
            }
        } catch (Exception e) {
            throw new DetailedException(e).addMessage("Impossible de retrouver les médias référencés dans le A7.");
        }

        return result;
    }

    /**
     * @return tous les noeuds qui référencent ce fichier
     */
    public List<Node> getNodesReferingFile(File file) throws DetailedException {
        ArrayList<Node> result = new ArrayList<Node>();
        Map<Node, File> medias = getMainContent().getMediasNodes();
        for (Node node : medias.keySet()) {
            if (medias.get(node).equals(file)) {
                // Si ce noeud référence le fichier demandé
                result.add(node);
            }
        }
        return result;
    }

    /**
     * Pour toutes les balises de média, remplace tous les oldName par newName,
     * et enregistre le nouveau XML dans le binaire.
     *
     * @param oldFileName : le contenu de la balise filename à remplacer (attention : ce
     *                    n'est pas que le nom du fichier, mais son URL relative)
     * @param newFileName : le nouveau contenu de la balise filename (attention : ce
     *                    n'est pas que le nom du fichier, mais son URL relative).
     */
    public void renameFilename(String oldFileName, String newFileName) throws DetailedException {
        this.getMainContent().changeFilename(oldFileName, newFileName);
    }

    protected boolean isHtmlContent() {
        return this.isHtmlContent;
    }

}
