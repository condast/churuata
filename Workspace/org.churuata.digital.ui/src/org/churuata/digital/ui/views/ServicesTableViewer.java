package org.churuata.digital.ui.views;

import java.util.ArrayList;
import java.util.Collection;

import org.churuata.digital.core.location.IChuruataService;
import org.churuata.digital.ui.ChuruataLanguage;
import org.condast.commons.Utils;
import org.condast.commons.ui.controller.EditEvent.EditTypes;
import org.condast.commons.ui.na.NALanguage;
import org.condast.commons.ui.table.AbstractTableViewerWithDelete;
import org.condast.commons.ui.widgets.IStoreWithDelete;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ServicesTableViewer extends AbstractTableViewerWithDelete<IChuruataService>{
	private static final long serialVersionUID = 1L;

	private enum Columns{
		SERVICE,
		CONTRIBUTOR, DESCRIPTION;

		@Override
		public String toString() {
			return ChuruataLanguage.getInstance().getString( this );
		}

		public static int getWeight( Columns column ){
			switch( column ){
			case CONTRIBUTOR:
			case SERVICE:
				return 30;
			default:
				return 10;
			}
		}
	}

	public ServicesTableViewer(Composite parent,int style ) {
		super(parent,style, true );
	}

	@Override
	protected void createContentComposite( Composite parent,int style ){
		super.createContentComposite(parent, style);
		TableViewer viewer = super.getViewer();
		for( Columns column: Columns.values() ){
			createColumn( column );
		}
		String deleteStr = NALanguage.getInstance().getString( Buttons.DELETE );
		super.createDeleteColumn( Columns.values().length, deleteStr, 10 );	
		viewer.setLabelProvider( new ServicesLabelProvider() );
	}
	
	public IChuruataService[] getInput(){
		Collection<IChuruataService> contacts = new ArrayList<IChuruataService>();
		if( Utils.assertNull( super.getInput() ))
			return null;
		for( Object obj: super.getInput() ){
			contacts.add( (IChuruataService) obj );				
		}
		return contacts.toArray( new IChuruataService[ contacts.size() ]);
	}
	
	public void setInput( Collection<IChuruataService> contacts ){
		super.setInput( contacts );
	}
	
	@Override
	protected void onButtonCreated(Buttons type, Button button) {
		GridData gd_button = new GridData(32, 32);
		gd_button.horizontalAlignment = SWT.RIGHT;
		button.setLayoutData(gd_button);
		button.setText("");
	}

	@Override
	protected boolean onButtonSelected(Buttons buttontype, SelectionEvent e) {
		boolean result = false;
		try {
			e.data = EditTypes.ADDED;
			notifyWidgetSelected( e );
			result = true;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return result;
	}
	
	@Override
	protected boolean onDeleteButton( Collection<IChuruataService> deleted ) {
		return true;
	}

	private TableViewerColumn createColumn( final Columns column ) {
		TableViewerColumn result = super.createColumn( column.toString(), column.ordinal(), Columns.getWeight(column) );
		return result;
	}
	
	@Override
	protected void onRefresh() {
		//setInput(ap);
	}
	
	private class ServicesLabelProvider extends DeleteLabelProvider implements ITableLabelProvider{
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		@Override
		public String getColumnText( Object element, int columnIndex ) {
			String retval = super.getColumnText(element, columnIndex);
			if( retval != null )
				return retval;
			Columns column = Columns.values()[ columnIndex ];
			IStoreWithDelete<IChuruataService> swd = (IStoreWithDelete<IChuruataService>) element;
			IChuruataService service = swd.getStore();
			switch( column){
			case SERVICE:
				retval = ChuruataLanguage.getInstance().getString( service.getService());
				break;
			case CONTRIBUTOR:
				retval = service.getContribution().name();
				break;
			case DESCRIPTION:
				retval = service.getDescription();
				break;
			default:
				break;				
			}
			swd.addText(retval);
			return retval;
		}
	}
}