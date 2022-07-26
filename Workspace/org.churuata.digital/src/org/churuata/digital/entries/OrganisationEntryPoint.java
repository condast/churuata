package org.churuata.digital.entries;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.churuata.digital.core.AbstractChuruataEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries.Pages;
import org.churuata.digital.core.data.ChuruataOrganisationData;
import org.churuata.digital.core.data.ChuruataProfileData;
import org.churuata.digital.core.location.IChuruataService;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.views.OrganisationComposite;
import org.condast.commons.Utils;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.config.Config;
import org.condast.commons.messaging.core.util.NodeData;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.na.data.PersonData;
import org.condast.commons.na.profile.IProfileData;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.messaging.jump.JumpController;
import org.condast.commons.ui.messaging.jump.JumpEvent;
import org.condast.commons.ui.messaging.jump.NodeJumpEvent;
import org.condast.commons.ui.player.PlayerImages;
import org.condast.commons.ui.session.AbstractSessionHandler;
import org.condast.commons.ui.session.SessionEvent;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.StartupParameters;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class OrganisationEntryPoint extends AbstractChuruataEntryPoint<ChuruataOrganisationData> {
	private static final long serialVersionUID = 1L;

	public static final String S_CHURUATA = "Churuata-Digital";

	private OrganisationComposite organisationComposite;
	private Button btnNext;

	private SessionHandler handler;
	
	private WebController controller;

	private JumpEvent<ChuruataOrganisationData> event;
	private ChuruataOrganisationData data = null;

	private IEditListener<ChuruataOrganisationData> listener = e->onOrganisationEvent(e);
	private IEditListener<IChuruataService> serviceListener = e->onServiceEvent(e);

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	protected boolean prepare(Composite parent) {
		StartupParameters service = RWT.getClient().getService( StartupParameters.class );
		IDomainProvider<SessionStore<ChuruataOrganisationData>> domain = Dispatcher.getDomainProvider( service );
		if( domain == null )
			return false;
		SessionStore<ChuruataOrganisationData> store = domain.getData();
		if( store == null )
			return false;
		if(( store.getProfile()  == null )  && ( store.getData()  == null ))
			return false;
		setData(store);
		handler = new SessionHandler( parent.getDisplay());
		return true;
	}

	@Override
    protected Composite createComposite(Composite parent) {
        parent.setLayout(new GridLayout( 1, false ));
        organisationComposite = new OrganisationComposite( parent, SWT.NONE);
 		organisationComposite.setData( RWT.CUSTOM_VARIANT, S_CHURUATA );
 		organisationComposite.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, false));
		Group group = new Group( parent, SWT.NONE );
		group.setText("Add Churuata Service");
		group.setLayout( new GridLayout(5, false ));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		btnNext = new Button(group, SWT.NONE);
		btnNext.setEnabled(false);
		btnNext.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		btnNext.setImage( PlayerImages.getImage( PlayerImages.Images.NEXT));
		btnNext.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(final SelectionEvent e) {
				try{
					SessionStore<ChuruataOrganisationData> store = getSessionStore();
					if( store.getData() == null )
						return;
					IProfileData person = store.getProfile();
					jump(person.getId(), store.getData(), Pages.LOCATION );						
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
				super.widgetSelected(e);
			}
		});

 		return organisationComposite;
    }

	@Override
	protected boolean postProcess(Composite parent) {
		Config config = Config.getInstance();
		String context = config.getServerContext();
		controller = new WebController();
		controller.setInput(context, IRestPages.Pages.ORGANISATION.toPath());
		this.organisationComposite.addEditListener(listener);
		this.organisationComposite.addServiceListener(serviceListener);
		
		SessionStore<ChuruataOrganisationData> store = super.getSessionStore();
		JumpController<ChuruataOrganisationData> jc = new JumpController<>();
		event = jc.getEvent( Pages.ORGANISATION.toPath());		
		if( event != null ) {
			this.data = event.getData();
			store.setData(this.data);
		}else {
			this.data = store.getData();
		}

		if( this.data != null ) {
			this.organisationComposite.setInput( this.data, true);
			btnNext.setEnabled(!Utils.assertNull( this.data.getServices() ));
		}
		return super.postProcess(parent);
	}

	protected void onOrganisationEvent( EditEvent<ChuruataOrganisationData> event ) {
		SessionStore<ChuruataOrganisationData> store = super.getSessionStore();
		IProfileData person = store.getProfile();

		ChuruataOrganisationData organisation = event.getData();
		organisation.setContact((PersonData) person); 
		switch( event.getType()) {
		case ADDED:
			store.setData( this.organisationComposite.getInput());
			controller.page = Pages.SERVICES;
			jump( person.getId(), organisation, Pages.SERVICES);			
			break;
		case COMPLETE:
			data = event.getData();
			store.setData(event.getData());
			btnNext.setEnabled(( data != null ));
			break;
		default:
			break;
		}
	}

	protected void onServiceEvent( EditEvent<IChuruataService> event ) {
		try {
			SessionStore<ChuruataOrganisationData> store = super.getSessionStore();
			IChuruataService sevice = event.getData();
			switch( event.getType()) {
			case SELECTED:
				store.setSelectedService(sevice);
				store.setData( this.organisationComposite.getInput());
				controller.page = Pages.SERVICES;
				JumpController<NodeData<ChuruataOrganisationData, IChuruataService>> jc = new JumpController<>();
				jc.jump( new NodeJumpEvent<ChuruataOrganisationData, IChuruataService>( this, store.getToken(), Pages.SERVICES.toPath(), JumpController.Operations.UPDATE, store.getData(), sevice));			
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void jump( long personId, ChuruataOrganisationData organisation, Pages page ) {
		SessionStore<ChuruataOrganisationData> store = super.getSessionStore();
		if( organisation.getId() <= 0 ) { 
			controller.register(personId, organisation);
		}
		else
			Dispatcher.jump( page, store.getToken());		
	}
	
	@Override
	protected void createTimer(boolean create, int nrOfThreads, TimeUnit unit, int startTime, int rate) {
		super.createTimer(true, nrOfThreads, unit, startTime, 10000);
	}

	@Override
	protected void handleTimer() {
		handler.addData(getSessionStore());
		super.handleTimer();
	}

	@Override
	public void close() {
		this.organisationComposite.removeServiceListener(serviceListener);
		this.organisationComposite.removeEditListener(listener);
		super.close();
	}
	
	private class SessionHandler extends AbstractSessionHandler<SessionStore<ChuruataOrganisationData>>{

		protected SessionHandler(Display display) {
			super(display);
		}

		@Override
		protected void onHandleSession(SessionEvent<SessionStore<ChuruataOrganisationData>> sevent) {
			/* NOTHING */
		}
	}
	
	private class WebController extends AbstractHttpRequest<ChuruataOrganisationData.Requests>{
		
		private Pages page;
		
		public WebController() {
			super();
		}

		public void setInput(String context, String path) {
			super.setContextPath(context + path);
		}

		public void register( long personId, ChuruataOrganisationData organisation ) {
			Map<String, String> params = super.getParameters();
			params.put(ChuruataProfileData.Parameters.PERSON_ID.toString(), String.valueOf( personId ));
			Gson gson = new Gson();
			String data = gson.toJson(organisation, ChuruataOrganisationData.class);
			try {
				sendPut(ChuruataOrganisationData.Requests.REGISTER, params, data );
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
				case REGISTER:
					ChuruataOrganisationData data = gson.fromJson(event.getResponse(), ChuruataOrganisationData.class);
					store.setData(data);
					switch( page ) {
					case ORGANISATION:
						Dispatcher.jump( Pages.LOCATION, store.getToken());
						break;
					case SERVICES:
						Dispatcher.jump( page, store.getToken());
						break;
					default:
						break;
					}
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