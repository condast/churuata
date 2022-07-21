package org.churuata.digital.entries.register;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import org.churuata.digital.core.AbstractWizardEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries.Pages;
import org.churuata.digital.core.data.ChuruataOrganisationData;
import org.churuata.digital.core.data.ServiceData;
import org.churuata.digital.core.data.simple.SimpleOrganisationData;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.map.OrganisationMapBrowser;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.session.SessionEvent;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.StartupParameters;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


public class LocationEntryPoint extends AbstractWizardEntryPoint<OrganisationMapBrowser, ChuruataOrganisationData> {
	private static final long serialVersionUID = 1L;

	private OrganisationMapBrowser mapComposite;

	private IEditListener<LatLng> listener = e->onEditEvent( e );

	private WebController controller;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	protected IDomainProvider<SessionStore<ChuruataOrganisationData>> getDomainProvider(StartupParameters service) {
		return Dispatcher.getDomainProvider(service);
	}
	
	@Override
	protected OrganisationMapBrowser onCreateComposite(Composite parent, int style) {
        mapComposite = new OrganisationMapBrowser( parent, SWT.NONE);
 		mapComposite.setData( RWT.CUSTOM_VARIANT, S_CHURUATA );
		mapComposite.addEditListener(listener);
 		return mapComposite;
	}

	@Override
	protected boolean onPostProcess(String context, ChuruataOrganisationData data, SessionStore<ChuruataOrganisationData> store) {
		controller = new WebController( context, IRestPages.Pages.ORGANISATION.toPath());
		mapComposite.setInput(context);
		mapComposite.setInput(new SimpleOrganisationData( store.getData()));
		getBtnNext().setEnabled(false);
		mapComposite.locate();
		return true;
	}

	private Object onEditEvent(EditEvent<LatLng> e) {
		switch( e.getType()) {
		case SELECTED:
			if( e.getData() == null )
				return null;
			Button btnNext = getBtnNext();
			btnNext.setEnabled(true);
			SessionStore<ChuruataOrganisationData> store = getSessionStore();
			ChuruataOrganisationData data = store.getData();
			data.setLocation(e.getData());
			controller.update(data);
			break;
		default:
			break;
		}
		return null;
	}

	@Override
	protected void onButtonPressed(ChuruataOrganisationData data, SessionStore<ChuruataOrganisationData> store) {
		Dispatcher.jump( Pages.SHOW_LEGAL, store.getToken());
	}

	
	@Override
	protected void onHandleTimer(SessionEvent<ChuruataOrganisationData> event) {
		try {
			//mapComposite.refresh(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
				SessionStore<ChuruataOrganisationData> store = getSessionStore();
				Gson gson = new Gson();
				switch( event.getRequest()){
				case SET_LOCATION:
					ChuruataOrganisationData data = gson.fromJson(event.getResponse(), ChuruataOrganisationData.class);
					store.setData(data);
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
