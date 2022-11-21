package fr.solunea.thaleia.plugins.cannelle.v6;

import fr.solunea.thaleia.plugins.cannelle.v6.panels.MainPanel;
import fr.solunea.thaleia.webapp.pages.ThaleiaV6MenuPage;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.StringResourceModel;

@AuthorizeInstantiation("user")
@AuthorizeAction(action = Action.RENDER, roles = {"user"})
public class MainPage extends ThaleiaV6MenuPage {

    public MainPage() {
        super();
        MainPanel content = new MainPanel("content");
        content.add(new AttributeModifier("lang", ThaleiaSession.get().getLocale().toString()));
        add(content);
    }

    public MainPage(String panel) {
        super();
        MainPanel content = new MainPanel("content", panel);
        content.add(new AttributeModifier("lang", ThaleiaSession.get().getLocale().toString()));
        add(content);
    }

    /**
     * Définition de la classe à ajouter à la navbar.
     */
    @Override
    protected void setNavbarClass() {
        navbarClass = "thaleia-xl";
    }

    /**
     * Surcharge du bouton "home" pour avoir "Thaleia XL"
     * @return Label
     */
    @Override
    protected Label getHomeButtonLabel() {

        return (Label) new Label("homeLinkLabel",
                new StringResourceModel("homeLinkLabel", this, null))
                .setEscapeModelStrings(false);
    }


}
