package org.churuata.digital.entries;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.churuata.digital.core.AbstractChuruataEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries.Pages;
import org.churuata.digital.core.data.OrganisationData;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.image.ChuruataImages;
import org.churuata.digital.ui.views.OrganisationComposite;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.config.Config;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.na.data.PersonData;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
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


public class OrganisationEntryPoint extends AbstractChuruataEntryPoint {
	private static final long serialVersionUID = 1L;

	public static final String S_CHURUATA = "Churuata-Digital";

	public static final String S_CHURUATA_PAGE = "/churuata";

	private OrganisationComposite servicesComposite;
	private Button btnNext;

	private SessionHandler handler;
	
	private WebController controller;

	private OrganisationData data = null;

	private IEditListener<OrganisationData> listener = e->onServiceEvent(e);

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	protected boolean prepare(Composite parent) {
		StartupParameters service = RWT.getClient().getService( StartupParameters.class );
		IDomainProvider<SessionStore> domain = Dispatcher.getDomainProvider( service );
		if( domain == null )
			return false;
		SessionStore store = domain.getData();
		if(( store == null ) || ( store.getPersonData()  == null ))
			return false;
		setData(store);
		handler = new SessionHandler( parent.getDisplay());
		return true;
	}

	@Override
    protected Composite createComposite(Composite parent) {
        parent.setLayout(new GridLayout( 1, false ));
        servicesComposite = new OrganisationComposite( parent, SWT.NONE);
 		servicesComposite.setData( RWT.CUSTOM_VARIANT, S_CHURUATA );
 		servicesComposite.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, false));
		Group group = new Group( parent, SWT.NONE );
		group.setText("Add Churuata Service");
		group.setLayout( new GridLayout(5, false ));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		ChuruataImages images = ChuruataImages.getInstance();

		btnNext = new Button(group, SWT.NONE);
		btnNext.setEnabled(false);
		btnNext.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		btnNext.setImage( images.getImage( ChuruataImages.Images.ADD));
		btnNext.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(final SelectionEvent e) {
				try{
					SessionStore store = getSessionStore();
					if( store.getOrganisation() == null )
						return;
					jump( store.getOrganisation(), Pages.ORGANISATION );						
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
		Config config = new Config();
		String context = config.getServerContext();
		controller = new WebController();
		controller.setInput(context, IRestPages.Pages.ORGANISATION.toPath());
		this.servicesComposite.addEditListener(listener);
		return super.postProcess(parent);
	}

	protected void onServiceEvent( EditEvent<OrganisationData> event ) {
		SessionStore store = super.getSessionStore();
		PersonData person = store.getPersonData();

		controller.type = event.getType();
		OrganisationData organisation = event.getData();
		organisation.setContact(person); 
		switch( event.getType()) {
		case ADDED:
			store.setOrganisation( this.servicesComposite.getInput());
			jump( organisation, Pages.SERVICES);
				
			break;
		case COMPLETE:
			data = event.getData();
			store.setOrganisation(event.getData());
			btnNext.setEnabled(( data != null ));
			break;
		default:
			break;
		}
	}

	protected void jump( OrganisationData organisation, Pages page ) {
		SessionStore store = super.getSessionStore();
		if( organisation.getId() < 0 ) { 
			controller.register(organisation);
		}
		else
			Dispatcher.jump( Pages.SERVICES, store.getToken());		
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
		this.servicesComposite.removeEditListener(listener);
		super.close();
	}
	
	private class SessionHandler extends AbstractSessionHandler<SessionStore>{

		protected SessionHandler(Display display) {
			super(display);
		}

		@Override
		protected void onHandleSession(SessionEvent<SessionStore> sevent) {
			/* NOTHING */
		}
	}
	
	private class WebController extends AbstractHttpRequest<OrganisationData.Requests>{
		
		private EditEvent.EditTypes type;
		
		public WebController() {
			super();
		}

		public void setInput(String context, String path) {
			super.setContextPath(context + path);
		}

		public void register( OrganisationData organisation ) {
			Map<String, String> params = super.getParameters();
			params.put(OrganisationData.Parameters.NAME.toString(), organisation.getName());
			params.put(OrganisationData.Parameters.DESCRIPTION.toString(), organisation.getDescription());
			params.put(OrganisationData.Parameters.WEBSITE.toString(), organisation.getWebsite());
			try {
				sendGet(OrganisationData.Requests.REGISTER, params );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		@Override
		protected String onHandleResponse(ResponseEvent<OrganisationData.Requests> event) throws IOException {
			try {
				SessionStore store = getSessionStore();
				Gson gson = new Gson();
				switch( event.getRequest()){
				case CREATE:
					OrganisationData data = gson.fromJson(event.getResponse(), OrganisationData.class);
					store.setOrganisation(data);
					switch( type ) {
					case ADDED:
						Dispatcher.jump( Pages.CONTACTS, store.getToken());
						break;
					case COMPLETE:
						Dispatcher.jump( Pages.ORGANISATION, store.getToken());
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
		protected void onHandleResponseFail(HttpStatus status, ResponseEvent<OrganisationData.Requests> event) throws IOException {
			super.onHandleResponseFail(status, event);
		}	
	}
}