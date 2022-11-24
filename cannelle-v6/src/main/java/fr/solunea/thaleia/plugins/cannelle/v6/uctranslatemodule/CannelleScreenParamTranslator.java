package fr.solunea.thaleia.plugins.cannelle.v6.uctranslatemodule;


import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.IScreenParameter;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.screenparser.CannelleScreenParameters;

import java.util.Optional;

/**
 * Traduit des paramètres sous forme de MAP
 */
public class CannelleScreenParamTranslator {

    private String originLanguage;
    private String targetLanguage;

    public ITranslatorAPI translatorAPI;

    public CannelleScreenParamTranslator from(String originLanguage) {
        this.originLanguage = originLanguage;
        return this;
    }

    public CannelleScreenParamTranslator to(String targetLanguage) {
        this.targetLanguage = targetLanguage;
        return this;
    }

    public CannelleScreenParameters translate(CannelleScreenParameters toTranslateParam) {

        // on modifie l'objet renvoyé, mais il reste référencé dans toTranslateParam
        // voir si on continue à renvoyer ...
        // à la fin on devrait avoir une Map interne de CannelleScreenParameters completement traduite ...

        IScreenParameter paramToTranslate = toTranslateParam.getScreenParameter("monParam");
        Optional<String> textToTranslate = paramToTranslate.getTranslatableValue();

        String textTranslation;
        if (textToTranslate.isPresent()) {
            textTranslation = translatorAPI.from(originLanguage).to(targetLanguage).translate(textToTranslate.get());
            paramToTranslate.setValue(textTranslation);
        }
        return toTranslateParam;
    }

    public void with(ITranslatorAPI translatorAPI) {
        this.translatorAPI = translatorAPI;
    }
}
