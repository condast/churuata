package org.churuata.digital.entries.admin;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import org.churuata.digital.core.AbstractWizardEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries.Pages;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.session.SessionStore;
import org.condast.commons.authentication.core.AdminData;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.authentication.ui.views.AdminWidget;
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

public class EditAdminEntryPoint extends AbstractWizardEntryPoint<AdminWidget, AdminData>{
	private static final long serialVersionUID = 1L;

	public static final String S_ADD_ACCOUNT = "Add Account";

	private AdminWidget adminWidget;

	private IEditListener<AdminData> listener = e->onOrganisationEvent(e);

	private WebController controller;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	@SuppressWarnings("unchecked")
	@Override
	protected IDomainProvider<SessionStore<AdminData>> getDomainProvider(StartupParameters service) {
		IDomainProvider<?> domain = Dispatcher.getDomainProvider(service);
		return (IDomainProvider<SessionStore<AdminData>>) domain;
	}

	@Override
	protected void onButtonPressed(AdminData data, SessionStore<AdminData> store) {
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
	protected AdminWidget onCreateComposite(Composite parent, int style) {
		adminWidget = new AdminWidget(parent, SWT.NONE );
		adminWidget.setData( RWT.CUSTOM_VARIANT, S_CHURUATA );
		adminWidget.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ));
		adminWidget.addEditListener(listener);
		return adminWidget;
	}

	@Override
	protected boolean onPostProcess(String context, AdminData data, SessionStore<AdminData> store) {
		controller = new WebController( store.getLoginUser());
		controller.setInput(context, IRestPages.Pages.ADMIN.toPath());
		controller.getAll( IAdmin.Roles.UNKNOWN);
		return true;
	}

	protected void onOrganisationEvent( EditEvent<AdminData> event ) {
		ContactPersonData data = null;
		SessionStore<AdminData> store = super.getSessionStore();
		switch( event.getType()) {
		case SELECTED:
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
	protected void onHandleTimer(SessionEvent<AdminData> event) {
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

		public void getAll( IAdmin.Roles role ) {
			Map<String, String> params = super.getParameters();
			params.put( AdminData.Parameters.USER_ID.toString(), String.valueOf( user.getId()));
			params.put( AdminData.Parameters.SECURITY.toString(), String.valueOf( user.getSecurity()));
			params.put( AdminData.Parameters.ROLE.toString(), role.name());
			try {
				sendGet(AdminData.Requests.SET_ROLE, params );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		@Override
		protected String onHandleResponse(ResponseEvent<AdminData.Requests> event) throws IOException {
			try {
				SessionStore<AdminData> store = getSessionStore();
				Gson gson = new Gson();
				switch( event.getRequest()){
				case SET_ROLE:
					AdminData data = gson.fromJson(event.getResponse(), AdminData.class);
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