package fr.solunea.thaleia.plugins.cannelle.contents;

import fr.solunea.thaleia.utils.DetailedException;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@SuppressWarnings("serial")
public abstract class AbstractContent implements IContent, Serializable {

	private static final Logger logger = Logger
			.getLogger(AbstractContent.class);

	private final Properties properties;

	private String identifier;

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public void setIdentifier(String identifier) throws DetailedException {
		this.identifier = identifier;
	}

	public AbstractContent() {
		properties = new Properties();
	}

	@Override
	public void addProperty(String key, String value) {
		logger.debug("Ajout de la propriété '" + key + "'= '" + value + "'");
		properties.put(key, value);
	}

	@Override
	public void addProperties(Map<String, String> properties) {
		for (String key : properties.keySet()) {
			logger.debug("Ajout de la propriété '" + key + "'= '"
					+ properties.get(key) + "'");
			this.properties.put(key, properties.get(key));
		}
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	@Override
	public Map<String, String> getProperties() {
		Map<String, String> result = new HashMap<String, String>(properties
				.stringPropertyNames().size());
		for (final String name : properties.stringPropertyNames()) {
			result.put(name, properties.getProperty(name));
		}
		return result;
	}

}
