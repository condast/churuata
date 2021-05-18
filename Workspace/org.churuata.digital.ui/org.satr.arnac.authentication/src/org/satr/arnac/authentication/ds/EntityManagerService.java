package org.satr.arnac.authentication.ds;

import javax.persistence.EntityManagerFactory;

import org.condast.commons.persistence.service.EntityManagerFactoryService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.satr.arnac.authentication.Activator;
import org.satr.arnac.authentication.core.Dispatcher;

/**
 * Used by Gemini Blueprint
 * @author Kees
 *
 */
@Component( name="org.satr.arnac.authentication.entity.service")
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