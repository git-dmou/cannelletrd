package fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters;

import fr.solunea.thaleia.plugins.cannelle.v6.uctranslatemodule.ITranslatable;
import fr.solunea.thaleia.utils.DetailedException;

import java.util.Properties;

public interface IScreenParameter extends ITranslatable {

    /**
     * @param properties les propriétés qui concernent ce ScreenParameter : Par exemple, si les paramètres définissent
     *                   :
     *                   <p>
     *                   <pre>
     *                                                templates.1.params.1.type = fr.solunea.solucms.executeservice
     *                                                .xls.vanilla
     *                                                .parameters.TextParameter
     *                                                templates.1.params.1.name=Énoncé
     *                                                templates.1.params.1.value.offset.line=0
     *                                                </pre>
     *                   <p>
     *                   alors ce ScreenParameter doit avoir comme properties :
     *                   <p>
     *                   <pre>
     *                                                type=fr.solunea.solucms.executeservice.xls.vanilla.parameters
     *                                                .TextParameter
     *                                                name=Énoncé idInFile=@@question@@
     *                                                value.offset.col=1
     *                                                value.offset.line=0
     *                                                </pre>
     */
    void setProperties(Properties properties) throws DetailedException;

    /**
     * @return la valeur de cette propriété.
     */
    String getProperty(String key, String defaultValue);

    /**
     * @throws DetailedException si la valeur de ce paramètre n'est pas valide; Les conditions de validité dépendent du
     *                           paramètre
     */
    void isValid() throws DetailedException;

    String getValue();

    void setValue(String parameterValue);

    default void setSafeKey(String safeKey) {}

    default String getSafeKey() {
        return "";
    }

    /**
     * @return true si la valeur de ce paramètre correspond au nom d'un fichier qui se trouve dans les fichiers
     * importés.
     */
    boolean valueIsAFileName();

    /**
     * @return true si la valeur de ce paramètre correspond à du contenu HTML.
     */
    boolean isValueAHtmlText();

    /**
     * @return le nom de la contentProperty associée à ce paramètre d'écran, ou null si aucun nom ne lui est associé.
     */
    String getContentPropertyName();

    /**
     * @return doit-on analyser le formattage du texte, pour le traduire en balisage HTML (par exemple, écrire le code
     * HTML qui correspond à la mise en forme du contenu de la cellule Excel).
     */
    boolean parseTextFormatInHTML();

    String toString();

}