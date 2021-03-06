package org.churuata.rest.service;

import javax.persistence.EntityManagerFactory;

import org.churuata.rest.Activator;
import org.churuata.rest.core.Dispatcher;
import org.condast.commons.persistence.service.AbstractFactoryService;
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
public class EntityManagerService extends AbstractFactoryService<EntityManagerFactory>{

	private Dispatcher service = Dispatcher.getInstance();
	
	public EntityManagerService() {
		super( Activator.BUNDLE_ID );
	}
	
	
	@Reference( cardinality = ReferenceCardinality.MANDATORY,
	policy=ReferencePolicy.DYNAMIC)
	@Override
	public synchronized void bindEMF( EntityManagerFactory emf) {
		service.setEMF(emf);
		super.bindEMF(emf);
	}

	@Override
	public synchronized void unbindEMF( EntityManagerFactory emf) {
		service.disconnect();
		super.unbindEMF(emf);
	}
}