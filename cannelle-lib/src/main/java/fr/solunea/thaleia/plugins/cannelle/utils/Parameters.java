/**
 * Le code source, le matériel préparatoire et la documentation de ce
 * logiciel sont la propriété exclusive de la société Solunea, au titre
 * du droit de propriété intellectuelle. Ces éléments ont fait l'objet
 * de dépôts probatoires.
 * <p>
 * À défaut d'accord préalable écrit de Solunea, vous ne devez pas
 * utiliser, copier, modifier, traduire, créer une œuvre dérivée,
 * transmettre, vendre ou distribuer, de manière directe ou indirecte,
 * inverser la conception ou l'assemblage ou tenter de trouver le code
 * source (sauf cas prévus par la loi), ou transférer tout droit relatif
 * audit logiciel.
 * <p>
 * Solunea
 * SARL - N° SIRET 48795234300027
 */
package fr.solunea.thaleia.plugins.cannelle.utils;

import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.utils.LogUtils;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

/**
 * Permet de charger des paramètres (couples clé/valeur) depuis un fichier de
 * propriétés, d'en ajouter, et de les parcourir.
 */
@SuppressWarnings("serial")
public class Parameters implements Serializable {

    private static final Logger logger = Logger.getLogger(Parameters.class);

    /**
     * Le nom de la propriété qui contient le nom du plugin
     */
    public static final String PLUGIN_NAME = "plugin.name";

    /**
     * Le nom de la propriété qui contient la description du plugin
     */
    public static final String PLUGIN_DESCRIPTION = "plugin.description";

    /**
     * Le nom de la propriété qui contient la version du plugin
     */
    public static final String PLUGIN_VERSION = "plugin.version";

    /**
     * L'attribut RandomPool pour définir le pool de question aléatoire
     */
    public static final String RANDOM_POOL = "randomPool";

    /**
     * Le nom de la locale anglaise.
     */
    public static final String LOCALE_EN = "en";

    /**
     * Le nom de la locale française.
     */
    public static final String LOCALE_FR = "fr";

    /**
     * Le nom de la locale espagnole.
     */
    public static final String LOCALE_ES = "es";

    /**
     * Le nom de la locale néerlandaise.
     */
    public static final String LOCALE_NL = "nl";

    /**
     * Le nom de la locale allemande.
     */
    public static final String LOCALE_DE = "de";

    /**
     * Le nom de la locale italienne.
     */
    public static final String LOCALE_IT = "it";

    /**
     * Le nom de la locale chinois simplifié.
     */
    public static final String LOCALE_ZH = "zh";

    /**
     * Le nom de la locale portugaise.
     */
    public static final String LOCALE_PT = "pt";

    /**
     * Le nom de la locale turque.
     */
    public static final String LOCALE_TR = "tr";

    /**
     * Le nom de la locale grecque.
     */
    public static final String LOCALE_EL = "el";

    /**
     * Le nom de la locale arabe.
     */
    public static final String LOCALE_AR = "ar";

    /**
     * Le nom de la locale hébreu.
     */
    public static final String LOCALE_HE = "he";

    /**
     * Le nom de la locale russe.
     */
    public static final String LOCALE_RU = "ru";

    /**
     * Le nom de la locale slovaque.
     */
    public static final String LOCALE_SK = "sk";

    /**
     * Le nom de la propriété qui contient le nom de la configuration à
     * utiliser. Dans le répertoire des ressources, il s'agira du nom du
     * répertoire dans lequel chercher les fichiers de traitement, sous le
     * répertoire /Resources/act_files/types.
     */
    public final static String PACKAGER_CONF_DIR = "export.format.configuration";

    /**
     * Le nom de la propriété qui contient le nom du répertoire, dans le
     * répertoire ressources du plugin, qui contient les fichiers de
     * configuration pour ce package. C'est dans ce répertoire que seront
     * recherchés automatiquement les fichiers sous la forme
     * [resources]/[format.
     * configuration]/types/[export.format.package.configuration]/[lang]/*
     */
    public static final String PACKAGER_CONF_PACKAGE_DIR = "export.format.package.configuration";

    /**
     * Le nom de la propriété qui contient la langue dans laquelle on veut
     * exporter les contenus.
     */
    public static final String EXPORT_LANG = "export.locale";

    /**
     * Le nom de la propriété qui contient le nom du format demandé pour le
     * paquet exporté.
     */
    public static final String EXPORT_FORMAT = "export.format";

    /**
     * Le nom de la propriété qui contient le lien (en relatif dans le
     * répertoire localisé des ressources) vers le modèle de fichier Act à
     * utiliser.
     */
    public static final String ACT_MODEL_FILENAME = "export.format.act.actmodel";

    /**
     * Le nom d'une propriété qui décrit la navigation à paramétrer dans le ACT.
     */
    public final static String FORMAT_ACT_UPDATE_NAVIGATION_PROGRESSION =
            "export.format.act.update.navigation" + ".storeprogression";
    /**
     * Le nom d'une propriété qui décrit la navigation à paramétrer dans le ACT.
     */
    public final static String FORMAT_ACT_UPDATE_NAVIGATION_SCORE = "export.format.act.update.navigation.scoreonce";

    /**
     * Le nom de la propriété qui indique s'il faut activer la communication
     * Scorm.
     */
    public static final String SCORM_COMMUNICATION = "export.format.act.scorm.communication";

    /**
     * Le nom de la propriété qui indique le nom de la classe de traitement
     * d'ajout des fichiers du conteneur (méta Html...).
     */
    public static final String FORMAT_ACT_CONTAINER_CLASS = "export.format.act.container.class";

    /**
     * Le début du nom des propriétés qui décrivent des traitements spécifiques.
     */
    public static final String FORMAT_ACT_SPECIFIC = "export.format.act.container.specific";

    /**
     * Le début du nom des propriétés qui décrivent des propriétés à placer dans
     * le ACT.
     */
    public static final String FORMAT_ACT_UPDATE_PROPERTY = "export.format.act.update.properties";

    /**
     * La nom du paramètre qui contient le nom du ContentType correspondant aux
     * modules que le plugin peut exporter.
     */
    public static final String CONTENT_TYPE_NAME_MODULE = "exportableModules.contentType.name";

    /**
     * La valeur de ce paramètre est : dans le fichier XLS, le nom de la feuille
     * qui est ouverte et analysée (module).
     */
    public static final String PARSED_SHEET_NAME_MODULE = "xls.parser.sheetname.module";

    /**
     * La valeur de ce paramètre est : dans le fichier XLS, le nom de la feuille
     * qui est ouverte et analysée (écrans).
     */
    public static final String PARSED_SHEET_NAME_CONTENTS = "xls.parser.sheetname.contents";

    /**
     * La valeur de ce paramètre est : dans le fichier XLS, le nom de la feuille
     * qui est ouverte et analysée (écrans).
     */
    public static final String PARSED_SHEET_NAME_RANDOM_SELECTION = "xls.parser.sheetname.randomselection";

    /**
     * La valeur de ce paramètre est : dans le fichier XLS, la position des
     * propriétés du module
     */
    public static final String MODULE_PROPERTIES_POSITION = "xls.parser.cellsrange.module.properties.position";

    /**
     * Le nom de la propriété qui contient le nom de la propriété dans le
     * fichier Excel qui décrit la langue des contenus de ce fichier.
     */
    public static final String XLS_CONTENT_LANGAGE_TITLE = "xls.parser.content.langage.title";

    /**
     * Le nom de la propriété qui contient le nom de la propriété dans le
     * fichier Excel qui décrit l'identifiant du module.
     */
    public static final String XLS_MODULE_IDENTIFIER_TITLE = "xls.parser.module.identifier";

    /**
     * Indique si l'identifiant du module doit être rendu unique
     * automatiquement.
     */
    public static final String XLS_MODULE_IDENTIFIER_TITLE_UNIQUE = "xls.parser.module.identifier.unique";

    /**
     * Le paramètre qui décrit quelle représentation textuelle doit-on associer
     * au booléen VRAI retrouvé dans le fichier Excel, par exemple dans une
     * proposition de réponse ?
     */
    public static final String EXCEL_BOOLEAN_TRUE_TEXT_VALUE = "excel.boolean.true";
    /**
     * Le paramètre qui décrit quelle représentation textuelle doit-on associer
     * au booléen FAUX retrouvé dans le fichier Excel, par exemple dans une
     * proposition de réponse ?
     */
    public static final String EXCEL_BOOLEAN_FALSE_TEXT_VALUE = "excel.boolean.false";

    /**
     * Dans le fichier properties, préfixe permettant d'identifier les
     * propriétés d'un ContentProperty de type module_cayenne
     */
    public static final String CONTENT_PROPERTY_MODULE = "content.module.";

    /**
     * suffixe permettant d'identifier le nom d'un ContentProperty
     */
    public static final String CONTENT_PROPERTY_NAME = "name";

    /**
     * préfixe permettant d'identifier les propriétés d'un ContentProperty de
     * type screen_cayenne
     */
    public static final String CONTENT_PROPERTY_SCREEN = "content.screen.";

    /**
     * La clé principale pour les paramètres de définition des options (score,
     * correction etc.)
     */
    public static final String OPTIONS = "xls.parser.options";

    /**
     * La clé principale pour les paramètres de définition d'un template.
     */
    public static final String TEMPLATES = "templates";

    /**
     * La valeur de ce paramètre est : dans le fichier XLS, la position du
     * premier bloc d'exercice
     */
    public static final String FIRST_TEMPLATE_POSITION = "xls.parser.cellsrange.first.template.position";

    /**
     * La valeur de ce paramètre est : Dans un bloc de cellules (les blocs sont
     * définis par les séparateurs xls.parser.separator.X), la position
     * (colonne) de la cellule qui contient le title de template à utiliser pour
     * ce bloc
     */
    public static final String XLS_PARSER_TEMPLATE_TITLE_COL = "xls.parser.cellsrange.template.title.position.col";
    /**
     * La valeur de ce paramètre est : Dans un bloc de cellules (les blocs sont
     * définis par les séparateurs xls.parser.separator.X), la position (ligne)
     * de la cellule qui contient le title de template à utiliser pour ce bloc
     */
    public static final String XLS_PARSER_TEMPLATE_TITLE_LINE = "xls.parser.cellsrange.template.title.position.line";

    /**
     * Le paramètre qui décrit les séparateurs de blocs de cellules dans la
     * feuille XLS.
     */
    public static final String XLS_PARSER_SEPARATORS = "xls.parser.separator";

    /**
     * La valeur de ce paramètre est : dans le fichier XLS modèle, le nom de la
     * feuille qui est ouverte et analysée
     */
    public static final String PARSED_SHEET_MODEL_NAME = "xls.model.function.sheet.name";

    /**
     * Le nom de la propriété qui contient le nom de la ContentProperty, qui est
     * associée au module, et qui décrit le titre du module.
     */
    public static final String MODULE_TITLE_PARAM_NAME = "content.module.contentproperties.title";

    /**
     * La clé principale pour les paramètres de définition de la fonction
     * replace
     */
    public static final String FUNCTION_REPLACE = "xls.model.function.replace";

    /**
     * La clé principale pour les paramètres de définition de la fonction
     * replace
     */
    public static final String FUNCTION_REPLACE_PAIR = "xls.model.function.replacePair";

    /**
     * La clé principale pour les paramètres de définition de la fonction
     * replace
     */
    public static final String FUNCTION_REPLACE_MIXED_PAIR = "xls.model.function.replaceMixedPair";

    /**
     * La clé principale pour les paramètres de définition de la fonction
     * replace
     */
    public static final String FUNCTION_REPLACE_URLORFILE_PAIR = "xls.model.function.replaceUrlOrFilePair";

    /**
     * La clé principale pour les paramètres de définition de la fonction de
     * définition des couples
     */
    public static final String FUNCTION_COUPLES = "xls.model.function.couples";

    /**
     * La clé principale pour les paramètres de définition de la fonction de
     * correction de la classification HTML
     */
    public static final String CLASSIFICATION_COUPLES = "xls.model.function.classificationCorrection";

    /**
     * Pour la fonction replace, l'option
     * "doit-on effectuer le remplacement si la valeur de remplacement et vide ?"
     */
    public static final String FUNCTION_REPLACE_RUN_IF_EMPTY_OPTION = "runIfReplaceValueIsVoid";

    /**
     * Le numéro de la première ligne à analyser dans le fichier Xls de
     * description des modèles d'écran.
     */
    public static final String FUNCTION_FIRSTLINE = "xls.model.function.firstline";

    /**
     * La clé principale pour les paramètres de définition de la fonction
     * replaceIf
     */
    public static final String FUNCTION_REPLACE_IF = "xls.model.function.replaceIf";

    /**
     * La clé principale pour les paramètres de définition de la fonction
     * replaceIfCorrection
     */
    public static final String FUNCTION_REPLACE_IF_CORRECTION = "xls.model.function.replaceIfCorrection";

    /**
     * La clé principale pour les paramètres de définition de la fonction
     * replaceAlways
     */
    public static final String FUNCTION_REPLACE_ALWAYS = "xls.model.function.replaceAlways";

    /**
     * Dans le fichier Xls de paramètres, le nom de la locale EN
     */
    public static final String LOCALE_EN_NAME_FR = "Anglais";

    /**
     * Dans le fichier Xls de paramètres, le nom de la locale EN
     */
    public static final String LOCALE_EN_NAME_EN = "English";

    /**
     * Dans le fichier Xls de paramètres, le nom de la locale FR
     */
    public static final String LOCALE_FR_NAME_FR = "Français";

    /**
     * Dans le fichier Xls de paramètres, le nom de la locale FR
     */
    public static final String LOCALE_FR_NAME_EN = "French";

    /**
     * Dans le fichier XlS de paramètres, le nom de la locale ES
     */
    public static final String LOCALE_ES_NAME_ES = "Español";

    /**
     * Dans le fichier XlS de paramètres, le nom de la locale NL
     */
    public static final String LOCALE_NL_NAME_NL = "Nederlands";

    /**
     * Dans le fichier XlS de paramètres, le nom de la locale DE
     */
    public static final String LOCALE_DE_NAME_DE = "Deutsch";

    /**
     * Dans le fichier XlS de paramètres, le nom de la locale IT
     */
    public static final String LOCALE_IT_NAME_IT = "Italiano";

    /**
     * Dans le fichier XlS de paramètres, le nom de la locale ZH
     */
    public static final String LOCALE_ZH_NAME_ZH = "简体中文";

    /**
     * Dans le fichier XlS de paramètres, le nom de la locale PT
     */
    public static final String LOCALE_PT_NAME_PT = "Português";

    /**
     * Dans le fichier XlS de paramètres, le nom de la locale TR
     */
    public static final String LOCALE_TR_NAME_TR = "Türk";

    /**
     * Dans le fichier XlS de paramètres, le nom de la locale EL
     */
    public static final String LOCALE_EL_NAME_EL = "Ελληνικά";

    /**
     * Dans le fichier XlS de paramètres, le nom de la locale AR
     */
    public static final String LOCALE_AR_NAME_AR = "عرب";

    /**
     * Dans le fichier XlS de paramètres, le nom de la locale HE
     */
    public static final String LOCALE_HE_NAME_HE = "עִברִית";

    /**
     * Dans le fichier XlS de paramètres, le nom de la locale RU
     */
    public static final String LOCALE_RU_NAME_RU = "русский";

    /**
     * Dans le fichier XlS de paramètres, le nom de la locale SK
     */
    public static final String LOCALE_SK_NAME_SK = "slovenský";

    /**
     * Le nom du paramètre dont la valeur est le nom de la propriété retrouvée
     * dans le fichier Excel (nom de la ContentProprty) qui contient
     * l'identifiant de l'écran.
     */
    public static final String SCREEN_PROPERTY_IDENTIFIER_KEY = "content.screen.contentProperty.identifier";

    /**
     * Le nom du paramètre dont la valeur est le nom de la ContentProperty dans
     * laquelle placer le nom du plugin utilisé pour la génération.
     */
    public static final String PLUGIN_NAME_PROPERTY = "content.properties.plugin.name";

    /**
     * Le nom du paramètre dont la valeur est le nom de la ContentProperty dans
     * laquelle placer le nom du plugin utilisé pour la génération.
     */
    public static final String PLUGIN_VERSION_PROPERTY = "content.properties.plugin.version";

    /**
     * Le nom du paramètre dont la valeur est la langue dans laquelle les écrans
     * sont générés. Cette valeur est le nom du répertoire sous ScreenFactory
     * dans lequel rechercher les modèles d'écran.
     */
    public static final String CONTENT_LANGUAGE = "content.language";

    /**
     * Le nom du paramètre dont la valeur est la classe d'implémentation de la
     * recherche des propriétés du module. Doit implémenter
     * fr.solunea.thaleia.plugins.cannelle.parsers.IModuleParserService
     */
    public static final String MODULE_PARSER_IMPLEMENTATION = "module.parser.implementation";

    /**
     * Le format des contenus à générer (A7/HTML)
     */
    public static final String CONTENT_FORMAT = "cannelle.content.format";
    public static final String CONTENT_FORMAT_HTML = "html";
    public static final String CONTENT_FORMAT_A7 = "a7";


    /**
     * Le nom du paramètre dont la valeur est la classe d'implémentation de la
     * recherche des propriétés des écrans. Doit implémenter
     * fr.solunea.thaleia.plugins.cannelle.parsers.IScreenParserService
     */
    public static final String SCREEN_RANDOM_PARSER_IMPLEMENTATION = "screen.random.parser.implementation";

    /**
     * Le nom du paramètre dont la valeur est la classe d'implémentation de la
     * recherche des propriétés des écrans. Doit implémenter
     * fr.solunea.thaleia.plugins.cannelle.parsers.IScreenParserService
     */
    public static final String SCREEN_PARSER_IMPLEMENTATION = "screen.parser.implementation";

    /**
     * Le nom du paramètre dont la valeur est la classe d'implémentation de la
     * recherche des propriétés des écrans pour la traduction du module . Doit implémenter
     * fr.solunea.thaleia.plugins.cannelle.parsers.IScreenParserService
     */
    public static final String SCREEN_PARSER_TRANSLATOR_IMPLEMENTATION = "screen.parser.translator.implementation";

    /**
     * Le nom du paramètre dont la valeur est la valeur de la cellule qui
     * identifie l'en-tête du tableau des ressources du module.
     */
    public static final String MODULE_RESOURCES_HEADER_POSITION = "xls.parser.content.resources.header";

    private final Properties properties = new Properties();

    /**
     * @return la valeur correspondante, ou "" si la valeur n'est pas définie
     */
    public String getValue(String key) {
        return properties.getProperty(key, "");
    }

    /**
     * Initialise les paramètres, d'après les paramètres par défaut du fichier
     * passé en paramètre, surdéfinis par les valeurs de la table passée en
     * paramètre.
     */
    public Parameters(InputStream is, Map<String, Object> params) throws Exception {
        // Chargement des paramètres par défaut (fichier properties)
        try {
            properties.load(is);
            is.close();
        } catch (Exception e) {
            String message = "Problème de chargement du fichier de propriétés :" + e.toString();
            logger.debug(message + "\n" + LogUtils.getStackTrace(e.getStackTrace()));
            throw e;
        }
        // logger.debug(properties.size()
        // + " propriétés chargées depuis le fichier !" + is.toString());

        if (params != null) {
            // Chargement des propriétés de la requête
            logger.debug("Chargement des propriétés complémentaires...");
            for (String key : params.keySet()) {
                properties.put(key, params.get(key));
            }
            logger.debug(properties.size() + " propriétés complémentaires ajoutées !");
            // Journalisation des propriétés retrouvées
            logger.debug("Propriétés courantes : " + this.toString());
        }
    }

    /**
     * Permet d'ajouter un paramètre supplémentaire (ou remplacer sa valeur s'il
     * existe déjà.
     */
    public void addParameter(String key, String value) {
        properties.put(key, value);
    }

    /**
     * Permet d'ajouter un ou plusieurs paramètres supplémentaires
     */
    public void addParameters(Map<String, Object> params) {
        // Chargement des propriétés de la requête
        logger.debug("Ajout de propriétés...");
        for (String key : params.keySet()) {
            properties.put(key, params.get(key));
        }
        logger.debug(properties.size() + " propriétés ajoutées !");

        // Journalisation des propriétés retrouvées
        logger.debug("Propriétés courantes : " + this.toString());
    }

    @Override
    public String toString() {
        String result = "";

        for (Object key : properties.keySet()) {
            String stringKey = (String) key;
            String value = getValue(stringKey);
            // On masque les valeurs des propriétés qui contiennent le mot
            // 'password' ou 'pwd'
            if (stringKey.toLowerCase(Locale.US).indexOf("password") > -1
                    || stringKey.toLowerCase(Locale.US).indexOf("pwd") > -1) {
                value = value.replaceAll(".", "*");

            }
            result = result + "\n'" + key + "' = '" + value + "'";
        }

        return result;
    }

    /**
     * @return toutes les clés qui commencent par la string passée en paramètre
     */
    public List<String> getKeysStartingWith(String string) {
        List<String> result = new ArrayList<String>();

        Enumeration<Object> keys = properties.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            if (key.startsWith(string)) {
                result.add(key);
            }
        }
        return result;
    }

    /**
     * @return l'entier le plus élevé pour la clé qui commence par 'key'. Par
     * exemple si le fichier properties contient<br/>
     * toto.tata.0.truc<br/>
     * toto.tata.0.titi<br/>
     * toto.tata.1.truc<br/>
     * toto.tata.2.toto.tutu<br/>
     * toto.tata.0.truc<br/>
     * Alors pour le paramètre 'key', on renvoie 2.<br/>
     * Attention : les entiers sont comptés à partir de 0, et remontés
     * un par un. Par exemple :<br/>
     * toto.tata.10.truc<br/>
     * toto.tata.4.titi<br/>
     * toto.tata.3.truc<br/>
     * => On renvoie une exception car les entiers ne commencent pas à 0
     * <br/>
     * toto.tata.0.truc<br/>
     * toto.tata.1.titi<br/>
     * toto.tata.3.truc<br/>
     * => On renvoie 1 car il n'y a pas de 2 défini avant 3.
     * @throws DetailedException si aucun entier n'a été trouvé à la suite de 'key'.
     */
    public int getMaxIndexForKey(String key) throws DetailedException {

        if (key == null) {
            throw new DetailedException("Impossible de rechercher les numéros d'ordre pour une clé nulle !");
        }

        boolean searchNextOrder = true;
        int result = -1;
        int order = 0;
        while (searchNextOrder) {
            // On recherche les clés
            // key.[order][...]
            String orderedKey = key + "." + order;
            List<String> keys = getKeysStartingWith(orderedKey);
            if (keys.isEmpty()) {
                // Rien de trouvé pour ce numéro d'ordre : on ne cherche pas
                // les
                // suivants
                searchNextOrder = false;
            } else {
                // Une clé existe pour le numéro d'ordre n°order
                // On cherchera le numéro d'ordre suivant
                result = order;
                order++;
            }
        }

        if (result == -1) {
            throw new DetailedException("La clé '" + key + "' n'a pas de numéro d'ordre.");
        }

        logger.debug("Numéro d'ordre max pour les propriétés '" + key + "' : " + result);
        return result;
    }

}
