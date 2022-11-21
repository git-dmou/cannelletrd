package fr.solunea.thaleia.plugins.cannelle.contents;

import fr.solunea.thaleia.utils.DetailedException;

import java.util.Map;

public interface IContent {

	/**
	 * Ajoute cette propriété (ou remplace la valeur existante pour cette clé).
	 * 
	 * @param key
	 * @param value
	 */
	void addProperty(String key, String value);

	/***
	 * Ajoute ces propriétés (ou remplace les valeurs existantes pour ces clés).
	 * 
	 * @param properties
	 */
	void addProperties(Map<String, String> properties);

	/**
	 * @param key
	 * @param defaultValue
	 * @return la valeur de cette propriété
	 */
	String getProperty(String key, String defaultValue);

	/**
	 * @return toutes les propriétés de cet objet.
	 */
	Map<String, String> getProperties();

	/**
	 * @param parameters
	 * @return l'identifiant unique qui a été attribué à ce contenu.
	 * @throws DetailedException
	 */
	String getIdentifier();
	
	/**
	 * @param id l'identifiant unique qui à attribuer à ce contenu.
	 * @throws Exception 
	 */
	void setIdentifier(String id) throws DetailedException;
}
