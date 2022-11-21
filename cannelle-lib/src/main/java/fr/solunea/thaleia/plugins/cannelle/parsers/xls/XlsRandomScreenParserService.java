package fr.solunea.thaleia.plugins.cannelle.parsers.xls;

import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.model.User;
import fr.solunea.thaleia.plugins.cannelle.contents.IContent;
import fr.solunea.thaleia.plugins.cannelle.contents.parsing.ParsedObject;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.utils.DetailedException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muse on 27/04/2017.
 */
public class XlsRandomScreenParserService extends XlsScreenParserService {

    private static final Logger logger = Logger.getLogger(XlsRandomScreenParserService.class);

    /**
     * Constructeur par défaut
     *
     * @param parameters
     * @param resourcesHandler
     */
    public XlsRandomScreenParserService(Parameters parameters,
                                        ResourcesHandler resourcesHandler) {
        super(parameters, resourcesHandler);
    }


    /**
     * @return le nom de la feuille du fichier qui doit être parsée pour les écrans alatoire.
     * @throws DetailedException
     */
    @Override
    protected String getParsedSheetName(Parameters parameters) throws DetailedException {
        return parameters.getValue(Parameters.PARSED_SHEET_NAME_RANDOM_SELECTION);
    }

    @Override
    public List<ParsedObject<IContent>> getScreens(Locale locale, User user, String moduleID) throws DetailedException {
        List<ParsedObject<IContent>> screenDefinitions = new ArrayList<ParsedObject<IContent>>();
        if (super.openSheetToParse() != null) {
            screenDefinitions = super.getScreens(locale, user, moduleID);
            // On ajoute comme propriété de chaque écran l'attribut randomPool
            for (ParsedObject<IContent> screenParsedObject : screenDefinitions) {
                IContent screen = screenParsedObject.getObject();
                screen.addProperty(Parameters.RANDOM_POOL, "1");
            }
        } else {
            logger.debug("Absence du feuillet : Écrans de sélection aléatoire");
        }
        return screenDefinitions;
    }
}
