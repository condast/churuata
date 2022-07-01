package org.churuata.digital.ui.organisation;

import java.util.ArrayList;
import java.util.Collection;

import org.churuata.digital.core.data.OrganisationData;
import org.churuata.digital.ui.ChuruataLanguage;
import org.churuata.digital.ui.image.ChuruataImages;
import org.churuata.digital.ui.image.ChuruataImages.Images;
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
	protected boolean onDeleteButton( Collection<OrganisationData> deleted ) {
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
			IStoreWithDelete<OrganisationData> swd = (IStoreWithDelete<OrganisationData>) arg0;
			if( swd.getCount() == 1 )
				return null;

			Columns column = Columns.values()[ columnIndex ];
			OrganisationData organisation = swd.getStore();
			ChuruataImages images = ChuruataImages.getInstance();
			switch( column){
			case VERIFIED:
				image = organisation.isVerified()? images.getImage( Images.CHECK): image;
				break;
			default:
				break;				
			}
			return image;
		}
	}
}