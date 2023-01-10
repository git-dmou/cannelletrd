/**
 * Retourne un cookie s'il existe.
 * @param {String} name Le cookie à rechercher.
 * @return {string|null} Le contenu du cookie s'il est trouvé, sinon null
 */
function getCookie(name) {
    var dc,
        prefix,
        begin,
        end;

    dc = document.cookie;
    prefix = name + "=";
    begin = dc.indexOf("; " + prefix);
    end = dc.length; // default to end of the string

    // found, and not in first position
    if (begin !== -1) {
        // exclude the "; "
        begin += 2;
    } else {
        //see if cookie is in first position
        begin = dc.indexOf(prefix);
        // not found at all or found as a portion of another cookie name
        if (begin === -1 || begin !== 0 ) return null;
    }

    // if we find a ";" somewhere after the prefix position then "end" is that position,
    // otherwise it defaults to the end of the string
    if (dc.indexOf(";", begin) !== -1) {
        end = dc.indexOf(";", begin);
    }

    return decodeURI(dc.substring(begin + prefix.length, end) ).replace(/\"/g, '');
}

/**
 * Retourne l'url de l'instance courante de Thaleia.
 * @detail https://XXXX/YYYY
 *      - XXXX = nom d'hôte du serveur
 *      - YYYY = nom de l'instance Thaleia sur ce serveur
 */
function getCurrentThaleiaInstanceUrl() {

    const regex = /https:\/\/([^\/]*\/[^\/]*)/gm;
    let m;

    while ((m = regex.exec(window.location.href)) !== null) {
        // This is necessary to avoid infinite loops with zero-width matches
        if (m.index === regex.lastIndex) {
            regex.lastIndex++;
        }
        if(m[0] !== undefined) {
            return `${m[0]}/`
        }
    }
}

/**
 * Retourne le poids d'un fichier sous un forme simplifiée à la lecture.
 * @param bytes
 * @param si
 * @param dp
 * @return {string}
 */
function humanReadableFileSize(bytes, si=false, dp=1) {
    const thresh = si ? 1000 : 1024;

    if (Math.abs(bytes) < thresh) {
        return bytes + ' B';
    }

    const units = si
        ? ['kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
        : ['KiB', 'MiB', 'GiB', 'TiB', 'PiB', 'EiB', 'ZiB', 'YiB'];
    let u = -1;
    const r = 10**dp;

    do {
        bytes /= thresh;
        ++u;
    } while (Math.round(Math.abs(bytes) * r) / r >= thresh && u < units.length - 1);


    return bytes.toFixed(dp) + ' ' + units[u];
}

/**
 * Teste si un nom de fichier correspond à un format d'image.
 * @param {String} filename Nom de fichier à tester.
 * @return {boolean}
 */
function isImage(filename) {
    try {
        const imgExtensions = ["bmp", "fpx", "gif", "j2c", "j2k", "jfif", "jif", "jp2", "jpeg", "jpg", "jpx",
                "pcd", "png", "svg", "tif", "tiff", "webp"],
            fileExtension = filename.split(".").pop();
        return imgExtensions.includes(fileExtension);
    } catch (e) {
        return false;
    }
}

/**
 * Teste si un nom de fichier correspond à un format d'audio.
 * @param {String} filename Nom de fichier à tester.
 * @return {boolean}
 */
function isAudio(filename) {
    try {
        const audioExtensions = ["wv", "wma", "webm", "wav", "vox", "voc", "tta", "sln", "rf64", "raw", "ra", "rm",
                "opus", "ogg", "oga", "mogg", "nmf", "msv", "mpc", "mp3", "mmf", "m4p", "m4b", "m4a", "ivs", "iklax",
                "gsm", "flac", "dvf", "dss", "dct", "cda", "awb", "au", "ape", "amr", "alac", "aiff", "act", "aax",
                "aac", "aa", "8svx", "3gp"],
            fileExtension = filename.split(".").pop();
        return audioExtensions.includes(fileExtension);
    } catch (e) {
        return false;
    }
}

/**
 * Teste si un nom de fichier correspond à un format de video.
 * @param {String} filename Nom de fichier à tester.
 * @return {boolean}
 */
function isVideo(filename) {
    try {
        const videoExtensions = ["webm", "mkv", "flv", "flv", "vob", "ogg", "ogv", "drc", "gif", "gifv", "mng", "avi",
                "MTS", "M2TS", "TS", "mov", "qt", "wmv", "yuv", "rm", "rmvb", "viv", "asf", "amv", "mp4", "m4p", "m4v",
                "mpg", "mp2", "mpeg", "mpe", "mpv", "mpg", "mpeg", "m2v", "m4v", "svi", "3gp", "3g2", "mxf", "roq",
                "nsv", "flv", "f4v", "f4p", "f4a", "f4b"],
            fileExtension = filename.split(".").pop();
        return videoExtensions.includes(fileExtension);
    } catch (e) {
        return false;
    }
}

/**
 * Teste si un nom de fichier correspond à un format texte.
 * @param {String} filename Nom de fichier à tester.
 * @return {boolean}
 */
function isTextFile(filename) {
    try {
        const imgExtensions = ["txt", "doc", "docx", "odt", "rtf", "tex", "wpd"],
            fileExtension = filename.split(".").pop();
        return imgExtensions.includes(fileExtension);
    } catch (e) {
        return false;
    }
}

/**
 * Est-ce qu'un fichier est d'une certaine extension
 * @param {String} file Nom du fichier.
 * @param {String} extension Extension de fichier à tester.
 */
function checkFileExtension(file, extension) {
    return(file.split(".").pop() === extension)
}