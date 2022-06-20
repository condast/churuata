package org.churuata.digital.session;

import org.churuata.digital.core.data.OrganisationData;
import org.churuata.digital.core.data.ProfileData;
import org.condast.commons.authentication.session.DefaultSessionStore;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.na.data.PersonData;

public class SessionStore extends DefaultSessionStore{

	private static final String S_CHURUATA_ID = "org.churuata.digital";

	private long token;
	
	private LatLng selected;
	
	private OrganisationData organisation;
	
	private ProfileData profile; 
	
	private PersonData personData;

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

	public OrganisationData getOrganisation() {
		return organisation;
	}

	public void setOrganisation(OrganisationData organisation) {
		this.organisation = organisation;
	}

	public ProfileData getProfile() {
		return profile;
	}

	public void setProfile(ProfileData profile) {
		this.profile = profile;
	}

	public PersonData getPersonData() {
		return personData;
	}

	public void setPersonData(PersonData personData) {
		this.personData = personData;
	}	
}