package fr.solunea.thaleia.plugins.cannelle.v6.uctranslatemodule;

import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.model.User;
import fr.solunea.thaleia.plugins.cannelle.contents.parsing.ExcelDefinition;
import fr.solunea.thaleia.plugins.cannelle.parsers.xls.XlsScreenParserService;
import fr.solunea.thaleia.utils.DetailedException;

import java.util.List;

public class ScreensTranslator {


    String targetLanguage;
    Locale locale;
    User user;
    XlsScreenParserService screenParserService;

    String moduleId;


    public ScreensTranslator(Locale locale, User user, XlsScreenParserService screenParserService, String moduleId) {
        this.locale = locale;
        this.user = user;
        this.screenParserService = screenParserService;
        this.moduleId = moduleId;
    }


    public void translateModule(String targetLanguage) throws DetailedException {

        List<ExcelDefinition> cellsRanges = screenParserService.getExcelDefinitions();
        ExcelDefinition cellRange = cellsRanges.get(0);

        screenParserService.getScreen(locale, user, screenParserService.getScreenFactory(screenParserService.getParameters(), screenParserService.getResourcesHandler()), null);

    }


}
