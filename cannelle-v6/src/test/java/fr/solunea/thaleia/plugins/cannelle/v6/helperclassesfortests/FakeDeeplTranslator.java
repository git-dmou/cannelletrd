package fr.solunea.thaleia.plugins.cannelle.v6.helperclassesfortests;

import fr.solunea.thaleia.plugins.cannelle.v6.uctranslatemodule.ITranslatorAPI;

public class FakeDeeplTranslator implements ITranslatorAPI {
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
        return "blue car";
    }
}
