/**
 * Le code source, le matériel préparatoire et la documentation de ce
 * logiciel sont la propriété exclusive de la société Solunea, au titre
 * du droit de propriété intellectuelle. Ces éléments ont fait l'objet
 * de dépôts probatoires.
 * <p>
 * À défaut d'accord préalable écrit de Solunea, vous ne devez pas
 * utiliser, copier, modifier, traduire, créer une œuvre dérivée,
 * transmettre, vendre ou distribuer, de manière directe ou indirecte,
 * inverser la conception ou l'assemblage ou tenter de trouver le code
 * source (sauf cas prévus par la loi), ou transférer tout droit relatif
 * audit logiciel.
 * <p>
 * Solunea
 * SARL - N° SIRET 48795234300027
 */
package fr.solunea.thaleia.plugins.cannelle.packager;

import fr.solunea.thaleia.plugins.cannelle.packager.act.ActFormatPackager;
import fr.solunea.thaleia.plugins.cannelle.utils.Parameters;
import fr.solunea.thaleia.plugins.cannelle.utils.ResourcesHandler;
import fr.solunea.thaleia.utils.DetailedException;

public final class PackageFormaterFactory {

    public final static String ACT_FORMAT = "act";
    public final static String QTI_FORMAT = "qti";
    public final static String PDF_FORMAT = "pdf";

    private static final PackageFormaterFactory instance = new PackageFormaterFactory();

    private PackageFormaterFactory() {
    }

    public static PackageFormaterFactory getInstance() {
        return instance;
    }

    public IFormatPackager getPackager(String format, Parameters parameters, ResourcesHandler resourcesHandler)
			throws DetailedException {
        IFormatPackager result;

        if (format.equals(ACT_FORMAT)) {
            result = new ActFormatPackager(parameters, resourcesHandler);

            // } else if (format.equals(QTI_FORMAT)) {
            //
            // result = new QTIFormatPackager();
            // } else if (format.equals(PDF_FORMAT)) {
            //
            // result = new PDFFormatPackager();
        } else {
            throw new DetailedException("Le format '" + format + "' n'est pas reconnu !");
        }

        return result;
    }

}
