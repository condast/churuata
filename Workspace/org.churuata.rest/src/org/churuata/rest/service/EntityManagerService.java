package org.churuata.rest.service;

import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.EntityManagerFactory;

import org.churuata.rest.Activator;
import org.churuata.rest.core.Dispatcher;
import org.condast.commons.persistence.service.EntityManagerFactoryService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * Used by Gemini Blueprint
 * @author Kees
 *
 */
@Component( name="org.churuata.rest.entity.service",
immediate=true)
public class EntityManagerService extends EntityManagerFactoryService{

	private Dispatcher service = Dispatcher.getInstance();
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public EntityManagerService() {
		super( Activator.BUNDLE_ID );
	}
		
	@Reference( cardinality = ReferenceCardinality.AT_LEAST_ONE,
	policy=ReferencePolicy.DYNAMIC)
	@Override
	public synchronized void bindEMF( EntityManagerFactory emf) {
		Map<String,Object> props = emf.getProperties();
		String attr = (String) props.get(BUNDLE_NAME_KEY);
		logger.info("Attempting to bind factory: " + attr);

		if( !compare( emf, BUNDLE_NAME_KEY, Activator.BUNDLE_ID))
			return;
		logger.info("FACTORY FOUND: " + BUNDLE_NAME_KEY);
		service.setEMF(emf);
		super.bindEMF(emf);
		logger.info("FACTORY BOUND succesfully ");
	}

	@Override
	public synchronized void unbindEMF( EntityManagerFactory emf) {
		if( !compare( emf, BUNDLE_NAME_KEY, Activator.BUNDLE_ID))
			return;
		service.disconnect();
		super.unbindEMF(emf);
	}
}