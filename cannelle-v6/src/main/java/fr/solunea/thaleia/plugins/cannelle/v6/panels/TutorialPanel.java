package fr.solunea.thaleia.plugins.cannelle.v6.panels;

import fr.solunea.thaleia.plugins.welcomev6.panels.PluginNavigationMenu;
import fr.solunea.thaleia.plugins.welcomev6.utils.PanelUtils;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;

public class TutorialPanel extends Panel {

    private static final String ZIP_MODEL_FR = "Thaleia_XL_demo.xls";
    private static final String ZIP_MODEL_EN = "Thaleia_XL_demo_EN.xls";

    public TutorialPanel(String id, PluginNavigationMenu pluginNavigationMenu) {
        super(id);

        add(new Image("img-help-1", new PackageResourceReference(TutorialPanel.class,
                "../img" + "/help-2.png")));
        add(new Image("img-help-2", new PackageResourceReference(TutorialPanel.class,
                "../img" + "/help-1.png")));
        add(new Image("img-help-3", new PackageResourceReference(TutorialPanel.class,
                "../img" + "/help-3.png")));

        // Bouton de téléchargement du modèle Excel, dans la locale actuellement sélectionnée.
        MarkupContainer getDefaultXls = PanelUtils.getDownloadLink("getDefaultXls", new Model<>() {
            @Override
            public String getObject() {
                // par défaut, on renvoie le fichier FR
                String filename = ZIP_MODEL_FR;
                // Si l'IHM de Thaleia est en anglais
                try {
                    if (ThaleiaSession.get().getLocale().equals(java.util.Locale.ENGLISH)) {
                        filename = ZIP_MODEL_EN;
                    }
                } catch (Exception e) {
                    // Si la session avait expiré, on évite une NPE sur l'appel
                    // de localeModel.getObject().getTitleInManifest()
                }
                return filename;
            }
        });
        add(getDefaultXls);

        // Bouton "J'ai des questions" redirigeant sur la page "Ressources".
        add(new AjaxLink<Void>("goToResources") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                pluginNavigationMenu.clickOnLink(target, PluginNavigationMenu.Link.resources);
            }
        });
    }
}
