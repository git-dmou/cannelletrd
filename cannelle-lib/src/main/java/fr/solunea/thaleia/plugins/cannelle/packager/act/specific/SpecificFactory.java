package fr.solunea.thaleia.plugins.cannelle.packager.act.specific;

import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;

import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.plugins.cannelle.contents.ExportedModule;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.ThaleiaApplication;

public final class SpecificFactory {

	private static final Logger logger = Logger
			.getLogger(SpecificFactory.class);

	private static final SpecificFactory instance = new SpecificFactory();

	private SpecificFactory() {
	}

	public static SpecificFactory getInstance() {
		return instance;
	}

	/**
	 * @param className
	 *            le nom de la classe à instancier
	 * @param resourcesHandler
	 * @param module
	 * @param parameters
	 * @param specificParamsStartsWith
	 *            les paramètres destinés à cette instance de traitement
	 *            commencent tous par cette valeur (par exemple :
	 *            'format.act.container.specific.[n° d'ordre]'). Cette chaîne
	 *            sera utilisée par la classe de traitement pour récupérer ses
	 *            propres paramètres.
	 * @param locale
	 *            la locale à utiliser pour les traitements
	 * @return un objet implémentant ISpecificTreatment
	 * @throws DetailedException
	 */
	public ISpecificTreatment getSpecific(String className,
			ResourcesHandler resourcesHandler, ExportedModule module,
			Parameters parameters, String specificParamsStartsWith,
			Locale locale) throws DetailedException {

		if (className == null) {
			throw new DetailedException(
					"Le nom de la classe demandée est nul !");
		}
		if ("".equals(className)) {
			throw new DetailedException(
					"Le nom de la classe demandée est vide !");
		}

		logger.debug("Chargement de la classe '" + className + "'...");
		Class<?> containerClass;
		try {
			// containerClass =
			// Class.class.getClassLoader().loadClass(className);
			containerClass = ThaleiaApplication.get().getApplicationSettings()
					.getClassResolver().getClassLoader().loadClass(className);
		} catch (Exception e) {
			throw new DetailedException(e)
					.addMessage("Impossible de charger la classe '" + className
							+ "' : " + e.toString());
		}

		logger.debug("Instanciation de la classe '" + className + "'...");
		Object result;

		@SuppressWarnings("rawtypes")
		Constructor constructor;
		try {
			constructor = containerClass
					.getConstructor(new Class[] {
							Class.forName("fr.solunea.thaleia.plugins.canelle.lib.utils.ResourcesHandler"),
							Class.forName("fr.solunea.thaleia.plugins.canelle.lib.executeservice.exportation.format.model.Module"),
							Class.forName("fr.solunea.thaleia.plugins.canelle.lib.executeservice.Parameters"),
							Class.forName("java.lang.String"),
							Class.forName("fr.solunea.thaleia.model.Locale") });
		} catch (Exception e) {
			throw new DetailedException(e)
					.addMessage("Impossible d'obtenir le constructeur pour la classe '"
							+ className + "'.");
		}

		try {
			Object[] params = new Object[] { resourcesHandler, module,
					parameters, specificParamsStartsWith, locale };
			result = constructor.newInstance(params);
		} catch (Exception e) {
			throw new DetailedException(e)
					.addMessage("Impossible d'instancier la classe '"
							+ className + "'.");
		}

		if (result == null) {
			throw new DetailedException("L'objet '" + className
					+ "' instancié est nul !");
		}

		if (!(result instanceof ISpecificTreatment)) {
			throw new DetailedException("L'objet '" + className
					+ "' instancié n'hérite pas de l'interface '"
					+ ISpecificTreatment.class.getName() + "' !");
		}

		return (ISpecificTreatment) result;
	}

}
