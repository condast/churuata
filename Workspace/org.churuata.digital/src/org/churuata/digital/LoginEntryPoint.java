package org.churuata.digital;

import java.util.concurrent.TimeUnit;

import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.store.SessionStore;
import org.condast.commons.authentication.core.LoginData;
import org.condast.commons.authentication.ui.views.AuthenticationGroup;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.entry.AbstractRestEntryPoint;
import org.condast.commons.ui.provider.ICompositeProvider;
import org.condast.commons.ui.session.AbstractSessionHandler;
import org.condast.commons.ui.session.SessionEvent;
import org.condast.commons.ui.utils.RWTUtils;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.StartupParameters;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class LoginEntryPoint extends AbstractRestEntryPoint{
	private static final long serialVersionUID = 1L;

	private static final String S_CHURUATA_ACTIVE_HOME = "/churuata/active?token=";
	
	private long token;
	private ILoginUser user;
	private boolean loggedIn;
	private SessionHandler session;
	
	private AuthenticationGroup login;
	
	private Dispatcher dispatcher = Dispatcher.getInstance();

	@Override
	protected Composite createComposite(Composite parent) {
		parent.setLayout(new FillLayout());
		StartupParameters service = RWT.getClient().getService( StartupParameters.class );
		String tokenstr = service.getParameter(ILoginUser.Attributes.TOKEN.name().toLowerCase());
		token = Long.parseLong(tokenstr);
		ICompositeProvider<Composite> provider = dispatcher.getComposite(BasicApplication.Pages.LOGIN.name().toLowerCase());
		login = (AuthenticationGroup) provider.getComposite(parent, SWT.NONE);
		login.addEditListener(e->onEditEvent(e));
		session = new SessionHandler(login.getDisplay());
		return login;
	}

	@Override
	protected boolean prepare(Composite parent) {
		return true;
	}

	@Override
	protected void createTimer(boolean create, int nrOfThreads, TimeUnit unit, int startTime, int rate) {
		super.createTimer(true, nrOfThreads, unit, startTime, rate);
	}

	protected void onEditEvent( EditEvent<LoginData> event ) {
		switch( event.getType()) {
		case COMPLETE:
			SessionStore store = dispatcher.createSessionStore(RWT.getUISession().getHttpSession());
			user = store.getLoginUser();
			user.setToken(token);
			RWTUtils.redirect( S_CHURUATA_ACTIVE_HOME);
			break;
		default:
			break;
		}
	}

	@Override
	protected void handleTimer() {
		try {
			session.addData(user);
			super.handleTimer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void handleSessionTimeout(boolean reload) { /* NOTHING */}
	
	private class SessionHandler extends AbstractSessionHandler<ILoginUser>{

		protected SessionHandler(Display display) {
			super(display);
		}

		@Override
		protected void onHandleSession(SessionEvent<ILoginUser> sevent) {
			loggedIn = login.refresh();	
			if( loggedIn )
				stopTimer();
		}
		
	}
}