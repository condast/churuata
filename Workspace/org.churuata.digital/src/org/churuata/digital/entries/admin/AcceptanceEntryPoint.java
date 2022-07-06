package org.churuata.digital.entries.admin;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;

import org.churuata.digital.core.AbstractWizardEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries.Pages;
import org.churuata.digital.core.data.OrganisationData;
import org.churuata.digital.core.model.IOrganisation;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.organisation.AcceptOrganisationTableViewer;
import org.condast.commons.authentication.core.LoginData;
import org.condast.commons.authentication.http.IDomainProvider;
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

public class AcceptanceEntryPoint extends AbstractWizardEntryPoint<AcceptOrganisationTableViewer, OrganisationData>{
	private static final long serialVersionUID = 1L;

	public static final String S_ADD_ACCOUNT = "Add Account";

	private AcceptOrganisationTableViewer acceptTableViewer;

	private IEditListener<OrganisationData> listener = e->onOrganisationEvent(e);

	private WebController controller;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	protected IDomainProvider<SessionStore<OrganisationData>> getDomainProvider(StartupParameters service) {
		return Dispatcher.getDomainProvider(service);
	}

	@Override
	protected AcceptOrganisationTableViewer onCreateComposite(Composite parent, int style) {
		acceptTableViewer = new AcceptOrganisationTableViewer(parent, SWT.NONE );
		acceptTableViewer.setData( RWT.CUSTOM_VARIANT, S_CHURUATA );
		acceptTableViewer.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ));
		acceptTableViewer.addEditListener(listener);
		return acceptTableViewer;
	}

	@Override
	protected boolean onPostProcess(String context, OrganisationData data, SessionStore<OrganisationData> store) {
		controller = new WebController( store.getLoginUser());
		controller.setInput(context, IRestPages.Pages.ORGANISATION.toPath());
		controller.getAll( IOrganisation.Verification.ALL);
		return true;
	}

	protected void onOrganisationEvent( EditEvent<OrganisationData> event ) {
		OrganisationData data = event.getData();
		switch( event.getType()) {
		case CHANGED:
			Button btnNext = super.getBtnNext();
			btnNext.setEnabled(( data != null ));
			if( data != null )
				controller.setVerified(data);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onButtonPressed(OrganisationData data, SessionStore<OrganisationData> store) {
		try{
			Dispatcher.jump( Pages.ACTIVE, store.getToken());						
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
	}

	@Override
	protected void onHandleTimer(SessionEvent<OrganisationData> event) {
		try {
			//acceptTableViewer.refresh();
			super.handleTimer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		this.acceptTableViewer.removeEditListener(listener);
		super.close();
	}


	private class WebController extends AbstractHttpRequest<OrganisationData.Requests>{
		
		private ILoginUser user;
		
		public WebController( ILoginUser user ) {
			super();
			this.user = user;
		}

		public void setInput(String context, String path) {
			super.setContextPath(context + path);
		}

		public void getAll( IOrganisation.Verification verify ) {
			Map<String, String> params = super.getParameters();
			params.put( LoginData.Parameters.USER_ID.toString(), String.valueOf( user.getId()));
			params.put( LoginData.Parameters.SECURITY.toString(), String.valueOf( user.getSecurity()));
			params.put(OrganisationData.Parameters.VERIFIED.toString(), verify.name());
			try {
				sendGet(OrganisationData.Requests.GET_ALL, params );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		public void setVerified( IOrganisation organisation ) {
			Map<String, String> params = super.getParameters();
			params.put( LoginData.Parameters.USER_ID.toString(), String.valueOf( user.getId()));
			params.put( LoginData.Parameters.SECURITY.toString(), String.valueOf( user.getSecurity()));
			params.put(OrganisationData.Parameters.ORGANISATION_ID.toString(), String.valueOf( organisation.getId()));
			params.put(OrganisationData.Parameters.VERIFIED.toString(), String.valueOf( organisation.isVerified()));
			try {
				sendGet(OrganisationData.Requests.SET_VERIFIED, params );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		@Override
		protected String onHandleResponse(ResponseEvent<OrganisationData.Requests> event) throws IOException {
			try {
				Gson gson = new Gson();
				switch( event.getRequest()){
				case GET_ALL:
					OrganisationData[] data = gson.fromJson(event.getResponse(), OrganisationData[].class);
					acceptTableViewer.setInput( Arrays.asList(data));
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
		protected void onHandleResponseFail(HttpStatus status, ResponseEvent<OrganisationData.Requests> event) throws IOException {
			super.onHandleResponseFail(status, event);
		}
	
	}
}