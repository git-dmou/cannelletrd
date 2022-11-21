package fr.solunea.thaleia.plugins.cannelle.xls.screens.functions;


import fr.solunea.thaleia.plugins.cannelle.contents.A7File;
import fr.solunea.thaleia.plugins.cannelle.messages.LocalizedMessages;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.generator.ScreenGeneratorUtils;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.AssociationURLorFileParameter;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.IScreenParameter;
import fr.solunea.thaleia.service.utils.FilesUtils;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Map;

public class ReplaceUrlOrFilePairFunction extends AbstractFunction {

    private static final Logger logger = Logger.getLogger(ReplaceUrlOrFilePairFunction.class);

    private final String pairName;
    private final String leftValueToReplace;
    private final String rightValueToReplace;

    private String leftValue = "";
    private String rightValue = "";

    /**
     * @param pairName            le nom de la paire dont il faut remplacer les membres gauche
     *                            et droite
     * @param leftValueToReplace  la valeur à remplacer dans le modèle pour le membre gauche
     * @param rightValueToReplace la valeur à remplacer dans le modèle pour le membre droit
     */
    public ReplaceUrlOrFilePairFunction(String pairName, String leftValueToReplace, String rightValueToReplace,
                                        File a7File) {
        this.pairName = pairName;
        this.leftValueToReplace = leftValueToReplace;
        this.rightValueToReplace = rightValueToReplace;
    }

    public String getLeftValue() {
        return leftValue;
    }

    public void setLeftValue(String leftValue) {
        this.leftValue = leftValue;
    }

    public String getRightValue() {
        return rightValue;
    }

    public void setRightValue(String rightValue) {
        this.rightValue = rightValue;
    }

    @Override
    public void run(Map<String, IScreenParameter> screenParametersMap, Parameters parameters, String assetsDirName,
                    ResourcesHandler resourcesHandler, A7File a7) throws DetailedException {
        // On recherche le paramètre qui contient la paire concernée
        // Cela DOIT être un AssociationParameter
        AssociationURLorFileParameter uRLorFileParameter =
                (AssociationURLorFileParameter) screenParametersMap.get(getPairName());

        if (uRLorFileParameter == null) {
            // Le message localisé correspondant
            String message = LocalizedMessages.getMessage("mandatory.parameter.not.found", getPairName());
            // On demande sa présentation
            ThaleiaSession.get().addError(message);

            throw new DetailedException("Le paramètre '" + getPairName()
                    + "' doit être remplacé (obligatoire), mais n'a pas été trouvé dans les " + "paramètres définis "
                    + "dans" + " le fichier Excel source.");
        }

        setLeftValue(uRLorFileParameter.getValue());
        setRightValue(uRLorFileParameter.getResponse());

        // Par defaut et si aucune valeur n'est précisée pour la valeur de droite ou gauche,
        // on remplace par une chaine vide
        if (leftValue == null) {
            leftValue = "";
        }
        if (rightValue == null) {
            rightValue = "";
        }
        String left = uRLorFileParameter.getValue();
        String right = uRLorFileParameter.getResponse();
        String leftIsFileName = resourcesHandler.getUploadedFiles().findStringFile(left);
        String rightIsFileName = resourcesHandler.getUploadedFiles().findStringFile(right);
        String sizeIsFileName = "";
        String ext = "";
        if (leftIsFileName != null) {
            sizeIsFileName = resourcesHandler.getUploadedFiles().getSizeFile(left) + "Ko) ";
            ext = " ( " + resourcesHandler.getUploadedFiles().getFileExtension(left) + " ";
        }


        // On procède au remplacement de la proposition (membre gauche)
        remplacement(parameters, assetsDirName, resourcesHandler, a7, leftIsFileName, leftValueToReplace, leftValue,
                "", "");

        // On procède au remplacement de la réponse (membre droit)
        remplacement(parameters, assetsDirName, resourcesHandler, a7, rightIsFileName, rightValueToReplace,
                rightValue, sizeIsFileName, ext);
    }

    public String getPairName() {
        return pairName;
    }

    private void remplacement(Parameters parameters, String assetsDirName, ResourcesHandler resourcesHandler,
                              A7File a7, String isFileName, String valueToReplace, String value, String size,
                              String ext) throws DetailedException {
        if (valueToReplace != null) {
            if (isFileName != null) {
                replace(parameters, assetsDirName, resourcesHandler, a7, valueToReplace, isFileName);
            } else {
                FilesUtils.replaceAllInFile(valueToReplace,
                        value + ext + size, a7.getFile(), A7File.getEncoding(), true);
            }
        }
    }

    private void replace(Parameters parameters, String assetsDirName, ResourcesHandler resourcesHandler, A7File a7,
                         String ValueToReplace, String Value) throws DetailedException {
        FilesUtils.replaceAllInFile(ValueToReplace, Value, a7.getFile(), A7File.getEncoding(), true);

        checkMediaFilename(Value);
        try {
            ScreenGeneratorUtils.safeAddMedia(resourcesHandler, parameters, ValueToReplace, Value, a7.getFile(),
                    A7File.getEncoding(), assetsDirName);
        } catch (Exception e) {
            // Le message localisé correspondant
            String message = LocalizedMessages.getMessage(LocalizedMessages.FILE_NOT_FOUND_ERROR, "");
            // On demande sa présentation
            ThaleiaSession.get().addError(message);
            throw new DetailedException("Impossible d'associer le fichier '" + Value + "' dans " + "l'archive : " + e);
        }
    }

}
