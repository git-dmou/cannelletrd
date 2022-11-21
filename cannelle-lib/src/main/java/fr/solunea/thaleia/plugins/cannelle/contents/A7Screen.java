/**
 * Le code source, le matériel préparatoire et la documentation de ce
 * logiciel sont la propriété exclusive de la société Solunea, au titre
 * du droit de propriété intellectuelle. Ces éléments ont fait l'objet 
 * de dépôts probatoires.
 *  
 * À défaut d'accord préalable écrit de Solunea, vous ne devez pas
 * utiliser, copier, modifier, traduire, créer une œuvre dérivée,
 * transmettre, vendre ou distribuer, de manière directe ou indirecte,
 * inverser la conception ou l'assemblage ou tenter de trouver le code
 * source (sauf cas prévus par la loi), ou transférer tout droit relatif
 * audit logiciel.
 *  
 * Solunea
 * SARL - N° SIRET 48795234300027
 *
 */
package fr.solunea.thaleia.plugins.cannelle.contents;

import java.io.File;

import fr.solunea.thaleia.model.ContentVersion;
import fr.solunea.thaleia.model.Locale;
import fr.solunea.thaleia.utils.DetailedException;

/**
 * Un A7 et ses médias, tel que reconnu dans Thaleia.
 * 
 */
@SuppressWarnings("serial")
public class A7Screen extends AbstractVersionedContent {

	// private static final Logger logger = Logger.getLogger(A7Screen.class);

	/**
	 * Dans Thaleia, le nom de la propriété qui contient la durée de
	 * consultation d'un écran.
	 */
	private static final String DURATION_PROPERTY_NAME = "duration";

	/**
	 * La valeur par défaut de la durée de consultation d'un écran, si celle-ci
	 * n'est pas définie pour l'écran dans Thaleia..
	 */
	private static final String DEFAULT_DURATION = "1";

	/**
	 * Dans Thaleia, le nom de la propriété qui contient la difficulté de
	 * l'écran.
	 */
	private static final String DIFFICULTY_PROPERTY_NAME = "difficulty";

	/**
	 * La valeur par défaut de la difficulté d'un écran, si celle-ci n'est pas
	 * définie pour l'écran dans Thaleia.
	 */
	private static final String DEFAULT_DIFFICULTY = "";

	/**
	 * Dans Thaleia, le nom de la propriété qui contient le thème de l'écran.
	 */
	private static final String THEME_PROPERTY_NAME = "Theme";

	/**
	 * La valeur par défaut du thème d'un écran, si celle-ci n'est pas définie
	 * pour l'écran dans Thaleia.
	 */
	private static final String DEFAULT_THEME = "";

	private final A7Content a7Content;

	/**
	 * @param version
	 * @param zipSource
	 *            un fichier Zip qui contient le A7 et ses médias.
	 * @throws Exception
	 */
	public A7Screen(ContentVersion version, File zipSource)
			throws DetailedException {

		super(version);

		if (version == null) {
			throw new DetailedException("La version ne doit pas être nulle !");
		}

		a7Content = A7Content.createFromArchive(zipSource);

	}

	public A7Screen(ContentVersion version, A7Content a7Content)
			throws DetailedException {
		super(version);

		if (version == null) {
			throw new DetailedException("La version ne doit pas être nulle !");
		}
		this.a7Content = a7Content;
	}

	public int getDuration(Locale locale) throws DetailedException {
		String result = getContentVersionPropertyValue(DURATION_PROPERTY_NAME,
				locale, DEFAULT_DURATION);
		try {
			return Integer.parseInt(result);

		} catch (NumberFormatException e) {
			throw new DetailedException(e)
					.addMessage("Impossible de retrouver la durée de consultation de l'écran.");
		}
	}

	public String getDifficulty(Locale locale) {
		return getContentVersionPropertyValue(DIFFICULTY_PROPERTY_NAME, locale,
				DEFAULT_DIFFICULTY);
	}

	public String getTheme(Locale locale) {
		return getContentVersionPropertyValue(THEME_PROPERTY_NAME, locale,
				DEFAULT_THEME);
	}

	public A7Content getA7Content() {
		return a7Content;
	}

	/**
	 * @param contentDir
	 * @return une copie de cet écran (un nouvel objet, qui partage la même
	 *         ContentVersion), mais dont le contenu bianire (a7 et médias) est
	 *         copié dans ce répertoire. La copie s'effectue de manière sûre
	 *         (pas de doublons pour les noms de fichiers)
	 * @throws DetailedException
	 */
	public A7Screen safeCopy(File contentDir) throws DetailedException {
		return new A7Screen(this.getVersion(), getA7Content().safeCopyTo(
				contentDir));
	}
}
