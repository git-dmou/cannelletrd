package fr.solunea.thaleia.plugins.cannelle.parsers;

import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.utils.DetailedException;
import fr.solunea.thaleia.webapp.security.ThaleiaSession;

import java.lang.reflect.Constructor;

/**
 * Une fabrique qui instancie un objet dont le nom de classe est dans les
 * paramètres
 */
public class AbstractObjectFactory<T> {

    private Class<T> type;
    private String classNameParameter;

    /**
     * Une fabrique d'objets qui héritent de cette classe.
     *
     * @param classNameParameter le nom du paramètre qui contient le nom de la classe à
     *                           instancier
     * @param type               la classe dont l'objet à instancier doit hériter
     */
    AbstractObjectFactory(Class<T> type, String classNameParameter) {
        this.type = type;
        this.classNameParameter = classNameParameter;
    }

    /**
     * @return un objet de la classe className, qui doit hériter de T
     */
    @SuppressWarnings("unchecked")
    public T getObject(Parameters parameters, ResourcesHandler resourcesHandler) throws DetailedException {

        // Recherche de la propriété qui contient le nom de la classe
        // d'implémentation
        String className = parameters.getValue(this.classNameParameter);

        if (className.isEmpty()) {
            throw new DetailedException("Le paramètre '" + classNameParameter + "' n'est pas renseigné !");
        }

        try {
            // Instanciation d'un objet dont la classe porte ce nom
            // return ClassFactory.getInstanceOf(className, this.type,
            // ThaleiaSession.get().getPluginService().getClassLoader());

            // Recherche de la classe qui porte ce nom
            Class<T> clazz;
            try {
                clazz = (Class<T>) Class.forName(className, false,
						ThaleiaSession.get().getPluginService().getClassLoader()).asSubclass(this.type);

            } catch (Exception e) {
                throw new DetailedException(e).addMessage(
                        "Impossible de charger la classe '" + className + "' implémentant l'interface '"
                                + this.type.getName() + "' : " + e);
            }

            // Instanciation, avec les paramètres et le resources handler
            Constructor<T> constructor;
            try {
                // On recherche le constructeur avec les paramètres de type
                // Parameters et ResourcesHandler
                constructor = clazz.getDeclaredConstructor(Parameters.class, ResourcesHandler.class);
                return constructor.newInstance(parameters, resourcesHandler);

            } catch (Exception e) {
                throw new DetailedException(e).addMessage(
                        "Impossible d'instancier un objet de la classe '" + clazz.getName() + "' : " + e);
            }

        } catch (DetailedException e) {
            throw new DetailedException(e).addMessage("Impossible d'instancier un objet de type '" + className + "'.");
        }

    }
}
