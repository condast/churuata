package org.churuata.digital;

import java.util.HashMap;
import java.util.Map;

import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.Application.OperationMode;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
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
			default:
				break;
			}
			return result;
		}

		public Class<? extends AbstractEntryPoint> getEntryPoint() {
			Class<? extends AbstractEntryPoint> result = null;
			switch( this ) {
			case READY:
				result = ActiveEntryPoint.class;
				break;
			case BANNER:
				result = BannerEntryPoint.class;
				break;
			case CREATE:
				result = CreateEntryPoint.class;
				break;
			case EDIT:
				result = EditEntryPoint.class;
				break;
			case LOGIN:
				result = LoginEntryPoint.class;
				break;
			case LOGOFF:
				result = LogoffEntryPoint.class;
				break;
			case MAP:
				result = BasicEntryPoint.class;
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
		for( Pages page: Pages.values()) {
			if( page.getEntryPoint() == null )
				continue;
			Map<String, String> properties = new HashMap<String, String>();
			properties.put(WebClient.PAGE_TITLE, "Churuata Digital");
			properties.put( WebClient.THEME_ID, S_CHURUATA_THEME );
			application.addEntryPoint( page.toString(), page.getEntryPoint(), properties);
		}
	}
}
