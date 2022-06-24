package org.churuata.digital.entries;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;

import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries;
import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.map.MapBrowser;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.config.Config;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.EditEvent.EditTypes;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.entry.AbstractRestEntryPoint;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.StartupParameters;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;


public class BasicEntryPoint extends AbstractRestEntryPoint<SessionStore> {
	private static final long serialVersionUID = 1L;

	public static final String S_CHURUATA = "Churuata-Digital";

	public static final String S_CHURUATA_PAGE = "/churuata";

	private MapBrowser mapComposite;
	
	private long token;
	
	private IEditListener<LatLng> listener = e->onEditEvent( e );
	
	@Override
	protected boolean prepare(Composite parent) {
		StartupParameters service = RWT.getClient().getService( StartupParameters.class );
		String tokenstr = service.getParameter( IDomainProvider.Attributes.TOKEN.name().toLowerCase());
		if(!StringUtils.isEmpty(tokenstr)) 
			token = Long.parseLong(tokenstr);
		return true;
	}

	@Override
    protected Composite createComposite(Composite parent) {
        parent.setLayout(new FillLayout());
        mapComposite = new MapBrowser( parent, SWT.NONE);
 		mapComposite.setData( RWT.CUSTOM_VARIANT, S_CHURUATA );
 		mapComposite.addEditListener(listener);
 		return mapComposite;
    }

	@Override
	protected boolean postProcess(Composite parent) {
		Config config = new Config();
		mapComposite.setInput(config.getServerContext());
		mapComposite.locate();
		return super.postProcess(parent);
	}

	private Object onEditEvent(EditEvent<LatLng> e) {
		switch( e.getType()) {
		case SELECTED:
			if( e.getData() == null )
				return null;
			HttpSession hs = RWT.getUISession().getHttpSession();
			hs.setAttribute(EditTypes.SELECTED.name(), e.getData());
			Dispatcher.redirect(Entries.Pages.SERVICES, token);
			break;
		default:
			break;
		}
		return null;
	}

	@Override
	protected void createTimer(boolean create, int nrOfThreads, TimeUnit unit, int startTime, int rate) {
		super.createTimer(true, nrOfThreads, unit, startTime, 10000);
	}

	@Override
	protected void handleTimer() {
		try {
			mapComposite.refresh();
			super.handleTimer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		this.mapComposite.removeEditListener(listener);
		super.close();
	}	
}
