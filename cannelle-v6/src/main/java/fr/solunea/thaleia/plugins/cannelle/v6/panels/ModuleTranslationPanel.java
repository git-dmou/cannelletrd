package fr.solunea.thaleia.plugins.cannelle.v6.panels;

import fr.solunea.thaleia.model.User;
import fr.solunea.thaleia.plugins.cannelle.v6.CannelleV6Plugin;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;


public class ModuleTranslationPanel extends Panel {

    private Boolean reloadedAngular = false;
    private static Logger logger = Logger.getLogger(ModuleTranslationPanel.class);
    private String ContentVersionId;
    private String locale;

    private User authenthicatedUser;
    private String token;

//    private static final ResourceReference thaleiaAPI = new JavaScriptResourceReference(CannelleV6Plugin.class, "/js/thaleia_api.js");
    private static final ResourceReference translationController = new JavaScriptResourceReference(CannelleV6Plugin.class, "/js/translationController.js");
//    private static final ResourceReference commonFunctions = new JavaScriptResourceReference(CannelleV6Plugin.class, "/js/common_functions.js");
//    private static final ResourceReference localization = new JavaScriptResourceReference(CannelleV6Plugin.class, "/js/Localisation.js");
//    private static final ResourceReference localization_EN = new JavaScriptResourceReference(CannelleV6Plugin.class, "/js/Localisation_EN.js");
//    private static final ResourceReference localization_FR = new JavaScriptResourceReference(CannelleV6Plugin.class, "/js/Localisation_FR.js");
//    private static final ResourceReference notifier = new JavaScriptResourceReference(CannelleV6Plugin.class, "/js/Notifyer.js");

    public ModuleTranslationPanel(String id, int contentVersionId) {
        super(id);
        // transmet contentVersionId, pour le rendre disponible sur l'écran
        // et permettre l'apel à l'API transform/translate !
        Label formContentVersionId = new Label("wContentVersionId", Model.of(contentVersionId));
        add(formContentVersionId);


//        authenthicatedUser = ThaleiaSession.get().getAuthenticatedUser();

//        Label userLabel = new Label("wAuthenthicatedUser", Model.of(authenthicatedUser));
//        add(userLabel);
//        Label tokenLabel = new Label("wToken", Model.of(token));
//        add(tokenLabel);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
//        response.render(JavaScriptReferenceHeaderItem.forReference(thaleiaAPI).setDefer(true));
        response.render(JavaScriptReferenceHeaderItem.forReference(translationController).setDefer(true));
//        response.render(JavaScriptReferenceHeaderItem.forReference(commonFunctions).setDefer(true));
//        response.render(JavaScriptReferenceHeaderItem.forReference(localization).setDefer(true));
//        response.render(JavaScriptReferenceHeaderItem.forReference(localization_EN).setDefer(true));
//        response.render(JavaScriptReferenceHeaderItem.forReference(localization_FR).setDefer(true));
//        response.render(JavaScriptReferenceHeaderItem.forReference(notifier).setDefer(true));
    }


}