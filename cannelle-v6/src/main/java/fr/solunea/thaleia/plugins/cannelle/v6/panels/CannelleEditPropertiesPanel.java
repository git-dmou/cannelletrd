package fr.solunea.thaleia.plugins.cannelle.v6.panels;

import fr.solunea.thaleia.model.Content;
import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.model.Publication;
import fr.solunea.thaleia.plugins.welcomev6.contents.ActionsOnContent;
import fr.solunea.thaleia.plugins.welcomev6.properties.EditPropertiesPanel;
import fr.solunea.thaleia.webapp.panels.ThaleiaFeedbackPanel;
import org.apache.wicket.model.IModel;

public class CannelleEditPropertiesPanel extends EditPropertiesPanel {


    public CannelleEditPropertiesPanel(IModel<Content> content, IModel<Locale> locale, ActionsOnContent actionsOnContent, String id, ThaleiaFeedbackPanel feedbackPanel) {
        super(content, locale, actionsOnContent, id, feedbackPanel);

        // Le panneau de traduction automatique du module
        moduleTranslationPanel = new ModuleTranslationPanel("moduleTranslation", currentContentVersionId).setOutputMarkupId(true);
        add(moduleTranslationPanel) ;



    }

    @Override
    protected void onEditPublication(IModel<Publication> model) {

    }

    @Override
    protected void onOut() {

    }

    @Override
    public boolean showSourceLinks() {
        return false;
    }


}
