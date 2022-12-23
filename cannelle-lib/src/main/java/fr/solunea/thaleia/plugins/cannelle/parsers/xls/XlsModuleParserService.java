package fr.solunea.thaleia.plugins.cannelle.parsers.xls;

import fr.solunea.thaleia.plugins.cannelle.messages.LocalizedMessages;
import fr.solunea.thaleia.plugins.cannelle.parsers.IModuleParserService;
import fr.solunea.thaleia.plugins.cannelle.utils.ModuleResources;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.functions.AbstractFunction;
import fr.solunea.thaleia.utils.DetailedException;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Parse un fichier Excel pour récupérer les paramètres décrivant un module.
 */
public class XlsModuleParserService extends AbstractXlsParserService implements IModuleParserService {

    private static final Logger logger = Logger.getLogger(XlsModuleParserService.class);

    private enum ModulePropToBeTranslated {
        TITLE("xls.parser.module.properties.0.contentProperty"),
        DESCRIPTION("xls.parser.module.properties.1.contentProperty");

        ModulePropToBeTranslated(String propertyKeyInPropertiesFiles) {
        }
    }

    private enum LanguagePropertyName  {
        FR("Langue"),
        EN("Langue"),
        ES("Lengua"),
        NL("Taal");

        private String languagePropertyName;

        LanguagePropertyName(String languageName) {
            this.languagePropertyName = languageName;
        }

        public String getLanguagePropertyName() {
            return languagePropertyName;
        }
    }

    private enum LanguagesNames {
        FR(Parameters.LOCALE_FR_NAME_FR),
        EN(Parameters.LOCALE_EN_NAME_EN),
        ES(Parameters.LOCALE_ES_NAME_ES),
        EL(Parameters.LOCALE_EL_NAME_EL),
        DE(Parameters.LOCALE_DE_NAME_DE),
        IT(Parameters.LOCALE_IT_NAME_IT),
        ZH(Parameters.LOCALE_ZH_NAME_ZH),
        PT(Parameters.LOCALE_PT_NAME_PT),
        TR(Parameters.LOCALE_TR_NAME_TR),
        AR(Parameters.LOCALE_AR_NAME_AR),
        HE(Parameters.LOCALE_HE_NAME_HE),
        RU(Parameters.LOCALE_RU_NAME_RU),
        SK(Parameters.LOCALE_SK_NAME_SK);

        String languageFullName;
        LanguagesNames(String locale) {
            this.languageFullName = locale;
        }
        public String getLanguageFullName() {
            return languageFullName;
        }


    }

    /**
     * Tous les couples clé / valeur définis dans la feuille du fichier Excel qui décrit les propriétés du module.
     */
    private Map<String, String> moduleProperties;
//    private Map<String, String> keys_TranslatedKeys;

    public XlsModuleParserService(Parameters parameters, ResourcesHandler resourcesHandler) {
        super(parameters, resourcesHandler);
    }

    /**
     * @return la valeur de cette propriété. Attention : les noms de propriétés ne sont pas ceux qui sont écrits dans
     * les cellules Excel, mais sont traduits d'après les indications des paramètres de traitements.
     */
    @Override
    public Map<String, String> getModuleProperties(String origLanguage, String targetLanguage) throws DetailedException {
        if (moduleProperties == null) {

            // Parcourt du Excel pour récupérer les valeur dans la feuille de
            // description du module.
            Sheet sheet;
            try {
                sheet = openSheetToParse();

                if (sheet == null) {
                    String sheetName = getParsedSheetName(getParameters());

                    // Le message localisé correspondant
                    String message = LocalizedMessages.getMessage(LocalizedMessages.SHEET_NOT_FOUND_ERROR, sheetName);

                    // On demande sa présentation
                    notifiySession(message);

                    throw new DetailedException(
                            "La feuille '" + sheetName + "' n'a pas été trouvée dans le fichier Excel.");
                }

            } catch (Exception e) {
                throw new DetailedException(e).addMessage(
                        "La feuille de description du module " + "n'a pas pu être ouverte dans le fichier Excel : ");
            }

            loadModuleProperties(sheet, origLanguage, targetLanguage);

            loadModuleResources(sheet);

            if (!targetLanguage.equals("")) {
             translateTranslatableProperties();   
            }
            return moduleProperties;

        }
        return moduleProperties;
    }

    private void translateTranslatableProperties() {
    }

    /**
     * @return le nom de la feuille du fichier qui doit être parsée.
     */
    @Override
    protected String getParsedSheetName(Parameters parameters) throws DetailedException {
        return parameters.getValue(Parameters.PARSED_SHEET_NAME_MODULE);
    }

    /**
     * Charge les couples clé/valeur décrits dans le fichier Excel.
     */
    private void loadModuleProperties(Sheet sheet, String origLanguage, String targetLanguage) throws DetailedException {
        moduleProperties = new HashMap<>();
//        keys_TranslatedKeys = new HashMap<>();

        logger.debug("Récupération des propriétés du module dans le fichier Excel...");

        // La première ligne
        int firstRowNum = Integer.parseInt(getParameters().getValue(Parameters.MODULE_PROPERTIES_POSITION + ".line"));

        // La dernière ligne
        int lastRowNum = sheet.getLastRowNum();

        logger.debug(
                "Analyse des lignes " + firstRowNum + " à " + lastRowNum + " de la feuille '" + sheet.getSheetName()
                        + "'...");

//        ModulePropertiesTranslator translator = ModulePropertiesTranslator.getInstance(getParameters(), origLanguage, targetLanguage);
        ModulePropertiesTranslator translatorFromLabelToContentProp = ModulePropertiesTranslator.getInstance(getParameters());

        for (int i = firstRowNum; i <= lastRowNum; i++) {
            try {
                Row row = sheet.getRow(i);

                // Il peut arriver qu'un numéro de ligne ne soit pas présent. On "saute" cet indice dans ce cas.
                if (row != null) {

                    // On recherche la première cellule de cette ligne
                    int firstCellNum = row.getFirstCellNum();
                    logger.debug("Analyse de la ligne " + i + ", première cellule : " + firstCellNum);
                    if (firstCellNum != -1) {
                        Cell firstCell = row.getCell(firstCellNum);
                        String key = getStringValue(firstCell);
                        if (!"".equals(key)) {
                            logger.debug("Analyse de la cellule " + key);
                            Cell secondCell = row.getCell(firstCell.getColumnIndex() + 1);
                            String value = getStringValue(secondCell);
                            if (value == null) {
                                // logger.debug("La clé " + key +
                                // " n'a pas de valeur.");
                                value = "";
                            } else {
                                // logger.debug("Trouvé le couple " + key + "=" +
                                // value);
                            }

                            // Si cette propriété est associée à une ContentProperty
                            // en conf, alors on la stocke sous le nom de cette
                            // ContentProperty

                            String translatedkey = translatorFromLabelToContentProp.translate(key, key);
                            if (isModulePropertyNeedTranslation(targetLanguage, value)) {
                                //todo: APPEL Traducteur DeepL pour traduction Titre et Description
//                                value = String.valueOf(LanguagesNames.valueOf(targetLanguage));
                                // APPEL au Module DeepL pour traduire les propriétés
                                // et remplacement par la valeur traduite
                            }
                            if (isModuleLanguage(targetLanguage, key)) {
                                value = String.valueOf(LanguagesNames.valueOf(targetLanguage).getLanguageFullName());
                            }

                            logger.debug("Prise en compte du couple (" + key + " / " + translatedkey + ")=" + value);
                            moduleProperties.put(translatedkey, value);
//                            keys_TranslatedKeys.put(key, translatedkey);
                        }
                    }

                }
            } catch (Exception e) {
                throw new DetailedException(e).addMessage("La ligne " + i + " n'a pas pu être analysée.");
            }
        }

    }

    private static boolean isModulePropertyNeedTranslation(String targetLanguage, String key) {
        return !targetLanguage.equals("") && Arrays.stream(ModulePropToBeTranslated.values()).anyMatch((l) -> {
            return key.equals(ModulePropToBeTranslated.valueOf(String.valueOf(l)));
        });
    }private static boolean isModuleLanguage(String targetLanguage, String value) {
        return !targetLanguage.equals("") && Arrays.stream(LanguagePropertyName.values()).anyMatch((l) -> {
            return value.equals(LanguagePropertyName.valueOf(String.valueOf(l)).getLanguagePropertyName());
        });
    }

    /**
     * Chargement des propriétés du module qui décrivent les ressources associées. Ce traitement est un peu différent de
     * la récupération des couples clé/valeur des autres propriétés : pour une ressource, il nous faut son nom (string)
     * d'une part, et son fichier (file) ou son URL (string) d'autre part.
     */
    private void loadModuleResources(Sheet sheet) throws DetailedException {
        try {
            // On cherche les ressources de type FICHIER en supposant que dans
            // la conf du plugin on a défini les resources de
            // Resource[resource_file_min]Name à Resource[resource_file_max]Name
            int resource_file_min = ModuleResources.FILE_RESOURCE_MIN_INDEX;
            int resource_file_max = ModuleResources.FILE_RESOURCE_MAX_INDEX;
            // On va les parcourir depuis le premier
            int resource_file_current = resource_file_min;

            // On cherche les ressources de type URL en supposant que dans
            // la conf du plugin on a défini les resources de Resource1Name à
            // Resource[resource_url_min]Name à Resource[resource_url_max]Name
            int resource_url_min = ModuleResources.URL_RESOURCE_MIN_INDEX;
            int resource_url_max = ModuleResources.URL_RESOURCE_MAX_INDEX;
            // On va les parcourir depuis le premier
            int resource_url_current = resource_url_min;

            // On recherche la ligne qui contient l'en-tête du tableau de
            // description des ressources
            int resourcesHeaderLineNumber = getResourcesHeaderLineNumber(sheet);

            // On parcourt les lignes entre CELLE QUI SUIT l'en-tête des
            // resources et la fin de la feuille.
            logger.debug(
                    "Recherche des ressources du module décrite entre les lignes " + (resourcesHeaderLineNumber + 2)
                            + " et " + sheet.getLastRowNum() + "...");
            for (int i = resourcesHeaderLineNumber + 2; i <= sheet.getLastRowNum(); i++) {
                try {
                    Row row = sheet.getRow(i);
                    // On recherche la première cellule de cette ligne
                    int firstCellNum = row.getFirstCellNum();
                    if (firstCellNum != -1) {
                        Cell firstCell = row.getCell(firstCellNum);
                        String content = getStringValue(firstCell).trim();
                        if (!"".equals(content)) {
                            // Un contenu (fichier ou URL + son nom sur la
                            // cellule à droite)

                            // Le libellé de la ressource
                            String label = getStringValue(row.getCell(firstCellNum + 1));

                            logger.debug("Analyze de la ressource '" + label + "' -> '" + content + "'");

                            if (isURL(content)) {
                                // Il existe encore une ressource de type URL
                                // disponible ?
                                if (resource_url_current <= resource_url_max) {
                                    // On peuple la ressource de type URL

                                    // Le nom de la propriété qui contient le
                                    // nom de la resource
                                    String namePropertyName = "Resource" + resource_url_current + "Name";

                                    // Le nom de la propriété qui contient l'URL
                                    // de la resource
                                    String urlPropertyName = "Resource" + resource_url_current + "URL";

                                    moduleProperties.put(namePropertyName, label);
                                    moduleProperties.put(urlPropertyName, content);

                                    // La prochaine ressource prendra le
                                    // prochain indice de propriété
                                    resource_url_current++;

                                } else {
                                    logger.debug("La ressource de type URL '" + content
                                            + "' n'est pas traitée, car il n'y a plus "
                                            + "de ressources disponibles dans la " + "configuration du plugin.");
                                }
                            } else {
                                // La ligne décrit une ressource de type  fichier.

                                // Il existe encore une ressource de type Fichier disponible ?
                                if (resource_file_current <= resource_file_max) {
                                    // Le nom de la propriété qui contient le nom de la resource
                                    String namePropertyName = "Resource" + resource_file_current + "Name";

                                    // Le nom de la propriété qui contient le  nom du fichier de la resource
                                    String filePropertyName = "Resource" + resource_file_current + "File";

                                    // On vérifie que le fichier associé est bien présent dans les ressources
                                    // transmises.
                                    if (!isUploadedFileExists(content)) {
                                        // Le message localisé correspondant
                                        String message =
                                                LocalizedMessages.getMessage(LocalizedMessages.RESOURCE_FILE_NOT_FOUND, content);
                                        // On demande sa présentation
                                        notifiySession(message);

                                        throw new Exception(
                                                "Le fichier '" + content + "' est décrit comme ressource du module, "
                                                        + "mais n'a pas été trouvé dans " + "l'archive transmise.");
                                    }
                                    // Si le fichier comporte des accents, on prévient
                                    AbstractFunction.checkMediaFilename(content);

                                    moduleProperties.put(namePropertyName, label);
                                    moduleProperties.put(filePropertyName, content);

                                    // La prochaine ressource prendra le
                                    // prochain indice de propriété
                                    resource_file_current++;

                                } else {
                                    logger.debug("La ressource de type fichier '" + content
                                            + "' n'est pas traitée, car il n'y a plus "
                                            + "de ressources disponibles dans la " + "configuration du plugin.");
                                }
                            }

                        }
                    }

                } catch (Exception e) {
                    // On ne fait que logger l'échec de l'analyse
                    logger.debug("L'en-tête des ressources du module n'a pas été trouvé dans le fichier Excel : " + e);
                }
            }

        } catch (Exception e) {
            throw new DetailedException(e).addMessage(
                    "L'analyse de la description des ressources n'a pas pu" + " être effectuée dans le fichier Excel.");
        }

    }

    /**
     * @return le numéro de la ligne qui contient l'en-tête du tableau de description des ressources. Si pas défini,
     * alors on renvoie la dernière ligne du fichier.
     */
    private int getResourcesHeaderLineNumber(Sheet sheet) {
        // Par défaut, on renvoie la dernière ligne
        int result = sheet.getLastRowNum();

        // L'entête à rechercher
        String header = getParameters().getValue(Parameters.MODULE_RESOURCES_HEADER_POSITION);
        if (!"".equals(header)) {
            // Si l'entête est défini en conf
            // On parcourt toutes les premièeres cellules des lignes de la
            // feuille à la recherche de l'entête
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                try {
                    Row row = sheet.getRow(i);
                    // On recherche la première cellule de cette ligne
                    int firstCellNum = row.getFirstCellNum();
                    if (firstCellNum != -1) {
                        Cell firstCell = row.getCell(firstCellNum);
                        String content = getStringValue(firstCell);
                        if (header.equals(content)) {
                            // Et nous avons un bingo !
                            return i;
                        }
                    }

                } catch (Exception e) {
                    // On ne fait que logger l'échec de l'analyse
                    logger.debug("L'en-tête des ressources du module n'a pas été trouvé dans le fichier Excel : " + e);
                }
            }
        }

        // Pas trouvé
        logger.debug("L'en-tête du tableau des ressources du module '" + header + "' n'a pas été trouvé.");
        return result;
    }

    /**
     * @return true si cette chaîne de caractères est une URL
     */
    private boolean isURL(String string) {
        try {
            new URL(string);
            return true;

        } catch (MalformedURLException e) {
            return false;
        }
    }

    /**
     * @return true si un fichier portant ce nom est trouvé dans les fichiers uploadés.
     */
    private boolean isUploadedFileExists(String content) {

        return (null != this.getResourcesHandler().getUploadedFiles().getFile(content));

    }

}
