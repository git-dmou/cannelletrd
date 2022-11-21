package fr.solunea.thaleia.plugins.cannelle.xls.screens.functions;

import fr.solunea.thaleia.plugins.cannelle.contents.A7File;
import fr.solunea.thaleia.plugins.cannelle.messages.LocalizedMessages;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.generator.ScreenGeneratorUtils;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.IScreenParameter;
import fr.solunea.thaleia.service.utils.FilesUtils;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import org.apache.log4j.Logger;

import java.util.Map;

public class ReplaceFunction extends AbstractFunction {

    private final static Logger logger = Logger.getLogger(ReplaceFunction.class);

    private final String toReplace;
    private String replaceValue;
    private final boolean runIfReplaceValueIsEmpty;

    public ReplaceFunction(String toReplace, String replaceValue, boolean runIfReplaceValueIsEmpty) {
        this.toReplace = toReplace;
        this.replaceValue = replaceValue;
        this.runIfReplaceValueIsEmpty = runIfReplaceValueIsEmpty;
    }

    @Override
    public void run(Map<String, IScreenParameter> screenParametersMap, Parameters parameters, String assetsDirName, ResourcesHandler resourcesHandler, A7File a7) throws DetailedException {

        // Le paramètre d'écran que l'on veut traiter.
        IScreenParameter screenParameter = getScreenParameter(screenParametersMap, getReplaceValue());

        // On récupère la valeur de ce ScreenParameters pour remplacer
        // celle de la fonction
        logger.debug("Valeur du paramètre '" + getReplaceValue() + "' = " + screenParameter.getValue());
        setReplaceValue(screenParameter.getValue());

        // // Si la valeur du paramètre est du contenu Html, alors on échappe
        // les
        // // balises > et <.
        // if (screenParameter.isValueAHtmlText()) {
        // setReplaceValue(Escaper.htmlEscape(screenParameter.getValue()));
        // }

        logger.debug("On appelle la méthode replace avec les arguments suivants : toReplace : '" + getToReplace() +
				"', replaceValue : '" + replaceValue + "'");

        if (getReplaceValue() != null && getToReplace() != null) {
            if (!isRunIfReplaceValueIsEmpty() && getReplaceValue().length() == 0) {
                // On n'effectue pas le traitement
                return;
            }

            if (!screenParameter.valueIsAFileName()) {
                FilesUtils.replaceAllInFile(getToReplace(), getReplaceValue(), a7.getFile(), A7File.getEncoding(),
						true);
            }
        }

        // Si le paramètre correspond à un fichier, on copie le fichier
        // en question pour l'ajouter au répertoire de résultat.
        if (screenParameter.valueIsAFileName() && !"".equals(getReplaceValue())) {

            // Erreur si le nom de fichier contient des accents : on ne pourra pas le récupérer correctement dans le
			// zip qui a été envoyé
            checkMediaFilename(getReplaceValue());

            try {
                ScreenGeneratorUtils.safeAddMedia(resourcesHandler, parameters, getToReplace(), getReplaceValue(),
						a7.getFile(), A7File.getEncoding(), assetsDirName);

            } catch (Exception e) {
                // Le message localisé correspondant
                String message = LocalizedMessages.getMessage(LocalizedMessages.FILE_NOT_FOUND_ERROR, new
						Object[]{getReplaceValue()});

                // On demande sa présentation
                ThaleiaSession.get().addError(message);

                throw new DetailedException("Impossible d'associer le fichier '" + getReplaceValue() + "' dans "
						+ "l'archive : " + e);
            }
        }
    }

    /**
     * @return le ScreenParameter correspondant à cet identifiant
     */
    private IScreenParameter getScreenParameter(Map<String, IScreenParameter> screenParametersMap, String
			parameterId) throws DetailedException {
        logger.debug("Recherche du paramètre '" + parameterId + "'");
        IScreenParameter screenParameter = screenParametersMap.get(parameterId);
        if (screenParameter == null) {
            // Le message localisé correspondant
            String message = LocalizedMessages.getMessage(LocalizedMessages.PARAMETER_NOT_FOUND_ERROR, new
					Object[]{parameterId});

            // On demande sa présentation
            ThaleiaSession.get().addError(message);

            throw new DetailedException("Impossible de retrouver la valeur de remplacement '" + parameterId + "' pour"
					+ " cette fonction de remplacement ! Vérifier le fichier .properties" + " pour s'assurer que cette"
					+ " propriété est bien recherchée dans le Xls.");
        }
        return screenParameter;
    }

    public String getToReplace() {
        return toReplace;
    }

    public String getReplaceValue() {
        return replaceValue;
    }

    public boolean isRunIfReplaceValueIsEmpty() {
        return runIfReplaceValueIsEmpty;
    }

    public void setReplaceValue(String replaceValue) {
        this.replaceValue = replaceValue;
    }

}
