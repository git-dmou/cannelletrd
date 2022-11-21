package fr.solunea.thaleia.plugins.cannelle.utils;

import fr.solunea.thaleia.service.utils.FilesUtils;
import fr.solunea.thaleia.service.utils.IPreviewHelper;
import fr.solunea.thaleia.utils.DetailedException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class CannellePreviewHelper implements IPreviewHelper {

    protected static final Logger logger = Logger.getLogger(CannellePreviewHelper.class);

    @Override
    public void adapt(File directory) throws DetailedException {

        // Dans ce répertoire décompressé, on supprime une éventuelle
        // communication Scorm
        suppressScormInitialisation(directory);

        // On ajoute un fichier imsmanifest pipeau, sinon l'aperçu plante sous
        // Chrome si la communication n'est pas activée (et que donc il n'y a
        // pas de imsmanifest.xml).
        checkManifest(directory);

    }

    /**
     * Le grand marabout D'Ficell va maintenant sous vos yeux ébahis deviner
     * dans ce répertoire si une métastructure est configurée pour activer la
     * communication Scorm, et auquel cas la désactiver. Et aussi, ta femme va
     * revenir comme un petit toutou.
     *
     * @param directory le répertoire dans lequel sera modifié le fichier index.html
     * @throws DetailedException
     */
    private void suppressScormInitialisation(File directory) throws DetailedException {

        // On recherche un fichier index.html
        File index = FilesUtils.firstFound("index.html", directory, false);

        if (index != null) {
            logger.debug("Désactivation de communication scorm dans le fichier '" + index.getAbsolutePath() + "'.");
            try {
                FilesUtils.replaceAllInFile("scorm: true,",
                        "scorm: false,", index, "UTF-8");
            } catch (DetailedException e) {
                throw new DetailedException(e)
                        .addMessage("Impossible de désactiver la communication SCORM dans le fichier '"
                                + index.getAbsolutePath() + "'.");
            }
        } else {
            logger.debug("Pas de fichier html trouvé : pas de désactivation de communication scorm.");
        }
    }

    private void checkManifest(File directory) throws DetailedException {

        // On recherche un fichier imsmanifest.xml
        File manifest = FilesUtils.firstFound("imsmanifest.xml", directory, false);

        if (manifest == null) {
            // Création du fichier
            manifest = new File(directory.getAbsolutePath() + File.separator + "imsmanifest.xml");
            try {
                FileUtils.touch(manifest);
                logger.debug("Le manifest '" + manifest.getAbsolutePath() + "' a été créé !");

            } catch (IOException e) {
                throw new DetailedException(e)
                        .addMessage("Impossible de créer le fichier manifest '" + manifest.getAbsolutePath() + "'.");
            }

            // On remplit avec des valeurs de base
            String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><manifest xmlns=\"http://www.imsproject.org/xsd/imscp_rootv1p1p2\" xmlns:adlcp=\"http://www.adlnet.org/xsd/adlcp_rootv1p2\" xmlns:imsmd=\"http://www.imsproject.org/xsd/imsmd_rootv1p2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" identifier=\"Manifest\" version=\"1.1\" xsi:schemaLocation=\"http://www.imsproject.org/xsd/imscp_rootv1p1p2 imscp_rootv1p1p2.xsd          http://www.imsglobal.org/xsd/imsmd_rootv1p2p1 imsmd_rootv1p2p1.xsd          http://www.adlnet.org/xsd/adlcp_rootv1p2 adlcp_rootv1p2.xsd\"><metadata><schema>ADL SCORM</schema><schemaversion>1.2</schemaversion><adlcp:location>imsmanifest.xml.xml</adlcp:location></metadata><organizations default=\"B0\"><organization identifier=\"B0\" structure=\"hierarchical\"><title>Contenu de test</title><item identifier=\"A0\" identifierref=\"R_A0\"><title>Contenu de test</title><metadata><schema>ADL SCORM</schema><schemaversion>1.2</schemaversion><imsmd:lom><imsmd:general><imsmd:description><imsmd:langstring xml:lang=\"x-none\">Item description</imsmd:langstring></imsmd:description></imsmd:general><imsmd:annotation><imsmd:description><imsmd:langstring>Optional</imsmd:langstring></imsmd:description></imsmd:annotation></imsmd:lom></metadata><adlcp:maxtimeallowed>0000:00:00.00</adlcp:maxtimeallowed><adlcp:timelimitaction>exit,message</adlcp:timelimitaction><adlcp:masteryscore>80</adlcp:masteryscore></item></organization></organizations><resources><resource identifier=\"R_A0\" type=\"webcontent\" adlcp:scormtype=\"sco\" href=\"index.html\"><file href=\"index.html\" /><dependency identifierref=\"R_D2\" /><dependency identifierref=\"R_D3\" /><dependency identifierref=\"R_D4\" /><dependency identifierref=\"R_D5\" /><dependency identifierref=\"R_D6\" /><dependency identifierref=\"R_D7\" /><dependency identifierref=\"R_D8\" /><dependency identifierref=\"R_D9\" /><dependency identifierref=\"R_D10\" /><dependency identifierref=\"R_D11\" /><dependency identifierref=\"R_D12\" /><dependency identifierref=\"R_D13\" /><dependency identifierref=\"R_D14\" /><dependency identifierref=\"R_D15\" /><dependency identifierref=\"R_D16\" /><dependency identifierref=\"R_D17\" /><dependency identifierref=\"R_D18\" /><dependency identifierref=\"R_D19\" /><dependency identifierref=\"R_D20\" /><dependency identifierref=\"R_D21\" /><dependency identifierref=\"R_D22\" /><dependency identifierref=\"R_D23\" /></resource><resource type=\"webcontent\" identifier=\"R_D2\" adlcp:scormtype=\"asset\" href=\"background.swf\" /><resource type=\"webcontent\" identifier=\"R_D3\" adlcp:scormtype=\"asset\" href=\"config.xml\" /><resource type=\"webcontent\" identifier=\"R_D4\" adlcp:scormtype=\"asset\" href=\"index.html\" /><resource type=\"webcontent\" identifier=\"R_D5\" adlcp:scormtype=\"asset\" href=\"index.xml\" /><resource type=\"webcontent\" identifier=\"R_D6\" adlcp:scormtype=\"asset\" href=\"interface.swf\" /><resource type=\"webcontent\" identifier=\"R_D7\" adlcp:scormtype=\"asset\" href=\"Metastructure.swf\" /><resource type=\"webcontent\" identifier=\"R_D8\" adlcp:scormtype=\"asset\" href=\"Metastructure.swf-revision.txt\" /><resource type=\"webcontent\" identifier=\"R_D9\" adlcp:scormtype=\"asset\" href=\"player.swf\" /><resource type=\"webcontent\" identifier=\"R_D10\" adlcp:scormtype=\"asset\" href=\"player_version.txt\" /><resource type=\"webcontent\" identifier=\"R_D11\" adlcp:scormtype=\"asset\" href=\"sharedlib/libFont.swf\" /><resource type=\"webcontent\" identifier=\"R_D12\" adlcp:scormtype=\"asset\" href=\"sharedlib/libUI.swf\" /><resource type=\"webcontent\" identifier=\"R_D13\" adlcp:scormtype=\"asset\" href=\"js/API_SCORM.js\" /><resource type=\"webcontent\" identifier=\"R_D14\" adlcp:scormtype=\"asset\" href=\"js/API_SCORM_FINDER.js\" /><resource type=\"webcontent\" identifier=\"R_D15\" adlcp:scormtype=\"asset\" href=\"js/logger.js\" /><resource type=\"webcontent\" identifier=\"R_D16\" adlcp:scormtype=\"asset\" href=\"js/prototype.js\" /><resource type=\"webcontent\" identifier=\"R_D17\" adlcp:scormtype=\"asset\" href=\"js/ResManager.js\" /><resource type=\"webcontent\" identifier=\"R_D18\" adlcp:scormtype=\"asset\" href=\"js/Resource.js\" /><resource type=\"webcontent\" identifier=\"R_D19\" adlcp:scormtype=\"asset\" href=\"js/soluneatools.js\" /><resource type=\"webcontent\" identifier=\"R_D20\" adlcp:scormtype=\"asset\" href=\"js/swfobject.js\" /><resource type=\"webcontent\" identifier=\"R_D21\" adlcp:scormtype=\"asset\" href=\"css/global.css\" /><resource type=\"webcontent\" identifier=\"R_D22\" adlcp:scormtype=\"asset\" href=\"css/Metastructure.css\" /><resource type=\"webcontent\" identifier=\"R_D23\" adlcp:scormtype=\"asset\" href=\"css/Resource.css\" /></resources></manifest>";
            try {
                FileUtils.writeStringToFile(manifest, content, "UTF-8", false);
            } catch (IOException e) {
                throw new DetailedException(e).addMessage(
                        "Impossible de modifier le contenu du fichier manifest '" + manifest.getAbsolutePath() + "'.");
            }
        } else {
            logger.debug("Le manifest '" + manifest.getAbsolutePath() + "' existe déjà !");
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.solunea.thaleia.service.utils.IPreviewHelper#guessMainFile(java.io
     * .File)
     */
    public String guessMainFile(File tempDir) {

        if (tempDir == null) {
            logger.debug("Le répertoire est nul !");
            return "";
        }
        if (!tempDir.isDirectory()) {
            logger.debug("Le répertoire '" + tempDir.getAbsolutePath() + "' est un fichier !");
            return "";

        }
        if (!tempDir.exists()) {
            logger.debug("Le répertoire '" + tempDir.getAbsolutePath() + "' n'existe pas !");
            return "";
        }

        File result;

        // On recherche un fichier html ou htm à la racine
        String[] htmlExtensions = {"html", "htm"};
        result = FilesUtils.firstFound(htmlExtensions, tempDir, false);

        // Si pas trouvé, on recherche un SWF à la racine
        if (result == null) {
            String[] flasHextensions = {"swf"};
            result = FilesUtils.firstFound(flasHextensions, tempDir, false);
        }

        // Si pas trouvé, on recherche un EXE à la racine
        if (result == null) {
            String[] exeExtensions = {"exe"};
            result = FilesUtils.firstFound(exeExtensions, tempDir, false);
        }

        // Si pas trouvé, on renvoie ""
        if (result == null) {
            return "";
        } else {
            // Trouvé : on renvoie le chemin relatif de ce fichier dans le
            // répertoire
            // +1 pour ne pas avoir le premier /
            return result.getAbsolutePath().substring(tempDir.getAbsolutePath().length() + 1,
                    result.getAbsolutePath().length());
        }
    }

}
