package fr.solunea.thaleia.plugins.cannelle.utils;

import org.apache.poi.hssf.record.FontRecord;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Font;

public class XlsFontStyle {

	private String a7Color = "";
	private long fontSizeRatio;
	private int xlsSize;
	private HSSFColor color;
	private String align;
	private String fontName;
	private boolean bold;
	private boolean italic;
	private boolean underline;

	/**
	 * @param defaultFontStyle
	 * @param hssfFont
	 * @param workbook
	 * @return le FontStyle correspondant aux informations de style de hssfFont.
	 *         Les valeurs par défaut sont celles de defaultFontStyle.
	 */
	public static XlsFontStyle getFontStyle(XlsFontStyle defaultFontStyle,
			HSSFFont hssfFont, HSSFWorkbook workbook) {
		XlsFontStyle result = new XlsFontStyle(
				defaultFontStyle.getFontSizeRatio(),
				defaultFontStyle.getXlsSize(), defaultFontStyle.getColor(),
				defaultFontStyle.getAlign(), defaultFontStyle.getFontName(),
				defaultFontStyle.isBold(), defaultFontStyle.isItalic(),
				defaultFontStyle.isUnderline());

		if (hssfFont != null) {
			String fontName = hssfFont.getFontName();
			result.setFontName(fontName);
		}

		// gras ?
		if (hssfFont != null) {
			if (hssfFont.getBold()) {
				result.setBold(true);
			}
		}

		// italique ?
		if (hssfFont != null) {
			if (hssfFont.getItalic()) {
				result.setItalic(true);
			}
		}

		// souligné ?
		if (hssfFont != null) {
			// On traite toutes les manières de sousligner de la même façon.
			if (hssfFont.getUnderline() != FontRecord.U_NONE) {
				result.setUnderline(true);
			}
		}

		// taille
		if (hssfFont != null) {
			// La taille qui est récupérée par font.getFontHeight()
			// est la taille renseignée dans le fichier XLS x 20 !
			int xlsSize = hssfFont.getFontHeight() / 20;
			result.setXlsSize(xlsSize);
		}

		// Couleur
		if (hssfFont != null) {
			// l'index de la couleur dans la palette
			short colorIndex = hssfFont.getColor();
			// La couleur correspondante
			HSSFColor color = workbook.getCustomPalette().getColor(colorIndex);
			if (color != null) {
				result.setColor(color);
			} else {
				// couleur par défaut
				// c'est le cas pour la "couleur automatique" du XLS
				result.setA7Color(defaultFontStyle.getA7Color());
				// fontStyle.setColor(defaultFontStyle.getColor());
			}
		}

		return result;
	}

	private void setUnderline(boolean b) {
		this.underline = true;
	}

	private void setItalic(boolean b) {
		this.italic = true;
	}

	public String toString() {
		String result = "FontStyle : \n";
		if (a7Color != null) {
			result = result + "  a7Color=" + a7Color;
		} else {
			result = result + "  a7Color nul !";
		}
		result = result + "  fontSizeRatio=" + fontSizeRatio;
		result = result + "  xlsSize=" + xlsSize;
		result = result + "  getA7Size()=" + getA7Size();
		if (color != null) {
			result = result + "  color=" + color.toString();
		} else {
			result = result + "  color nul !";
		}
		result = result + "  getA7Color()=" + getA7Color();
		if (align != null) {
			result = result + "  align=" + align;
		} else {
			result = result + "  align nul !";
		}
		if (fontName != null) {
			result = result + "  fontName=" + fontName;
		} else {
			result = result + "  fontName nul !";
		}
		result = result + "  bold=" + bold;

		return result;
	}

	/**
	 * @param fontSizeRatio
	 *            Le rapport entre la taille dans le A7 et la taille
	 *            correspondante dans le XLS Par exemple, 25/10 signifie qu'à
	 *            une taille de 10 dans le XLS on fait correspondre une taille
	 *            de 25 dans le A7.
	 * @param xlsSize
	 *            la taille, exprimée dans le fichier XLS
	 * @param color
	 *            la couleur dans le XLS. ATTENTION : si l'objet passé en
	 *            paramètre est nul, alors il faut passer par setA7Color pour
	 *            que getA7Color puisse renvoyer une valeur. Si la couleur
	 *            HSSFColor est nulle et que setA7Color n'a pas été appelé,
	 *            alors get A7Color renverra nul.
	 * @param align
	 *            "LEFT", "RIGHT", "CENTER", "JUSTIFY"
	 * @param fontName
	 * @param bold
	 */
	XlsFontStyle(long fontSizeRatio, int xlsSize, HSSFColor color,
			String align, String fontName, boolean bold, boolean italic,
			boolean underline) {
		this.fontSizeRatio = fontSizeRatio;
		this.xlsSize = xlsSize;
		this.color = color;
		this.align = align;
		this.fontName = fontName;
		this.bold = bold;
		this.italic = italic;
		this.underline = underline;
	}

	public void setXlsSize(int xlsSize) {
		this.xlsSize = xlsSize;
	}

	public void setColor(HSSFColor color) {
		this.color = color;
	}

	/**
	 * @param color
	 *            Cette valeur sera perdue si la couleur a été fixée par le
	 *            constructeur ou par setColor().
	 */
	public void setA7Color(String color) {
		this.a7Color = color;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public int getXlsSize() {
		return xlsSize;
	}

	/**
	 * @return la taille à appliquer dans le A7 pour la police, en prenant en
	 *         compte le ratio d'échelle entre une taille XLS (10 par défaut) et
	 *         une taille A7 (25 par défaut par exemple).
	 */
	public String getA7Size() {
		return (new Integer(new Long(xlsSize * fontSizeRatio).intValue()))
				.toString();
	}

	/**
	 * @return la couleur, au format A7 : "#RRGGBB".<br/>
	 *         Si la couleur HSSFColor a été fixée (par constructeur ou par
	 *         setColor), alors c'est cette valeur qui est renvoyée. SINON,
	 *         c'est la valeur fixée par setA7Color.
	 */
	public String getA7Color() {

		if (color != null) {
			// On utilise la couleur HSSFColor

			String rgb = "#";
			short[] triplet = color.getTriplet();
			for (int j = 0; j < triplet.length; j++) {
				// Hex.asHex(triplet[j]) renvoie 'xc0' : on enlève le x
				String hex = asHex(triplet[j]);
				// On onlève le x
				hex = hex.substring(2, hex.length());
				// Si un seul caractère, on ajoute 0 devant
				if (hex.length() == 1) {
					hex = "0" + hex;
				}
				rgb = rgb + hex;
			}
			return rgb;

		} else {
			// On utilise la couleur A7Color
			return a7Color;
		}
	}

	private String asHex(long value) {
		return "0x" + Long.toHexString(value);
	}

	public HSSFColor getColor() {
		return color;
	}

	public String getAlign() {
		return align;
	}

	public String getFontName() {
		return fontName;
	}

	public boolean isBold() {
		return bold;
	}

	public boolean isItalic() {
		return italic;
	}

	public boolean isUnderline() {
		return underline;
	}

	public long getFontSizeRatio() {
		return fontSizeRatio;
	}

}
