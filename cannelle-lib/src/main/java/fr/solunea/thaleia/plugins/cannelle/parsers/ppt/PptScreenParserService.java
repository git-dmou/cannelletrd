package fr.solunea.thaleia.plugins.cannelle.parsers.ppt;

import java.util.List;

import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.model.User;
import fr.solunea.thaleia.plugins.cannelle.contents.IContent;
import fr.solunea.thaleia.plugins.cannelle.contents.parsing.ParsedObject;
import fr.solunea.thaleia.plugins.cannelle.parsers.IScreenParserService;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.utils.DetailedException;

public class PptScreenParserService extends AbstractPptParserService implements
		IScreenParserService {

	public PptScreenParserService(Parameters parameters,
			ResourcesHandler resourcesHandler) {
		super(parameters, resourcesHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<ParsedObject<IContent>> getScreens(Locale locale, User user, String moduleID)
			throws DetailedException {
		// TODO Auto-generated method stub
		return null;
	}

}
