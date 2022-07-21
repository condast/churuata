package org.churuata.digital.entries;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.churuata.digital.core.AbstractChuruataEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries;
import org.churuata.digital.core.data.ChuruataOrganisationData;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.image.ChuruataImages;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.config.Config;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.na.data.ProfileData;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.na.person.ProfileComposite;
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

public class AccountEntryPoint extends AbstractChuruataEntryPoint<ChuruataOrganisationData>{
	private static final long serialVersionUID = 1L;

	public static final String S_PAGE = "page";

	public static final String S_CHURUATA = "churuata";
	public static final String S_ADD_ACCOUNT = "Add Account";

	private ProfileComposite profileComposite;
	private Button btnAdd;

	private IEditListener<ProfileData> listener = e->onProfileEvent(e);

	private WebController controller;
	
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
		setData(store);
		ILoginUser user = store.getLoginUser();
		return ( user != null );
	}
	
	@Override
	protected Composite createComposite(Composite parent) {
		parent.setLayout( new GridLayout(1,false));
		profileComposite = new ProfileComposite(parent, SWT.NONE );
		profileComposite.setData( RWT.CUSTOM_VARIANT, S_CHURUATA );
		profileComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ));
		profileComposite.addEditListener( listener);

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
					SessionStore<ChuruataOrganisationData> store = getSessionStore();
					if( store.getProfile() == null )
						return;
					ProfileData profile = store.getProfile();
					controller.update( profile);
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
				super.widgetSelected(e);
			}
		});
		return profileComposite;
	}

	@Override
	protected boolean postProcess(Composite parent) {
		Config config = Config.getInstance();
		String context = config.getServerContext();

		SessionStore<ChuruataOrganisationData> store = getSessionStore();
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

	protected void onProfileEvent( EditEvent<ProfileData> event ) {
		SessionStore<ChuruataOrganisationData> store = super.getSessionStore();
		switch( event.getType()) {
		case INITIALISED:
			break;
		case CHANGED:
		case COMPLETE:
			if( !isFilled( event.getData()))
				return;
			btnAdd.setEnabled(true);
			store.setProfile(event.getData());
			break;
		default:
			break;
		}
	}

	protected boolean isFilled( ProfileData profile ) {
		if( profile == null )
			return false;
		return !StringUtils.isEmpty(profile.getName()) && !StringUtils.isEmpty(profile.getFirstName()) &&
			!StringUtils.isEmpty(profile.getEmail()) && !StringUtils.isEmpty(profile.getSurname());
	}

	@Override
	protected void createTimer(boolean create, int nrOfThreads, TimeUnit unit, int startTime, int rate) {
		super.createTimer(true, nrOfThreads, unit, startTime, rate);
	}
	
	@Override
	protected void handleTimer() {
		try {
			super.handleTimer();
			SessionStore<ChuruataOrganisationData> store = getSessionStore();
			if(( store == null ) || ( store.getLoginUser() == null ))
				return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected boolean handleSessionTimeout(boolean reload) {
		SessionStore<ChuruataOrganisationData> store = super.getSessionStore();
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

		public void update( ProfileData person ) {
			Map<String, String> params = new HashMap<>();
			try {
				if( person == null )
					return;
				params.put(ProfileData.Parameters.USER_ID.toString(), String.valueOf( user.getId()));
				params.put(ProfileData.Parameters.SECURITY.toString(), String.valueOf( user.getSecurity() ));
				Gson gson = new Gson();
				String str = gson.toJson( person, ProfileData.class);
				sendPut(ProfileData.Requests.UPDATE_PERSON, params, str );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		@Override
		protected String onHandleResponse(ResponseEvent<ProfileData.Requests> event) throws IOException {
			try {
				SessionStore<ChuruataOrganisationData> store = getSessionStore();
				Gson gson = new Gson();
				ProfileData profile = null;
				switch( event.getRequest()){
				case UPDATE_PERSON:
					profile = gson.fromJson(event.getResponse(), ProfileData.class);
					store.setProfile(profile);
					Dispatcher.redirect(Entries.Pages.ACTIVE, store.getToken());
					break;
				case GET_PROFILE:					
					profile = gson.fromJson(event.getResponse(), ProfileData.class);
					profileComposite.setInput(profile, true);
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
		protected void onHandleResponseFail(HttpStatus status, ResponseEvent<ProfileData.Requests> event) throws IOException {
			super.onHandleResponseFail(status, event);
		}	
	}
}