package org.churuata.digital.entries;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import org.churuata.digital.core.AbstractWizardEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries.Pages;
import org.churuata.digital.core.data.ChuruataOrganisationData;
import org.churuata.digital.core.data.ProfileData;
import org.churuata.digital.core.location.IChuruataService;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.image.ChuruataImages;
import org.churuata.digital.ui.views.OrganisationComposite;
import org.condast.commons.authentication.core.LoginData;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.messaging.core.util.NodeData;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.na.data.AddressData;
import org.condast.commons.na.data.ContactPersonData;
import org.condast.commons.na.profile.IProfileData;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.image.DashboardImages;
import org.condast.commons.ui.image.IImageProvider.ImageSize;
import org.condast.commons.ui.messaging.jump.JumpController;
import org.condast.commons.ui.messaging.jump.JumpController.Operations;
import org.condast.commons.ui.messaging.jump.JumpEvent;
import org.condast.commons.ui.messaging.jump.NodeJumpEvent;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.StartupParameters;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class OrganisationEntryPoint extends AbstractWizardEntryPoint<OrganisationComposite, ChuruataOrganisationData> {
	private static final long serialVersionUID = 1L;

	public static final String S_ADD_ORGANISATION = "Add Organisation";
	public static final String S_CHURUATA = "Churuata-Digital";

	private OrganisationComposite organisationComposite;
	private Button btnLocate;

	private WebController controller;

	private IEditListener<ChuruataOrganisationData> listener = e->onOrganisationEvent(e);

	private Logger logger = Logger.getLogger(this.getClass().getName());

	
	public OrganisationEntryPoint() {
		super(S_ADD_ORGANISATION);
	}

	@Override
	protected SessionStore createSessionStore() {
		StartupParameters service = RWT.getClient().getService( StartupParameters.class );
		IDomainProvider<SessionStore> domain = Dispatcher.getDomainProvider( service );
		return ( domain == null )? null: domain.getData();
	}

	@Override
	protected OrganisationComposite onCreateComposite(Composite parent, int style) {
        parent.setLayout(new GridLayout( 1, false ));
        organisationComposite = new OrganisationComposite( parent, SWT.NONE);
 		organisationComposite.setData( RWT.CUSTOM_VARIANT, S_CHURUATA );
 		organisationComposite.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, false));		
		return organisationComposite;
	}

	
	@Override
	protected void onSetupButtonBar(Group buttonBar) {
		ChuruataImages images = ChuruataImages.getInstance();
		Button button = getBtnNext();
		button.setEnabled(false);
		button.setImage( images.getImage( ChuruataImages.Images.CHECK));
		
		btnLocate = new Button(buttonBar, SWT.NONE);
		btnLocate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		btnLocate.setImage( DashboardImages.getImage( DashboardImages.Images.LOCATE, ImageSize.NORMAL));
		btnLocate.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(final SelectionEvent e) {
				try{
					SessionStore store = getSessionStore();
					ILoginUser user = store.getLoginUser();
					JumpController<ChuruataOrganisationData> jc = new JumpController<>();
					ProfileData profile= store.getData();
					ChuruataOrganisationData org = (ChuruataOrganisationData) profile.getOrganisation()[0];
					JumpController.Operations operation = ( user==null)?Operations.CREATE: Operations.UPDATE;
					jc.jump( new JumpEvent<ChuruataOrganisationData>( this, Pages.ORGANISATION.name(), store.getToken(), Pages.LOCATION.toPath(), operation, org));
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
				super.widgetSelected(e);
			}
		});
		super.onSetupButtonBar(buttonBar);
	}

	@Override
	protected boolean onPostProcess(String context, SessionStore store) {
		controller = new WebController(context, IRestPages.Pages.ORGANISATION);
		this.organisationComposite.addEditListener(listener);
		
		ChuruataOrganisationData organisation = null;
		if( store.getData() != null ) {
			ProfileData profile= store.getData();
			organisation = (ChuruataOrganisationData) profile.getOrganisation()[0];
			setCache(organisation);
			this.organisationComposite.setInput( organisation, true);
		}
		
		JumpController<?> jc = new JumpController<>();
		JumpEvent<?> event = jc.getEvent( Pages.ORGANISATION.toPath());
		if( event != null ) {
			Pages source = Pages.valueOf(event.getIdentifier());
			switch( source ) {
			case ADDRESS:
				ILoginUser user = store.getLoginUser();
				controller.setAddress(user, organisation, (AddressData) event.getData());
				break;
			default:
				break;
			}
		}
		return true;
	}

	@Override
	protected void onButtonPressed(ChuruataOrganisationData data, SessionStore store) {
		try{
			if( store.getData() == null )
				return;
			JumpEvent<ChuruataOrganisationData> event = null;//getEvent();
			if(( event != null ) && ( JumpController.Operations.UPDATE.equals(event.getOperation()))) {
				controller.update(store.getLoginUser(), data);
			}else {
				ProfileData profile= store.getData();
				ChuruataOrganisationData organisation = (ChuruataOrganisationData) profile.getOrganisation()[0];
				if( store.getData().getId() <= 0 )
					controller.register(profile.getId(), organisation);
				else {
					ILoginUser user = store.getLoginUser();
					controller.update(user, organisation);
				}
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
	}

	protected void onOrganisationEvent( EditEvent<ChuruataOrganisationData> event ) {
		SessionStore store = super.getSessionStore();
		IProfileData profile = store.getData();

		ChuruataOrganisationData organisation = event.getData();
		organisation.setContact((ContactPersonData) profile); 
		switch( event.getType()) {
		case ADDED:
			profile.addOrganisation(organisation);
			JumpController<NodeData<ChuruataOrganisationData, IChuruataService>> jc = new JumpController<>();
			jc.jump( new NodeJumpEvent<ChuruataOrganisationData,IChuruataService>( this, Pages.ORGANISATION.name(), store.getToken(), Pages.SERVICES.toPath(), JumpController.Operations.CREATE, organisation, null));
			break;
		case COMPLETE:
			profile.addOrganisation(organisation);
			Button button = super.getBtnNext();
			button.setEnabled(( event.getData() != null ));
			break;
		default:
			break;
		}
	}
	
	@Override
	public void close() {
		this.organisationComposite.removeEditListener(listener);
		super.close();
	}
	
	private class WebController extends AbstractHttpRequest<ChuruataOrganisationData.Requests>{

		public WebController( String context, IRestPages.Pages page) {
			super( context, page.toPath());
		}

		public void register( long personId, ChuruataOrganisationData organisation ) {
			Map<String, String> params = super.getParameters();
			params.put(ProfileData.Parameters.PERSON_ID.toString(), String.valueOf( personId ));
			Gson gson = new Gson();
			String data = gson.toJson(organisation, ChuruataOrganisationData.class);
			try {
				sendPut(ChuruataOrganisationData.Requests.REGISTER, params, data );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		public void update(ILoginUser user, ChuruataOrganisationData organisation) {
			Map<String, String> params = super.getParameters();
			params.put( LoginData.Parameters.USER_ID.toString(), String.valueOf( user.getId()));
			params.put( LoginData.Parameters.SECURITY.toString(), String.valueOf( user.getSecurity() ));
			params.put( ChuruataOrganisationData.Parameters.ORGANISATION_ID.toString(), String.valueOf( organisation.getId() ));
			GsonBuilder builder = new GsonBuilder();
			builder.enableComplexMapKeySerialization();
			Gson gson = builder.create();
			ChuruataOrganisationData temp = new ChuruataOrganisationData(organisation);
			try {
				String data = gson.toJson(temp, ChuruataOrganisationData.class);
				sendPut(ChuruataOrganisationData.Requests.UPDATE, params, data );
			} catch (Exception e) {
				logger.warning(e.getMessage());
			}
		}

		public void setAddress(ILoginUser user, ChuruataOrganisationData organisation, AddressData address) {
			Map<String, String> params = super.getParameters();
			try {
				params.put(ChuruataOrganisationData.Parameters.USER_ID.toString(), String.valueOf( user.getId()));
				params.put(ChuruataOrganisationData.Parameters.SECURITY.toString(), String.valueOf( user.getSecurity() ));
				params.put(ChuruataOrganisationData.Parameters.ORGANISATION_ID.toString(), String.valueOf( organisation.getId()));
				Gson gson = new Gson();
				String data = gson.toJson(address, AddressData.class);
				sendPut(ChuruataOrganisationData.Requests.SET_ADDRESS, params, data );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		@Override
		protected String onHandleResponse(ResponseEvent<ChuruataOrganisationData.Requests> event) throws IOException {
			try {
				SessionStore store = getSessionStore();
				Gson gson = new Gson();
				ProfileData profile= store.getData();
				ChuruataOrganisationData data = (ChuruataOrganisationData) profile.getOrganisation()[0];
				JumpController<ChuruataOrganisationData> jc = new JumpController<>();
				switch( event.getRequest()){
				case REGISTER:
					data = gson.fromJson(event.getResponse(), ChuruataOrganisationData.class);
					jc.jump( new JumpEvent<ChuruataOrganisationData>( this, Pages.ORGANISATION.name(), store.getToken(), Pages.ACTIVE.toPath(), JumpController.Operations.DONE, data));			
					break;
				case UPDATE:
					data = gson.fromJson(event.getResponse(), ChuruataOrganisationData.class);
					getBtnNext().setEnabled(false);
					break;
				case REMOVE_SERVICES:
					data = gson.fromJson(event.getResponse(), ChuruataOrganisationData.class);
					organisationComposite.setInput(data, true);
					break;
				case SET_ADDRESS:
					data = gson.fromJson(event.getResponse(), ChuruataOrganisationData.class);
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