package fr.solunea.thaleia.plugins.cannelle.xls.screens;

import fr.solunea.thaleia.model.User;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.v6.uctranslatemodule.CannelleScreenParamTranslator;
import fr.solunea.thaleia.plugins.cannelle.v6.uctranslatemodule.DeeplTranslator;
import fr.solunea.thaleia.plugins.cannelle.v6.uctranslatemodule.ITranslatorAPI;
import fr.solunea.thaleia.utils.DetailedException;
import org.apache.log4j.Logger;

public class ScreenFactoryWithTranslation extends ScreenFactory{

    private static final Logger logger = Logger.getLogger(ScreenFactoryWithTranslation.class);

    public ScreenFactoryWithTranslation(Parameters parameters, ResourcesHandler resourcesHandler, String origLanguage, String targetLanguage) throws DetailedException {
        super(parameters, resourcesHandler, origLanguage, targetLanguage);
    }

    protected void translateScreen(User user) throws DetailedException {
        CannelleScreenParamTranslator translator = new CannelleScreenParamTranslator();
        ITranslatorAPI translatorAPI = getTranslatorAPI(user);
        translator.from(origLanguage).to(targetLanguage).with(translatorAPI);
        translator.translate(cannelleScreenParameters);
    }

    private ITranslatorAPI getTranslatorAPI(User user) {
        ITranslatorAPI deeplTranslator = new DeeplTranslator(user.getThirdPartyServiceKey("DeepL", "DeepL-Auth-Key"));
        return deeplTranslator;
    }
}
