package fr.solunea.thaleia.plugins.cannelle.parsers;

import java.util.Map;

import fr.solunea.thaleia.utils.DetailedException;

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
	public Map<String, String> getModuleProperties() throws DetailedException;

}
