package fr.solunea.thaleia.plugins.cannelle.v6.pages.customizationEditor;

import fr.solunea.thaleia.plugins.cannelle.v6.CannelleV6Plugin;
import fr.solunea.thaleia.webapp.ThaleiaApplication;
import fr.solunea.thaleia.webapp.pages.ThaleiaV6MenuPage;
import fr.solunea.thaleia.webapp.pages.ThaleiaPageV6;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

public class CustomizationEditorPage extends ThaleiaV6MenuPage {

    private static final ResourceReference generatorStyle = new CssResourceReference(CannelleV6Plugin.class, "pages/customizationEditor/css/style.css");
    private static final ResourceReference style = new CssResourceReference(ThaleiaPageV6.class, "css/v6/style.css");

    private static final ResourceReference index = new JavaScriptResourceReference(CannelleV6Plugin.class, "pages/customizationEditor/js/index.js");
    private static final ResourceReference fontSelect = new JavaScriptResourceReference(CannelleV6Plugin.class, "pages/customizationEditor/lib/fontselect.js");

    private static final ResourceReference angularJS = new JavaScriptResourceReference(CannelleV6Plugin.class, "pages/customizationEditor/lib/angular.min.js");
    private static final ResourceReference angularTranslate = new JavaScriptResourceReference(CannelleV6Plugin.class, "pages/customizationEditor/lib/angular-translate.min.js");

    private static final ResourceReference appRun = new JavaScriptResourceReference(CannelleV6Plugin.class, "pages/customizationEditor/js/app-run.js");
    private static final ResourceReference inputHandler = new JavaScriptResourceReference(CannelleV6Plugin.class, "pages/customizationEditor/js/inputHandler.js");
    private static final ResourceReference mainCtrl = new JavaScriptResourceReference(CannelleV6Plugin.class, "pages/customizationEditor/js/main-ctrl.js");
    private static final ResourceReference thaleiaCustomApi = new JavaScriptResourceReference(CannelleV6Plugin.class, "pages/customizationEditor/js/thaleiaApi.js");
    private static final ResourceReference commonFunctions = new JavaScriptResourceReference(CannelleV6Plugin.class, "pages/customizationEditor/js/commonFunctions.js");


    public CustomizationEditorPage() {
        super();
        addBtnBack();
    }

    private void addBtnBack() {
        add(new Link<>("btnBack") {
            public void onClick() {
                setResponsePage(ThaleiaApplication.get().getHomePage());
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(JavaScriptReferenceHeaderItem.forReference(angularJS));
        response.render(JavaScriptReferenceHeaderItem.forReference(angularTranslate));
        response.render(JavaScriptReferenceHeaderItem.forReference(fontSelect).setDefer(true));
        response.render(JavaScriptReferenceHeaderItem.forReference(thaleiaCustomApi));

        response.render(JavaScriptReferenceHeaderItem.forReference(index).setDefer(true));
        response.render(JavaScriptReferenceHeaderItem.forReference(appRun).setDefer(true));
        response.render(JavaScriptReferenceHeaderItem.forReference(mainCtrl).setDefer(true));
        response.render(JavaScriptReferenceHeaderItem.forReference(inputHandler).setDefer(true));
        response.render(JavaScriptReferenceHeaderItem.forReference(commonFunctions).setDefer(true));


        response.render(CssReferenceHeaderItem.forReference(style));
        response.render(CssReferenceHeaderItem.forReference(generatorStyle));
    }

}
