class thaleia_api {

    /**
     * Url de l'instance courante. Sera utilisée pour les appels à l'API Thaleia.
     * @private String
     */
    _url;
    setUrl(url) { this._url = url }
    getUrl() { return this._url }

    /**
     * Token de connexion à l'API Thaleia.
     * @private
     */
    _token;
    setToken(token) { this._token = token }
    getToken() { return this._token }

    _login;
    setLogin(login) { this._login = login }
    getLogin() { return this._login }

    _password;
    setPassword(password) { this._password = password }
    getPassword() { return this._password }

    constructor(url, login =null, password =null) {
        if(DEBUG) console.log(`instanciation d'un objet thaleia_api`, arguments);
        this.setUrl(url);
        this.setLogin(login);
        this.setPassword(password);
    }

    /**
     * Récupération du token de connexion.
     */
    async getTokenFromThalieaAPI() {
        let headers;
        if(this.getLogin() !== null && this.getPassword() !== null) {
            headers = {
                user: this.getLogin(),
                password: this.getPassword()
            }
        }

        await $.ajax({
            url : this.getUrl()+"api/v1/login",
            type : 'GET',
            dataType : 'json',
            context: this,
            headers: headers,

            success: (result) => {
                if(DEBUG) console.log("Token de connexion à l'API Thaleia récupéré : \"" + result + "\"");
                this.setToken(result);
            },

            error : (result, status, error) => {
                console.error("Une erreur est survenue pendant la récupération du token de connexion à l'API Thaleia !");
                console.error("erreur : " + error.toString());
                console.error("status : " + status.toString());
            },

        });
    }

    /**
     * Récupération des ressources d'un utilisateur.
     */
    getUserResources() {

    }

    /**
     * Création d'un répertoire temporaire.
     */
    async createTempdir(async = true) {
        let url = this.getUrl() + "api/v1/tempdirs",
            token = this.getToken();

        if(DEBUG) console.log(`Demande de création d'un tempDir à l'url "${url}"`);

        return new Promise(function (resolve, reject) {
            let xhr = new XMLHttpRequest();
            xhr.open("POST", url, async);
            xhr.setRequestHeader('Authorization', token);
            xhr.onload = function () {
                if (this.status >= 200 && this.status < 300) {
                    // L'url du répertoire créé est stocké dans le header "Location"
                    let location = xhr.getResponseHeader("Location");
                    if(DEBUG) console.log(`Création du tempdir "${location}"`)
                    resolve(location);
                } else {
                    reject({
                        status: this.status,
                        statusText: xhr.statusText
                    });
                }
            };
            xhr.onerror = function () {
                reject({
                    status: this.status,
                    statusText: xhr.statusText
                });
            };
            xhr.send();
        });
    }

    /**
     * Récupération de la liste des répertoires temporaires de l'utilisateur.
     */
    async getTempdirList(async = true) {
        let method = "GET",
            url = this.getUrl() + "api/v1/tempdirs",
            token = this.getToken();

        return new Promise(function (resolve, reject) {
            let xhr = new XMLHttpRequest();
            xhr.open(method, url, async);
            xhr.setRequestHeader('Authorization', token);
            xhr.onload = function () {
                if (this.status >= 200 && this.status < 300) {
                    resolve(JSON.parse(xhr.response));
                } else {
                    reject({
                        status: this.status,
                        statusText: xhr.statusText
                    });
                }
            };
            xhr.onerror = function () {
                reject({
                    status: this.status,
                    statusText: xhr.statusText
                });
            };
            xhr.send();
        });
    }

    /**
     * Description d'un répertoire temporaire.
     * @detail  Si ce tempDir existe et que l’utilisateur y a accès, on renvoie la description d’un
     *          répertoire et de son contenu, par exemple :
     *          {
     *              "id":"AE45-FRGND-5762-DF532",
     *              "creationDate":"2020-08-02 12h43:02",
     *              "files": [
     *                  {
     *                      "id":"21321-243246-242-123214",
     *                      "name":"Le fichier.pdf",
     *                      "size":"1369993"
     *                  },
     *                  ...
     *              ]
     *          }
     * @param url (String) Répertoire à décrire (ex: http://localhost:8080/thaleia/api/v1/tempdirs/d15b299419864718a769eb0963e7ebd6)
     */
    async getTempdirInfo(url) {
        let method = "GET",
            token = this.getToken();

        return new Promise(function (resolve, reject) {
            let xhr = new XMLHttpRequest();
            xhr.open(method, url, false);
            xhr.setRequestHeader('Authorization', token);
            xhr.onload = function () {
                if (this.status >= 200 && this.status < 300) {
                    if(DEBUG) console.log(`Infos du tempdir ${url} :`, xhr.response);
                    resolve(JSON.parse(xhr.response));
                } else {
                    reject({
                        status: this.status,
                        statusText: xhr.statusText
                    });
                }
            };
            xhr.onerror = function () {
                reject({
                    status: this.status,
                    statusText: xhr.statusText
                });
            };
            xhr.send();
        });
    }

    /**
     * Exporte le répertoire et son contenu sous forme d'une archive zip.
     * @param {String} tempdirId: Répertoire temporaire à exporter (/api/v1/tempdirs/[tempdirId]).
     * @param {Boolean} async Requête asynchrone ?
     */
    exportTmpDir(tempdirId, async=true) {
        let token = this.getToken(),
            url = this.getUrl()+"api/v1/tempdirs/"+tempdirId+"/export?format=zip";

        return new Promise(function (resolve, reject) {
            let xhr = new XMLHttpRequest();
            xhr.open("GET", url, async);
            xhr.setRequestHeader('Authorization', token);
            xhr.onload = function () {
                if (this.status >= 200 && this.status < 300) {
                    if(DEBUG) console.log(`L'export du répertoire temporaire "${tempdirId}" s'est déroulé avec succès.`);
                    resolve(JSON.parse(xhr.response));
                } else {
                    reject({
                        status: this.status,
                        statusText: xhr.statusText
                    });
                }
            };
            xhr.onerror = function () {
                reject({
                    status: this.status,
                    statusText: xhr.statusText
                });
            };
            xhr.send();
        });
    }

    /**
     * Ajout d’un fichier dans un répertoire temporaire.
     * @param {string} url Adresse de l'API à contacter, de forme
     *          "[instanceThaleia]/api/v1/tempdirs/[tempdirId]/files".
     * @param {File} file Fichier à envoyer.
     */
    addFile(url, file) {
        return new Promise((resolve, reject) => {
            // Requête en deux étapes (voir la doc).
            this.getUrlToUploadFile(url, file.name)
                .then(targetUrl => this.sendFile(targetUrl, file))
                .then(result => resolve(result),
                    raison => reject(raison))
                .catch(function(e){console.error(e)})
        })
    }

    /**
     * Retourne l'url à laquelle envoyer le fichier à uploader.
     * @param {string} url Adresse de l'api à contacter, de forme
     *          "[instanceThaleia]/api/v1/tempdirs/[tempdirId]/files".
     * @param {string} filename Nom du fichier à envoyer.
     * @param {Boolean} async Requête asynchrone ?
     * @return {Promise<String>} URL pour envoyer le fichier.
     */
    async getUrlToUploadFile(url, filename, async=true) {
        let token = this.getToken(),
            body = JSON.stringify({ "filename" : filename });

        return new Promise(function (resolve, reject) {
            let xhr = new XMLHttpRequest();
            xhr.open("POST", url, async);
            xhr.setRequestHeader('Authorization', token);
            xhr.onload = function () {
                if(this.status === 201) {
                    let location = xhr.getResponseHeader("Location");
                    if(DEBUG) console.log(`URL d'upload pour le fichier "${filename}" : ${location}`);
                    resolve(location);
                } else {
                    reject({
                        status: this.status,
                        statusText: xhr.statusText
                    });
                }
            };
            xhr.onerror = function () {
                reject({
                    status: this.status,
                    statusText: xhr.statusText
                });
            };
            xhr.send(body);
        });
    }

    /**
     * Envoi du fichier à l'url d'upload.
     * @detail  Requête Multipart-Form-Data POST sur l’URL d’upload.
     *          Le fichier n’est ajouté au répertoire temporaire qu’à l’issue de la réception.
     *          Si un fichier présent porte ce nom, alors il est remplacé par cette nouvelle version.
     * @param {string} url Adresse d'upload, de type
     *          "[instanceThaleia]/api/v1/tempdirs/[tempdirId]/files/[fileId]".
     * @param {File} file Fichier à envoyer.
     * @param {Boolean} async Requête asynchrone ?
     * @return {Promise<String>} Url du fichier ajouté dans le répertoire.
     */
    async sendFile(url, file, async=true) {
        let token = this.getToken();

        return new Promise(function (resolve, reject) {
            let formData = new FormData(),
                xhr = new XMLHttpRequest();

            formData.append("first", file, file.name);

            xhr.open("POST", url, async);
            xhr.setRequestHeader('Authorization', token);
            xhr.onload = function () {
                if(this.status === 201) {
                    // En cas de succès, renvoie 201, avec un en-tête Location qui pointe sur l’URL du
                    // fichier ajouté dans le répertoire temporaire : /api/v1/tempdirs/[tempdirId]/files/[fileId]
                    let location = xhr.getResponseHeader("Location");
                    if(DEBUG) console.log(`Le fichier "${file.name} a été uploadé avec succès."`);
                    resolve(location);
                } else {
                    reject({
                        status: this.status,
                        statusText: xhr.statusText
                    });
                }
            };
            xhr.onerror = function () {
                reject({
                    status: this.status,
                    statusText: xhr.statusText
                });
            };
            xhr.send(formData);
        })
    }

    /**
     * Renvoie la description du fichier stocké dans le répertoire temporaire, par exemple :
     *  {
     *      "id":"21321-243246-242-123214",
     *      "name":"Le fichier.pdf",
     *      "size":"1369993"
     *  }
     * @param tempdirId: Répertoire temporaire contenant le fichier (/api/v1/tempdirs/[tempdirId]/files/[fileId]).
     * @param fileId: Fichier à décrire (/api/v1/tempdirs/[tempdirId]/files/[fileId]).
     */
    getFileDescriptionInTempDir(tempdirId, fileId) {
        let token = this.getToken(),
            url = this.getUrl()+"api/v1/tempdirs/"+tempdirId+"/files/"+fileId;

        return new Promise(function (resolve, reject) {
            let xhr = new XMLHttpRequest();
            xhr.open("GET ", url, async);
            xhr.setRequestHeader('Authorization', token);
            xhr.onload = function () {
                if(this.status === 201) {
                    if(DEBUG) console.log(`Récupération de la description du fichier "${url}" effectuée avec succès.`)
                    resolve(xhr.response);
                } else {
                    reject({
                        status: this.status,
                        statusText: xhr.statusText
                    });
                }
            };
            xhr.onerror = function () {
                reject({
                    status: this.status,
                    statusText: xhr.statusText
                });
            };
            xhr.send();
        });
    }

    /**
     * Suppression d’un répertoire temporaire.
     * @param {String} tempdir Répertoire temporaire à supprimer (/api/v1/tempdirs/[tempdirId]).
     * @param {Boolean} async Requête asynchrone ?
     * @return {Promise<unknown>}
     */
    deleteTempdir(tempdir, async=true) {
        let token = this.getToken();

        return new Promise(function (resolve, reject) {
            let xhr = new XMLHttpRequest();
            xhr.open("DELETE", tempdir, async);
            xhr.setRequestHeader('Authorization', token);
            xhr.onload = function () {
                if(this.status === 200) {
                    if(DEBUG) console.log(`Le répertoire temporaire "${tempdir} a été supprimé avec succès."`);
                    resolve(xhr.response);
                } else {
                    reject({
                        status: this.status,
                        statusText: xhr.statusText
                    });
                }
            };
            xhr.onerror = function () {
                reject({
                    status: this.status,
                    statusText: xhr.statusText
                });
            };
            xhr.send();
        });
    }

    /**
     * Suppression d’un fichier dans un répertoire temporaire.
     * @param {String} url Répertoire temporaire contenant le fichier (/api/v1/tempdirs/[tempdirId]/files/[fileId]).
     * @param {String} fileId Fichier à supprimer (/api/v1/tempdirs/[tempdirId]/files/[fileId]).
     * @param {Boolean} async Requête asynchrone ?
     */
    deleteFileInTempdir(url, fileId, async=true) {
        let token = this.getToken();
        url = `${url}/files/${fileId}`;

        return new Promise(function (resolve, reject) {
            let xhr = new XMLHttpRequest();
            xhr.open("DELETE", url, async);
            xhr.setRequestHeader('Authorization', token);
            xhr.onload = function () {
                if(this.status === 200) {
                    if(DEBUG) console.log(`Le fichier "${fileId} a été supprimé avec succès."`);
                    resolve(xhr.response);
                } else {
                    reject({
                        status: this.status,
                        statusText: xhr.statusText
                    });
                }
            };
            xhr.onerror = function () {
                reject({
                    status: this.status,
                    statusText: xhr.statusText
                });
            };
            xhr.send();
        });
    }

    /**
     * Converti un fichier source en contenu pédagogique.
     * @param {String} instance Instance de l'API.
     * @param {String} fileUrl URL du fichier à transformer en contenu.
     * @param {String} type Type de contenu (cannelle, cannelle_import)
     * @param {String} locale Locale de l'IHM du contenu à générer.
     * @param {Boolean} async Requête asynchrone ?
     * @return {Promise<Object>} Diffère selon le type de contenu. Un type cannelle renverra le contenu pédagogique
     *          généré sous forme de ZIP, tandis qu'un cannelle_import retournera le contenu suivant :
     *          {
     *          "code": 200,
     *          "message": "Import successful",
     *          "content_version_id": "123456"          // ID du content version pour demander une prévisualisation.
     *          }
     */
    transformFromUrl(instance, fileUrl, type, locale, async=true) {
        try {
            let token = this.getToken();
            return new Promise((resolve, reject)=>{
                let xhr = new XMLHttpRequest(),
                    requestUrl = `${instance}api/v1/transform?type=${type}&locale=${locale}`,
                    method="POST",
                    body = {
                        "type": "http_download",
                        "url": `${fileUrl}/export?format=zip`,
                        "headers": [
                            {
                                "name": "Authorization",
                                "value": token
                            }
                        ]
                    };
                xhr.open(method, requestUrl, async);
                xhr.setRequestHeader('Authorization', token);
                xhr.setRequestHeader("Content-Type", "application/json");
                xhr.onload = function() {
                    if(this.status === 200) {
                        resolve(JSON.parse(xhr.response));
                    } else {
                        reject(JSON.parse(xhr.response))
                    }
                };
                xhr.onerror = function () {
                    reject({
                        status: this.status,
                        statusText: xhr.statusText
                    })
                };
                xhr.send(JSON.stringify(body));
            })
        } catch (e) {
            console.error(e)
        }
    }

    /**
     *
     * Converti une archive zip en contenu pédagogique.
     * @detail Requête de type : POST https://{instance}/api/v1/transform?type={TYPE}&locale={LOCALE}
     * @param {String} instance Instance de l'API.
     * @param {String} type Type de contenu pédagogique (cannelle/dialogue)
     * @param {String} locale Locale de l'interface du contenu.
     * @param {File} file Archive zip.
     * @param {Boolean} async Requête asynchrone ?
     * @return {Promise<Object>} Diffère selon le type de contenu. Un type cannelle renverra le contenu pédagogique
     *          généré sous forme de ZIP, tandis qu'un cannelle_import retournera le contenu suivant :
     *          {
     *          "code": 200,
     *          "message": "Import successful",
     *          "content_version_id": "123456"          // ID du content version pour demander une prévisualisation.
     *          }
     */
    transformFromZipFile(instance, type, locale, file, async=true) {
        let token = this.getToken(),
            apiUrl = `${instance}api/v1/transform?type=${type}&locale=${locale}`;

        return new Promise(function (resolve, reject) {
            let formData = new FormData(),
                xhr = new XMLHttpRequest();
            formData.append("first", file, file.name);
            xhr.open("POST", apiUrl, async);
            xhr.setRequestHeader('Authorization', token);
            xhr.onload = function () {
                if(this.status === 200) {
                    resolve(JSON.parse(xhr.response));
                } else {
                    reject(JSON.parse(xhr.response))
                }
            };
            xhr.onerror = function () {
                reject({
                    status: this.status,
                    statusText: xhr.statusText
                });
            };
            xhr.send(formData);
        })
    }

    /**
     *
     * Traduit un contenu à partir de sa content_version_PK.
     * @detail Requête de type : POST https://{instance}/api/v1/transform/translate?locale={LOCALE}
     * @param {String} instance Instance de l'API.
     * @param {String} type Type de contenu pédagogique (cannelle/dialogue)
     * @param {String} locale Locale de l'interface du contenu.
     * @param {File} file Archive zip.
     * @param {Boolean} async Requête asynchrone ?
     * @param origLanguage dans le corps de la requête
     * @param targetLanguage dans le corps de la requête
     * @param contentVersionId dans le corps de la requête
     *
     * @return {Promise<Object>} cré une nouvelle version du contenu, traduit dans targetLanguage
     *          retournera le contenu suivant :
     *          {
     *          "code": 200,
     *          "message": "Translation successful",
     *          "content_version_id": "123456"          // ID du content version pour demander une prévisualisation. ?
     *          }
     */
    translateFromContentVersionId(instance,locale, origLanguage, targetLanguage, contentVersionId, async=true) {
        try {
            let token = this.getToken(),
                apiUrl = `${instance}api/v1/transform/translate?locale=${locale}`;

            return new Promise(function (resolve, reject) {
                let
                    xhr = new XMLHttpRequest(),
                    body = {
                        "origLanguage": origLanguage,
                        "targetLanguage": targetLanguage,
                        "contentVersionId": contentVersionId,
                        "headers": [
                            {
                                "name": "Authorization",
                                "value": token
                            }
                        ]
                    };
                xhr.open("POST", apiUrl, async);
                xhr.setRequestHeader('Authorization', token);
                xhr.setRequestHeader("Content-Type", "application/json");

                xhr.onload = function () {
                    if (this.status === 200) {
                        resolve(JSON.parse(xhr.response));
                    } else {
                        reject(JSON.parse(xhr.response))
                    }
                };
                xhr.onerror = function () {
                    reject({
                        status: this.status,
                        statusText: xhr.statusText
                    });
                };
                xhr.send(JSON.stringify(body));
            })
        } catch (e) {
            console.error(e)
        }
    }

    /**
     * Prévisualisation d'une version d'un contenu.
     * @detail GET /api/v1/contentVersion/[id]/preview
     * @detail Renvoie 200 en cas de réussite, avec dans le corps de la réponse le détail de
     *          l’emplacement de prévisualisation :
     *          {
     *              "content_version_id": "3665",
     *              "content_identifier": "20160823_yles_c",
     *              "content_type_name": "module_cannelle",
     *              "last_update_date": "20201014-15:16:02",
     *              "revision_number": 94,
     *              "preview_url": "https://prt-rmar//thaleia/preview/1602685225741/index.html"
     *          }
     * @detail Si la ContentVersion demandée n’existe pas, renvoie un code 404 et le corps de
     *          réponse :
     *          {
     *              "code": 404,
     *              "message": "ContentVersion not found",
     *              "description": ""
     *          }
     * @detail Si l’accès à la ContentVersion n’est pas permis pour cet utilisateur, renvoie le
     *          code 403 et le corps de réponse :
     *          {
     *              "code": 403,
     *              "message": "ContentVersion access not permitted",
     *              "description": ""
     *          }
     * @detail Si une erreur a eu lieu durant la prévisualisation, alors renvoie un code 500 et
     *          le corps de réponse :
     *          {
     *              "code": 500,
     *              "message": "Error during previsualisation production.",
     *              "description": ""
     *          }
     * @param {String} instance Instance client.
     * @param {String} id Identifiant de la version du contenu à prévisualiser.
     * @param {Boolean} async Requête asynchrone.
     * @return {Promise<resolve|reject>}
     */
    preview(instance, id, async=true) {
        let token = this.getToken();
        return new Promise((resolve, reject)=>{
            let xhr = new XMLHttpRequest(),
                url = `${instance}api/v1/contentVersion/${id}/preview`,
                method = "GET";

            xhr.open(method, url, async);
            xhr.setRequestHeader('Authorization', token);
            xhr.onload = function() {
                if(this.status === 200) {
                    resolve(JSON.parse(xhr.response));
                } else {
                    reject(JSON.parse(xhr.response))
                }
            };
            xhr.onerror = function () {
                reject({
                    status: this.status,
                    statusText: xhr.statusText
                })
            };
            xhr.send();
        })
    }
}


