/**
 * Classe représentant un fichier dans le tempdir de l'utilisateur courant.
 */
class MyFile {

    _id;
    setId(id) {this._id=id}
    getId() {return this._id}

    /**
     * Nom du fichier
     * @private {String}
     */
    _name;
    setName(name) {this._name = name}
    getName() {return this._name}

    /**
     * Taille du fichier en format "humain" (ex: 1 Mo).
     * @private {String}
     */
    _size;
    setSize(size) {this._size=size}
    getSize() {return this._size}

    /**
     * Icone correspondant au format du fichier.
     * @private {String}
     */
    _icon;
    setIcon(icon) {this._icon=icon}
    getIcon() {return this._icon}

    /**
     * Exgtension du fichier.
     * @private {String}
     */
    _extension;
    setExtension() {this._extension = this.getName().split(".").pop();}
    getExtension() {return this._extension}

    _path;
    setPath(path) {this._path=path}
    getPath() {return this._path}

    /**
     * Est-ce que le fichier est en erreur (par exemple si son upload a échoué).
     * @private Boolean
     */
    _isError;
    setIsError(isError) {this._isError=isError}
    getIsError() {return this._isError}

    /**
     * Afficher ou non la progressbar d'upload du fichier.
     * @private Boolean
     */
    _progressbar = false;
    setProgressbar(progressbar) {this._progressbar=progressbar}
    getProgressbar() {return this._progressbar}

    /**
     * Statut du fichier.
     * uploading : fichier en cours d'upload
     * uploaded : Fichier uploadé.
     * error : fichier en erreur
     * @private String
     */
    _status;
    setStatus(status) {this._status = status}
    getStatus() {return this._status}

    /**
     * Création d'un objet MyFile.
     * @constructor
     * @param {File} file Fichier à convertir en MyFile.
     */
    constructor(file) {
        // Si aucun ID n'est défini dans le fichier en input un id aléatoire est assigné.
        if(typeof file.id !== "undefined") {this.setId(file.id)}
        if(typeof file.status !== "undefined") {this.setId(file.status)}
        else {this.setId(Math.random().toString(36).substring(10))}
        this.setName(file.name);
        this.setSize(humanReadableFileSize(file.size));
        this.setExtension()
        this.setIcon(this.determineIconForFile(file.name));
    }

    /**
     *
     * @param {String} filename Nom du fichier (avec son extension)
     * @return {*}
     */
    determineIconForFile(filename) {
        let ext = filename.split('.').pop(),
            icon;

        if(isImage(ext)) {icon = "icon-image"}
        else if(isAudio(ext)) {icon = "icon-sound"}
        else if(isVideo(ext)) {icon = "icon-video"}
        else if(ext === "xls" || ext === "xlsx") {icon = "icon-excel"}
        else if(ext === "pdf") {icon = "icon-pdf"}
        else if(isTextFile(ext)) {icon = "icon-text"}
        else {icon = "icon-file"}

        return icon;
    }

    /**
     * Mets le fichier en statut erreur.
     * @detail L'icône du fichier est mise à jour avec la valeur "warning".
     */
    setFileOnError() {
        this.setIsError(true);
        this.setStatus("error");
        this.setIcon("icon-warning");
    }
}