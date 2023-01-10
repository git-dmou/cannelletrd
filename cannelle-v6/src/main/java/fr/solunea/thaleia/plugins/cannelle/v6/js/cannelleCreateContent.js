
// Pour le dev, on utilise une adresse en localhost, il faudra changer l'adresse URL pour le passage en prod
const URL = getCurrentThaleiaInstanceUrl();
// const URL = "https://localhost/thaleia/";
// const URL = "https://LAPTOP-DDMSR82V/thaleia/";
// const URL = "http://localhost:8080/thaleia/";
// const URL = "https://desktop-2o5bup9/thaleia/";
// const URL = "https://prt-jwan/thaleia/";

/**
 * Mode debug.
 * @type {boolean}
 */
let DEBUG = true;

let API = new thaleia_api(URL);

/**
 * Nom du cookie pour le tempDir courant. Le cookie est personnalisé avec l'url de l'instance.
 * @type {string}
 */
let workingTempDirCookie = "thaleia-cannelle-createContent-workingTempdir" + URL.toString();

/**
 * Retourne le répertoire temporaire courrant.
 * @brief   Si un tempdir est défini dans les cookies et qu'il existe encore on l'utilise.
 *          Sinon on en crée un nouveau.
 * @return  {string} ID du tempdir courant.
 */
async function getWorkingTempDir() {
    return new Promise((resolve, reject) => {
        if(DEBUG) console.log("Recherche du tempdir courant.");

            // On cherche l'ID du tempdir dans les cookies
        let tempdir = getCookie(workingTempDirCookie);

        if(tempdir == null) {
            if(DEBUG) console.log("Aucun tempdir trouvé dans les cookies.")
            // On en crée un nouveau.
            API.createTempdir()
                .then((value)=> {
                    if(DEBUG) console.log(`Le tempdir "${value}" a été créé.`)
                    document.cookie = `${workingTempDirCookie}=${value}`;
                    resolve(value);
                })
        }
        else {
            if(DEBUG) console.log(`Un tempdir a été trouvé dans les cookies "${tempdir}"`);
            // Sinon on vérifie qu'il existe toujours
            API.getTempdirList()
                .then(tempdirList => {
                    if(DEBUG) console.log(`Liste des tempdir trouvés :`, tempdirList);
                    //Seule la dernière partie de l'url nous intéresse pour le test d'existence.
                    let tempdirID = tempdir.split('/').pop(),
                        found = false;
                    for(let i=0; i<tempdirList.length;i++) {
                        if(tempdirList[i].id === tempdirID) {found = true}
                    }
                    if(found) {
                        if(DEBUG) console.log(`Le tempdir "${tempdir}" trouvé existe.`)
                        resolve(tempdir);
                    }else{
                        if(DEBUG) console.log(`Le tempdir trouvé n'existe pas, un nouveau tempdir va être créé.`);
                        API.createTempdir()
                            .then((value)=> {
                                if(DEBUG) console.log(`Le tempdir "${value}" a été créé.`)
                                document.cookie = `${workingTempDirCookie}=${value}`;
                                resolve(value);
                            })
                    }
                })
        }
    })
}

/**
 * Retourne la liste des fichiers du tempdir courant.
 * @return {Promise<void>}
 */
async function getFilesInWorkingTempDir() {
    return new Promise(function(resolve, reject) {
        getWorkingTempDir()
            .then(workingTempDir => {
                return API.getTempdirInfo(workingTempDir);
            })
            .then(tempdirInfo => {
                resolve(tempdirInfo.files);
            })
    })
}

/**
 * Envoie des fichiers à l'API.
 * @param {Array} files Les fichiers à envoyer à l'API.
 */
function sendFiles(files){
    return new Promise(function(resolve, reject) {
        getWorkingTempDir()
            .then(tempdir => {
                if(DEBUG) console.log(`Le tempdir a été récupéré : "${tempdir}", on passe à l'ajout récursif des fichiers.`);
                recursiveAddFiles(tempdir + "/files", files)
            })
            .then( data =>resolve(data) )
            .catch(function(e){
                if(DEBUG) console.error(`caught by sendFiles .catch ${e}`)
                throw e
            })
    })
}

/**
 * Envoie une liste de fichiers à l'API, les fichiers sont envoyés un par un.
 * @param {String} tempdir L'url de l'API.
 * @param {Array} files Les fichiers à envoyer
 * @return {Promise<void>|*} Retourne une promesse resolved une fois tous les fichiers envoyés.
 */
function recursiveAddFiles(tempdir, files) {
    const nextFile = files.shift();

    // Tant qu'il y a des fichiers dans la liste
    if(nextFile){
        if(DEBUG) console.log(`Ajout récursif du fichier "${nextFile.name}".`);
        return API.addFile(tempdir, nextFile)
            .then(_ => recursiveAddFiles(tempdir, files))
            .catch(function(e){
                if(DEBUG) console.error(`caught by recursiveAddFiles .catch ${e}`)
                throw e
            })
    }else{
        return Promise.resolve();
    }
}

/**
 * Envoie une requête de suppression d'un fichier dans le tempdir courant à l'API.
 * @param {String} fileId ID du fichier à supprimer.
 * @return {Promise<unknown>}
 */
function deletefileInTempdir(fileId) {
    return new Promise(function(resolve, reject) {
        getWorkingTempDir()
            .then(tempdirId => {
                if(DEBUG) console.log(`Le tempdir a été récupéré : "${tempdirId}", on passe à la suppression du fichier "${fileId}".`);
                API.deleteFileInTempdir(tempdirId, fileId);
            })
            .then(_=>resolve() )
    })
}

function mySendFile(tempdir, file) {
    return API.addFile(tempdir, file);
}

// -------------------- Nouvelle architecture

/**
 * Retourne l'URL de l'instance Thaleia courante.
 * @return {string}
 */
function getThaleiaInstanceUrl() {
    return URL;
}

/**
 * Retourne la liste des fichiers d'un tempdir.
 * @param {String} tempdir
 * @return {Promise<unknown>}
 */
function getFilesInTempDir(tempdir) {
    try {
        return new Promise(function(resolve, reject) {
            API.getTempdirInfo(tempdir)
                .then(data=> {
                    resolve(data.files);
                },rejected=> {
                    reject(rejected);
                })
        })
    } catch (e) {
        throw e;
    }
}
