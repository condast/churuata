package org.churuata.digital.entries.admin;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.churuata.digital.core.AbstractWizardEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries.Pages;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.session.SessionStore;
import org.condast.commons.authentication.core.AdminData;
import org.condast.commons.authentication.core.LoginData;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.authentication.ui.views.AdminWidget;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.session.SessionEvent;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.StartupParameters;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class EditAdminEntryPoint extends AbstractWizardEntryPoint<AdminWidget, LoginData>{
	private static final long serialVersionUID = 1L;

	public static final String S_ADD_ACCOUNT = "Add Account";

	private AdminWidget adminWidget;

	private IEditListener<LoginData> listener = e->onOrganisationEvent(e);

	private WebController controller;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	@SuppressWarnings("unchecked")
	@Override
	protected IDomainProvider<SessionStore<LoginData>> getDomainProvider(StartupParameters service) {
		IDomainProvider<?> domain = Dispatcher.getDomainProvider(service);
		return (IDomainProvider<SessionStore<LoginData>>) domain;
	}

	@Override
	protected void onButtonPressed(LoginData data, SessionStore<LoginData> store) {
		try{
			Dispatcher.jump( Pages.ADMIN, store.getToken());						
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
	}

	@Override
	protected AdminWidget onCreateComposite(Composite parent, int style) {
		adminWidget = new AdminWidget(parent, SWT.NONE );
		adminWidget.setData( RWT.CUSTOM_VARIANT, S_CHURUATA );
		adminWidget.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ));
		adminWidget.addEditListener(listener);
		return adminWidget;
	}

	@Override
	protected boolean onPostProcess(String context, LoginData data, SessionStore<LoginData> store) {
		HttpSession session = RWT.getUISession().getHttpSession();
		LoginData client = (LoginData) session.getAttribute(AdminData.Parameters.LOGIN_USER.name());
		adminWidget.setInput(client, true);
		controller = new WebController( store.getLoginUser());
		controller.setInput(context, IRestPages.Pages.ADMIN.toPath());
		return true;
	}

	protected void onOrganisationEvent( EditEvent<LoginData> event ) {
		switch( event.getType()) {
		case CHANGED:
			Button btnNext = super.getBtnNext();
			btnNext.setEnabled(true);
			controller.setRole(event.getData());
			break;
		default:
			break;
		}
	}

	@Override
	protected void onHandleTimer(SessionEvent<LoginData> event) {
		try {
			//acceptTableViewer.refresh();
			super.handleTimer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	@Override
	public void close() {
		this.adminWidget.removeEditListener(listener);
		super.close();
	}


	private class WebController extends AbstractHttpRequest<AdminData.Requests>{
		
		private ILoginUser user;
		
		public WebController( ILoginUser user ) {
			super();
			this.user = user;
		}

		public void setInput(String context, String path) {
			super.setContextPath(context + path);
		}

		public void setRole( LoginData client ) {
			Map<String, String> params = super.getParameters();
			params.put( LoginData.Parameters.USER_ID.toString(), String.valueOf( user.getId()));
			params.put( LoginData.Parameters.SECURITY.toString(), String.valueOf( user.getSecurity()));
			params.put( AdminData.Parameters.LOGIN_USER.toString(), String.valueOf( client.getId() ));
			params.put( AdminData.Parameters.ROLE.toString(), client.getRole().name());
			try {
				sendGet(AdminData.Requests.SET_ROLE, params );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		@Override
		protected String onHandleResponse(ResponseEvent<AdminData.Requests> event) throws IOException {
			try {
				Gson gson = new Gson();
				switch( event.getRequest()){
				case SET_ROLE:
					LoginData data = gson.fromJson(event.getResponse(), LoginData.class);
					adminWidget.setInput( data, true );
					break;
				default:
					break;
				}
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
			finally {
			}
			return null;
		}

		@Override
		protected void onHandleResponseFail(HttpStatus status, ResponseEvent<AdminData.Requests> event) throws IOException {
			super.onHandleResponseFail(status, event);
		}
	
	}
}