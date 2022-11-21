package fr.solunea.thaleia.plugins.cannelle.contents;

import fr.solunea.thaleia.plugins.cannelle.messages.LocalizedMessages;
import fr.solunea.thaleia.plugins.cannelle.utils.URLParser;
import fr.solunea.thaleia.plugins.cannelle.utils.XmlUtils;
import fr.solunea.thaleia.service.utils.FilenamesUtils;
import fr.solunea.thaleia.service.utils.XpathUtils;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import org.apache.log4j.Logger;
import org.apache.wicket.util.io.IOUtils;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.input.DOMBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

/**
 * Un fichier A7, sans ses médias.
 */
public class A7File implements Serializable { // NOPMD

    private static final String ORDER = "order";

    private static final Logger logger = Logger.getLogger(A7File.class);

    /**
     * L'encodage des fichiers a7.
     */
    private static final String XML_ENCODING = "UTF-8";

    private final File a7;
    private Document document;

    public A7File(File a7) {
        this.a7 = a7;
    }

    public File getFile() {
        return this.a7;
    }

    public static String getEncoding() {
        return XML_ENCODING;
    }

    /**
     * @param filenameTag
     * @param newFilenameTag
     * @throws Exception
     */
    public void changeFilename(String filenameTag, String newFilenameTag) throws DetailedException {
        try {
            NodeList liste = getDocument().getElementsByTagName("filename");
            logger.debug("Trouvé " + liste.getLength() + " balises filename dans le A7 '" + a7.getAbsolutePath() + "'");
            int j = 0;
            while (j < liste.getLength()) {
                String assetName = liste.item(j).getChildNodes().item(0).getNodeValue();
                assetName = FilenamesUtils.getNormalizedPath(assetName);
                if (assetName.equals(filenameTag)) {
                    // C'est un filename à modifier
                    liste.item(j).getChildNodes().item(0).setNodeValue(newFilenameTag);
                }
                j++;
            }

            logger.debug("Enregistrement du A7...");
            commit();

        } catch (Exception e) {
            throw new DetailedException(e).addMessage(
                    "Impossible de modifier le filename '" + filenameTag + "' dans le fichier A7 '"
                            + a7.getAbsolutePath() + "'.");
        }
    }

    /**
     * @return le XML en mémoire, sinon le charge depuis le binaire A7.
     */
    Document getDocument() throws DetailedException {
        if (document == null) {
            InputStream is = null;
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db;
                db = dbf.newDocumentBuilder();
                is = new FileInputStream(a7);
                document = db.parse(is);
            } catch (Exception e) {
                throw new DetailedException(e).addMessage(
                        "Impossible d'analyser le fichier A7 '" + a7.getAbsolutePath() + "'.");
            } finally {
                IOUtils.closeQuietly(is);
            }
        }

        return document;
    }

    /**
     * Enregistre le XML actuellement stocké en mémoire dans le fichier binaire
     */
    private void commit() throws DetailedException {
        // On enregistre l'objet Xmlk courant dans le fichier binaire.
        commit(getJdomDocument(), getFile());

        // On remet à zéro l'objet document Xml chargé en mémoire pour s'assurer
        // qu'il sera bien rechargé depuis le binaire.
        this.document = null;
    }

    /**
     * Enregistre ce document Xml dans le binaire.
     */
    private static void commit(org.jdom2.Document document, File a7File) throws DetailedException {

        try {
            // On trie les attributs des balises "zone", car certains
            // remplacements se font ligne à ligne.
            sortZonesAttributes(document);

        } catch (DetailedException e) {
            e.addMessage("Impossible de trier les attributs des balises 'zone' dans le A7 '" + a7File.getAbsolutePath()
                    + "'.");
            throw e;
        }

        try {
            XmlUtils.writeXML(document, a7File, getEncoding());

        } catch (DetailedException e) {
            e.addMessage("Impossible d'enregistrer les modifications dans le A7 '" + a7File.getAbsolutePath() + "'.");
            throw e;
        }

    }

    /**
     * Tri les attributs des balises zones selon l'ordre 'id', 'occ' puis
     * 'active'
     */
    private static void sortZonesAttributes(org.jdom2.Document document) throws DetailedException {
        try {
            // Voici l'ordre INVERSE utilisé par le comparateur d'attributs :
            final List<String> order = new ArrayList<String>();
            order.add("active");
            order.add("occ");
            order.add("id");
            // On trie donc pour avoir : id, puis occ, et enfin active.
            Comparator<Attribute> comparator = new Comparator<Attribute>() {
                @Override
                public int compare(Attribute a1, Attribute a2) {
                    return order.indexOf(a2.getName()) - order.indexOf(a1.getName());
                }
            };

            // récupération des balises 'zones'
            List<Element> zones = XpathUtils.getElements("//zone", document);

            // Pour chacune des balises "zone"
            for (Element zone : zones) {
                zone.sortAttributes(comparator);
            }

        } catch (Exception e) {
            throw new DetailedException(e).addMessage("Impossible de trier les attributs des balises dans le A7.");
        }

    }

    /**
     * @return la liste des contenus des balises filenames de ce A7.
     */
    public List<String> getFilenames() throws DetailedException {
        List<String> result = new ArrayList<String>();

        try {
            // Récupération de la liste des Noeuds XML que le document fait
            // réference
            NodeList liste = getDocument().getElementsByTagName("filename");
            logger.debug("Trouvé " + liste.getLength() + " balises filename dans le A7 '" + a7.getAbsolutePath() + "'");
            int j = 0;
            // pour tous les fichiers referencés
            while (j < liste.getLength()) {
                String assetName = liste.item(j).getChildNodes().item(0).getNodeValue();

                // Si la ressource est une URL, alors on ne cherche pas à la
                // traiter comme un fichier.
                if (!URLParser.isAnUrl(assetName.toLowerCase())) {
                    File asset = new File(a7.getParentFile().getAbsolutePath() // NOPMD
                            + File.separator + assetName.replaceAll(" ", "\\ "));
                    if (!asset.exists()) {
                        // On a voulu décrire une URL, mais comme elle ne
                        // commence pas par http://, on ne la reconnait pas, et
                        // on la traite comme un fichier. Or, ce fichier
                        // n'existe pas.

                        // Le message localisé correspondant
                        String message = LocalizedMessages.getMessage(LocalizedMessages.URL_ERROR, new
                                Object[]{assetName});

                        // On demande sa présentation
                        ThaleiaSession.get().addError(message);

                        throw new Exception(
                                "Le fichier asset '" + asset.getAbsolutePath() + "' référencé par le fichier '"
                                        + a7.getAbsolutePath() + "' n'existe pas ! (assetName=" + assetName + ")");
                    }
                    logger.debug("Trouvé le fichier référencé : '" + asset.getAbsolutePath() + "'");
                    result.add(assetName);
                }

                j++;
            }
        } catch (Exception e) {
            throw new DetailedException(e).addMessage("Impossible d'analyser le fichier A7.");
        }

        return result;
    }

    /**
     * @return la liste des médias (fichiers réels, tels que référencés dans le
     * XML) pour ce contenu.<br/>
     * Clé = balise filename / Valeur = fichier binaire stocké dans le
     * filesystem
     */
    public Map<Node, File> getMediasNodes() throws DetailedException {
        Map<Node, File> result = new HashMap<Node, File>();
        Document document;
        //On test si le documen est un XML, si oui on le récupère et on poursuit le traitement.
        try {
            document = getDocument();
        } catch (DetailedException e) {
            logger.debug("Impossible d'interpréter " + a7.getAbsolutePath()
                    + " comme un fichier XML, on renvoie une map vide");
            return result;
        }
        // Parsing du A7
        try {
            NodeList liste = document.getElementsByTagName("filename");
            logger.debug("Trouvé " + liste.getLength() + " balises filename dans le A7 '" + a7.getAbsolutePath() + "'");
            int j = 0;
            // pour tous les fichiers referencés
            while (j < liste.getLength()) {
                Node filenameTag = liste.item(j);
                String assetName = filenameTag.getChildNodes().item(0).getNodeValue();
                // Si la ressource est une URL, alors on ne cherche pas à la
                // traiter comme un fichier.
                // logger.debug("assetName='"+assetName+"'");
                if (!URLParser.isAnUrl(assetName.toLowerCase())) {
                    String assetPath = a7.getParentFile().getAbsolutePath() + "/" + assetName;
                    logger.debug("Récupération du fichier '" + assetPath + "'.");
                    File asset = new File(assetPath); // NOPMD
                    if (!asset.exists()) {
                        throw new Exception(
                                "Le fichier asset '" + asset.getAbsolutePath() + "' référencé par le fichier '"
                                        + a7.getAbsolutePath()
                                        + "' n'existe pas ! Vérifiez la cohérence du fichier A7 '"
                                        + a7.getAbsolutePath() + "' assetName='" + assetName + "'");
                    }
                    logger.debug("Trouvé le fichier référencé : '" + asset.getAbsolutePath() + "'");
                    result.put(filenameTag, asset);
                }
                j++;
            }
        } catch (Exception e) {
            throw new DetailedException(e).addMessage(
                    "Impossible d'analyser les balises filename du A7 '" + a7.getAbsolutePath() + ".");
        }

        return result;
    }

    /**
     * Supprime l'étape demandée, et n'enregistre pas les modifications dans le
     * fichier XML.
     *
     * @param screen           la valeur de l'attribut "order" de la balise step qui sera
     *                         supprimée
     * @param firstIndexNumber = le numéro du premier élément d'une liste (par exemple : 0 ou
     *                         1)
     */
    public void deleteSteps(int page, int screen, List<Integer> stepsToDelete, // NOPMD
                            int firstIndexNumber) throws DetailedException {
        if (!getFile().getName().endsWith("html")) {
            try {

                org.jdom2.Document document = getJdomDocument();

                // On commence par ajouter un attribut "ordre" à chaque balise step,
                // décrivant le numéro d'ordre avant les traitements de suppression
                // de step. En effet, une fois qu'on a commencé les suppressions, la
                // prochaine fonction de suppression se basera sur des ordres qui ne
                // sont plus valables.
                addOrderAttribute(firstIndexNumber, document);

                // On parcourt les pages
                List<Element> stepsToDetach = new ArrayList<Element>();
                Element pagesElement = document.getRootElement().getChild("pages");
                for (Element pageElement : pagesElement.getChildren("page")) {
                    Element screensElement = pageElement.getChild("screens");
                    for (Element screenElement : screensElement.getChildren("screen")) {
                        Element synchroElement = screenElement.getChild("synchro");
                        for (Element step : synchroElement.getChildren("step")) {
                            if (step.getAttribute(ORDER) != null) {
                                int order = step.getAttribute(ORDER).getIntValue();
                                logger.debug("Analyse de l'attribut ordre : " + order);
                                for (Integer stepNumber : stepsToDelete) {
                                    if (stepNumber == order) {
                                        // Marquage pour suppression
                                        logger.debug("Suppression de l'étape.");
                                        stepsToDetach.add(step);
                                    }
                                }
                            }
                        }
                    }
                }

                for (Element step : stepsToDetach) {
                    step.detach();
                }

                if (!stepsToDetach.isEmpty()) {
                    commit(document, getFile());
                    // On remet à zéro l'objet Xl actuellement en mémoire
                    this.document = null;
                }

            } catch (DetailedException e) {
                throw e.addMessage(
                        "Une erreur a eu lieu durant la suppression de l'étape page=" + page + " écran=" + screen
                                + " étapes=" + stepsToDelete + " : " + e);

            } catch (Exception e) {
                throw new DetailedException(
                        "Une erreur a eu lieu durant la suppression de l'étape page=" + page + " écran=" + screen
                                + " étapes=" + stepsToDelete + " : " + e);
            }
        }
    }

    /**
     * Vérifie la présence des attributs 'order' aux balises 'step', débutant à
     * firstIndexNumber. Si ces attributs existent, alors ils ne sont pas
     * modifiés
     */
    private static void addOrderAttribute(int firstIndexNumber, org.jdom2.Document document) throws DetailedException {

        int order = firstIndexNumber;

        Element pagesElement = document.getRootElement().getChild("pages");
        for (Element page : pagesElement.getChildren("page")) {
            Element screensElement = page.getChild("screens");
            for (Element screen : screensElement.getChildren("screen")) {
                Element synchroElement = screen.getChild("synchro");
                for (Element step : synchroElement.getChildren("step")) {
                    if (step.getAttribute(ORDER) == null) {
                        step.setAttribute(ORDER, Integer.toString(order));
                        order++;
                    }
                }
                // On remet le numéro de step à firstIndexNumber pour les
                // prochains écrans
                order = firstIndexNumber;
            }
        }
    }

    private org.jdom2.Document getJdomDocument() throws DetailedException {
        DOMBuilder in = new DOMBuilder();
        try {
            return in.build(getDocument());

        } catch (DetailedException e) {
            throw new DetailedException(e).addMessage(
                    "Impossible de convertir le document du a7 " + a7.getAbsolutePath());
        }
    }

    /**
     * Supprime cette zone de la définition des zones, et des références dans la
     * synchronisation.
     */
    public void deleteZone(String zoneId) throws DetailedException {
        if (!getFile().getName().endsWith("html")) {
            try {
                // recherche de toutes les balises Zone portant cet id
                org.jdom2.Document document = getJdomDocument();

                List<Element> zones = XpathUtils.getElements("//zone[@id='" + zoneId + "']", document);

                for (Element zone : zones) {
                    zone.detach();
                    logger.debug("La zone " + zone.getAttributeValue("id") + " a été détachée.");
                }

                commit(document, getFile());
                // On remet à zéro l'objet Xl actuellement en mémoire
                this.document = null;

            } catch (Exception e) {
                throw new DetailedException(
                        "Une erreur a eu lieu durant la suppression de la zone " + zoneId + " : " + e);
            }

        }
    }
}
