package fr.solunea.thaleia.plugins.cannelle.xls.screens.screenparser;

import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.generator.IContentGenerator;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.Dictionary;

public abstract class AbstractExcelTemplate implements IExcelTemplate {

	/**
	 * Racine des paramètres du modèle pour ce template.
	 * Sera utilisé pour renseigner les screenParamers de l'objet CannelleScreenParameters
	 */
	private String paramsKey;

	private IContentGenerator contentGenerator;
	private Dictionary dictionary;
	private Parameters parameters;

	/**
	 * Définition de la racine des paramètres du modèle pour ce template.
	 * @param paramsKey Racine des paramètres du modèle pour ce template.
	 */
	public void setParamsKey(String paramsKey) {
		this.paramsKey = paramsKey;
	}

	/**
	 * Retourne la racine des paramètres du modèle pour ce template.
	 * @return Racine des paramètres du modèle pour ce template
	 */
	public String getParamsKey() {
		return paramsKey;
	}

	protected Parameters getParameters() {
		return parameters;
	}

	@Override
	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}

	@Override
	public IContentGenerator getContentGenerator() {
		return contentGenerator;
	}

	@Override
	public void setContentGenerator(IContentGenerator contentGenerator) {
		this.contentGenerator = contentGenerator;
	}


	@Override
	public Dictionary getDictionary() {
		return dictionary;
	}

	@Override
	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

	@Override
	public String toString() {
		return "AbstractExcelTemplate{" +
				"paramsKey='" + paramsKey + '\'' +
				", contentGenerator=" + contentGenerator +
				", dictionary=" + dictionary +
				", parameters=" + parameters +
				'}';
	}
}
