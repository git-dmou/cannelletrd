package fr.solunea.thaleia.plugins.cannelle.v6.helperclassesfortests;

import fr.solunea.thaleia.plugins.cannelle.xls.screens.generator.ApportScreenGenerator;

public class ApportScreenGeneratorForTest extends ApportScreenGenerator {


    @Override
    protected void notifiySession(String message) {
    }

    @Override
    protected String getMessage(String screenId) {
        String message = "";
        return message;
    }

    /*protected A7Content getA7Content(File destination, Locale locale, User user) throws DetailedException {
        A7Content result = fr.solunea.thaleia.plugins.cannelle.v6.helperclassesfortests.ScreenGeneratorUtilsForTest.createA7ContentFromModel(destination, getResourcesHandler(),
                getParametersPrefix(), getParameters(), locale, user);
        return result;
    }*/


}
