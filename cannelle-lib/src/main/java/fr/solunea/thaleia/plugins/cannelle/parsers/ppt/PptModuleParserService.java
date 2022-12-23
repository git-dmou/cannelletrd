package fr.solunea.thaleia.plugins.cannelle.parsers.ppt;

import fr.solunea.thaleia.plugins.cannelle.parsers.IModuleParserService;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.utils.DetailedException;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

import java.util.*;

public class PptModuleParserService extends AbstractPptParserService implements
		IModuleParserService {

	// Tous les couples clé / valeurs qui décrit les propriétés du module.
	private Map<String, String> moduleProperties;

	public PptModuleParserService(Parameters parameters,
			ResourcesHandler resourcesHandler) {
		super(parameters, resourcesHandler);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return la valeur de cette propriété. Attention : les noms de propriétés
	 *         ne sont pas ceux qui sont écrits dans les cellules Excel, mais
	 *         sont traduits d'après les indications des paramètres de
	 *         traitements.
	 * @throws DetailedException
	 */
	@Override
	public Map<String, String> getModuleProperties(String origLanguage, String targetLanguage) throws DetailedException {
		if (moduleProperties == null) {
			loadModuleProperties();
		}
		return moduleProperties;
	}

	/**
	 * Charge les couples clé/valeur
	 * 
	 * @throws DetailedException
	 */
	private void loadModuleProperties() throws DetailedException {
		moduleProperties = new HashMap<String, String>();

		logger.debug("Récupération des propriétés du module dans le fichier ppt...");

		// Parcourt du Excel pour récupérer les valeur dans la feuille de
		// description du module.
		XMLSlideShow ppt;
		try {
			ppt = getPptSlideShow();

		} catch (Exception e) {
			throw new DetailedException(e)
					.addMessage("La feuille de description du module "
							+ "n'a pas pu être ouverte dans le fichier ppt.");
		}

		System.out.println("\n ----------------- 9-Starting Parsing ----------------\n");

		// on récupère le premier slide, qui correspond aux paramètres, fichier
		// mémo, et logos du module
		XSLFSlide slide = ppt.getSlides().get(0);

		// On classe les formes que contient ce slide, en les triant par
		// position Y
		List<XSLFShape> shapes = slide.getShapes();
		Collections.sort(shapes, new Comparator<XSLFShape>() {
			@Override
			public int compare(XSLFShape o1, XSLFShape o2) {
				return (int) (o1.getAnchor().getY() - o2.getAnchor().getY());
			}
		});

		// On code ici en dur, les infos à récupérer
		String memo = ((XSLFTextShape) shapes.get(0)).getText();
		String title = ((XSLFTextShape) shapes.get(1)).getText();

		/*
		 * String logo_module = ((XSLFPictureShape)
		 * shapes.get(2))..getFileName(); String logo_associe_1 =
		 * ((XSLFPictureShape) shapes.get(3)).getFileName(); String
		 * logo_associe_2 = ((XSLFPictureShape) shapes.get(4)).getFileName();
		 * String logo_associe_3 = ((XSLFPictureShape)
		 * shapes.get(5)).getFileName();
		 */

		moduleProperties.put("Memo", memo);
		moduleProperties.put("Title", title);
		moduleProperties.put("Langue", "fr");
		/*
		 * Pour les logos ... moduleProperties.put(key, value);
		 * moduleProperties.put(key, value); moduleProperties.put(key, value);
		 * moduleProperties.put(key, value);
		 */

		System.out.println("\n --------------- FIN ----------------\n");
	}

}
