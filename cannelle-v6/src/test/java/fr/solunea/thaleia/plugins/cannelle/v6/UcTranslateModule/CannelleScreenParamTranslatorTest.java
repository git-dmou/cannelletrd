package fr.solunea.thaleia.plugins.cannelle.v6.uctranslatemodule;

import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.*;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.screenparser.CannelleScreenParameters;
import fr.solunea.thaleia.utils.DetailedException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class CannelleScreenParamTranslatorTest {


    @Nested
    class htmlTagsAndSpecialCharactersTreatment {

        @Test()
        void spanTagsShouldBeEliminated() throws DetailedException {
//            pour éviter leur affichage dans le sommaire du  module !!

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new FormattedTextParameter();
                // *********************
                paramToTranslate.setValue("<span>voiture bleue</span>");
                Properties properties1 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
                paramToTranslate.setProperties(properties1);
                paramToTranslate.setSafeKey("safeKeyParam1");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                assertThat(translatedParams.getScreenParameter("monParam").getTranslatableValue().get()).isEqualTo(" blue car ");
            }
        }


        @Test
        void b_Tags() throws DetailedException {

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new FormattedTextParameter();
                // *********************
                paramToTranslate.setValue("<b>voiture bleue");
                Properties properties1 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
                paramToTranslate.setProperties(properties1);
                paramToTranslate.setSafeKey("safeKeyParam1");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                assertThat(translatedParams.getScreenParameter("monParam").getTranslatableValue().get()).isEqualTo("<b>blue car");
            }
        }


        @Test
        void slash_b_Tags() throws DetailedException {

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new FormattedTextParameter();
                // *********************
                paramToTranslate.setValue("voiture bleue</b>");
                Properties properties1 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
                paramToTranslate.setProperties(properties1);
                paramToTranslate.setSafeKey("safeKeyParam1");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                assertThat(translatedParams.getScreenParameter("monParam").getTranslatableValue().get()).isEqualTo("blue car</b>");
            }
        }

        @Test
        void b_AndSlash_b_Tags() throws DetailedException {

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new FormattedTextParameter();
                // *********************
                paramToTranslate.setValue("<b>voiture bleue</b>");
                Properties properties1 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
                paramToTranslate.setProperties(properties1);
                paramToTranslate.setSafeKey("safeKeyParam1");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                assertThat(translatedParams.getScreenParameter("monParam").getTranslatableValue().get()).isEqualTo("<b>blue car</b>");
            }
        }

        @Test
        void b_AndSlash_b_Tags_with2ParamsToTranslate() throws DetailedException {

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate1 = new FormattedTextParameter();
                // *********************
                paramToTranslate1.setValue("<b>voiture bleue</b>");
                Properties properties1 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
                paramToTranslate1.setProperties(properties1);
                paramToTranslate1.setSafeKey("safeKeyParam1");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam1", paramToTranslate1);

                // *********************
                IScreenParameter paramToTranslate2 = new FormattedTextParameter();
                // *********************

                paramToTranslate2.setValue("<b>cheval vert</b>");
                Properties properties2 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
                paramToTranslate2.setProperties(properties2);
                paramToTranslate2.setSafeKey("safeKeyParam2");

//                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam2", paramToTranslate2);

                translator = new CannelleScreenParamTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                assertThat(translatedParams.getScreenParameter("monParam1").getTranslatableValue().get()).isEqualTo("<b>blue car</b>");
                assertThat(translatedParams.getScreenParameter("monParam2").getTranslatableValue().get()).isEqualTo("<b>green horse</b>");
            }
        }

        @Test
        void specialString_3Points() throws DetailedException {

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new FormattedTextParameter();
                // *********************
                paramToTranslate.setValue("voiture bleue ... ");
                Properties properties1 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
                paramToTranslate.setProperties(properties1);
                paramToTranslate.setSafeKey("safeKeyParam1");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                assertThat(translatedParams.getScreenParameter("monParam").getTranslatableValue().get()).containsAnyOf("blue car ... ", "blue car... ");
//                assertThat(translatedParams.getScreenParameter("monParam").getTranslatableValue().get()).isEqualTo("blue car ... ");
            }
        }

        @Test
        void specialString_3Points_2() throws DetailedException {

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new FormattedTextParameter();
                // *********************
                paramToTranslate.setValue("voiture bleue, ... cheval vert");
                Properties properties1 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
                paramToTranslate.setProperties(properties1);
                paramToTranslate.setSafeKey("safeKeyParam1");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                assertThat(translatedParams.getScreenParameter("monParam").getTranslatableValue().get()).containsAnyOf("blue car, ... green horse", "blue car, green horse...");
//                assertThat(translatedParams.getScreenParameter("monParam").getTranslatableValue().get()).isEqualTo("blue car ... ");
            }
        }

        @Test
        void specialString_3Points_3() throws DetailedException {

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new FormattedTextParameter();
                // *********************
                paramToTranslate.setValue("voiture bleue, ... Les chevaux sont vert.");
                Properties properties1 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
                paramToTranslate.setProperties(properties1);
                paramToTranslate.setSafeKey("safeKeyParam1");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                assertThat(translatedParams.getScreenParameter("monParam").getTranslatableValue().get()).containsAnyOf("blue car, ... Horses are green.", "blue car,...The horses are green.");
//                assertThat(translatedParams.getScreenParameter("monParam").getTranslatableValue().get()).isEqualTo("blue car ... ");
            }
        }

        @Test
        void specialString_quotes() throws DetailedException {

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new FormattedTextParameter();
                // *********************
                paramToTranslate.setValue("voiture \"bleue\" ou jaunes ");
                Properties properties1 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
                paramToTranslate.setProperties(properties1);
                paramToTranslate.setSafeKey("safeKeyParam1");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                assertThat(translatedParams.getScreenParameter("monParam").getTranslatableValue().get()).containsAnyOf("car  \"blue\"  or yellow ");
//                assertThat(translatedParams.getScreenParameter("monParam").getTranslatableValue().get()).isEqualTo("blue car ... ");
            }
        }

        @Test
        void specialString_quotes2() throws DetailedException {

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new FormattedTextParameter();
                // *********************
                paramToTranslate.setValue("voiture \"bleue\" ou jaunes ou \"vert\"");
                Properties properties1 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
                paramToTranslate.setProperties(properties1);
                paramToTranslate.setSafeKey("safeKeyParam1");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                assertThat(translatedParams.getScreenParameter("monParam").getTranslatableValue().get()).containsAnyOf("car  \"blue\"  or yellow or  \"green\"  ");
//                assertThat(translatedParams.getScreenParameter("monParam").getTranslatableValue().get()).isEqualTo("blue car ... ");
            }
        }

        @Test
        void specialString_quotes_with_tags() throws DetailedException {

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new FormattedTextParameter();
                // *********************
                paramToTranslate.setValue("voiture <b>\"bleue\"</b> ou jaunes ou \"vert\"");
                Properties properties1 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
                paramToTranslate.setProperties(properties1);
                paramToTranslate.setSafeKey("safeKeyParam1");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                assertThat(translatedParams.getScreenParameter("monParam").getTranslatableValue().get()).containsAnyOf("car <b>\"blue\"</b> or yellow or  \"green\"  ", "car<b> \"blue\" </b>or yellow or  \"green\"  ");
//                assertThat(translatedParams.getScreenParameter("monParam").getTranslatableValue().get()).isEqualTo("blue car ... ");
            }
        }

        @Test
        void specialString_quotes_suroundedBy_tags_someQuotesMayBeLost() throws DetailedException {

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new FormattedTextParameter();
                // *********************
                paramToTranslate.setValue("voiture \"<b>bleue</b>\"</br> ou jaunes ou \"vert\"");
                Properties properties1 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
                paramToTranslate.setProperties(properties1);
                paramToTranslate.setSafeKey("safeKeyParam1");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                assertThat(translatedParams.getScreenParameter("monParam").getTranslatableValue().get()).containsAnyOf("car  \"<b>blue</b>\" </br>or yellow or green  ");
//                assertThat(translatedParams.getScreenParameter("monParam").getTranslatableValue().get()).isEqualTo("blue car ... ");
            }
        }



        @Test
        void twoHtmlTagsSideBySide() throws DetailedException {

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new FormattedTextParameter();
                // *********************
//                paramToTranslate.setValue("voiture bleue ou <br>jaunes ou <b>vert</b></br>");
                paramToTranslate.setValue("<br>De plus, les formations dispensées dans des centres de formation exigent des <b>frais de déplacement et une organisation non négligeable. </b></br>");
                Properties properties1 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
                paramToTranslate.setProperties(properties1);
                paramToTranslate.setSafeKey("safeKeyParam1");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                assertThat(translatedParams.getScreenParameter("monParam").getTranslatableValue().get()).containsAnyOf("<br>In addition, training at training centers requires<b>significant travel and organizational costs.</b></br>");
//                assertThat(translatedParams.getScreenParameter("monParam").getTranslatableValue().get()).isEqualTo("blue car ... ");
            }
        }

        @Test
        void variablesInsideBracketsShouldNotBeTranslated() throws DetailedException {

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new FormattedTextParameter();
                // *********************
//                paramToTranslate.setValue("voiture bleue ou <br>jaunes ou <b>vert</b></br>");
                paramToTranslate.setValue("[firstname] [name] a suivi avec succès le [date] la formation e-learning");
                Properties properties1 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
                paramToTranslate.setProperties(properties1);
                paramToTranslate.setSafeKey("safeKeyParam1");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("ES").translate(cannelleScreenParameters);
            }

            then: {
                assertThat(translatedParams.getScreenParameter("monParam").getTranslatableValue().get()).containsAnyOf("[firstname] [name] ha completado con éxito el curso de aprendizaje electrónico [date]");
            }
        }









    }

    @Nested
    class paramNOTtoTranslate {

        @Test
        void valueOf_TextParameter_Should_NOT_BeTranslated() throws DetailedException {

            // revoir la condition comme quoi ce type de paramètre ne doit pas être traduit !

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new TextParameter();
                // *********************

                paramToTranslate.setValue("voiture bleue");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
//            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                // on teste .getValue() parce que getTranslatedValue() renvoie une valeur vide si le Type n'est pas traductible
                assertThat(translatedParams.getScreenParameter("monParam").getValue()).isEqualTo("voiture bleue");
            }

        }

        @Test
        void valueOf_IllustrationQruParameter_Should_NOT_BeTranslated() throws DetailedException {

            // revoir la condition comme quoi ce type de paramètre ne doit pas être traduit !

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new IllustrationQruParameter();
                // *********************

                paramToTranslate.setValue("voiture bleue");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
//            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                // on teste .getValue() parce que getTranslatedValue() renvoie une valeur vide si le Type n'est pas traductible
                assertThat(translatedParams.getScreenParameter("monParam").getValue()).isEqualTo("voiture bleue");
            }

        }

        @Test
        void valueOf_BilanParameter_Should_NOT_BeTranslated() throws DetailedException {

            // revoir la condition comme quoi ce type de paramètre ne doit pas être traduit !

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new BilanParameter();
                // *********************

                paramToTranslate.setValue("voiture bleue");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
//            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                // on teste .getValue() parce que getTranslatedValue() renvoie une valeur vide si le Type n'est pas traductible
                assertThat(translatedParams.getScreenParameter("monParam").getValue()).isEqualTo("voiture bleue");
            }

        }


        @Test
        void valueOf_CompleteParameter_Should_NOT_BeTranslated() throws DetailedException {

            // revoir la condition comme quoi ce type de paramètre ne doit pas être traduit !

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new CompleteParameter();
                // *********************

                paramToTranslate.setValue("voiture bleue");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
//            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                // on teste .getValue() parce que getTranslatedValue() renvoie une valeur vide si le Type n'est pas traductible
                assertThat(translatedParams.getScreenParameter("monParam").getValue()).isEqualTo("voiture bleue");
            }

        }

        @Test
        void valueOf_FileParameter_Should_NOT_BeTranslated() throws DetailedException {

            // revoir la condition comme quoi ce type de paramètre ne doit pas être traduit !

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new FileParameter();
                // *********************

                paramToTranslate.setValue("voiture bleue");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
//            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                // on teste .getValue() parce que getTranslatedValue() renvoie une valeur vide si le Type n'est pas traductible
                assertThat(translatedParams.getScreenParameter("monParam").getValue()).isEqualTo("voiture bleue");
            }

        }

        @Test
        void valueOf_IllustrationParameter_Should_NOT_BeTranslated() throws DetailedException {

            // revoir la condition comme quoi ce type de paramètre ne doit pas être traduit !

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new IllustrationParameter();
                // *********************

                paramToTranslate.setValue("voiture bleue");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
//            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                // on teste .getValue() parce que getTranslatedValue() renvoie une valeur vide si le Type n'est pas traductible
                assertThat(translatedParams.getScreenParameter("monParam").getValue()).isEqualTo("voiture bleue");
            }

        }

        @Test
        void valueOf_OptionalIllustrationParameter_Should_NOT_BeTranslated() throws DetailedException {

            // revoir la condition comme quoi ce type de paramètre ne doit pas être traduit !

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new OptionalIllustrationParameter();
                // *********************

                paramToTranslate.setValue("voiture bleue");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
//            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                // on teste .getValue() parce que getTranslatedValue() renvoie une valeur vide si le Type n'est pas traductible
                assertThat(translatedParams.getScreenParameter("monParam").getValue()).isEqualTo("voiture bleue");
            }

        }

        @Test
        void valueOf_TranslateValueParameter_Should_NOT_BeTranslated() throws DetailedException {

            // revoir la condition comme quoi ce type de paramètre ne doit pas être traduit !

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new TranslateValueParameter();
                // *********************

                paramToTranslate.setValue("voiture bleue");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
//            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                // on teste .getValue() parce que getTranslatedValue() renvoie une valeur vide si le Type n'est pas traductible
                assertThat(translatedParams.getScreenParameter("monParam").getValue()).isEqualTo("voiture bleue");
            }

        }

        @Test
        void valueOf_TrioMixedParameter_Should_NOT_BeTranslated() throws DetailedException {

            // revoir la condition comme quoi ce type de paramètre ne doit pas être traduit !

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new TrioMixedParameter();
                // *********************

                paramToTranslate.setValue("voiture bleue");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
//            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                // on teste .getValue() parce que getTranslatedValue() renvoie une valeur vide si le Type n'est pas traductible
                assertThat(translatedParams.getScreenParameter("monParam").getValue()).isEqualTo("voiture bleue");
            }

        }

        @Test
        void valueOf_VideoParameter_Should_NOT_BeTranslated() throws DetailedException {

            // revoir la condition comme quoi ce type de paramètre ne doit pas être traduit !

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new VideoParameter();
                // *********************

                paramToTranslate.setValue("voiture bleue");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
//            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                // on teste .getValue() parce que getTranslatedValue() renvoie une valeur vide si le Type n'est pas traductible
                assertThat(translatedParams.getScreenParameter("monParam").getValue()).isEqualTo("voiture bleue");
            }

        }

        @Test
        void valueOf_ArrangementParameter_Should_NOT_BeTranslated() throws DetailedException {

            // revoir la condition comme quoi ce type de paramètre ne doit pas être traduit !

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new ArrangementParameter();
                // *********************

                paramToTranslate.setValue("voiture bleue");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
//            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                // on teste .getValue() parce que getTranslatedValue() renvoie une valeur vide si le Type n'est pas traductible
                assertThat(translatedParams.getScreenParameter("monParam").getValue()).isEqualTo("voiture bleue");
            }

        }












    }




    @Nested
    class paramToTranslate {

        @Test
        void valueOf_FormattedTextParameter_ShouldBeTranslated() throws DetailedException {

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new FormattedTextParameter();
                // *********************
                paramToTranslate.setValue("voiture bleue");
                Properties properties1 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
                paramToTranslate.setProperties(properties1);
                paramToTranslate.setSafeKey("safeKeyParam1");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                assertThat(translatedParams.getScreenParameter("monParam").getTranslatableValue().get()).isEqualTo("blue car");
            }
        }

        @Test
        void valueOf_AssociationMixedParameter_ShouldBeTranslated() throws DetailedException {

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new AssociationMixedParameter();
                // *********************
                paramToTranslate.setValue("voiture bleue");
                Properties properties1 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
//            paramToTranslate.setProperties(properties1);
                paramToTranslate.setSafeKey("safeKeyParam1");



                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
//            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                assertThat(translatedParams.getScreenParameter("monParam").getTranslatableValue().get()).isEqualTo("blue car");
            }
        }

        @Test
        void valueOf_QruParameter_ShouldBeTranslated() throws DetailedException {

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new QruParameter();
                // *********************
                paramToTranslate.setValue("voiture bleue");

                Properties properties1 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
                paramToTranslate.setProperties(properties1);
                paramToTranslate.setSafeKey("safeKeyParam1");


                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                assertThat(translatedParams.getScreenParameter("monParam").getTranslatableValue().get()).isEqualTo("blue car");
            }
        }

        @Test
        void valueOf_ClassificationParameter_ShouldBeTranslated() throws DetailedException {

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new ClassificationParameter();
                // *********************
                paramToTranslate.setValue("voiture bleue");

                Properties properties1 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
                paramToTranslate.setProperties(properties1);
                paramToTranslate.setSafeKey("safeKeyParam1");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
//            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                assertThat(translatedParams.getScreenParameter("monParam").getTranslatableValue().get()).isEqualTo("blue car");
            }
        }

        @Test
        void valueOf_AssociationParameter_ShouldBeTranslated() throws DetailedException {

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new AssociationParameter();
                // *********************
                paramToTranslate.setValue("voiture bleue");
                ((AssociationParameter) paramToTranslate).setResponse("cheval vert");

                Properties properties1 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
                paramToTranslate.setProperties(properties1);
                paramToTranslate.setSafeKey("safeKeyParam1");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
//            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
//                la propriété AssociationParameter a 2 champs à traduire au lieu d'un !
//                - value
//                - response
                assertThat(translatedParams.getScreenParameter("monParam").getValue()).isEqualTo("blue car");
                assertThat(((AssociationParameter) (translatedParams.getScreenParameter("monParam"))).getResponse()).isEqualTo("green horse");
            }
        }

        @Test
        void valueOf_AssociationParameter_ShouldBeTranslated_evenIfNoResponse() throws DetailedException {

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new AssociationParameter();
                // *********************
                paramToTranslate.setValue("voiture bleue");
//                ((AssociationParameter) paramToTranslate).setResponse("cheval vert");

                Properties properties1 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
                paramToTranslate.setProperties(properties1);
                paramToTranslate.setSafeKey("safeKeyParam1");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
//            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
//                la propriété AssociationParameter a 2 champs à traduire au lieu d'un !
//                - value
//                - response
                assertThat(translatedParams.getScreenParameter("monParam").getValue()).isEqualTo("blue car");
                assertThat(((AssociationParameter) (translatedParams.getScreenParameter("monParam"))).getResponse()).isEqualTo("");
            }
        }


        @Test
        void valueOf_AssociationURLorFileParameter_ShouldBeTranslated() throws DetailedException {

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate = new AssociationURLorFileParameter();
                // *********************
                ((AssociationURLorFileParameter) paramToTranslate).setResponse("voiture bleue");

                Properties properties1 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
                paramToTranslate.setProperties(properties1);
                paramToTranslate.setSafeKey("safeKeyParam1");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

                translator = new CannelleScreenParamTranslator();
//            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                assertThat(translatedParams.getScreenParameter("monParam").getTranslatableValue().get()).isEqualTo("blue car");
            }
        }

    }


    @Nested
    class multiParamTranslation {

        @Test
        void twoParam_ShouldBeTranslated() throws DetailedException {

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate1 = new QruParameter();
                IScreenParameter paramToTranslate2 = new QruParameter();
                // *********************
                paramToTranslate1.setValue("voiture bleue");
                Properties properties1 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
                paramToTranslate1.setProperties(properties1);
                paramToTranslate1.setSafeKey("safeKeyParam1");

                paramToTranslate2.setValue("cheval vert");
                Properties properties2 = new Properties();
//            properties2.setProperty("contentproperty", "safeKeyParam2");
                paramToTranslate2.setProperties(properties2);
                paramToTranslate2.setSafeKey("safeKeyParam2");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam1", paramToTranslate1);
                cannelleScreenParameters.addScreenParameter("monParam2", paramToTranslate2);

                translator = new CannelleScreenParamTranslator();
//            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                try {
                    translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
                } catch (DetailedException e) {
                    throw new RuntimeException(e);
                }
            }

            then: {
                assertThat(translatedParams.getScreenParameter("monParam1").getTranslatableValue().get()).isEqualTo("blue car");
                assertThat(translatedParams.getScreenParameter("monParam2").getTranslatableValue().get()).isEqualTo("green horse");
            }
        }

        @Test
        void twoDifferentParam_ShouldBeTranslated() throws DetailedException {

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate1 = new QruParameter();
                IScreenParameter paramToTranslate2 = new FormattedTextParameter();
                // *********************
                paramToTranslate1.setValue("voiture bleue");
                Properties properties1 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
                paramToTranslate1.setProperties(properties1);
                paramToTranslate1.setSafeKey("safeKeyParam1");




                paramToTranslate2.setValue("cheval vert");
                Properties properties2 = new Properties();
                properties2.setProperty("contentproperty", "safeKeyParam2");
                paramToTranslate2.setProperties(properties2);
                paramToTranslate2.setSafeKey("safeKeyParam2");


                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam1", paramToTranslate1);
                cannelleScreenParameters.addScreenParameter("monParam2", paramToTranslate2);

                translator = new CannelleScreenParamTranslator();
//            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                assertThat(translatedParams.getScreenParameter("monParam1").getTranslatableValue().get()).isEqualTo("blue car");
                assertThat(translatedParams.getScreenParameter("monParam2").getTranslatableValue().get()).isEqualTo("green horse");
            }
        }

        @Test
        void twoDifferentParam_ShouldBeTranslated2() throws DetailedException {

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate1 = new QruParameter();
                IScreenParameter paramToTranslate2 = new AssociationURLorFileParameter();

                // *********************
                paramToTranslate1.setValue("voiture bleue");
                Properties properties1 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
                paramToTranslate1.setProperties(properties1);
                paramToTranslate1.setSafeKey("safeKeyParam1");


                ((AssociationURLorFileParameter) paramToTranslate2).setResponse("cheval vert"); // c'est le champ "response" qui doit être traduit !
                Properties properties2 = new Properties();
//            properties2.setProperty("contentproperty", "safeKeyParam2");
                paramToTranslate2.setProperties(properties2);
                paramToTranslate2.setSafeKey("safeKeyParam2");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam1", paramToTranslate1);
                cannelleScreenParameters.addScreenParameter("monParam2", paramToTranslate2);

                translator = new CannelleScreenParamTranslator();
//            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                assertThat(translatedParams.getScreenParameter("monParam1").getTranslatableValue().get()).isEqualTo("blue car");
                assertThat(translatedParams.getScreenParameter("monParam2").getTranslatableValue().get()).isEqualTo("green horse");
            }

        }

        @Test
        void twoDifferentParam_ShouldBeTranslated3() throws DetailedException {

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate1 = new QruParameter();
                IScreenParameter paramToTranslate2 = new FormattedTextParameter();
                IScreenParameter paramToTranslate3 = new FormattedTextParameter();
                // *********************

                paramToTranslate1.setValue("voiture bleue");
                Properties properties1 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
                paramToTranslate1.setProperties(properties1);
                paramToTranslate1.setSafeKey("safeKeyParam1");


                paramToTranslate2.setValue("cheval vert");
                Properties properties2 = new Properties();
//            properties2.setProperty("contentproperty", "safeKeyParam2");
                paramToTranslate2.setProperties(properties2);
                paramToTranslate2.setSafeKey("safeKeyParam2");

                paramToTranslate3.setValue("chien jaune");
                Properties properties3 = new Properties();
//            properties3.setProperty("contentproperty", "safeKeyParam3");
                paramToTranslate3.setProperties(properties3);
                paramToTranslate3.setSafeKey("safeKeyParam3");


                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam1", paramToTranslate1);
                cannelleScreenParameters.addScreenParameter("monParam2", paramToTranslate2);
                cannelleScreenParameters.addScreenParameter("monParam3", paramToTranslate3);

                translator = new CannelleScreenParamTranslator();
//            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();
                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                assertThat(translatedParams.getScreenParameter("monParam1").getTranslatableValue().get()).isEqualTo("blue car");
                assertThat(translatedParams.getScreenParameter("monParam2").getTranslatableValue().get()).isEqualTo("green horse");
            }
        }

        @Test
        void twoDifferentParam_ShouldBeTranslated3_DeeplJavaApi() throws DetailedException {

            CannelleScreenParameters cannelleScreenParameters;
            CannelleScreenParamTranslator translator;
            given: {
                // *********************
                IScreenParameter paramToTranslate1 = new QruParameter();
                IScreenParameter paramToTranslate2 = new FormattedTextParameter();
                IScreenParameter paramToTranslate3 = new FormattedTextParameter();
                // *********************

                paramToTranslate1.setValue("voiture bleue");
                Properties properties1 = new Properties();
//            properties1.setProperty("contentproperty", "safeKeyParam1");
                paramToTranslate1.setProperties(properties1);
                paramToTranslate1.setSafeKey("safeKeyParam1");

                paramToTranslate2.setValue("cheval vert");
                Properties properties2 = new Properties();
//            properties2.setProperty("contentproperty", "safeKeyParam2");
                paramToTranslate2.setProperties(properties2);
                paramToTranslate2.setSafeKey("safeKeyParam2");

                paramToTranslate3.setValue("chien jaune");
                Properties properties3 = new Properties();
//            properties3.setProperty("contentproperty", "safeKeyParam3");
                paramToTranslate3.setProperties(properties3);
                paramToTranslate3.setSafeKey("safeKeyParam3");

                cannelleScreenParameters = new CannelleScreenParameters();
                cannelleScreenParameters.addScreenParameter("monParam1", paramToTranslate1);
                cannelleScreenParameters.addScreenParameter("monParam2", paramToTranslate2);
                cannelleScreenParameters.addScreenParameter("monParam3", paramToTranslate3);

                translator = new CannelleScreenParamTranslator();
//            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
                ITranslatorAPI translatorAPI = new DeeplTranslator();

                translator.with(translatorAPI);
            }


            CannelleScreenParameters translatedParams;
            when:   {
                translatedParams = translator.from("FR").to("EN").translate(cannelleScreenParameters);
            }

            then: {
                assertThat(translatedParams.getScreenParameter("monParam1").getTranslatableValue().get()).isEqualTo("blue car");
                assertThat(translatedParams.getScreenParameter("monParam2").getTranslatableValue().get()).isEqualTo("green horse");
            }
        }

    }


}
