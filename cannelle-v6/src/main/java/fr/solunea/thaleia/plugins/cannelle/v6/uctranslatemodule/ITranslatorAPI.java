package fr.solunea.thaleia.plugins.cannelle.v6.uctranslatemodule;

public interface ITranslatorAPI {

    public ITranslatorAPI from(String originLanguage );
    public ITranslatorAPI to(String originLanguage );

    public String translate(String textToTranslate);
}
