package org.churuata.digital.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.churuata.digital.BasicApplication;
import org.churuata.digital.core.location.Churuata;
import org.churuata.digital.core.location.IChuruata;
import org.churuata.digital.core.location.IChuruataCollection;
import org.churuata.digital.session.SessionStore;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.ui.utils.RWTUtils;
import org.eclipse.rap.rwt.RWT;

public class Dispatcher implements IChuruataCollection {

	private static Dispatcher dispatcher = new Dispatcher();
	
	private Collection<IChuruata> churuatas;
	
	private Map<Long, SessionStore> sotires;
	
	public Dispatcher() {
		super();
		sotires = new HashMap<>();
		churuatas = new ArrayList<>();
	}

	public static Dispatcher getInstance() {
		return dispatcher;
	}
	
	public SessionStore getComposite( long token ) {
		return sotires.get(token);
	}

	@Override
	public boolean contains( IChuruata churuata ) {
		return this.churuatas.contains(churuata);
	}
	
	@Override
	public boolean addChuruata(  IChuruata churuata ) {
		return this.churuatas.add(churuata);
	}

	@Override
	public boolean removeChuruata(  IChuruata churuata ) {
		return this.churuatas.remove(churuata);
	}
	
	@Override
	public Churuata[] getChuruatas() {
		return this.churuatas.toArray( new Churuata[ this.churuatas.size()]);
	}

	@Override
	public IChuruata[] getChuruatas(LatLng latlng, int distance) {
		Collection<IChuruata> results = new ArrayList<>();
		for( IChuruata churuata: this.churuatas) {
			if( LatLngUtils.distance( churuata.getLocation(), latlng) < distance )
				results.add(churuata);
		}
		return results.toArray( new Churuata[ results.size() ]);
	}
	
	public static boolean redirect( BasicApplication.Pages page, long token ) {
		return redirect( page.toPath(), token );
	}

	public static boolean redirect( String path, long token ) {
		if( token < 0 )
			return false;
		HttpSession session = RWT.getUISession().getHttpSession();
		session.setAttribute( IDomainProvider.Attributes.TOKEN.name(), token);
		return RWTUtils.redirect( path);
	}


}
