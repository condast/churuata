package org.churuata.digital;

import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.ui.map.MapBrowser;
import org.condast.commons.ui.xml.XMLFactoryBuilder;
import org.condast.commons.xml.AbstractXMLBuilder.Selection;
import org.condast.commons.xml.BuildEvent;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;


public class BasicEntryPoint extends AbstractEntryPoint {
	private static final long serialVersionUID = 1L;

	public static final String S_CHURUATA = "Churuata-Digital";

	private Dispatcher dispatcher = Dispatcher.getInstance();
	
	@Override
    protected void createContents(Composite parent) {
        parent.setLayout(new FillLayout());
        XMLFactoryBuilder builder = new XMLFactoryBuilder( parent, this.getClass());
        builder.addListener(e->onBuilderEvent(e));
        builder.build();
        Composite root = builder.getRoot();
 		root.setData( RWT.CUSTOM_VARIANT, S_CHURUATA );
 		dispatcher.initSessionStore(RWT.getUISession());
    }

	private void onBuilderEvent( BuildEvent<Widget> event) {
		try {
			if(!"COMPLETE".equals(event.getEvent()))
				return;
			if( !Selection.isOfSelection(event.getName()))
				return;
			switch( Selection.valueOf( event.getName())) {
			case COMPOSITE:
				MapBrowser browser = (MapBrowser) event.getData();
				browser.setInput(dispatcher);
				break;
			default:
				break;
			}
		}catch( Exception ex ) {
			ex.printStackTrace();
		}
		
	}
}
