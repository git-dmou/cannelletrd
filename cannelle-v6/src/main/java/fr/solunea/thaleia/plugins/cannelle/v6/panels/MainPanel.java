package fr.solunea.thaleia.plugins.cannelle.v6.panels;

import fr.solunea.thaleia.model.Content;
import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.plugins.cannelle.v6.CannelleActionOnContents;
import fr.solunea.thaleia.plugins.cannelle.v6.MainPage;
import fr.solunea.thaleia.plugins.welcomev6.panels.PluginNavigationMenu;
import fr.solunea.thaleia.webapp.panels.ThaleiaFeedbackPanel;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class MainPanel extends Panel {

    protected static final Logger logger = Logger.getLogger(MainPanel.class);
    private static final String CONTENT_PANEL_ID = "contentPanel";

    /**
     * Panneau d'affichage des feedbacks.
     */
    private ThaleiaFeedbackPanel feedbackPanel;
    private Component contentPanel;
    private PluginNavigationMenu pluginNavigationMenu;

    public MainPanel(String id) {
        super(id);
        addFeedback();
        addPluginNavigationMenu();
        addTutorialPanel();

        // Par défaut, on présente la page de création de contenu
        contentPanel = new CreationPanel(CONTENT_PANEL_ID).setOutputMarkupId(true);
        add(contentPanel.setOutputMarkupId(true));
    }

    /**
     * Constructeur mais avec redirection sur une page du plugin Cannelle
     *
     * @param id    Identifiant du Component.
     * @param panel Nom du panel désiré ("myProductions", "parameters", "resources", "creation")
     */
    public MainPanel(String id, String panel) {
        super(id);
        addFeedback();
        addPluginNavigationMenu();
        addTutorialPanel();
        setContentPanel(panel);
    }

    private void addFeedback() {
        feedbackPanel = new ThaleiaFeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);
    }

    private void addPluginNavigationMenu() {
        // Navigation dans les pages du plugin : création, mes modules, paramètres, ressources
        pluginNavigationMenu = new PluginNavigationMenu("pluginMenuPanel") {
            @Override
            public void onCreateLinkClicked(AjaxRequestTarget target) {
                setResponsePage(new MainPage("creation"));
                //target.appendJavaScript("angular.reloadWithDebugInfo();");
            }

            @Override
            public void onMyModulesLinkClicked(AjaxRequestTarget target) {
                contentPanel = contentPanel.replaceWith(new MyProductionsPanel(CONTENT_PANEL_ID, this,
                        feedbackPanel).setOutputMarkupId(true));
                target.add(contentPanel);
            }

            @Override
            public void onParametersLinkClicked(AjaxRequestTarget target) {
                contentPanel = contentPanel.replaceWith(new ParametersPanel(CONTENT_PANEL_ID, feedbackPanel).setOutputMarkupId(true));
                target.add(contentPanel);
            }

            @Override
            public void onResourcesLinkClicked(AjaxRequestTarget target) {
                contentPanel = contentPanel.replaceWith(new ResourcesPanel(CONTENT_PANEL_ID).setOutputMarkupId(true));
                target.add(contentPanel);
            }

            @Override
            public void onEditModuleClicked(AjaxRequestTarget target, IModel<Content> content, IModel<Locale> locale) {
                contentPanel = contentPanel.replaceWith(
                                new PropertiesPanel(CONTENT_PANEL_ID, content, locale, CannelleActionOnContents
                                        .getActionsOnContent(Model.of(""), getPage()), feedbackPanel))
                        .setOutputMarkupId(true);
                target.add(contentPanel);
            }
        };

        add(pluginNavigationMenu.setOutputMarkupId(true));
    }

    private void addTutorialPanel() {
        add(new TutorialPanel("tutorialPanel", pluginNavigationMenu));
    }

    /**
     * Ouvre le panel désiré.
     *
     * @param panel Nom du panel désiré ("myProductions", "parameters", "resources", "creation")
     */
    private void setContentPanel(String panel) {
        switch (panel) {
            case "myProductions":
                contentPanel = new MyProductionsPanel(CONTENT_PANEL_ID, pluginNavigationMenu, feedbackPanel) {
                    @Override
                    public void renderHead(IHeaderResponse response) {
                        super.renderHead(response);
                        response.render(new OnDomReadyHeaderItem(
                                "$('[id^=modules]').addClass(\"active\");" +
                                "$('[id^=create]').removeClass(\"active\");" +
                                "$('[id^=parameters]').removeClass(\"active\");" +
                                "$('[id^=resources]').removeClass(\"active\");"));
                    }
                }.setOutputMarkupId(true);
                add(contentPanel);
                break;
            case "parameters":
                contentPanel = new ParametersPanel(CONTENT_PANEL_ID, feedbackPanel) {
                    @Override
                    public void renderHead(IHeaderResponse response) {
                        super.renderHead(response);
                        response.render(new OnDomReadyHeaderItem(
                                "$('[id^=modules]').removeClass(\"active\");" +
                                        "$('[id^=create]').removeClass(\"active\");" +
                                        "$('[id^=parameters]').addClass(\"active\");" +
                                        "$('[id^=resources]').removeClass(\"active\");"));
                    }
                }.setOutputMarkupId(true);
                add(contentPanel);
                break;
            case "resources":
                contentPanel = new ResourcesPanel(CONTENT_PANEL_ID) {
                    @Override
                    public void renderHead(IHeaderResponse response) {
                        super.renderHead(response);
                        response.render(new OnDomReadyHeaderItem(
                                "$('[id^=modules]').removeClass(\"active\");" +
                                        "$('[id^=create]').removeClass(\"active\");" +
                                        "$('[id^=parameters]').removeClass(\"active\");" +
                                        "$('[id^=resources]').addClass(\"active\");"));
                    }
                }.setOutputMarkupId(true);
                add(contentPanel);
                break;
            case "creation":
            default:
                // Par défaut, on présente la page de création de contenu
                contentPanel = new CreationPanel(CONTENT_PANEL_ID) {
                    @Override
                    public void renderHead(IHeaderResponse response) {
                        super.renderHead(response);
                        response.render(new OnDomReadyHeaderItem(
                                "$('[id^=modules]').removeClass(\"active\");" +
                                        "$('[id^=create]').addClass(\"active\");" +
                                        "$('[id^=parameters]').removeClass(\"active\");" +
                                        "$('[id^=resources]').removeClass(\"active\");"));
                    }
                }.setOutputMarkupId(true);
                add(contentPanel.setOutputMarkupId(true));
                break;
        }
    }
}
