package fr.solunea.thaleia.plugins.cannelle.contents.parsing;

import fr.solunea.thaleia.plugins.cannelle.utils.CellsRange;

/**
 * Une zone de cellules, récupérées à un endroit d'un fichier Excel.
 *
 */
public class ExcelDefinition {
	
	public CellsRange getCellsRange() {
		return cellsRange;
	}

	public String getLocation() {
		return location;
	}

	private CellsRange cellsRange;
	private String location;

	public ExcelDefinition(CellsRange cellsRange, String location) {
		this.cellsRange = cellsRange;
		this.location = location;
	}

}
