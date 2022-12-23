package fr.solunea.thaleia.plugins.cannelle.parsers;

import fr.solunea.thaleia.utils.DetailedException;

import java.util.Map;

/**
 * Un parser qui intéprète des données fournies en entrée pour produire les
 * propriétés de description d'un module.
 * 
 */
public interface IModuleParserService {

	/**
	 * @return Tous les couples clé / valeur retrouvés par le parser qui
	 *         décrivent les propriétés du module.
	 */
	public Map<String, String> getModuleProperties(String origLanguage, String targetLanguage) throws DetailedException;

}
