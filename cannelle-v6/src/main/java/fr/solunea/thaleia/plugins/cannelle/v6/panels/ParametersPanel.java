package fr.solunea.thaleia.plugins.cannelle.v6.panels;

import fr.solunea.thaleia.plugins.cannelle.packager.act.ActFormatPackager;
import fr.solunea.thaleia.plugins.cannelle.v6.MainPage;
import fr.solunea.thaleia.plugins.welcomev6.customization.*;
import fr.solunea.thaleia.plugins.welcomev6.messages.LocalizedMessages;
import fr.solunea.thaleia.service.utils.scorm.ManifestFactory;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.panels.ThaleiaFeedbackPanel;
import fr.solunea.thaleia.webapp.utils.MessagesUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;

import java.io.Serializable;
import java.nio.file.*;
import java.util.*;

public class ParametersPanel extends Panel {

    protected static final Logger logger = Logger.getLogger(ParametersPanel.class);
    private static final String DEFAULT_SCORM_VALUE = ManifestFactory.VERSION_2004;
    private final ThaleiaFeedbackPanel feedbackPanel;

    public ParametersPanel(String id, ThaleiaFeedbackPanel feedbackPanel) {
        super(id);
        this.feedbackPanel = feedbackPanel;
        init();
    }

    private void init() {
        addCustomizationPanel();
        addScormSelector();
    }

    /**
     * Ajoute le panneau d'import / export de la personnalisation cliente.
     */
    private void addCustomizationPanel() {
        add(new CannelleCustomizationPanel("customization",
       // add(new CustomizationPanel("customization",
                null,
                new MainPage(),
                getGraphicCustomizationName(),
                getGraphicCustomizationDefaultResource(),
                DEFAULT_SCORM_VALUE,
                (ICustomizationValidator & Serializable) customizationFile -> {

                    Path zipFile = Paths.get(customizationFile.getAbsolutePath());
                    FileSystem fs;
                    try {
                        fs = FileSystems.newFileSystem(zipFile, null);
                    } catch (Exception e) {
                        logger.warn(e);
                        String message = MessagesUtils.getLocalizedMessage("customization.error.file.format", MainPanel.class, (Object[]) null);
                        throw new DetailedException(message);
                    }

                    // On vérifie la présence d'un répertoire "engine" à la racine du zip
                    Path engineDir = fs.getPath("engine");
                    if (!Files.exists(engineDir)) {
                        String message = MessagesUtils.getLocalizedMessage("customization.error.engine.missing", MainPanel.class, (Object[]) null);
                        throw new DetailedException(message);
                    }

                    // On vérifie l'absence de fichier index.html à la racine
                    Path indexFile = fs.getPath("index.html");
                    if (Files.exists(indexFile)) {
                        String message = MessagesUtils.getLocalizedMessage("customization.error.index.presence", MainPanel.class, (Object[]) null);
                        throw new DetailedException(message);
                    }
                },
                feedbackPanel));
    }

    /**
     * Ajoute le panneau de sélection de la version SCORM.
     */
    private void addScormSelector() {
        // Pour chacune des propriétés personnalisables de type String : un panneau
        RepeatingView customizationPropertiesPanels = new RepeatingView("SCORMSelector");
        for (fr.solunea.thaleia.plugins.welcomev6.customization.CustomizationPropertyProposal customizationProperty : getPropertiesToCustomize()) {
            // La partie de personnalisation de classe (cutomizationClass)
            if (customizationProperty.values == null || customizationProperty.values.isEmpty()) {
                // On ajoute un panneau de type "Champ texte à remplir"
                customizationPropertiesPanels.add(new CustomizationPropertyTextValuePanel
                        (customizationPropertiesPanels.newChildId(), customizationProperty.name,
                                customizationProperty.label, customizationProperty.defaultValue));
            } else {
                // On ajoute un panneau de type "Sélecteur de valeur"
                customizationPropertiesPanels.add(new CustomizationPropertySelectorPanel(
                        customizationPropertiesPanels.newChildId(),
                        customizationProperty.name,
                        customizationProperty.label,
                        customizationProperty.values,
                        customizationProperty.defaultValue)
                );
            }
        }
        add(customizationPropertiesPanels);
    }

    /**
     * @return la liste des propriétés qu'il est possible de personnaliser.
     */
    private List<CustomizationPropertyProposal> getPropertiesToCustomize() {
        // Les locales par défaut des sessions Thaleia
        java.util.Locale fr = new java.util.Locale("fr");
        java.util.Locale en = new java.util.Locale("en");
        java.util.Locale es = new java.util.Locale("es");

        List<CustomizationPropertyProposal> properties = new ArrayList<>();

        // Le label de l'option "format SCORM"
        Map<Locale, String> scormPropertyLabel = new HashMap<>();
        scormPropertyLabel.put(fr, LocalizedMessages.getMessageForLocaleAndClass("scorm.format.option.label",
                ParametersPanel.this.getClass(), fr, (Object[]) null));
        scormPropertyLabel.put(en, LocalizedMessages.getMessageForLocaleAndClass("scorm.format.option.label",
                ParametersPanel.this.getClass(), en, (Object[]) null));
        scormPropertyLabel.put(es, LocalizedMessages.getMessageForLocaleAndClass("scorm.format.option.label",
                ParametersPanel.this.getClass(), es, (Object[]) null));

        // Les options possibles pour le format SCORM
        List<String> scormPropertyValues = new ArrayList<>();
        scormPropertyValues.add(ManifestFactory.VERSION_1_2);
        scormPropertyValues.add(ManifestFactory.VERSION_2004);
        scormPropertyValues.add(ManifestFactory.VERSION_2004_CSOD);
        scormPropertyValues.add(ManifestFactory.VERSION_2004_4);
        scormPropertyValues.add(ManifestFactory.CMI5);

        CustomizationPropertyProposal scormProperty = new CustomizationPropertyProposal(ActFormatPackager.class
                .getName(), scormPropertyLabel, scormPropertyValues, ManifestFactory.VERSION_2004);
        properties.add(scormProperty);

        return properties;
    }

    /**
     * @return le nom avec lequel la personnalisation graphique doit être stockée dans les paramètres de
     * personnalisation de l'application.
     */
    private String getGraphicCustomizationName() {
        return ActFormatPackager.class.getName();
    }

    /**
     * @return dans le jar, le nom de la ressource qui pointe sur le zip de la personnalisation graphique par défaut.
     */
    private String getGraphicCustomizationDefaultResource() {
        return "Resources_cannelle/content-customization-html.zip";
    }
}
