package fr.solunea.thaleia.plugins.cannelle.xls.screens.generator;

import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.model.User;
import fr.solunea.thaleia.plugins.cannelle.contents.A7Content;
import fr.solunea.thaleia.plugins.cannelle.messages.LocalizedMessages;
import fr.solunea.thaleia.plugins.cannelle.parsers.IdentifierTranslator;
import fr.solunea.thaleia.plugins.cannelle.parsers.xls.XlsScreenModelParserService;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.functions.AbstractFunction;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.functions.FunctionRunner;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.IScreenParameter;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Prend en compte un fichier Excel pour effectuer les générations d'écran à
 * partir de modèles (prise en compte des paramètres, remplacements, etc.)
 */
public class ApportScreenGenerator extends AbstractContentGenerator implements IContentGenerator {

    private static final Logger logger = Logger.getLogger(ApportScreenGenerator.class);

    @Override
    public A7Content generateFiles(List<IScreenParameter> screenParameters, File destination, Locale locale,
                                   User user) throws DetailedException {

        // On s'assure que la destination existe.
        boolean mkdirs = destination.mkdirs();
        if (!mkdirs || !destination.exists() || !destination.isDirectory()) {
            throw new DetailedException(
                    "Une erreur a eu lieu durant la création du répertoire " + destination.getAbsolutePath());
        }

        // On copie le fichier a7 modèle et on récupère le fichier a7 copié
        // (a7modelTemp)
        logger.debug("Copie des fichiers a7 modèles vers " + destination);
        A7Content result = getA7Content(destination, locale, user);

        // On interprète les ScreenParameters, afin de créer les propriétés à
        // associer à cet écran
        Map<String, String> contentProperties = parseScreenParameters(screenParameters);

        // On fixe l'identifiant de cet écran, d'après les paramètres de
        // génération de l'écran
        String screenId = IdentifierTranslator.parseIdentifier(contentProperties, getParameters(),
                getParametersPrefix(), true);
        logger.debug("On attribue l'identifiant '" + screenId + "'");
        result.setIdentifier(screenId);

        //L'objet "contentProperties" est ajouté à l'objet "result" après que son traitement ai été effectué
        result.addProperties(contentProperties);

        //On cherche maintenant à remplacer l'identifiant de l'écran contenu dans l'objet screenParameters
        String propertyName = IdentifierTranslator.getIdentifierPropertyName(getParameters(), getParametersPrefix(),
                true);
        for (IScreenParameter parameter : screenParameters) {
            String contentPropertyName = parameter.getContentPropertyName();
            //On verifie que le parametre d'identifiant de l'écran existe
            if (contentPropertyName != null && contentPropertyName.equals(propertyName)) {
                //Si ce parametre est vide, on le remplace avec la valeur définie précédement
                if (parameter.getValue().equals("")) {
                    parameter.setValue(screenId);
                }
            }
        }

        // On récupère le fichier modèle xls
        File xlsModel = ScreenGeneratorUtils.getXlsFileModel(destination, getResourcesHandler(),
                getParametersPrefix(), getParameters(), locale, user);

        // Le parseur de fichier Xls modèle
        XlsScreenModelParserService screenModelParser = new XlsScreenModelParserService(getParameters(),
                getResourcesHandler());

        // Le fichier a7 de l'écran, qui va être modifié
        File a7modelTemp = result.getMainContent().getFile();

        // On récupère tous les remplacements à effectuer indiqués dans le
        // fichier Excel modèle
        List<AbstractFunction> functions = screenModelParser.parseFunctions(xlsModel, a7modelTemp);

        // On récupère la collection de tous les ScreenParameters avec leur
        // identifiant (Enoncé etc.)
        Map<String, IScreenParameter> screenParametersMap = getScreenParametersMap(screenParameters);

        // Le nom du répertoire des médias pour un a7.
        String assetsDirName = getParameters().getValue(
                getParametersPrefix() + ScreenGeneratorUtils.ASSETS_DIR_NAME_PARAM);
        logger.debug("Le nom du répertoire des médias pour un a7 est " + assetsDirName);

        // L'objet d'exécution des fonctions
        FunctionRunner functionRunner = new FunctionRunner(screenParametersMap, getParameters(), assetsDirName,
                getResourcesHandler(), result.getMainContent());

        try {
            // On exécute toutes les fonctions de remplacement définies
            logger.debug("Lancement des " + functions.size() + " fonctions de remplacements à traiter.");
            for (AbstractFunction function : functions) {
                functionRunner.run(function);
            }
        } catch (Exception e) {
            logger.debug(ExceptionUtils.getFullStackTrace(e));

            // Le message localisé correspondant
            String message = getMessage(screenId);

            // On demande sa présentation
            notifiySession(message);

            throw new DetailedException(e).addMessage("Impossible d'effectuer les remplacements demandés " + "dans le"
                    + " fichier Excel de définition des remplacements.");
        }

        return result;

    }

    protected String getMessage(String screenId) {
        String message = LocalizedMessages.getMessage(LocalizedMessages.REPLACE_FUNCTION_ON_SCREEN_ERROR, screenId);
        return message;
    }

    protected A7Content getA7Content(File destination, Locale locale, User user) throws DetailedException {
        A7Content result = ScreenGeneratorUtils.createA7ContentFromModel(destination, getResourcesHandler(),
                getParametersPrefix(), getParameters(), locale, user);
        return result;
    }

    protected void notifiySession(String message) {
        ThaleiaSession.get().addError(message);
    }

    /**
     * A partir d'une liste de ScreenParameter, peuple une collection contenant
     * tous les ScreenParameters et leur nom ("Enoncé", "Introduction" etc.)
     */
    private Map<String, IScreenParameter> getScreenParametersMap(List<IScreenParameter> screenParameters) {

        HashMap<String, IScreenParameter> result = new HashMap<>();
        for (IScreenParameter screenParameter : screenParameters) {
            result.put(screenParameter.getProperty("name", ""), screenParameter);
        }
        return result;
    }

}
