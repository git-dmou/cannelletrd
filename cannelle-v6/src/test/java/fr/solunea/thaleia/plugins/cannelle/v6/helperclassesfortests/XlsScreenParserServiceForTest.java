package fr.solunea.thaleia.plugins.cannelle.v6.helperclassesfortests;

import fr.solunea.thaleia.plugins.cannelle.contents.parsing.ExcelDefinition;
import fr.solunea.thaleia.plugins.cannelle.messages.LocalizedMessages;
import fr.solunea.thaleia.plugins.cannelle.parsers.xls.XlsScreenParserService;
import fr.solunea.thaleia.plugins.cannelle.utils.CellsRange;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.ScreenFactory;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.IScreenParameter;
import fr.solunea.thaleia.utils.DetailedException;

import java.util.ArrayList;
import java.util.List;

public class XlsScreenParserServiceForTest extends XlsScreenParserService {

    public XlsScreenParserServiceForTest(Parameters parameters, ResourcesHandler resourcesHandler) throws DetailedException {
        super(parameters, resourcesHandler);
//        this.screenFactory = getScreenFactory(parameters, resourcesHandler);
    }




    @Override
    protected void notifiySession(String message) {
    }

    @Override
    protected String getLocalizedMessage(String resourceKey, Object... parameters) {
        return "";
    }


     @Override
    public ScreenFactory getScreenFactory(Parameters parameters, ResourcesHandler resourcesHandler) throws DetailedException {
        return (ScreenFactory) new ScreenFactoryForTest(parameters, resourcesHandler);
    }

    /**
     * reprend les traitements de getScreens()
     * sans la création des écrans
     * pour récupérer les info de parse et des templates
     */


//    public List<IExcelTemplate> parseScreensData() throws DetailedException {
    public List<List<IScreenParameter>> parseScreensData() throws DetailedException {
        List<ExcelDefinition> cellsRanges;

        cellsRanges = getExcelDefinitions();

        ScreenFactoryForTest screenFactory = (ScreenFactoryForTest) getScreenFactory(getParameters(), getResourcesHandler());

//        List<IExcelTemplate> screensTemplates = new ArrayList<>();
       List<List<IScreenParameter>> screensParameters = new ArrayList<>();

        String screenId;
        // On parcourt les blocs de cellules
        // On vérifie que les identifiants d'exercices existent et sont bien
        // uniques
        for (ExcelDefinition excelDefinition : cellsRanges) {
            String excelLine = excelDefinition.getLocation();
            CellsRange cellsRange = excelDefinition.getCellsRange();

            screenTraitement:
            {
                // On fabrique l'écran correspondant à ce bloc de cellules
//                IExcelTemplate screen;
                List<IScreenParameter> screen;
                parseScreenAndScreenCreation:
                {
                    try {
                        screen = screenFactory.parseScreensData(cellsRange);    //parseScreen(cellsRange, getResourcesHandler(), locale, user);
                    } catch (DetailedException e) {
                        // Le message localisé correspondant
                        String message = getLocalizedMessage(LocalizedMessages.SCREEN_GENERATION_ERROR, excelLine);

                        // On demande sa présentation
                        notifiySession(message);

                        throw new DetailedException(e).addMessage(
                                "Impossible de générer l'écran défini à la ligne " + excelLine);
                    }
                }
//                screensTemplates.add(screen);
                screensParameters.add(screen);
            }
        }

//        return screensTemplates;
        return screensParameters;

    }


}

