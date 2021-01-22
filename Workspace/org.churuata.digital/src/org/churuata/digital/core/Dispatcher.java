package org.churuata.digital.core;

import java.util.ArrayList;
import java.util.Collection;
import org.churuata.digital.core.location.Churuata;

public class Dispatcher {

	private static Dispatcher dispatcher = new Dispatcher();
	
	private Collection<Churuata> churuatas;
	
	public Dispatcher() {
		super();
		churuatas = new ArrayList<>();
	}

	public static Dispatcher getInstance() {
		return dispatcher;
	}
	
	public boolean addChuruata(  Churuata churuata ) {
		return this.churuatas.add(churuata);
	}

	public boolean removeChuruata(  Churuata churuata ) {
		return this.churuatas.remove(churuata);
	}
	
	public Churuata[] getChuruatas() {
		return this.churuatas.toArray( new Churuata[ this.churuatas.size()]);
	}

}
