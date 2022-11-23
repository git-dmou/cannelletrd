package fr.solunea.thaleia.plugins.cannelle.v6.helperclassesfortests;

// utilsation d'une classe séparé car en java 11, il n'est pas possible de créer des inner class avec des methodes static !
// ce qui est nécessaire ici pour @overide les methodes de AbstractXlsParserService.java

import fr.solunea.thaleia.plugins.cannelle.parsers.xls.XlsModuleParserService;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;

public class XlsModuleParserServiceForTest extends XlsModuleParserService {

    public XlsModuleParserServiceForTest(Parameters parameters, ResourcesHandler resourcesHandler) {
        super(parameters, resourcesHandler);
    }


//    suppression de la dependence Mail
//    + ThaleiaApplication
//    + ThaleiaSession
    protected void sendNotificationMail() {
    }

    //    suppression de la dependance ThaleiaSession
    protected void notifiySession(String message) {
    }


}



