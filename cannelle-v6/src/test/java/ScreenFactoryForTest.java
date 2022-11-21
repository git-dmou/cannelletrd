import fr.solunea.thaleia.plugins.cannelle.utils.CellsRange;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.ScreenFactory;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.generator.IContentGenerator;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.IScreenParameter;
import fr.solunea.thaleia.utils.DetailedException;

import java.util.List;

public class ScreenFactoryForTest extends ScreenFactory{


    /**
     * Instancie les classes de traitement de templates qui sont définis dans la
     * conf.
     *
     * @param parameters
     * @param resourcesHandler
     */
    public ScreenFactoryForTest(Parameters parameters, ResourcesHandler resourcesHandler) throws DetailedException {
        super(parameters, resourcesHandler);
    }

    @Override
    protected ClassLoader getClassLoaderHere() {
        return getClass().getClassLoader();
    }


//    public IExcelTemplate parseScreensData(CellsRange cells) throws DetailedException {
    public List<IScreenParameter> parseScreensData(CellsRange cells) throws DetailedException {

//        IExcelTemplate template = prepareTemplate(cells);
        prepareTemplate(cells);

        System.out.println("cannelleScreenParameters: " + cannelleScreenParameters.toString());

//        return template;
        return screenParameters;

    }

    /*protected IContentGenerator getContentGenerator(IExcelTemplate template) throws DetailedException {

        IContentGenerator contentGenerator = new ApportScreenGeneratorForTest();

        return contentGenerator;
    }*/

    @Override
    protected IContentGenerator getContentGeneratorFromProperties(String contentGeneratorClassName) throws DetailedException {
//        IContentGenerator contentGenerator = ClassFactory.getInstanceOf(contentGeneratorClassName,
//                IContentGenerator.class, getClassLoaderHere());
        IContentGenerator contentGenerator = new ApportScreenGeneratorForTest();
        return contentGenerator;
    }



}
