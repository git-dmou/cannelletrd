package fr.solunea.thaleia.plugins.cannelle.v6;

import fr.solunea.thaleia.model.ContentVersion;
import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.plugins.cannelle.utils.CannellePreviewHelper;
import fr.solunea.thaleia.plugins.cannelle.v6.utils.ExportUtils;
import fr.solunea.thaleia.plugins.welcomev6.messages.LocalizedMessages;
import fr.solunea.thaleia.plugins.welcomev6.preview.PreviewPage;
import fr.solunea.thaleia.service.ContentService;
import fr.solunea.thaleia.service.utils.IPreviewHelper;
import fr.solunea.thaleia.service.utils.export.ExportFormat;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.utils.LogUtils;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.IModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@AuthorizeInstantiation("user")
public class CanellePreviewPage extends PreviewPage {

    /**
     * @param showMenuActions doit-on présenter les actions du menu sur cette page ?
     */
    public CanellePreviewPage(final ContentVersion module, final IModel<Locale> locale, boolean showMenuActions) {
        super(module, locale, showMenuActions);
    }

    /**
     * @param locale la locale dans laquelle il faut présenter le module.
     */
    public CanellePreviewPage(final ContentVersion module, final IModel<Locale> locale) {
        super(module, locale);
    }

    @Override
    protected File prepareFile() throws DetailedException {
        List<String> errorMessagesKeys = new ArrayList<>();

        File result;
        try {
            // On veut exporter le module dans la locale de sa dernière version fabriquée
            ContentService contentService = ThaleiaSession.get().getContentService();
            Locale lastVersionLocale =
                    contentService.getVersionLocale(((ContentVersion) CanellePreviewPage.this.getDefaultModelObject()));
            if (lastVersionLocale == null) {
                throw new DetailedException("Impossible de trouver la locale pour laquelle un fichier source existe.");
            }

            result = ExportUtils.exportModule(((ContentVersion) CanellePreviewPage.this.getDefaultModelObject()),
                    lastVersionLocale, errorMessagesKeys, ExportFormat.PREVIEW,
                    ThaleiaSession.get().getAuthenticatedUser());

        } catch (Exception e) {
            logger.debug(LogUtils.getStackTrace(e.getStackTrace()));

            // On présente les erreurs transmises par le
            // traitement
            for (String key : errorMessagesKeys) {
                error(LocalizedMessages.getMessage(key));
            }

            throw new DetailedException(e).addMessage("Impossible de préparer le fichier à prévisualiser Cannellev5.");
        }

        return result;
    }

    @Override
    protected IPreviewHelper getPreviewHelper() {
        return new CannellePreviewHelper();
    }

}
