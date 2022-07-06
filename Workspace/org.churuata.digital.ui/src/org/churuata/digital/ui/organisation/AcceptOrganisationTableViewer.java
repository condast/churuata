package org.churuata.digital.ui.organisation;

import java.util.ArrayList;
import java.util.Collection;

import org.churuata.digital.core.data.OrganisationData;
import org.churuata.digital.ui.ChuruataLanguage;
import org.condast.commons.Utils;
import org.condast.commons.ui.celleditors.AbstractCheckBoxCellEditor;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.EditEvent.EditTypes;
import org.condast.commons.ui.edit.CheckBoxEditingSupport;
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

public class AcceptOrganisationTableViewer extends AbstractTableViewerWithDelete<OrganisationData>{
	private static final long serialVersionUID = 1L;

	private enum Columns{
		NAME,
		WEBSITE, 
		DESCRIPTION,
		VERIFIED,
		TRUST;

		@Override
		public String toString() {
			return ChuruataLanguage.getInstance().getString( this );
		}

		public static int getWeight( Columns column ){
			switch( column ){
			case DESCRIPTION:
				return 50;
			case WEBSITE:
			case NAME:
				return 30;
			default:
				return 10;
			}
		}
	}

	public AcceptOrganisationTableViewer(Composite parent,int style ) {
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

	public OrganisationData[] getInput(){
		Collection<OrganisationData> contacts = new ArrayList<OrganisationData>();
		if( Utils.assertNull( super.getInput() ))
			return null;
		for( Object obj: super.getInput() ){
			contacts.add( (OrganisationData) obj );				
		}
		return contacts.toArray( new OrganisationData[ contacts.size() ]);
	}
	
	public void setInput( Collection<OrganisationData> contacts ){
		super.setInput( contacts );
	}
	
	@Override
	protected void onRowDoubleClick(OrganisationData selection) {
		/* NOTHING */
	}

	@Override
	protected void onButtonCreated(Buttons type, Button button) {
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
	protected boolean onDeleteButton(Collection<OrganisationData> deleted) {
		if( Utils.assertNull(deleted))
			return false;
		EditEvent<OrganisationData> event = new EditEvent<OrganisationData>( this, EditTypes.DELETE, deleted);
		notifyEditEvent( event);
		return true;
	}

	private TableViewerColumn createColumn( final Columns column ) {
		TableViewerColumn result = super.createColumn( column.toString(), column.ordinal(), Columns.getWeight(column) );
		switch( column) {
		case VERIFIED:
			result.setEditingSupport( new CheckBoxEditingSupport<>( getViewer(), new VerifyCheckBoxEditor() ) );
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
			IStoreWithDelete<OrganisationData> swd = (IStoreWithDelete<OrganisationData>) element;
			OrganisationData organisation = swd.getStore();
			switch( column){
			case NAME:
				retval = organisation.getName();
				break;
			case WEBSITE:
				retval = organisation.getWebsite();
				break;
			case DESCRIPTION:
				retval = organisation.getDescription();
				break;
			case TRUST:
				retval =  String.valueOf( organisation.getScore() );
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
				return image;//delete image
			IStoreWithDelete<OrganisationData> swd = (IStoreWithDelete<OrganisationData>) arg0;
			if( swd.getCount() == 1 )
				return null;

			Columns column = Columns.values()[ columnIndex ];
			OrganisationData organisation = swd.getStore();
			switch( column){
			case VERIFIED:
				image = setCheckedButton( true, organisation.isVerified());
				break;
			default:
				break;				
			}
			return image;
		}
	}
	
	private class VerifyCheckBoxEditor extends AbstractCheckBoxCellEditor<StoreWithDelete>{
		private static final long serialVersionUID = 1L;

		@Override
		protected void onToggle() {
			OrganisationData organisation = super.getData().getStore();
			boolean value = organisation.isVerified();
			organisation.setVerified( !value );
			notifyEditEvent(new EditEvent<OrganisationData>( this, EditTypes.CHANGED, organisation));
		}

		@Override
		protected Object doGetValue() {
			OrganisationData organisation = super.getData().getStore();
			return organisation.isVerified();
		}

		@Override
		protected void doSetValue( Object value ) {
			OrganisationData organisation = super.getData().getStore();
			organisation.setVerified((boolean)value );
		}
	}
}