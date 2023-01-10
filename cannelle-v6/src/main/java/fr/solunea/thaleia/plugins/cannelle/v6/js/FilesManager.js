/**
 * Gestionnaire de fichiers pour CreationPage.
 */
class FilesManager {

    /**
     * Liste des fichiers.
     * @type {[MyFile, MyFile, ...]}
     * @private
     */
    _files = [];
    setFiles(files) {this._files=files}
    getFiles() {return this._files}
    getFile(index) {return this._files[index]}

    /**
     * Tempdir correspondant au contenu du gestionnaire de fichiers.
     * @type {string}
     * @private
     */
    _tempdir = '';
    setTempdir(tempdir) {this._tempdir=tempdir}
    getTempdir() {return this._tempdir}

    /**
     * Version du contenu sur la plateforme thaleia (est utilisé pour la prévisualisation).
     * @type {string}
     * @private
     */
    _contentVersion = '';
    setContentVersion(contentVersion) {this._contentVersion=contentVersion}
    getContentVersion() {return this._contentVersion}

    /**
     * Est-ce qu'au moins un fichier excel est présent dans la liste ?
     * @type {boolean}
     * @private
     */
    _atLeastOneExcelFileInList = false;
    setAtLeastOneExcelFileInList(data) {this._atLeastOneExcelFileInList=data}
    getAtLeastOneExcelFileInList() {return this._atLeastOneExcelFileInList}

    //------------------------------
    //          Constructor
    //------------------------------

    /**
     *
     * @constructor
     */
    constructor() {

    }

    //------------------------------
    //          Properties
    //------------------------------

    /**
     * Réinitialise le gestionnaire de fichiers.
     * @detail Les éléments suivants sont remis à zéro :
     *          - _files : Liste de fichiers.
     *          - _tempdir : tempdir correspondant au contenu.
     */
    reset() {
        this.setFiles([])
        this.setTempdir('')
        this.setAtLeastOneExcelFileInList(false)
    }

    /**
     * Ajoute un fichier à la liste et retourne l'ID correspondant..
     * @detail Un fichier de type MyFile sera ajouté. Pour les autres types, on tentera d'instancier
     *  un objet MyFile correspondant et de l'ajouter.
     * @param {MyFile} file
     */
    addFile(file) {
        try {
            // Un objet de type MyFile sera ajouté.
            if(file instanceof MyFile) {
                // Si le nom de fichier existe déjà
                if(this.getFileByName(file.getName())) {
                    // On remplace l'existant par celui-ci
                    this.replaceFileByName(file);
                } else {            // Sinon on se contente de l'ajouter à la liste.
                    this._files.push(file)
                }
                this.setAtLeastOneExcelFileInList(this.findExcellFileInList())
            }
            // Sinon un objet de type File sera instancié en objet MyFile
            // Puis sera ajouté via un nouvel appel à cette fonction.
            else if(file instanceof File) {
                this.addFile(new MyFile(file))
            }
            // Sinon, il s'agit d'un type d'objet qu'on ne peut traiter.
            else{
                new Error(`[FilesManager.addFile] Tentative d'ajout d'un fichier non supporté.`)
            }
        } catch (e) {
            console.error(e);
            throw `[FilesManager.addFile()] Une erreur est survenue pendant l'ajout d'un fichier.`;
        }
    }

    /**
     * Ajoute une liste de fichiers.
     * @param {Array} files
     */
    addFiles(files) {
        try {
            for(let i=0; i<files.length; i++) {
                this.addFile(files[i])
            }
        } catch {
            throw `[FilesManager.addFiles()]Une erreur est survenue pendant l'ajout d'une liste de fichiers.`
        }
    }

    /**
     * Retrouve un fichier par son ID.
     * @param {String} id ID du fichier.
     * @return {MyFile|false} Retourne le fichier si trouvé ou false si rien n'est trouvé.
     */
    getFileById(id) {
        for(const file of this.getFiles()) {
            if(file.getId() === id) {
                return file;
            }
        }
        return false;
    }

    /**
     * Retrouve un fichier par son Nom.
     * @param {String} name Nom du fichier.
     * @return {MyFile|false} Retourne le fichier si trouvé ou false si rien n'est trouvé.
     */
    getFileByName(name) {
        for(const file of this.getFiles()) {
            if(file.getName() === name) {
                return file;
            }
        }
        return false;
    }

    /**
     * Retourne l'index d'un fichier dans la liste de fichiers.
     * @param {String} id ID à rechercher.
     * @return {number|false} Index trouvé ou false si rien n'est trouvé.
     */
    getFileIndexById(id) {
        for(let i=0; i<this.getFiles().length; i++) {
            if(this.getFiles()[i].getId() === id) {
                return i;
            }
        }
        return false;
    }

    /**
     * Remplace un fichier dans la liste en se basant sur le nom.
     * @param {MyFile} file
     * @return {MyFile|boolean}
     */
    replaceFileByName(file) {
        for(let i=0; i<this.getFiles().length; i++) {
            if(this.getFiles()[i].getName() === file.getName()) {
                this._files[i] = file;
                this.setAtLeastOneExcelFileInList(this.findExcellFileInList())
            }
        }
    }

    /**
     * Mets à jour l'identifiant d'un fichier en le cherchant par son identifiant.
     * @param {String} searchById Identifiant à rechercher.
     * @param {String} newId Nouvel identifiant à assigner.
     */
    updateFileIdById(searchById, newId) {
        this.getFileById(searchById).setId(newId);
    }

    /**
     * Mets à jour l'identifiant d'un fichier en le cherchant par son nom.
     * @param {String} name Nom à rechercher.
     * @param {String} id Nouvel identifiant à assigner.
     */
    updateFileIdByName(name, id) {
        this.getFileByName(name).setId(id);
    }

    /**
     * Mets à jour le status d'un fichier en le cherchant par son identifiant.
     * @param id Identifiant à rechercher.
     * @param status Nouveau statut à assigner.
     */
    updateFileStatusById(id, status) {
        this.getFileById(id).setStatus(status);
    }

    /**
     * Mise en statut erreur d'un fichier via son identifiant.
     * @param {String} id Identifiant du fichier.
     */
    setFileOnErrorById(id) {
        try {
            this.getFileById(id).setFileOnError();
        } catch (e) {
            console.error(`Une erreur est survenue lors de la mise en erreur du fichier d'id "${id}"`)
        }
    }

    /**
     * Mise en statut erreur d'un fichier via son nom.
     * @param {String} name Nom du fichier
     */
    setFileOnErrorByName(name) {
        try {
            this.getFileByName(name).setFileOnError();
        } catch (e) {
            console.error(`Une erreur est survenue lors de la mise en erreur du fichier de nom "${name}`)
        }
    }

    /**
     * Supprime un fichier par son ID.
     * @param {String} id ID du fichier à supprimer.
     */
    deleteFileById(id) {
        let index = this.getFileIndexById(id),
            ngToDelete = 1;
        this.getFiles().splice(index, ngToDelete);
        this.setAtLeastOneExcelFileInList(this.findExcellFileInList())
    }

    /**
     * Est-ce qu'il y a au moins un fichier excel dans la liste de fichiers uploadés.
     * @return {boolean} Fichier excel trouvé ?
     */
    findExcellFileInList() {
        for(const file of this.getFiles()) {
            if((file.getExtension() === "xls") || (file.getExtension() === "xlsx")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retourne le nombre de fichiers présents.
     * @return {number}
     */
    countFiles() {
        return this._files.length;
    }

    /**
     * Est-ce que tous les fichiers sont uploadés ?
     * @detail Si un fichier n'a pas le statut "uploaded" ou "error" la valeur false est retournée.
     * @returns {boolean}
     */
    allFilesUploaded() {
        let authorized = ['uploaded', 'error'];

        for(let i=0; i<this.getFiles().length; i++) {
            if(!authorized.includes(this.getFiles()[i].getStatus())) {
                return false;
            }
        }

        return true;
    }
}