package fr.solunea.thaleia.plugins.cannelle.packager.act.specific;

import fr.solunea.thaleia.model.ContentProperty;
import fr.solunea.thaleia.model.ContentVersion;
import fr.solunea.thaleia.model.Domain;
import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.model.dao.ContentPropertyDao;
import fr.solunea.thaleia.model.dao.ContentPropertyValueDao;
import fr.solunea.thaleia.model.dao.ContentVersionDao;
import fr.solunea.thaleia.plugins.cannelle.contents.ExportedModule;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.service.utils.FilesUtils;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.ThaleiaApplication;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Applique la valeur d'un paramètre comme remplacement dans une chaîne de
 * caractères.
 */
public class PropertyReplacer extends AbstractSpecificTreatment {

    // Les paramètres attendus pour le traitement
    public static final String PARAM_FILENAME = "filename";
    public static final String PARAM_FILEENCONDIG = "fileencoding";
    public static final String PARAM_FIND = "find";
    public static final String PARAM_REPLACE = "replace";
    private static final Logger logger = Logger.getLogger(PropertyReplacer.class);
    private final Domain domain;

    public PropertyReplacer(ResourcesHandler resourcesHandler, ExportedModule module, Parameters parameters,
                            String specificParamsStartsWith, Locale locale, Domain domain) throws DetailedException {
        super(resourcesHandler, module, parameters, specificParamsStartsWith, locale);
        this.domain = domain;
    }

    @Override
    public void run() throws DetailedException {
        try {
            logger.debug("Mise à jour d'un fichier par la classe : '" + this.getClass().getName() + "'...");

            // Exception si paramètre manquant
            checkParams();

            // Fichier à traiter
            String filename = getSpecificValue(PARAM_FILENAME);
            File file = new File(getModule().getAct().getActFile().getParentFile() + File.separator + filename);
            // Chaîne à rechercher
            String find = getSpecificValue(PARAM_FIND);
            // Chaîne de remplacement
            String replaceBy = getSpecificValue(PARAM_REPLACE);
            // Dans la chaîne de remplacement, on interprète les valeurs à
            // écrire
            replaceBy = parseValue(replaceBy, domain);
            // L'encodage des caractères du fichier
            String encoding = getSpecificValue(PARAM_FILEENCONDIG);

            if (file.exists()) {
                logger.debug("Fichier à traiter : '" + file.getAbsolutePath() + "'");
                FilesUtils.replaceAllInFile(find, replaceBy, file, encoding);

            } else {
                throw new DetailedException(
                        "Impossible de modifier la valeur '" + find + "' par '" + replaceBy + "' dans le fichier '"
                                + file.getAbsolutePath() + "' : il n'existe pas dans le paquet exporté !");
            }
            logger.debug("Mise à jour du fichier ok !");

        } catch (DetailedException e) {
            e.addMessage("Impossible d'effectuer le traitement spécifique '" + this.getClass().getName() + "'.");
            throw e;
        }
    }

    /**
     * Vérification de la présence des paramètres nécessaires au traitement.
     */
    private void checkParams() throws DetailedException {
        List<String> requiredParams = new ArrayList<>(
                Arrays.asList(PARAM_FILEENCONDIG, PARAM_FILENAME, PARAM_FIND, PARAM_REPLACE));

        for (String param : requiredParams) {
            if (getSpecificValue(param).equals("")) {
                throw new DetailedException("Le traitement de la classe '" + this.getClass().getName()
                        + "' attend le paramètre '" + param + "' en entrée.");
            }
        }

    }

    /**
     * @return la chaîne, où toutes les occurences de @[nom d'un paramètre]@
     * sont remplacées par la valeur de ce paramètre.
     */
    private String parseValue(String replaceBy, Domain domain) {
        String result = replaceBy;

        if (replaceBy.startsWith("@") && replaceBy.endsWith("@")) {
            // On recherche tous les paramètres
            List<String> params = this.getParameters().getKeysStartingWith("");

            // Pour tous ces paramètres
            for (String param : params) {
                String escapedParam = "@" + param + "@";
                if (result.equals(escapedParam)) {
                    result = result.replaceAll(escapedParam, this.getParameters().getValue(param));
                }
            }
        }

        if (replaceBy.startsWith("@@module_property@")) {
            // On recherche la valeur de la propriété du module
            String propertyName = replaceBy.substring("@@module_property@".length());
            logger.debug("Recherche de la valeur du paramètre '" + propertyName + "'.");

            try {
                ContentPropertyValueDao contentPropertyValueDao = new ContentPropertyValueDao(ThaleiaApplication.get().getConfiguration().getLocalDataDir().getAbsolutePath(), ThaleiaApplication.get().getConfiguration().getBinaryPropertyType(), ThaleiaApplication.get().contextService.getContextSingleton());
                ContentPropertyDao contentPropertyDao = new ContentPropertyDao(ThaleiaApplication.get().contextService.getContextSingleton());

                ContentVersion version = getModuleLastVersion(getModule().getIdentifier(), domain);

                ContentProperty contentPropertyTitle = contentPropertyDao.findByName(propertyName);
                result = contentPropertyValueDao.find(version, contentPropertyTitle, getLocale()).get(0).getValue();

            } catch (Exception e) {
                logger.warn("Impossible de résoudre la propriété '" + propertyName + "' : " + e);
            }
        }

        return result;
    }

    private ContentVersion getModuleLastVersion(String identifier, Domain domain) throws DetailedException {

        ContentVersionDao contentVersionDao = new ContentVersionDao(domain.getObjectContext());
        try {
            List<ContentVersion> versions = contentVersionDao.findLastVersionByName(identifier, domain);
            if (versions.isEmpty()) {
                throw new DetailedException("Pas de module identifié '" + identifier + "'.");
            }
            return versions.get(0);

        } catch (DetailedException e) {
            e.addMessage("Impossible de retrouver le module dont l'identifiant est '" + identifier + "'.");
            throw e;
        }
    }
}
