package org.churuata.digital.core.rest;

import org.condast.commons.strings.StringStyler;

public interface IRestPages {

	//Same as the alias in plugin.xml
	public static final String S_CHURUATA_CONTEXT_PATH = "churuatas";
	public static final String S_CHURUATA_CONTEXT_PATH_ORGANISATION = S_CHURUATA_CONTEXT_PATH + "/organisation";

	public static enum Pages{
		AUTH,
		SUPPORT,
		CONTACT,
		ORGANISATION,
		WALKERS;

		@Override
		public String toString() {
			return StringStyler.xmlStyleString( name());
		}
		
		public String toPath() {
			switch( this ) {
			case ORGANISATION:
				return S_CHURUATA_CONTEXT_PATH_ORGANISATION + "/" + toString();
			case CONTACT:
				return S_CHURUATA_CONTEXT_PATH_ORGANISATION + "/" + toString();
			default:
				return S_CHURUATA_CONTEXT_PATH + "/" + toString();
			}
		}
	}
}
