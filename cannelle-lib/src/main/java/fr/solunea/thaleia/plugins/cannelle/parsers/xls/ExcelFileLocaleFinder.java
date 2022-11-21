package fr.solunea.thaleia.plugins.cannelle.parsers.xls;

import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.service.utils.ZipUtils;
import fr.solunea.thaleia.utils.DetailedException;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.util.Collection;

public class ExcelFileLocaleFinder {

    /**
     * Ouvre cette archive, recherche le premier fichier Excel trouvé, et tente d'y retrouver la locale du modèle de
     * fichier Excel utilisé.
     */
    public static String parseLocale(File zipFile, File tempDir) throws DetailedException {

        if (!tempDir.isDirectory()) {
            throw new DetailedException(tempDir.getAbsolutePath() + " n'est pas un répertoire !");
        }

        // On dézippe les fichiers Excel du paquet
        try {
            ZipUtils.doDezip(zipFile.getAbsolutePath(), tempDir.getAbsolutePath(), false, true, null,
                    ExcelFileSpecification.EXTENSIONS);
        } catch (Exception e) {
            throw new DetailedException("Impossible de déziper le paquet fourni : " + e);
        }

        // On liste les fichiers Excel décompressés
        Collection<File> xlsFiles = FileUtils.listFiles(tempDir, ExcelFileSpecification.EXTENSIONS, true);

        // On retrouve le fichier Excel
//        File xlsFile = AbstractXlsParserService.findXlsFile(xlsFiles);

//         implémentation d'une classe Anonyme pour pouvoir appeler la methode findXlsFile() (qui était statique)
//         en methode d'instance.
//         Comme on veux overider les dépendences à ThaleiaSession et ThaleiaApplication en Test
//         on est obliger de les implementer en methode d'instance, ce qui implique de passer findXlsFile()
//         en methode d'instance au lieu de Static !
//         en attendant un refacto pour faire plus propre !!!!
        File xlsFile = new AbstractXlsParserService(null, null) {
            @Override
            protected String getParsedSheetName(Parameters parameters) throws DetailedException {
                return null;
            }
        }.findXlsFile(xlsFiles);

        try {
            Workbook workbook = AbstractXlsParserService.openWorkbook(xlsFile);
            String firstSheetName = workbook.getSheetAt(0).getSheetName();
            String sheetLocale = new ExcelFileSpecification().getSheetLocale(firstSheetName);
            AbstractXlsParserService.closeWorkbook(workbook);
            return sheetLocale;
        } catch (DetailedException e) {
            throw new DetailedException(e).addMessage(
                    "Erreur durant l'ouverture du fichier '" + xlsFile.getAbsolutePath() + "' : "
                            + e.toString());
        }
    }
}
