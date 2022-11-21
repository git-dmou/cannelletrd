package fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters;

import fr.solunea.thaleia.utils.DetailedException;

/**
 * Un paramètre décrivant le contenu d'une zone de texte à présenter : zone de
 * texte, titre, etc.
 * <p>
 * On considère qu'on peut écrire en HTML dans un TextParameter. Mais on ne
 * récupère pas la mise en forme Excel dans ce HTML.
 */
public class TextParameter extends AbstractScreenParameter {

    @Override
    public boolean isValueAHtmlText() {
        return true;
    }

    /**
     * Le nom du paramètre qui contient le nom de la propriété à laquelle il
     * faut donner la valeur qui est dans le commentaire de la cellule.
     */
    public static final String COMMENT_CONTENT_PROPERTY = "comment.contentproperty";

    private String commentPropertyName = "";
    private String commentPropertyValue = "";

    @Override
    public void isValid() throws DetailedException {

        // Vérification de la présence d'une valeur, même vide
        if (getValue() == null) {
            throw new DetailedException("La valeur du paramètre de type '" + this.getClass().getName() + "' n'a pas "
                    + "été initialisée !");
        }

    }

    @Override
    public String getValue() {
        // Les retours chariots ne seront pas stockés
        // correctement dans les écrans en JSON.
        if(value != null) {
            return value.replace("\n","<br/>");
        } else {
            return value;
        }
    }

    @Override
    public boolean valueIsAFileName() {
        return false;
    }

    /**
     * Le nom de la propriété associée au commentaire.
     */
    public void setCommentPropertyName(String value) {
        this.commentPropertyName = value;
    }

    /**
     * La valeur de la propriété associée au commentaire.
     */
    public void setCommentPropertyValue(String value) {
        this.commentPropertyValue = value;
    }

    /**
     * Le nom de la propriété associée au commentaire.
     */
    public String getCommentPropertyName() {
        return commentPropertyName;
    }

    /**
     * La valeur de la propriété associée au commentaire.
     */
    public String getCommentPropertyValue() {
        return commentPropertyValue;
    }

}