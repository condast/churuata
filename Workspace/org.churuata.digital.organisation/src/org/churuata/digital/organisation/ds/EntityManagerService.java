package org.churuata.digital.organisation.ds;

import javax.persistence.EntityManagerFactory;

import org.churuata.digital.organisation.Activator;
import org.churuata.digital.organisation.core.Dispatcher;
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
@Component( name="org.churuata.digital.authentication.entity.service",
immediate=true)
public class EntityManagerService extends EntityManagerFactoryService{
		
	Dispatcher service = Dispatcher.getInstance();		

	public EntityManagerService() {
		super( Activator.BUNDLE_ID );
	}
	
	@Reference( cardinality = ReferenceCardinality.AT_LEAST_ONE,
			policy=ReferencePolicy.DYNAMIC)
	@Override
	public synchronized void bindEMF(EntityManagerFactory emf) {
		if( !compare( emf, BUNDLE_NAME_KEY, Activator.BUNDLE_ID))
			return;
		service.setEMF(emf);
		super.bindEMF(emf);
	}

	@Override
	public synchronized void unbindEMF(EntityManagerFactory emf) {
		if( !compare( emf, BUNDLE_NAME_KEY, Activator.BUNDLE_ID))
			return;
		service.disconnect();
		super.unbindEMF(emf);
	}
}