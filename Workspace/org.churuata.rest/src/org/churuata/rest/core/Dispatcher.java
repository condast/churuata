package org.churuata.rest.core;

import java.util.ArrayList;
import java.util.Collection;

import org.churuata.digital.core.location.ChuruataData;
import org.condast.commons.data.plane.FieldData;
import org.condast.commons.persistence.service.AbstractPersistencyService;

public class Dispatcher extends AbstractPersistencyService {

	//Needs to be the same as in the persistence.xml file
	private static final String S_CHURUATA_SERVICE_ID = "org.churuata.rest.service"; 
	private static final String S_CHURUATA_SERVICE = "Churuata REST Service"; 
		
	private Collection<ChuruataData> results;
	private FieldData fieldData;

	private static Dispatcher dispatcher = new Dispatcher();
	private Dispatcher() {
		super( S_CHURUATA_SERVICE_ID, S_CHURUATA_SERVICE );
		results = new ArrayList<>();
	}

	public static Dispatcher getInstance() {
		return dispatcher;
	}

	public void clear() {
		this.results.clear();
	}
	
	public FieldData getFieldData() {
		return fieldData;
	}

	public void setFieldData(FieldData fieldData) {
		this.fieldData = fieldData;
	}
	
	public Collection<ChuruataData> getResults() {
		return results;
	}

	public void subscribe(long id, int i) {
		// TODO Auto-generated method stub
		
	}

}
