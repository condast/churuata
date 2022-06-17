package org.churuata.digital.organisation.ds;

import java.util.logging.Logger;

import org.churuata.digital.organisation.core.AuthenticationDispatcher;
import org.condast.commons.authentication.core.ILoginProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(
	name = AuthenticationComponent.COMPONENT_NAME, immediate=true
)
public class AuthenticationComponent{

	public static final String COMPONENT_NAME = "org.churuata.digital.organisation.service.login";

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

	@Reference( cardinality = ReferenceCardinality.MANDATORY,
			policy=ReferencePolicy.DYNAMIC,
			target="(type=churuata)")
	public void setFactory( ILoginProvider loginProvider ){
		dispatcher.setLoginProvider(loginProvider);
	}

	public void unsetFactory( ILoginProvider factory ){
		dispatcher.unsetLoginProvider(factory);
	}
}
