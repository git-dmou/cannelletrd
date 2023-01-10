/**
 *
 * gestion de l'appel à l'API transform/translate
 *
 * @param element
 * @param origLanguage
 * @param targetLanguage
 * @param contentVersionId
 */


function translate(element, origLanguage, targetLanguage, contentVersionId ) {
    console.log("bouton traduire pressé !!! -> " + contentVersionId)


    let localization = new Localisation();

    try {
            if (DEBUG) console.group("Demande de traduction du module content_version_PK : " + contentVersionId);
            translationNotification(localization.getLocalisedString('translationInProgress'), "alert")

            let
                // type = "cannelle_import",
                locale = localization.getLocale(),
                instance = URL;
            if (DEBUG) console.log("Informations de la requête API translate : {locale, instance, origLanguage, targetLanguage, contentVersionId} ", {locale, instance, origLanguage, targetLanguage, contentVersionId});

            API.translateFromContentVersionId(instance, locale, origLanguage, targetLanguage, contentVersionId, true)
                .then(resolved => {
                    if (DEBUG) console.log(resolved);
                    if (DEBUG) console.log(localization.getLocalisedString('moduleIsReady')) ;
                    translationNotification(localization.getLocalisedString('moduleIsReady'), "success")
                }, rejected => {
                    translationNotification("problème appel API transform/translate : \n" + rejected.description, "alert")
                    // throw new Error("problème appel API transform/translate");
                })
                .catch(function (e) {
                    translationNotification(e.message, "alert")
                    throw e;
                });

    } catch (e) {
        translationNotification(e.message, "alert")
        throw e;
    } finally {
        if (DEBUG) console.groupEnd();
    }

}

function translationNotification(message, type) {
    let translatorFeedback = document.getElementById("TranslatorFeedback");

    translatorFeedback.innerHTML= decodeURI(message);

    switch (type) {
        case "alert": translatorFeedback.className="btn-outline-danger";
            break;
        case "success": translatorFeedback.className="btn-outline-success";
            break;
    }
}
