package fr.solunea.thaleia.plugins.cannelle.adaptors.translationExtapi;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TranslatorsGateway {

    public String translate(String text) throws URISyntaxException, IOException, InterruptedException {



        HttpRequest DeepLRequest = HttpRequest.newBuilder(new URI("https://api-free.deepl.com/v2/translate"))
                .POST(HttpRequest.BodyPublishers.ofString("text=" + text + "&" + "target_lang=EN" + "&" + "source_lang=FR"
                + "&" + "preserve_formatting=1"))
                .header("Authorization","DeepL-Auth-Key 3bab90c2-a11d-422f-3357-a628955c1ffb:fx" )
                .header("Content-Type","application/x-www-form-urlencoded")
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(DeepLRequest, HttpResponse.BodyHandlers.ofString());
        return response.body() ;
    }
}
