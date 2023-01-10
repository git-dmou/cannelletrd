class Notifyer {

    /**
     * Afficher ou non la notification.
     * @type {boolean}
     * @private
     */
    _show = false;
    setShow(show) {this._show=show}
    getShow() {return this._show}

    /**
     * Type de nofication.
     * @detail Types possibles :
     *              error, duplicateOfResource, moduleIsReady, howToAddZip, loader
     * @type {string}
     * @private
     */
    _type='';
    setType(type) {this._type=type}
    getType() {return this._type}

    /**
     * Titre de la notification.
     * @type {string}
     * @private
     */
    _title='';
    setTitle(title) {this._title=title}
    getTitle() {return this._title}

    /**
     * Détails de la notification
     * @detail Pour le moment, ne sert qu'aux erreurs.
     * @type {[]}
     */
    _details=[];
    setDetails(details) {this._details = details}
    /**
     * Retourne la liste des détails d'une notification.
     * @detail Si aucun id n'est spécifié, la liste entière sera retournée sous forme de tableau.
     * @param {Number} id (optionnel) Index du détail à retourner. S'il n'est pas spécifié, la liste entière sera retournée
     * @return {Array|String} Toute la liste ou un détail.
     */
    getDetail(id=null) {
        if(id==null) return this._details
        else return this._details[id]
    }

    /**
     * Liste des éléments à masquer dans la notification courante.
     * @type {null}
     * @private
     */
    _elementsToHide = [];

    /**
     * Ajoute une liste d'éléments à cacher dans la notification courante.
     * @param {Array|String} elements Eléménts à cacher.
     */
    setElementsToHide(elements) {
        if(Array.isArray(elements)) {
            elements.forEach(element => this.setElementToHide(element));
        } else {
            this.setElementToHide(elements);
        }
    }

    /**
     * Ajoute un élément à la liste des éléments à cacher dans la notification courante.
     * @detail L'élément n'est ajouté que s'il n'est pas déjà dans la liste.
     * @param {String} element Elément à cacher.
     */
    setElementToHide(element) {
        if(! this._elementsToHide.includes(element)) {
            this._elementsToHide.push(element)
        }
    }

    /**
     * Retourne la liste des éléments à cacher dans la notification courante.
     * @returns {null}
     */
    getElementsToHide() {return this._elementsToHide}

    /**
     * Est-ce qu'un élément donné est dans la liste d'éléments à cacher de la notification courante ?
     * @param {String} element Elément à rechercher.
     * @returns {boolean}
     */
    getElementToHide(element) {return this._elementsToHide.includes(element)}

    constructor() {

    }

    /**
     * Création d'une notification.
     * @details Un clear() est effectué avant de renseigner la nouvelle notification.
     * @param {String} type Type de notification.
     * @param {String} title Titre de la notification.
     * @param {[]} details Détails de la notification.
     * @param {[]} elementsToHide Eléments à ne pas afficher.
     */
    displayNotification(type, title, details= null, elementsToHide = null) {
        this.clear()
        this.setShow(true)
        this.setType(type)
        this.setTitle(title)
        this.setElementsToHide(elementsToHide);
        if(details !== null) this.setDetails(details)
    }

    /**
     * Réinitialise le Notifyer.
     */
    clear() {
        this.setShow(false);
        this.setType('');
        this.setTitle('');
        this.clearDetails();
        this._elementsToHide = [];
    }

    /**
     * Ajoute une liste de details
     * @param {Array} details
     */
    addDetails(details) {
        if(Array.isArray(details)) {
            details.forEach(detail =>
                this.addDetail(detail)
            )
        } else {
            throw `Tentative d'ajout d'une liste de détails non tableau.`
        }
    }

    /**
     * Ajoute un detail à ceux existants (Peut être utile pour les gestion d'erreurs).
     * @param {String} detail Détail de la notification.
     */
    addDetail(detail) {
        this._details.push(detail)
    }

    /**
     * Vide la liste de details.
     */
    clearDetails() {
        this.details = []
    }

}