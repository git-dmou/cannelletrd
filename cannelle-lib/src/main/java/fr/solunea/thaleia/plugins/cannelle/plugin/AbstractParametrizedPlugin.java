package fr.solunea.thaleia.plugins.cannelle.plugin;

import java.io.InputStream;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import fr.solunea.thaleia.plugins.IPluginImplementation;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.utils.LogUtils;
import fr.solunea.thaleia.webapp.ThaleiaApplication;

/**
 * Un plugin, dont le paramétrage est fourni dans un fichier de propriétés.
 * 
 */
public abstract class AbstractParametrizedPlugin implements
		IPluginImplementation {

	private static final Logger logger = Logger
			.getLogger(AbstractParametrizedPlugin.class);

	/**
	 * Dans le répertoires ressources, nom du dossier contenant les ressources
	 * concernant les propriétés (les fichiers .properties)
	 */
	private static final String DEFAULT_PARAMETERS_DIR = "Properties";

	private Parameters parameters;

	@Override
	public String getName(Locale locale) throws DetailedException {

		if (locale.equals(Locale.ENGLISH)) {
			return getParameters().getValue(
					Parameters.PLUGIN_NAME + "." + Parameters.LOCALE_EN);
		} else {
			return getParameters().getValue(
					Parameters.PLUGIN_NAME + "." + Parameters.LOCALE_FR);
		}

	}

	@Override
	public String getDescription(Locale locale) throws DetailedException {

		if (locale.equals(Locale.ENGLISH)) {
			return getParameters().getValue(
					Parameters.PLUGIN_DESCRIPTION + "." + Parameters.LOCALE_EN);
		} else {
			return getParameters().getValue(
					Parameters.PLUGIN_DESCRIPTION + "." + Parameters.LOCALE_FR);
		}
	}

	@Override
	public String getVersion(Locale locale) throws DetailedException {
		return getParameters().getValue(Parameters.PLUGIN_VERSION);
	}

	@Override
	public byte[] getImageAsPng() {
		byte[] result = null;

		// On charge l'image comme ressource = un fichier contenu dans le Jar
		// On stocke son contenu dans un tableau d'octets.
		InputStream is = ThaleiaApplication.get().getApplicationSettings()
				.getClassResolver().getClassLoader() // NOPMD
				.getResourceAsStream(getImageFileName());
		try {
			result = IOUtils.toByteArray(is);
		} catch (Exception e) {
			logger.warn("Impossible de charger l'image : " + e);
		} finally {
			IOUtils.closeQuietly(is);
		}

		return result;
	}

	public Parameters getParameters() throws DetailedException {
		if (parameters == null) {
			try {
				setParametersFile(getParametersFileName());

			} catch (DetailedException e) {
				throw new DetailedException(e)
						.addMessage("Impossible d'initialiser les paramètres.");
			}
		}

		return parameters;
	}

	/**
	 * @return le nom du fichier de paramètres dans le jar.
	 */
	protected abstract String getParametersFileName();

	/**
	 * Initialise les paramètres, en prenant en entrée le fichier qui porte le
	 * nom demandé dans le répertoire du plugin qui contient les fichiers de
	 * paramètres.
	 * 
	 * @param parametersFile
	 *            le nom du fichier de paramètres, par exemple :
	 *            defaultparams_export_cdrom.properties
	 * @throws DetailedException
	 */
	protected void setParametersFile(String parametersFile)
			throws DetailedException {
		try {
			// logger.debug("Recherche des propriétés dans le fichier '"
			// + DEFAULT_PARAMETERS_DIR + "/" + parametersFile + "'...");

			InputStream is = ThaleiaApplication
					.get()
					.getApplicationSettings()
					.getClassResolver()
					.getClassLoader()
					// NOPMD
					.getResourceAsStream(
							DEFAULT_PARAMETERS_DIR + "/" + parametersFile);

			if (is == null) {
				throw new Exception("Le fichier '" + DEFAULT_PARAMETERS_DIR
						+ "/" + parametersFile
						+ "' n'a pas été trouvé dans le classloader.");
			}

			parameters = new Parameters(is, null);

		} catch (Exception e) {
			throw new DetailedException(
					"Impossible d'initialiser les paramètres avec le fichier '"
							+ parametersFile + "' : " + e + "\n"
							+ LogUtils.getStackTrace(e.getStackTrace()));
		}

	}

	/**
	 * @return dans le jar, le nom de l'image qui correspond au plugin.
	 */
	protected abstract String getImageFileName();

}
