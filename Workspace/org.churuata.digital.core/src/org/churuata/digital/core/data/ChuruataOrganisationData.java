package org.churuata.digital.core.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import org.churuata.digital.core.location.IChuruataService;
import org.churuata.digital.core.model.IOrganisation;
import org.condast.commons.Utils;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.na.data.ContactPersonData;
import org.condast.commons.na.data.OrganisationData;
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
		SET_SERVICE_ADDRESS,
		SET_SERVICE_LOCATION,
		SET_VERIFIED, 
		UPDATE_SERVICE, 
		REMOVE_SERVICES, 
		UPDATE;
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
		SERVICE_ID,
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
	
	private boolean principal;
	
	private Collection<ServiceData> services;
	
	public ChuruataOrganisationData() {
		super();
		Calendar calendar = Calendar.getInstance();
		calendar.add( Calendar.YEAR, 1);
		services = new ArrayList<>();
		this.verified = false;
		this.score = 0;
		this.principal = false;
	}

	public ChuruataOrganisationData( LatLng location ){
		super( location );
		this.verified = false;
		this.score = 0;
		this.principal = false;
		services = new ArrayList<>();
	}

	public ChuruataOrganisationData( LatLng location, String name, String description ){
		this( location );
		this.verified = false;
		this.score = 0;
		this.principal = false;
	}

	public ChuruataOrganisationData( IOrganisation organisation ){
		super.setOrganisationId( organisation.getId());
		super.setLocation( organisation.getLocation());
		super.setContact( new ContactPersonData( organisation.getContact()));
		super.setName( organisation.getName());
		super.setDescription( organisation.getDescription());
		super.setWebsite( organisation.getWebsite());
		this.type = organisation.getType();
		this.verified = organisation.isVerified();
		this.score = organisation.getScore();
		this.principal = organisation.isPrincipal();
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
	public boolean isPrincipal() {
		return principal;
	}

	public void setPrincipal(boolean primary) {
		this.principal = primary;
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
		this.services.clear();
		if( Utils.assertNull(services))
			return;
		for( IChuruataService cs: services )
			this.services.add( (ServiceData) cs );
	}

	@Override
	public boolean removeService(IChuruataService service) {
		return this.services.remove(service);
	}

	@Override
	public void removeService(String type, String value) {
		Collection<IChuruataService> temp = new ArrayList<>( this.services );
		temp.forEach( s-> {
			if( s.getService().name().equals(type) && s.getDescription().equals(value))
				this.services.remove(s);
		});
	}

	@Override
	public boolean removeService(long serviceId) {
		boolean result = false;
		Collection<IChuruataService> temp = new ArrayList<>( this.services );
		for( IChuruataService s: temp ){
			if( s.getId() == serviceId ) {
				this.services.remove(s);
				result = true;
			}
		}
		return result;
	}

	@Override
	public int getServicesSize() {
		return this.services.size();
	}
}