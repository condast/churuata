package org.churuata.digital.service;

import java.util.logging.Logger;

import org.churuata.digital.core.AuthenticationDispatcher;
import org.condast.commons.authentication.core.ILoginProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(
	name = AuthenticationComponent.COMPONENT_NAME
)
public class AuthenticationComponent{

	public static final String COMPONENT_NAME = "org.churuata.digital.dashboard.service.login";

	private AuthenticationDispatcher dispatcher = AuthenticationDispatcher.getInstance();
	
    private static final Logger logger = Logger.getLogger( AuthenticationComponent.class.getName());
    
	@Activate
	public void activate(){
		logger.info("Activating the " + COMPONENT_NAME);		
	}

	@Deactivate
	public void deactivate(){
		logger.info("Deactivating the" + COMPONENT_NAME);				
	}

	@Reference( cardinality = ReferenceCardinality.AT_LEAST_ONE,
			policy=ReferencePolicy.DYNAMIC)
	public void setFactory( ILoginProvider login ){
		dispatcher.setLoginProvider(login);
	}

	public void unsetFactory( ILoginProvider login ){
		dispatcher.unsetLoginProvider(login);
	}
}
