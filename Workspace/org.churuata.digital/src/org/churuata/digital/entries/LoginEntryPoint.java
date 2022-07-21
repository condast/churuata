package org.churuata.digital.entries;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.churuata.digital.BasicApplication;
import org.churuata.digital.core.AbstractChuruataEntryPoint;
import org.churuata.digital.core.AuthenticationDispatcher;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries;
import org.churuata.digital.core.data.ChuruataOrganisationData;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.session.SessionStore;
import org.condast.commons.authentication.core.LoginData;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.authentication.ui.views.AuthenticationGroup;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.authentication.utils.AuthenticationUtils;
import org.condast.commons.config.Config;
import org.condast.commons.legal.LegalUtils;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.session.AbstractSessionHandler;
import org.condast.commons.ui.session.SessionEvent;
import org.condast.commons.ui.utils.RWTUtils;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class LoginEntryPoint extends AbstractChuruataEntryPoint<ChuruataOrganisationData>{
	private static final long serialVersionUID = 1L;

	private static final String S_CHURUATA_HOME = "/" + S_CHURUATA + "/home";
	private static final String S_CHURUATA_ACTIVE_HOME = "/" + S_CHURUATA + "/active";
	private static final String S_TOKEN_ARG = "?token=";
	
	private AuthenticationGroup group;

	private IEditListener<LoginData> elistener = e -> onEditEvent(e); 

	private Logger logger = Logger.getLogger( this.getClass().getName());
	
	private AuthenticationController controller;
	private SessionHandler handler;
	
	@Override
	protected Composite createComposite(Composite parent) {
		Config config = Config.getInstance();
		group = new AuthenticationGroup(parent, SWT.NONE);
		group.setData( RWT.CUSTOM_VARIANT, BasicApplication.S_CHURUATA_VARIANT );
		String path = LegalUtils.createLegalPath(config.getServerContext(), S_CHURUATA, "privacy");
		group.setPrivacyPath( path);
		path = LegalUtils.createLegalPath(config.getServerContext(), S_CHURUATA, "tos");
		group.setLicensePath( path);
		group.addEditListener(elistener);
		return group;
	}

	@Override
	protected boolean prepare(Composite parent) {
		handler = new SessionHandler( parent.getDisplay());
		setData(null);
		Config config = Config.getInstance();
		String context = config.getServerContext();
		controller = new AuthenticationController( context + IRestPages.Pages.AUTH.toPath());
		return true;
	}
	
	@Override
	protected void createTimer(boolean create, int nrOfThreads, TimeUnit unit, int startTime, int rate) {
		super.createTimer(true, nrOfThreads, unit, startTime, rate);
	}
	
	@Override
	protected void handleTimer() {
		handler.addData(getSessionStore());
		super.handleTimer();
	}

	private void onEditEvent(EditEvent<LoginData> e) {
		switch( e.getType()){
		case CUSTOM:	
			RWTUtils.redirect( Entries.Pages.GET_EMAIL.toPath());
			break;
		case COMPLETE:
			try {
				LoginData data = group.getInput(); 
				LoginData.Requests request = data.isRegister()?LoginData.Requests.REGISTER: LoginData.Requests.LOGIN;
				this.controller.request( request, data );
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void close() {
		group.removeEditListener(elistener);
		super.close();
	}

	@Override
	protected boolean handleSessionTimeout(boolean reload) {
		SessionStore<ChuruataOrganisationData> store = getSessionStore();
		store.clear();
		Dispatcher.redirect( BasicApplication.S_CHURUATA, getToken());
		return true;
	}

	private class SessionHandler extends AbstractSessionHandler<SessionStore<ChuruataOrganisationData>>{

		protected SessionHandler(Display display) {
			super(display);
		}

		@Override
		protected void onHandleSession(SessionEvent<SessionStore<ChuruataOrganisationData>> sevent) {
			group.refresh();
		}
	}
	
	private class AuthenticationController extends AbstractHttpRequest<LoginData.Requests>{

		public void request(LoginData.Requests request, LoginData data ) throws IOException {
			Map<String, String> parameters = getParameters();
			parameters.put(LoginData.Parameters.NAME.toString(), data.getNickName());
			parameters.put(LoginData.Parameters.PASSWORD.toString(), data.getPassword());
			parameters.put(LoginData.Parameters.EMAIL.toString(), data.getEmail());
			parameters.put(IDomainProvider.Attributes.DOMAIN.name().toLowerCase(), S_CHURUATA );
			sendGet( request, parameters);	
		}
		
		public AuthenticationController(String path) {
			super(path);
		}

		@Override
		protected String onHandleResponse(ResponseEvent<LoginData.Requests> event) throws IOException {			
			String str = event.getResponse();
			Gson gson = new Gson();
			AuthenticationUtils.Dictionary login = gson.fromJson(str, AuthenticationUtils.Dictionary.class);
			switch( event.getRequest()){
			case REGISTER:
			case LOGIN:
				AuthenticationDispatcher authentication = AuthenticationDispatcher.getInstance();
				ILoginUser user = authentication.getLoginUser(login.getKey(), login.getValue());
				Dispatcher dispatcher = Dispatcher.getInstance();
				IDomainProvider<SessionStore<ChuruataOrganisationData>> domain = dispatcher.getDomain(user.getId(), user.getSecurity());
				if( domain == null )
					break;
				long token = domain.getToken();
				String next = ( user == null )?S_CHURUATA_HOME: S_CHURUATA_ACTIVE_HOME;
				next += S_TOKEN_ARG + token;
				logger.info("Redirecting; " + next );
				Dispatcher.redirect( next, token);
				handler.addData(getSessionStore());
				break;
			default:
				break;
			}
			return event.getResponse();
		}		
	}
}