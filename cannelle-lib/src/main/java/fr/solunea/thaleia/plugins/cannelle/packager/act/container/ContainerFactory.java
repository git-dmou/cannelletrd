package fr.solunea.thaleia.plugins.cannelle.packager.act.container;

import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;

import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.ThaleiaApplication;

/**
 * Produit les objets de type Container, en fonction de leur nom de classe, et
 * en leur fournissant le bon accès aux ressources du plugin.
 * 
 * @author RMAR
 * 
 */
public final class ContainerFactory {

	private static final Logger logger = Logger
			.getLogger(ContainerFactory.class);

	private static final ContainerFactory instance = new ContainerFactory();

	private ContainerFactory() {
	}

	public static ContainerFactory getInstance() {
		return instance;
	}

	/**
	 * @param className
	 *            le nom de la classe à instancier
	 * @param resourcesHandler
	 * @return un objet implémentant IContainer
	 * @throws DetailedException
	 */
	public IContainer getContainer(String className,
			ResourcesHandler resourcesHandler) throws DetailedException {

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
							+ "'");
		}

		logger.debug("Instanciation de la classe '" + className + "'...");
		Object container;

		@SuppressWarnings("rawtypes")
		Constructor constructor;
		try {
			constructor = containerClass
					.getConstructor(new Class[] { ResourcesHandler.class });
		} catch (Exception e) {
			throw new DetailedException(e)
					.addMessage("Impossible d'obtenir le constructeur pour la classe '"
							+ className + "'.");
		}

		try {
			Object[] params = new Object[] { resourcesHandler };
			container = constructor.newInstance(params);
		} catch (Exception e) {
			throw new DetailedException(e)
					.addMessage("Impossible d'instancier la classe '"
							+ className + "'.");
		}

		if (container == null) {
			throw new DetailedException("L'objet container '" + className
					+ "' instancié est nul !");
		}

		if (!(container instanceof IContainer)) {
			throw new DetailedException("L'objet container '" + className
					+ "' instancié n'hérite pas de l'interface '"
					+ IContainer.class.getName() + "' !");
		}

		return (IContainer) container;
	}

}
