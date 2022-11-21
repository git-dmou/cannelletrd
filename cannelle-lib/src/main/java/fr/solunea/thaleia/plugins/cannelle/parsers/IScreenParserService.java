package fr.solunea.thaleia.plugins.cannelle.parsers;

import java.util.List;

import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.model.User;
import fr.solunea.thaleia.plugins.cannelle.contents.IContent;
import fr.solunea.thaleia.plugins.cannelle.contents.parsing.ParsedObject;
import fr.solunea.thaleia.utils.DetailedException;

public interface IScreenParserService {

	List<ParsedObject<IContent>> getScreens(Locale locale, User user, String moduleID)
			throws DetailedException;

}
