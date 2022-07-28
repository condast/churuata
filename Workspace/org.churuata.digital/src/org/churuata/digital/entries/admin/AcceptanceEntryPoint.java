package org.churuata.digital.entries.admin;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import org.churuata.digital.core.AbstractWizardEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries;
import org.churuata.digital.core.Entries.Pages;
import org.churuata.digital.core.data.ChuruataOrganisationData;
import org.churuata.digital.core.model.IOrganisation;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.organisation.OrganisationsTableViewer;
import org.condast.commons.Utils;
import org.condast.commons.authentication.core.LoginData;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.na.data.OrganisationData;
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

public class AcceptanceEntryPoint extends AbstractWizardEntryPoint<OrganisationsTableViewer, ChuruataOrganisationData>{
	private static final long serialVersionUID = 1L;

	public static final String S_ADD_ACCOUNT = "Add Account";

	private OrganisationsTableViewer acceptTableViewer;

	private IEditListener<ChuruataOrganisationData> listener = e->onOrganisationEvent(e);

	private WebController controller;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public AcceptanceEntryPoint() {
		super(S_ADD_ACCOUNT);
	}

	@Override
	protected SessionStore createSessionStore() {
		StartupParameters service = RWT.getClient().getService( StartupParameters.class );
		IDomainProvider<SessionStore> domain = Dispatcher.getDomainProvider( service );
		return ( domain == null )? null: domain.getData();
	}

	@Override
	protected OrganisationsTableViewer onCreateComposite(Composite parent, int style) {
		acceptTableViewer = new OrganisationsTableViewer(parent, SWT.NONE );
		acceptTableViewer.setData( RWT.CUSTOM_VARIANT, Entries.S_CHURUATA );
		acceptTableViewer.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ));
		acceptTableViewer.addEditListener(listener);
		return acceptTableViewer;
	}

	@Override
	protected boolean onPostProcess(String context, ChuruataOrganisationData data, SessionStore store) {
		controller = new WebController( store.getLoginUser());
		controller.setInput(context, IRestPages.Pages.ORGANISATION.toPath());
		controller.getAll( IOrganisation.Verification.ALL);
		return true;
	}

	protected void onOrganisationEvent( EditEvent<ChuruataOrganisationData> event ) {
		ChuruataOrganisationData data = (ChuruataOrganisationData) event.getData();
		switch( event.getType()) {
		case CHANGED:
			Button btnNext = super.getBtnNext();
			btnNext.setEnabled(( data != null ));
			if( data != null )
				controller.setVerified(data);
			break;
		case DELETE:
			controller.removeAll(event.getBatch());
			break;
		default:
			break;
		}
	}

	@Override
	protected void onButtonPressed(ChuruataOrganisationData data, SessionStore store) {
		try{
			Dispatcher.jump( Pages.ACTIVE, store.getToken());						
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
	}

	@Override
	protected void onHandleTimer(SessionEvent<ChuruataOrganisationData> event) {
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


	private class WebController extends AbstractHttpRequest<ChuruataOrganisationData.Requests>{
		
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
			params.put(ChuruataOrganisationData.Parameters.VERIFIED.toString(), verify.name());
			try {
				sendGet(ChuruataOrganisationData.Requests.GET_ALL, params );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		public void setVerified( IOrganisation organisation ) {
			Map<String, String> params = super.getParameters();
			params.put( LoginData.Parameters.USER_ID.toString(), String.valueOf( user.getId()));
			params.put( LoginData.Parameters.SECURITY.toString(), String.valueOf( user.getSecurity()));
			params.put(ChuruataOrganisationData.Parameters.ORGANISATION_ID.toString(), String.valueOf( organisation.getId()));
			params.put(ChuruataOrganisationData.Parameters.VERIFIED.toString(), String.valueOf( organisation.isVerified()));
			try {
				sendGet(ChuruataOrganisationData.Requests.SET_VERIFIED, params );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		public void removeAll( Collection<ChuruataOrganisationData> organisations ) {
			if( Utils.assertNull(organisations))
				return;
			Map<String, String> params = super.getParameters();
			params.put( LoginData.Parameters.USER_ID.toString(), String.valueOf( user.getId()));
			params.put( LoginData.Parameters.SECURITY.toString(), String.valueOf( user.getSecurity()));
			Gson gson = new Gson();
			String data = gson.toJson( OrganisationData.getIDs(organisations), long[].class );
			try {
				sendDelete(ChuruataOrganisationData.Requests.REMOVE_ORGANISATIONS, params, data );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		@Override
		protected String onHandleResponse(ResponseEvent<ChuruataOrganisationData.Requests> event) throws IOException {
			try {
				Gson gson = new Gson();
				switch( event.getRequest()){
				case GET_ALL:
					ChuruataOrganisationData[] data = gson.fromJson(event.getResponse(), ChuruataOrganisationData[].class);
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
		protected void onHandleResponseFail(HttpStatus status, ResponseEvent<ChuruataOrganisationData.Requests> event) throws IOException {
			super.onHandleResponseFail(status, event);
		}
	
	}
}