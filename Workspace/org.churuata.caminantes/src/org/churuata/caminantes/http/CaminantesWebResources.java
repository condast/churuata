package org.churuata.caminantes.http;

import java.util.logging.Logger;

import org.osgi.service.component.annotations.Component;
//import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;

@Component( service = CaminantesWebResources.class )
//@HttpWhiteboardResource(pattern="/caminantes/web/*", prefix="/WEB-INF")
public class CaminantesWebResources {

	private Logger logger = Logger.getLogger(CaminantesWebResources.class.getName());

	public CaminantesWebResources() {
		logger.info("**** RESOURCES LOADED: " + this.getClass().getName()); 
	}
}
