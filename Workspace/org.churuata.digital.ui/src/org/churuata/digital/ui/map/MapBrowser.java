package org.churuata.digital.ui.map;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

import org.condast.commons.Utils;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.plane.Field;
import org.condast.commons.data.plane.FieldData;
import org.condast.commons.data.plane.IPolygon;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.js.commons.controller.JavascriptSynchronizer;
import org.condast.js.commons.eval.EvaluationEvent;
import org.condast.js.commons.eval.IEvaluationListener;
import org.condast.js.commons.images.IDefaultMarkers.Markers;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Composite;
import org.openlayer.map.control.GeoView;
import org.openlayer.map.control.IconsView;
import org.openlayer.map.control.MapField;
import org.openlayer.map.controller.OpenLayerController;

public class MapBrowser extends Browser {
	private static final long serialVersionUID = 1L;

	public static String S_ERR_NO_FIELD_DATA = "The vessel does not have any field data: ";
	public static String S_ERR_NO_GPS_SIGNAL = "NO GPS SIGNAL";

	public static final int DEFAULT_SCAN_DELAY = 20;//20 update pulses

	private enum CallBacks{
		POINT,
		POLYGON;

		@Override
		public String toString() {
			return StringStyler.xmlStyleString(name());
		}
	}

	private OpenLayerController mapController;
	private JavascriptSynchronizer<String> synchronizer;


	private boolean drawing;

	private LatLng clickedLocation;

	private ILoginUser user;

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

	private IEvaluationListener<Object> mapListener = new IEvaluationListener<Object>(){

		@Override
		public void notifyEvaluation(EvaluationEvent<Object> event) {
			try {
				if(!OpenLayerController.S_CALLBACK_ID.equals(event.getId())) {
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
				String str = (String) event.getData()[1];
				if( !StringUtils.isEmpty(str) && str.startsWith( IPolygon.Types.POINT.name())) {
					Object[] loc = ( Object[])event.getData()[2];
					clickedLocation = new LatLng((String) event.getData()[1], (double)loc[1], (double)loc[0] );
					IconsView icons = new IconsView( mapController );
					icons.clearIcons();
					Markers marker = Markers.RED;
					char type = 'C';
					icons.addMarker(clickedLocation, marker, type);
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
						if( drawing )
							return;
						Object[] coords = (Object[]) event.getData()[2];
						LatLng latlng = new LatLng(( Double) coords[1], (Double)coords[0]);				
					}
				}
			}
			catch( Exception ex ) {
				ex.printStackTrace();
			}
		}
	};

	private Logger logger = Logger.getLogger( this.getClass().getName() );

	public MapBrowser(Composite parent, int style) {
		super(parent, style);
		this.drawing = false;
		LatLng location = new LatLng( "Cucuta", 7.89391, -72.50782);
		this.mapController = new OpenLayerController( this, location, 11 );
		this.mapController.addEvaluationListener(mapListener);
		this.synchronizer = new JavascriptSynchronizer<>(mapController);
	}

	public void setLocation( LatLng location) {
		this.clickedLocation =  location;
		if( clickedLocation == null )
			return;
		GeoView geo = new GeoView( mapController);
		//FieldData fieldData = new FieldData( new Field( clickedLocation, 10000, 10000));
		//geo.setFieldData( fieldData);
		MapField mapField = new MapField( mapController);
		mapField.setField(new Field( clickedLocation, 10000, 10000), 100);
	}


	public void setInput( String context, ILoginUser user ){
		this.user = user;
	}

	public void dispose() {
		this.mapController.removeEvaluationListener( mapListener);
		this.mapController.dispose();
		this.removeProgressListener(plistener);
		super.dispose();
	}
}
