package org.churuata.digital.ui;

import org.condast.commons.i18n.Language;

public class ChuruataLanguage extends Language {

	private static final String S_LANGUAGE = "NALanguage";

	private static ChuruataLanguage language = new ChuruataLanguage();
	
	private ChuruataLanguage() {
		super( S_LANGUAGE, "NL", "nl");
	}
	
	public static ChuruataLanguage getInstance(){
		return language;
	}	
	
	
}
