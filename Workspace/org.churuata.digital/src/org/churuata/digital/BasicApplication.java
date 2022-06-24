package org.churuata.digital;

import java.util.HashMap;
import java.util.Map;

import org.churuata.digital.core.Entries;
import org.churuata.digital.core.IPageEntry;
import org.churuata.digital.entries.BasicEntryPoint;
import org.churuata.digital.session.SessionStore;
import org.condast.commons.ui.entry.IDataEntryPoint;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.Application.OperationMode;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.application.EntryPointFactory;
import org.eclipse.rap.rwt.client.WebClient;

public class BasicApplication implements ApplicationConfiguration {

	public static final String S_CAMINANTES_TITLE = "Caminantes";
	public static final String S_CAMINANTES_PATH = "/caminantes";

	public static final String S_CHURUATA_TITLE = "Churuata Digital";
	public static final String S_CHURUATA_VARIANT = "churuata";
	public static final String S_CHURUATA = "/" + S_CHURUATA_VARIANT;

	private static final String S_CHURUATA_THEME = "churuata.theme";
	private static final String S_THEME_CSS = "themes/theme.css";

	private static final String S_PAGE_RESOURCE = "/resources/pages.txt";

	public void configure(Application application) {
		application.addStyleSheet( S_CHURUATA_THEME, S_THEME_CSS );
		application.setOperationMode( OperationMode.SWT_COMPATIBILITY );
		SessionStore store = new SessionStore();
		Entries entries = new Entries( this.getClass(), S_PAGE_RESOURCE);
		for( Entries.Pages page: Entries.Pages.values()) {
			IPageEntry entry = entries.getPageEntry(page);
			if( entry == null )
				continue;
			Map<String, String> properties = new HashMap<String, String>();
			properties.put(WebClient.PAGE_TITLE, entry.getTitle());
			properties.put( WebClient.THEME_ID, S_CHURUATA_THEME );
			ChuruataEntryPointFactory factory = new ChuruataEntryPointFactory(store, entry);
			application.addEntryPoint( page.toString(), factory, properties);
		}
		
		//Add las caminantas
		Map<String, String> properties = new HashMap<String, String>();
		properties.put(WebClient.PAGE_TITLE, S_CAMINANTES_TITLE);
		properties.put( WebClient.THEME_ID, S_CHURUATA_THEME );
		application.addEntryPoint( S_CAMINANTES_PATH, BasicEntryPoint.class, properties);
	}
	
	private class ChuruataEntryPointFactory  implements EntryPointFactory{

		private SessionStore store;
		IPageEntry page;
		
		public ChuruataEntryPointFactory( SessionStore store, IPageEntry page) {
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
