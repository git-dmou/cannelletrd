package fr.solunea.thaleia.plugins.cannelle.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import fr.solunea.thaleia.utils.DetailedException;

/**
 * Un bloc de cellules.
 * 
 */
public class CellsRange {

	/**
	 * La base des index pour les lignes et les colonnes de cette sélection.
	 * C'est à dire l'index de la cellule en haut et à gauche. Identique pour
	 * les lignes et les colonnes.
	 */
	public static final int INDEX_BASE = 0;

	List<Row> rows;

	/**
	 * Le bloc de cellules définit par cette liste de lignes.
	 * 
	 * @param rows
	 */
	public CellsRange(List<Row> rows) {

		if (rows == null) {
			this.rows = new ArrayList<Row>();
		} else {
			this.rows = rows;
		}
	}

	/**
	 * @return le nombre de lignes.
	 */
	public int getHeight() {
		return rows.size();
	}

	/**
	 * @return le nombre de colonnes de la ligne qui contient le plus de
	 *         colonnes.
	 */
	public int getWidth() {
		int maxColumns = 0;
		for (Row row : rows) {

			// row.getLastCellNum : short representing the last logical cell in
			// the row PLUS ONE, or -1 if the row does not contain any cells.
			int rowColumns = row.getLastCellNum() - 1;

			if (rowColumns > maxColumns) {
				maxColumns = rowColumns;
			}
		}
		return maxColumns;
	}

	@Override
	public String toString() {
		String result = CellsRange.class.getName() + " object:\n";
		int line = INDEX_BASE;
		for (Row row : rows) {
			if (row != null) {
				// Pour chaque ligne
				result = result + "line #" + line + " : ";
				// On parcourt toutes les cellules de cette ligne
				Iterator<Cell> rowIterator = row.cellIterator();
				while (rowIterator.hasNext()) {
					Cell cell = rowIterator.next();
					result = result + "| "
							+ XlsUtils.getStringValue(cell, "Vrai", "Faux")
							+ " | ";
				}
				// fin de ligne
				line++;
				result = result + "\n";
			}
		}
		// On supprime le dernier retour à la ligne
		result = result.substring(0, result.length() - 1);

		return result;
	}

	/**
	 * @param column
	 *            le numéro de colonne, la première étant à l'indice INDEX_BASE
	 * @param row
	 *            le numéro de ligne, la première étant à l'indice INDEX_BASE
	 * @return la cellule, en tant que chaîne de caractère.<br/>
	 * @throws DetailedException
	 *             si la cellule n'existe pas
	 */
	public Cell getCell(int column, int row) throws DetailedException {

		try {
			// On recherche la ligne
			// La liste des lignes commence à 0. Donc on ajoute INDEX_BASE selon
			// l'index de départ que l'on souhaite :
			// Liste ( A, B, C ...)
			// index java : 0, 1, 2 ...
			// index CellsRange : BASE, BASE+1, BASE+2....
			Row resultRow = rows.get(row + INDEX_BASE);

			// On recherche la cellule de cette ligne
			return resultRow.getCell(column + INDEX_BASE);

		} catch (Exception e) {
			throw new DetailedException(e)
					.addMessage("Impossible d'obtenir la cellule à la ligne "
							+ row + " et la colonne " + column + ".");
		}
	}

	public void createCell(int column, int row) {
		rows.get(row).createCell(column);
	}
}
