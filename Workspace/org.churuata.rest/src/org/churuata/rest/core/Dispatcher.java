package org.churuata.rest.core;

import java.util.Map;

import javax.persistence.EntityManager;

import org.condast.commons.persistence.service.AbstractPersistencyService;

public class Dispatcher extends AbstractPersistencyService {

	//Needs to be the same as in the persistence.xml file
	private static final String S_CHURUATA_SERVICE_ID = "org.churuata.rest.service"; 
	private static final String S_CHURUATA_SERVICE = "Churuata REST Service"; 

	private EntityManager manager;
	
	private static Dispatcher dispatcher = new Dispatcher();
	private Dispatcher() {
		super( S_CHURUATA_SERVICE_ID, S_CHURUATA_SERVICE );
	}

	public static Dispatcher getInstance() {
		return dispatcher;
	}
		
	@Override
	protected Map<String, String> onPrepareManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onManagerCreated(EntityManager manager) {
		this.manager = manager;
	}

	public void subscribe(long id, int i) {
		// TODO Auto-generated method stub
		
	}

}
