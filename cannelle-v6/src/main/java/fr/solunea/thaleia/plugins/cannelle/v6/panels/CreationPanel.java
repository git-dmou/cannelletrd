package fr.solunea.thaleia.plugins.cannelle.v6.panels;

import fr.solunea.thaleia.plugins.cannelle.v6.CannelleV6Plugin;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

public class CreationPanel extends Panel {

    private Boolean reloadedAngular = false;
    protected static final Logger logger = Logger.getLogger(CreationPanel.class);

    private static final ResourceReference commonFunctions = new JavaScriptResourceReference(CannelleV6Plugin.class, "/js/common_functions.js");
    private static final ResourceReference localisation = new JavaScriptResourceReference(CannelleV6Plugin.class, "/js/Localisation.js");
    private static final ResourceReference localisation_FR = new JavaScriptResourceReference(CannelleV6Plugin.class, "/js/Localisation_FR.js");
    private static final ResourceReference localisation_EN = new JavaScriptResourceReference(CannelleV6Plugin.class, "/js/Localisation_EN.js");
    private static final ResourceReference myFile = new JavaScriptResourceReference(CannelleV6Plugin.class, "/js/MyFile.js");
    private static final ResourceReference notifyer = new JavaScriptResourceReference(CannelleV6Plugin.class, "/js/Notifyer.js");
    private static final ResourceReference filesManager = new JavaScriptResourceReference(CannelleV6Plugin.class, "/js/FilesManager.js");
    private static final ResourceReference thaleiaAPI = new JavaScriptResourceReference(CannelleV6Plugin.class, "/js/thaleia_api.js");
    private static final ResourceReference cannelleCreateContent = new JavaScriptResourceReference(CannelleV6Plugin.class, "/js/cannelleCreateContent.js");
    private static final ResourceReference angularJS = new JavaScriptResourceReference(CannelleV6Plugin.class, "/js/angular.min.js");
    private static final ResourceReference angularSanitize = new JavaScriptResourceReference(CannelleV6Plugin.class, "/js/angular-sanitize.js");
    private static final ResourceReference appRun = new JavaScriptResourceReference(CannelleV6Plugin.class, "/js/app-run.js");
    private static final ResourceReference dragndrop = new JavaScriptResourceReference(CannelleV6Plugin.class, "/js/dragndrop.js");


    public CreationPanel(String id) {
        super(id);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptReferenceHeaderItem.forReference(commonFunctions).setDefer(true));
        response.render(JavaScriptReferenceHeaderItem.forReference(localisation).setDefer(true));
        response.render(JavaScriptReferenceHeaderItem.forReference(localisation_FR).setDefer(true));
        response.render(JavaScriptReferenceHeaderItem.forReference(localisation_EN).setDefer(true));
        response.render(JavaScriptReferenceHeaderItem.forReference(myFile).setDefer(true));
        response.render(JavaScriptReferenceHeaderItem.forReference(notifyer).setDefer(true));
        response.render(JavaScriptReferenceHeaderItem.forReference(filesManager).setDefer(true));
        response.render(JavaScriptReferenceHeaderItem.forReference(thaleiaAPI).setDefer(true));
        response.render(JavaScriptReferenceHeaderItem.forReference(cannelleCreateContent).setDefer(true));
        response.render(JavaScriptReferenceHeaderItem.forReference(angularJS).setDefer(true));
        response.render(JavaScriptReferenceHeaderItem.forReference(angularSanitize).setDefer(true));
        response.render(JavaScriptReferenceHeaderItem.forReference(appRun).setDefer(true));
        response.render(JavaScriptReferenceHeaderItem.forReference(dragndrop).setDefer(true));
    }


}
