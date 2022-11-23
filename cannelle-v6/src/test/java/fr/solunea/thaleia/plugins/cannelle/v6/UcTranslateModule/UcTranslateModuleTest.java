package fr.solunea.thaleia.plugins.cannelle.v6.UcTranslateModule;

import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.plugins.cannelle.contents.IContent;
import fr.solunea.thaleia.plugins.cannelle.contents.parsing.ParsedObject;
import fr.solunea.thaleia.plugins.cannelle.parsers.xls.XlsModuleParserService;
import fr.solunea.thaleia.plugins.cannelle.utils.PackagedFiles;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.v6.CannelleV6Plugin;
import fr.solunea.thaleia.plugins.cannelle.v6.ExcelTemplate;
import fr.solunea.thaleia.plugins.cannelle.v6.helperclassesfortests.XlsModuleParserServiceForTest;
import fr.solunea.thaleia.plugins.cannelle.v6.helperclassesfortests.XlsScreenParserServiceForTest;
import fr.solunea.thaleia.plugins.cannelle.v6.uctranslatemodule.ScreensTranslator;
import fr.solunea.thaleia.utils.DetailedException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class UcTranslateModuleTest {




    @Test
    void translateModule1ScreenTitle_FR_to_EN_Blue_Car_1() throws URISyntaxException, DetailedException {
        int folderIndex= 1;
        Parameters parameters;
        ResourcesHandler handler;
        List<ParsedObject<IContent>> moduleScreenList = null;


        File tempDir;
        File xlSourceModuleFile;
        preparationDesDonneesEntree: {
//            xlSourceModuleFile = new File(getClass().getClassLoader().getResource("modele_excel_classe_virtuelle_fr.zip").toURI());
//            xlSourceModuleFile = new File(this.getClass().getClassLoader().getResource("resources/modele_excel_UcTranslateModuleTest_fr.zip").toURI());
//            xlSourceModuleFile = new File(this.getClass().getResource("resources/modele_excel_UcTranslateModuleTest_fr.zip").toURI());
//            xlSourceModuleFile = new File(this.getClass().getResource("modele_excel_UcTranslateModuleTest_fr.zip").getFile());
            xlSourceModuleFile = new File("src/test/resources/modele_excel_UcTranslateModuleTest_fr.zip");
            InputStream propertiesStream = ExcelTemplate.class.getClassLoader().getResourceAsStream("Properties/cannelle_v6_fr.properties");
            tempDir = new File("C:\\Users\\dmou\\Documents\\solunea_dev\\_thaleia_files\\newArch\\temp\\" + this.getClass().toString() + "\\tempDir");
            File resourceDir = new File("C:\\Users\\dmou\\Documents\\solunea_dev\\_thaleia_files\\newArch\\temp\\" + this.getClass().toString() + "\\resourceDir");

            // map qui permet de transmettre des properties à requeter
            // on veux tout récupéré, donc rien à transmettre !
            Map<String, Object> propertyMap = null;

            parametreDeRequete:
            {
                parameters = null;
                try {
                    parameters = new Parameters(propertiesStream, propertyMap);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            PackagedFiles packagedFiles = new PackagedFiles(xlSourceModuleFile, new File("C:\\Users\\dmou\\Documents\\solunea_dev\\_thaleia_files\\newArch\\temp\\packagedFiles"));

            // le paramètre "configuration" n'est pas très clair ....
            handler = (ResourcesHandlerForTest) new ResourcesHandlerForTest(packagedFiles, "undefined", CannelleV6Plugin.class, tempDir, resourceDir);


        }

        Map<String, String> moduleProperties;
        TRTrecuperationDesProprietesDuModule:
        {

            XlsModuleParserService parserProperties = new XlsModuleParserServiceForTest(parameters, handler);

            recuperationDesPropertiesDuModule:
            {
                moduleProperties = parserProperties.getModuleProperties();
                System.out.println(moduleProperties.toString());

// exemple de sortie :
//        {SCORMCommunication=Oui,
//         Contenu de la ressource (fichier ou URL)=Libellé de la ressource,         ---> !!! ligne de titre !!!
//         Description=Cette formation décrit les principales caractéristiques d'une solution de classe virtuelle en termes de fonctionnalités et d'usage. (Date de mise à jour : 12/09/2022),
//         Langue=Français,
//         PassageNote=80,
//         Ressources du module=,                                                    ---> !!! correspond à xls.parser.content.resources.header !!!
//         Title=Les basiques de la classe virtuelle HTML,
//         ModuleFormat=Pour site Web (HTML),
//         Identifiant=Les basiques de la classe virtuelle HTML}
            }

        }


//        XlsScreenParserServiceForTest parserScreens = new XlsScreenParserServiceForTest(parameters, handler);
        Locale locale = (Locale) new FakeLocale("fr");
        XlsScreenParserServiceForTest parserScreens = new XlsScreenParserServiceForTest(parameters, handler);
        ScreensTranslator screensTranslator = new ScreensTranslator(locale, null, parserScreens, moduleProperties.get("Identifiant"));

        TRTTraductionDuModule:
        {
            try {
//           détermination de la locale :
//                CannelleTreatment.java récupère la locale du fichier XL
//                en utilisant ExcelFileLocaleFinder.java.parseLocale()
//                                        \__> ExcelFileSpecification.java.getSheetLocale()
//                                                  sheetNameLocales.put("Module properties", "en");
//                                                  sheetNameLocales.put("Propiedades del módulo", "es");
//                                                  sheetNameLocales.put("Propriétés du module", "fr");
//                                                  sheetNameLocales.put("Eigenschappen van de module", "nl");
//                --> et la transforme en une locale "Thaleia" en recherchant cette locale dans la table Locale !



                screensTranslator.translateModule("EN");

//                System.out.println(moduleScreenList.toString());
            } catch (DetailedException e) {
                System.out.printf(e.toString());
            }
        }



        assertThat(new File(tempDir,"tempDir/screens/" + folderIndex + "/Titrage_html/index.html")).hasContent("Blue car");

    }

    static class FakeLocale extends Locale {
        String NAME;
        public FakeLocale(String NAME) {
            this.NAME = NAME;
        }

        public String getName() {
            return NAME;
        }

    }



}
