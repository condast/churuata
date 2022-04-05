package org.churuata.digital;

import java.util.concurrent.TimeUnit;

import org.churuata.digital.core.AuthenticationDispatcher;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.store.SessionStore;
import org.condast.commons.authentication.core.AuthenticationEvent;
import org.condast.commons.authentication.ui.views.AuthenticationGroup;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.ui.entry.AbstractRestEntryPoint;
import org.condast.commons.ui.provider.ICompositeProvider;
import org.condast.commons.ui.session.AbstractSessionHandler;
import org.condast.commons.ui.session.SessionEvent;
import org.condast.commons.ui.utils.RWTUtils;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.StartupParameters;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class LoginEntryPoint extends AbstractRestEntryPoint<SessionStore>{
	private static final long serialVersionUID = 1L;

	private static final String S_CHURUATA_ACTIVE_HOME = "/churuata/active?token=";
	
	private long token;
	private ILoginUser user;
	private boolean loggedIn;
	private SessionHandler session;
	
	private AuthenticationGroup login;
	
	private Dispatcher dispatcher = Dispatcher.getInstance();

	private AuthenticationDispatcher authentication = AuthenticationDispatcher.getInstance(); 
	@Override
	protected Composite createComposite(Composite parent) {
		StartupParameters service = RWT.getClient().getService( StartupParameters.class );
		String tokenstr = service.getParameter(ILoginUser.Attributes.TOKEN.name().toLowerCase());
		token = Long.parseLong(tokenstr);
		ICompositeProvider<Composite> provider = dispatcher.getComposite(BasicApplication.Pages.LOGIN.name().toLowerCase());
		login = (AuthenticationGroup) provider.getComposite(parent, SWT.NONE);
		session = new SessionHandler(login.getDisplay());
		return login;
	}

	@Override
	protected boolean prepare(Composite parent) {
		authentication.getLoginProvider().addAuthenticationListener( e->onAuthenticationEvent(e));
		return true;
	}

	private void onAuthenticationEvent( AuthenticationEvent event ) {
		SessionStore store = getData();
		if( store == null )
			return;
		switch( event.getEvent()) {
		case LOGIN:
			user = event.getUser();
			//store.user.setToken(token);
			store.setLoginUser(user);
			authentication.putUser(user, store);
			String next = S_CHURUATA_ACTIVE_HOME + token;
			RWTUtils.redirect(next);
			break;
		default:
			store.setLoginUser( null );
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
			session.addData(user);
			super.handleTimer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void close() {
		authentication.getLoginProvider().addAuthenticationListener( e->onAuthenticationEvent(e));
		super.close();
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