package fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters;

import fr.solunea.thaleia.plugins.cannelle.utils.URLParser;
import org.apache.log4j.Logger;

public class VideoParameter extends IllustrationParameter {

    @SuppressWarnings("unused")
    private final static Logger logger = Logger.getLogger(IllustrationParameter.class);

    @Override
    public boolean valueIsAFileName() {
        // Oui s'il s'agit d'un fichier
        // Non s'il s'agit d'une URL

        // logger.debug("La valeur de paramètre vidéo '" + getValue()
        // + "' est une URL ? " + URLParser.isAnUrl(getValue()));
        return !URLParser.isAnUrl(getValue());
    }

    @Override
    public String getValue() {
        // On assure qu'il n'y a pas d'espace avant ou après l'URL
        return super.getValue().trim();
    }

}
