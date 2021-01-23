package org.churuata.rest.core;

import java.util.Map;

import javax.persistence.EntityManager;

import org.condast.commons.persistence.service.AbstractPersistencyService;

public class Dispatcher extends AbstractPersistencyService {

	//Needs to be the same as in the persistence.xml file
	private static final String S_COVAID_SERVICE_ID = "org.covaid.rest.service"; 
	private static final String S_COVAID_SERVICE = "CovAID REST Service"; 

	
	private EntityManager manager;
	
	private static Dispatcher dispatcher = new Dispatcher();
	
	private Dispatcher() {
		super( S_COVAID_SERVICE_ID, S_COVAID_SERVICE );
	}

	public static Dispatcher getInstance() {
		return dispatcher;
	}
		
	public boolean isRegistered( String identifier, String token ) {
		return false;
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
