package org.churuata.digital.entries;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.churuata.digital.core.AbstractWizardEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries.Pages;
import org.churuata.digital.core.data.ChuruataOrganisationData;
import org.churuata.digital.core.data.ProfileData;
import org.churuata.digital.core.data.ServiceData;
import org.churuata.digital.core.location.IChuruataService;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.session.SessionStore;
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
	private Button btnAddress;

	private WebController controller;

	private IEditListener<ChuruataOrganisationData> listener = e->onOrganisationEvent(e);
	private IEditListener<IChuruataService> serviceListener = e->onServiceEvent(e);

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
	protected boolean onPrepare(SessionStore store) {
		return true;//always true, because this does not require login
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
		btnAddress = new Button(buttonBar, SWT.NONE);
		btnAddress.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		btnAddress.setImage( DashboardImages.getImage( DashboardImages.Images.LOCATE, ImageSize.NORMAL));
		btnAddress.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(final SelectionEvent e) {
				try{
					SessionStore store = getSessionStore();
					JumpController<AddressData> jc = new JumpController<>();
					jc.jump( new JumpEvent<AddressData>( this, store.getToken(), Pages.ADDRESS.toPath(), JumpController.Operations.CREATE, store.getData().getAddress()));
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
	protected boolean onPostProcess(String context, ChuruataOrganisationData data, SessionStore store) {
		controller = new WebController(context, IRestPages.Pages.ORGANISATION);
		this.organisationComposite.addEditListener(listener);
		this.organisationComposite.addServiceListener(serviceListener);
		
		if( store.getData() != null ) {
			this.organisationComposite.setInput( store.getData(), true);
			Button btnNext = super.getBtnNext();
			btnNext.setEnabled(this.organisationComposite.checkRequiredFields());
		}
		return true;
	}

	@Override
	protected void onButtonPressed(ChuruataOrganisationData data, SessionStore store) {
		try{
			if( store.getData() == null )
				return;
			JumpEvent<ChuruataOrganisationData> event = getEvent();
			if(( event != null ) && ( JumpController.Operations.UPDATE.equals(event.getOperation()))) {
				controller.update(store.getLoginUser(), store.getData());
			}else {
				IProfileData person = store.getProfile();
				if( store.getData().getId() <= 0 )
					controller.register(person.getId(), store.getData());
				else {
					JumpController<NodeData<ChuruataOrganisationData, IChuruataService>> jc = new JumpController<>();
					jc.jump( new NodeJumpEvent<ChuruataOrganisationData,IChuruataService>( this, store.getToken(), Pages.LOCATION.toPath(), JumpController.Operations.UPDATE, store.getData(), null));
				}
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
	}

	protected void onOrganisationEvent( EditEvent<ChuruataOrganisationData> event ) {
		SessionStore store = super.getSessionStore();
		IProfileData person = store.getProfile();

		ChuruataOrganisationData organisation = event.getData();
		organisation.setContact((ContactPersonData) person); 
		switch( event.getType()) {
		case ADDED:
			store.setData( event.getData());
			JumpController<NodeData<ChuruataOrganisationData, IChuruataService>> jc = new JumpController<>();
			jc.jump( new NodeJumpEvent<ChuruataOrganisationData,IChuruataService>( this, store.getToken(), Pages.SERVICES.toPath(), JumpController.Operations.CREATE, organisation, null));
			break;
		case COMPLETE:
			store.setData(event.getData());
			Button button = super.getBtnNext();
			button.setEnabled(( event.getData() != null ));
			break;
		default:
			break;
		}
	}

	protected void onServiceEvent( EditEvent<IChuruataService> event ) {
		try {
			SessionStore store = super.getSessionStore();
			IChuruataService service = event.getData();
			ILoginUser user = store.getLoginUser();
			switch( event.getType()) {
			case SELECTED:
				store.setSelectedService(service);
				store.setData( this.organisationComposite.getInput());
				JumpController<NodeData<ChuruataOrganisationData, IChuruataService>> jc = new JumpController<>();
				jc.jump( new NodeJumpEvent<ChuruataOrganisationData, IChuruataService>( this, store.getToken(), Pages.SERVICES.toPath(), JumpController.Operations.UPDATE, store.getData(), service));			
				break;
			case DELETE:
				controller.remove( user, store.getData(), ServiceData.getIds( event.getBatch() ));
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void createTimer(boolean create, int nrOfThreads, TimeUnit unit, int startTime, int rate) {
		super.createTimer(true, nrOfThreads, unit, startTime, 10000);
	}

	@Override
	public void close() {
		this.organisationComposite.removeServiceListener(serviceListener);
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

		public void remove(ILoginUser user, ChuruataOrganisationData organisation, long[] batch) {
			Map<String, String> params = super.getParameters();
			params.put( LoginData.Parameters.USER_ID.toString(), String.valueOf( user.getId()));
			params.put( LoginData.Parameters.SECURITY.toString(), String.valueOf( user.getSecurity() ));
			params.put( ChuruataOrganisationData.Parameters.ORGANISATION_ID.toString(), String.valueOf( organisation.getId() ));
			Gson gson = new Gson();
			String data = gson.toJson(batch, long[].class);
			try {
				sendDelete(ChuruataOrganisationData.Requests.REMOVE_SERVICES, params, data );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		@Override
		protected String onHandleResponse(ResponseEvent<ChuruataOrganisationData.Requests> event) throws IOException {
			try {
				SessionStore store = getSessionStore();
				Gson gson = new Gson();
				ChuruataOrganisationData data = null;
				JumpController<ChuruataOrganisationData> jc = new JumpController<>();
				switch( event.getRequest()){
				case REGISTER:
					data = gson.fromJson(event.getResponse(), ChuruataOrganisationData.class);
					store.setData(data);
					jc.jump( new JumpEvent<ChuruataOrganisationData>( this, store.getToken(), Pages.ACTIVE.toPath(), JumpController.Operations.DONE, data));			
					break;
				case UPDATE:
					data = gson.fromJson(event.getResponse(), ChuruataOrganisationData.class);
					store.setData(data);
					jc.jump( new JumpEvent<ChuruataOrganisationData>( this, store.getToken(), Pages.ORGANISATIONS.toPath(), JumpController.Operations.DONE, data));			
					break;
				case REMOVE_SERVICES:
					data = gson.fromJson(event.getResponse(), ChuruataOrganisationData.class);
					store.setData(data);
					organisationComposite.setInput(data, true);
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