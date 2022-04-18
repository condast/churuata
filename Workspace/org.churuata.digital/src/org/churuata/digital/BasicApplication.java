package org.churuata.digital;

import java.util.HashMap;
import java.util.Map;

import org.churuata.digital.core.store.SessionStore;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.entry.IDataEntryPoint;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.Application.OperationMode;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.application.EntryPointFactory;
import org.eclipse.rap.rwt.client.WebClient;

public class BasicApplication implements ApplicationConfiguration {

	public static final String S_CHURUATA_VARIANT = "churuata";
	public static final String S_CHURUATA = "/" + S_CHURUATA_VARIANT;

	private static final String S_CHURUATA_THEME = "churuata.theme";
	private static final String S_THEME_CSS = "themes/theme.css";

	public enum Pages{
		BANNER,
		CREATE,
		EDIT,
		LOGIN,
		LOGOFF,
		MAP,
		SERVICES,
		READY;

		@Override
		public String toString() {
			return "/" + StringStyler.xmlStyleString( name());
		}

		public String toPath() {
			return S_CHURUATA + "/" + StringStyler.xmlStyleString( name());
		}

		public String getTitle() {
			String result = StringStyler.prettyString(name());
			switch( this ) {
			case READY:
				result = "Arnac Control";
				break;
			case BANNER:
				result = "Banner";
				break;
			case CREATE:
				result = "create a Churuata";
				break;
			case EDIT:
				result = "Change a Churuata";
				break;
			case LOGIN:
				result = "Login";
				break;
			case LOGOFF:
				result = "Logoff";
				break;
			case MAP:
				result = "Map";
				break;
			case SERVICES:
				result = "Services";
				break;
			default:
				break;
			}
			return result;
		}

		public EntryPoint getEntryPoint() {
			EntryPoint result = null;
			switch( this ) {
			case READY:
				result = new ActiveEntryPoint();
				break;
			case BANNER:
				result = new BannerEntryPoint();
				break;
			case CREATE:
				result = new CreateEntryPoint();
				break;
			case EDIT:
				result = new EditEntryPoint();
				break;
			case LOGIN:
				result = new LoginEntryPoint();
				break;
			case LOGOFF:
				result = new LogoffEntryPoint();
				break;
			case SERVICES:
				result = new ServicesEntryPoint();
				break;
			case MAP:
				result = new BasicEntryPoint();
				break;
			default:
				break;
			}
			return result;
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

	public void configure(Application application) {
		application.addStyleSheet( S_CHURUATA_THEME, S_THEME_CSS );
		application.setOperationMode( OperationMode.SWT_COMPATIBILITY );
		SessionStore store = new SessionStore();
		for( Pages page: Pages.values()) {
			if( page.getEntryPoint() == null )
				continue;
			Map<String, String> properties = new HashMap<String, String>();
			properties.put(WebClient.PAGE_TITLE, "Churuata Digital");
			properties.put( WebClient.THEME_ID, S_CHURUATA_THEME );
			ChuruataEntryPointFactory factory = new ChuruataEntryPointFactory(store, page);
			application.addEntryPoint( page.toString(), factory, properties);
		}
	}
	
	private class ChuruataEntryPointFactory  implements EntryPointFactory{

		private SessionStore store;
		Pages page;
		
		public ChuruataEntryPointFactory( SessionStore store, Pages page) {
			super();
			this.store = store;
			this.page = page;
		}

		@SuppressWarnings({ "unchecked" })
		@Override
		public EntryPoint create() {
			EntryPoint entryPoint = page.getEntryPoint();
			if( entryPoint instanceof IDataEntryPoint ) {
				IDataEntryPoint<SessionStore> ae = (IDataEntryPoint<SessionStore>) entryPoint;
				ae.setData(store);
			}
			return entryPoint;
		}	
	}

}
