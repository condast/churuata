package org.churuata.digital.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.churuata.digital.core.data.OrganisationData;
import org.churuata.digital.core.location.IChuruataService;
import org.condast.commons.Utils;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.EditEvent.EditTypes;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.table.AbstractTableViewerWithDelete;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;

public class ChuruataTableComposite extends AbstractTableViewerWithDelete<IChuruataService>{
	private static final long serialVersionUID = 976428552549736382L;

	public static final String S_TABLECOLUMN_ID = "ChuruataTableColumn";

	public enum Columns{
		SERVICES,
		CONTRIBUTOR,
		DESCRIPTION,
		CONTRIBUTION,
		FROM,
		TO;

		public int getWeight() {
			int[] bounds = { 40, 30, 100, 30, 30, 30 };
			return bounds[ordinal()];
		}

		@Override
		public String toString() {
			return StringStyler.prettyString( super.name());
		}
	}
	
	private Collection<IEditListener<IChuruataService>> listeners;

	private Composite container;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ChuruataTableComposite( Composite parent, int style){
		super( parent, style, true);
		this.container = this;
		listeners = new ArrayList<>();
	}

	public void addEditListener( IEditListener<IChuruataService> listener ) {
		this.listeners.add(listener);
	}

	public void removeEditListener( IEditListener<IChuruataService> listener ) {
		this.listeners.remove(listener);
	}

	private void notifyEditListeners( EditEvent<IChuruataService> event ) {
		for( IEditListener<IChuruataService> listener: this.listeners ) {
			listener.notifyInputEdited(event);
		}
	}

	@Override
	protected void createContentComposite( Composite parent,int style ){
		super.createContentComposite(parent, style);
		TableViewer viewer = super.getViewer();
		for( Columns column: Columns.values() ){
			createColumn( column );
		}
		String deleteStr = Buttons.DELETE.toString();
		super.createDeleteColumn( Columns.values().length, deleteStr, 10 );
		viewer.setLabelProvider( new ChuruataLabelProvider() );
	}

	public IChuruataService[] getInput() {
		Collection<IChuruataService> types = new ArrayList<>();
		if( !Utils.assertNull(super.getInput())) {
			for( Object tp: super.getInput() )
				types.add((IChuruataService) tp);
		}
		return types.toArray( new IChuruataService[ types.size()]);
	}
	
	protected void setInput( OrganisationData churuata) {
		super.setInput( Arrays.asList( churuata.getServices()));
	}

	@Override
	protected void onRowDoubleClick(IChuruataService selection) {
		try{
			notifyEditListeners(new EditEvent<>( container, EditTypes.SELECTED, selection));
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
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
		try{
			notifyEditListeners(new EditEvent<IChuruataService>( container, EditTypes.ADDED));
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
		return result;
	}

	@Override
	protected boolean onDeleteButton( Collection<IChuruataService> deleted ) {
		Collection<Long> ids = new ArrayList<>();
		boolean result = false;
		for( IChuruataService vessel: deleted )
			ids.add(vessel.getId());
		//try {
			//Map<String, String> params = controller.getUserParams(user.getId(), user.getSecurity());
			//Gson gson = new Gson();
			//String data = gson.toJson(ids.toArray(new Long[ ids.size()]), Long[].class);
			//params.put( IChuruataType.Parameters.IDS.toString(), data);
			//controller.delete(IUserData.Requests.REMOVE_ALL, params, data);
			result = true;
		//} catch (IOException e) {
		//	e.printStackTrace();
		//}
		return result;
	}

	private TableViewerColumn createColumn( final Columns column ) {
		TableViewerColumn result = super.createColumn( column.toString(), column.ordinal(), column.getWeight() );
		return result;
	}

	@Override
	protected void onRefresh() {
		//updateTable( controller.getInput());
	}

	private class ChuruataLabelProvider extends DeleteLabelProvider{
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		@Override
		public String getColumnText(Object element, int columnIndex) {
			StoreWithDelete store = (AbstractTableViewerWithDelete<IChuruataService>.StoreWithDelete) element;
			String result = super.getColumnText(element, columnIndex);
			if( result != null )
				return result;
			Columns column = Columns.values()[ columnIndex ];
			IChuruataService p = (IChuruataService) store.getStore();
			try {
				switch( column ) {
				case SERVICES:
					result = p.getService().toString();
					break;
				case CONTRIBUTOR:
					result = p.getContribution().toString();
					break;
				case DESCRIPTION:
					result = p.getDescription();
					break;
				case FROM:
					result = p.from().toString();
					break;
				case TO:
					result = p.to().toString();
					break;
				default:
					break;
				}
			}
			catch( Exception ex ) {
				return super.getText(element);
			}
			return result;
		}

		public Image getColumnImage(Object element, int columnIndex) {				
			Image image = super.getColumnImage(element, columnIndex);
			if( columnIndex == getDeleteColumnindex() ){
				return image;
			}
			Columns column = Columns.values()[ columnIndex ];
			try {
				switch( column ) {
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
	}
}
