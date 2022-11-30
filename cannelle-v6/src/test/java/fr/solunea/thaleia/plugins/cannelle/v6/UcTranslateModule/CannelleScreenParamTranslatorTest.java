package fr.solunea.thaleia.plugins.cannelle.v6.uctranslatemodule;

import fr.solunea.thaleia.plugins.cannelle.v6.helperclassesfortests.FakeDeeplTranslator;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.*;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.screenparser.CannelleScreenParameters;
import fr.solunea.thaleia.utils.DetailedException;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class CannelleScreenParamTranslatorTest {


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
            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
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
    void valueOf_FormattedTextParameter_ShouldBeTranslated() throws DetailedException {

        CannelleScreenParameters cannelleScreenParameters;
        CannelleScreenParamTranslator translator;
        given: {
            // *********************
            IScreenParameter paramToTranslate = new FormattedTextParameter();
            // *********************
            paramToTranslate.setValue("voiture bleue");
            Properties properties1 = new Properties();
            properties1.setProperty("contentproperty", "safeKeyParam1");
            paramToTranslate.setProperties(properties1);

            cannelleScreenParameters = new CannelleScreenParameters();
            cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

            translator = new CannelleScreenParamTranslator();
            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
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
            properties1.setProperty("contentproperty", "safeKeyParam1");
            paramToTranslate.setProperties(properties1);


            cannelleScreenParameters = new CannelleScreenParameters();
            cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

            translator = new CannelleScreenParamTranslator();
            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
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
            properties1.setProperty("contentproperty", "safeKeyParam1");
            paramToTranslate.setProperties(properties1);

            cannelleScreenParameters = new CannelleScreenParameters();
            cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

            translator = new CannelleScreenParamTranslator();
            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
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
            properties1.setProperty("contentproperty", "safeKeyParam1");
            paramToTranslate.setProperties(properties1);

            cannelleScreenParameters = new CannelleScreenParameters();
            cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

            translator = new CannelleScreenParamTranslator();
            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
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

            Properties properties1 = new Properties();
            properties1.setProperty("contentproperty", "safeKeyParam1");
            paramToTranslate.setProperties(properties1);

            cannelleScreenParameters = new CannelleScreenParameters();
            cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

            translator = new CannelleScreenParamTranslator();
            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
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
    void valueOf_AssociationURLorFileParameter_ShouldBeTranslated() throws DetailedException {

        CannelleScreenParameters cannelleScreenParameters;
        CannelleScreenParamTranslator translator;
        given: {
            // *********************
            IScreenParameter paramToTranslate = new AssociationURLorFileParameter();
            // *********************
            ((AssociationURLorFileParameter) paramToTranslate).setResponse("voiture bleue");

            Properties properties1 = new Properties();
            properties1.setProperty("contentproperty", "safeKeyParam1");
            paramToTranslate.setProperties(properties1);

            cannelleScreenParameters = new CannelleScreenParameters();
            cannelleScreenParameters.addScreenParameter("monParam", paramToTranslate);

            translator = new CannelleScreenParamTranslator();
            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
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
            properties1.setProperty("contentproperty", "safeKeyParam1");
            paramToTranslate1.setProperties(properties1);

            paramToTranslate2.setValue("cheval vert");
            Properties properties2 = new Properties();
            properties2.setProperty("contentproperty", "safeKeyParam2");
            paramToTranslate2.setProperties(properties2);


            cannelleScreenParameters = new CannelleScreenParameters();
            cannelleScreenParameters.addScreenParameter("monParam1", paramToTranslate1);
            cannelleScreenParameters.addScreenParameter("monParam2", paramToTranslate2);

            translator = new CannelleScreenParamTranslator();
            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
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
            properties1.setProperty("contentproperty", "safeKeyParam1");
            paramToTranslate1.setProperties(properties1);



            paramToTranslate2.setValue("cheval vert");
            Properties properties2 = new Properties();
            properties2.setProperty("contentproperty", "safeKeyParam2");
            paramToTranslate2.setProperties(properties2);


            cannelleScreenParameters = new CannelleScreenParameters();
            cannelleScreenParameters.addScreenParameter("monParam1", paramToTranslate1);
            cannelleScreenParameters.addScreenParameter("monParam2", paramToTranslate2);

            translator = new CannelleScreenParamTranslator();
            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
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
            properties1.setProperty("contentproperty", "safeKeyParam1");
            paramToTranslate1.setProperties(properties1);

            ((AssociationURLorFileParameter) paramToTranslate2).setResponse("cheval vert"); // c'est le champ "response" qui doit être traduit !
            Properties properties2 = new Properties();
            properties2.setProperty("contentproperty", "safeKeyParam2");
            paramToTranslate2.setProperties(properties2);


            cannelleScreenParameters = new CannelleScreenParameters();
            cannelleScreenParameters.addScreenParameter("monParam1", paramToTranslate1);
            cannelleScreenParameters.addScreenParameter("monParam2", paramToTranslate2);

            translator = new CannelleScreenParamTranslator();
            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
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
            properties1.setProperty("contentproperty", "safeKeyParam1");
            paramToTranslate1.setProperties(properties1);

            paramToTranslate2.setValue("cheval vert");
            Properties properties2 = new Properties();
            properties2.setProperty("contentproperty", "safeKeyParam2");
            paramToTranslate2.setProperties(properties2);


            paramToTranslate3.setValue("chien jaune");
            Properties properties3 = new Properties();
            properties3.setProperty("contentproperty", "safeKeyParam3");
            paramToTranslate3.setProperties(properties3);


            cannelleScreenParameters = new CannelleScreenParameters();
            cannelleScreenParameters.addScreenParameter("monParam1", paramToTranslate1);
            cannelleScreenParameters.addScreenParameter("monParam2", paramToTranslate2);
            cannelleScreenParameters.addScreenParameter("monParam3", paramToTranslate3);

            translator = new CannelleScreenParamTranslator();
            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();
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
            properties1.setProperty("contentproperty", "safeKeyParam1");
            paramToTranslate1.setProperties(properties1);

            paramToTranslate2.setValue("cheval vert");
            Properties properties2 = new Properties();
            properties2.setProperty("contentproperty", "safeKeyParam2");
            paramToTranslate2.setProperties(properties2);


            paramToTranslate3.setValue("chien jaune");
            Properties properties3 = new Properties();
            properties3.setProperty("contentproperty", "safeKeyParam3");
            paramToTranslate3.setProperties(properties3);


            cannelleScreenParameters = new CannelleScreenParameters();
            cannelleScreenParameters.addScreenParameter("monParam1", paramToTranslate1);
            cannelleScreenParameters.addScreenParameter("monParam2", paramToTranslate2);
            cannelleScreenParameters.addScreenParameter("monParam3", paramToTranslate3);

            translator = new CannelleScreenParamTranslator();
            ITranslatorAPI translatorAPI = new FakeDeeplTranslator();

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
