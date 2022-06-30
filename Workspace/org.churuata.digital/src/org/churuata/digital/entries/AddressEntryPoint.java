package org.churuata.digital.entries;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.churuata.digital.core.AbstractChuruataEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries;
import org.churuata.digital.core.data.OrganisationData;
import org.churuata.digital.core.data.ProfileData;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.image.ChuruataImages;
import org.churuata.digital.ui.views.EditPersonComposite;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.config.Config;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
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

public class AddressEntryPoint extends AbstractChuruataEntryPoint<OrganisationData>{
	private static final long serialVersionUID = 1L;

	public static final String S_PAGE = "page";

	public static final String S_CHURUATA = "churuata";

	private EditPersonComposite editComposite;
	private Button btnAdd;

	private IEditListener<ProfileData> listener = e->onOrganisationEvent(e);

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
		ILoginUser user = store.getLoginUser();
		return ( user != null );
	}
	
	@Override
	protected Composite createComposite(Composite parent) {
		parent.setLayout( new GridLayout(1,false));
		editComposite = new EditPersonComposite(parent, SWT.NONE );
		editComposite.setData( RWT.CUSTOM_VARIANT, S_CHURUATA );
		editComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ));
		editComposite.addEditListener( listener);

		Group group = new Group( parent, SWT.NONE );
		group.setText("Add Churuata");
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
					SessionStore<OrganisationData> store = getSessionStore();
					if( store.getData() == null )
						return;
					controller.create( store.getProfile());
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
				super.widgetSelected(e);
			}
		});

		return editComposite;
	}

	@Override
	protected boolean postProcess(Composite parent) {
		Config config = Config.getInstance();
		String context = config.getServerContext();

		SessionStore<OrganisationData> store = getSessionStore();
		ILoginUser user = store.getLoginUser();
		ProfileData profile = store.getProfile();
		if( profile == null ) {
			profile = null;//new ProfileData( selected );
			store.setProfile(profile); 
		}

		controller = new WebController();
		controller.setInput(context, IRestPages.Pages.CONTACT.toPath());
		controller.user = user;
		controller.get();
		return true;
	}

	protected void onOrganisationEvent( EditEvent<ProfileData> event ) {
		ProfileData data = null;
		SessionStore<OrganisationData> store = super.getSessionStore();
		switch( event.getType()) {
		case INITIALISED:
			break;
		case CHANGED:
			data = event.getData();
			store.setProfile(data);
			break;
		case SELECTED:
			data = event.getData();
			store.setProfile(data);
			Dispatcher.jump(Entries.Pages.CREATE, store.getToken());
			break;
		case ADDED:
			editComposite.getInput();
			Dispatcher.jump(Entries.Pages.SERVICES, store.getToken());
			break;
		case COMPLETE:
			data = event.getData();
			store.setProfile(data);
			btnAdd.setEnabled(true);
			break;
		default:
			break;
		}
	}

	@Override
	protected void createTimer(boolean create, int nrOfThreads, TimeUnit unit, int startTime, int rate) {
		super.createTimer(true, nrOfThreads, unit, startTime, rate);
	}

	@Override
	protected void handleTimer() {
		try {
			super.handleTimer();
			SessionStore<OrganisationData> store = getSessionStore();
			if(( store == null ) || ( store.getLoginUser() == null ))
				return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected boolean handleSessionTimeout(boolean reload) {
		SessionStore<OrganisationData> store = super.getSessionStore();
		store.setLoginUser(null);
		return super.handleSessionTimeout(reload);
	}
	
	private class WebController extends AbstractHttpRequest<ProfileData.Requests>{
		
		private ILoginUser user;
		
		public WebController() {
			super();
		}

		public void setInput(String context, String path) {
			super.setContextPath(context + path);
		}

		public void create( ProfileData profile ) {
			Map<String, String> params = new HashMap<>();
			try {
				if( profile == null )
					return;
				params.put(ProfileData.Parameters.USER_ID.toString(), String.valueOf( user.getId()));
				params.put(ProfileData.Parameters.SECURITY.toString(), String.valueOf( user.getSecurity() ));
				Gson gson = new Gson();
				String str = gson.toJson( profile, ProfileData.class);
				sendPut(ProfileData.Requests.CREATE, params, str );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		public void get() {
			Map<String, String> params = super.getParameters();
			try {
				params.put(ProfileData.Parameters.USER_ID.toString(), String.valueOf( user.getId()));
				params.put(ProfileData.Parameters.SECURITY.toString(), String.valueOf( user.getSecurity() ));
				sendGet(ProfileData.Requests.GET_PROFILE, params );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		@Override
		protected String onHandleResponse(ResponseEvent<ProfileData.Requests> event) throws IOException {
			try {
				SessionStore<OrganisationData> store = getSessionStore();
				switch( event.getRequest()){
				case CREATE:
					Dispatcher.jump(Entries.Pages.ACTIVE, store.getToken());
					break;
				case GET_PROFILE:
					Gson gson = new Gson();
					ProfileData profile = gson.fromJson(event.getResponse(), ProfileData.class);
					editComposite.setInput(profile);
					Dispatcher.jump(Entries.Pages.ACTIVE, store.getToken());
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