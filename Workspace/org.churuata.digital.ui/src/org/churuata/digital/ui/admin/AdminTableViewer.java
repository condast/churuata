package org.churuata.digital.ui.admin;

import java.util.ArrayList;
import java.util.Collection;

import org.churuata.digital.ui.ChuruataLanguage;
import org.condast.commons.Utils;
import org.condast.commons.authentication.user.IAdmin;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.ui.controller.EditEvent.EditTypes;
import org.condast.commons.ui.na.NALanguage;
import org.condast.commons.ui.table.AbstractTableViewerWithDelete;
import org.condast.commons.ui.widgets.IStoreWithDelete;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class AdminTableViewer extends AbstractTableViewerWithDelete<IAdmin>{
	private static final long serialVersionUID = 1L;

	private enum Columns{
		NAME,
		FULL_NAME,
		EMAIL,
		ROLE;

		@Override
		public String toString() {
			return ChuruataLanguage.getInstance().getString( this );
		}

		public static int getWeight( Columns column ){
			switch( column ){
			case NAME:
			case ROLE:
				return 30;
			default:
				return 10;
			}
		}
	}

	private Button addbutton;

	public AdminTableViewer(Composite parent,int style ) {
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
	
	public Button getAddButton() {
		return addbutton;
	}

	public IAdmin[] getInput(){
		Collection<IAdmin> contacts = new ArrayList<IAdmin>();
		if( Utils.assertNull( super.getInput() ))
			return null;
		for( Object obj: super.getInput() ){
			contacts.add( (IAdmin) obj );				
		}
		return contacts.toArray( new IAdmin[ contacts.size() ]);
	}
	
	public void setInput( Collection<IAdmin> contacts ){
		super.setInput( contacts );
	}
	
	@Override
	protected void onRowDoubleClick(IAdmin selection) {
		/* NOTHING */
	}

	@Override
	protected void onButtonCreated(Buttons type, Button button) {
		switch( type ) {
		case ADD:
			this.addbutton = button;
			this.addbutton.setEnabled(false);
			break;
		default:
			break;
		}
		GridData gd_button = new GridData(32, 32);
		gd_button.horizontalAlignment = SWT.RIGHT;
		button.setLayoutData(gd_button);
		button.setText("");
	}

	@Override
	protected boolean onAddButtonSelected(SelectionEvent e) {
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
	protected boolean onDeleteButton( Collection<IAdmin> deleted ) {
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
			IStoreWithDelete<IAdmin> swd = (IStoreWithDelete<IAdmin>) element;
			IAdmin admin = swd.getStore();
			ILoginUser user = admin.getUser();
			switch( column){
			case NAME:
				retval = user.getUserName();
				break;
			case EMAIL:
				retval = user.getEmail();
				break;
			case ROLE:
				retval = ChuruataLanguage.getInstance().getString( admin.getRole().toString());
				break;
			default:
				break;				
			}
			swd.addText(retval);
			return retval;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Image getColumnImage(Object arg0, int columnIndex) {
			if( columnIndex == getDeleteColumnindex() ){
				IStoreWithDelete<IAdmin> swd = (IStoreWithDelete<IAdmin>) arg0;
				if( swd.getCount() == 1 )
					return null;
			}
			return super.getColumnImage(arg0, columnIndex);
		}
	}
}