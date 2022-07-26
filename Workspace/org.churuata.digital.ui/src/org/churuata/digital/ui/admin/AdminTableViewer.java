package org.churuata.digital.ui.admin;

import java.util.ArrayList;
import java.util.Collection;

import org.condast.commons.Utils;
import org.condast.commons.authentication.core.AdminData;
import org.condast.commons.authentication.core.LoginData;
import org.condast.commons.authentication.user.IAdmin;
import org.condast.commons.authentication.user.IAdmin.Roles;
import org.condast.commons.ui.celleditors.ComboCellEditor;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.EditEvent.EditTypes;
import org.condast.commons.ui.na.NALanguage;
import org.condast.commons.ui.table.AbstractTableViewerWithDelete;
import org.condast.commons.ui.widgets.IStoreWithDelete;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class AdminTableViewer extends AbstractTableViewerWithDelete<LoginData>{
	private static final long serialVersionUID = 1L;

	private enum Columns{
		USER_NAME,
		LOCATION,
		EMAIL, 
		ROLE;

		@Override
		public String toString() {
			return NALanguage.getInstance().getString( this );
		}

		public static int getWeight( Columns column ){
			switch( column ){
			case EMAIL:
			case USER_NAME:
				return 30;
			default:
				return 10;
			}
		}
	}

	public AdminTableViewer(Composite parent,int style ) {
		super(parent,style, false );
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
	
	public LoginData[] getInput(){
		Collection<LoginData> contacts = new ArrayList<LoginData>();
		if( Utils.assertNull( super.getInput() ))
			return null;
		for( Object obj: super.getInput() ){
			contacts.add( (LoginData) obj );				
		}
		return contacts.toArray( new LoginData[ contacts.size() ]);
	}
	
	public void setInput( Collection<LoginData> contacts ){
		super.setInput( contacts );
	}
	
	@Override
	protected void onRowDoubleClick(LoginData selection) {
		notifyEditEvent( new EditEvent<LoginData>( this, EditTypes.SELECTED, selection ));
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
	protected boolean onDeleteButton( Collection<LoginData> deleted ) {
		return true;
	}

	private TableViewerColumn createColumn( final Columns column ) {
		TableViewerColumn result = super.createColumn( column.toString(), column.ordinal(), Columns.getWeight(column) );
		switch( column ) {
		case ROLE:				
			result.setEditingSupport( new ComboEditingSupport( super.getViewer()));
		break;
		default:
			break;
		}
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
			IStoreWithDelete<LoginData> swd = (IStoreWithDelete<LoginData>) element;
			LoginData user = swd.getStore();
			switch( column){
			case USER_NAME:
				retval = user.getNickName();
				break;
			case EMAIL:
				retval = user.getEmail();
				break;
			//case LOCATION:
			//	retval = user.getLocation().toLocation();
			//	break;
			case ROLE:
				retval = user.getRole().toString();
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
			Image image = super.getColumnImage(arg0, columnIndex);
			if( image != null )
				return image; //delete checkbox

			IStoreWithDelete<LoginData> swd = (IStoreWithDelete<LoginData>) arg0;
			if( swd.getCount() == 1 )
				return null;
			Columns column = Columns.values()[ columnIndex ];
			switch( column){
			default:
				break;				
			}
			return image;
		}
	}
	
	private class ComboEditingSupport extends EditingSupport{
		private static final long serialVersionUID = 1L;
		
		private ComboCellEditor cellEditor;

		public ComboEditingSupport(ColumnViewer viewer) {
			super(viewer);
			this.cellEditor = new ComboCellEditor();
			this.cellEditor.create(getParent());
			this.cellEditor.setItems(IAdmin.Roles.getItems());
		}

		@Override
		protected boolean canEdit(Object arg0) {
			return true;
		}

		@Override
		protected CellEditor getCellEditor(Object arg0) {
			return this.cellEditor;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Object getValue(Object arg0) {
			IStoreWithDelete<LoginData> store = (IStoreWithDelete<LoginData>) arg0;
			LoginData login = store.getStore();
			AdminData admin = login.getAdmin();
			return ( admin == null )? Roles.UNKNOWN: admin.getRole().ordinal();
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void setValue(Object arg0, Object arg1) {
			IStoreWithDelete<LoginData> store = (IStoreWithDelete<LoginData>) arg0;
			LoginData login = store.getStore();
			int value = (int) arg1;
			AdminData admin = login.getAdmin();
			admin.setRole(Roles.values()[ value]);
			notifyEditEvent( new EditEvent<LoginData>( this, EditTypes.CHANGED, login));
			super.getViewer().update(admin, null);			
		}
	}
}