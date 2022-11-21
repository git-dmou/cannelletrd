package fr.solunea.thaleia.plugins.cannelle.utils;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.RichTextString;

public class XlsUtils {

    private static final Logger logger = Logger.getLogger(XlsUtils.class);

    /**
     * @param trueStringValue  la valeur String à associer à une cellule contenant le booléen Vrai
     * @param falseStringValue la valeur String à associer à une cellule contenant le booléen Faux
     */
    public static String getStringValue(Cell cell, String trueStringValue, String falseStringValue) {

        if (cell == null) {
            logger.debug("Cellule nulle : on renvoie ''.");
            return "";
        }

        CellType cellType = cell.getCellType();

        if (cellType == CellType.FORMULA) {
            // Si formule, on interprète le résultat selon son type.
            // logger.debug("La cellule " + cell + " est une formule : '"
            // + cell.getCellFormula() + "'.");

            CellType formulaResultType = cell.getCachedFormulaResultType();
            // logger.debug("cell.getCachedFormulaResultType()="
            // + cell.getCachedFormulaResultType());

            String result = getStringValue(formulaResultType, cell, trueStringValue, falseStringValue);
            logger.debug("La cellule " + cell + " est une formule : '" + cell.getCellFormula() + "' Valeur = '" + result
                    + "'");

            return result;

        } else {
            // On renvoie le résultat
            return getStringValue(cellType, cell, trueStringValue, falseStringValue);
        }
    }

    private static String getStringValue(CellType cellType, Cell cell, String trueStringValue, String falseStringValue) {

        if (cellType == CellType.BLANK) {
            return "";

        } else if (cellType == CellType.ERROR) {
            logger.debug("L'interprétation de la formule de la cellule est en erreur : " + cell);
            return "";

        } else if (cellType == CellType.STRING) {
            return cell.getStringCellValue();

        } else if (cellType == CellType.NUMERIC) {
            Double doubleValue = cell.getNumericCellValue();
            return String.valueOf(doubleValue.intValue());

        } else if (cellType == CellType.BOOLEAN) {
            if (cell.getBooleanCellValue()) {
                return trueStringValue;
            } else {
                return falseStringValue;
            }

        } else {
            return "";
        }
    }

    public static String getStringComment(Cell cell) {
        Comment comment = cell.getCellComment();
        if (comment != null) {
            RichTextString richTextString = comment.getString();

            if (richTextString != null) {
                logger.debug("Commentaire retrouvé : '" + richTextString.getString() + "'");

                String result = richTextString.getString();
                if (result.indexOf('\n') != -1) {
                    // On ignore la première ligne, car c'est le nom de l'auteur
                    // du commentaire.
                    result = result.substring(result.indexOf('\n'));
                }
                return result;
            }
        }
        return "";

    }

    /**
     * @return la valeur du contenu textuel de la cellule, formatté en Html
     */
    public static String getHtmlFormattedContent(Cell cell, String trueStringValue, String falseStringValue) {
        try {
            StringBuilder result;

            // La police par défaut
            // TODO mettre en conf

            // 20 / 10 : On veut par défaut une taille de 20 dans la page html
            // pour une taille de 10 dans le fichier Excel

            // Avec cette ligne, on formatte par défaut en Arial 10
            //XlsFontStyle defaultFontStyle = new XlsFontStyle((20 / 10), 10, null, "LEFT", "Arial", false, false,
            // false);

            // Avec cete ligne, on ne fixe si la fonte, ni la taille
            XlsFontStyle defaultFontStyle = new XlsFontStyle((20
                    / 10), 10, null, "LEFT", "'rubik', Helvetica", false, false, false);

            // #666666 = RGB 102,102,102
            defaultFontStyle.setA7Color("#666666");

            logger.debug("Format par défaut demandé : " + defaultFontStyle.toString());

            HSSFWorkbook workbook = (HSSFWorkbook) cell.getSheet().getWorkbook();

            // Récupération du style de la cellule : ignoré
            //            short cellFontIndex = cell.getCellStyle().getFontIndex();
            //            HSSFFont cellFont = workbook.getFontAt(cellFontIndex);
            //            XlsFontStyle cellFontStyle = XlsFontStyle.getFontStyle(defaultFontStyle, cellFont, workbook);

            // On considère qu'on récupère un object HSSFRichTextString
            HSSFRichTextString rtString = (HSSFRichTextString) cell.getRichStringCellValue();

            // Le contenu non formatté
            String unformated = rtString.getString();
            logger.debug("Texte non formaté récupéré : '" + unformated + "'");
            if (unformated.length() == 0) {
                // On n'ajoute pas de formattage pour un texte vide.
                return "";
            }

            // On récupère le texte qui se trouve avant le premier formatting
            // run
            int endIndex;
            int formattingRuns = 0;
            try {
                endIndex = rtString.getIndexOfFormattingRun(0);
                formattingRuns = rtString.numFormattingRuns();
                result = new StringBuilder(unformated.substring(0, endIndex));
            } catch (Exception e) {
                // S'il n'y a qu'un seul run, une exception est levée.
                // On donne donc le style par défaut à tout le run.
                result = new StringBuilder(unformated);
            }

            // On applique le style de la cellule à ce premier run (problème : on surdéfinit le style défini pour les
            // contenus dans les CSS de la méta Thaleia.
            //            result = formatHtmlContent(result, cellFontStyle.getAlign(), cellFontStyle.getA7Size(),
            // cellFontStyle
            //                    .getA7Color(), cellFontStyle.getFontName(), cellFontStyle.isBold(), cellFontStyle
            // .isItalic(),
            //                    cellFontStyle.isUnderline());

            // On n'applique pas la personnalisation au premier run, afin d'utiliser les CSS par défaut des écrans
            result = new StringBuilder(formatSimpleHtmlContent(result.toString()));

            logger.info("Texte formaté pour le premier run : '" + result + "'");

            // On parcourt tous les formatting runs
            for (int i = 0; i < formattingRuns; i++) {
                // formatting run i
                logger.info("Run n°" + i + " sur " + (formattingRuns - 1));

                int startIndex = rtString.getIndexOfFormattingRun(i);
                logger.info("startIndex=" + startIndex);

                // fin de ce formating run = debut du prochain - 1
                if (i < (formattingRuns - 1)) {
                    // il existe
                    endIndex = rtString.getIndexOfFormattingRun(i + 1);
                } else {
                    // c'est le dernier run : on va jusqu'à la fin
                    endIndex = unformated.length();
                }
                logger.info("endIndex=" + endIndex);

                // le texte non formaté de ce run
                String unformatedRun = unformated.substring(startIndex, endIndex);
                logger.info("Texte non formaté du run n°" + i + " : '" + unformatedRun + "'");

                // Récupération du détail du format : nom de la police
                HSSFFont font = workbook.getFontAt(rtString.getFontOfFormattingRun(i));

                // Le format du run
                XlsFontStyle fontStyle = XlsFontStyle.getFontStyle(defaultFontStyle, font, workbook);

                // Application du style au run
                String formatedText = formatHtmlContent(unformatedRun, fontStyle.getAlign(), fontStyle.getA7Size(),
                        fontStyle.getA7Color(), fontStyle.getFontName(), fontStyle.isBold(), fontStyle.isUnderline(),
                        fontStyle.isItalic());
                result.append(formatedText);
                logger.info("Format demandé pour ce run : " + fontStyle.toString());
                logger.info("Texte formaté du run n°" + i + " : '" + formatedText + "'");

            }

            return result.toString();

        } catch (Exception e) {
            logger.info("Le contenu de la cellule n'a pas pu être interprété avec son formattage : " + e.toString());
            logger.info("Récupération du texte sans formattage.");
            return getStringValue(cell, trueStringValue, falseStringValue);
        }
    }

    /**
     * Enrobe le texte d'un format HTML, dans la balise demandée
     *
     * @param content La valeur du texte
     * @param align   "LEFT" "RIGHT" "CENTER"
     * @param size    entre 8 et ...
     * @param color   Ex "#ffffff"
     * @param font    "Arial", ...
     */
    private static String formatHtmlContent(String content, String align, String size, String color, String font,
                                            boolean bold, boolean underline, boolean italic) {
        // Les retours à la ligne
        String result = content.replace("\n", "<br/>");

        // Le style CSS
        String style = "";
        style = style + " font-family:" + font + ";";
        style = style + " font-size:" + size + "px;";
        style = style + " color:" + color + ";";
        style = style + " text-align:" + align + ";";
        if (bold) {
            style = style + " font-weight:bold;";
        }
        if (underline) {
            style = style + " text-decoration:underline;";
        }
        if (italic) {
            style = style + " font-style:italic;";
        }

        // La balise span
        result = "<span style=\"" + style + "\">" + result + "</span>";
        return result;
    }

    private static String formatSimpleHtmlContent(String content) {
        // Les retours à la ligne
        String result = content.replace("\n", "<br/>");

        // pas de style !

        // La balise span
        result = "<span>" + result + "</span>";
        return result;
    }

}
