package org.churuata.digital.core;

import org.churuata.digital.core.location.Churuata;
import org.churuata.digital.core.location.ChuruataTypes;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ChuruataProvider implements ITreeContentProvider {
	private static final long serialVersionUID = 4294651251926296097L;

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);	
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if( parentElement instanceof Churuata[] )
			return (Object[]) parentElement;
		if(!( parentElement instanceof Churuata ))
			return null;
		Churuata churuata = (Churuata) parentElement;
		return churuata.getTypes();
	}

	@Override
	public Object getParent(Object element) {
		if(!( element instanceof ChuruataTypes ))
			return null;
		ChuruataTypes type = (ChuruataTypes) element;
		return type.getParent();
	}

	@Override
	public boolean hasChildren(Object element) {
		return ( element instanceof Churuata );
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}
}
