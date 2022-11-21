package fr.solunea.thaleia.plugins.cannelle.parsers.xls;

import fr.solunea.thaleia.utils.DetailedException;

import java.util.HashMap;
import java.util.Map;

public class ExcelFileSpecification {

    // On est forcé de lister toutes les combinaisons possible, dans le cas où utilise ce tableau pour la
    // méthode FileUtils.listFiles(tempDir, extensions, true), qui est sensible à la casse.
    static final String[] EXTENSIONS = {"xls", "xlsx", "Xls", "xLs", "xlS", "XLs", "xLS", "XlS", "XLS", "Xlsx",
            "xLsx", "xlSx", "xlsX", "XLsx", "xLSx", "xlSX", "XlSx", "XlsX", "xLsX", "XLSx", "xLSX", "XlSX", "xLSX",
            "XLSX"};

    // La locale correspondant à ces noms de feuilles, qui sont les noms de la première feuille d'un fichier Excel
    // source d'une formation Thaleia XL.
    private final Map<String, String> sheetNameLocales;

    public ExcelFileSpecification() {
        sheetNameLocales = new HashMap<>();
        sheetNameLocales.put("Module properties", "en");
        sheetNameLocales.put("Propiedades del módulo", "es");
        sheetNameLocales.put("Propriétés du module", "fr");
        sheetNameLocales.put("Eigenschappen van de module", "nl");
    }

    /**
     * @return le nom de la locale pour ce nom de feuille Excel.
     */
    public String getSheetLocale(String sheetName) throws DetailedException {

        for (String existingSheetName : sheetNameLocales.keySet()) {
            if (existingSheetName.equalsIgnoreCase(sheetName)) {
                return sheetNameLocales.get(existingSheetName);
            }
        }
        throw new DetailedException("Le nom de la feuille Excel '" + sheetName + "' n'est pas reconnu.");
    }
}
