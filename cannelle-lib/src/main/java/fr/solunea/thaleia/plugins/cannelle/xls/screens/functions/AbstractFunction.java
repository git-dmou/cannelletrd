package fr.solunea.thaleia.plugins.cannelle.xls.screens.functions;

import fr.solunea.thaleia.plugins.cannelle.contents.A7File;
import fr.solunea.thaleia.plugins.cannelle.messages.LocalizedMessages;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.IScreenParameter;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.Map;

public abstract class AbstractFunction {

    private static final Logger logger = Logger.getLogger(AbstractFunction.class);

    public abstract void run(Map<String, IScreenParameter> screenParametersMap, Parameters parameters, String
            assetsDirName, ResourcesHandler resourcesHandler, A7File a7) throws DetailedException;

    /**
     * Vérifie si ce nom de fichier contient des accents, et dans ce cas, déclenche un message d'avertissement.
     */
    public static void checkMediaFilename(String filename) {
        if (!StringUtils.isAsciiPrintable(filename)) {
            // Le message localisé correspondant
            String message = LocalizedMessages.getMessage(LocalizedMessages.FILENAME_ERROR, new Object[]{filename});

            // On demande sa présentation
            ThaleiaSession.get().warn(message);
            logger.debug("Le nom du fichier média '" + filename + "' comporte des caractères non " + "valides.");
        }
    }

}
