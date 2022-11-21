package fr.solunea.thaleia.plugins.cannelle.utils;

import java.util.HashMap;
import java.util.Map;

public class ModuleResources {

	/**
	 * Dans la conf du plugin, le plus petit indice des ressources de module de
	 * type fichier, définies par Resource[INDEX]Name
	 */
	public static final int FILE_RESOURCE_MIN_INDEX = 1;
	/**
	 * Dans la conf du plugin, le plus grand indice des ressources de module de
	 * type fichier, définies par Resource[INDEX]Name
	 */
	public static final int FILE_RESOURCE_MAX_INDEX = 6;
	/**
	 * Dans la conf du plugin, le plus petit indice des ressources de module de
	 * type URL, définies par Resource[INDEX]Name
	 */
	public static final int URL_RESOURCE_MIN_INDEX = 7;
	/**
	 * Dans la conf du plugin, le plus grand indice des ressources de module de
	 * type URL, définies par Resource[INDEX]Name
	 */
	public static final int URL_RESOURCE_MAX_INDEX = 12;

	/**
	 * @return La table des noms (non localisés) de ContentProperty
	 *         (ContentProperty.name) associées à une resource de module de type
	 *         fichier, pour lesquelles la clé = Resource[X]Name, et la valeur
	 *         Resource[X]File
	 */
	public static Map<String, String> getFileResourcesContentPropertiesNames() {
		Map<String, String> result = new HashMap<String, String>();

		for (int i = FILE_RESOURCE_MIN_INDEX; i <= FILE_RESOURCE_MAX_INDEX; i++) {
			String resourceXName = "Resource" + i + "Name";
			String resourceXFile = "Resource" + i + "File";
			result.put(resourceXName, resourceXFile);
		}

		return result;
	}

	/**
	 * @return La table des noms (non localisés) de ContentProperty
	 *         (ContentProperty.name) associées à une resource de module de type
	 *         URL, pour lesquelles la clé = Resource[X]Name, et la valeur
	 *         Resource[X]URL
	 */
	public static Map<String, String> getURLResourcesContentPropertiesNames() {
		Map<String, String> result = new HashMap<String, String>();

		for (int i = URL_RESOURCE_MIN_INDEX; i <= URL_RESOURCE_MAX_INDEX; i++) {
			String resourceXName = "Resource" + i + "Name";
			String resourceXFile = "Resource" + i + "URL";
			result.put(resourceXName, resourceXFile);
		}

		return result;
	}

}
