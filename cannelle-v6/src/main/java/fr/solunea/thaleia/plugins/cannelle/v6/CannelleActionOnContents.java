package fr.solunea.thaleia.plugins.cannelle.v6;

import fr.solunea.thaleia.model.*;
import fr.solunea.thaleia.model.dao.ContentPropertyDao;
import fr.solunea.thaleia.model.dao.ContentPropertyValueDao;
import fr.solunea.thaleia.plugins.cannelle.v6.utils.ExportUtils;
import fr.solunea.thaleia.plugins.welcomev6.contents.ActionsOnContent;
import fr.solunea.thaleia.plugins.welcomev6.utils.ContentTypeParser;
import fr.solunea.thaleia.service.utils.Configuration;
import fr.solunea.thaleia.service.utils.export.ExportFormat;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.ThaleiaApplication;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Ici on implémente les actions spécifiques à Cayenne pour les traitements
 * d'export, de publication, etc. qui concernent les contenus Cayenne.
 */
public class CannelleActionOnContents implements Serializable {

    private static final String ANALYSE_PLUGIN_MAINPAGE = "fr.solunea.thaleia.plugins.analyze.pages.MainStatsPage";

    /**
     * Renvoie l'objet qui contient : 1 - Les actions spécifiques à Cayenne pour les traitements d'export, de
     * publication, etc. qui concernent les contenus Cayenne. 2 - Les actions communes à tous les contenus.
     *
     * @param modelToRefreshAfterDelete après la suppression d'un contenu, quel modèle doit-on détacher pour mettre à
     *                                  jour le tableau ou la liste utilisée pour présenter les contenus sur la page ?
     * @param page                      la page, pour permettre les setResponsePage.
     */
    public static ActionsOnContent getActionsOnContent(IModel<?> modelToRefreshAfterDelete, Page page) {
        return new ActionsOnContent() {

            @Override
            protected File getFileForExport(IModel<ContentVersion> version, IModel<Locale> locale,
                                            List<String> errorMessageKeys, ExportFormat format) throws DetailedException {
                return ExportUtils.exportModule(version.getObject(), locale.getObject(), errorMessageKeys, format,
                        ThaleiaSession.get().getAuthenticatedUser());
            }

            @Override
            protected boolean isPreviewable(IModel<ContentVersion> contentVersion, IModel<Locale> locale) {
                // On ne le présente pas si le format du contenu est EXE.
                // On ne le présente que s'il existe une version à ce contenu
                // On ne présente pas de contenu si pas de source à la version : c'est le cas d'une version qui a été
                // générée dans une autre langue.
                return contentVersion.getObject() != null
                        && !ContentTypeParser.isExeContent(contentVersion.getObject(), locale.getObject())
                        && sourceAvailable(contentVersion, locale);
            }

            @Override
            public void launchExportPage(IModel<Content> content, IModel<Locale> locale, Page page) {
                page.setResponsePage(new CannelleDownloadPage(content.getObject(), locale, false, page));
            }

            @Override
            public void onPreview(IModel<ContentVersion> contentVersion, IModel<Locale> locale) {
                page.setResponsePage(new CanellePreviewPage(contentVersion.getObject(), locale, false));
            }

            @Override
            public void onAnalyze(IModel<Content> content) {
                //                this.setResponsePage(ANALYSE_PLUGIN_MAINPAGE);
                // On instancie la page d'analyse du plugin de publication
                try {
                    Class<?> analysePageClass;
                    analysePageClass = Class.forName(ANALYSE_PLUGIN_MAINPAGE, true,
                            ThaleiaSession.get().getPluginService().getClassLoader());
                    Page analyzePage = (Page) analysePageClass.getDeclaredConstructor().newInstance();
                    page.setResponsePage(analyzePage);
                } catch (Exception e) {
                    logger.error("Impossible d'instancier la page d'analyse du plugin Publish !");
                    page.setResponsePage(ThaleiaApplication.get().getRedirectionPage(Configuration.AUTHENTIFIED_USERS_WELCOME_PAGE, Configuration.DEFAULT_AUTHENTIFIED_USERS_WELCOME_PAGE, Configuration.HOME_MOUNT_POINT));
                }
            }

            @Override
            public void refreshModelAfterDelete() {
                modelToRefreshAfterDelete.detach();
            }

            @Override
            public void onEdit(AjaxRequestTarget target, IModel<Content> content, IModel<Locale> locale) {
                logger.info("Pas d'éditeur web !");
            }

            @Override
            public File getSourceFile(IModel<ContentVersion> version, IModel<Locale> locale) {

                // Le fichier de ce ContentPropertyValue
                ContentPropertyValue sourcePropertyValue = getSourcePropertyValue(version, locale);
                if (sourcePropertyValue == null) {
                    return null;
                }

                File file = new ContentPropertyValueDao(ThaleiaApplication.get().getConfiguration().getLocalDataDir().getAbsolutePath(),
                        ThaleiaApplication.get().getConfiguration().getBinaryPropertyType(), ThaleiaSession.get().getContextService().getContextSingleton()).getFile(sourcePropertyValue);

                if (file == null || !file.exists()) {
                    logger.warn("Le fichier de cette propriété n'a pas été trouvé : " + sourcePropertyValue);
                    return null;
                } else {
                    return file;
                }
            }

            @Override
            public boolean sourceAvailable(IModel<ContentVersion> version, IModel<Locale> locale) {
                return getSourcePropertyValue(version, locale) != null;
            }

            @Override
            public String getContentTitle(ContentVersion contentVersion, Locale locale) {
                // Il y a peut-être plusieurs propriétés nommées "Title". On cherche
                // celle qui concerne ce type de contenu.
                try {
                    ContentPropertyValue result =
                            ThaleiaSession.get().getContentService().getContentPropertyValue(contentVersion, "Title",
                                    locale);
                    if (result == null) {
                        return "";
                    } else {
                        return result.getValue();
                    }

                } catch (Exception e) {
                    logger.warn(e);
                    return "";
                }
            }

            /**
             * La propriété qui pointe sur le fichier source de cette version, et cette locale.
             * @return null si pas de source.
             */
            private ContentPropertyValue getSourcePropertyValue(IModel<ContentVersion> version, IModel<Locale> locale) {
                ContentProperty uploadedFileProperty = new ContentPropertyDao(version.getObject().getObjectContext()).findByName(
                        "SourceFile");
                // On récupère la valeur de cette propriété (pour cette
                // propriété, de ce module, pour cette locale)
                List<ContentPropertyValue> uploadedFilePropertyValues = new ContentPropertyValueDao(ThaleiaApplication.get().getConfiguration().getLocalDataDir().getAbsolutePath(),
                ThaleiaApplication.get().getConfiguration().getBinaryPropertyType(),version.getObject().getObjectContext()).find(version.getObject(), uploadedFileProperty, locale.getObject());
                if (uploadedFilePropertyValues.isEmpty()) {
                    return null;
                } else {
                    // On renvoie la 1ère valeur trouvée
                    return uploadedFilePropertyValues.get(0);
                }
            }

        };
    }
}
