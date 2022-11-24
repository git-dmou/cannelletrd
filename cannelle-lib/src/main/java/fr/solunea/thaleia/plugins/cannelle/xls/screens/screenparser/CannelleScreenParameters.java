package fr.solunea.thaleia.plugins.cannelle.xls.screens.screenparser;

import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.AbstractScreenParameter;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.IScreenParameter;
import fr.solunea.thaleia.plugins.cannelle.xls.screens.parameters.StaticParameter;

import java.util.*;

/**
 * Classe contenant les paramètres d'un écran cannelle.
 */
public class CannelleScreenParameters implements Iterable<String> {

    private final Map<String, IScreenParameter> _screenParameters = new HashMap<String, IScreenParameter>();

    public CannelleScreenParameters() {

    }

    public IScreenParameter getScreenParameter(String paramName) {
        return _screenParameters.get(paramName);
    }

    public void addScreenParameter(String paramName, IScreenParameter parameter) {
        this._screenParameters.put(paramName, parameter);
    }

    public List<StaticParameter> getStaticParameters() {
        List<StaticParameter> result = new ArrayList<StaticParameter>();
        for (IScreenParameter parameter : _screenParameters.values()) {
            if (StaticParameter.class.isAssignableFrom(parameter.getClass())) {
                result.add((StaticParameter) parameter);
            }
        }
        return result;
    }

    public List<IScreenParameter> getOptionalParameters() {
        List<IScreenParameter> result = new ArrayList<IScreenParameter>();
        for(IScreenParameter parameter : _screenParameters.values()) {
            if(Boolean.parseBoolean(parameter.getProperty(AbstractScreenParameter.IS_OPTIONAL_VALUE, "false"))) {
                result.add(parameter);
            }
        }
        return result;
    }

    @Override
    public Iterator<String> iterator() {
        return  _screenParameters.keySet().iterator();
    }
}
