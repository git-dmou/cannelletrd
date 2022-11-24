package fr.solunea.thaleia.plugins.cannelle.v6.helperclassesfortests;

import fr.solunea.thaleia.plugins.cannelle.v6.uctranslatemodule.ITranslatorAPI;

import java.util.HashMap;
import java.util.Map;

public class FakeDeeplTranslator implements ITranslatorAPI {

    Map<String, String> dic = new HashMap<>();


    public FakeDeeplTranslator() {
        dic.put("voiture bleue", "blue car");
        dic.put("cheval vert", "green horse");

    }

    @Override
    public ITranslatorAPI from(String originLanguage) {
        return this;
    }

    @Override
    public ITranslatorAPI to(String originLanguage) {
        return this;
    }

    @Override
    public String translate(String textToTranslate) {

       return dic.get(textToTranslate);
    }
}
