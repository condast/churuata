package org.churuata.digital;

import java.util.concurrent.TimeUnit;

import org.churuata.digital.BasicApplication.Pages;
import org.churuata.digital.core.store.SessionStore;
import org.churuata.digital.ui.views.EditChuruataComposite;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.config.Config;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.entry.AbstractRestEntryPoint;
import org.condast.commons.ui.utils.RWTUtils;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class EditEntryPoint extends AbstractRestEntryPoint<SessionStore>{
	private static final long serialVersionUID = 1L;

	public static final String S_PAGE = "page";

	public static final String S_ARNAC = "arnac";

	public static final String S_ERR_NO_VESSEL = "No Vessel has been found.";	

	private EditChuruataComposite editComposite;
	
	/**
	 * Slow down start time a bit in order to let the browser find the location
	 * @param startTime
	 * @param rate
	 */
	public EditEntryPoint() {
		super( 3*DEFAULT_SCHEDULE, DEFAULT_SCHEDULE);
	}

	@Override
	protected boolean prepare(Composite parent) {
		SessionStore store = getData();
		if( store == null )
			return false;
		ILoginUser user = store.getLoginUser();
		if ( user == null )
			return false;
		return true;
	}
	
	@Override
	protected Composite createComposite(Composite parent) {
		parent.setLayout( new FillLayout());
		editComposite = new EditChuruataComposite(parent, SWT.NONE );
		editComposite.setData( RWT.CUSTOM_VARIANT, S_ARNAC );
		editComposite.addEditListener( e->onRegistrationCompleted(e));
		return editComposite;
	}

	@Override
	protected boolean postProcess(Composite parent) {
		Config config = new Config();
		String context = config.getServerContext();
		SessionStore store = getData();

		ILoginUser user = store.getLoginUser();
		editComposite.setInput(context, user );
		LatLng selected = store.getSelected();
		editComposite.setInput(selected);
		return true;
	}
	
	protected void onRegistrationCompleted( EditEvent<LatLng> event ) {
		LatLng data = event.getData();
		SessionStore store = getData();
		switch( event.getType()) {
		case COMPLETE:
			RWTUtils.redirect(Pages.READY.toPath());
			break;
		case CHANGED:
			store.setSelected( data);
			break;
		default:
			break;
		}
	}

	@Override
	protected void createTimer(boolean create, int nrOfThreads, TimeUnit unit, int startTime, int rate) {
		super.createTimer(true, nrOfThreads, unit, startTime, rate);
	}

	@Override
	protected void handleTimer() {
		try {
			super.handleTimer();
			SessionStore store = getData();
			if(( store == null ) || ( store.getLoginUser() == null ))
				return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void handleSessionTimeout(boolean reload) {
		SessionStore store = super.getData();
		store.setLoginUser(null);
		super.handleSessionTimeout(reload);
	}

	@Override
	public void close() {
		this.editComposite.removeEditListener(e->onRegistrationCompleted(e));
		super.close();
	}
}