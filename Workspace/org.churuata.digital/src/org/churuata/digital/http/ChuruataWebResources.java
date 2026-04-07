package org.churuata.digital.http;

import java.util.logging.Logger;

import org.osgi.service.component.annotations.Component;
//import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;

@Component( service = ChuruataWebResources.class )
//@HttpWhiteboardResource(pattern="/churuata/web/*", prefix="/WEB-INF")
public class ChuruataWebResources {

	private Logger logger = Logger.getLogger(ChuruataWebResources.class.getName());

	public ChuruataWebResources() {
		logger.info("**** RESOURCES LOADED: " + this.getClass().getName()); 
	}
}
