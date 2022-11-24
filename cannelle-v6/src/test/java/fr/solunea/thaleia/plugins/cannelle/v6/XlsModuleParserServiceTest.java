package fr.solunea.thaleia.plugins.cannelle.v6;

import fr.solunea.thaleia.model.Domain;
import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.model.User;
import fr.solunea.thaleia.plugins.cannelle.contents.A7Content;
import fr.solunea.thaleia.plugins.cannelle.contents.IContent;
import fr.solunea.thaleia.plugins.cannelle.contents.parsing.ParsedObject;
import fr.solunea.thaleia.plugins.cannelle.parsers.xls.XlsModuleParserService;
import fr.solunea.thaleia.plugins.cannelle.utils.PackagedFiles;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.v6.uctranslatemodule.ResourcesHandlerForTest;
import fr.solunea.thaleia.plugins.cannelle.v6.helperclassesfortests.XlsModuleParserServiceForTest;
import fr.solunea.thaleia.plugins.cannelle.v6.helperclassesfortests.XlsScreenParserServiceForTest;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.IScreenParameter;
import fr.solunea.thaleia.utils.DetailedException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Classe de test pour comprendre le fonctionnement de XlsModuleParserService
 */



public class XlsModuleParserServiceTest {


    @Test
    void parseXLFileModuleProperties() throws DetailedException, URISyntaxException {
        Parameters parameters;
        ResourcesHandler handler;

        preparationDonneesEntree:
        {
            //        File xlSourceModuleFile = new File(getClass().getClassLoader().getResource("modele_excel_tutoriel_fr.zip").toURI());
            File xlSourceModuleFile = new File(getClass().getClassLoader().getResource("modele_excel_classe_virtuelle_fr.zip").toURI());
            InputStream propertiesStream = ExcelTemplate.class.getClassLoader().getResourceAsStream("Properties/cannelle_v6_fr.properties");
            File tempDir = new File("C:\\Users\\dmou\\Documents\\solunea_dev\\_thaleia_files\\newArch\\temp\\fr.solunea.thaleia.plugins.cannelle.v6.XlsModuleParserServiceTest\\tempDir");
            File resourceDir = new File("C:\\Users\\dmou\\Documents\\solunea_dev\\_thaleia_files\\newArch\\temp\\fr.solunea.thaleia.plugins.cannelle.v6.XlsModuleParserServiceTest\\resourceDir");

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

        Map<String, String> moduleGeneralProperties;
        TRTrecuperationDesProprietesDuModule:
        {

            XlsModuleParserService parserProperties = new XlsModuleParserServiceForTest(parameters, handler);

            recuperationDesPropertiesDuModule:
            {
                moduleGeneralProperties = parserProperties.getModuleProperties();
                System.out.println(moduleGeneralProperties.toString());

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



        XlsScreenParserServiceForTest parserScreens = new XlsScreenParserServiceForTest(parameters, handler);

        TRTrecuperationDesPropertiesDesEcrans:
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

//                List<ParsedObject<IContent>> moduleScreenList = parserScreens.getScreens(locale, null, moduleProperties.get("Identifiant"));
//                List<IExcelTemplate> moduleScreenList = parserScreens.parseScreensData();
                List<List<IScreenParameter>> moduleScreensPropertiesList = parserScreens.parseScreensData();
                System.out.println(moduleScreensPropertiesList.toString());
            } catch (DetailedException e) {
                System.out.printf(e.toString());
            }
        }


    }

    @Deprecated
    @Test
    void createModuleScreensNonRegressionTestFor_basiquesDeLaClasseVirtuelle() throws DetailedException, URISyntaxException {
        Parameters parameters;
        ResourcesHandler handler;
        List<ParsedObject<IContent>> moduleScreenList = null;


        preparationDonneesEntree:
        {
            //        File xlSourceModuleFile = new File(getClass().getClassLoader().getResource("modele_excel_tutoriel_fr.zip").toURI());
            File xlSourceModuleFile = new File(getClass().getClassLoader().getResource("modele_excel_classe_virtuelle_fr.zip").toURI());
            InputStream propertiesStream = ExcelTemplate.class.getClassLoader().getResourceAsStream("Properties/cannelle_v6_fr.properties");
            File tempDir = new File("C:\\Users\\dmou\\Documents\\solunea_dev\\_thaleia_files\\newArch\\temp\\fr.solunea.thaleia.plugins.cannelle.v6.XlsModuleParserServiceTest\\tempDir");
            File resourceDir = new File("C:\\Users\\dmou\\Documents\\solunea_dev\\_thaleia_files\\newArch\\temp\\fr.solunea.thaleia.plugins.cannelle.v6.XlsModuleParserServiceTest\\resourceDir");

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



        XlsScreenParserServiceForTest parserScreens = new XlsScreenParserServiceForTest(parameters, handler);

        TRTrecuperationDesPropertiesDesEcrans:
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

                Locale locale = (Locale) new FakeLocale("fr");


                moduleScreenList = parserScreens.getScreens(locale, null, moduleProperties.get("Identifiant"));
//                List<IExcelTemplate> moduleScreenList = parserScreens.parseScreensData();
//                List<List<IScreenParameter>> moduleScreenList = parserScreens.parseScreensData();
                System.out.println(moduleScreenList.toString());
            } catch (DetailedException e) {
                System.out.printf(e.toString());
            }
        }

        // controle que chaque écran a une occurence
        assertThat(29).isEqualTo(moduleScreenList.size());

        // contrôle que le dossier de chaque écran a bien un contenu
        for (ParsedObject<IContent> screen : moduleScreenList
             ) {
            File screenDirectory = ((A7Content) screen.getObject()).getDirectory();
            assertThat(screenDirectory).isDirectoryContaining(file -> file.getName().equals("index.html"));
        }

    }

    @Deprecated
    @Test
    void testLocaleEtUser() {
        //        Locale locale = (Locale) new FakeLocale("fr");
        //        DataObject context = new
//        FakeLocale locale = new FakeLocale("FR");
        Locale locale = (Locale) new FakeLocale("FR");
        System.out.println("locale : " + locale.getName());


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


    static class FakeUser extends User {

        String login;
        String domain;

        public FakeUser(String login, String domain) {
            this.login = login;
            this.domain = domain;
        }

        public Domain getDomain() {
            return (Domain) (Object) "denis";
        }
    }
}
