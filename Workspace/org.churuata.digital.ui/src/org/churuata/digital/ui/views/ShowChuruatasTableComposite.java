package org.churuata.digital.ui.views;

import java.util.logging.Logger;

import org.churuata.digital.core.location.Churuata;
import org.churuata.digital.ui.image.InformationImages;
import org.condast.commons.strings.StringStyler;
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
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;

public class ShowChuruatasTableComposite extends AbstractTableComposite<Churuata>{
	private static final long serialVersionUID = 976428552549736382L;

	public static final String S_TABLECOLUMN_ID = "ChuruataTableColumn";

	public enum Columns{
		NAME,
		DESCRIPTION,
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
	
	private ImageController controller;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ShowChuruatasTableComposite( Composite parent, int style){
		super( parent, style);
		setContentProvider( ArrayContentProvider.getInstance());
		controller = new ImageController( );
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
					Churuata p = (Churuata) element;
					try {
						switch( column ) {
						case NAME:
							result = p.getName().toString();
							break;
						case DESCRIPTION:
							result = p.getDescription();
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
					notifyTableEvent( new TableEvent<Churuata>( e.widget, TableEvents.VIEW_TABLE, getInput() ));
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

	/* (non-Javadoc)
	 * @see org.condast.eclipse.swt.composite.AbstractTableComposite#prepareInput(org.aieonf.concept.model.IModelLeaf)
	 */
	@Override
	protected void onSetInput(Churuata[] leaf){
	}

	@Override
	protected int compareTables(int columnIndex, Churuata o1, Churuata o2) {
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
			default:
				break;
		}	
	}
	
	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
}
