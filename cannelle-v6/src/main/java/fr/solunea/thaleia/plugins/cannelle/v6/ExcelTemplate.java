package fr.solunea.thaleia.plugins.cannelle.v6;

import fr.solunea.thaleia.webapp.security.ThaleiaSession;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Un fichier Excel modèle pour Cannelle, à télécharger pour commencer à produire.
 */
public class ExcelTemplate implements Serializable {

    private static final String XLS_MODEL_FILENAME_FR = "modele_cannelle_fr.xls";
    private static final String XLS_MODEL_FILENAME_EN = "modele_cannelle_en.xls";
    private static final String XLS_MODEL_FILENAME_ES = "modele_cannelle_es.xls";
    private static final String XLS_MODEL_FILENAME_NL = "modele_cannelle_nl.xls";
    private static final String XLS_MODEL_FILENAME_FR_ACC = "modele_cannelle_accessible_fr.xls";
    private static final String XLS_MODEL_FILENAME_EN_ACC = "modele_cannelle_accessible_en.xls";
    private static final String XLS_MODEL_FILENAME_ES_ACC = "modele_cannelle_accessible_es.xls";
    private static final String XLS_MODEL_FILENAME_NL_ACC = "modele_cannelle_accessible_nl.xls";

    private final String name;
    private final String filename;
    private final String shortName;

    public ExcelTemplate(String name, String shortName, String filename) {
        this.name = name;
        this.shortName = shortName;
        this.filename = filename;
    }

    public static List<ExcelTemplate> listAll() {
        List<ExcelTemplate> result = new ArrayList<>();
        result.add(new ExcelTemplate("FR", "FR", XLS_MODEL_FILENAME_FR));
        result.add(new ExcelTemplate("EN", "EN", XLS_MODEL_FILENAME_EN));
        result.add(new ExcelTemplate("ES", "ES", XLS_MODEL_FILENAME_ES));
        result.add(new ExcelTemplate("NL", "NL", XLS_MODEL_FILENAME_NL));
        // Les modèles accessibles.
        result.add(new ExcelTemplate("FR* (Accessible)", "FR*", XLS_MODEL_FILENAME_FR_ACC));
        result.add(new ExcelTemplate("EN* (Accessible)", "EN*", XLS_MODEL_FILENAME_EN_ACC));
        result.add(new ExcelTemplate("ES* (Accesible)", "ES*", XLS_MODEL_FILENAME_ES_ACC));
        result.add(new ExcelTemplate("NL* (Toegankelijk)", "NL*", XLS_MODEL_FILENAME_NL_ACC));
        return result;
    }

    public static ExcelTemplate getDefault() {
        try {
            // Si la locale préférée de l'utilisateur est EN, on présente le modèle EN
            if (ThaleiaSession.get().getAuthenticatedUser().getPreferedLocale().getName().toLowerCase().startsWith(
                    "en")) {
                return new ExcelTemplate("EN", "EN", XLS_MODEL_FILENAME_EN);
            }
        } catch (Exception e) {
            // rien
        }
        // Par défaut, la version FR
        return new ExcelTemplate("FR", "FR", XLS_MODEL_FILENAME_FR);
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getFilename() {
        return filename;
    }
}
