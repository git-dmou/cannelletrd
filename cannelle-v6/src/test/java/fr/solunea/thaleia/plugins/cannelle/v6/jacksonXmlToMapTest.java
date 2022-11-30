package fr.solunea.thaleia.plugins.cannelle.v6;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class jacksonXmlToMapTest {


    @Test
    public void noHeaderOneTagConversionXmlToMap () throws JsonProcessingException {
        // la balise est considéré comme un header, donc la map n'a pas le résultat attendu !

        String xmlString = "<a>bonjour</a>";
        XmlMapper mapper = new XmlMapper();
        Map<String, String> param = mapper.readValue(xmlString, Map.class);
        System.out.println("param : " + param.toString());

        assertThat(param.get("a")).isEqualTo(null);
        assertThat(param.get("")).isEqualTo("bonjour");
    }

    @Test
    public void withHeaderOneTagConversionXmlToMap () throws JsonProcessingException {

        String xmlString = "<h><a>bonjour</a></h>";
        XmlMapper mapper = new XmlMapper();
        Map<String, String> param = mapper.readValue(xmlString, Map.class);
        System.out.println("param : " + param.toString());

        assertThat(param.get("a")).isEqualTo("bonjour");
        assertThat(param.get("h")).isEqualTo(null);
    }

    @Test
    public void withHeader2TagConversionXmlToMap () throws JsonProcessingException {

        String xmlString = "<h><a>bonjour</a><b>au revoir</b></h>";
        XmlMapper mapper = new XmlMapper();
        Map<String, String> param = mapper.readValue(xmlString, Map.class);
        System.out.println("param : " + param.toString());

        assertThat(param.get("a")).isEqualTo("bonjour");
        assertThat(param.get("b")).isEqualTo("au revoir");
        assertThat(param.get("h")).isEqualTo(null);
    }

    @Test
    public void with2Header2TagConversionXmlToMap () throws JsonProcessingException {
//        résultat = 2 MAP imbriquées !
//        interessant pour faire du multi écran !

        String xmlString = "<h><h2><a>bonjour</a><b>au revoir</b></h2></h>";
        XmlMapper mapper = new XmlMapper();
        Map<String, Map<String, String>> param = mapper.readValue(xmlString, Map.class);
        System.out.println("param : " + param.toString());

        Map<String,String> param2 = param.get("h2");

        assertThat(param2.get("a")).isEqualTo("bonjour");
        assertThat(param2.get("b")).isEqualTo("au revoir");
        assertThat(param2.get("h")).isEqualTo(null);
    }

    @Test
    public void headerWrapping2HeadersOf2TagsConversionXmlToMap () throws JsonProcessingException {
//        résultat = 2 MAP imbriquées !
//        interessant pour faire du multi écran !

        String xmlString = "<h><h2><a>bonjour</a><b>au revoir</b></h2><h3><a>rouge</a><c>bleue</c></h3></h>";
        XmlMapper mapper = new XmlMapper();
        Map<String, Map<String, String>> param = mapper.readValue(xmlString, Map.class);
        System.out.println("param : " + param.toString());

        Map<String,String> param2 = param.get("h2");

        assertThat(param2.get("a")).isEqualTo("bonjour");
        assertThat(param2.get("b")).isEqualTo("au revoir");
        assertThat(param2.get("h")).isEqualTo(null);

        Map<String,String> param3 = param.get("h3");

        assertThat(param3.get("a")).isEqualTo("rouge");
        assertThat(param3.get("c")).isEqualTo("bleue");

    }

    @Test
    public void TwoHeaderWrapping2HeadersOf2TagsConversionXmlToMapAvoidingTheFirst () throws JsonProcessingException {
//        résultat = 2 MAP imbriquées !
//        interessant pour faire du multi écran !

//        on tente d'éviter la premier header qui est surnuméraire et qui va perturber le traitement

        String xmlString = "<h><k><h2><a>bonjour</a><b>au revoir</b></h2></k></h>";
        XmlMapper mapper = new XmlMapper();
//        JsonNode = new


        Map<String, Map<String, String>> param = mapper.readValue(xmlString, Map.class);
        System.out.println("param : " + param.toString());

//        Map<String,String> param2 = param.get("h2");

        assertThat(param.get("a")).isEqualTo("bonjour");
        assertThat(param.get("b")).isEqualTo("au revoir");
        assertThat(param.get("h")).isEqualTo(null);

    }






}
