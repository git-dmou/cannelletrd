package fr.solunea.thaleia.plugins.cannelle.parsers;

import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;

public class ScreenParserServiceFactory extends
		AbstractObjectFactory<IScreenParserService> {

	public ScreenParserServiceFactory(String parserName) {
		super(IScreenParserService.class,
				parserName);
	}

}
