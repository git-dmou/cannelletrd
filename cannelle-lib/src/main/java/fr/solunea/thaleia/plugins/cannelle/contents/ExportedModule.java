package fr.solunea.thaleia.plugins.cannelle.contents;

import fr.solunea.thaleia.model.ContentVersion;
import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.plugins.cannelle.packager.act.Act;
import fr.solunea.thaleia.utils.DetailedException;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;

/**
 * Un objet exporté, sur lequel vont s'effectuer des traitements.
 */
@SuppressWarnings("serial")
public class ExportedModule extends AbstractVersionedContent {

    private static final Logger logger = Logger.getLogger(ExportedModule.class);

    /**
     * Dans Thaleia, le nom de la propriété qui contient le masteryScore
     */
    private static final String MASTERYSCORE_PROPERTY_NAME = "PassageNote";
    /**
     * Dans Thaleia, le nom de la propriété qui contient le titre
     */
    private static final String MODULE_TITLE_PROPERTY_NAME = "Title";
    /**
     * Dans Thaleia, le nom de la propriété qui contient l'information : doit-on
     * activer la communication Scorm ?
     */
    private static final String SCORMCOMMUNICATION_PROPERTY_NAME = "SCORMCommunication";

    /**
     * Dans Thaleia, le nom de la propriété qui contient l'information : quel
     * est le format de ce module (HTML, EXE...) ?
     */
    private static final String MODULE_FORMAT_PROPERTY_NAME = "ModuleFormat";
    /**
     * Le répertoire où est exporté le module.
     */
    private File destinationDir;
    /**
     * La liste des écrans de ce module.
     */
    private List<A7Screen> screens;

    /**
     * La locale dans laquelle le module est exporté.
     */
    private Locale locale;
    private Act act;

    /**
     * @param moduleVersion le module exporté
     */
    public ExportedModule(ContentVersion moduleVersion) throws DetailedException {
        super(moduleVersion);

        if (!moduleVersion.getContent().getIsModule()) {
            throw new DetailedException("Le contenu à exporter n'est pas un module !");
        }
    }

    public void setExportDir(File destinationDir) {
        this.setDestinationDir(destinationDir);
    }

    public File getDestinationDir() {
        return destinationDir;
    }

    public void setDestinationDir(File destinationDir) {
        this.destinationDir = destinationDir;
    }

    public void setScreens(List<A7Screen> a7List) {
        this.screens = a7List;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public List<A7Screen> getScreens() {
        return screens;
    }

    public String getMasteryScore(String defaultValue) throws DetailedException {
        return getContentVersionPropertyValue(MASTERYSCORE_PROPERTY_NAME, locale, defaultValue);
    }

    public String getScormCommunication(String defaultValue) throws DetailedException {
        return getContentVersionPropertyValue(SCORMCOMMUNICATION_PROPERTY_NAME, locale, defaultValue);
    }

    public void setAct(Act act) {
        this.act = act;
    }

    public Act getAct() {
        return act;
    }

    /**
     * @return le titre du module, dans la locale demandée pour cet export.
     */
    public String getTitle(String defaultValue) {
        // On recherche la valeur de la propriété "Title"
        return getContentVersionPropertyValue(MODULE_TITLE_PROPERTY_NAME, getLocale(), defaultValue);
    }

    /**
     * @return true si il est demandé que le module puisse s'exécuter en mode
     * local (par exemple en .exe).
     */
    public boolean isLocalExecution() {
        // TODO externaliser dans la conf l'interprétation des valeurs de cette
        // propriété.
        String htmlValueFr = "Pour site Web (HTML)";
        String exeValueFr = "Pour exécution locale (EXE)";
        String htmlValueEn = "For web site (HTML)";
        String exeValueEn = "For local execution (EXE)";
        String htmlValueEs = "Para sitio Web (HTML)";
        String exeValueEs = "Para ejecución local (EXE)";
        String htmlValueNl = "Voor website (HTML)";
        String exeValueNl = "Voor lokale uitvoering (EXE)";

        // Si propriété non trouvée, par défaut on exporte en HTML
        String localExecutionString = getContentVersionPropertyValue(MODULE_FORMAT_PROPERTY_NAME, locale, htmlValueFr);

        if (localExecutionString.toLowerCase().equals(htmlValueFr.toLowerCase())
                || localExecutionString.toLowerCase().equals(htmlValueEn.toLowerCase())
                || localExecutionString.toLowerCase().equals(htmlValueEs.toLowerCase())
                || localExecutionString.toLowerCase().equals(htmlValueNl.toLowerCase())) {
            // Format HTML
            return false;

        } else if (localExecutionString.toLowerCase().equals(exeValueFr.toLowerCase())
                || localExecutionString.toLowerCase().equals(exeValueEn.toLowerCase())
                || localExecutionString.toLowerCase().equals(exeValueEs.toLowerCase())
                || localExecutionString.toLowerCase().equals(exeValueNl.toLowerCase())) {
            // Format EXE
            return true;

        } else {
            logger.debug("La valeur de la propriété en base '" + MODULE_FORMAT_PROPERTY_NAME + "' est '"
                    + localExecutionString + "' : elle n'est pas reconnue. "
                    + "La valeur prise en compte par défaut est : '" + htmlValueFr + "'");
            return true;
        }
    }
}
