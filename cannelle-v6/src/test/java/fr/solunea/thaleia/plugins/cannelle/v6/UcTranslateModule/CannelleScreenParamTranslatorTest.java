package fr.solunea.thaleia.plugins.cannelle.v6.uctranslatemodule;

import fr.solunea.thaleia.plugins.cannelle.v6.helperclassesfortests.FakeDeeplTranslator;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.*;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.screenparser.CannelleScreenParameters;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CannelleScreenParamTranslatorTest {


    @Test
    void valueOf_TextParameter_Should_NOT_BeTranslated() {

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
            assertThat(translatedParams.getScreenParameter("monParam").getValue()).isEqualTo("voiture bleue");
        }

    }

    @Test
    void valueOf_FormattedTextParameter_ShouldBeTranslated() {

        CannelleScreenParameters cannelleScreenParameters;
        CannelleScreenParamTranslator translator;
        given: {
            // *********************
            IScreenParameter paramToTranslate = new FormattedTextParameter();
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
            assertThat(translatedParams.getScreenParameter("monParam").getValue()).isEqualTo("blue car");
        }
    }

    @Test
    void valueOf_AssociationMixedParameter_ShouldBeTranslated() {

        CannelleScreenParameters cannelleScreenParameters;
        CannelleScreenParamTranslator translator;
        given: {
            // *********************
            IScreenParameter paramToTranslate = new AssociationMixedParameter();
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
            assertThat(translatedParams.getScreenParameter("monParam").getValue()).isEqualTo("blue car");
        }
    }

    @Test
    void valueOf_QruParameter_ShouldBeTranslated() {

        CannelleScreenParameters cannelleScreenParameters;
        CannelleScreenParamTranslator translator;
        given: {
            // *********************
            IScreenParameter paramToTranslate = new QruParameter();
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
            assertThat(translatedParams.getScreenParameter("monParam").getValue()).isEqualTo("blue car");
        }
    }

    @Test
    void valueOf_ClassificationParameter_ShouldBeTranslated() {

        CannelleScreenParameters cannelleScreenParameters;
        CannelleScreenParamTranslator translator;
        given: {
            // *********************
            IScreenParameter paramToTranslate = new ClassificationParameter();
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
            assertThat(translatedParams.getScreenParameter("monParam").getValue()).isEqualTo("blue car");
        }
    }

    @Test
    void valueOf_AssociationParameter_ShouldBeTranslated() {

        CannelleScreenParameters cannelleScreenParameters;
        CannelleScreenParamTranslator translator;
        given: {
            // *********************
            IScreenParameter paramToTranslate = new AssociationParameter();
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
            assertThat(translatedParams.getScreenParameter("monParam").getValue()).isEqualTo("blue car");
        }
    }

    @Test
    void valueOf_AssociationURLorFileParameter_ShouldBeTranslated() {

        CannelleScreenParameters cannelleScreenParameters;
        CannelleScreenParamTranslator translator;
        given: {
            // *********************
            IScreenParameter paramToTranslate = new AssociationURLorFileParameter();
            // *********************
            ((AssociationURLorFileParameter) paramToTranslate).setResponse("voiture bleue");

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
            assertThat(translatedParams.getScreenParameter("monParam").getValue()).isEqualTo("blue car");
        }
    }

    @Test
    void twoParam_ShouldBeTranslated() {

        CannelleScreenParameters cannelleScreenParameters;
        CannelleScreenParamTranslator translator;
        given: {
            // *********************
            IScreenParameter paramToTranslate1 = new QruParameter();
            IScreenParameter paramToTranslate2 = new QruParameter();
            // *********************
            paramToTranslate1.setValue("voiture bleue");
            paramToTranslate2.setValue("cheval vert");

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
            assertThat(translatedParams.getScreenParameter("monParam").getValue()).isEqualTo("blue car");
        }
    }







}