package fr.solunea.thaleia.plugins.cannelle.packager.act.specific;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom2.Element;

import fr.solunea.thaleia.plugins.cannelle.packager.act.Act;
import fr.solunea.thaleia.utils.DetailedException;

public final class SpecificUtils {

	private static final Logger logger = Logger.getLogger(SpecificUtils.class);

	private static final SpecificUtils instance = new SpecificUtils();

	private SpecificUtils() {
	}

	public static SpecificUtils getInstance() {
		return instance;
	}

	/**
	 * @param list
	 * @return une chaîne de caractères correspondant à la liste demandée, sous
	 *         la forme : {@code 14[,]21[,]22 }
	 * @throws DetailedException
	 */
	public String formatContentsList(List<String> list)
			throws DetailedException {
		try {
			String result = "";
			for (String string : list) {
				result = result + string;
				result = result + "[,]";
			}
			// On supprime le dernier [,]
			if (result.lastIndexOf("[,]") != -1) {
				result = result.substring(0, result.lastIndexOf("[,]"));
			}

			return result;
		} catch (Exception e) {
			throw new DetailedException(e)
					.addMessage("Impossible de formater la liste "
							+ list.toString() + "'.");
		}
	}

	/**
	 * @param act
	 * @param attribute
	 * @param value
	 * @return Dans ce ACT, la liste des identifiants de contenu qui contiennent
	 *         un attribut 'attribute' dont la valeur est 'value'.
	 * @throws DetailedException
	 */
	public List<String> getContents(Act act, String attribute, String value)
			throws DetailedException {
		try {
			logger.debug("Recherche des contenus dont l'attribut '" + attribute
					+ "'='" + value + "'");
			List<String> result = new ArrayList<String>();

			Element contents = act.getDocument().getRootElement()
					.getChild("contents");
			List<Element> contentList = contents.getChildren();
			for (Element content : contentList) {
				String thisContentId = content.getAttributeValue("id");
				String thisContentAttributeValue = content
						.getAttributeValue(attribute);
				if (value.equals(thisContentAttributeValue)
						&& thisContentId != null) {
					result.add(thisContentId);
				}
			}

			return result;

		} catch (Exception e) {
			throw new DetailedException(e)
					.addMessage("Impossible de retrouver la liste des contenus dont l'attribut '"
							+ attribute + "'='" + value + "'.");
		}
	}

}
