package org.churuata.digital.authentication.http;

import java.util.logging.Logger;

import org.osgi.service.component.annotations.Component;
//import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;

@Component( service = LegalResources.class )
//@HttpWhiteboardResource(pattern="/churuata.digital/legal/*", prefix="/legal")
public class LegalResources {

	private Logger logger = Logger.getLogger(LegalResources.class.getName());

	public LegalResources() {
		logger.info("**** RESOURCES LOADED: " + this.getClass().getName()); 
	}
}
