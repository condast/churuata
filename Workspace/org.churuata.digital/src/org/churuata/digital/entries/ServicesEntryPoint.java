package org.churuata.digital.entries;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.churuata.digital.core.AbstractChuruataEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries.Pages;
import org.churuata.digital.core.data.ChuruataOrganisationData;
import org.churuata.digital.core.data.ServiceData;
import org.churuata.digital.core.location.IChuruataService;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.image.ChuruataImages;
import org.churuata.digital.ui.views.ServiceComposite;
import org.condast.commons.authentication.core.LoginData;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.config.Config;
import org.condast.commons.messaging.core.util.NodeData;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
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
import com.google.gson.JsonSyntaxException;

public class ServicesEntryPoint extends AbstractChuruataEntryPoint<ChuruataOrganisationData> {
	private static final long serialVersionUID = 1L;

	public static final String S_CHURUATA = "Churuata-Digital";

	public static final String S_CHURUATA_PAGE = "/churuata";

	private ServiceComposite servicesComposite;
	private Button btnAdd;

	private WebController controller;

	private NodeJumpEvent<ChuruataOrganisationData, IChuruataService> event;
	private IChuruataService data = null;

	private IEditListener<IChuruataService> listener = e->onServiceEvent(e);

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	protected SessionStore<ChuruataOrganisationData> createSessionStore() {
		StartupParameters service = RWT.getClient().getService( StartupParameters.class );
		IDomainProvider<SessionStore<ChuruataOrganisationData>> domain = Dispatcher.getDomainProvider( service );
		return ( domain == null )? null: domain.getData();
	}

	@Override
	protected boolean prepare(Composite parent) {
		if( !super.prepare(parent))
			return false;
		SessionStore<ChuruataOrganisationData> store = super.getSessionStore();
		ILoginUser user = store.getLoginUser();
		return ( user != null );
	}

	@Override
    protected ServiceComposite createComposite(Composite parent) {
        parent.setLayout(new GridLayout( 1, false ));
        servicesComposite = new ServiceComposite( parent, SWT.NONE);
 		servicesComposite.setData( RWT.CUSTOM_VARIANT, S_CHURUATA );
 		servicesComposite.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, false));
		Group group = new Group( parent, SWT.NONE );
		group.setText("Add Churuata Service");
		group.setLayout( new GridLayout(5, false ));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		ChuruataImages images = ChuruataImages.getInstance();

		btnAdd = new Button(group, SWT.NONE);
		btnAdd.setEnabled(false);
		btnAdd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		btnAdd.setImage( images.getImage( ChuruataImages.Images.ADD));
		btnAdd.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(final SelectionEvent e) {
				try{
					if( data == null )
						return;
					SessionStore<ChuruataOrganisationData> store = getSessionStore();
					ILoginUser user = store.getLoginUser();
					ChuruataOrganisationData organisation = store.getData();
					if(( event == null ) || !JumpController.Operations.UPDATE.equals( event.getOperation() )) {
						controller.addService( organisation, data);
					}else {
						controller.updateService(user, organisation, data);
					}
					
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
				super.widgetSelected(e);
			}
		});

 		return servicesComposite;
    }

	@Override
	protected boolean postProcess(Composite parent) {
		Config config = Config.getInstance();
		String context = config.getServerContext();
		controller = new WebController(context, IRestPages.Pages.ORGANISATION.toPath());
		this.servicesComposite.addEditListener(listener);

		JumpController<NodeData<ChuruataOrganisationData, IChuruataService>> jc = new JumpController<>();
		event = (NodeJumpEvent<ChuruataOrganisationData, IChuruataService>) jc.getEvent( Pages.SERVICES.toPath());		
		if( event != null ) {
			this.data = event.getChild();
			this.servicesComposite.setInput(this.data);
		}
		return super.postProcess(parent);
	}

	protected void onServiceEvent( EditEvent<IChuruataService> event ) {
		switch( event.getType()) {
		case COMPLETE:
			data = event.getData();
			btnAdd.setEnabled( data != null);
			break;
		default:
			break;
		}
	}

	@Override
	protected void createTimer(boolean create, int nrOfThreads, TimeUnit unit, int startTime, int rate) {
		super.createTimer(true, nrOfThreads, unit, startTime, 10000);
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

		@Override
		protected String onHandleResponse(ResponseEvent<ChuruataOrganisationData.Requests> event) throws IOException {
			try {
				SessionStore<ChuruataOrganisationData> store = getSessionStore();
				Gson gson = new Gson();
				switch( event.getRequest()){
				case UPDATE_SERVICE:
				case ADD_SERVICE:
					ChuruataOrganisationData data = gson.fromJson(event.getResponse(), ChuruataOrganisationData.class);
					store.setData(data);
					JumpController<ChuruataOrganisationData> jc = new JumpController<>();
					jc.jump( new JumpEvent<ChuruataOrganisationData>( this, store.getToken(), Pages.ORGANISATION.toPath(), JumpController.Operations.UPDATE, data));			
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
