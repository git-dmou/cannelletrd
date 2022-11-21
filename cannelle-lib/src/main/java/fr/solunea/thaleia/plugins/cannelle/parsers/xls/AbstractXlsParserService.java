package fr.solunea.thaleia.plugins.cannelle.parsers.xls;

import fr.solunea.thaleia.model.User;
import fr.solunea.thaleia.plugins.cannelle.messages.LocalizedMessages;
import fr.solunea.thaleia.plugins.cannelle.service.ParametersAwareService;
import fr.solunea.thaleia.plugins.cannelle.utils.CellsRange;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.utils.XlsUtils;
import fr.solunea.thaleia.service.TemplatedMailsService;
import fr.solunea.thaleia.service.utils.FilenamesUtils;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.ThaleiaApplication;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Permet d'analyser un fichier Excel : ouverture d'une feuille, et récupération
 * du contenu d'une cellule.
 */
public abstract class AbstractXlsParserService extends ParametersAwareService {

    private static final Logger logger = Logger.getLogger(AbstractXlsParserService.class);
    private static final Map<Workbook, InputStream> openedFiles = new HashMap<>();
    private File xlsFile;

    public AbstractXlsParserService(Parameters parameters, ResourcesHandler resourcesHandler) {
        super(parameters, resourcesHandler);
    }

    public File findXlsFile(Collection<File> xlsFiles) throws DetailedException {
        File xlsFile = null;
        if (xlsFiles.isEmpty()) {
            // Le message localisé correspondant
            String message = LocalizedMessages.getMessage(LocalizedMessages.NO_XLS_ERROR);
            // On demande sa présentation
            notifiySession(message);

            sendNotificationMail();

            throw new DetailedException("Aucun fichier XLS n'a été trouvé dans l'archive !");

        } else if (xlsFiles.size() > 1) {
            // Plusieurs fichiers trouvés : on va tous les ouvrir et rechercher le bon.
            // C'est celui qui ne provoque pas d'exception à la recherche de la locale correspondant au nom de la première feuille
            ExcelFileSpecification excelFileSpecification = new ExcelFileSpecification();
            for (File file : xlsFiles) {
                // On cherche la première feuille de ce fichier Excel
                try {
                    Workbook workbook = openWorkbook(file);
                    if (workbook != null) {
                        Sheet firstSheet = workbook.getSheetAt(0);
                        String sheetLocale = excelFileSpecification.getSheetLocale(firstSheet.getSheetName());
                        xlsFile = file;
                        closeWorkbook(workbook);
                        logger.debug("Le fichier Excel '" + file.getAbsolutePath() + "' est une source Cannelle dont la locale est : " + sheetLocale);
        }
            } catch (DetailedException e) {
                    logger.debug("Le fichier Excel '" + file.getAbsolutePath() + "' n'est pas une source Cannelle (" + e.getMessage() + ").");
            }
        }
            // Aucune source Excel trouvée
            if (xlsFile == null) {
                throw new DetailedException("Aucun fichier XLS n'a été trouvé dans l'archive !");
    }

        } else {
            // Un seul fichier Excel trouvé.
            xlsFile = xlsFiles.iterator().next();
        }
        return xlsFile;
    }

    protected void sendNotificationMail() {
        // On prépare le corps de l'email de rapport d'erreur
        User user = ThaleiaSession.get().getAuthenticatedUser();
        String details = "Une erreur a eu lieu lors de l'import dans Cannelle d'un " + "fichier par " + "l'utilisateur "
                + user.getLogin() + " (" + user.getName() + ") sur l'instance "
                + ThaleiaApplication.get().getApplicationRootUrl() + "<br><br>Aucun fichier XLS n'a été trouvé dans l'archive.";
        new TemplatedMailsService(ThaleiaSession.get().getContextService().getContextSingleton()).sendErrorReport("cannelle.import.error.report.email",
                "cannelle.import.error.report.subject", details);
    }

    protected  void notifiySession(String message) {
        ThaleiaSession.get().addError(message);
    }

    public static void closeWorkbook(Workbook workbook) {
        InputStream is = openedFiles.get(workbook);
        if (is != null) {
            IOUtils.closeQuietly(is);
    }
    }

    public static Workbook openWorkbook(File file) throws DetailedException {
        if (file == null) {
            throw new DetailedException("Le fichier Excel ne doit pas être nul !");
        }

        logger.debug("Analyse du fichier " + file.getAbsolutePath());

        // On ouvre le fichier XLS
        logger.debug("Ouverture du fichier '" + file.getAbsolutePath() + "'...");
        InputStream is;
        try {
            is = new FileInputStream(file);
            try {
                Workbook workbook = WorkbookFactory.create(is);
                openedFiles.put(workbook, is);
                return workbook;
            } catch (IOException ioe) {
                throw new DetailedException(ioe).addMessage(
                        "Erreur de lecture durant l'ouverture du fichier '" + file.getAbsolutePath() + "' : "
                                + ioe.toString());
            }

        } catch (FileNotFoundException e) {
            throw new DetailedException(e).addMessage("Le fichier '" + file.getAbsolutePath() + "' n'existe pas !");
        }
    }

    protected File getXlsFile() {
        return xlsFile;
    }

    protected void setXlsFile(File xlsFile) {
        this.xlsFile = xlsFile;
    }

    protected Sheet openSheetToParse() throws DetailedException {
        // On initialise le fichier Excel avec le fichier transmis
        logger.debug("Initialisation du fichier Excel");
        setXlsFile(getXlsFileFromResourcesHandler());
        return openSheetToParse(xlsFile);
    }

    /**
     * @return Ouvre la feuille à utiliser durant les traitements
     */
    protected Sheet openSheetToParse(File file) throws DetailedException {
        String parsedSheetName = getParsedSheetName(getParameters());
        Workbook wb = openWorkbook(file);
        logger.debug("Ouverture de la feuille '" + parsedSheetName + "'...");
        return wb.getSheet(parsedSheetName);
    }

    /**
     * Initialise le paramètre xlsFile avec le fichier Excel contenus dans les
     * ressources transmises au service
     */
    protected File getXlsFileFromResourcesHandler() throws DetailedException {
        File xlsFile;
        // Pour permettre aux boulets d'envoyer une archive où le fichier Excel
        // n'est pas à la racine, on recherche dans toutes l'arborescence des
        // fichiers envoyés.
        boolean recursive = true;


        // On parcourt les ressources et on liste tous les fichiers Excel
        Collection<File> xlsFiles = getResourcesHandler().getUploadedFiles().listFiles(ExcelFileSpecification.EXTENSIONS, recursive);

        logger.debug("Trouvé " + xlsFiles.size() + " fichier(s) XLS dans les fichiers uploadés.");

        xlsFile = findXlsFile(xlsFiles);

        //On vérifie la fréquence du nom du fichier XLS dans l'archive.
        if (Collections.frequency(getResourcesHandler().getUploadedFiles().listFilenamesWithoutExtension(null,
                recursive), FilenamesUtils.getNameWithoutExtension(xlsFile.getName()))
                > 1) {

            // Le message localisé correspondant
            String message = LocalizedMessages.getMessage(LocalizedMessages.MULTIPLE_SHEET_NAME_ERROR,
                    FilenamesUtils.getNameWithoutExtension(xlsFile.getName()));

            // On demande sa présentation
            notifiySession(message);

            throw new DetailedException("L'une des ressources porte le même nom que le fichier XLS : "
                    + FilenamesUtils.getNameWithoutExtension(xlsFile.getName())
                    + ", il est nécessaire de renommer cette dernière.");
        }

        logger.debug("On utilise le fichier '" + xlsFile.getAbsolutePath() + "' comme spécifications.");
        return xlsFile;
    }

    public Cell getCellAt(CellsRange cells, int column, int line) throws DetailedException {
        try {
            if (cells.getCell(column, line) == null) {
                cells.createCell(column, line);
            }
            return cells.getCell(column, line);
        } catch (Exception e) {
            throw new DetailedException(e).addMessage(
                    "Impossible d'accéder à la cellule col=" + column + " lign=" + line);
        }
    }

    public String getStringValue(Cell cell) {

        return XlsUtils.getStringValue(cell, getParameters().getValue(Parameters.EXCEL_BOOLEAN_TRUE_TEXT_VALUE),
                getParameters().getValue(Parameters.EXCEL_BOOLEAN_FALSE_TEXT_VALUE));
    }

    /**
     * @return le nom de la feuille du fichier qui doit être parsée.
     */
    protected abstract String getParsedSheetName(Parameters parameters) throws DetailedException;


}
