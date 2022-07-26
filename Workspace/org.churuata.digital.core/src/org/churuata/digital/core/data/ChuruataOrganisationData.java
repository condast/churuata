package org.churuata.digital.core.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import org.churuata.digital.core.location.IChuruataService;
import org.churuata.digital.core.model.IOrganisation;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.na.data.OrganisationData;
import org.condast.commons.na.data.PersonData;
import org.condast.commons.strings.StringStyler;

/**
 * The persistent class for the eet_tb_persoon database table.
 */
public class ChuruataOrganisationData extends OrganisationData implements IOrganisation, Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	public static final String S_ORGANISATION_REST_PATH = "organisation/admin/";
	
	public enum Requests{
		ADD_SERVICE,
		CREATE,
		REGISTER,
		FIND,
		FIND_IN_RANGE,
		GET_ALL, 
		REMOVE_SERVICE,
		REMOVE_ORGANISATION,
		REMOVE_ORGANISATIONS,
		VERIFY,
		SET_ADDRESS,
		SET_LOCATION,
		SET_VERIFIED, 
		UPDATE_SERVICE;
	}

	public enum Parameters{
		USER_ID,
		SECURITY,
		ORGANISATION_ID,
		NAME,
		DESCRIPTION,
		LATITUDE,
		LONGITUDE,
		RANGE,
		TYPE, 
		VERIFIED,
		WEBSITE;

		@Override
		public String toString() {
			return StringStyler.xmlStyleString( name());
		}
	}
	
	private OrganisationTypes type;
	
	private boolean verified;
	
	private int score;
	
	private boolean primary;
	
	private Collection<ServiceData> services;
	
	public ChuruataOrganisationData() {
		super();
		Calendar calendar = Calendar.getInstance();
		calendar.add( Calendar.YEAR, 1);
		services = new ArrayList<>();
		this.verified = false;
		this.score = 0;
		this.primary = false;
	}

	public ChuruataOrganisationData( LatLng location ){
		super( location );
		this.verified = false;
		this.score = 0;
		this.primary = false;
		services = new ArrayList<>();
	}

	public ChuruataOrganisationData( LatLng location, String name, String description ){
		this( location );
		this.verified = false;
		this.score = 0;
		this.primary = false;
	}

	public ChuruataOrganisationData( IOrganisation organisation ){
		super.setOrganisationId( organisation.getId());
		super.setLocation( organisation.getLocation());
		super.setContact( new PersonData( organisation.getContact()));
		super.setName( organisation.getName());
		super.setDescription( organisation.getDescription());
		super.setWebsite( organisation.getWebsite());
		this.type = organisation.getType();
		this.verified = organisation.isVerified();
		this.score = organisation.getScore();
		this.primary = organisation.isPrimary();
		services = new ArrayList<>();
		for( IChuruataService service: organisation.getServices())
			services.add( new ServiceData( service ));
	}

	@Override
	public OrganisationTypes getType() {
		return type;
	}

	public void setType(OrganisationTypes type) {
		this.type = type;
	}

	@Override
	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	@Override
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public void clearServices(){
		this.services.clear();
	}
	
	public int addService( IChuruataService type ) {
		this.services.add(new ServiceData( type ));
		return this.services.size();
	}

	public IChuruataService[] getServices() {
		return services.toArray( new IChuruataService[ this.services.size()]);
	}

	public void setChuruataServices(IChuruataService[] input) {
		this.services.clear();
		for( IChuruataService cs: input )
			this.services.add( new ServiceData( cs ));
	}

	@Override
	public void setServices(IChuruataService[] services) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeService(IChuruataService service) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeService(String type, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeService(long serviceId) {
		// TODO Auto-generated method stub		
	}

	@Override
	public int getServicesSize() {
		return this.services.size();
	}
}