package org.churuata.digital.core.rest;

import org.condast.commons.strings.StringStyler;

public interface IRestPages {

	//Same as the alias in plugin.xml
	public static final String S_CHURUATA_CONTEXT_PATH = "churuata";

	public static enum Pages{
		AUTH,
		SUPPORT,
		WALKERS;

		@Override
		public String toString() {
			return StringStyler.xmlStyleString( name());
		}
		
		public String toPath() {
			return S_CHURUATA_CONTEXT_PATH + "/" + toString();
		}
	}
}
