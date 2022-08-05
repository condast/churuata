package org.churuata.digital.entries;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import org.churuata.digital.core.AbstractWizardEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries;
import org.churuata.digital.core.Entries.Pages;
import org.churuata.digital.core.data.ChuruataOrganisationData;
import org.churuata.digital.core.data.ProfileData;
import org.churuata.digital.core.data.ServiceData;
import org.churuata.digital.core.location.IChuruataService;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.image.ChuruataImages;
import org.churuata.digital.ui.image.ChuruataImages.Images;
import org.churuata.digital.ui.views.ServiceComposite;
import org.condast.commons.authentication.core.LoginData;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.messaging.core.util.NodeData;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.na.data.AddressData;
import org.condast.commons.na.model.IAddress;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.image.DashboardImages;
import org.condast.commons.ui.image.IImageProvider.ImageSize;
import org.condast.commons.ui.messaging.jump.JumpController;
import org.condast.commons.ui.messaging.jump.JumpEvent;
import org.condast.commons.ui.messaging.jump.NodeJumpEvent;
import org.condast.commons.ui.messaging.jump.JumpController.Operations;
import org.condast.commons.ui.na.images.NAImages;
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
import com.google.gson.JsonSyntaxException;

public class ServiceEntryPoint extends AbstractWizardEntryPoint<ServiceComposite, IChuruataService> {
	private static final long serialVersionUID = 1L;

	public static final String S_ADD_SERVICE = "Add Service";

	private ServiceComposite servicesComposite;

	private JumpEvent<NodeData<?, ?>> event;
	private WebController controller;

	private Button btnLocate;
	private Button btnAddress;

	private IEditListener<IChuruataService> listener = e->onServiceEvent(e);

	private Logger logger = Logger.getLogger(this.getClass().getName());

	
	public ServiceEntryPoint() {
		super(S_ADD_SERVICE);
	}

	@Override
	protected SessionStore createSessionStore() {
		StartupParameters service = RWT.getClient().getService( StartupParameters.class );
		IDomainProvider<SessionStore> domain = Dispatcher.getDomainProvider( service );
		return ( domain == null )? null: domain.getData();
	}

	@Override
	protected ServiceComposite onCreateComposite(Composite parent, int style) {
        parent.setLayout(new GridLayout( 1, false ));
        servicesComposite = new ServiceComposite( parent, SWT.NONE);
 		servicesComposite.setData( RWT.CUSTOM_VARIANT, Entries.S_CHURUATA );
 		servicesComposite.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, false));
 		return servicesComposite;
    }

	@Override
	protected void onSetupButtonBar(Group buttonBar) {
		ChuruataImages images = ChuruataImages.getInstance();
		Button btnAdd = getBtnNext();
		JumpController.Operations operation = (event==null)?JumpController.Operations.UPDATE: event.getOperation();
		Images image =  JumpController.Operations.CREATE.equals( operation )?Images.ADD: Images.CHECK; 
		btnAdd.setImage( images.getImage(image));

		btnLocate = new Button(buttonBar, SWT.NONE);
		btnLocate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		btnLocate.setImage( DashboardImages.getImage( DashboardImages.Images.LOCATE, ImageSize.NORMAL));
		btnLocate.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(final SelectionEvent e) {
				try{
					SessionStore store = getSessionStore();
					JumpController<NodeData<ChuruataOrganisationData, IChuruataService>> jc = new JumpController<>();
					NodeData<ChuruataOrganisationData, IChuruataService> node = (NodeData<ChuruataOrganisationData, IChuruataService>) event.getData();
					ChuruataOrganisationData org = ( node == null )?null: node.getData();
					jc.jump( new NodeJumpEvent<ChuruataOrganisationData,IChuruataService>( this, Pages.SERVICE.name(), store.getToken(), Pages.LOCATION.toPath(), operation, org, getCache()));
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
				super.widgetSelected(e);
			}
		});

		btnAddress = new Button(buttonBar, SWT.NONE);
		btnAddress.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		btnAddress.setImage( NAImages.getImage( NAImages.Images.ADDRESS, ImageSize.NORMAL));
		btnAddress.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(final SelectionEvent e) {
				try{
					SessionStore store = getSessionStore();
					ILoginUser user = store.getLoginUser();
					JumpController.Operations operation = ( user==null)?Operations.CREATE: Operations.UPDATE;
					JumpController<NodeData<IChuruataService, IAddress>> jc = new JumpController<>();
					IChuruataService service = getCache();
					IAddress address = ( service == null )?null: service.getAddress();
					jc.jump( new NodeJumpEvent<IChuruataService, IAddress>( this, Pages.SERVICE.name(), store.getToken(), Pages.ADDRESS.toPath(), operation, getCache(), address));
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
				super.widgetSelected(e);
			}
		});

		
		super.onSetupButtonBar(buttonBar);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean onPostProcess(String context, SessionStore store) {
		controller = new WebController(context, IRestPages.Pages.ORGANISATION.toPath());
		this.servicesComposite.addEditListener(listener);

		JumpController<?> jc = new JumpController<>();
		if( jc != null ) {
			event = (JumpEvent<NodeData<?, ?>>) jc.getEvent( Pages.SERVICE.toPath());		
			if( event != null ) {
				Pages source = Pages.valueOf(event.getIdentifier());
				switch( source) {
				case SERVICES:
					NodeData<ChuruataOrganisationData,IChuruataService> oNode = (NodeData<ChuruataOrganisationData, IChuruataService>) event.getData();
					setCache( oNode.getChild());
					break;
				case ADDRESS:
					ILoginUser user = store.getLoginUser();
					NodeData<IChuruataService, AddressData> anode = (NodeData<IChuruataService, AddressData>) event.getData();
					IChuruataService service = anode.getData();
					setCache( service);
					controller.setAddress(user, service, (AddressData) anode.getChild());
					break;
				default:
					break;
				}
			}
		}
		this.servicesComposite.setInput(getCache());
		return true;
	}

	@Override
	protected void onButtonPressed(IChuruataService service, SessionStore store) {
		try{
			if( service == null )
				return;
			ILoginUser user = store.getLoginUser();
			
			ProfileData profile= store.getData();
			ChuruataOrganisationData organisation = (ChuruataOrganisationData) profile.getOrganisation()[0];
			if( user == null ) {
				organisation.addService(service);
				JumpController<ProfileData> jc = new JumpController<>();
				jc.jump( new JumpEvent<ProfileData>( this, Pages.SERVICE.name(), store.getToken(), Pages.ORGANISATION.toPath(), JumpController.Operations.DONE, store.getData()));							
			}else {
				if(( event == null ) || !JumpController.Operations.UPDATE.equals( event.getOperation() )) {
					controller.addService( organisation, service);
				}else {
					controller.updateService(user, organisation, service);
				}
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
	}

	protected void onServiceEvent( EditEvent<IChuruataService> event ) {
		switch( event.getType()) {
		case COMPLETE:
			setCache( event.getData());
			Button btnAdd = super.getBtnNext();
			btnAdd.setEnabled( event.getData() != null);
			break;
		default:
			break;
		}
	}

	@Override
	public void close() {
		this.servicesComposite.removeEditListener(listener);
		super.close();
	}
		
	private class WebController extends AbstractHttpRequest<ChuruataOrganisationData.Requests>{
		
		public WebController( String context, String path) {
			super( context, path);
		}

		public void addService( ChuruataOrganisationData organisation, IChuruataService service ) {
			Map<String, String> params = super.getParameters();
			params.put(ServiceData.Parameters.PERSON_ID.toString(), String.valueOf( organisation.getContact().getId()));
			params.put(ServiceData.Parameters.ORGANISATION_ID.toString(), String.valueOf( organisation.getId()));
			params.put(ServiceData.Parameters.NAME.toString(), service.getContribution().toString());
			params.put(ServiceData.Parameters.TYPE.toString(),  service.getService().name());
			params.put(ServiceData.Parameters.DESCRIPTION.toString(), service.getDescription());
			params.put(ServiceData.Parameters.FROM_DATE.toString(),  String.valueOf( service.from().getTime()));
			params.put(ServiceData.Parameters.TO_DATE.toString(),  String.valueOf( service.to().getTime()));
			try {
				sendGet(ChuruataOrganisationData.Requests.ADD_SERVICE, params );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		public void updateService( ILoginUser user, ChuruataOrganisationData organisation, IChuruataService service ) {
			Map<String, String> params = super.getParameters();
			params.put(ServiceData.Parameters.ORGANISATION_ID.toString(), String.valueOf( organisation.getId()));
			params.put(LoginData.Parameters.USER_ID.toString(), String.valueOf( user.getId()));
			params.put(LoginData.Parameters.SECURITY.toString(), String.valueOf( user.getSecurity()));
			try {
				Gson gson = new Gson();
				String data = gson.toJson(service, ServiceData.class);
				sendPut(ChuruataOrganisationData.Requests.UPDATE_SERVICE, params, data );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		public void setAddress(ILoginUser user,  IChuruataService service, AddressData address) {
			Map<String, String> params = super.getParameters();
			try {
				params.put(ChuruataOrganisationData.Parameters.USER_ID.toString(), String.valueOf( user.getId()));
				params.put(ChuruataOrganisationData.Parameters.SECURITY.toString(), String.valueOf( user.getSecurity() ));
				params.put(ChuruataOrganisationData.Parameters.SERVICE_ID.toString(), String.valueOf( service.getId()));
				Gson gson = new Gson();
				String data = gson.toJson(address, AddressData.class);
				sendPut(ChuruataOrganisationData.Requests.SET_SERVICE_ADDRESS, params, data );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		@Override
		protected String onHandleResponse(ResponseEvent<ChuruataOrganisationData.Requests> event) throws IOException {
			try {
				SessionStore store = getSessionStore();
				ProfileData profile = store.getData();
				Gson gson = new Gson();
				switch( event.getRequest()){
				case UPDATE_SERVICE:
				case ADD_SERVICE:
					ChuruataOrganisationData data = gson.fromJson(event.getResponse(), ChuruataOrganisationData.class);
					profile.addOrganisation(data);
					JumpController<ChuruataOrganisationData> jc = new JumpController<>();
					jc.jump( new JumpEvent<ChuruataOrganisationData>( this, Pages.SERVICE.name(), store.getToken(), Pages.SERVICES.toPath(), JumpController.Operations.UPDATE, data));			
					break;
				case SET_SERVICE_ADDRESS:
					ServiceData service = gson.fromJson(event.getResponse(), ServiceData.class);
					setCache(service);
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
