package org.churuata.digital.ui.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.churuata.digital.core.location.IChuruata;
import org.churuata.digital.core.location.IChuruataType;
import org.churuata.digital.core.location.IChuruata.Requests;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.ui.image.InformationImages;
import org.churuata.digital.ui.image.IInformationImages.Information;
import org.churuata.digital.ui.views.EditChuruataComposite.Parameters;
import org.condast.commons.Utils;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.ui.controller.EditEvent.EditTypes;
import org.condast.commons.ui.image.ImageController;
import org.condast.commons.ui.table.AbstractTableComposite;
import org.condast.commons.ui.table.ITableEventListener.TableEvents;
import org.condast.commons.ui.table.TableEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.rap.rwt.RWT;

public class ChuruataTableComposite extends AbstractTableComposite<IChuruataType>{
	private static final long serialVersionUID = 976428552549736382L;

	public static final String S_TABLECOLUMN_ID = "ChuruataTableColumn";

	public enum Columns{
		NAME,
		DESCRIPTION,
		TYPE,
		DELETE;

		public int getWeight() {
			int[] bounds = { 50, 100, 5, 5 };
			return bounds[ordinal()];
		}

		@Override
		public String toString() {
			return StringStyler.prettyString( super.name());
		}
	}
	
	private ImageController icontroller;
	
	private WebController controller;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ChuruataTableComposite( Composite parent, int style){
		super( parent, style);
		controller = new WebController( );
		icontroller = new ImageController( );
	}

	/**
	 * Initialise the composite
	 */
	@Override
	protected void prepare() {
		//Description
		for( Columns column: Columns.values()) {
			ColumnLabelProvider clp = new ColumnLabelProvider(){
				private static final long serialVersionUID = 1L;

				@Override
				public String getText(Object element) {
					String result = null;
					IChuruataType p = (IChuruataType) element;
					try {
						switch( column ) {
						case NAME:
							result = p.getType().toString();
							break;
						case DESCRIPTION:
							result = p.getDescription();
							break;
						case TYPE:
							result = p.getType().toString();
							break;
						case DELETE:
							break;
						}
					}
					catch( Exception ex ) {
						return super.getText(element);
					}
					return result;
				}

				public Image getImage(Object element) {				
					Image image = super.getImage(element);
					try {
						switch( column ) {
						case DELETE:
/*
							LabelProviderImages images= new LabelProviderImages();
							Churuata model = (Churuata) element;
							if( model.isReadOnly()) {
								return null;
							}
							Boolean result = isInList( model );
							image = images.getChecked((result==null)?false:result);
							*/
							break;
						default:
							image = null;
							break;
						}
					}
					catch( Exception ex ) {
						ex.printStackTrace();
					}
					return image;
				}

				/* (non-Javadoc)
				 * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
				 */
				@Override
				public String getToolTipText(Object element){
					return null;//Descriptor.getText(element);
				}		
			};
			super.addColumnLabelProvider(clp);
		}	
	}

	// This will create the columns for the table
	@Override
	protected void createColumns(final Composite parent, final TableViewer viewer) {
		TableViewerColumn column = null;
		InformationImages images = InformationImages.getInstance();		
		for( Columns col: Columns.values()) {
			column = this.registerColum( S_TABLECOLUMN_ID, SWT.NONE, col.getWeight(), col.ordinal() );
			switch( col ) {
			case DESCRIPTION:
				column.getColumn().setText(col.toString());
				column.getColumn().addListener(SWT.Selection, e->{				
					notifyTableEvent( new TableEvent<IChuruataType>( e.widget, TableEvents.VIEW_TABLE, getInput() ));
				});
				break;
			case TYPE:
				Image image = images.getImage( Information.EDIT, true );
				column.getColumn().setImage( image);
				column.getColumn().addListener(SWT.Selection, e->{				
					notifyTableEvent( new TableEvent<IChuruataType>( e.widget, TableEvents.SELECT, getInput() ));
				});
				break;
			case DELETE:
				//super.setDeleteColumn(column, true);
				break;
			default:
				column.getColumn().setText(col.toString());
				break;
			}
		}
	}

	@Override
	protected void initTableColumnLayout(TableColumnLayout tclayout)
	{
		Table table = super.getTableViewer().getTable();
		for( Columns col: Columns.values())
			tclayout.setColumnData( table.getColumn( col.ordinal()), new ColumnWeightData( col.getWeight() ) );
	}

	public void setInput( String context ){
		controller.setInput(context, IRestPages.Pages.SUPPORT.toPath());
		controller.services();
	}

	/* (non-Javadoc)
	 * @see org.condast.eclipse.swt.composite.AbstractTableComposite#prepareInput(org.aieonf.concept.model.IModelLeaf)
	 */
	@Override
	protected void onSetInput(IChuruataType[] input){
	}

	@Override
	protected int compareTables(int columnIndex, IChuruataType o1, IChuruataType o2) {
		Columns column = Columns.values()[columnIndex];
		int result = 0;
		switch( column) {
		case DESCRIPTION:
			break;
		default:
			break;
		}
		return result;
	}

	@Override
	public void onHeaderClicked(SelectionEvent e){
		if( !( e.getSource() instanceof TableColumn ))
			return;
		TableColumn col = ( TableColumn )e.getSource();
		int index = ( Integer )col.getData( S_INDEX );
		Columns column = Columns.values()[index];
		switch( column ){
			case DESCRIPTION:
				break;
			case TYPE:
				//RWTUtils.redirect( S_EDIT_LOCATION );
				break;
			default:
				break;
		}	
	}
	
	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

	private class WebController extends AbstractHttpRequest<IChuruata.Requests, LatLng[]>{
		
		private Collection<LatLng> churuatas;
		
		public WebController() {
			super();
			churuatas = new ArrayList<>();
		}

		public void setInput(String context, String path) {
			super.setContextPath(context + path);
		}

		public void services() {
			Map<String, String> params = new HashMap<>();
			try {
				HttpSession session = RWT.getUISession().getHttpSession();		
				LatLng selected = (LatLng) session.getAttribute( EditTypes.SELECTED.name());
				params.put(Parameters.LAT.toString(), String.valueOf( selected.getLatitude()));
				params.put(Parameters.LON.toString(), String.valueOf( selected.getLongitude()));
				sendGet(IChuruata.Requests.FIND, params);
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}
		
		@Override
		protected String onHandleResponse(ResponseEvent<Requests, LatLng[]> event, LatLng[] data) throws IOException {
			try {
				switch( event.getRequest()){
				case FIND:
					churuatas.clear();
					Gson gson = new Gson();
					int xmax = 10000; int ymax = 10000;
					LatLng[] results = gson.fromJson(event.getResponse(), LatLng[].class);
					if(!Utils.assertNull(results))
						churuatas.addAll(Arrays.asList(results));
					break;
				default:
					break;
				}
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
			finally {
				//updateMap();
			}
			return null;
		}
		
	}

}
