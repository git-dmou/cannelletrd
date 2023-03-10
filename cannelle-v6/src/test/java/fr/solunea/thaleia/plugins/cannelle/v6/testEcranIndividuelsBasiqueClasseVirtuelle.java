package fr.solunea.thaleia.plugins.cannelle.v6;

import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.plugins.cannelle.contents.IContent;
import fr.solunea.thaleia.plugins.cannelle.contents.parsing.ParsedObject;
import fr.solunea.thaleia.plugins.cannelle.parsers.xls.XlsModuleParserService;
import fr.solunea.thaleia.plugins.cannelle.utils.PackagedFiles;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.v6.helperclassesfortests.ResourcesHandlerForTest;
import fr.solunea.thaleia.plugins.cannelle.v6.helperclassesfortests.XlsModuleParserServiceForTest;
import fr.solunea.thaleia.plugins.cannelle.v6.helperclassesfortests.XlsScreenParserServiceForTest;
import fr.solunea.thaleia.utils.DetailedException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class testEcranIndividuelsBasiqueClasseVirtuelle {

    @Test
    void basiquesDeLaClasseVirtuelle_moduleDeTestPartiel() throws DetailedException, URISyntaxException {
        Parameters parameters;
        ResourcesHandler handler;
        List<ParsedObject<IContent>> moduleScreenList = null;


        preparationDonneesEntree:
        {
            //        File xlSourceModuleFile = new File(getClass().getClassLoader().getResource("modele_excel_tutoriel_fr.zip").toURI());
//            File xlSourceModuleFile = new File(getClass().getClassLoader().getResource("modele_excel_classe_virtuelle_fr.zip").toURI());
            File xlSourceModuleFile = new File("src/test/resources/modele_excel_UcTranslateModuleTest_fr.zip");

//            File xlSourceModuleFile = new File(getClass().getClassLoader().getResource("modele_excel_tutoriel_fr.zip").toURI());
            InputStream propertiesStream = ExcelTemplate.class.getClassLoader().getResourceAsStream("Properties/cannelle_v6_fr.properties");
            File tempDir = new File("C:\\Users\\dmou\\Documents\\solunea_dev\\_thaleia_files\\newArch\\temp\\" + getClass().toString() + "\\tempDir");
            File resourceDir = new File("C:\\Users\\dmou\\Documents\\solunea_dev\\_thaleia_files\\newArch\\temp\\" + getClass().toString() + "\\resourceDir");

            // map qui permet de transmettre des properties ?? requeter
            // on veux tout r??cup??r??, donc rien ?? transmettre !
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

            // le param??tre "configuration" n'est pas tr??s clair ....
            handler = (ResourcesHandlerForTest) new ResourcesHandlerForTest(packagedFiles, "undefined", CannelleV6Plugin.class, tempDir, resourceDir);
        }

        Map<String, String> moduleProperties;
        TRTrecuperationDesProprietesDuModule:
        {

            XlsModuleParserService parserProperties = new XlsModuleParserServiceForTest(parameters, handler);

            recuperationDesPropertiesDuModule:
            {
                moduleProperties = parserProperties.getModuleProperties("","");
                System.out.println(moduleProperties.toString());

// exemple de sortie :
//        {SCORMCommunication=Oui,
//         Contenu de la ressource (fichier ou URL)=Libell?? de la ressource,         ---> !!! ligne de titre !!!
//         Description=Cette formation d??crit les principales caract??ristiques d'une solution de classe virtuelle en termes de fonctionnalit??s et d'usage. (Date de mise ?? jour : 12/09/2022),
//         Langue=Fran??ais,
//         PassageNote=80,
//         Ressources du module=,                                                    ---> !!! correspond ?? xls.parser.content.resources.header !!!
//         Title=Les basiques de la classe virtuelle HTML,
//         ModuleFormat=Pour site Web (HTML),
//         Identifiant=Les basiques de la classe virtuelle HTML}
            }

        }



        XlsScreenParserServiceForTest parserScreens = new XlsScreenParserServiceForTest(parameters, handler);

        TRTrecuperationDesPropertiesDesEcrans:
        {
            try {
//           d??termination de la locale :
//                CannelleTreatment.java r??cup??re la locale du fichier XL
//                en utilisant ExcelFileLocaleFinder.java.parseLocale()
//                                        \__> ExcelFileSpecification.java.getSheetLocale()
//                                                  sheetNameLocales.put("Module properties", "en");
//                                                  sheetNameLocales.put("Propiedades del m??dulo", "es");
//                                                  sheetNameLocales.put("Propri??t??s du module", "fr");
//                                                  sheetNameLocales.put("Eigenschappen van de module", "nl");
//                --> et la transforme en une locale "Thaleia" en recherchant cette locale dans la table Locale !

                Locale locale = (Locale) new ModuleScreensCreationNONREGRESSIONTest.FakeLocale("fr");

                // meta en anglais :
                moduleProperties.put("Langue","English");


                moduleScreenList = parserScreens.getScreens(locale, null, moduleProperties.get("Identifiant"));

                System.out.println(moduleScreenList.toString());
            } catch (DetailedException e) {
                System.out.printf(e.toString());
            }
        }
    }

}
