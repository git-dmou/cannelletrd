package fr.solunea.thaleia.plugins.cannelle.v6.uctranslatemodule;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.IScreenParameter;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.screenparser.CannelleScreenParameters;
import fr.solunea.thaleia.utils.DetailedException;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Traduit des paramètres sous forme de MAP
 */
public class CannelleScreenParamTranslator {

    private final static Logger logger = Logger.getLogger(CannelleScreenParamTranslator.class);

    private String originLanguage;
    private String targetLanguage;

    public ITranslatorAPI translatorAPI;

    private Map<String, String> htmlTagsSubstition = new HashMap<>();




    public CannelleScreenParamTranslator() {
        htmlTagsSubstition.put("<span>", " infspansup ");
        htmlTagsSubstition.put("</span>", " infslspansup ");
        htmlTagsSubstition.put("<p>", " infpansup ");
        htmlTagsSubstition.put("</p>", " infslpansup ");
        htmlTagsSubstition.put("<sub>", " infsbsup ");
        htmlTagsSubstition.put("</sub>", " infslsbsup ");
        htmlTagsSubstition.put("<sup>", " infspsup ");
        htmlTagsSubstition.put("</sup>", " infslspsup ");
        htmlTagsSubstition.put("<small>", " infsmlsup ");
        htmlTagsSubstition.put("</small>", " infslsmlsup ");
        htmlTagsSubstition.put("<mark>", " infmksup ");
        htmlTagsSubstition.put("</mark>", " infslmksup ");
        htmlTagsSubstition.put("<pre>", " infprsup ");
        htmlTagsSubstition.put("</pre>", " infslprsup ");
        htmlTagsSubstition.put("<strong>", " infstsup ");
        htmlTagsSubstition.put("</strong>", " infslstsup ");
        htmlTagsSubstition.put("<b>", " infbsup ");
        htmlTagsSubstition.put("</b>", " infslbsup ");
        htmlTagsSubstition.put("<i>", " infjsup ");
        htmlTagsSubstition.put("</i>", " infsljsup ");
        htmlTagsSubstition.put("<u>", " infvsup ");
        htmlTagsSubstition.put("</u>", " infslvsup ");
        htmlTagsSubstition.put("<ins>", " infnssup ");
        htmlTagsSubstition.put("</ins>", " infslnssup ");
        htmlTagsSubstition.put("<s>", " infssup ");
        htmlTagsSubstition.put("</s>", " infslssup ");
        htmlTagsSubstition.put("<del>", " infdlsup ");
        htmlTagsSubstition.put("</del>", " infsldlsup ");
        htmlTagsSubstition.put("<em>", " infksup ");
        htmlTagsSubstition.put("</em>", " infslksup ");
        htmlTagsSubstition.put("<br>", " infbrsup ");
        htmlTagsSubstition.put("</br>", " infslbrsup ");
        htmlTagsSubstition.put("<br/>", " infbrslsup ");
        htmlTagsSubstition.put("<hr>", " infslhrsup ");
        htmlTagsSubstition.put("<hr/>", " infhrslsup ");
        htmlTagsSubstition.put("<true>", " inftruesup ");
        htmlTagsSubstition.put("<false>", " inffalsesup ");
        htmlTagsSubstition.put("\\.\\.\\.", " ptptpt "); // substitution des "...", si non escapé, tout le contenu est remplacé par "ptptpt ptptpt ..." !!!
        htmlTagsSubstition.put("\\n", " lnst ");
        htmlTagsSubstition.put(String.valueOf('"'), " qtqt ");
        htmlTagsSubstition.put("vars", "vars");
//        htmlTagsSubstition.put("[[]name[]]", "[n]");
//        htmlTagsSubstition.put("[[]date[]]", "[d]");

    }

    public CannelleScreenParamTranslator from(String originLanguage) {
        this.originLanguage = originLanguage;
        return this;
    }

    public CannelleScreenParamTranslator to(String targetLanguage) {
        this.targetLanguage = targetLanguage;
        return this;
    }

    public CannelleScreenParameters translate(CannelleScreenParameters toTranslateParams) throws DetailedException {

        // on modifie l'objet renvoyé, mais il reste référencé dans toTranslateParam
        // voir si on continue à renvoyer ...
        // à la fin on devrait avoir une Map interne de CannelleScreenParameters completement traduite ...


        String paramLocaleKey;
        IScreenParameter param;
        Optional<String> textToTranslate;
        String textToTranslateCleaned;
        String safeKey;
        String textTranslation = "";
        Iterator<String> iter = toTranslateParams.iterator();
//        List<String> translatableParams = new ArrayList<>();
        Map<String, String> safeLocaleKeys = new HashMap<>();
        Map<String, String> safeTranslatableParams = new HashMap<>();

        prepareTranslatableParams: {
            while (iter.hasNext()) {
                paramLocaleKey = iter.next();
                param = toTranslateParams.getScreenParameter(paramLocaleKey);
                textToTranslate = param.getTranslatableValue();
                safeKey = param.getSafeKey();

                if (textToTranslate.isPresent() && !textToTranslate.get().equals("")) {
//                    textToTranslateCleaned = cleanHtmlTags(textToTranslate.get());
                    textToTranslateCleaned = cleanHtmlTags(textToTranslate.orElse(""));
                    safeTranslatableParams.put(safeKey, textToTranslateCleaned);
                    safeLocaleKeys.put(safeKey, paramLocaleKey);
                }
            }
        }

//        Iterator<String> translatableParamsIter = translatableParams.keySet().iterator();
//        Iterator<String> translatableParamsIter = safeTranslatableParams.iterator();


        if (isThereSomethingToTranslate(safeTranslatableParams)) {
            String textToTranslateXML = convertParamToXML(safeTranslatableParams);
            try {
                textTranslation = translatorAPI.from(originLanguage).to(targetLanguage).translateXML(textToTranslateXML);
            } catch (Exception e) {
                logger.debug("probleme de traduction ");
            }
            integrateXmlTranslationIntoCannelleScreenParam(textTranslation, toTranslateParams, safeLocaleKeys);
        }


//      les parametres traduits ont été substitués dans la l'instance de toTranslateParams (CannelleScreenParameters)
        return toTranslateParams;
    }

    private static boolean isThereSomethingToTranslate(Map<String, String> safeTranslatableParams) {
        return safeTranslatableParams.size() > 0;
    }

    private String cleanHtmlTags(String s) {
        String safeString = s;
        for (String htmlTag : htmlTagsSubstition.keySet()
             ) {
            if (htmlTag.equals("vars")) {
                safeString = substituteBracketsVars(safeString);
            } else {
                String safeHtmlTag = htmlTagsSubstition.get(htmlTag);
                safeString = safeString.replaceAll(htmlTag, safeHtmlTag);
            }
        }
        return safeString;
    }

    private String substituteBracketsVars(String stringToProcess) {
        String result = stringToProcess;
        result = result.replaceAll("\\[firstname\\]","["+"f"+"]");
        result = result.replaceAll("\\[name\\]","["+"n"+"]");
        result = result.replaceAll("\\[date\\]","["+"d"+"]");
        return result;
    }

    private String substituteBracketsVarsBack(String stringToProcess) {
        String result = stringToProcess;
        result = result.replaceAll("\\[f\\]","["+"firstname"+"]");
        result = result.replaceAll("\\[n\\]","["+"name"+"]");
        result = result.replaceAll("\\[d\\]","["+"date"+"]");
        return result;
    }


    private void integrateXmlTranslationIntoCannelleScreenParam(String textTranslation, CannelleScreenParameters toTranslateParams, Map<String, String> safeLocaleKeys) throws DetailedException {
        ObjectMapper jsonMapper = new ObjectMapper();
        String translationString = "";
//        screenData = new HashMap<>();
        Map<String, String> screenData = null;
        try {

            JsonNode rootNode = jsonMapper.readTree(textTranslation);
            JsonNode translationDataArray = rootNode.path("translations");
            Iterator<JsonNode> elements = translationDataArray.elements();

            translationString = String.valueOf(elements.next().get("text").asText());

            XmlMapper xmlMapper = new XmlMapper();
            //            ScreenData screenData = xmlMapper.readValue(textTranslation, ScreenData.class);
//            screenData = null;
//            JsonNode screenData = xmlMapper.readTree(textTranslation);
            Map<String, Map<String, String>> screensData = xmlMapper.readValue(translationString, Map.class);
            screenData = screensData.get("screendata");

        } catch (Exception e) {
//            throw new DetailedException("problème retour API traduction").addMessage("traduction : " + textTranslation);
            System.out.println("problème retour API traduction, traduction : " + translationString);
        }

        HtmlTagsSubstitution(screenData);

        writeTranslationToScreenParam(screenData, toTranslateParams, safeLocaleKeys);

    }

    private void HtmlTagsSubstitution(Map<String, String> screenData) {
        Iterator<String> keyIterator = screenData.keySet().stream().iterator() ;
        String key;
        String taggedValue;
        while (keyIterator.hasNext()) {
            key = keyIterator.next();
            taggedValue = substituteTag(screenData.get(key));
            screenData.put(key, taggedValue);
        }
    }

    private String substituteTag(String s) {
        String tagged_s = s;
        Iterator<Map.Entry<String, String>> substitutionIterator = htmlTagsSubstition.entrySet().iterator();
        Map.Entry<String, String> subtitutionKeyValue;
        String quoteSubsText = htmlTagsSubstition.get(String.valueOf('"'));
        String quotedTextFindRegex = quoteSubsText.stripLeading() + "(.*?)" + quoteSubsText.stripTrailing();
        Pattern p = Pattern.compile(quotedTextFindRegex);
        Matcher matcher = p.matcher(tagged_s);

        quoteSubstitutionBeforeEveryThing:{
            while (matcher.find()) {
//            String replacedQuoteRegex =   subtitutionStringForTag.stripLeading() + matcher.group(1) + subtitutionStringForTag.stripTrailing() ;
//                String replacedQuoteRegex = quoteSubsText.stripLeading() + matcher.group(1) + quoteSubsText.stripTrailing();
                String replacedQuoteRegex = matcher.group(0);
                String newQuotedText = " \"" + matcher.group(1) + "\" ";
                tagged_s = tagged_s.replaceAll(replacedQuoteRegex, newQuotedText);
            }
        }

        while(substitutionIterator.hasNext()) {
            subtitutionKeyValue = substitutionIterator.next();

            String subtitutionStringForTag = subtitutionKeyValue.getValue();
            String injectedTag = subtitutionKeyValue.getKey();

//            pour éviter que le sommaire contienne les tags "<span> item ... </span>" à l'affichage !!!
            if (subtitutionStringForTag.equals(" infslspansup ") || subtitutionStringForTag.equals(" infspansup ") ) {
                injectedTag = " ";
            }

            if (subtitutionStringForTag.equals(quoteSubsText)) {
                // on supprime les guillemets qui ne sont pas substituable ?
                tagged_s = tagged_s.replaceAll(quoteSubsText.trim(), "");
                /*
                while (matcher.find()) {
                    String replacedQuoteRegex =   subtitutionStringForTag.stripLeading() + matcher.group(1) + subtitutionStringForTag.stripTrailing() ;
                    String newQuotedText = " \"" + matcher.group(1) + "\" ";
                    tagged_s = tagged_s.replaceAll(replacedQuoteRegex, newQuotedText);
                }
                */
            } else if (subtitutionStringForTag.equals("vars")) {
                tagged_s = substituteBracketsVarsBack(tagged_s);
            } else  {
                tagged_s = tagged_s.replaceAll(subtitutionStringForTag, injectedTag);
                tagged_s = tagged_s.replaceAll(subtitutionStringForTag.stripTrailing(), injectedTag);
                tagged_s = tagged_s.replaceAll(subtitutionStringForTag.stripLeading(), injectedTag);
            }
        }
        return tagged_s;
    }

    private void writeTranslationToScreenParam(Map<String, String> screenData, CannelleScreenParameters toTranslateParams, Map<String, String> safeLocaleKeys) {
        Iterator<String> iter = screenData.keySet().iterator();
        while (iter.hasNext()) {
            String paramSafeKey = iter.next();
            String translationValue = screenData.get(paramSafeKey);
            String LocalKey = safeLocaleKeys.get(paramSafeKey);
            toTranslateParams.getScreenParameter(LocalKey).setTranslatableValue(translationValue);
        }
        System.out.println();

    }

    private String convertParamToXML(Map<String, String> safeTranslatableParams) {

        ScreenData screenData = new ScreenData(safeTranslatableParams);
//        @JacksonXmlRootElement(localName = "toto")
//        this.safeTranslatableParams = safeTranslatableParams;
        JacksonXmlModule xmlModule = new JacksonXmlModule();
        xmlModule.setDefaultUseWrapper(true);
//        xmlModule.setXMLTextElementName("toto");
        XmlMapper mapper = new XmlMapper(xmlModule);
//        XmlMapper mapper = new XmlMapper();
        String xmlText = "";
//        mapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
        try {
//            xmlText = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(safeTranslatableParams);
//            xmlText = mapper.writeValueAsString(safeTranslatableParams);
            xmlText = mapper.writeValueAsString(screenData);
        } catch (JsonProcessingException e) {
            System.out.println("pb conversion ScreenData vers xml !!!");
        }

        return xmlText;
    }

    public void with(ITranslatorAPI translatorAPI) {
        this.translatorAPI = translatorAPI;
    }

    @JacksonXmlRootElement(localName = "screenstranslation")
        static class ScreenData {
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "screendata")
//        @JsonRawValue
        private Map<String, String> screenData;


        public ScreenData() {
        }

        public ScreenData(Map<String, String> screenData) {
            this.screenData = screenData;
        }

//        public String getScreenData(String key) {
//            return screenData.get(key);
//        }
//
//        public void setScreenData(String key, String value) {
//            screenData.put(key, value);
//        }

        public void setScreenData(Map<String, String> screenData) {
            this.screenData = screenData;
        }


        public Map<String, String> getScreenData() {
            return screenData;
        }
    }
}
