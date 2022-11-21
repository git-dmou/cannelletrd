package fr.solunea.thaleia.plugins.cannelle.v6.panels;

import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.plugins.cannelle.v6.CannelleActionOnContents;
import fr.solunea.thaleia.plugins.welcomev6.contents.ActionsOnContent;
import fr.solunea.thaleia.plugins.welcomev6.contents.ContentsPanel;
import fr.solunea.thaleia.webapp.panels.ThaleiaFeedbackPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

public abstract class CannelleContentsPanel extends ContentsPanel {

    public static final String MODULE_CONTENT_TYPE_NAME = "module_cannelle";

    /**
     * @param locale la locale dans laquelle présenter les valeurs des propriétés des modules.
     */
    public CannelleContentsPanel(String id, final IModel<Locale> locale, ThaleiaFeedbackPanel feedbackPanel) {
        super(id, locale, feedbackPanel);
    }

    @Override
    protected int getItemsPerPage() {
        return 20;
    }

    @Override
    protected String getPresentedContentTypeName() {
        return MODULE_CONTENT_TYPE_NAME;
    }

    @Override
    protected ActionsOnContent getActionsOnContent(IModel<?> modelToRefreshAfterDelete) {
        return CannelleActionOnContents.getActionsOnContent(modelToRefreshAfterDelete, getPage());
    }

    @Override
    protected boolean showEditLink() {
        return false;
    }

    @Override
    protected String getContentPropertyNameThatMustExists() {
        return "SCORMCommunication";
    }

    @Override
    protected void onSelectedLocaleChanged(AjaxRequestTarget target) {
        // Mise à jour des contenus présentés dans le tableau : on ne veut que les contenus de la
        // locale sélectionnée.
        detachContentsModel();
    }

    // ne pas remplacer par onConfigure() : fait disparaitre la liste !
    @Override
    public boolean isVisible() {
        return true;
    }

}
