package fr.solunea.thaleia.plugins.cannelle.v6.utils;

import fr.solunea.thaleia.utils.IEvent;

public enum CannelleEvent implements IEvent {
    /**
     * Ouverture de la page de Cannelle
     */
    CannellePageAcces,
    /**
     * Cannelle : import d'un fichier Zip (= tentative de création ou de mise à jour d'un contenu)
     */
    CannelleZipUpload,
    /**
     * Cannelle : l'import d'un fichier Zip a réussi : un contenu a été créé ou mis à jour.
     */
    CannelleContentCreationOk,
    /**
     * Cannelle : l'import d'un fichier Zip a échoué : aucun contenu n'a été créé ou mis à jour.
     */
    CannelleContentCreationError
}
