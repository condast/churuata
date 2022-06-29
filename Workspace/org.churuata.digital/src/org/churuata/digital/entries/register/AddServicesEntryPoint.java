package org.churuata.digital.entries.register;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.churuata.digital.core.AbstractChuruataEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries.Pages;
import org.churuata.digital.core.data.OrganisationData;
import org.churuata.digital.core.data.ProfileData;
import org.churuata.digital.core.data.ServiceData;
import org.churuata.digital.core.location.IChuruataService;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.image.ChuruataImages;
import org.churuata.digital.ui.views.ServiceComposite;
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

public class AddServicesEntryPoint extends AbstractChuruataEntryPoint<OrganisationData> {
	private static final long serialVersionUID = 1L;

	private ServiceComposite servicesComposite;
	private Button btnOk;

	private SessionHandler handler;
	
	private IChuruataService data = null;

	private IEditListener<IChuruataService> listener = e->onContactEvent(e);

	private WebController controller;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	protected boolean prepare(Composite parent) {
		StartupParameters service = RWT.getClient().getService( StartupParameters.class );
		IDomainProvider<SessionStore<OrganisationData>> domain = Dispatcher.getDomainProvider( service );
		if( domain == null )
			return false;
		SessionStore<OrganisationData> store = domain.getData();
		if( store == null )
			return false;
		setData(store);
		handler = new SessionHandler( parent.getDisplay());
		return true;
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

		btnOk = new Button(group, SWT.NONE);
		btnOk.setEnabled(false);
		btnOk.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		btnOk.setImage( images.getImage( ChuruataImages.Images.ADD));
		btnOk.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(final SelectionEvent e) {
				try{
					if( data == null )
						return;
					SessionStore<OrganisationData> store = getSessionStore();
					PersonData person = store.getPersonData();
					controller.addService(data, person.getId());
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
		controller = new WebController( context, IRestPages.Pages.CONTACT.toPath());
		ServiceComposite.createContactTypes( this.servicesComposite, EnumSet.allOf( IChuruataService.Services.class ));
		this.servicesComposite.select( IChuruataService.Services.FOOD.ordinal());
		this.servicesComposite.addEditListener(listener);
		return super.postProcess(parent);
	}

	protected void onContactEvent( EditEvent<IChuruataService> event ) {
		switch( event.getType()) {
		case COMPLETE:
			SessionStore<OrganisationData> store = getSessionStore();
			if( store.getContactPersonData() == null )
				return;
			data = event.getData();
			btnOk.setEnabled( data != null);
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
	protected void handleTimer() {
		handler.addData(getSessionStore());
		super.handleTimer();
	}

	@Override
	public void close() {
		this.servicesComposite.removeEditListener(listener);
		super.close();
	}
	
	private class SessionHandler extends AbstractSessionHandler<SessionStore<OrganisationData>>{

		protected SessionHandler(Display display) {
			super(display);
		}

		@Override
		protected void onHandleSession(SessionEvent<SessionStore<OrganisationData>> sevent) {
			/* NOTHING */
		}
	}
	
	private class WebController extends AbstractHttpRequest<ProfileData.Requests>{
		
		public WebController(String context, String path) {
			super();
			super.setContextPath(context + path);
		}

		public void addService( IChuruataService service, long personId ) {
			Map<String, String> params = super.getParameters();
			params.put(ServiceData.Parameters.PERSON_ID.toString(), String.valueOf( personId));
			params.put(ServiceData.Parameters.ORGANISATION_ID.toString(), service.getContribution().name());
			params.put(ServiceData.Parameters.TYPE.toString(), service.getDescription());
			params.put(ServiceData.Parameters.NAME.toString(), service.getDescription());
			try {
				sendGet(ProfileData.Requests.ADD_CONTACT_TYPE, params );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		@Override
		protected String onHandleResponse(ResponseEvent<ProfileData.Requests> event) throws IOException {
			try {
				SessionStore<OrganisationData> store = getSessionStore();
				Gson gson = new Gson();
				switch( event.getRequest()){
				case ADD_CONTACT_TYPE:
					PersonData data = gson.fromJson(event.getResponse(), PersonData.class);
					store.setPersonData(data);
					Dispatcher.jump( Pages.REGISTER, store.getToken());
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
		protected void onHandleResponseFail(HttpStatus status, ResponseEvent<ProfileData.Requests> event) throws IOException {
			super.onHandleResponseFail(status, event);
		}
	
	}
}
