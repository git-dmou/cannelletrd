class Localisation {

    constructor() {

    }

    /**
     * Retourne une chaîne de texte localisée avec la locale courante.
     * @param {String} value
     * @return {String}
     */
    getLocalisedString(value) {
        try {
            let locale = this.getLocale();
            let localisation = `localisation_${locale}`;
            return window[localisation][value];
        } catch (e) {
            let msg = 'Une erreur est survenue lors de la récupération de la locale courante.';
            console.error(msg);
        }
    }

    /**
     * Retourne la locale courante.
     * @return {String}
     */
    getLocale() {
        const raw = $("#thaleia-xl-content").attr('lang');
        let locale;
        switch (raw) {
            case 'fr-FR':
            default:
                locale = raw;
        }

        return locale;
    }

}