package org.churuata.digital.entries.register;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.churuata.digital.core.AbstractChuruataEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries;
import org.churuata.digital.core.data.ChuruataOrganisationData;
import org.churuata.digital.core.data.ChuruataProfileData;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.image.ChuruataImages;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.config.Config;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.na.data.PersonData;
import org.condast.commons.na.data.ProfileData;
import org.condast.commons.na.profile.IProfileData;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.na.person.PersonComposite;
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

public class AddOrganisationEntryPoint extends AbstractChuruataEntryPoint<ChuruataOrganisationData>{
	private static final long serialVersionUID = 1L;

	public static final String S_PAGE = "page";

	public static final String S_CHURUATA = "churuata";
	public static final String S_ADD_ACCOUNT = "Add Account";

	private PersonComposite personComposite;
	private Button btnAdd;

	private IEditListener<PersonData> listener = e->onPersonEvent(e);

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
	protected Composite createComposite(Composite parent) {
		parent.setLayout( new GridLayout(1,false));
		personComposite = new PersonComposite(parent, SWT.NONE );
		personComposite.setData( RWT.CUSTOM_VARIANT, S_CHURUATA );
		personComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ));
		personComposite.addEditListener( listener);

		Group group = new Group( parent, SWT.NONE );
		group.setText( S_ADD_ACCOUNT);
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
					SessionStore store = getSessionStore();
					if( store.getProfile() == null )
						return;
					controller.update( store.getProfile());
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
				super.widgetSelected(e);
			}
		});

		return personComposite;
	}

	@Override
	protected boolean postProcess(Composite parent) {
		if( !super.postProcess(parent))
			return false;
		Config config = Config.getInstance();
		String context = config.getServerContext();

		SessionStore store = getSessionStore();
		ILoginUser user = store.getLoginUser();
		IProfileData profile = store.getProfile();
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

	protected void onPersonEvent( EditEvent<PersonData> event ) {
		PersonData data = null;
		SessionStore store = super.getSessionStore();
		switch( event.getType()) {
		case INITIALISED:
			break;
		case CHANGED:
			data = event.getData();
			//store.setProfile(data);
			break;
		case SELECTED:
			data = event.getData();
			//store.setProfile(data);
			//Dispatcher.jump(BasicApplication.Pages.CREATE, store.getToken());
			break;
		case ADDED:
			personComposite.getInput();
			//Dispatcher.jump(BasicApplication.Pages.SERVICES, store.getToken());
			break;
		case COMPLETE:
			data = event.getData();
			ProfileData profile = new ProfileData( data );
			store.setProfile(profile);
			btnAdd.setEnabled(( data != null ));
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
			SessionStore store = getSessionStore();
			if(( store == null ) || ( store.getLoginUser() == null ))
				return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected boolean handleSessionTimeout(boolean reload) {
		SessionStore store = super.getSessionStore();
		store.setLoginUser(null);
		return super.handleSessionTimeout(reload);
	}
	
	private class WebController extends AbstractHttpRequest<ChuruataProfileData.Requests>{
		
		private ILoginUser user;
		
		public WebController() {
			super();
		}

		public void setInput(String context, String path) {
			super.setContextPath(context + path);
		}

		public void get() {
			Map<String, String> params = super.getParameters();
			try {
				params.put(ChuruataProfileData.Parameters.USER_ID.toString(), String.valueOf( user.getId()));
				params.put(ChuruataProfileData.Parameters.SECURITY.toString(), String.valueOf( user.getSecurity() ));
				sendGet(ChuruataProfileData.Requests.GET_PROFILE, params );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		public void update( IProfileData person ) {
			Map<String, String> params = new HashMap<>();
			try {
				if( person == null )
					return;
				params.put(ChuruataProfileData.Parameters.USER_ID.toString(), String.valueOf( user.getId()));
				params.put(ChuruataProfileData.Parameters.SECURITY.toString(), String.valueOf( user.getSecurity() ));
				Gson gson = new Gson();
				String str = gson.toJson( person, ChuruataProfileData.class);
				sendPut(ChuruataProfileData.Requests.UPDATE_PERSON, params, str );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		@Override
		protected String onHandleResponse(ResponseEvent<ChuruataProfileData.Requests> event) throws IOException {
			try {
				SessionStore store = getSessionStore();
				switch( event.getRequest()){
				case UPDATE_PERSON:
					Dispatcher.redirect(Entries.Pages.ACTIVE, store.getToken());
					break;
				case GET_PROFILE:					Gson gson = new Gson();
					ChuruataProfileData profile = gson.fromJson(event.getResponse(), ChuruataProfileData.class);
					personComposite.setInput(profile, true);
					store.setProfile(profile);
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
		protected void onHandleResponseFail(HttpStatus status, ResponseEvent<ChuruataProfileData.Requests> event) throws IOException {
			super.onHandleResponseFail(status, event);
		}
	
	}

}