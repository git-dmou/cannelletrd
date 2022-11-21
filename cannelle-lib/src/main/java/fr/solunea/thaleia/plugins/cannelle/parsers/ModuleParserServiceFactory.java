package fr.solunea.thaleia.plugins.cannelle.parsers;

import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;

/**
 * Une fabrique de IModuleParserService
 * 
 */
public class ModuleParserServiceFactory extends
		AbstractObjectFactory<IModuleParserService> {

	public ModuleParserServiceFactory() {
		super(IModuleParserService.class,
				Parameters.MODULE_PARSER_IMPLEMENTATION);
	}

}
