package org.churuata.digital.entries;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.churuata.digital.core.data.OrganisationData;
import org.churuata.digital.core.data.simple.SimpleOrganisationData;
import org.churuata.digital.core.model.IOrganisation;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.ui.map.MapBrowser;
import org.condast.commons.config.Config;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.EditEvent.EditTypes;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.entry.AbstractRestEntryPoint;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class BasicEntryPoint extends AbstractRestEntryPoint<OrganisationData> {
	private static final long serialVersionUID = 1L;

	public static final String S_CHURUATA = "Churuata-Digital";

	public static final int DEFAULT_RANGE = 10000;

	private MapBrowser mapComposite;
	
	private LatLng location;
	
	private IEditListener<LatLng> listener = e->onEditEvent( e );

	private WebController controller;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	protected boolean prepare(Composite parent) {
		return true;
	}

	@Override
    protected Composite createComposite(Composite parent) {
        parent.setLayout(new FillLayout());
        mapComposite = new MapBrowser( parent, SWT.NONE);
 		mapComposite.setData( RWT.CUSTOM_VARIANT, S_CHURUATA );
 		mapComposite.addEditListener(listener);
 		return mapComposite;
    }

	@Override
	protected boolean postProcess(Composite parent) {
		Config config = Config.getInstance();
		controller = new WebController();
		controller.setInput(config.getServerContext(), IRestPages.Pages.ORGANISATION.toPath());
		mapComposite.setInput(config.getServerContext());
		mapComposite.locate();
		return super.postProcess(parent);
	}

	private Object onEditEvent(EditEvent<LatLng> e) {
		switch( e.getType()) {
		case SELECTED:
			if( e.getData() == null )
				return null;
			location = e.getData();
			HttpSession hs = RWT.getUISession().getHttpSession();
			hs.setAttribute(EditTypes.SELECTED.name(), e.getData());
			break;
		default:
			break;
		}
		return null;
	}

	@Override
	protected void createTimer(boolean create, int nrOfThreads, TimeUnit unit, int startTime, int rate) {
		super.createTimer(true, nrOfThreads, unit, startTime, 10000);
	}

	@Override
	protected void handleTimer() {
		try {
			//mapComposite.refresh( controller.data);
			if( location != null )
				controller.locate( location, DEFAULT_RANGE);

			super.handleTimer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		this.mapComposite.removeEditListener(listener);
		super.close();
	}
	
	private class WebController extends AbstractHttpRequest<OrganisationData.Requests>{
		
		SimpleOrganisationData[] data;
		
		public WebController() {
			super();
		}

		public void setInput(String context, String path) {
			super.setContextPath(context + path);
		}

		public void locate( LatLng location, int range ) {
			Map<String, String> params = super.getParameters();
			params.put(OrganisationData.Parameters.LATITUDE.toString(), String.valueOf( location.getLatitude()));
			params.put(OrganisationData.Parameters.LONGITUDE.toString(), String.valueOf( location.getLongitude()));
			params.put(OrganisationData.Parameters.RANGE.toString(), String.valueOf( range ));
			params.put(OrganisationData.Parameters.VERIFIED.toString(), IOrganisation.Verification.VERIFIED.name());
			try {
				sendGet(OrganisationData.Requests.FIND_IN_RANGE, params );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		@Override
		protected String onHandleResponse(ResponseEvent<OrganisationData.Requests> event) throws IOException {
			try {
				Gson gson = new Gson();
				switch( event.getRequest()){
				case FIND_IN_RANGE:
					data = gson.fromJson(event.getResponse(), SimpleOrganisationData[].class);
					mapComposite.setInput(data);
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
