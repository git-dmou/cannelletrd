package fr.solunea.thaleia.plugins.cannelle.v6.panels;

import fr.solunea.thaleia.model.Content;
import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.model.Publication;
import fr.solunea.thaleia.plugins.analyze.pages.PublicationEditPage;
import fr.solunea.thaleia.plugins.publish.MainPage;
import fr.solunea.thaleia.plugins.welcomev6.contents.ActionsOnContent;
import fr.solunea.thaleia.plugins.welcomev6.properties.EditPropertiesPanel;
import fr.solunea.thaleia.webapp.panels.ThaleiaFeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class PropertiesPanel extends Panel {
    public PropertiesPanel(String id,
                           IModel<Content> content,
                           IModel<Locale> locale,
                           ActionsOnContent actionsOnContent,
                           ThaleiaFeedbackPanel feedbackPanel) {
        super(id);
        add(new EditPropertiesPanel(content, locale, actionsOnContent, "editPropertiesPanel", feedbackPanel) {
            @Override
            protected void onEditPublication(IModel<Publication> model) {
                setResponsePage(new PublicationEditPage(model, MainPage.class));
            }

            @Override
            protected void onOut() {
                setResponsePage(new fr.solunea.thaleia.plugins.cannelle.v6.MainPage("myProductions"));
            }

            @Override
            public boolean showSourceLinks() {
                return true;
            }
        });
    }
}
