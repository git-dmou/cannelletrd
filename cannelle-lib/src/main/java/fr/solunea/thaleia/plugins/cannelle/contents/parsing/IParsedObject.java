package fr.solunea.thaleia.plugins.cannelle.contents.parsing;

/**
 * Un objet qui a été construit d'après des informations récoltées dans un
 * fichier (Excel, PowerPoint, ...).
 * 
 */
public interface IParsedObject<T> {

	/**
	 * @return l'objet construit d'après les informations.
	 */
	T getObject();

	/**
	 * @return L'endroit dans le fichier original où a été trouvé l'objet. Par
	 *         exemple, le numéro de ligne, le nom de la feuille, etc.
	 */
	String getOriginalLocation();

}
