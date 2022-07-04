package org.churuata.digital.entries.admin;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.churuata.digital.core.AbstractWizardEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries.Pages;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.admin.AdminTableViewer;
import org.condast.commons.authentication.core.AdminData;
import org.condast.commons.authentication.core.LoginData;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.authentication.user.IAdmin;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.na.data.ContactPersonData;
import org.condast.commons.na.data.PersonData;
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

public class AdminEntryPoint extends AbstractWizardEntryPoint<AdminTableViewer, LoginData>{
	private static final long serialVersionUID = 1L;

	public static final String S_ADD_ACCOUNT = "Add Account";

	private AdminTableViewer adminTableViewer;

	private IEditListener<LoginData> listener = e->onOrganisationEvent(e);

	private WebController controller;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	
	public AdminEntryPoint() {
		super( false );
	}

	@SuppressWarnings("unchecked")
	@Override
	protected IDomainProvider<SessionStore<LoginData>> getDomainProvider(StartupParameters service) {
		IDomainProvider<?> domain = Dispatcher.getDomainProvider(service);
		return (IDomainProvider<SessionStore<LoginData>>) domain;
	}

	@Override
	protected void onButtonPressed(LoginData data, SessionStore<LoginData> store) {
		try{
			if( store.getContactPersonData() == null )
				return;
			PersonData person = store.getPersonData();
			//if( person == null ) 				
				//controller.getAll( store.getContactPersonData());
			//else
			//	Dispatcher.jump( Pages.ORGANISATION, store.getToken());						
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
	}

	@Override
	protected AdminTableViewer onCreateComposite(Composite parent, int style) {
		adminTableViewer = new AdminTableViewer(parent, SWT.NONE );
		adminTableViewer.setData( RWT.CUSTOM_VARIANT, S_CHURUATA );
		adminTableViewer.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ));
		adminTableViewer.addEditListener(listener);
		return adminTableViewer;
	}

	@Override
	protected boolean onPostProcess(String context, LoginData data, SessionStore<LoginData> store) {
		controller = new WebController( store.getLoginUser());
		controller.setInput(context, IRestPages.Pages.ADMIN.toPath());
		controller.getAll( IAdmin.Roles.UNKNOWN);
		return true;
	}

	protected void onOrganisationEvent( EditEvent<LoginData> event ) {
		ContactPersonData data = null;
		SessionStore<LoginData> store = super.getSessionStore();
		switch( event.getType()) {
		case SELECTED:
			HttpSession session = RWT.getUISession().getHttpSession();
			session.setAttribute(AdminData.Parameters.LOGIN_USER.name(), event.getData());
			Dispatcher.jump( Pages.EDIT_ADMIN, store.getToken());
			break;
		case COMPLETE:
			//data = event.getData();
			store.setContactPersonData(data);
			Button btnNext = super.getBtnNext();
			btnNext.setEnabled(( data != null ));
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
		this.adminTableViewer.removeEditListener(listener);
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

		public void getAll( IAdmin.Roles role ) {
			Map<String, String> params = super.getParameters();
			params.put( LoginData.Parameters.USER_ID.toString(), String.valueOf( user.getId()));
			params.put( LoginData.Parameters.SECURITY.toString(), String.valueOf( user.getSecurity()));
			params.put( AdminData.Parameters.ROLE.toString(), role.name());
			try {
				sendGet(AdminData.Requests.GET_ALL, params );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		@Override
		protected String onHandleResponse(ResponseEvent<AdminData.Requests> event) throws IOException {
			try {
				SessionStore<LoginData> store = getSessionStore();
				Gson gson = new Gson();
				switch( event.getRequest()){
				case GET_ALL:
					LoginData[] data = gson.fromJson(event.getResponse(), LoginData[].class);
					adminTableViewer.setInput( Arrays.asList(data));
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