package fr.solunea.thaleia.plugins.cannelle.v6;

import fr.solunea.thaleia.plugins.cannelle.v6.uctranslatemodule.DeeplTranslator;
import fr.solunea.thaleia.plugins.cannelle.v6.uctranslatemodule.ITranslatorAPI;
import fr.solunea.thaleia.utils.DetailedException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

class DeepLApiXmlFileNoAssertTest {

   /* private static InnerDeepLTranslator translator;

    @BeforeAll
    static void init() {
        translator = new InnerDeepLTranslator();

    }*/



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
    public class DeeplApi_XMLFile_AutomaticOutlineDetection_FR_to_EN_Test {
//        Fichier XML "Basiques de la classe virtuelle"
//        --> video interactive partiellement traduite
//        --> Apport.Présentation.du.score.thématique non traduite
//        ==> ATTENTION aux caracteres avec codage WEB : &#xx; !!!





        @Test
        public void simpleXML_FR_EN_javaAPI() throws URISyntaxException, IOException, InterruptedException, DetailedException {
//            Attention les "balises" <true> / <false> non échappées posent des pb en xml !!!!
//            solution : utiliser des caractères d'échappement : NON !!! on ne peux pas échapper les caractères "<" et ">"
//                       ou des caractères de remplacement : i.n.f.true.s.u.p   et   i.n.f.false.s.u.p ---> exclure de la traduction ?
            String textXML;
            ITranslatorAPI translatorJavaApi;
            given: {
                translatorJavaApi = new DeeplTranslator();
                textXML = "<data>bonjour les amis</data>";
            }

//            List<String> responseJsonList;
            String textTranslation;
            when: {
           textTranslation = translatorJavaApi.from("FR").to("EN-GB").translate(textXML);
            }

            then: {
                System.out.println("traduction java api : " + textTranslation);

//                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }

        @Test
        public void simpleXML_FR_EN_HTTP() throws URISyntaxException, IOException, InterruptedException, DetailedException {
//            Attention les "balises" <true> / <false> non échappées posent des pb en xml !!!!
//            solution : utiliser des caractères d'échappement : NON !!! on ne peux pas échapper les caractères "<" et ">"
//                       ou des caractères de remplacement : i.n.f.true.s.u.p   et   i.n.f.false.s.u.p ---> exclure de la traduction ?
            String textXML;
            ITranslatorAPI translatorJavaApi;
            given: {
                translatorJavaApi = new DeeplTranslator();
                textXML = "<data>bonjour les amis</data>";
            }

//            List<String> responseJsonList;
            String textTranslation = "";
            when: {
                try {
                    textTranslation = translatorJavaApi.from("FR").to("EN-GB").translateXML(textXML);
                } catch (Exception e) {
                    System.out.println("pb appel API DeepL");
                }
            }

            then: {
                System.out.println("traduction java api : " + textTranslation);

//                assertThat(responseJsonList).containsExactlyElementsOf(expectedTextsList);
            }
        }

    }



}
