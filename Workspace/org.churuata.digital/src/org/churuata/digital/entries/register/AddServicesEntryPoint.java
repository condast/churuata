package org.churuata.digital.entries.register;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.churuata.digital.core.AbstractChuruataEntryPoint;
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
import org.churuata.digital.ui.views.ServiceComposite;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.config.Config;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.na.data.ContactPersonData;
import org.condast.commons.na.profile.IProfileData;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
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

public class AddServicesEntryPoint extends AbstractChuruataEntryPoint<ChuruataOrganisationData> {
	private static final long serialVersionUID = 1L;

	private ServiceComposite servicesComposite;
	private Button btnOk;

	private IChuruataService data = null;

	private IEditListener<IChuruataService> listener = e->onContactEvent(e);

	private WebController controller;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	protected SessionStore createSessionStore() {
		StartupParameters service = RWT.getClient().getService( StartupParameters.class );
		IDomainProvider<SessionStore> domain = Dispatcher.getDomainProvider( service );
		return ( domain == null )? null: domain.getData();
	}

	@Override
	protected boolean onPrepare(SessionStore store) {
		return true;
	}

	@Override
    protected ServiceComposite createComposite(Composite parent) {
        parent.setLayout(new GridLayout( 1, false ));
        servicesComposite = new ServiceComposite( parent, SWT.NONE);
 		servicesComposite.setData( RWT.CUSTOM_VARIANT, Entries.S_CHURUATA );
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
					SessionStore store = getSessionStore();
					IProfileData person = store.getProfile();
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
		if( !super.postProcess(parent))
			return false;
		Config config = Config.getInstance();
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
	public void close() {
		this.servicesComposite.removeEditListener(listener);
		super.close();
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
				SessionStore store = getSessionStore();
				Gson gson = new Gson();
				switch( event.getRequest()){
				case ADD_CONTACT_TYPE:
					ContactPersonData data = gson.fromJson(event.getResponse(), ContactPersonData.class);
					ProfileData profile = new ProfileData(  data );
					store.setProfile(profile);
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
