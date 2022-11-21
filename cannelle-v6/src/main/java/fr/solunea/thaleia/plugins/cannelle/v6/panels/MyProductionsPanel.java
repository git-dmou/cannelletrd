package fr.solunea.thaleia.plugins.cannelle.v6.panels;

import fr.solunea.thaleia.model.Content;
import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.model.dao.ContentDao;
import fr.solunea.thaleia.model.dao.LocaleDao;
import fr.solunea.thaleia.model.dao.UserDao;
import fr.solunea.thaleia.plugins.cannelle.v6.MainPage;
import fr.solunea.thaleia.plugins.welcomev6.panels.PluginNavigationMenu;
import fr.solunea.thaleia.webapp.panels.ThaleiaFeedbackPanel;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import org.apache.log4j.Logger;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.Calendar;
import java.util.List;

public class MyProductionsPanel extends Panel {

    protected static final Logger logger = Logger.getLogger(MyProductionsPanel.class);

    private final PluginNavigationMenu pluginNavigationMenu;
    private final ThaleiaFeedbackPanel feedbackPanel;

    private boolean isContentsToShow;
    private long dureeAffichageListe = 0;

    public MyProductionsPanel(String id, PluginNavigationMenu pluginNavigationMenu, ThaleiaFeedbackPanel feedbackPanel) {
        super(id);
        this.pluginNavigationMenu = pluginNavigationMenu;
        this.feedbackPanel = feedbackPanel;
        this.isContentsToShow = isContentsToShow();
        addGoToCreationPage();
        addContentsList();
    }


    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        dureeAffichageListe = Calendar.getInstance().getTimeInMillis();
    }

    @Override
    protected void onAfterRender() {
        super.onAfterRender();
        dureeAffichageListe = Calendar.getInstance().getTimeInMillis() - dureeAffichageListe;
        logger.debug("************** FIN render MyProductions, duree = " + dureeAffichageListe + " ms ************************");
    }

    /**
     * Si l'utilisateur n'a créé aucun contenu le lien vers la page de création sera affiché     *
     */
    private void addGoToCreationPage() {
        MarkupContainer noContentPanel = new MarkupContainer("noContentPanel") {

            @Override
            protected void onConfigure() {
                super.onConfigure();
                this.setVisible(!isContentsToShow);
            }
        };
        add(noContentPanel);

        noContentPanel.add(new AjaxLink<>("goToCreationPage") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                pluginNavigationMenu.clickOnLink(target, PluginNavigationMenu.Link.create);
            }
        });

        noContentPanel.add(new Image("illustrationProduction", new PackageResourceReference(
                MainPage.class,"img/illustrationProduction.svg")));
    }

    private boolean isContentsToShow() {
        System.out.println("!!!!!!!!!!!!!!!!!! MyProductionsPanel.java, isContentsToShow() executed !!!!!!!!!!!!!!!!!!!!!!!!!!!");

        UserDao userDao = new UserDao(ThaleiaSession.get().getContextService().getNewContext());
        ContentDao contentDao = new ContentDao(ThaleiaSession.get().getContextService().getNewContext());

        List<String> domainsPK = userDao.getDomainsIdWithRightsOn(ThaleiaSession.get().getAuthenticatedUser());
        List<String> contentsPK = contentDao.getContentsIdForDomainsPk(domainsPK);
        return !contentsPK.isEmpty();
    }

    /**
     * Ajoute la liste des contenus créés.
     */
    private void addContentsList() {
        // On stocke la locale dans un modèle détachable, par défaut la
        // locale stockée en session = la dernière sélectionnée.
        // C'est la locale sélectionnée pour l'import, et pour le
        // téléchargement du modèle Excel.
        IModel<Locale> contentsLocaleModel = new Model<>() {
            @Override
            public Locale getObject() {
                // logger.debug("On renvoie la locale du dernier contenu édité : " + locale);
                return new LocaleDao(ThaleiaSession.get().getContextService().getContextSingleton())
                        .get(ThaleiaSession.get().getLastContentLocale().getObjectId());
            }

            @Override
            public void setObject(Locale object) {
                super.setObject(object);
                // logger.debug("Nouvelle locale de la page : " + object);
                ThaleiaSession.get().setLastContentLocale(object);
            }
        };

        // Le tableau des contenus existants dans la locale sélectionnée.
        CannelleContentsPanel cannelleContentsPanel = (CannelleContentsPanel) new CannelleContentsPanel(
                "modules", contentsLocaleModel, feedbackPanel) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                this.setVisible(!isContentsToShow);
            }

            @Override
            protected void onEditProperties(AjaxRequestTarget target, IModel<Content> content, IModel<Locale> locale) {
                try {
                    pluginNavigationMenu.clickOnEditModuleLink(target, content, locale);
                } catch (Exception e) {
                    logger.warn("Erreur d'ouverture de la page d'édition des propriétés. Est-ce que le plugin" +
                            " publish est installé ?", e);
                }
            }
        }.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
        add(cannelleContentsPanel);
    }

}
