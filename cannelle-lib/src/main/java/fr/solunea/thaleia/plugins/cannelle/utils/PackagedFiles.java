package fr.solunea.thaleia.plugins.cannelle.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import fr.solunea.thaleia.service.utils.FilenamesUtils;
import fr.solunea.thaleia.service.utils.ZipUtils;
import fr.solunea.thaleia.utils.DetailedException;

/**
 * Permet d'accéder facilement au contenu d'une archive contenant des fichiers.
 * 
 */
public class PackagedFiles {

	private final File expandedDir;

	private static final Logger logger = Logger.getLogger(PackagedFiles.class);

	/**
	 * Une archive vide.
	 * 
	 */
	public PackagedFiles() {
		expandedDir = null;
	}

	/**
	 * Prend en entrée le fichier comme une archive, la décompresse, et met à
	 * disposition les fichiers contenus.
	 * 
	 * @param archiveFile
	 *            l'archive à prendre en entrée. Si ce fichier n'est pas une
	 *            archive, alors on considère que c'était une archive vide.
	 * @param expandedDir
	 *            un répertoire temporaire DANS LEQUEL on pourra décompresser le
	 *            fichier uploadé.
	 * @throws DetailedException
	 *             si le binaire est nul.
	 */
	public PackagedFiles(File archiveFile, File expandedDir)
			throws DetailedException {

		// Si la décompression de archiveFile échoue, on n'arrête pas pour
		// autant le traitement.
		try {
			// On prépare l'existence de la destination
			try {
				FileUtils.forceMkdir(expandedDir);
			} catch (IOException e1) {
				throw new DetailedException(e1)
						.addMessage("Impossible de préparer la destination "
								+ expandedDir.getAbsolutePath());
			}

			if (archiveFile == null) {
				throw new DetailedException(
						"L'archive ne peut pas être nulle !");
			}

			if (!archiveFile.canRead()) {
				throw new DetailedException("L'archive '"
						+ archiveFile.getAbsolutePath()
						+ "' ne peut pas être lue !");
			}

			try {
				logger.debug("Décompression de "
						+ archiveFile.getAbsolutePath() + " dans "
						+ expandedDir.getAbsolutePath());
				ZipUtils.dezip(archiveFile.getAbsolutePath(),
						expandedDir.getAbsolutePath());

			} catch (Exception e) {
				throw new DetailedException(e).addMessage("L'archive '"
						+ archiveFile.getAbsolutePath()
						+ "' ne peut pas être décompressée :" + e.toString());
			}

		} catch (DetailedException e) {
			logger.info("Le fichier transmis ne peut pas être considéré comme une archive :"
					+ e.toString());
		}

		try {
			if (expandedDir == null) {
				throw new DetailedException(
						"La destination ne peut pas être nulle !");
			}

			if (!expandedDir.exists()) {
				throw new DetailedException("La destination '"
						+ expandedDir.getAbsolutePath() + "' de l'archive '"
						+ archiveFile.getAbsolutePath() + "' n'existe pas !");
			}

			this.expandedDir = expandedDir;

		} catch (DetailedException e) {
			e.addMessage("Le fichier transmis ne peut pas être considéré comme une archive :"
					+ e.toString());
			throw e;
		}

	}

	/**
	 * Renvoie le premier fichier trouvé qui porte ce nom dans les fichier
	 * uploadés.
	 * 
	 * @param nameFile
	 * @return le fichier, ou null s'il n'a pas été trouvé.
	 */
	public File getFile(String nameFile) {
		// On echappe les noms avec des espaces dans le chemin vers le fichier
		String filename = nameFile.replaceAll(" ", "\\ ");

		File result = getFile(filename, true);
		// Si ce fichier n'est pas trouvé, on le recherche sans tenir compte de
		// l'extension
		if (result == null) {
			result = getFile(nameFile, false);
		}

		return result;
	}

	public String getSizeFile(String nameFile){
		File sizeFile = getFile(nameFile);
		String size = Long.toString((sizeFile.length()/1024)+1);
		return size;
	}

	/**
	 * Retourne un l'extension du fichier mis en paramétre
	 *
	 * @param nameFile a string.
	 * @return l'extension du fichier mis en paramètre
	 */
	public String getFileExtension(String nameFile){
		File file = getFile(nameFile);
		String ext = "";
		String name = file.getName();
		ext = name.substring(name.lastIndexOf("."));
		return ext;
	}


	/**
	 * Renvoie le premier fichier trouvé qui porte ce nom dans les fichier
	 * uploadés.
	 *
	 * @param nameFile
	 * @return le nom du fichier, ou null s'il n'a pas été trouvé.
	 */
	public String findStringFile(String nameFile) {
		// On echappe les noms avec des espaces dans le chemin vers le fichier
		String filename = nameFile.replaceAll(" ", "\\ ");

		String result = getFileExtention(filename, true);
		// Si ce fichier n'est pas trouvé, on le recherche sans tenir compte de
		// l'extension
		if (result == null) {
			result = getFileExtention(nameFile, false);
		}

		return result;
	}

	/**
	 * @param searchedFilename
	 * @param extensionSensitive
	 *            si true, alors on recherche avec l'extension. Si false, alors
	 *            on renvoie le premier fichier trouvé, sans tenir compte de
	 *            l'extension demandée.
	 * @return le nom du fichier mis en paramètre et son extension
	 */
	private String getFileExtention(String searchedFilename, boolean extensionSensitive) {
		// On echappe les noms avec des espaces dans le chemin vers le fichier
		searchedFilename.replaceAll(" ", "\\ ");

		// Le nom demandé sans extension
		String searchedFilenameNoExtension = FilenamesUtils.getNameWithoutExtension(searchedFilename);

		Collection<File> files = listFiles(null, true);
		Iterator<File> it = files.iterator();
		while (it.hasNext()) {
			File file = it.next();
			if (extensionSensitive) {
				if (file.getName().equals(searchedFilename)) {
					return file.getName();
				}
			} else {
				String fileNameNoExtension = FilenamesUtils.getNameWithoutExtension(file.getName());
				// On compare les noms de fichiers sans leurs extensions
				if (fileNameNoExtension.equals(searchedFilenameNoExtension)) {
					return file.getName();
				}
			}
		}
		return null;

	}

	/**
	 * @param searchedFilename
	 * @param extensionSensitive
	 *            si true, alors on recherche avec l'extension. Si false, alors
	 *            on renvoie le premier fichier trouvé, sans tenir compte de
	 *            l'extension demandée.
	 * @return
	 */
	private File getFile(String searchedFilename, boolean extensionSensitive) {
		// On echappe les noms avec des espaces dans le chemin vers le fichier
		searchedFilename.replaceAll(" ", "\\ ");

		// Le nom demandé sans extension
		String searchedFilenameNoExtension = FilenamesUtils.getNameWithoutExtension(searchedFilename);

		Collection<File> files = listFiles(null, true);
		Iterator<File> it = files.iterator();
		while (it.hasNext()) {
			File file = it.next();
			if (extensionSensitive) {
				if (file.getName().equals(searchedFilename)) {
					return file;
				}
			} else {
				String fileNameNoExtension = FilenamesUtils.getNameWithoutExtension(file.getName());
				// On compare les noms de fichiers sans leurs extensions
				if (fileNameNoExtension.equals(searchedFilenameNoExtension)) {
					return file;
				}
			}
		}
		return null;

	}

	/**
	 * @param extensions
	 * @param recursive
	 * @return tous les fichiers de cette extension parmi les fichiers uploadés.
	 */
	public Collection<File> listFiles(String[] extensions, boolean recursive) {
		if (expandedDir == null) {
			return new ArrayList<File>();
		} else {
			return FileUtils.listFiles(expandedDir, extensions, recursive);
		}
	}

	public Collection<String> listFilenamesWithoutExtension(String[] extensions, boolean recursive) {
		Collection<File> listFiles = listFiles(extensions, recursive);
		Collection<String> listFilenames = new ArrayList<String>();
		if (!listFiles.isEmpty()) {
			for (File file : listFiles) {
				listFilenames.add(FilenamesUtils.getNameWithoutExtension(file.getName()));
			}
		}
		return listFilenames;
	}

	@Override
	public String toString() {
		if (expandedDir == null) {
			return this.getClass().getName() + " - pas de répertoire associé.";
		} else {
			int recursiveCount = FileUtils.listFiles(expandedDir, null, true)
					.size();
			return this.getClass().getName() + " - répertoire : '"
					+ expandedDir.getAbsolutePath() + "' contenant "
					+ recursiveCount + " fichier(s).";
		}

	}

	/**
	 * Normalise les noms de fichiers (suppressions des espaces, des caractères
	 * spéciaux etc.)
	 * 
	 * @throws DetailedException
	 */
	public void normalizeFiles() throws DetailedException {
		try {
			String[] extensions = { "jpg", "png" };
			Iterator<File> itFiles = this.listFiles(extensions, true)
					.iterator();
			File file = null;
			while (itFiles.hasNext()) {
				file = itFiles.next();
				File newFile = new File(file.getParent() + File.separator // NOPMD
						+ FilenamesUtils.getNormalizedFilename(file));
				file.renameTo(newFile);
			}
		} catch (Exception e) {
			throw new DetailedException(e)
					.addMessage("Erreur durant la normalisation des noms de fichiers reçus.");
		}
	}

	public File getExpandedDir() {
		return expandedDir;
	}
}
