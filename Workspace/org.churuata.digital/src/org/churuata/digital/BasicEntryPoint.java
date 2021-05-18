package org.churuata.digital;

import org.churuata.digital.ui.map.MapBrowser;
import org.condast.commons.ui.entry.AbstractRestEntryPoint;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;


public class BasicEntryPoint extends AbstractRestEntryPoint {
	private static final long serialVersionUID = 1L;

	public static final String S_CHURUATA = "Churuata-Digital";

	public static final String S_CHURUATA_PAGE = "/churuata";

	private MapBrowser mapComposite;
	
	@Override
	protected boolean prepare(Composite parent) {
		return true;
	}

	@Override
    protected Composite createComposite(Composite parent) {
        parent.setLayout(new FillLayout());
        mapComposite = new MapBrowser( parent, SWT.NONE);
 		mapComposite.setData( RWT.CUSTOM_VARIANT, S_CHURUATA );
 		return mapComposite;
    }

	@Override
	protected boolean postProcess(Composite parent) {
		mapComposite.locate();
		return super.postProcess(parent);
	}	
}
