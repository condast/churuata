package test.churuata.rap.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.logging.Logger;

import org.churuata.digital.core.location.ChuruataData;
import org.churuata.digital.core.location.IChuruata;
import org.churuata.digital.core.location.IChuruataService;
import org.condast.commons.Utils;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.data.plane.Field;
import org.condast.commons.data.plane.FieldData;
import org.condast.commons.data.plane.IPolygon;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.EditEvent.EditTypes;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.session.AbstractSessionHandler;
import org.condast.commons.ui.session.SessionEvent;
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
import org.openlayer.map.control.MapField;
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
	
	private Collection<IEditListener<LatLng>> listeners;
	
	private SessionHandler handler;
	private WebController controller;
	
	private FieldData fieldData;
	private LatLng clicked;
	
	private boolean located;
	
	private IEvaluationListener<Object> listener = e->onNotifyEvaluation(e);

	private Logger logger = Logger.getLogger( this.getClass().getName() );

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

	public MapBrowser(Composite parent, int style) {
		super(parent, style);
		clicked = new LatLng( "Cucuta", 7.89391, -72.50782);
		this.located = false;
		this.mapController = new OpenLayerController( this, clicked, 11 );
		this.mapController.addEvaluationListener( listener);
		this.listeners = new ArrayList<>();
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
			MapField mapview = new MapField(mapController );
			mapview.clear();
			if(!OpenLayerController.S_CALLBACK_ID.equals(event.getId())) {
				IconsView icons = new IconsView( mapController );
				updateMarkers(icons);
				NavigationView view = new NavigationView(mapController);
				view.getLocation();
				return;
			}
			if( Utils.assertNull( event.getData()))
				return;
			Collection<Object> eventData = Arrays.asList(event.getData());
			StringBuilder builder = new StringBuilder();
			builder.append("Map data: ");
			for( Object obj: eventData ) {
				if( obj != null )
					builder.append(obj.toString());
				builder.append(", ");
			}
			logger.fine(builder.toString());
			String str = (String) event.getData()[0];

			LatLng home = null;
			if( NavigationView.Commands.isValue(str)) {
				NavigationView.Commands cmd = NavigationView.Commands.valueOf(StringStyler.styleToEnum(str));
				switch( cmd ) {
				case GET_GEO_LOCATION:
					Object[] arr = (Object[]) event.getData()[2];
					home = new LatLng( "home", (double)arr[0], (double)arr[1]);
					fieldData = new FieldData( -1, home, 10000l, 10000l, 0d, 11);
					GeoView geo = new GeoView( this.mapController );
					geo.setFieldData(fieldData);
					geo.jump();
					located = true;
					return;
				}
			}

			if( !StringUtils.isEmpty(str) && str.startsWith( IPolygon.Types.POINT.name())) {
				Object[] loc = ( Object[])event.getData()[2];
				home = new LatLng((String) event.getData()[1], (double)loc[1], (double)loc[0] );
				notifyEditListeners( new EditEvent<LatLng>( this, EditTypes.CHANGED, home ));

				if( Utils.assertNull( controller.churuatas ))
					return;
				IconsView icons = new IconsView( mapController );
				IChuruata churuata = new ChuruataData(home);
				controller.churuatas.add(churuata);
				createMarker(icons, churuata, true);
				updateMarkers(icons);
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
					clicked = new LatLng(( Double) coords[1], (Double)coords[0]);				
					notifyEditListeners( new EditEvent<LatLng>( this, EditTypes.SELECTED, clicked ));
				}
			}
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}
	}

	public void locate() {
		if( located )
			return;
		NavigationView view = new NavigationView(mapController);
		view.getLocation();
		handler.addData("update");
	}

	protected void updateMap() {
		if( mapController.isExecuting())
			return;
		IconsView icons = new IconsView( mapController );
		icons.clearIcons();
		
		if( this.fieldData != null ) {
			Markers marker = Markers.RED;
			icons.addMarker(this.fieldData.getCoordinates(), marker, this.fieldData.getCoordinates().getId().charAt(0));		
		}

		if( this.clicked != null ) {
			Markers marker = Markers.ORANGE;
			icons.addMarker(clicked, marker, 'C');		
		}

		Collection<IChuruata> churuatas = controller.churuatas;
		if( Utils.assertNull(churuatas))
			return;
		
		for( IChuruata mt: churuatas ) {
			Markers marker = Markers.GREEN;
			icons.addMarker(mt.getLocation(), marker, mt.getLocation().getId().charAt(0));
		}
	}

	public void refresh() {
		try {
			this.controller.show();
			//NavigationView view = new NavigationView(mapController);
			//view.getLocation();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void dispose() {
		//this.mapController.removeEvaluationListener( listener );
		this.mapController.dispose();
		this.removeProgressListener(plistener);
		super.dispose();
	}

	public void updateMarkers( IconsView icons) {
		icons.clearIcons();
		if( Utils.assertNull( controller.churuatas ))
			return;
		for( IChuruata churuata: controller.churuatas) {
			createMarker(icons, churuata, false);
		}		
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

	private class WebController {
		
		private Collection<IChuruata> churuatas;
		
		public WebController() {
			super();
			churuatas = new ArrayList<>();
		}

		public void show() {
			int xmax = 10000; int ymax = 10000;
			int amount = 25;
			Random random = new Random();
			if( fieldData == null )
				return;
			Field field = new Field( fieldData );
			this.churuatas.clear();
			for( int i=0; i<amount; i++ ) {	
				int x = random.nextInt(xmax);
				int y = random.nextInt(ymax);
				char newChar = (char)('A' + i);
				String id = String.valueOf( newChar );
				LatLng location = LatLngUtils.transform( field.getCentre(), x, y);
				location.setId( id);
				ChuruataData cd = new ChuruataData( location ); 
				//cd.setName("Organisation " + i);
				churuatas.add( cd);
			}
			handler.addData("update");		
		}		
	}

	private class SessionHandler extends AbstractSessionHandler<String>{

		protected SessionHandler(Display display) {
			super(display);
		}

		@Override
		protected void onHandleSession(SessionEvent<String> sevent) {
			if( Utils.assertNull( controller.churuatas ))
				return;
			updateMap();
			notifyEditListeners( new EditEvent<LatLng>( this,EditTypes.COMPLETE, fieldData.getCoordinates()));
		}	
	}
}
