package fr.solunea.thaleia.plugins.cannelle.v6.panels;

import fr.solunea.thaleia.plugins.cannelle.v6.pages.customizationEditor.CustomizationEditorPage;
import fr.solunea.thaleia.plugins.welcomev6.customization.CustomizationPanel;
import fr.solunea.thaleia.plugins.welcomev6.customization.CustomizationPropertyProposal;
import fr.solunea.thaleia.plugins.welcomev6.customization.ICustomizationValidator;
import fr.solunea.thaleia.webapp.panels.ThaleiaFeedbackPanel;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;

import java.util.List;

public class CannelleCustomizationPanel extends CustomizationPanel {
    public CannelleCustomizationPanel(String id,
                                      List<CustomizationPropertyProposal> customizationProperties,
                                      WebPage returnPage, String graphicCustomizationName,
                                      String defaultGraphicCustomizationFile, String defaultScormValue,
                                      ICustomizationValidator customizationValidator) {
        super(id, customizationProperties, returnPage, graphicCustomizationName, defaultGraphicCustomizationFile,
                defaultScormValue, customizationValidator);
        addLinkToCustomizationEditor();
    }

    public CannelleCustomizationPanel(String id,
                                      List<CustomizationPropertyProposal> customizationProperties,
                                      WebPage returnPage, String graphicCustomizationName,
                                      String defaultGraphicCustomizationFile, String defaultScormValue,
                                      ICustomizationValidator customizationValidator,
                                      ThaleiaFeedbackPanel feedbackPanel) {
        super(id, customizationProperties, returnPage, graphicCustomizationName, defaultGraphicCustomizationFile,
                defaultScormValue, customizationValidator, feedbackPanel);
        addLinkToCustomizationEditor();
    }

    private void addLinkToCustomizationEditor() {
        Link<Void> goToCustomizationEditor = new Link<>("goToCustomizationEditor") {
            @Override
            public void onClick() {
                try {
                    Page customizationEditorPage = new CustomizationEditorPage();
                    setResponsePage(customizationEditorPage);
                } catch (Exception e) {
                    logger.error(e);
                }
            }
        };
        add(goToCustomizationEditor);
    }
}
