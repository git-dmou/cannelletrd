package fr.solunea.thaleia.plugins.cannelle.v6.uctranslatemodule;

import fr.solunea.thaleia.utils.DetailedException;

import java.io.IOException;
import java.net.URISyntaxException;

public interface ITranslatorAPI {

    public ITranslatorAPI from(String originLanguage );
    public ITranslatorAPI to(String targetLanguage );

    public String translate(String textToTranslate) throws DetailedException;

    String translateXML(String textXML) throws URISyntaxException, IOException, InterruptedException;
}
