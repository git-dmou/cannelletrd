package fr.solunea.thaleia.plugins.cannelle.v6.panels;

import fr.solunea.thaleia.plugins.cannelle.v6.ExcelTemplate;
import fr.solunea.thaleia.plugins.cannelle.v6.MainPage;
import fr.solunea.thaleia.plugins.welcomev6.utils.PanelUtils;
import fr.solunea.thaleia.webapp.ThaleiaApplication;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import org.apache.log4j.Logger;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;

public class ResourcesPanel extends Panel {

    protected static final Logger logger = Logger.getLogger(ResourcesPanel.class);

    private final String DOCUMENTATION_FR = ThaleiaApplication.get().getApplicationRootUrl() + "/doc/#/fr/general_documentation/quick_overview";
    private final String DOCUMENTATION_EN = ThaleiaApplication.get().getApplicationRootUrl() + "/doc/#/en/general_documentation/quick_overview";

    private static final String ZIP_MODEL_RANDOM_QUIZ_FR = "modele_excel_quiz_aleatoire_fr.zip";
    private static final String ZIP_MODEL_RANDOM_QUIZ_EN = "modele_excel_quiz_aleatoire_en.zip";
    private static final String ZIP_MODEL_PRODUCT_PRESENTATION_FR = "modele_excel_presentation_produit_fr.zip";
    private static final String ZIP_MODEL_PRODUCT_PRESENTATION_EN = "modele_excel_presentation_produit_en.zip";
    private static final String ZIP_MODEL_TUTORIAL_FR = "modele_excel_tutoriel_fr.zip";
    private static final String ZIP_MODEL_TUTORIAL_EN = "modele_excel_tutoriel_en.zip";
    private static final String ZIP_MODEL_VIRTUAL_CLASSROOM_BASICS_FR = "modele_excel_classe_virtuelle_fr.zip";
    private static final String ZIP_MODEL_VIRTUAL_CLASSROOM_BASICS_EN = "modele_excel_classe_virtuelle_en.zip";

    private static final String QUIZ_URL_FR = "https://www.solunea.net/thaleia/QuizFR/index.html";
    private static final String QUIZ_URL_EN = "https://www.solunea.net/thaleia/QuizEN/index.html";
    private static final String TUTORIAL_URL_FR = "https://www.solunea.net/thaleia/Tuto_Gif_FR/index.html";
    private static final String TUTORIAL_URL_EN = "https://www.solunea.net/thaleia/Tuto_Gifs_EN/index.html";
    private static final String PRODUCT_PRESENTATION_FR = "https://www.solunea.net/thaleia/preview_presentation_produit_fr/";
    private static final String PRODUCT_PRESENTATION_EN = "https://www.solunea.net/thaleia/PrezProdEN/index.html";
    private static final String VIRTUAL_CLASSROOM_BASICS_FR = "https://www.solunea.net/thaleia/les_basiques_de_la_classe_virtuelle_fr/index.html";
    private static final String VIRTUAL_CLASSROOM_BASICS_EN = "https://www.solunea.net/thaleia/les_basiques_de_la_classe_virtuelle_en/index.html";

    public ResourcesPanel(String id) {
        super(id);

        addGetDefaultXls();
        addGetHelpFile();

        // Liens vers les zip mod??les excels.
        addGetRandomQuizModel();
        addGetProductPresentationModel();
        addGetTutorialModel();
        addGetVirtualClassroomBasics();

        // Liens vers les modules de pr??sentation.
        addLinkToQuizz();
        addLinkToTutorial();
        addLinkToProductPresentation();
        addLinkToVirtualClassroomBasics();
    }

    /**
     * Ajout du t??l??chargement du fichier excel par d??faut.
     */
    private void addGetDefaultXls() {
        // Mod??le du fichier Excel mod??le ?? t??l??charger (FR, EN, FR-ACCESSIBLE...)
        IModel<ExcelTemplate> excelTemplateModel = Model.of(ExcelTemplate.getDefault());

        // El??ment de t??l??chargement du mod??le Excel par d??faut.
        MarkupContainer getDefaultXls = new MarkupContainer("getDefaultXls") {
        };
        add(getDefaultXls);

        // Bouton de t??l??chargement du mod??le Excel, dans la locale actuellement s??lectionn??e.
        MarkupContainer getDefaultXlsBtn = PanelUtils.getDownloadLink("getDefaultXlsBtn", new Model<>() {
            @Override
            public String getObject() {
                return excelTemplateModel.getObject().getFilename();
            }
        });
        getDefaultXls.add(getDefaultXlsBtn);

        // S??lecteur qui permet de choisir le type de mod??le Excel ?? t??l??charger.
        final Label currentSelectedTemplate = new Label("currentSelectedTemplate", new Model<String>() {
            @Override
            public String getObject() {
                return excelTemplateModel.getObject().getShortName();
            }
        });
        getDefaultXls.add(currentSelectedTemplate.setOutputMarkupId(true));

        final ListView<ExcelTemplate> excelTemplatesList = new ListView<>("excelSelectorRow",
                ExcelTemplate.listAll()) {
            @Override
            public void populateItem(final ListItem<ExcelTemplate> item) {
                final AjaxLink<?> lnk = new AjaxLink<>("excelSelectorLink", Model.of(item
                        .getModelObject())) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        // On stocke le nouveau mod??le de template dans le mod??le
                        excelTemplateModel.setObject(item.getModelObject());
                        target.add(currentSelectedTemplate);
                    }
                };
                lnk.add(new AttributeAppender("onclick", "$(\"#dropdownExcelTypeSelectorMenu\").removeClass(\"show\")"));
                lnk.add(new Label("excelSelectorLabel", new Model<String>() {
                    @Override
                    public String getObject() {
                        return item.getModelObject().getName();
                    }
                }));
                item.add(lnk.setOutputMarkupId(true));
            }
        };
        getDefaultXls.add(excelTemplatesList.setOutputMarkupId(true));

        // Illustration du panneau "T??l??charger un mod??le de cr??ation vierge
        getDefaultXls.add(new Image("illustrationNewCreationModel", new PackageResourceReference(MainPage.class, "img/illustrationNewCreationModel.svg")));
    }

    /**
     * Ajout du t??l??chargement du mod??le de Quiz al??atoire
     */
    private void addGetRandomQuizModel() {
        add(PanelUtils.getDownloadLink("getRandomQuizModel", new Model<>() {
            @Override
            public String getObject() {
                // par d??faut, on renvoie le fichier FR
                String filename = ZIP_MODEL_RANDOM_QUIZ_FR;
                // Si l'IHM de Thaleia est en anglais
                try {
                    if (ThaleiaSession.get().getLocale().equals(java.util.Locale.ENGLISH)) {
                        filename = ZIP_MODEL_RANDOM_QUIZ_EN;
                    }
                } catch (Exception e) {
                    // Si la session avait expir??, on ??vite une NPE sur l'appel
                    // de localeModel.getObject().getTitleInManifest()
                }
                return filename;
            }
        }));
    }

    /**
     * Ajout du t??l??chargement de la Pr??sentation produit.
     * Pour l'instant, il n'existe qu'une version FR.
     */
    private void addGetProductPresentationModel() {
        // Le bouton de t??l??chargement de l'exemple Zip, dans la langue d'import des contenus
        add(PanelUtils.getDownloadLink("getProductPresentationModel", new Model<>() {
            @Override
            public String getObject() {
                // par d??faut, on renvoie le fichier FR
                String filename = ZIP_MODEL_PRODUCT_PRESENTATION_FR;
                // Si l'IHM de Thaleia est en anglais
                try {
                    if (ThaleiaSession.get().getLocale().equals(java.util.Locale.ENGLISH)) {
                        filename = ZIP_MODEL_PRODUCT_PRESENTATION_EN;
                    }
                } catch (Exception e) {
                    // Si la session avait expir??, on ??vite une NPE sur l'appel
                    // de localeModel.getObject().getTitleInManifest()
                }
                return filename;
            }
        }));
    }

    /**
     * Ajout du t??l??chargement du Tutoriel.
     */
    private void addGetTutorialModel() {
        // Le bouton de t??l??chargement de l'exemple Zip, dans la langue d'import des contenus
        add(PanelUtils.getDownloadLink("getTutorialModel", new Model<>() {
            @Override
            public String getObject() {
                // par d??faut, on renvoie le fichier FR
                String filename = ZIP_MODEL_TUTORIAL_FR;
                // Si l'IHM de Thaleia est en anglais
                try {
                    if (ThaleiaSession.get().getLocale().equals(java.util.Locale.ENGLISH)) {
                        filename = ZIP_MODEL_TUTORIAL_EN;
                    }
                } catch (Exception e) {
                    // Si la session avait expir??, on ??vite une NPE sur l'appel
                    // de localeModel.getObject().getTitleInManifest()
                }
                return filename;
            }
        }));
    }

    /**
     * Ajout du t??l??chargement des Basiques de la classe virtuelle.
     */
    private void addGetVirtualClassroomBasics() {
        // Le bouton de t??l??chargement de l'exemple Zip, dans la langue d'import des contenus
        add(PanelUtils.getDownloadLink("getVirtualClassroomBasicsModel", new Model<>() {
            @Override
            public String getObject() {
                // par d??faut, on renvoie le fichier FR
                String filename = ZIP_MODEL_VIRTUAL_CLASSROOM_BASICS_FR;
                // Si l'IHM de Thaleia est en anglais
                try {
                    if (ThaleiaSession.get().getLocale().equals(java.util.Locale.ENGLISH)) {
                        filename = ZIP_MODEL_VIRTUAL_CLASSROOM_BASICS_EN;
                    }
                } catch (Exception e) {
                    // Si la session avait expir??, on ??vite une NPE sur l'appel
                    // de localeModel.getObject().getTitleInManifest()
                }
                return filename;
            }
        }));
    }

    /**
     * Ajout du lien vers la documentation.
     */
    private void addGetHelpFile() {
        ExternalLink link = new ExternalLink("getHelpFile", DOCUMENTATION_FR);
        try {
            if (ThaleiaSession.get().getLocale().equals(java.util.Locale.ENGLISH)) {
                link = new ExternalLink("getHelpFile", DOCUMENTATION_EN);
            }
        } catch (Exception e) {
            // Si la session avait expir??, on ??vite une NPE sur l'appel
            // de localeModel.getObject().getTitleInManifest()
        }
        add(link);
    }

    private void addLinkToQuizz() {
        ExternalLink link = new ExternalLink("linkToQuiz", QUIZ_URL_FR);
        try {
            if (ThaleiaSession.get().getLocale().equals(java.util.Locale.ENGLISH)) {
                link = new ExternalLink("linkToQuiz", QUIZ_URL_EN);
            }
        } catch (Exception e) {
            // Si la session avait expir??, on ??vite une NPE sur l'appel
            // de localeModel.getObject().getTitleInManifest()
        }
        add(link);
    }

    private void addLinkToTutorial() {
        ExternalLink link = new ExternalLink("linkToTutorial", TUTORIAL_URL_FR);
        try {
            if (ThaleiaSession.get().getLocale().equals(java.util.Locale.ENGLISH)) {
                link = new ExternalLink("linkToTutorial", TUTORIAL_URL_EN);
            }
        } catch (Exception e) {
            // Si la session avait expir??, on ??vite une NPE sur l'appel
            // de localeModel.getObject().getTitleInManifest()
        }
        add(link);
    }

    private void addLinkToVirtualClassroomBasics() {

        ExternalLink link = new ExternalLink("linkToVirtualClassroomBasics", VIRTUAL_CLASSROOM_BASICS_FR);
        try {
            if (ThaleiaSession.get().getLocale().equals(java.util.Locale.ENGLISH)) {
                link = new ExternalLink("linkToVirtualClassroomBasics", VIRTUAL_CLASSROOM_BASICS_EN);
            }
        } catch (Exception e) {
            // Si la session avait expir??, on ??vite une NPE sur l'appel
            // de localeModel.getObject().getTitleInManifest()
        }
        add(link);
    }

    private void addLinkToProductPresentation() {

        ExternalLink link = new ExternalLink("linkToProductPresentation", PRODUCT_PRESENTATION_FR);
        try {
            if (ThaleiaSession.get().getLocale().equals(java.util.Locale.ENGLISH)) {
                link = new ExternalLink("linkToProductPresentation", PRODUCT_PRESENTATION_EN);
            }
        } catch (Exception e) {
            // Si la session avait expir??, on ??vite une NPE sur l'appel
            // de localeModel.getObject().getTitleInManifest()
        }
        add(link);
    }


}
