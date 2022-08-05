package org.churuata.digital.entries.register;

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
import org.churuata.digital.core.data.simple.SimpleOrganisationData;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.image.ChuruataImages;
import org.churuata.digital.ui.map.OrganisationMapBrowser;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.messaging.jump.JumpController;
import org.condast.commons.ui.messaging.jump.JumpEvent;
import org.condast.commons.ui.session.SessionEvent;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.StartupParameters;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.google.gson.JsonSyntaxException;

public class LocationEntryPoint extends AbstractWizardEntryPoint<OrganisationMapBrowser, ChuruataOrganisationData> {
	private static final long serialVersionUID = 1L;

	public static final String S_TITLE = "Enter location";
	
	private OrganisationMapBrowser mapComposite;

	private IEditListener<LatLng> listener = e->onEditEvent( e );

	private WebController controller;
	
	private JumpEvent<ChuruataOrganisationData> event;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	
	public LocationEntryPoint() {
		super(S_TITLE);
	}

	@Override
	protected SessionStore createSessionStore() {
		StartupParameters service = RWT.getClient().getService( StartupParameters.class );
		IDomainProvider<SessionStore> domain = Dispatcher.getDomainProvider( service );
		return ( domain == null )? null: domain.getData();
	}

	@Override
	protected OrganisationMapBrowser onCreateComposite(Composite parent, int style) {
        mapComposite = new OrganisationMapBrowser( parent, SWT.NONE);
 		mapComposite.setData( RWT.CUSTOM_VARIANT, Entries.S_CHURUATA );
		mapComposite.addEditListener(listener);
 		return mapComposite;
	}

	@Override
	protected boolean onPostProcess(String context, SessionStore store) {
		JumpController<ChuruataOrganisationData> jc = new JumpController<>();
		event = jc.getEvent( Pages.LOCATION.toPath());		
		
		controller = new WebController( context, IRestPages.Pages.ORGANISATION.toPath());
		mapComposite.locate();
		mapComposite.setInput(context);

		ProfileData profile= store.getData();
		ChuruataOrganisationData organisation = (ChuruataOrganisationData) profile.getOrganisation()[0];
		mapComposite.setInput(new SimpleOrganisationData( organisation ));
		return true;
	}

	@Override
	protected void onSetupButtonBar(Group buttonBar) {
		ChuruataImages images = ChuruataImages.getInstance();
		Button button = getBtnNext();
		button.setEnabled(false);
		button.setImage( images.getImage( ChuruataImages.Images.CHECK));
		super.onSetupButtonBar(buttonBar);
	}

	private Object onEditEvent(EditEvent<LatLng> e) {
		switch( e.getType()) {
		case SELECTED:
			if( e.getData() == null )
				return null;
			SessionStore store = getSessionStore();
			ProfileData profile= store.getData();
			ChuruataOrganisationData organisation = (ChuruataOrganisationData) profile.getOrganisation()[0];
			organisation.setLocation(e.getData());
			controller.update(organisation);
			break;
		default:
			break;
		}
		return null;
	}

	@Override
	protected void onButtonPressed(ChuruataOrganisationData data, SessionStore store) {
		JumpController<ChuruataOrganisationData> jc = new JumpController<>();
		switch( event.getOperation()) {
		case UPDATE:
			jc.jump( new JumpEvent<ChuruataOrganisationData>( this, Pages.LOCATION.name(), store.getToken(), Pages.ORGANISATION.toPath(), JumpController.Operations.DONE, data));			
			break;
		default:
			jc.jump( new JumpEvent<ChuruataOrganisationData>( this, Pages.LOCATION.name(), store.getToken(), Pages.SHOW_LEGAL.toPath(), JumpController.Operations.CREATE, data));			
			break;
		}
	}

	
	@Override
	protected void onHandleSyncTimer(SessionEvent<SessionStore> sevent) {
		this.mapComposite.refresh();
		super.onHandleSyncTimer(sevent);
	}

	@Override
	public void close() {
		this.mapComposite.removeEditListener(listener);
		super.close();
	}
	
	private class WebController extends AbstractHttpRequest<ChuruataOrganisationData.Requests>{
		
		public WebController(String context, String path) {
			super();
			super.setContextPath(context + path);
		}

		public void update( ChuruataOrganisationData data ) {
			Map<String, String> params = super.getParameters();
			params.put(ServiceData.Parameters.PERSON_ID.toString(), String.valueOf(data.getContact().getId()));
			params.put(ServiceData.Parameters.ORGANISATION_ID.toString(), String.valueOf( data.getId()));
			params.put(ServiceData.Parameters.LATITUDE.toString(), String.valueOf(data.getLocation().getLatitude()));
			params.put(ServiceData.Parameters.LONGITUDE.toString(), String.valueOf( data.getLocation().getLongitude()));
			try {
				super.sendGet(ChuruataOrganisationData.Requests.SET_LOCATION, params);
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		@Override
		protected String onHandleResponse(ResponseEvent<ChuruataOrganisationData.Requests> event) throws IOException {
			try {
				switch( event.getRequest()){
				case SET_LOCATION:
					Button btnNext = getBtnNext();
					btnNext.setEnabled(true);
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
