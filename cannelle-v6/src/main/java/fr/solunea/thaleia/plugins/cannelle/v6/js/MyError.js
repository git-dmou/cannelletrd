class MyError {

    /**
     * Erreur levée par le code.
     * @private {Error}
     */
    _error;
    setError(error) {this._error=error}
    getError() {return this._error}

    /**
     * Titre personnalisé de l'erreur qui sera affiché dans la notification.
     * @type {string}
     * @private
     */
    _title='';
    setTitle(title) {this._title=title}
    getTitle() {return this._title}

    /**
     * Détail des erreurs rencontrées.
     * @detail Par exemple, les traitements de l'api peuvent renvoyer une liste d'erreurs de
     * traitement séparées par un retour à la ligne.
     * @detail Array of String
     * @type {Array}
     * @private
     */
    _detail=[];
    setDetail(detail) {
        if(Array.isArray(detail)) detail.forEach(elem => this.setDetail(elem))
        else this._detail.push(detail)
    }

    /**
     * Retourne la liste des détails d'erreur ou un détail spécifique.
     * @detail Si aucun id n'est spécifié, la liste entière sera retournée sous forme de tableau.
     * @param {Number} id (optionnel) Index du détail à retourner. S'il n'est pas spécifié, la liste entière sera retournée
     * @return {Array|String} Toute la liste ou un détail.
     */
    getDetail(id=null) {
        if(detail==null) return this._detail
        else return this._detail[id]
    }


    //------------------------------
    //          Constructor
    //------------------------------

    /**
     * Création d'un objet MyError.
     * @param {String} title Titre à afficher de l'erreur.
     * @param {Array} details Détail des erreurs à afficher.
     * @param {Error} error Erreur levée par le code.
     */
    constructor(title, details=null, error=null ) {
        this.setTitle(title)
        if(details !== null) this.setDetail(details)
        if(error !== null) this.setError(error)

    }

    //------------------------------
    //          Properties
    //------------------------------

    /**
     * Affiche un élément de l'erreur dans la console
     * @param {String} element Elément à afficher dans la console (title, error ou detail)
     * @param {String} logLevel Niveau de log de la console (info, log, warn, error)
     * @return {boolean} true si réussite, false si échec
     */
    log(element='title', logLevel = 'log') {
        try {
            switch(elem) {
                case 'error':
                    console[logLevel](this.getError());
                    return true;
                case 'detail':
                    console[logLevel](this.getDetail());
                    return true;
                default:    // Le titre est logué par défaut.
                    console[logLevel](this.getTitle());
                    return true;
            }
        } catch (e) {
            console.error(e);
            return false;
        }
    }


}