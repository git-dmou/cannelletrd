package fr.solunea.thaleia.plugins.cannelle.contents;

import java.io.Serializable;

import fr.solunea.thaleia.model.ContentVersion;
import fr.solunea.thaleia.model.Locale;

/**
 * Contient les informations qui ont été produites par l'import d'un module et
 * des écrans. Cet objet permet de conserver des informations qui ont pu être
 * saisies par l'utilisateur lors de l'import (par exemple le nom, ou la locale
 * concernée), ces informations n'étant pas stockées ailleurs, et ne restant
 * valables que le temps de sa session Thaleia.
 * 
 */
public class TitledModule implements Serializable {

	private static final long serialVersionUID = 1L;
	private ContentVersion contentVersion;
	private String contentTitle;
	private Locale locale;

	public TitledModule(ContentVersion contentVersion, String contentTitle,
			Locale locale) {
		this.contentVersion = contentVersion;
		this.contentTitle = contentTitle;
		this.locale = locale;
	}

	public ContentVersion getContentVersion() {
		return contentVersion;
	}

	public void setContentVersion(ContentVersion contentVersion) {
		this.contentVersion = contentVersion;
	}

	public String getContentTitle() {
		return contentTitle;
	}

	public void setContentTitle(String contentTitle) {
		this.contentTitle = contentTitle;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

}
