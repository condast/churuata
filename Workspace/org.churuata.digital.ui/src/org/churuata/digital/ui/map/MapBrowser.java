package org.churuata.digital.ui.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

import org.churuata.digital.core.location.Churuata;
import org.churuata.digital.core.location.IChuruata;
import org.churuata.digital.core.location.IChuruataCollection;
import org.churuata.digital.core.location.IChuruataType;
import org.churuata.digital.ui.utils.RWTUtils;
import org.churuata.digital.ui.views.ShowChuruatasComposite;
import org.condast.commons.Utils;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.plane.IPolygon;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.controller.EditEvent.EditTypes;
import org.condast.js.commons.eval.EvaluationEvent;
import org.condast.js.commons.eval.IEvaluationListener;
import org.condast.js.commons.images.IDefaultMarkers.Markers;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
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
	
	private IChuruataCollection churuatas;

	private Collection<IEditListener<LatLng>> listeners;

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

	private Logger logger = Logger.getLogger( this.getClass().getName() );

	public MapBrowser(Composite parent, int style) {
		super(parent, style);
		LatLng location = new LatLng( "Cucuta", 7.89391, -72.50782);
		this.mapController = new OpenLayerController( this, location, 11 );
		this.mapController.addEvaluationListener( e->onNotifyEvaluation(e));
		this.listeners = new ArrayList<>();
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
			String str = (String) event.getData()[1];
			
			if( !StringUtils.isEmpty(str) && str.startsWith( IPolygon.Types.POINT.name())) {
				Object[] loc = ( Object[])event.getData()[2];
				LatLng clicked = new LatLng((String) event.getData()[1], (double)loc[1], (double)loc[0] );
				notifyEditListeners( new EditEvent<LatLng>( this, EditTypes.CHANGED, clicked ));

				if( churuatas == null )
					return;
				IChuruata[] nearest = churuatas.getChuruatas(clicked, 1000); 
				ChuruataDialog dialog = null;
				//if( Utils.assertNull(nearest))	
				//	dialog = new ChuruataDialog( getShell(), clicked  );
				//else
				//	dialog = new ChuruataDialog( getShell(), nearest[0]  );						
				IconsView icons = new IconsView( mapController );
				//int buttonID = dialog.open();
				//switch(buttonID) {
				//case Window.OK:
				//	IChuruata churuata = dialog.onOkButtonPressed();
				IChuruata churuata = new Churuata(clicked);
				churuatas.addChuruata(churuata);
				createMarker(icons, churuata, true);
				updateMarkers(icons);
				RWTUtils.redirect( S_UNITY_START_PAGE );

				//	break;
				//case Window.CANCEL:
				//	updateMarkers(icons);
				//	break;
				//}	
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

	public void setInput( IChuruataCollection input ){
		this.churuatas = input;
		NavigationView view = new NavigationView(mapController);
		view.getLocation();
	}

	public void dispose() {
		this.mapController.removeEvaluationListener( e->onNotifyEvaluation(e));
		this.mapController.dispose();
		this.removeProgressListener(plistener);
		super.dispose();
	}

	public void updateMarkers( IconsView icons) {
		icons.clearIcons();
		if( this.churuatas == null )
			return;
		for( IChuruata churuata: churuatas.getChuruatas()) {
			createMarker(icons, churuata, false);
		}		
	}

	public static String createMarker( IconsView icons, IChuruata churuata, boolean newEntry ) {
		Markers marker = Markers.BROWN;
		char chr = 'N';
		String result = null;
		IChuruataType[] types = churuata.getTypes();
		if( Utils.assertNull(types)){
			result = icons.addMarker(churuata.getLocation(), marker, chr);
		}else if( types.length > 1) {
			marker = Markers.GREEN;
			chr = 'M';
			result = icons.addMarker(churuata.getLocation(), marker, chr);				
		}else {
			marker = newEntry? Markers.PURPLE: IChuruataType.Types.getMarker(types[0].getType());
			chr = types[0].getType().name().charAt(0);
			result = icons.addMarker(churuata.getLocation(), marker, chr);				
		}
		return result;
	}

	public class ChuruataDialog extends Dialog{
		private static final long serialVersionUID = 7782765745284140623L;
		
		public static final String S_EDIT = "Edit Links";
		
		private Point size;
		
		private ShowChuruatasComposite active;
		
		private LatLng clicked;
		
		private IChuruata joined;
					
		/**
		 * Create the dialog.
		 * @param parentShell
		 */
		public ChuruataDialog( Shell shell, LatLng clicked )
		{
			super(shell );
			this.clicked = clicked;
			setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.MAX | SWT.RESIZE);
		}

		/**
		 * Create the dialog.
		 * @param parentShell
		 */
		public ChuruataDialog( Shell shell, IChuruata joined )
		{
			super(shell );
			this.joined = joined;
			this.clicked = joined.getLocation();
			setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.MAX | SWT.RESIZE);
		}

		//Create a shade over the underlying composites when the dialog is activated 
		@Override
		protected void configureShell(Shell shell) {
			super.configureShell(shell);
			shell.setText( S_EDIT );
		}

		/**
		 * Create contents of the dialog.
		 * @param parent
		 */
		@Override
		protected Control createDialogArea(Composite parent)
		{
			Composite container = (Composite) super.createDialogArea(parent);
			container.setData( RWT.CUSTOM_ITEM_HEIGHT, Integer.valueOf( 10 ));
			container.setLayout( new FillLayout() );
			active = new ShowChuruatasComposite( container, SWT.NONE, clicked );
			if( joined != null )
				active.setInput(joined, true);
			active.setInput( churuatas.getChuruatas() );
			active.addEditListener( e->onCompositeEdited(e));
			return container;
		}

		protected void onCompositeEdited( EditEvent<IChuruata> event ) {
			Button button = getButton( IDialogConstants.OK_ID );
			if( button != null )
				button.setEnabled(EditEvent.EditTypes.COMPLETE.equals(event.getType()));
		}
				
		/**
		 * Create contents of the button bar.
		 * @param parent
		 */
		@Override
		protected void createButtonsForButtonBar(Composite parent)
		{
			createButton(parent, IDialogConstants.OK_ID, "OK",
					false);
			createButton(parent, IDialogConstants.CANCEL_ID,
					"Cancel", true);
			getButton( IDialogConstants.OK_ID ).setEnabled( false );
		}

		/**
		 * Return the initial size of the dialog.
		 */
		@Override
		protected Point getInitialSize(){
			size = new Point( 800, 600 );
			return size;
		}
				
		/**
		 * Response to pressing the OK-button
		 */
		public IChuruata  onOkButtonPressed(){
			Logger logger = Logger.getLogger( this.getClass().getName() );
			logger.info("OK Selected");
			IChuruata churuata = active.getInput();
			try {
				if( !churuatas.contains(churuata))
					churuatas.addChuruata((Churuata) churuata);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return churuata;
		}
	}
}
