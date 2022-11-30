package fr.solunea.thaleia.plugins.cannelle.v6.helperclassesfortests;

import fr.solunea.thaleia.plugins.cannelle.v6.uctranslatemodule.ITranslatorAPI;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class FakeDeeplTranslator implements ITranslatorAPI {

    Map<String, String> dic = new HashMap<>();


    public FakeDeeplTranslator() {
        dic.put("voiture bleue", "blue car");
        dic.put("cheval vert", "green horse");
        dic.put("<screenstranslation><screendata/></screenstranslation>", "<screenstranslation><screendata/></screenstranslation>");
        dic.put("<screenstranslation><screendata><monParam1>voiture bleue</monParam1></screendata></screenstranslation>", "<screenstranslation><screendata><monParam1>blue car</monParam1></screendata></screenstranslation>");
        dic.put("<screenstranslation><screendata><safeKeyParam1>voiture bleue</safeKeyParam1></screendata></screenstranslation>", "<screenstranslation><screendata><safeKeyParam1>blue car</safeKeyParam1></screendata></screenstranslation>");
        dic.put("<screenstranslation><screendata><monParam1>voiture bleue</monParam1><monParam2>cheval vert</monParam2></screendata></screenstranslation>", "<screenstranslation><screendata><safeKeyParam2>green horse</safeKeyParam2><safeKeyParam1>blue car</safeKeyParam1></screendata></screenstranslation>");
        dic.put("<screenstranslation><screendata><safeKeyParam2>cheval vert</safeKeyParam2><safeKeyParam1>voiture bleue</safeKeyParam1></screendata></screenstranslation>", "<screenstranslation><screendata><safeKeyParam1>blue car</safeKeyParam1><safeKeyParam2>green horse</safeKeyParam2></screendata></screenstranslation>");
        dic.put("<screenstranslation><screendata><safeKeyParam3>chien jaune</safeKeyParam3><safeKeyParam2>cheval vert</safeKeyParam2><safeKeyParam1>voiture bleue</safeKeyParam1></screendata></screenstranslation>", "<screenstranslation><screendata><safeKeyParam3>yellow dog</safeKeyParam3><safeKeyParam2>green horse</safeKeyParam2><safeKeyParam1>blue car</safeKeyParam1></screendata></screenstranslation>");
        dic.put("<screenstranslation><screendata><safeKeyParam3>chien jaune</safeKeyParam3><safeKeyParam2>cheval vert</safeKeyParam2><safeKeyParam1>voiture bleue</safeKeyParam1></screendata></screenstranslation>", "<screenstranslation><screendata><safeKeyParam3>yellow dog</safeKeyParam3><safeKeyParam2>green horse</safeKeyParam2><safeKeyParam1>blue car</safeKeyParam1></screendata></screenstranslation>");

    }

    @Override
    public ITranslatorAPI from(String originLanguage) {
        return this;
    }

    @Override
    public ITranslatorAPI to(String targetLanguage) {
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

    @Override
    public String translateXML(String textXML) throws URISyntaxException, IOException, InterruptedException {
        return null;
    }
}
