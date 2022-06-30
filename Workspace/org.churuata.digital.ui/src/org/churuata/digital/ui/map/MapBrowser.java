package org.churuata.digital.ui.map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.churuata.digital.core.data.OrganisationData;
import org.churuata.digital.core.location.ChuruataData;
import org.churuata.digital.core.location.IChuruata;
import org.churuata.digital.core.location.IChuruataService;
import org.churuata.digital.core.location.IChuruata.Requests;
import org.churuata.digital.core.rest.IRestPages;
import org.condast.commons.Utils;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.plane.FieldData;
import org.condast.commons.data.plane.IPolygon;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.session.AbstractSessionHandler;
import org.condast.commons.ui.session.SessionEvent;
import org.condast.commons.ui.controller.EditEvent.EditTypes;
import org.condast.js.commons.eval.EvaluationEvent;
import org.condast.js.commons.eval.IEvaluationListener;
import org.condast.js.commons.images.IDefaultMarkers.Markers;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.openlayer.map.control.GeoView;
import org.openlayer.map.control.IconsView;
import org.openlayer.map.control.NavigationView;
import org.openlayer.map.controller.OpenLayerController;

public class MapBrowser extends Browser {
	private static final long serialVersionUID = 1L;

	public static String S_ERR_NO_FIELD_DATA = "The vessel does not have any field data: ";
	public static String S_ERR_NO_GPS_SIGNAL = "NO GPS SIGNAL";

	public static final int DEFAULT_SCAN_DELAY = 20;//20 update pulses

	protected static final String S_UNITY_START_PAGE = "web/unity/index.html";

	private enum CallBacks{
		POINT,
		POLYGON;

		@Override
		public String toString() {
			return StringStyler.xmlStyleString(name());
		}
	}

	private OpenLayerController mapController;
	
	private Collection<OrganisationData> organisations;

	private Collection<IEditListener<LatLng>> listeners;
	
	private boolean located;

	private ProgressListener plistener = new ProgressListener() {
		private static final long serialVersionUID = 1L;
		@Override
		public void completed(ProgressEvent event) {
			try{
				logger.info("Browser activated" );
			}
			catch( Exception ex ){
				ex.printStackTrace();
			}
		}

		@Override
		public void changed(ProgressEvent event) {
		}
	};

	private SessionHandler handler;
	private WebController controller;
	
	private FieldData fieldData;
	
	private OrganisationData input;
	
	private IEvaluationListener<Object> listener = e->onNotifyEvaluation(e);

	private Logger logger = Logger.getLogger( this.getClass().getName() );

	public MapBrowser(Composite parent, int style) {
		super(parent, style);
		this.located = false;
		LatLng location = new LatLng( "Cucuta", 50.380502,31.539470);
		this.fieldData = new FieldData(-1, location, 10000, 10000, 0, 11 );
		this.mapController = new OpenLayerController( this, location, 11 );
		this.mapController.addEvaluationListener( listener);
		this.listeners = new ArrayList<>();
		this.organisations = new ArrayList<>();
		controller = new WebController();
		this.handler = new SessionHandler(getDisplay());
	}

	public void addEditListener( IEditListener<LatLng> listener ) {
		this.listeners.add(listener);
	}

	public void removeEditListener( IEditListener<LatLng> listener ) {
		this.listeners.remove(listener);
	}

	protected void notifyEditListeners( EditEvent<LatLng> event ) {
		for( IEditListener<LatLng> listener: listeners)
			listener.notifyInputEdited( event );
	}

	private void onNotifyEvaluation(EvaluationEvent<Object> event) {
		try {
			logger.info("evaluating: " + event.getId());
			if( !OpenLayerController.S_CALLBACK_ID.equals(event.getId()) || Utils.assertNull( event.getData()))
				return;
			Collection<Object> eventData = Arrays.asList(event.getData());
			StringBuilder builder = new StringBuilder();
			builder.append("Map data: ");
			for( Object obj: eventData ) {
				if( obj != null )
					builder.append(obj.toString());
				builder.append(", ");
			}
			logger.info(builder.toString());
			String str = (String) event.getData()[0];

			LatLng home = null;
			if( NavigationView.Commands.isValue(str)) {
				logger.info("String found: " + str);
				NavigationView.Commands cmd = NavigationView.Commands.valueOf(StringStyler.styleToEnum(str));
				switch( cmd ) {
				case GET_GEO_LOCATION:
					logger.info("Setting geo location");
					Object[] arr = (Object[]) event.getData()[2];
					home = new LatLng( "home", (double)arr[0], (double)arr[1]);
					fieldData = new FieldData( -1, home, 10000l, 10000l, 0d, 11);
					this.located = true;
					GeoView geo = new GeoView( this.mapController );
					geo.setFieldData(fieldData);
					geo.jump();
					logger.info("Jumped to geo location");
					notifyEditListeners( new EditEvent<LatLng>( this, EditTypes.SELECTED, home ));
					return;
				}
			}

			if( !StringUtils.isEmpty(str) && str.startsWith( IPolygon.Types.POINT.name())) {
				Object[] loc = ( Object[])event.getData()[2];
				home = new LatLng((String) event.getData()[1], (double)loc[1], (double)loc[0] );
				notifyEditListeners( new EditEvent<LatLng>( this, EditTypes.CHANGED, home ));
				//RWTUtils.redirect( S_UNITY_START_PAGE );
			}
			if( IEvaluationListener.EventTypes.SELECTED.equals( event.getEventType())) {
				logger.info(event.getData()[2].toString());
			}else {
				String data = (String) event.getData()[1];
				if( !StringUtils.isEmpty(data) && data.startsWith( CallBacks.POLYGON.name() )) {
					String wkt = (String )event.getData()[1];
					if( StringUtils.isEmpty( wkt ))
						return;
					String tp = (String) event.getData()[0];
					StringBuffer buffer = new StringBuffer();
					buffer.append(tp);
					buffer.append(": ");
					logger.fine( buffer.toString());
				}else {
					Object[] coords = (Object[]) event.getData()[2];
					LatLng latlng = new LatLng(( Double) coords[1], (Double)coords[0]);				
					IconsView icons = new IconsView( mapController );
					if( input != null )
						input.setLocation(latlng);
					icons.clearIcons();		
					createIcon(icons, input);
					notifyEditListeners( new EditEvent<LatLng>( this, EditTypes.SELECTED, latlng ));
				}
			}
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}
	}

	public void locate() {
		NavigationView view = new NavigationView(mapController);
		view.getLocation();
	}

	public void setInput( String context ){
		controller.setInput(context, IRestPages.Pages.SUPPORT.toPath());
		GeoView geo = new GeoView(this.mapController);
		geo.setFieldData(fieldData);
		geo.jump();
		onNavigation();
	}

	
	public OrganisationData getInput() {
		return input;
	}

	public void setInput(OrganisationData input) {
		this.input = input;
	}

	private void onNavigation() {
		try {
			if( located )
				return;
			logger.info("Requesting geo location");
			//NavigationView navigation = new NavigationView(mapController);
			//navigation.getLocation();
			handler.addData(null);
			//Only needed to enforce a refresh
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void updateMap() {
		if( mapController.isExecuting())
			return;
		IconsView icons = new IconsView( mapController );
		//icons.clearIcons();
		
		createIcon(icons, input);
		updateMarkers(icons);

		Collection<IChuruata> churuatas = new ArrayList<IChuruata>( controller.churuatas );
		if( Utils.assertNull(churuatas))
			return;
		
		for( IChuruata mt: churuatas ) {
			Markers marker = IChuruataService.Services.getMarker( IChuruataService.Services.values()[ mt.getMaxLeaves()]);
			icons.addMarker(mt.getLocation(), marker, mt.getLocation().getId().charAt(0));
		}
	}

	public void refresh( OrganisationData[] input) {
		try {
			//updateMap();
			//this.controller.show();
			//onNavigation();
			//handler.addData(input);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void dispose() {
		this.mapController.removeEvaluationListener( listener );
		this.mapController.dispose();
		this.removeProgressListener(plistener);
		super.dispose();
	}

	public void updateMarkers( IconsView icons) {
		if( Utils.assertNull( this.organisations ))
			return;
		for( OrganisationData churuata: this.organisations) {
			createIcon(icons, churuata);
		}		
	}

	protected static void createIcon( IconsView icons, OrganisationData data ) {
		Markers marker = Markers.RED;
		if(( data == null ) || (data.getLocation()==null))
			return;
		
		if( Utils.assertNull(data.getServices())) {
			icons.addMarker(data.getLocation(), marker, 'H');
			return;
		}
		IChuruataService service = data.getServices()[0];
		switch( service.getService()) {
		case FOOD:
			marker = Markers.GREEN;
			break;
		case COMMUNITY:
			marker = Markers.PINK;
			break;
		case EDUCATION:
			marker = Markers.YELLOW;
			break;
		case FAMILY:
			marker = Markers.ORANGE;
			break;
		case LEGAL:
			marker = Markers.PALEBLUE;
			break;
		case MEDICINE:
			marker = Markers.DARKGREEN;
			break;
		case SHELTER:
			marker = Markers.BROWN;
			break;
		default:
			break;
		}
		char chr = service.getService().name().charAt(0);
		icons.addMarker(data.getLocation(), marker, chr);
	}

	public static String createMarker( IconsView icons, IChuruata churuata, boolean newEntry ) {
		Markers marker = Markers.BROWN;
		char chr = 'N';
		String result = null;
		IChuruataService[] types = churuata.getTypes();
		if( Utils.assertNull(types)){
			result = icons.addMarker(churuata.getLocation(), marker, chr);
		}else if( types.length > 1) {
			marker = Markers.GREEN;
			chr = 'M';
			result = icons.addMarker(churuata.getLocation(), marker, chr);				
		}else {
			marker = newEntry? Markers.PURPLE: IChuruataService.Services.getMarker(types[0].getService());
			chr = types[0].getService().name().charAt(0);
			result = icons.addMarker(churuata.getLocation(), marker, chr);				
		}
		return result;
	}


	private class WebController extends AbstractHttpRequest<IChuruata.Requests>{
		
		private Collection<IChuruata> churuatas;
		
		public WebController() {
			super();
			churuatas = new ArrayList<>();
		}

		public void setInput(String context, String path) {
			super.setContextPath(context + path);
		}

		public void show() {
			Map<String, String> params = new HashMap<>();
			try {
				if( fieldData == null )
					return;
				LatLng home = fieldData.getCoordinates();
				params.put(OrganisationData.Parameters.LATITUDE.toString(), String.valueOf( home.getLatitude()));
				params.put(OrganisationData.Parameters.LONGITUDE.toString(), String.valueOf( home.getLongitude()));
				sendGet(IChuruata.Requests.SHOW, params);
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}
		
		@Override
		protected String onHandleResponse(ResponseEvent<Requests> event) throws IOException {
			try {
				switch( event.getRequest()){
				case SHOW:
					churuatas.clear();
					Gson gson = new Gson();
					IChuruata[] results = gson.fromJson(event.getResponse(), ChuruataData[].class);
					if(!Utils.assertNull(results))
						churuatas.addAll(Arrays.asList(results));
					logger.info("Churuatas found: " + churuatas.size());
					break;
				default:
					break;
				}
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onHandleResponseFail(HttpStatus status, ResponseEvent<Requests> event)
				throws IOException {
			logger.info("Failed: " + event.getRequest());
			super.onHandleResponseFail(status, event);
		}		
	}
	
	private class SessionHandler extends AbstractSessionHandler<OrganisationData[]>{

		protected SessionHandler(Display display) {
			super(display);
		}

		@Override
		protected void onHandleSession(SessionEvent<OrganisationData[]> sevent) {
			if( sevent.getData() != null ) {
				organisations.clear();
				organisations.addAll( Arrays.asList(sevent.getData()));
			}
			updateMap();		
		}	
	}
}
