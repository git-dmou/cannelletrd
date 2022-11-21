package fr.solunea.thaleia.plugins.cannelle.xls.screens.generator;

import java.io.File;
import java.util.List;

import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.model.User;
import fr.solunea.thaleia.plugins.cannelle.contents.A7Content;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.IScreenParameter;
import fr.solunea.thaleia.utils.DetailedException;

/**
 * L'objet qui a la charge d'instancier un contenu d'après les paramètres
 * récupérés dans le fichier Excel.
 * 
 * 
 */
public interface IContentGenerator {

	/**
	 * Génère l'écran correspondant à ces paramètres dans le répertoire demandé.
	 * 
	 * @param screenParameters
	 * @param destination
	 *            le répertoire dans lequel seront placés les fichiers.
	 * @param locale
	 *            : la locale des écrans générés, pour que le générateur utilise
	 *            les modèles d'écran dans la bonne locale.
	 * @param user
     * @return l'écran qui correspond à ces paramètres
	 * @throws DetailedException
	 */
	A7Content generateFiles(List<IScreenParameter> screenParameters, File destination, Locale locale, User user) throws DetailedException;

	void setResourcesHandler(ResourcesHandler resources);

	ResourcesHandler getResourcesHandler() throws DetailedException;

	void setParameters(Parameters parameters);

	Parameters getParameters() throws DetailedException;

	/**
	 * @param string
	 *            Dans les paramètres de ce générateur de contenu, le préfixe
	 *            des clés qui le concernent. Par exemple, si les paramètres
	 *            contiennent la clé
	 *            'templates.1.contentgenerator.class=fr.solunea.Toto', et que
	 *            le contentGenerator a été initialisé dans le template1, alors
	 *            le préfixe est 'templates.1.contentgenerator.'. Ce préfixe
	 *            doit être fixé lors du parsing du fichier Excel.
	 */
	void setParametersPrefix(String string);

	/**
	 * @return Dans les paramètres de ce générateur de contenu, le préfixe des
	 *         clés qui le concernent. Par exemple, si les paramètres
	 *         contiennent la clé
	 *         'templates.1.contentgenerator.class=fr.solunea.Toto', et que le
	 *         contentGenerator a été initialisé dans le template1, alors le
	 *         préfixe est 'templates.1.contentgenerator.'. Ce préfixe doit être
	 *         fixé lors du parsing du fichier Excel.
	 */
	String getParametersPrefix();

}
