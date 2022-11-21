package fr.solunea.thaleia.plugins.cannelle.adaptors.translationExtapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DeepLApiTest {

    private static DeepLTranslator translator;

    @BeforeAll
    static void init() {
        translator = new DeepLTranslator();

    }

    @Nested
    public class DeepLApiTextTest {

        @Test
        public void bonjourShouldReturnHello_FR_to_EN() throws URISyntaxException, IOException, InterruptedException {
            String text;
            given: {
                text = "bonjour";
            }

            List<String> responseJsonList;
            when: {
                responseJsonList = translate(translator, text);
            }

            then: {
                String [] expectedTextsArray = {"hello"};
                List<String> expectedTextsList = Arrays.asList(expectedTextsArray);

                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }

        @Test
        public void voiture_bleuShouldReturnblue_car_FR_to_EN() throws URISyntaxException, IOException, InterruptedException {
            String text;
            given: {
                text = "voiture bleu";
            }

            List<String> responseJsonList;
            when: {
                responseJsonList = translate(translator, text);
            }


            then: {
                String [] expectedTextsArray = {"blue car"};
                List<String> expectedTextsList = Arrays.asList(expectedTextsArray);

                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }

        @Test
        public void TwoTextsShouldReturn2TranslatedTexts_FR_to_EN() throws URISyntaxException, IOException, InterruptedException {
            String text1;
            String text2;
            given: {
                text1 = "voiture bleu";
                text2 = "oeuf rose";
            }

            List<String> responseJsonList;
            when: {
                responseJsonList = translateMultiple(translator, text1, text2);
            }

            then: {
                String [] expectedTextsArray = {"blue car", "pink egg"};
                List<String> expectedTextsList = Arrays.asList(expectedTextsArray);

                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }

        @Test
        public void unKnownWordShouldNOTbeTranslated_FR_to_EN() throws URISyntaxException, IOException, InterruptedException {
            String text;
            given: {
                text = "sdfzcelzerg";
            }

            List<String> responseJsonList;
            when: {
                responseJsonList = translate(translator, text);
            }

            then: {
                String [] expectedTextsArray = {"sdfzcelzerg"};
                List<String> expectedTextsList = Arrays.asList(expectedTextsArray);

                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }

        @Test
        public void symbolsShouldNOTbeTranslated_FR_to_EN1() throws URISyntaxException, IOException, InterruptedException {
            String text;
            given: {
                text = "un < deux";
            }

            List<String> responseJsonList;
            when: {
                responseJsonList = translate(translator, text);
            }

            then: {

                String [] expectedTextsArray = {"one < two"};
                List<String> expectedTextsList = Arrays.asList(expectedTextsArray);

                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }

        @Test
        public void symbolsShouldNOTbeTranslated_FR_to_EN2() throws URISyntaxException, IOException, InterruptedException {
            String text;
            given: {
                text = "un<deux";
            }

            List<String> responseJsonList;
            when: {
                responseJsonList = translate(translator, text);
            }

            then: {

                String [] expectedTextsArray = {"one<two"};
                List<String> expectedTextsList = Arrays.asList(expectedTextsArray);

                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }

        // vérification que les symboles > < ne sont pas interprété comme des balises Html/Xml
        @Test
        public void symbolsShouldNOTbeTranslated_FR_to_EN3() throws URISyntaxException, IOException, InterruptedException {
            String text;
            given: {
                text = "un < deux et quatre > 3";
            }

            List<String> responseJsonList;
            when: {
                responseJsonList = translate(translator, text);
            }

            then: {

                String [] expectedTextsArray = {"one < two and four > 3"};
                List<String> expectedTextsList = Arrays.asList(expectedTextsArray);

                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }

        // vérification que les symboles > < ne sont pas interprété comme des balises Html/Xml
        @Test
        public void symbolsShouldNOTbeTranslated_FR_to_EN4() throws URISyntaxException, IOException, InterruptedException {
            String text;
            given: {
                text = "un <deux et quatre> 3";
            }

            List<String> responseJsonList;
            when: {
                responseJsonList = translate(translator, text);
            }

            then: {

                String [] expectedTextsArray = {"one <two and four> 3"};
                List<String> expectedTextsList = Arrays.asList(expectedTextsArray);

                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }

        @Test
        public void noHandlingOfNewLineChar_FR_to_EN() throws URISyntaxException, IOException, InterruptedException {
            String text;
            given: {
                text = "le matin\nle soir";
            }

            List<String> responseJsonList;
            when: {
                responseJsonList = translate(translator, text);
            }

            then: {

                String [] expectedTextsArray = {"morningevening"};
                List<String> expectedTextsList = Arrays.asList(expectedTextsArray);

                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }

        @Test
        public void htmlTagsShouldNOTbeTranslated_FR_to_EN1() throws URISyntaxException, IOException, InterruptedException {
            String text;
            given: {
                text = "un grand <br> bol de lait";
            }

            List<String> responseJsonList;
            when: {
                responseJsonList = translateWithHtmlTags(translator, text);
            }


            then: {

                String [] expectedTextsArray = {"a large <br> bowl of milk"};
                List<String> expectedTextsList = Arrays.asList(expectedTextsArray);

                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }

        @Test
        public void htmlTagsShouldNOTbeTranslated_FR_to_EN2() throws URISyntaxException, IOException, InterruptedException {
            String text;
            given: {
                text = "un grand <br> bol<br> de lait";
            }

            List<String> responseJsonList;
            when: {
                responseJsonList = translateWithHtmlTags(translator, text);
            }

            then: {

                String [] expectedTextsArray = {"a large <br> bowl<br> of milk"};
                List<String> expectedTextsList = Arrays.asList(expectedTextsArray);

                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }

        // comportement des apostrophes
        @Test
        public void apostrophesTakenIntoAccount_FR_to_EN() throws URISyntaxException, IOException, InterruptedException {
            String text;
            given: {
                text = "c'est dur";
            }

            List<String> responseJsonList;
            when: {
                responseJsonList = translate(translator, text);
            }

            then: {

                String [] expectedTextsArray = {"it's hard"};
                List<String> expectedTextsList = Arrays.asList(expectedTextsArray);

                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }
    }


    /**
     * selon la doc Deepl :
     * le moteur de traduction extrait le texte de chaque balise XML et le traduit séparément
     * - vérifier les tests des textes simples
     * - vérifier le comportement de la balise Html <br> (saut de ligne)
     *        --> conversion du tag <br> en <br/> pour être utilisable ????
     *        --> idem pour tous les autres balises Html de mise en forme ?
     *        (cf https://fr.w3docs.com/apprendre-html/mise-en-forme-html.html)
     *           - <b> et <strong>
     *           - <i> et <em>
     *           - <pre> ? (conservation de la mise en forme)
     *           - <mark>
     *           - <small>
     *           - <del> et <s> (barré)
     *           - <ins> et <u> (souligné)
     *           - <sub> et <sup> (indices et exposants)
     *           - <dfn> mise en italique à la première utilisation
     *           - <p>, <br> et <hr> paragraphe, saut de ligne et ligne horizontale (<hr> ou <hr/> ?)
     * - possibilité de considérer <br> comme non spliting (ne pas séparer le texte qui doit être traduit d'un bloc !)
     * - comportement de <b></b> --> mise en gras dans le texte, non spliting ?
     *
     * - définition de la structure du document de manière explicite, toutes les autres balises ne sont pas prisent en compte
     *      - options : tag_handling=xml, split_sentences=nonewlines, outline_detection=0, splitting_tags=par,title, ...
     */

    @Nested
    public class DeeplXMLApi_AutomaticOutlineDetection_FR_to_EN_Test {

        @Disabled
        @Test
        public void symbolConversionTest() throws URISyntaxException, IOException, InterruptedException {
            // problème, on ne peut pas accéder au texte converti dans le body simplement ...
            // autant tester l'appel à l'API en boîte noire et voir le résultat !
            String text;
            given: {
                text = "un < deux";
            }

            String convertedText;
            when: {
                convertedText = HttpRequest.BodyPublishers.ofString(text).toString();
                System.out.println("texte converti : " + convertedText);

            }

            then: {
                assertThat(convertedText).contains("un &lt; deux");
            }
        }



        @Test
        public void simpleXMLTest() throws URISyntaxException, IOException, InterruptedException {
            String textXML;
            given: {
                textXML = "<texte a traduire>voiture bleu</texte a traduire>";
            }

            List<String> responseJsonList;
            when: {
                responseJsonList = translateXML(translator, textXML);
            }

            then: {

                String [] expectedTextsArray = {"<texte a traduire>blue car</texte a traduire>"};
                List<String> expectedTextsList = Arrays.asList(expectedTextsArray);

                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }

        @Test
        public void simpleXML2TagsTest() throws URISyntaxException, IOException, InterruptedException {
            String textXML;
            given: {
                textXML = "<doc><texte a traduire>voiture bleu</texte a traduire><texte a traduire>oeuf rose</texte a traduire></doc>";
            }

            List<String> responseJsonList;
            when: {
                responseJsonList = translateXML(translator, textXML);
            }

            then: {
                String [] expectedTextsArray = {"<doc><texte a traduire>blue car</texte a traduire><texte a traduire>pink egg</texte a traduire></doc>"};
                List<String> expectedTextsList = Arrays.asList(expectedTextsArray);

                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }

        @Test
        public void simpleXMLUnknownWordsShouldNotBeTranslatedTest() throws URISyntaxException, IOException, InterruptedException {
            String textXML;
            given: {
                textXML = "<doc><texte a traduire>voiture bleu</texte a traduire><texte a traduire>rhcfvrf</texte a traduire></doc>";
            }

            List<String> responseJsonList;
            when: {
                responseJsonList = translateXML(translator, textXML);
            }

            then: {

                String [] expectedTextsArray = {"<doc><texte a traduire>blue car</texte a traduire><texte a traduire>rhcfvrf</texte a traduire></doc>"};
                List<String> expectedTextsList = Arrays.asList(expectedTextsArray);

                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }

        @Test
        public void simpleXMLLesserThanSymbolConvertedBeforeTranslationTest1() throws URISyntaxException, IOException, InterruptedException {
            String textXML;
            given: {
                textXML = "<doc><texte a traduire>voiture bleu</texte a traduire><texte a traduire>un < deux</texte a traduire></doc>";
            }

            List<String> responseJsonList;
            when: {
                responseJsonList = translateXML(translator, textXML);
            }

            then: {

                String [] expectedTextsArray = {"<doc><texte a traduire>blue car</texte a traduire><texte a traduire>one &lt; two</texte a traduire></doc>"};
                List<String> expectedTextsList = Arrays.asList(expectedTextsArray);

                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }

        @Test
        public void simpleXMLLesserThanSymbolConvertedBeforeTranslationTest2() throws URISyntaxException, IOException, InterruptedException {
            String textXML;
            given: {
                textXML = "<doc><texte a traduire>voiture bleu</texte a traduire><texte a traduire>un<deux</texte a traduire></doc>";
            }

            List<String> responseJsonList;
            when: {
                responseJsonList = translateXML(translator, textXML);
            }

            then: {

                String [] expectedTextsArray = {"<doc><texte a traduire>blue car</texte a traduire><texte a traduire>one&lt;two</texte a traduire></doc>"};
                List<String> expectedTextsList = Arrays.asList(expectedTextsArray);

                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }

        @Test
        public void simpleXML_lt_followedBy_gt_symbols_interpretedAsTag() throws URISyntaxException, IOException, InterruptedException {
            // mais ce comportement donne un résultat correct en Traduction !!!
            // car < et > conservent le sens attendu !

            String textXML;
            given: {
                textXML = "<doc><texte a traduire>voiture bleu</texte a traduire><texte a traduire>un < deux et 3 > deux</texte a traduire></doc>";
            }

            List<String> responseJsonList;
            when: {
                responseJsonList = translateXML(translator, textXML);
            }

            then: {

                String [] expectedTextsArray = {"<doc><texte a traduire>blue car</texte a traduire><texte a traduire>one < deux et 3 > two</texte a traduire></doc>"};
                List<String> expectedTextsList = Arrays.asList(expectedTextsArray);

                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }

        @Test
        public void simpleXML_lt_followedBy_gt_symbols_inDifferentTagsConsideredAsAlone() throws URISyntaxException, IOException, InterruptedException {
            // comportement souhaité

            String textXML;
            given: {
                textXML = "<doc><texte a traduire>un < deux</texte a traduire><texte a traduire>3 > deux</texte a traduire></doc>";
            }

            List<String> responseJsonList;
            when: {
                responseJsonList = translateXML(translator, textXML);
            }

            then: {

                String [] expectedTextsArray = {"<doc><texte a traduire>one &lt; two</texte a traduire><texte a traduire>3 &gt; two</texte a traduire></doc>"};
                List<String> expectedTextsList = Arrays.asList(expectedTextsArray);

                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }

        @Test
        public void simpleXMLnoHandlingOfNewLineChar() throws URISyntaxException, IOException, InterruptedException {
            // "\n" remplacé par "/" !!!

            String textXML;
            given: {
                textXML = "<doc><texte a traduire>voiture bleu</texte a traduire><texte a traduire>le matin\nle soir</texte a traduire></doc>";
            }

            List<String> responseJsonList;
            when: {
                responseJsonList = translateXML(translator, textXML);
            }

            then: {

                String [] expectedTextsArray = {"<doc><texte a traduire>blue car</texte a traduire><texte a traduire>morning/evening</texte a traduire></doc>"};
                List<String> expectedTextsList = Arrays.asList(expectedTextsArray);

                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }

        @Test
        public void simpleXML_formatingTags() throws URISyntaxException, IOException, InterruptedException {
            String textXML;
            given: {
                textXML = "<doc><texte a traduire>voiture bleu</texte a traduire><texte a traduire>un grand <br> bol de lait</texte a traduire></doc>";
            }

            List<String> responseJsonList;
            when: {
                responseJsonList = translateXML(translator, textXML);
            }

            then: {

                String [] expectedTextsArray = {"<doc><texte a traduire>blue car</texte a traduire><texte a traduire>a large <br> bowl of milk</texte a traduire></doc>"};
                List<String> expectedTextsList = Arrays.asList(expectedTextsArray);

                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }

        @Test
        public void simpleXML_longText() throws URISyntaxException, IOException, InterruptedException {
            String textXML;
            given: {
                textXML = "<doc><texte a traduire>voiture bleu</texte a traduire><texte a traduire>Présenter les caractéristiques d'une solution de Classe Virtuelle et d'en comprendre l'utilité</texte a traduire></doc>";
            }

            List<String> responseJsonList;
            when: {
                responseJsonList = translateXML(translator, textXML);
            }

            then: {

                String [] expectedTextsArray = {"<doc><texte a traduire>blue car</texte a traduire><texte a traduire>Present the characteristics of a Virtual Classroom solution and understand its usefulness</texte a traduire></doc>"};
                List<String> expectedTextsList = Arrays.asList(expectedTextsArray);

                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }

        @Test
        public void simpleXML_longTextWithTags() throws URISyntaxException, IOException, InterruptedException {
            String textXML;
            given: {
                textXML = "<doc><texte a traduire>voiture bleu</texte a traduire><texte a traduire>Présenter les caractéristiques <b>d'une solution de Classe Virtuelle et d'en</b> comprendre l'utilité</texte a traduire></doc>";
            }

            List<String> responseJsonList;
            when: {
                responseJsonList = translateXML(translator, textXML);
            }

            then: {

                String [] expectedTextsArray = {"<doc><texte a traduire>blue car</texte a traduire><texte a traduire>Present the characteristics of <b>a Virtual Classroom solution and</b> understand <b>its</b> usefulness</texte a traduire></doc>"};
                List<String> expectedTextsList = Arrays.asList(expectedTextsArray);

                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }

        @Test
        public void simpleXML_longTextWithTags_trickyTranslationOf_reussir() throws URISyntaxException, IOException, InterruptedException {
            String textXML;
            given: {
                textXML = "<doc><texte a traduire>voiture bleu</texte a traduire><Moyens mis en œuvre>Ce module dure envrion 10 minutes avec une partie explicative et <b>une partie quiz. Pour réussir</b> cette formation vous devez atteindre un score de 80% minimum.</Moyens mis en œuvre></doc>";
            }

            List<String> responseJsonList;
            when: {
                responseJsonList = translateXML(translator, textXML);
            }

            then: {

                String [] expectedTextsArray = {"<doc><texte a traduire>blue car</texte a traduire><Moyens mis en œuvre>This module lasts about 10 minutes with an explanatory part and <b>a quiz part. To pass</b> this course you must reach a score of at least 80%.</Moyens mis en œuvre></doc>"};
                List<String> expectedTextsList = Arrays.asList(expectedTextsArray);

                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }

        @Test
        public void simpleXML_check_reussir_translation() throws URISyntaxException, IOException, InterruptedException {
            String textXML;
            given: {
                textXML = "<doc><texte a traduire>voiture bleu</texte a traduire><texte a traduire>réussir</texte a traduire></doc>";
            }

            List<String> responseJsonList;
            when: {
                responseJsonList = translateXML(translator, textXML);
            }

            then: {

                String [] expectedTextsArray = {"<doc><texte a traduire>blue car</texte a traduire><texte a traduire>succeed</texte a traduire></doc>"};
                List<String> expectedTextsList = Arrays.asList(expectedTextsArray);

                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }

        @Test
        public void simpleXML_apostrophesTakenIntoAccount() throws URISyntaxException, IOException, InterruptedException {
            String textXML;
            given: {
                textXML = "<doc><texte a traduire>voiture bleu</texte a traduire><texte a traduire>c'est dur</texte a traduire></doc>";
            }

            List<String> responseJsonList;
            when: {
                responseJsonList = translateXML(translator, textXML);
            }

            then: {
                String [] expectedTextsArray = {"<doc><texte a traduire>blue car</texte a traduire><texte a traduire>it's hard</texte a traduire></doc>"};
                List<String> expectedTextsList = Arrays.asList(expectedTextsArray);

                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }
    }

    private List<String> translateWithHtmlTags(DeepLTranslator translator, String text) throws URISyntaxException, IOException, InterruptedException {
        String responseJson;
        List<String> responseJsonList;

        responseJson = translator.translateWithHtmlTags(text);
        System.out.println("responseJson : " + responseJson);
        responseJsonList = formatTranslationResponseToTextsList(responseJson);
        return responseJsonList;
    }

    private List<String> translateMultiple(DeepLTranslator translator, String text1, String text2) throws URISyntaxException, IOException, InterruptedException {
        String responseJson;
        List<String> responseJsonList;

        responseJson = translator.translateMultiple(text1, text2);
        System.out.println("responseJson : " + responseJson);
        responseJsonList = formatTranslationResponseToTextsList(responseJson);

        return responseJsonList;
    }

    private List<String> translate(DeepLTranslator translator, String text) throws URISyntaxException, IOException, InterruptedException {
        String responseJson;
        List<String> responseJsonList;

        responseJson = translator.translate(text);
        System.out.println("responseJson : " + responseJson);
        responseJsonList = formatTranslationResponseToTextsList(responseJson);

        return responseJsonList;
    }

    private List<String> translateXML(DeepLTranslator translator, String text) throws URISyntaxException, IOException, InterruptedException {
        String responseJson;
        List<String> responseJsonList;

        responseJson = translator.translateXML(text);
        System.out.println("responseJson : " + responseJson);
        responseJsonList = formatTranslationResponseToTextsList(responseJson);

        return responseJsonList;
    }

    private List<String> formatTranslationResponseToTextsList(String jsonArraySting) throws JsonProcessingException {
        ObjectMapper jsonMapper = new ObjectMapper();
        JsonNode rootNode = jsonMapper.readTree(jsonArraySting);
        JsonNode translationDataArray = rootNode.path("translations");
        Iterator<JsonNode> elements = translationDataArray.elements();
        List<String> translatedTexts = new ArrayList<>();
        while (elements.hasNext()) {
            JsonNode textTranslationData = elements.next();
            translatedTexts.add(textTranslationData.get("text").asText());
        }

        return translatedTexts;
    }


    static class DeepLTranslator {

        public String translate(String text) throws URISyntaxException, IOException, InterruptedException {

            StringBuilder paramBuilder = new StringBuilder();

            paramBuilder
                    .append("target_lang=EN")
                    .append("&")
                    .append("source_lang=FR")
                    .append("&")
                    .append("preserve_formatting=1")
                    .append("&")
                    .append("text=")
                    .append(text);

            System.out.println("translation param : " + paramBuilder.toString());

            /*HttpRequest DeepLRequest = HttpRequest.newBuilder(new URI("https://api-free.deepl.com/v2/translate"))
                    .POST(HttpRequest.BodyPublishers.ofString("text=" + text + "&" + "target_lang=EN" + "&" + "source_lang=FR"
                            + "&" + "preserve_formatting=1"))
                    .header("Authorization","DeepL-Auth-Key 3bab90c2-a11d-422f-3357-a628955c1ffb:fx" )
                    .header("Content-Type","application/x-www-form-urlencoded")
                    .build();*/

            HttpRequest DeepLRequest = HttpRequest.newBuilder(new URI("https://api-free.deepl.com/v2/translate"))
                    .POST(HttpRequest.BodyPublishers.ofString(paramBuilder.toString()))
                    .header("Authorization","DeepL-Auth-Key 3bab90c2-a11d-422f-3357-a628955c1ffb:fx" )
                    .header("Content-Type","application/x-www-form-urlencoded")
                    .build();

            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> response = client.send(DeepLRequest, HttpResponse.BodyHandlers.ofString());
            String responseJson = response.body();
            return responseJson ;
        }

        public String translateWithHtmlTags(String text) throws URISyntaxException, IOException, InterruptedException {

            StringBuilder paramBuilder = new StringBuilder();

            paramBuilder
                    .append("target_lang=EN")
                    .append("&")
                    .append("source_lang=FR")
                    .append("&")
                    .append("preserve_formatting=1")
                    .append("&")
                    .append("tag_handling=html")
                    .append("&")
                    .append("text=")
                    .append(text);

            System.out.println("translation param : " + paramBuilder.toString());

            HttpRequest DeepLRequest = HttpRequest.newBuilder(new URI("https://api-free.deepl.com/v2/translate"))
                    .POST(HttpRequest.BodyPublishers.ofString(paramBuilder.toString()))
                    .header("Authorization","DeepL-Auth-Key 3bab90c2-a11d-422f-3357-a628955c1ffb:fx" )
                    .header("Content-Type","application/x-www-form-urlencoded")
                    .build();

            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> response = client.send(DeepLRequest, HttpResponse.BodyHandlers.ofString());
            String responseJson = response.body();
            return responseJson ;

        }

        public String translateMultiple(String text1, String text2 ) throws URISyntaxException, IOException, InterruptedException {

            StringBuilder paramBuilder = new StringBuilder();

            paramBuilder
                    .append("target_lang=EN")
                    .append("&")
                    .append("source_lang=FR")
                    .append("&")
                    .append("preserve_formatting=1")
                    .append("&")
                    .append("text=")
                    .append(text1)
                    .append("&")
                    .append("text=")
                    .append(text2);

            System.out.println("translation param : " + paramBuilder.toString());

            HttpRequest DeepLRequest = HttpRequest.newBuilder(new URI("https://api-free.deepl.com/v2/translate"))
                    .POST(HttpRequest.BodyPublishers.ofString(paramBuilder.toString()))
                    .header("Authorization","DeepL-Auth-Key 3bab90c2-a11d-422f-3357-a628955c1ffb:fx" )
                    .header("Content-Type","application/x-www-form-urlencoded")
                    .build();

            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> response = client.send(DeepLRequest, HttpResponse.BodyHandlers.ofString());
            String responseJson = response.body();
            return responseJson ;
        }


        public String translateXML(String textXML) throws URISyntaxException, IOException, InterruptedException {

            StringBuilder paramBuilder = new StringBuilder();

            paramBuilder
                    .append("target_lang=EN")
                    .append("&")
                    .append("source_lang=FR")
                    .append("&")
                    .append("preserve_formatting=1")
                    .append("&")
                    .append("tag_handling=xml")
                    .append("&")
                    .append("split_sentences=nonewlines")
                    .append("&")
                    .append("text=")
                    .append(textXML);

            System.out.println("translation param : " + paramBuilder.toString());

            HttpRequest DeepLRequest = HttpRequest.newBuilder(new URI("https://api-free.deepl.com/v2/translate"))
                    .POST(HttpRequest.BodyPublishers.ofString(paramBuilder.toString()))
                    .header("Authorization","DeepL-Auth-Key 3bab90c2-a11d-422f-3357-a628955c1ffb:fx" )
                    .header("Content-Type","application/x-www-form-urlencoded")
                    .build();

            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> response = client.send(DeepLRequest, HttpResponse.BodyHandlers.ofString());
            String responseJson = response.body();
            return responseJson ;
        }


    }
}
