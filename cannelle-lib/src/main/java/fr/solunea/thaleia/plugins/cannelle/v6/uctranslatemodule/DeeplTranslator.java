package fr.solunea.thaleia.plugins.cannelle.v6.uctranslatemodule;

import com.deepl.api.*;
import fr.solunea.thaleia.utils.DetailedException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DeeplTranslator implements ITranslatorAPI {

    private static final Logger logger = Logger.getLogger(DeeplTranslator.class);


    private String originLanguage = "FR";
    private String targetLanguage = "EN-GB";

//    private String authKey = "3bab90c2-a11d-422f-3357-a628955c1ffb:fx";
    private String authKey = "";
    private String deeplUrl = "https://api-free.deepl.com/v2/translate";

    private Translator translator;


    @Override
    public ITranslatorAPI from(String originLanguage) {
        this.originLanguage = originLanguage;
        return this;
    }

    @Override
    public ITranslatorAPI to(String targetLanguage) {
        this.targetLanguage = targetLanguage;
        return this;
    }

    @Override
    public String translate(String textToTranslate) throws DetailedException {
        String translatedText;
        TextTranslationOptions options = new TextTranslationOptions();
        options.isPreserveFormatting();
        options.setTagHandling("xml");
        options.setSentenceSplittingMode(SentenceSplittingMode.valueOf(String.valueOf(SentenceSplittingMode.NoNewlines)));
        TextResult textResult = null;
        try {
            textResult = translator.translateText(textToTranslate, originLanguage, targetLanguage, options );
        } catch (InterruptedException e) {
        } catch (DeepLException e) {
            throw new DetailedException(e).addMessage("Problème de traduction de l'API Deepl");
        }
        translatedText = textResult.getText();
        return translatedText;
    }

    public String translateXML(String textXML) throws URISyntaxException, IOException, InterruptedException {

        StringBuilder paramBuilder = new StringBuilder();

        paramBuilder
                .append("target_lang=" + targetLanguage)
                .append("&")
                .append("source_lang=" + originLanguage)
                .append("&")
                .append("preserve_formatting=1")
                .append("&")
                .append("tag_handling=xml")
                .append("&")
                .append("split_sentences=nonewlines")
                .append("&")
                .append("text=")
                .append(textXML);

//        System.out.println("translation param : " + paramBuilder.toString());
        logger.debug("translation param : " + paramBuilder.toString());

        HttpRequest DeepLRequest = HttpRequest.newBuilder(new URI(deeplUrl))
                .POST(HttpRequest.BodyPublishers.ofString(paramBuilder.toString()))
//                .header("Authorization","DeepL-Auth-Key 3bab90c2-a11d-422f-3357-a628955c1ffb:fx" )
                .header("Authorization","DeepL-Auth-Key " + authKey )
                .header("Content-Type","application/x-www-form-urlencoded")
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(DeepLRequest, HttpResponse.BodyHandlers.ofString());
        String responseJson = response.body();

//        System.out.println("translation response : " + responseJson);
        logger.debug("translation response : " + responseJson);

        return responseJson ;
    }


    public DeeplTranslator(String serviceKey) {
        this.authKey = serviceKey;
    }

    // pour usage en Test
    public DeeplTranslator() {
        this.authKey = "3bab90c2-a11d-422f-3357-a628955c1ffb:fx";
    }
}
