package fr.solunea.thaleia.plugins.cannelle.v6;

import fr.solunea.thaleia.model.ApplicationParameter;
import fr.solunea.thaleia.model.Content;
import fr.solunea.thaleia.model.ContentType;
import fr.solunea.thaleia.model.dao.ApplicationParameterDao;
import fr.solunea.thaleia.model.dao.ContentTypeDao;
import fr.solunea.thaleia.plugins.IPluginImplementation;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.v6.help.DetailsPage;
import fr.solunea.thaleia.plugins.cannelle.v6.panels.CannelleContentsPanel;
import fr.solunea.thaleia.service.utils.Configuration;
import fr.solunea.thaleia.service.utils.ZipUtils;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.utils.LogUtils;
import fr.solunea.thaleia.webapp.ThaleiaApplication;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.request.component.IRequestablePage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;

public class CannelleV6Plugin implements Serializable, IPluginImplementation {

    /**
     * Nom du fichier contenant les paramètres à charger par défaut dans les
     * Parameters, sans son extension
     */
    protected final static String DEFAULT_PARAMETERS_FILE = "cannelle_v6";
    /**
     * L'extension des fichiers de propriétés.
     */
    protected static final String EXTENSION = ".properties";
    private static final Logger logger = Logger.getLogger(CannelleV6Plugin.class);
    /**
     * Dans le répertoires ressources, nom du dossier contenant les ressources
     * concernant les propriétés (les fichiers .properties)
     */
    private static final String DEFAULT_PARAMETERS_DIR = "Properties";
    private final String parametersFileName;
    private Parameters parameters;

    private Configuration configuration = ThaleiaApplication.get().getConfiguration();

//    il n'est pas possible de charger le classLoader dans une variable d'instance
//    car un classLoader n'est pas sérializable !!!
//    private ClassLoader thaleiaClassloader = ThaleiaApplication.get().getApplicationSettings().getClassResolver().getClassLoader();

    /**
     * Un plugin non localisé : la conf du plugin qui sera utilisée pour le
     * charger sera la conf de la locale par défaut de ce plugin.
     */
    public CannelleV6Plugin() {
        // Le fichier de propriété par défaut
        parametersFileName = getDefaultParametersFileName() + "_fr" + EXTENSION;
        // logger.debug("Pas de locale tranmsise : on utilise le fichier de propriétés : "
        // + parametersFileName);
    }

    /**
     * @param locale la locale à prendre en compte pour le choix du paramétrage de ce plugin. Si la locale est
     */
    public CannelleV6Plugin(fr.solunea.thaleia.model.Locale locale) throws DetailedException {

        if (locale == null) {
            throw new DetailedException("Impossible d'instancier un plugin pour une locale nulle !");
        }

        // On recherche le nom de la locale
        parametersFileName = getParameterFilenameForLocale(locale);
        logger.debug("Locale tranmsise '" + locale.getName() + "' : on utilise le fichier de propriétés : "
                + parametersFileName);

    }

    private String getParameterFilenameForLocale(fr.solunea.thaleia.model.Locale locale) {
        String localeName = locale.getName();
        return getDefaultParametersFileName() + "_" + localeName + EXTENSION;
    }

    @Override
    public Class<? extends IRequestablePage> getPage() {
        return null;
    }

    @Override
    public Class<? extends IRequestablePage> getDetailsPage() {
        return DetailsPage.class;
    }

    protected String getDefaultParametersFileName() {
        return DEFAULT_PARAMETERS_FILE;
    }

    @Override
    public String getName(Locale locale) throws DetailedException {

        if (locale.equals(Locale.ENGLISH)) {
            return getParameters().getValue(Parameters.PLUGIN_NAME + "." + Parameters.LOCALE_EN);
        } else {
            return getParameters().getValue(Parameters.PLUGIN_NAME + "." + Parameters.LOCALE_FR);
        }

    }

    @Override
    public String getDescription(Locale locale) throws DetailedException {

        if (locale.equals(Locale.ENGLISH)) {
            return getParameters().getValue(Parameters.PLUGIN_DESCRIPTION + "." + Parameters.LOCALE_EN);
        } else {
            return getParameters().getValue(Parameters.PLUGIN_DESCRIPTION + "." + Parameters.LOCALE_FR);
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
        try (InputStream is = accessClassLoader().getResourceAsStream("cannelle.png")) {
            if (is != null) {
                result = IOUtils.toByteArray(is);
            }
        } catch (Exception e) {
            logger.warn("Impossible de charger l'image : " + e);
        }
        return result;
    }

    @Override
    public boolean canEdit(Content content) {
        try {
            ContentType pluginContentType = new ContentTypeDao(ThaleiaSession.get().getContextService().getContextSingleton()).findByName(CannelleContentsPanel.MODULE_CONTENT_TYPE_NAME);
            return content.getLastVersion().getContentType().equals(pluginContentType);
        } catch (Exception e) {
            logger.debug(e);
            return false;
        }
    }

    @Override
    public void onInstalled() throws DetailedException {
        // Changement de la page d'accueil pour la MainPage de CannelleV6
        setApplicationPageTo("fr.solunea.thaleia.plugins.cannelle.v6.MainPage",
                Configuration.AUTHENTIFIED_USERS_WELCOME_PAGE);
        installDefaultCustomizationFile();
    }

    private void installDefaultCustomizationFile() throws DetailedException {
        File defaultCustomizationDir = configuration.getDefaultCustomizationDir();
        boolean isDefaultCustomizationDirExists = defaultCustomizationDir.exists();

        File customizationFilesDir = configuration.getCustomizationFilesDir();
        logger.debug("contrôle installation Dossier customization : " + isDefaultCustomizationDirExists);

        if (isDefaultCustomizationDirExists) {
            logger.debug("dossier customization existe : " + customizationFilesDir.getAbsolutePath());

            if (isDefaultCustomizationFileExists(defaultCustomizationDir)) {
                logger.debug("fichier de customization par defaut déjà installé ");
            } else {
                copyDefaultCustomizationFile(defaultCustomizationDir);
            }
        } else {
            copyDefaultCustomizationFile(defaultCustomizationDir);
        }

        }

    private boolean isDefaultCustomizationFileExists(File defaultCustomizationDir) {
        File[] listFilesInCustomizationDir = defaultCustomizationDir.listFiles();
        File[] listCustomizationFiles =
                Arrays.stream(listFilesInCustomizationDir).filter((file) -> {
                    boolean isanArchive = ZipUtils.isAnArchive(file);
                    return isanArchive;
                }).toArray(File[]::new);
        boolean isCustomizationFileExists = listCustomizationFiles.length > 0;
        return isCustomizationFileExists;
    }

    private void copyDefaultCustomizationFile(File defaultCustomizationDir) throws DetailedException {
        try {
            InputStream defaultCustomizationFileStream = accessClassLoader().getResourceAsStream(getDefaultCustomizationFileName()) ;
            FileUtils.copyInputStreamToFile(defaultCustomizationFileStream,new  File(defaultCustomizationDir, "default-customization-file.zip"));
            logger.debug("fichier de customization par defaut installé dans " + defaultCustomizationDir);

        } catch (IOException e) {
            throw new DetailedException(e).addMessage("impossible de copier le fichier de customization par defaut");
        }
    }

    protected ClassLoader accessClassLoader() {
        return ThaleiaApplication.get().getApplicationSettings().getClassResolver().getClassLoader();
    }

    private String getDefaultCustomizationFileName() {
        return "Resources_cannelle/content-customization-html.zip";
    }


    private static void setApplicationPageTo(String pageName, String updatedPage) throws DetailedException {
        ApplicationParameterDao dao = ThaleiaApplication.get().getApplicationParameterDao();

        ApplicationParameter param = dao.findByName(updatedPage);

        if (param == null) {
            param = dao.get();
            param.setName(updatedPage);
        }

        param.setValue(pageName);
        dao.save(param);
    }


    public Parameters getParameters() throws DetailedException {
        if (parameters == null) {
            try {
                setParametersFile(getParametersFileName());

            } catch (DetailedException e) {
                throw new DetailedException(e).addMessage("Impossible d'initialiser les paramètres.");
            }
        }

        return parameters;
    }

    private String getParametersFileName() {
        return parametersFileName;
    }

    /**
     * Initialise les paramètres, en prenant en entrée le fichier qui porte le
     * nom demandé dans le répertoire du plugin qui contient les fichiers de
     * paramètres.
     *
     * @param parametersFile le nom du fichier de paramètres, par exemple : defaultparams_export_cdrom.properties
     */
    protected void setParametersFile(String parametersFile) throws DetailedException {
        try {
            // logger.debug("Recherche des propriétés dans le fichier '"
            // + DEFAULT_PARAMETERS_DIR + "/" + parametersFile + "'...");

            InputStream is = accessClassLoader()
                    // NOPMD
                    .getResourceAsStream(DEFAULT_PARAMETERS_DIR + "/" + parametersFile);

            if (is == null) {
                throw new Exception("Le fichier '" + DEFAULT_PARAMETERS_DIR + "/" + parametersFile + "' n'a pas été "
                        + "trouvé dans le classloader.");
            }

            parameters = new Parameters(is, null);

        } catch (Exception e) {
            throw new DetailedException(
                    "Impossible d'initialiser les paramètres avec le fichier '" + parametersFile + "' : " + e + "\n"
                            + LogUtils.getStackTrace(e.getStackTrace()));
        }

    }

}
