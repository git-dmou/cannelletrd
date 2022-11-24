package fr.solunea.thaleia.plugins.cannelle.v6.helperclassesfortests;

import fr.solunea.thaleia.plugins.cannelle.v6.uctranslatemodule.ITranslatorAPI;

import java.util.HashMap;
import java.util.Map;

public class FakeDeeplTranslator implements ITranslatorAPI {

    Map<String, String> dic = new HashMap<>();


    public FakeDeeplTranslator() {
        dic.put("voiture bleue", "blue car");
        dic.put("cheval vert", "green horse");
//        dic.put("<monParam1>voiture bleue</monParam1><monParam2>cheval vert</monParam2>", "<monParam1>blue car</monParam1><monParam2>green horse</monParam2>");
        dic.put("<params><monParam1>voiture bleue</monParam1><monParam2>cheval vert</monParam2><monParam3>chien jaune</monParam3>", "<monParam1>blue car</monParam1><monParam2>green horse</monParam2><monParam3>yellow dog</monParam3></params>");

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

//       return dic.get(textToTranslate);
        if (dic.containsKey(textToTranslate)) {
            return dic.get(textToTranslate);
        } else {
            return "";
        }
    }
}
