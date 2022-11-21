package fr.solunea.thaleia.plugins.cannelle.xls.screens.screenparser;

import fr.solunea.thaleia.plugins.cannelle.utils.CellsRange;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.generator.IContentGenerator;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.Dictionary;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.IScreenParameter;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.StaticParameter;
import fr.solunea.thaleia.utils.DetailedException;

import java.util.List;

/**
 * Un IExcelTemplate interprète un fichier Excel, afin d'y retrouver les
 * paramètres qui permettent de définir un écran de contenu.
 * 
 * Pour l'implémentation, plutôt que d'implémenter cette interface, il faut
 * hériter de ExcelTemplate afin de bénéficier d'implémentations génériques.
 * 
 * @author RMAR
 * 
 */
public interface IExcelTemplate {

	/**
	 * Définition de la racine des paramètres du modèle pour ce template.
	 * @param paramsKey Racine des paramètres du modèle pour ce template.
	 */
	void setParamsKey(String paramsKey);

	/**
	 * Retourne la racine des paramètres du modèle pour ce template.
	 * @return Racine des paramètres du modèle pour ce template.
	 */
	String getParamsKey();

	/**
	 * @return l'objet de génération de contenu associé à ce template.
	 * @throws DetailedException
	 */
	IContentGenerator getContentGenerator() throws DetailedException;

	/**
	 * Fixe l'objet de génération de contenu associé à ce template.
	 * 
	 * @param contentGenerator
	 */
	void setContentGenerator(IContentGenerator contentGenerator);

	/**
	 * @param cells
	 * @return Les paramètres de création des écrans retrouvés dans les cellules
	 * @throws DetailedException
	 */
	List<IScreenParameter> parseScreenParameters(CellsRange cells, CannelleScreenParameters cannelleScreenParameters)
			throws DetailedException;

	/**
	 * Ajoute ce dictionnaire d'options au template
	 * 
	 * @param dictionary
	 *            : contient toutes les options disponibles dans le template
	 *            ainsi que les valeurs correspondantes Par exemple, pour
	 *            l'options Score actif, l'identifiant est 'score.active', et
	 *            l'option 'Oui' correspond à la valeur '1'
	 */
	void setDictionary(Dictionary dictionary);

	/**
	 * Récupère le dictionnaire d'options du template
	 * 
	 * @return Le dictionnaire qui contient toutes les options disponibles dans
	 *         le template ainsi que les valeurs correspondantes Par exemple,
	 *         pour l'options Score actif, l'identifiant est 'score.active', et
	 *         l'option 'Oui' correspond à la valeur '1'
	 */
	Dictionary getDictionary();

	void setParameters(Parameters parameters);

}
