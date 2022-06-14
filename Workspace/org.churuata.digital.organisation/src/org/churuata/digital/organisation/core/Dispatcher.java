package org.churuata.digital.organisation.core;

import org.condast.commons.persistence.service.AbstractPersistencyService;
import org.condast.commons.persistence.service.IPersistenceService;

public class Dispatcher extends AbstractPersistencyService implements IPersistenceService{

	public static final String S_CHURUATA = "churuata";

	//Needs to be the same as in the persistence.xml file
	private static final String S_CHURUATA_SERVICE_ID = "org.churuata.digital.authentication"; 
	private static final String S_CHURUATA_SERVICE = "Churuata Digital Authentication Service"; 

	private static Dispatcher service = new Dispatcher();
	
	private Dispatcher(  ) {
		super( S_CHURUATA_SERVICE_ID, S_CHURUATA_SERVICE );
	}

	public static Dispatcher getInstance(){
		return service;
	}
}