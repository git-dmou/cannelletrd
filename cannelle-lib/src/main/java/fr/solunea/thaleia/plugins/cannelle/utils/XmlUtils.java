package fr.solunea.thaleia.plugins.cannelle.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import fr.solunea.thaleia.service.utils.XpathUtils;
import fr.solunea.thaleia.utils.DetailedException;

public class XmlUtils extends fr.solunea.thaleia.service.utils.XmlUtils {

	/**
	 * @param doc
	 * @param destination
	 * @param encoding
	 * @throws DetailedException
	 */
	public static void writeXML(Document doc, File destination, String encoding)
			throws DetailedException {

		try {
			Format format = Format.getPrettyFormat();

			// On recherche toutes les balises d'un texte de vignette
			List<Element> zones = XpathUtils.getElements(
					"//params/localized[@id='thumbnail.text']/lang", doc);
			for (Element zone : zones) {
				// Si la valeur de la balises est vide, ou ne contient que
				// des espaces
				if (zone.getValue().trim().isEmpty()) {
					// On remplace les espaces par des espace fines insécables
					// On s'assure donc que les espaces ne seront pas trimés
					// lors de la mise en forme du XML.
					String newText = "";
					for (int i = 0; i < zone.getValue().length(); i++) {
						newText = newText + "\u2009";
					}
					zone.setText(newText);
					// Note, j'ai essayé de placer le contenu dans du CDATA,
					// mais la balise était tout de même fermée, et les espaces
					// n'étaient pas conservés.
				}
			}

			XMLOutputter xo = new XMLOutputter(format);
			FileOutputStream fos = new FileOutputStream(destination);
			xo.output(doc, fos);
			IOUtils.closeQuietly(fos);
			/*
			 * logger.debug("Document XML écrit dans le fichier '" + destination
			 * + "'. Ce fichier peut-il être écrit ?" + destination.canWrite());
			 */
		} catch (Exception e) {
			throw new DetailedException(e)
					.addMessage("Impossible d'écrire le xml dans le fichier "
							+ destination.getAbsolutePath() + ".");
		}
	}

}
