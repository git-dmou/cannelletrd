package fr.solunea.thaleia.plugins.cannelle.v6;

import fr.solunea.thaleia.model.Content;
import fr.solunea.thaleia.model.ContentVersion;
import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.model.dao.ContentDao;
import fr.solunea.thaleia.model.dao.LocaleDao;
import fr.solunea.thaleia.plugins.cannelle.v6.utils.ExportUtils;
import fr.solunea.thaleia.plugins.welcomev6.download.DownloadPageWithNavigation;
import fr.solunea.thaleia.plugins.welcomev6.messages.LocalizedMessages;
import fr.solunea.thaleia.service.utils.export.ExportFormat;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import org.apache.wicket.Page;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class CannelleDownloadPage extends DownloadPageWithNavigation {

    protected IModel<Locale> locale;

    private final int contentId;
    private final int localeId;

    /**
     * @param content         le contenu dont on va exporter la dernière version
     * @param locale          la locale dans laquelle exporter le contenu.
     * @param showMenuActions doit-on présenter les actions du menu sur cette page ?
     * @param backPage        la destination pour le lien de retour présenté avec le bouton de téléchargement.
     */
    CannelleDownloadPage(final Content content, final IModel<Locale> locale, boolean showMenuActions, Page backPage) {
        super(showMenuActions, backPage);

        ContentDao contentDao = new ContentDao(content.getObjectContext());
        contentId = contentDao.getPK(content);
        localeId = new LocaleDao(locale.getObject().getObjectContext()).getPK(locale.getObject());

        this.locale = new LoadableDetachableModel<>() {
            @Override
            protected Locale load() {
                return new LocaleDao(locale.getObject().getObjectContext()).get(localeId);
            }
        };

        // On remplace les modèles par des versions LoadableDetachable pour
        // éviter les problèmes d'objets Cayenne qui deviennent Hollow si on
        // joue avec la navigation dans la pages par l'historique du client web
        setDefaultModel(new LoadableDetachableModel<ContentVersion>() {
            @Override
            protected ContentVersion load() {
                ContentVersion result = contentDao.get(contentId).getLastVersion();
                logger.debug("Chargement de la dernière version du contenu " + contentId + " : " + result);
                return result;
            }
        });
    }

    @Override
    protected File prepareFile() throws DetailedException {

        List<String> errorMessagesKeys = new ArrayList<>();

        File result;
        try {
            result = ExportUtils.exportModule(((ContentVersion) CannelleDownloadPage.this.getDefaultModelObject()),
                    locale.getObject(), errorMessagesKeys, ExportFormat.USER_DEFINED,
                    ThaleiaSession.get().getAuthenticatedUser());

        } catch (DetailedException e) {
            // On présente les erreurs transmises par le
            // traitement
            for (String key : errorMessagesKeys) {
                error(LocalizedMessages.getMessage(key));
            }
            throw e.addMessage("Impossible de préparer le fichier de l'export Cannellev5.");
        }

        return result;
    }

    @Override
    protected String getFileName() {
        return ((ContentVersion) CannelleDownloadPage.this.getDefaultModelObject()).getContentIdentifier();
    }

}
