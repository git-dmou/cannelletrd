package fr.solunea.thaleia.plugins.cannelle.v6.uctranslatemodule;


import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.IScreenParameter;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.screenparser.CannelleScreenParameters;

import java.util.*;

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

    public CannelleScreenParameters translate(CannelleScreenParameters toTranslateParams) {

        // on modifie l'objet renvoyé, mais il reste référencé dans toTranslateParam
        // voir si on continue à renvoyer ...
        // à la fin on devrait avoir une Map interne de CannelleScreenParameters completement traduite ...


        String paramKey;
        IScreenParameter param;
        Optional<String> textToTranslate;
        String textTranslation;
        Iterator<String> iter = toTranslateParams.iterator();
        List<String> translatableParams = new ArrayList<>();

        prepareTranslatableParams: {
            while (iter.hasNext()) {
                paramKey = iter.next();
                param = toTranslateParams.getScreenParameter(paramKey);
                textToTranslate = param.getTranslatableValue();

                if (textToTranslate.isPresent()) {
                    translatableParams.add(paramKey);
//                    translatableParams.put(paramKey, String.valueOf(textToTranslate));
     //               param.setValue(textTranslation);
                }
            }
        }

//        Iterator<String> translatableParamsIter = translatableParams.keySet().iterator();
        Iterator<String> translatableParamsIter = translatableParams.iterator();
           while (translatableParamsIter.hasNext()) {
               String key = translatableParamsIter.next();
               textToTranslate = toTranslateParams.getScreenParameter(key).getTranslatableValue();
               textTranslation = translatorAPI.from(originLanguage).to(targetLanguage).translate(textToTranslate.get());
               toTranslateParams.getScreenParameter(key).setTranslatableValue(textTranslation);
           }



//        IScreenParameter paramToTranslate = toTranslateParam.getScreenParameter("monParam");

//        String textTranslation;

        return toTranslateParams;
    }

    public void with(ITranslatorAPI translatorAPI) {
        this.translatorAPI = translatorAPI;
    }
}
