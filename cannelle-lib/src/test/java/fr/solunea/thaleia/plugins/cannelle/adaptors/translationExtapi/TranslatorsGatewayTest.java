package fr.solunea.thaleia.plugins.cannelle.adaptors.translationExtapi;


import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

//import org.assertj.core.api.Assertions;
import static org.assertj.core.api.Assertions.assertThat;

class TranslatorsGatewayTest {

    @Nested
    public class DeeplApiTest {

        @Test
        public void bonjourShouldReturnHello_FR_to_EN() throws URISyntaxException, IOException, InterruptedException {
            TranslatorsGateway translator;
            String text;
            given: {
                translator = new TranslatorsGateway();
                text = "bonjour";
            }

            String response;
            when: {
                response = translator.translate(text);
                System.out.println("response : " + response);
            }

            then: {
                assertThat(response).contains("hello");
            }
        }

        @Test
        public void voiture_bleuShouldReturnblue_car_FR_to_EN() throws URISyntaxException, IOException, InterruptedException {
            TranslatorsGateway translator;
            String text;
            given: {
                translator = new TranslatorsGateway();
                text = "voiture bleu";
            }

            String response;
            when: {
                response = translator.translate(text);
                System.out.println("response : " + response);
            }

            then: {
                assertThat(response).contains("blue car");
            }
        }

    }


}