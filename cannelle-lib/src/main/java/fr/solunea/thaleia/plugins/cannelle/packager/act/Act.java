package fr.solunea.thaleia.plugins.cannelle.packager.act;

import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.plugins.cannelle.contents.A7Content;
import fr.solunea.thaleia.plugins.cannelle.contents.A7Screen;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.service.utils.CopyUtils;
import fr.solunea.thaleia.service.utils.FilenamesUtils;
import fr.solunea.thaleia.service.utils.FilesUtils;
import fr.solunea.thaleia.service.utils.XmlUtils;
import fr.solunea.thaleia.utils.DetailedException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.jdom2.Attribute;
import org.jdom2.DefaultJDOMFactory;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.w3c.dom.Node;

import java.io.File;
import java.util.*;

public class Act {

    private static final String FILENAME = "filename";
    private static final String SUBTITLE = "subtitle";
    private static final String TITLE = "title";
    private static final String DURATION = "duration";
    private static final String MENU_ID = "menuId";
    private static final String RLC = "rlc";
    private static final String PROGRESS_ROLLUP = "progressRollup";
    private static final String SCORE_ROLLUP = "scoreRollup";
    private static final String CONTENT_ID = "contentid";
    private static final String VALUE = "value";
    private static final String ID = "id";
    private static final String CONTENTS = "contents";
    private static final String CONTENTS_TREE = "contentsTree";
    private static final String NAVIGATION = "navigation";
    /**
     * Le nom du répertoire (dans le même répertoire que le ACT) dans lequel les
     * fichiers du module sont à placer pour les ajouts de A7
     */
    private static final String CONTENT_DIR = "content";
    private static final Logger logger = Logger.getLogger(Act.class);
    final private File actFile;
    private final Document doc;
    /**
     * Analysé et mis à jour à la demande par getFreeContentId()
     */
    private int freeContentId = 0;

    /**
     * @param actFile le XML du fichier ACT
     */
    public Act(File actFile) throws DetailedException {
        this.actFile = actFile;

        if (this.actFile == null) {
            throw new DetailedException("Le fichier ACT est nul !");
        }
        if (!this.actFile.exists()) {
            throw new DetailedException("Le fichier ACT '" + this.actFile.getAbsolutePath() + "' n'existe pas !");
        }

        logger.debug("Ouverture du XML...");
        try {
            SAXBuilder sb = new SAXBuilder();
            doc = sb.build(actFile);
        } catch (Exception e) {
            throw new DetailedException(e).addMessage(
                    "Impossible d'interpréter le XML de '" + this.actFile.getAbsolutePath() + "'.");
        }

        Element propertiesElement = doc.getRootElement().getChild("properties");
        if (propertiesElement == null) {
            throw new DetailedException("Pas de balise 'properties' dans le modèle de ACT !");
        }

        Element navigationElement = doc.getRootElement().getChild(NAVIGATION);
        if (navigationElement == null) {
            throw new DetailedException("Pas de balise 'navigation' dans le modèle de ACT !");
        }

        Element contentsTreeElement = doc.getRootElement().getChild(CONTENTS_TREE);
        if (contentsTreeElement == null) {
            throw new DetailedException("Pas de balise 'contentsTree' dans le modèle de ACT !");
        }

        Element contentsElement = doc.getRootElement().getChild(CONTENTS);
        if (contentsElement == null) {
            throw new DetailedException("Pas de balise 'contents' dans le modèle de ACT !");
        }

        Element menusElement = doc.getRootElement().getChild("menus");
        if (menusElement == null) {
            throw new DetailedException("Pas de balise 'menus' dans le modèle de ACT !");
        }

        Element menuElement = menusElement.getChild("menu");
        if (menuElement == null) {
            throw new DetailedException("Pas de balise 'menu' dans les menus du modèle de ACT !");
        }

    }

    public File getActFile() {
        return this.actFile;
    }

    public Document getDocument() {
        return doc;
    }

    /**
     * @return le répertoire où placer les contenus à ajouter à ce ACT.
     */
    public File getContentDir() throws DetailedException {
        File result = new File(this.actFile.getParentFile().getAbsolutePath() + "/" + CONTENT_DIR);
        try {
            if (!result.exists()) {
                FileUtils.forceMkdir(result);
            }
        } catch (Exception e) {
            throw new DetailedException(e).addMessage(
                    "Impossible de fabriquer le répertoire des contenus du ACT '" + this.actFile.getAbsolutePath()
                            + "'.");
        }
        return result;
    }

    private int getFreeContentId() throws DetailedException {
        // si pas encore fait, on recherche le premier contentId libre = le plus
        // grand existant + 1
        if (freeContentId == 0) {
            freeContentId = getHighestContentId() + 1;
        }

        // On renvoie le contentId libre
        int result = freeContentId;
        // On met à jour le contentId libre pour le prochain appel
        freeContentId++;
        return result;
    }

    private int getHighestContentId() throws DetailedException {
        int result = 0;
        try {
            Element contents = doc.getRootElement().getChild(CONTENTS);
            List<Element> contentList = contents.getChildren();
            for (Element content : contentList) {
                int contentId = new Integer(content.getAttributeValue(ID));
                if (contentId > result) {
                    result = contentId;
                }
            }
        } catch (Exception e) {
            throw new DetailedException(e).addMessage("Impossible de retrouver le plus haut contentId défini.");
        }
        return result;
    }

    /**
     * Enregistre les modifications dans le fichier
     */
    public void commit() throws DetailedException {
        logger.debug("Enregistrement du fichier ACT dans '" + actFile.getAbsolutePath() + "'...");
        XmlUtils.writeXML(doc, actFile);
    }

    /**
     * Fixe toutes ces propriétés dans les properties du ACT. Si la propriété
     * existe, alors est remplacée. Si elle n'existe pas, alors elle est créée.
     */
    public void setProperties(Properties properties) throws DetailedException {
        for (Object o : properties.keySet()) {
            String key = (String) o;
            String value = properties.getProperty(key);
            logger.debug("ACT : on fixe la propriété '" + key + "' à '" + value + "'");

            // La valeur de la propriété doit-elle écrite en CDATA ?
            boolean inCData = false;
            // Oui si le paramètre format.act.update.properties.X.cdata = true
            // On fabrique cette clé format.act.update.properties.X.cdata
            // en remplaçant la dernière occurence de .key par .cdata
            String cdataParam;
            if (!key.contains(".key")) {
                cdataParam = key;
            } else {
                cdataParam = key.substring(0, key.length() - ".key".length());
            }
            cdataParam = cdataParam + ".cdata";
            // On cherche la valeur de la clé
            // format.act.update.properties.X.cdata
            String cdataValue = properties.getProperty(cdataParam);
            if (cdataValue == null) {
                cdataValue = "";
            }
            if ("true".equalsIgnoreCase(cdataValue)) {
                inCData = true;
            }

            setProperty(key, value, inCData);
        }
    }

    /**
     * Fixe cette propriété dans les properties du ACT. Si la propriété existe,
     * alors est remplacée. Si elle n'existe pas, alors elle est créée.
     *
     * @param cdata si oui, alors la valeur sera placée dans du CDATA
     */
    public void setProperty(String key, String value, boolean cdata) throws DetailedException {
        logger.debug("On fixe la propriété '" + key + "' à '" + value + "'...");
        if (getProperty(key).equals("")) {
            addProperty(key, value, cdata);
        } else {
            updateProperty(key, value, cdata);
        }
    }

    private void updateProperty(String key, String value, boolean cdata) throws DetailedException {
        try {
            if (key == null) {
                throw new DetailedException("La clé ne doit pas être nulle !");
            }
            if (value == null) {
                throw new DetailedException("La valeur ne doit pas être nulle !");
            }

            // Récupération des balises properties
            List<Element> properties = getPropertiesElements();

            // Recherche de la clé
            Iterator<Element> iter = properties.iterator();
            while (iter.hasNext()) {
                try {
                    Element data = iter.next();
                    Element property = data.getChild("property");
                    if (property.getText().equals(key)) {
                        // On a trouvé la balise data pour la clé demandée

                        if (cdata) {
                            data.getChild(VALUE).setContent(new DefaultJDOMFactory().cdata(value));
                        } else {
                            data.getChild(VALUE).setText(value);
                        }
                    }

                } catch (Exception e) {
                    logger.info("Erreur d'analyse du ACT '" + this.actFile.getAbsolutePath() + "' lors de la mise à "
                            + "jour de la propriété '" + key + "' : " + e.toString());
                    // On n'arrête pas la recherche suite à cette erreur
                    // En effet, une balise invalide ne doit pas empêcher la
                    // recherche sur les autres balises.
                }
            }
        } catch (DetailedException e) {
            e.addMessage("Impossible de fixer la propriété '" + key + "' à '" + value + "'.");
            throw e;
        }
    }

    private void addProperty(String key, String value, boolean cdata) throws DetailedException {
        try {
            Element propertiesElement = doc.getRootElement().getChild("properties");
            Element data = new Element("data");
            Element keyElement = new Element("property");
            Element valueElement = new Element(VALUE);
            if (cdata) {
                valueElement.setContent(new DefaultJDOMFactory().cdata(value));
                keyElement.setContent(new DefaultJDOMFactory().cdata(key));
            } else {
                valueElement.setText(value);
                keyElement.setText(key);
            }
            data.addContent(keyElement);
            data.addContent(valueElement);
            propertiesElement.addContent(data);
        } catch (Exception e) {
            throw new DetailedException(e).addMessage(
                    "Impossible d'ajouter la propriété key='" + key + "' value='" + value + "' properties du ACT '"
                            + this.actFile.getAbsolutePath() + "'.");
        }
    }

    /**
     * @return la valeur pour cette propriété dans le ACT. "" si la valeur n'est pas définie dans le ACT pour cette clé.
     * @throws DetailedException si les propriétés existantes dans le ACT sont mal formées.
     */
    private String getProperty(String key) throws DetailedException {
        String result = "";

        // Récupération des balises properties
        List<Element> properties = getPropertiesElements();

        // Recherche de la clé
        Iterator<Element> iter = properties.iterator();
        while (iter.hasNext()) {
            try {
                Element data = iter.next();
                Element property = data.getChild("property");
                if (property.getText().equals(key)) {
                    // On a trouvé la balise data pour la clé demandée
                    result = data.getChild(VALUE).getText();
                    return result;
                }

            } catch (Exception e) {
                logger.info(
                        "Erreur d'analyse du ACT '" + this.actFile.getAbsolutePath() + "' lors de la recherche " + "de"
                                + " la propriété '" + key + "' : " + e.toString());
                // On n'arrête pas la recherche suite à cette erreur
                // En effet, une balise invalide ne doit pas empêcher la
                // recherche
                // sur les autres balises.
            }
        }
        return result;
    }

    private List<Element> getPropertiesElements() throws DetailedException {
        List<Element> result;
        try {
            Element propertiesElement = doc.getRootElement().getChild("properties");
            result = propertiesElement.getChildren();

        } catch (Exception e) {
            throw new DetailedException(e).addMessage(
                    "Impossible d'analyser les balises properties du ACT '" + this.actFile.getAbsolutePath() + "'.");
        }
        return result;
    }

    /**
     * @return le titre de ce ACT, tel que stocké dans la propriété _H1, ou '' si cette propriété n'est pas définie
     */
    public String getTitle() throws DetailedException {
        return getProperty("_H1");
    }

    public void setTitle(String title) throws DetailedException {
        if (title == null) {
            throw new DetailedException("Le titre ne doit pas être nul !");
        }

        logger.debug("On fixe le titre à '" + title + "'");
        setProperty("_H1", title, true);
    }

    public void setStoreProgression(String value) throws DetailedException {
        try {
            Element navigation = doc.getRootElement().getChild(NAVIGATION);
            Element storeProgression = navigation.getChild("storeProgression");
            storeProgression.setText(value);

        } catch (Exception e) {
            throw new DetailedException(e).addMessage(
                    "Impossible de modifier la balise storeProgression du ACT '" + this.actFile.getAbsolutePath()
                            + "'.");
        }
    }

    public void setScoreOnce(String value) throws DetailedException {
        try {
            Element navigation = doc.getRootElement().getChild(NAVIGATION);
            Element storeProgression = navigation.getChild("scoreOnce");
            storeProgression.setText(value);

        } catch (Exception e) {
            throw new DetailedException(e).addMessage(
                    "Impossible de modifier la balise scoreOnce du ACT '" + this.actFile.getAbsolutePath() + "'.");
        }
    }

    private Element newNodeElement(Element contentElement, String scoreRollup, String progressRollup) throws DetailedException {

        Element result = new Element("node");

        try {
            if (contentElement == null) {
                throw new DetailedException("La balise 'content' est nulle !");
            }
            if (scoreRollup == null) {
                throw new DetailedException("Le scoreRollup est nul !");
            }
            if (progressRollup == null) {
                throw new DetailedException("Le progressRollup est nul !");
            }

            // On récupère le contentId de la balise content créée précédement
            String contentId = contentElement.getAttributeValue(ID);
            if (contentId == null) {
                throw new DetailedException("Balise 'content' mal générée : attribut 'id' absent !");
            }

            result.setAttribute(CONTENT_ID, contentId);
            result.setAttribute(SCORE_ROLLUP, scoreRollup);
            result.setAttribute(PROGRESS_ROLLUP, progressRollup);

            return result;

        } catch (DetailedException e) {
            if (contentElement != null) {
                e.addMessage("Impossible de créer une balise 'node' d'après la balise 'content' : "
                        + contentElement.toString() + " pour le scoreRollup='" + scoreRollup
                        + "' et le progressRollup='" + progressRollup + "'.");
            }
            throw e;
        }

    }

    /**
     * @return une balise 'content' pour ce contenu
     */
    private Element newContentElement(A7Content content, Map<String, String> attributes, String title,
                                      String subtitle, int duration) throws DetailedException {

        if (content == null) {
            throw new DetailedException("Impossible de créer la balise content : object content nul !");
        }
        if (attributes == null) {
            throw new DetailedException("Impossible de créer la balise content : object attributes nul !");
        }
        if (title == null) {
            throw new DetailedException("Impossible de créer la balise content : object title nul !");
        }
        if (subtitle == null) {
            throw new DetailedException("Impossible de créer la balise content : object title nul !");
        }

        try {
            // Balise content
            Element contentElement = new Element("content");
            int contentId = getFreeContentId();
            contentElement.setAttribute(ID, Integer.toString(contentId));
            contentElement.setAttribute(MENU_ID, Integer.toString(contentId));
            contentElement.setAttribute(DURATION, Integer.toString(duration));

            // Ajout des attributs
            for (String key : attributes.keySet()) {
                String value = attributes.get(key);
                contentElement.setAttribute(key, value);
            }

            // Balise titre
            Element titleElement = new Element(TITLE);
            titleElement.setContent(new DefaultJDOMFactory().cdata(title));
            contentElement.addContent(titleElement);

            // Balise sous-titre
            Element subtitleElement = new Element(SUBTITLE);
            subtitleElement.setContent(new DefaultJDOMFactory().cdata(subtitle));
            contentElement.addContent(subtitleElement);

            // Balise rlc/filename
            Element rlcElement = new Element(RLC);
            Element filenameElement = new Element(FILENAME);
            String a7Filename = getRelativePathTo(content);
            filenameElement.setContent(new DefaultJDOMFactory().cdata(a7Filename));
            rlcElement.addContent(filenameElement);
            contentElement.addContent(rlcElement);

            if ("bilan".equalsIgnoreCase(attributes.get("type"))) {
                // Balise doors/door SI C'EST UN ECRAN DE BILAN
                Element doorsElement = new Element("doors");

                Element doorElement = new Element("door");
                Attribute id = new Attribute(ID, "startout");
                doorElement.setAttribute(id);

                Element gotoDoorElement = new Element("gotoDoor");
                Attribute contentIdAttribute = new Attribute("contentId", "1");
                Attribute doorId = new Attribute("doorId", "startin");
                gotoDoorElement.setAttribute(contentIdAttribute);
                gotoDoorElement.setAttribute(doorId);

                doorElement.addContent(gotoDoorElement);
                doorsElement.addContent(doorElement);
                contentElement.addContent(doorsElement);
            }

            return contentElement;

        } catch (Exception e) {
            throw new DetailedException(e).addMessage("Impossible de créer la balise content pour le contenu.");
        }
    }

    /**
     * Effectue les traitements pour l'ajout d'un contenu : balise content, et
     * balise node.<br/>
     * Ajoute la balise content aux contents, mais n'ajoute PAS la balise node
     * dans le contentTree : elle est renvoyée pour qu'elle soit attachée où il
     * faut.
     */
    private Element addContent(A7Content content, Map<String, String> attributes, String title, String subtitle,
                               String scoreRollup, String progressRollup, int duration, boolean isMainContent,
                               Parameters parameters) throws DetailedException {
        try {
            logger.debug("Récupération des fichiers du contenu dans " + "le répertoire des fichiers du ACT : "
                    + this.getContentDir().getAbsolutePath());
            // Lors de la copie, le nom du A7 peut avoir été changé (gestion des
            // doublons)
            A7Content addedContent;
            addedContent = content.safeCopyTo(this.getContentDir());
            logger.debug("Fabrication de la balise 'content'..");
            Element contentElement = newContentElement(addedContent, attributes, title, subtitle, duration);

            logger.debug("Ajout du content '" + addedContent.getMainContent().getFile().getAbsolutePath() + "' dans "
                    + "les contents du ACT...");
            Element contentsElement = doc.getRootElement().getChild(CONTENTS);
            if (contentsElement == null) {
                throw new DetailedException("Pas de balise 'contents' dans le modèle de ACT !");
            }
            contentsElement.addContent(contentElement);

            logger.debug("Fabrication de la balise 'node'...");
            Element nodeElement = newNodeElement(contentElement, scoreRollup, progressRollup);

            if (isMainContent) {
                logger.debug("Enregistrement de ce content comme mainContent du ACT...");
                // Récupération de l'attribut id de la balsie content dans le
                // contents
                String id = contentElement.getAttributeValue(ID);

                if (id == null) {
                    throw new DetailedException(
                            "La balise content pour '" + content.getMainContent().getFile().getAbsolutePath()
                                    + "' a été mal formée : pas d'attribut 'id' !");
                }

                this.setMainContent(id);
            }

            return nodeElement;

        } catch (DetailedException e) {
            e.addMessage("Impossible d'ajouter un écran pour le contenu '"
                    + content.getMainContent().getFile().getAbsolutePath() + "' dans le ACT.");
            throw e;
        }

    }

    /**
     * Fixe le mainContent à cette valeur
     */
    private void setMainContent(String id) throws DetailedException {
        logger.debug("On fixe le mainContent du ACT à id='" + id + "'");
        try {
            // Recherche de la balise 'navigation'.'mainContent'
            Element navigationElement = doc.getRootElement().getChild(NAVIGATION);
            if (navigationElement == null) {
                throw new DetailedException("Pas de balise 'navigation' dans le ACT !");
            }
            Element mainContentElement = navigationElement.getChild("mainContent");
            if (mainContentElement == null) {
                throw new DetailedException("Pas de balise 'mainContent' dans le ACT !");
            }

            mainContentElement.setAttribute(CONTENT_ID, id);

        } catch (Exception e) {
            throw new DetailedException(e).addMessage("Impossible de fixer le mainContent du ACT à id='" + id + "'.");
        }
    }

    /**
     * @param nodeElement une balise node , dans laquelle on va chercher la valeur de l'attribut 'contentid', pour la
     *                    reprendre comme id ET contentId pour la balise item.
     */
    private Element newItemElement(Element nodeElement, String menuClickable, String menuTitle) throws DetailedException {
        try {

            if (nodeElement == null) {
                throw new DetailedException("Balise node nulle !");
            }
            if (menuClickable == null) {
                throw new DetailedException("Valeur de menuClickable nulle !");
            }
            if (menuTitle == null) {
                throw new DetailedException("Valeur de menuTitle nulle !");
            }

            Element result = new Element("item");

            // On récupère le contentId de la balise content créée précédement
            String contentId = nodeElement.getAttributeValue(CONTENT_ID);
            if (contentId == null) {
                throw new DetailedException("Balise 'node' mal générée : attribut 'contentid' absent !");
            }

            result.setAttribute("contentId", contentId);
            result.setAttribute(ID, contentId);
            result.setAttribute("clickable", menuClickable);

            // Contenu textuel
            Element text = new Element("text");
            text.setContent(new DefaultJDOMFactory().cdata(menuTitle));
            result.addContent(text);

            return result;

        } catch (Exception e) {
            throw new DetailedException(e).addMessage(
                    "Impossible de produire une balise 'item' de menu pour le " + "contenu.");
        }
    }

    /**
     * @return le chemin relatif (avec le nom de fichier) vers le A7 de ce contenu, depuis le fichier ACT
     */
    private String getRelativePathTo(A7Content content) throws DetailedException {
        try {
            return FilenamesUtils.getRelativePathTo(this.getActFile(), content.getMainContent().getFile());
        } catch (Exception e) {
            throw new DetailedException(e).addMessage("Impossible d'obtenir le chemin relatif pour le contenu.");
        }
    }

    /**
     * Ajoute ce contenu.
     *
     * @param content              le contenu
     * @param attributes           les attributs (clé/valeur) à ajouter à la balise du contenu
     * @param showInMenu           le contenu doit-il être ajouté au menu ?
     * @param insertContentTagName le nom de la balise dans le contentsTree où insérer les contenus
     * @param insertMenuTagName    le nom de la balise dans le menu où insérer les contenus. Si vide, alors on utilise
     *                             contentIdWhereAttach
     * @param duration             la durée de consultation du contenu
     * @param isMainContent        ce contenu doit-il être enregistré comme mainContent dans le ACT ?
     * @param contentIdWhereAttach le contentid du node où accrocher le contenu. N'est pas utilisé si insertMenuTagName
     *                             n'est pas vide
     * @return l'identifiant du content ajouté (= attribut id de sa balise content). "" Si aucun contenu ajouté.
     */
    public String addContent(A7Screen content, Map<String, String> attributes, String showInMenu, String title,
                             String subtitle, String scoreRollup, String progressRollup, String menuTitle,
                             String menuClickable, String insertContentTagName, String insertMenuTagName,
                             int duration, boolean isMainContent, String contentIdWhereAttach, Locale locale,
                             Parameters parameters) throws DetailedException {

        if (content == null) {
            throw new DetailedException("Impossible d'ajouter un contenu nul.");
        }

        try {
            // On interprète les paramètres en fonction du contenu, par
            // exemple remplacement de '@@content_name' par le nom du
            // contenu...
            String contentTitle = ParameterParser.parseParameter(title, content, locale, parameters);
            String contentSubtitle = ParameterParser.parseParameter(subtitle, content, locale, parameters);
            String contentShowInMenu = ParameterParser.parseParameter(showInMenu, content, locale, parameters);
            String contentScoreRollup = ParameterParser.parseParameter(scoreRollup, content, locale, parameters);
            String contentProgressRollup = ParameterParser.parseParameter(progressRollup, content, locale, parameters);
            String contentMenuTitle = ParameterParser.parseParameter(menuTitle, content, locale, parameters);
            String contentMenuClickable = ParameterParser.parseParameter(menuClickable, content, locale, parameters);

            // Idem pour les attributs
            Map<String, String> contentAttributes = new HashMap<>();
            for (String key : attributes.keySet()) {
                String value = attributes.get(key);
                value = ParameterParser.parseParameter(value, content, locale, parameters);
                logger.debug("Retrouvé l'attribut '" + key + "'='" + value + "' à ajouter à la balise content.");
                contentAttributes.put(key, value);
            }

            logger.debug(
                    "title='" + contentTitle + "' subtitle='" + contentSubtitle + "' menuTitle='" + contentMenuTitle
                            + "'");

            // Le texte à présenter dans le menu n'est pas récupéré avec du formattage HTML, mais s'il contient un
            // retour à la ligne dans Excel, alors il peut contenir un <br/>.
            // On supprime donc d'éventuels <br/> dans le titre présenté dans le menu.
            contentMenuTitle = FilesUtils.replaceAll(contentMenuTitle, "<br/>", " ");

            return addContent(content.getA7Content(), contentAttributes, contentShowInMenu, contentTitle,
                    contentSubtitle, contentScoreRollup, contentProgressRollup, contentMenuTitle,
                    contentMenuClickable, insertContentTagName, insertMenuTagName, duration, isMainContent,
                    contentIdWhereAttach, locale, parameters);

        } catch (DetailedException e) {
            e.addMessage("Impossible d'ajouter le contenu.");
            throw e;
        }
    }

    /**
     * Ajoute ce contenu.
     *
     * @param content              le contenu
     * @param attributes           les attributs (clé/valeur) à ajouter à la balise du contenu
     * @param showInMenu           le contenu doit-il être ajouté au menu ?
     * @param insertContentTagName le nom de la balise dans le contentsTree où insérer les contenus
     * @param insertMenuTagName    le nom de la balise dans le menu où insérer les contenus. Si vide, alors on utilise
     *                             contentIdWhereAttach
     * @param duration             la durée de consultation du contenu
     * @param isMainContent        ce contenu doit-il être enregistré comme mainContent dans le ACT ?
     * @param contentIdWhereAttach le contentid du node où accrocher le contenu. N'est pas utilisé si insertMenuTagName
     *                             n'est pas vide
     * @return l'identifiant du content ajouté (= attribut id de sa balise content). "" Si aucun contenu ajouté.
     */
    public String addContent(A7Content content, Map<String, String> attributes, String showInMenu, String title,
                             String subtitle, String scoreRollup, String progressRollup, String menuTitle,
                             String menuClickable, String insertContentTagName, String insertMenuTagName,
                             int duration, boolean isMainContent, String contentIdWhereAttach, Locale locale,
                             Parameters parameters) throws DetailedException {

        if (content == null) {
            throw new DetailedException("Impossible d'ajouter un contenu nul.");
        }

        try {
            logger.debug("title='" + title + "' subtitle='" + subtitle + "' menuTitle='" + menuTitle + "'");

            Element nodeElement = addContent(content, attributes, title, subtitle, scoreRollup, progressRollup,
                    duration, isMainContent, parameters);

            // Récupération de l'attribut contentId de la balise node.
            // Cet id est celui de l'attribut id de la balise content
            // correspondante dans le contentsTree
            String contentId = nodeElement.getAttributeValue(CONTENT_ID);
            if (contentId == null) {
                throw new DetailedException(
                        "La balise content pour '" + content.getMainContent().getFile().getAbsolutePath()
                                + "' a été mal formée : pas d'attribut 'id' !");
            }

            if (!isRandomPoolScreen(attributes)) {
                insertIntoContentsTree(nodeElement, insertContentTagName, contentIdWhereAttach);
            }

            if (showInMenu.equalsIgnoreCase("true")) {
                logger.debug("Fabrication de la balise menu 'item' pour ce content...");
                Element itemElement = newItemElement(nodeElement, menuClickable, menuTitle);

                logger.debug("Ajout de l'item pour '" + content.getMainContent().getFile().getAbsolutePath() + "' "
                        + "dans le menu du ACT...");
                // On considère qu'on travaille dans le premier menu trouvé
                // dans la balise "menus"
                Element menusElement = doc.getRootElement().getChild("menus");
                Element menuElement = menusElement.getChild("menu");
                Element menuInsertionTag = XmlUtils.getChildDeep(menuElement, insertMenuTagName);
                if (menuInsertionTag == null) {
                    throw new DetailedException(
                            "Pas de balise '" + insertMenuTagName + "' dans le modèle de ACT " + "pour"
                                    + " insérer les écrans de contenu !");
                }
                XmlUtils.addJustAfter(menuInsertionTag.getParentElement(), menuInsertionTag, itemElement);
            } else {
                logger.debug("Le contenu n'apparait pas dans le menu, car showInMenu='" + showInMenu + "'.");
            }

            logger.debug("Enregistrement du ACT...");
            commit();

            return contentId;

        } catch (DetailedException e) {
            e.addMessage("Impossible d'ajouter le contenu.");
            throw e;
        }
    }

    /**
     * Est-ce que les attributs de l'écran en cours d'ajout contient le tag randomScreen=true
     */
    private boolean isRandomPoolScreen(Map<String, String> screenAttributes) {
        return screenAttributes.get("randomPool").equals("true");
    }

    /**
     * Insère ce node dans le contentsTree :<br/>
     * - si insertContentTagName n'est pas vide, alors à la suite de la balise
     * insertContentTagName, comme premier de ses fils<br/>
     * SINON<br/>
     * - si contentIdWhereAttach n'est pas vide, comme fils du node dont le
     * contentid est contentIdWhereAttach,.<br/>
     * SINON<br/>
     * lance une exception.<br/>
     */
    private void insertIntoContentsTree(Element nodeElement, String insertContentTagName,
                                        String contentIdWhereAttach) throws DetailedException {

        if (nodeElement == null) {
            throw new DetailedException("Le node à accrocher est nul !");
        }
        if (insertContentTagName == null) {
            throw new DetailedException("La valeur de insertContentTagName est nulle !");
        }
        if (contentIdWhereAttach == null) {
            throw new DetailedException("La valeur de contentIdWhereAttach est nulle !");
        }

        try {
            if (!insertContentTagName.isEmpty()) {
                logger.debug("Ajout du node dans le contentsTree du ACT comme premier successeur de la balise '"
                        + insertContentTagName + "'...");
                Element contentsTreeElement = doc.getRootElement().getChild(CONTENTS_TREE);
                Element nodeInsertionTag = XmlUtils.getChildDeep(contentsTreeElement, insertContentTagName);
                if (nodeInsertionTag == null) {
                    throw new DetailedException("Pas de balise '" + insertContentTagName + "' dans le modèle de ACT "
                            + "pour insérer les écrans de contenu !");
                }
                XmlUtils.addJustAfter(nodeInsertionTag.getParentElement(), nodeInsertionTag, nodeElement);

            } else if (contentIdWhereAttach.isEmpty()) {
                logger.debug(
                        "Ajout du node dans le contentsTree du ACT comme premier fils du node dont le contentid " + ""
                                + "" + "" + "" + "est '" + contentIdWhereAttach + "'...");
                // Récupération du node dont le contentid est demandé
                Element node = getNodeTag(contentIdWhereAttach);
                if (node == null) {
                    throw new DetailedException(
                            "Aucun node retrouvé pour le contentid '" + contentIdWhereAttach + "'" + " !");
                }
                // Ajout du node du contenu, comme premier fils
                XmlUtils.addAsFirstChild(node, nodeElement);

            } else {
                throw new DetailedException(
                        "insertContentTagName n'est pas défini ET contentIdWhereAttach n'est pas " + "" + "" + "" + ""
                                + "défini " + ": le point d'attache des nodes dans le contentsTree doit " + "être "
                                + "précisé !");
            }

        } catch (DetailedException e) {
            e.addMessage("Impossible d'ajouter le node dans le contentsTree.");
            throw e;
        }

    }

    /**
     * Ajoute ces contenus, comme liste de balises 'content' en remplacement de
     * la balise insertionTagName OU comme fils du node dont le contentId est
     * passé en paramètre.
     *
     * @param insertContentTagName le nom de la balise dans le contentsTree où insérer les contenus
     * @param insertMenuTagName    le nom de la balise dans le menu où insérer les contenus
     * @param contentIdWhereAttach le contentid du node où accrocher les contenus
     */
    public void addContents(List<A7Screen> contents, Map<String, String> attributes, String showInMenu, String title,
                            String subtitle, String scoreRollup, String progressRollup, String menuTitle,
                            String menuClickable, String insertContentTagName, String insertMenuTagName,
                            boolean setFirstContentAsMainContent, String contentIdWhereAttach, Parameters parameters,
                            Locale locale) throws DetailedException {

        try {
            // On parcourt les contenus du dernier au premier car les balises
            // sont ajoutées juste après les points d'insertion. Il faut donc
            // commencer par le dernier pour qu'il soit après les précédents.

            //On vérifie d'abord si il existe une page de bilan dans le module
            boolean resultsPageFound = false;
            for (int i = contents.size() - 1; i >= 0; i--) {
                A7Screen content = contents.get(i);
                Map<String, String> contentAttributes = new HashMap<>();
                contentAttributes.putAll(attributes);
                for (String attributeName : contentAttributes.keySet()) {
                    String value = contentAttributes.get(attributeName);
                    if (attributeName.equals("type")) {
                        String newValue = new ActContentAttributeValueParser().parse(value, content, locale);
                        if (newValue.equals("bilan")) {
                            resultsPageFound = true;
                        }
                    }
                }
            }

            for (int i = contents.size() - 1; i >= 0; i--) {

                A7Screen content = contents.get(i);
                logger.debug("Ajout de l'écran de contenu '" + content.getIdentifier() + "'...");

                // La durée de consultation de cet écran
                int duration = content.getDuration(locale);

                boolean isMainContent = false;
                if (setFirstContentAsMainContent && i == 0) {
                    // Si on a demandé de fixer le premier écran comme
                    // mainContent
                    // On enregistre cet écran comme mainContent seulement si
                    // c'est le dernier écran à ajouter, c'est à dire le premier
                    // des écrans
                    isMainContent = true;
                }

                // On va mettre à jour les attributs associés à ce contenu.
                Map<String, String> contentAttributes = new HashMap<>();
                try {
                    // On récupère tous les attributs existants
                    contentAttributes.putAll(attributes);

                    // On ajoute les attributs spécifiques au traitement
                    contentAttributes.putAll(getContentAttributes(content, locale));
                } catch (Exception e) {
                    throw new DetailedException(e).addMessage(
                            "Impossible de récupérer les attributs spécifiques du " + "contenus.");
                }

                // On interprète les attributs "dynamiques"
                for (String attributeName : contentAttributes.keySet()) {
                    String value = contentAttributes.get(attributeName);
                    // On interpète la valeur de cet attribut
                    String newValue = new ActContentAttributeValueParser().parse(value, content, locale);

                    //Si cette page est la dernière, que l'attribut verifié est le "type", qu'il est vide, et qu'on a
                    // pas trouvé de bilan dans le module
                    // RMAR : code commenté, car il rend la dernière page qui ne serait pas un bilan invisible dans
                    // le menu.
                    //                    if (i == contents.size() - 1 && attributeName.equals("type") && newValue
                    //                    .equals("")
                    //                            && !resultsPageFound) {
                    //                        //On force cette page du module à être le bilan
                    //                        newValue = "bilan";
                    //                    }

                    logger.debug("L'attribut '" + attributeName + "' sera stocké avec la valeur '" + newValue + "'.");
                    contentAttributes.put(attributeName, newValue);
                }

                // On interprète également les attributs fixes :
                ActContentAttributeValueParser parser = new ActContentAttributeValueParser();
                String parsedShowInMenu = parser.parse(showInMenu, content, locale);
                String parsedTitle = parser.parse(title, content, locale);
                String parsedSubtitle = parser.parse(subtitle, content, locale);
                String parsedScoreRollup = parser.parse(scoreRollup, content, locale);
                String parsedProgressRollup = parser.parse(progressRollup, content, locale);
                String parsedMenuTitle = parser.parse(menuTitle, content, locale);
                String parsedMenuClickable = parser.parse(menuClickable, content, locale);

                addContent(content, contentAttributes, parsedShowInMenu, parsedTitle, parsedSubtitle,
                        parsedScoreRollup, parsedProgressRollup, parsedMenuTitle, parsedMenuClickable,
                        insertContentTagName, insertMenuTagName, duration, isMainContent, contentIdWhereAttach,
                        locale, parameters);
            }

        } catch (Exception e) {
            throw new DetailedException(e).addMessage("Impossible d'ajouter les contenus dans le ACT.");
        }

    }

    /**
     * @return Les attributs spécifiques à ce contenu
     */
    private Map<String, String> getContentAttributes(A7Screen screen, Locale locale) {
        Map<String, String> attributes = new HashMap<>();

        // DIFFICULTE
        attributes.put("difficulty", screen.getDifficulty(locale));

        // THEME
        attributes.put("theme", screen.getTheme(locale));

        return attributes;

    }

    /**
     * Suppression de toutes les balises portant ce nom
     *
     * @param tagNames les noms des balises à supprimer
     */
    public void cleanTags(List<String> tagNames) throws DetailedException {
        try {
            logger.debug("Nettoyage du ACT : suppression des balises : " + tagNames.toString());

            List<Element> toDelete = new ArrayList<>();

            @SuppressWarnings("rawtypes") Iterator iter = doc.getRootElement().getDescendants();
            while (iter.hasNext()) {
                Object descendant = iter.next();
                if (descendant instanceof Element && tagNames.contains(((Element) descendant).getName())) {
                    toDelete.add((Element) descendant);
                }
            }

            for (Element element : toDelete) {
                element.detach();
            }

            logger.debug("Enregistrement du ACT...");
            commit();

        } catch (Exception e) {
            throw new DetailedException(e).addMessage("Impossible de nettoyer le fichier ACT.");
        }
    }

    /**
     * @return la valeur de la propriété 'masteryScore', ou '' si cette propriété n'est pas définie
     */
    public String getMasteryScore() throws DetailedException {
        try {
            Element contentsTree = doc.getRootElement().getChild(CONTENTS_TREE);
            return contentsTree.getAttribute("masteryScore").getValue();
        } catch (Exception e) {
            throw new DetailedException(e).addMessage("Impossible de récupérer le masteryscore dans le ACT.");
        }
    }

    public void setMasteryScore(int masteryScore) throws DetailedException {
        // try {
        // logger.debug("Mise à jour de la balise 'cond'...");
        // Element condElement = doc.getRootElement().getChild("progress")
        // .getChild("state").getChild("cond");
        // condElement.setContent((new DefaultJDOMFactory())
        // .cdata("masteryScore > " + masteryScore));
        // } catch (Exception e) {
        // throw new DetailedException(
        // "Impossible de mettre à jour la balise 'cond' dans le ACT : "
        // + e.toString());
        // }
        // try {
        // logger.debug("Mise à jour de la balise 'masteryscore'...");
        // Element masteryscoreElement = doc.getRootElement()
        // .getChild("progress").getChild("state")
        // .getChild("masteryscore");
        // masteryscoreElement.setText(new Integer(masteryScore).toString());
        // } catch (Exception e) {
        // throw new DetailedException(
        // "Impossible de mettre à jour la balise 'masteryscore' dans le ACT : "
        // + e.toString());
        // }
        try {
            logger.debug("Mise à jour de la balise 'contentsTree'...");
            Element contentsTree = doc.getRootElement().getChild(CONTENTS_TREE);
            contentsTree.setAttribute("masteryScore", Integer.toString(masteryScore));
        } catch (Exception e) {
            throw new DetailedException(e).addMessage(
                    "Impossible de mettre à jour la balise '" + CONTENTS_TREE + "' " + "" + "" + "" + ""
                            + "dans le ACT.");
        }
    }

    /**
     * @return la somme de la durée (en secondes) des écrans de contenus dans l'arborescence contentsTree. La durée
     * prise en compte pour chacun d'eux est la valeur de l'attribut "duration" de leur balise content.
     */
    public int getDuration() throws DetailedException {
        try {
            int result = 0;

            // On liste les identifiant des contenus présents dans le
            // contentsTree
            List<String> contentsId = new ArrayList<>();
            Element contentsTree = doc.getRootElement().getChild(CONTENTS_TREE);
            @SuppressWarnings("rawtypes") Iterator iter = contentsTree.getDescendants();
            while (iter.hasNext()) {
                Object next = iter.next();
                if (next instanceof Element) {
                    String contentId = ((Element) next).getAttribute(CONTENT_ID).getValue();
                    if (contentId != null) {
                        contentsId.add(contentId);
                    }
                }
            }

            // Pour tous ces contenus, on cherche la durée et on l'ajoute au
            // résultat
            for (String contentId : contentsId) {
                Element content = getContentTag(contentId);
                if (content == null) {
                    logger.info("Le contenu '" + contentId + "' est défini dans le contentsTree, mais pas dans les "
                            + "contenus : attention : une erreur est probable à la consultation du module !");

                } else {
                    // on a trouvé le contenu, on cherche sa balise duration
                    String contentDurationString = content.getAttributeValue(DURATION);
                    if (contentDurationString == null) {
                        // pas de duration définie pour ce content : on l'ignore
                        // dans le calcul

                    } else {
                        try {
                            int duration = Integer.parseInt(contentDurationString);
                            result = result + duration;
                        } catch (Exception e) {
                            logger.info("La durée '" + contentDurationString + "' est définie pour le contenu '"
                                    + content.toString() + "' : cette valeur ne peut être prise en compte dans le "
                                    + "calcul" + " de la durée totale du module : " + e.toString());
                        }
                    }
                }
            }
            return result;

        } catch (Exception e) {
            throw new DetailedException(e).addMessage("Impossible de récupérer la durée de consultation du ACT.");
        }
    }

    /**
     * @return la balise content dans les contents, pour ce contentId. Renvoie null si non trouvé.
     */
    private Element getContentTag(String contentId) throws DetailedException {

        if (contentId == null) {
            logger.info("Demande d'un content pour un contentId nul !");
            return null;
        }

        Element result = null;
        try {
            Element contents = doc.getRootElement().getChild(CONTENTS);
            List<Element> contentList = contents.getChildren();
            for (Element content : contentList) {
                String thisContentId = content.getAttributeValue(ID);
                if (thisContentId.equals(contentId)) {
                    result = content;
                }
            }
        } catch (Exception e) {
            throw new DetailedException(e).addMessage(
                    "Impossible de retrouver le content pour contentId='" + contentId + "'.");
        }
        return result;
    }

    /**
     * @return la balise node dans le contentsTree, pour ce contentId. Renvoie null si non trouvé.
     */
    private Element getNodeTag(String contentId) throws DetailedException {

        if (contentId == null) {
            logger.info("Demande d'un node pour un contentId nul !");
            return null;
        }

        Element result = null;
        try {
            Element contents = doc.getRootElement().getChild(CONTENTS_TREE);
            Iterator<org.jdom2.Content> iter = contents.getDescendants();

            while (iter.hasNext()) {
                Object next = iter.next();
                if (next instanceof Element) {
                    Element current = (Element) next;

                    String thisContentId = current.getAttributeValue(CONTENT_ID);
                    if (thisContentId != null && thisContentId.equals(contentId)) {
                        result = current;
                    }
                }
            }
        } catch (Exception e) {
            throw new DetailedException(e).addMessage(
                    "Impossible de retrouver le node pour contentId='" + contentId + "'.");
        }
        return result;
    }

    /**
     * @return la liste de tous les contenus (A7) référencés par ce ACT.
     */
    private List<A7Content> getContents() throws DetailedException {
        List<A7Content> contents = new ArrayList<>();
        try {
            // On parcourt toutes les balises filename
            Element contentElements = doc.getRootElement().getChild(CONTENTS);
            List<Element> contentList = contentElements.getChildren();
            for (Element contentElement : contentList) {
                String thisContentId = contentElement.getAttributeValue(ID);

                // On recherche l'URL relative de ce contenu
                Element filenameElement;
                try {
                    filenameElement = contentElement.getChild(RLC).getChild(FILENAME);
                } catch (Exception e) {
                    throw new DetailedException(e).addMessage(
                            "Impossible de retrouver la balise filename pour le " + "contenu '" + thisContentId
                                    + "' !");
                }
                String relativeUrl = filenameElement.getText();
                logger.debug("Url relative pour le contenu '" + thisContentId + "' : '" + relativeUrl + "'");

                // Récupération du fichier correspondant
                relativeUrl = FilenamesUtils.getNormalizedPath(relativeUrl);
                File file = new File(this.getActFile().getParent() + "/" + relativeUrl);
                logger.debug("Fichier pour le contenu '" + thisContentId + "' : '" + file.getAbsolutePath() + "'");

                if (!file.exists()) {
                    throw new Exception(
                            "Le fichier pour le contenu '" + thisContentId + "' : '" + file.getAbsolutePath()
                                    + "' n'existe pas !");
                }

                // On recherche le nom du fichier a7 pour en faire l'identifiant
                String id = file.getName();
                if (id.indexOf('.') > 0) {
                    id = id.substring(0, id.lastIndexOf('.'));
                }

                A7Content content = A7Content.createFromA7File(file);
                contents.add(content);
            }

            return contents;

        } catch (Exception e) {
            throw new DetailedException(e).addMessage("Impossible de retrouver les fichiers des A7 du ACT.");
        }
    }

    /**
     * S'assure que tous les noms des A7 référencés sont normalisés, et modifie
     * les extensions. Change les noms de fichiers (noms du fichier et son
     * extension) et les balises content si nécessaire. Enregistre les
     * modifications dans le ACT.
     *
     * @param renameFiles  Si true, alors les noms de fichiers sont normalisés.
     * @param newExtension La nouvelle extension (sans le '.') à donner aux fichiers A7. Si null, alors les extensions
     *                     ne sont pas modifiées.
     */
    public void normalizeFileNames(boolean renameFiles, String newExtension) throws DetailedException {

        try {
            logger.debug("Renommage des contenus...");

            // On fabrique la liste de tous les contenus (A7) référencés par ce
            // ACT
            Map<Element, File> contents = new HashMap<>();
            try {
                // On parcourt toutes les balises filename
                Element contentElements = doc.getRootElement().getChild(CONTENTS);
                List<Element> contentList = contentElements.getChildren();
                for (Element content : contentList) {
                    String thisContentId = content.getAttributeValue(ID);

                    // On recherche l'URL relative de ce contenu
                    Element filenameElement;
                    try {
                        filenameElement = content.getChild(RLC).getChild(FILENAME);
                    } catch (Exception e) {
                        throw new DetailedException(e).addMessage(
                                "Impossible de retrouver la balise filename pour " + "le" + " contenu '" + thisContentId
                                        + "' !");
                    }
                    String relativeUrl = filenameElement.getText();
                    logger.debug("Url relative pour le contenu '" + thisContentId + "' : '" + relativeUrl + "'");

                    // Récupération du fichier correspondant
                    relativeUrl = FilenamesUtils.getNormalizedPath(relativeUrl);
                    File file = new File(this.getActFile().getParentFile() + "/" + relativeUrl);
                    logger.debug("Fichier pour le contenu '" + thisContentId + "' : '" + file.getAbsolutePath() + "'");

                    if (!file.exists()) {
                        throw new Exception(
                                "Le fichier pour le contenu '" + thisContentId + "' : '" + file.getAbsolutePath()
                                        + "' n'existe pas !");
                    }

                    contents.put(content, file);
                }
            } catch (Exception e) {
                throw new DetailedException(e).addMessage(
                        "Impossible de récupérer les noms" + " des fichiers des " + "contenus du ACT.");
            }
            logger.debug("A7 référencés par ce Act : " + contents);

            // Les fichiers traités par ce traitement, afin d'éviter les
            // doublons
            Collection<File> existingFiles = new ArrayList<>();
            // On place dans la liste des fichiers existants tous les fichiers
            // actuellement existants
            try {
                existingFiles.addAll(contents.values());
            } catch (Exception e) {
                throw new DetailedException(e).addMessage(
                        "Impossible de parcourir les " + "noms des fichiers des " + "contenus du ACT.");
            }

            // On parcourt tous contenus existants pour vérifier leurs noms de
            // fichier
            try {
                for (Element content : contents.keySet()) {
                    File file = contents.get(content);
                    String thisContentId = content.getAttributeValue(ID);

                    // Si demandé, on demande un nouveau nom pour le fichier
                    String newFilename;
                    if (renameFiles) {
                        newFilename = FilenamesUtils.rename(file.getAbsolutePath(), existingFiles);
                    } else {
                        newFilename = file.getAbsolutePath();
                    }

                    // Si demandé, on change l'extension si le fichier n'est pas
                    if (newExtension != null && newFilename.endsWith(".a7")) {
                        File tempFile = new File(newFilename);
                        newFilename = new File(tempFile.getParentFile() + "/"
                                + FilenamesUtils.changeExtension(tempFile, newExtension)).getAbsolutePath();
                    }

                    if (newFilename.equals(file.getAbsolutePath())) {
                        // le fichier n'est pas renommé par la normalisation ...
                        // on ne touche à rien !
                        logger.debug("Le A7 '" + file.getAbsolutePath() + "' n'est pas renommé.");

                    } else {
                        // Ce fichier doit être renommé !
                        logger.debug(
                                "Le A7 '" + file.getAbsolutePath() + "' doit être renommé en '" + newFilename + "'");

                        // Renommage du fichier
                        File destination = new File(newFilename);
                        FileUtils.moveFile(file, destination);

                        // Mise à jour de la balise du ACT
                        String a7Filename = FilenamesUtils.getRelativePathTo(this.getActFile(), destination);
                        Element filenameElement;
                        try {
                            filenameElement = content.getChild(RLC).getChild(FILENAME);
                        } catch (Exception e) {
                            throw new DetailedException(e).addMessage(
                                    "Impossible de retrouver la balise filename " + "pour le contenu '" + thisContentId
                                            + "' !");
                        }
                        filenameElement.setContent(new DefaultJDOMFactory() // NOPMD
                                .cdata(a7Filename));
                        logger.debug("Nouveau fichier pour le contenu '" + thisContentId + "' : '"
                                + destination.getAbsolutePath() + "' balise dans le ACT='" + a7Filename + "'");

                        // On l'enregistre parmi les fichiers existants, pour
                        // éviter qu'un autre soit renommé pareil
                        existingFiles.add(destination);
                    }
                }
            } catch (Exception e) {
                throw new DetailedException(e).addMessage("Impossible de parcourir les contenus du ACT.");
            }

            // Enregistrement des modifications dans le ACT
            commit();

            // Normalisation des références des A7 de ce ACT
            normalizeContents();

        } catch (DetailedException e) {
            e.addMessage("Impossible de normaliser les noms de contenus.");
            throw e;
        }
    }

    /**
     * Pour tous les contenus (A7) de ce ACT, on vérifie que les noms des médias
     * sont corrects. Si besoin, on modifie leur nom de fichier (dans le A7
     * concerné et dans le système de fichiers).
     */
    private void normalizeContents() throws DetailedException {
        try {
            // On récupère la liste de tous les contenus (A7) référencés par ce
            // ACT
            // String lang = ""; // On n'utilise pas la langue des contents pour
            // ce
            // traitement.
            List<A7Content> contents = getContents();

            // On prépare la liste des références à des fichiers, pour les
            // contenus.
            Map<File, References> filesReferences = new HashMap<>();

            for (A7Content content : contents) {
                try {
                    // Toutes les références à des médias
                    Map<Node, File> medias = content.getMainContent().getMediasNodes();

                    // On les groupe par fichier de destination, dans les
                    // références des fichiers.
                    for (Node node : medias.keySet()) {
                        File file = medias.get(node);
                        if (filesReferences.containsKey(file)) {
                            // Si on a déjà une référence sur ce fichier, on
                            // ajoute le contenu qui l'utilise
                            filesReferences.get(file).addContent(content);
                        } else {
                            // On crée une nouvelle référence sur ce fichier
                            References references = new References();
                            references.addContent(content);
                            filesReferences.put(file, references);
                        }
                    }

                } catch (DetailedException e) {
                    throw new DetailedException(e).addMessage(
                            "Impossible de préparer la liste des références de ce " + "contenu.");
                }
            }

            // Maintenant, on traite chacun des médias. Pour chaque média, s'il
            // doit être renommé, alors il l'est dans tous les noeuds de tous
            // les contenus qui l'utilisent.
            for (File file : filesReferences.keySet()) {

                // On demande un nom normalisé pour le fichier
                logger.debug("On souhaite normaliser le fichier " + file.getAbsolutePath());
                String renamedFilePath = FilenamesUtils.rename(file.getAbsolutePath(), filesReferences.keySet());

                if (renamedFilePath.equals(file.getAbsolutePath())) { // NOPMD
                    // le fichier n'est pas renommé par la normalisation ...
                    // on ne touche à rien !

                } else {
                    // Ce fichier doit être renommé !

                    // Le nouveau nom (sans le chemin local)
                    String renamedFileName = renamedFilePath.substring(renamedFilePath.lastIndexOf(File.separator));

                    // Les contenus qui font référence au fichier
                    References references = filesReferences.get(file);

                    // Tous les noeuds à traiter pour ce fichier
                    logger.debug("Le fichier est à renommer dans " + references.getContents().size() + " fichiers A7.");
                    for (A7Content content : references.getContents()) {

                        try {
                            // L'url relative depuis le A7Content vers le
                            // média avec son ancien nom
                            String oldFilename = FilenamesUtils.getRelativePathTo(content.getMainContent().getFile(),
                                    file);

                            // L'url relative depuis le A7Content vers le
                            // média avec son nouveau nom
                            String newFilename = FilenamesUtils.getRelativePathTo(content.getMainContent().getFile(),
                                    new File(
                                            file.getParentFile() + File.separator + renamedFileName));

                            content.renameFilename(oldFilename, newFilename);

                        } catch (Exception e) {
                            throw new DetailedException(e).addMessage(
                                    "Impossible de modifier la référence au " + "fichier" + " '"
                                            + file.getAbsolutePath() + "' dans le fichier "
                                            + content.getMainContent().getFile().getAbsolutePath());
                        }
                    }

                    // Renommage du fichier (dans le système de fichiers)
                    String filenameBeforeRename = file.getName();
                    file = FilesUtils.renameFile(file, renamedFileName);

                    // Attention, si le fichier est un .js, alors on doit
                    // également transmettre DANS le fichier le renommage.
                    CopyUtils.renameInJsFile(file, filenameBeforeRename);
                }
            }

        } catch (DetailedException e) {
            e.addMessage("Impossible de normaliser les références des A7 du ACT.");
            throw e;
        }
    }

    /**
     * Ajoute une balise resource aux resources.
     *
     * @param cdata : si true, alors les valeurs seront stockées dans du CDATA
     */
    public void addResource(String title, String filename, File file, boolean cdata) throws DetailedException {
        try {
            String detailedTitle = title;
            Element resourcesElement = doc.getRootElement().getChild("resources");
            Element resource = new Element("resource");
            Element titleElement = new Element("title");
            Element filenameElement = new Element("filename");

            // Conformité RGAA : on ajoute après le nom du fichier : "(FORMAT POIDS)". Par exemple : " (PDF 1,2Mo)"
            if (file != null && filename != null) {
                String extension = FilenameUtils.getExtension(filename); // "pdf"
                String size = FileUtils.byteCountToDisplaySize(file.length());
                detailedTitle = detailedTitle + " (" + extension + " " + size + ")";
            }

            if (cdata) {
                titleElement.setContent(new DefaultJDOMFactory().cdata(detailedTitle));
                filenameElement.setContent(new DefaultJDOMFactory().cdata(filename));
            } else {
                titleElement.setText(detailedTitle);
                filenameElement.setText(filename);
            }
            resource.addContent(titleElement);
            resource.addContent(filenameElement);
            resourcesElement.addContent(resource);

        } catch (Exception e) {
            throw new DetailedException(e).addMessage(
                    "Impossible d'ajouter la resource title='" + title + "' " + "filename='" + filename + "'.");
        }

    }

    /**
     * @return la version des exercices référencés par ce fichier ACT, true si ce sont des écrans HTML, sinon false
     */
    public boolean isA7Contents() throws DetailedException {
        for (A7Content a7content : getContents()) {
            String a7Name = a7content.getMainContent().getFile().getName();
            if (a7Name.endsWith(".a7") || a7Name.endsWith(".xml")) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return trus si le ACT contient un calcul de réussite = écran de bilan.
     */
    public boolean containsSuccessCalculus() {
        Element contentElements = doc.getRootElement().getChild(CONTENTS);
        for (Element contentElement : contentElements.getChildren()) {
            String type = contentElement.getAttributeValue("type");
            if (type.equalsIgnoreCase("bilan")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Des noeuds de référence à un média, dans un contenu.
     */
    private static class References {
        private final List<A7Content> contents = new ArrayList<>();

        References() {
        }

        /**
         * @return tous les contenus qui référencent
         */
        public List<A7Content> getContents() {
            return contents;
        }

        public void addContent(A7Content content) {
            contents.add(content);
        }
    }
}
