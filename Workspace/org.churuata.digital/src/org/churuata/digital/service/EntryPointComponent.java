package org.churuata.digital.service;

import java.util.logging.Logger;

import org.churuata.digital.core.Dispatcher;
import org.condast.commons.ui.provider.ICompositeProvider;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(
	name = EntryPointComponent.COMPONENT_NAME
)
public class EntryPointComponent{

	public static final String COMPONENT_NAME = "org.churuata.digital.dashboard.service.entry.point";

	private Dispatcher dispatcher = Dispatcher.getInstance();
	
    private static final Logger logger = Logger.getLogger( EntryPointComponent.class.getName());
    
	@Activate
	public void activate(){
		logger.info("Activating the " + COMPONENT_NAME);		
	}

	@Deactivate
	public void deactivate(){
		logger.info("Deactivating the" + COMPONENT_NAME);				
	}

	@Reference( cardinality = ReferenceCardinality.MANDATORY,
			policy=ReferencePolicy.DYNAMIC,
			target="(type=churuata)")
	public void setFactory( ICompositeProvider<Composite> provider ){
		dispatcher.addEntryPoint(provider);
	}

	public void unsetFactory( ICompositeProvider<Composite> provider ){
		dispatcher.removeEntryPoint(provider);
	}
}
