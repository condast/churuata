package org.churuata.digital.core;

import org.churuata.digital.BasicApplication;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.entry.AbstractEntries;

public class Entries extends AbstractEntries<Entries.Pages>{

	public static final String S_CHURUATA = "churuata";
	public static final String S_CHURUATA_DIGITAL = "Churuata-Digital";

	public static final String S_PAGE = "page";

	public static final String S_CHURUATA_RESOURCES = "/resources/index.html";

	public static final String S_HOME = "churuata";
	
	public enum Pages{
		ACCOUNT,
		ADDRESS,
		ACTIVE,
		BANNER,
		CONTACTS,
		CREATE,
		EDIT,
		EDIT_PROFILE,
		LOCATION,
		LOGIN,
		LOGOFF,
		MAP,
		ORGANISATION,
		ORGANISATIONS,
		SERVICES,
		READY, 
		REGISTER,
		GET_EMAIL,
		REGISTER_SERVICE,
		ADD_CONTACT,
		ADD_ORGANISATION,
		ADD_SERVICES,
		SHOW_LEGAL,
		CONFIRM_SERVICE,
		ADMIN,
		EDIT_ADMIN,
		USERS,
		ACCEPTANCE;
	
		@Override
		public String toString() {
			return "/" + StringStyler.xmlStyleString( name());
		}

		public String toPretty() {
			return StringStyler.prettyString(name());
		}

		public String toPath() {
			return BasicApplication.S_CHURUATA + "/" + StringStyler.xmlStyleString( name());
		}
			
		public static boolean isValid( String str ) {
			if( StringUtils.isEmpty(str))
				return false;
			for( Pages page: values()) {
				if( page.name().toLowerCase().equals(str.toLowerCase()))
					return true;
			}
			return false;
		}
	}

	private static Entries entry = new Entries();
	
	private Entries() {
		super( Pages.class, S_PAGE_RESOURCE );
	}

	public static Entries getInstance() {
		return entry;
	}
}