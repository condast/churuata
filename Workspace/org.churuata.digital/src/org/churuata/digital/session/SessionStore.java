package org.churuata.digital.session;

import org.churuata.digital.core.data.ChuruataOrganisationData;
import org.churuata.digital.core.data.ProfileData;
import org.churuata.digital.core.location.IChuruataService;
import org.condast.commons.authentication.session.DefaultSessionStore;
import org.condast.commons.data.latlng.LatLng;

public class SessionStore extends DefaultSessionStore<ProfileData>{

	private static final String S_CHURUATA_ID = "org.churuata.digital";

	private long token;
	
	private LatLng selected;
	
	private ChuruataOrganisationData organisation; 
	
	private IChuruataService selectedService;

	public IChuruataService getSelectedService() {
		return selectedService;
	}

	public void setSelectedService(IChuruataService selectedService) {
		this.selectedService = selectedService;
	}

	public SessionStore() {
		super( S_CHURUATA_ID);
	}

	public SessionStore( long token ) {
		this();
		this.token = token;
	}

	@Override
	public void clear() {
		super.clear();
	}

	public long getToken() {
		return token;
	}

	public void setToken(long token) {
		this.token = token;
	}

	public LatLng getSelected() {
		return selected;
	}

	public void setSelected(LatLng selected) {
		this.selected = selected;
	}

	public ChuruataOrganisationData getOrganisation() {
		return organisation;
	}

	public void setOrganisation(ChuruataOrganisationData organisation) {
		this.organisation = organisation;
	}
}