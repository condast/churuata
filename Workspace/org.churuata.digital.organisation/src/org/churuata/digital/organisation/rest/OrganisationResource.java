package org.churuata.digital.organisation.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.churuata.digital.core.data.ChuruataOrganisationData;
import org.churuata.digital.core.data.ServiceData;
import org.churuata.digital.core.data.simple.SimpleOrganisationData;
import org.churuata.digital.core.location.IChuruataService;
import org.churuata.digital.core.model.IOrganisation;
import org.churuata.digital.organisation.core.AuthenticationDispatcher;
import org.churuata.digital.organisation.core.Dispatcher;
import org.churuata.digital.organisation.core.LocationComparator;
import org.churuata.digital.organisation.model.Address;
import org.churuata.digital.organisation.model.Organisation;
import org.churuata.digital.organisation.model.Person;
import org.churuata.digital.organisation.model.Service;
import org.churuata.digital.organisation.services.AddressService;
import org.churuata.digital.organisation.services.ContactService;
import org.churuata.digital.organisation.services.OrganisationService;
import org.churuata.digital.organisation.services.PersonService;
import org.churuata.digital.organisation.services.ServicesService;
import org.condast.commons.Utils;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.na.data.AddressData;
import org.condast.commons.na.data.PersonData;
import org.condast.commons.na.model.IContact;
import org.condast.commons.na.model.IContactPerson;
import org.condast.commons.persistence.service.TransactionManager;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import com.google.gson.Gson;


// Plain old Java Object it does not extend as class or implements
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation.
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML.

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /organisation
@Path("/")
public class OrganisationResource{

	public static final String S_ERR_UNKNOWN_REQUEST = "An invalid request was rertrieved: ";
	public static final String S_ERR_INVALID_USER    = "The provided credentials are invalid:";

	private enum ErrorMessages{
		NO_NAME_OR_TYPE;
	}

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public OrganisationResource() {
		super();
	}

	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/create")
	public Response create( @QueryParam("user-id") long userId, @QueryParam("security") long security, String data) {

		AuthenticationDispatcher dispatcher=  AuthenticationDispatcher.getInstance();
		if( !dispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED).build();

		if( StringUtils.isEmpty(data)) 
			return Response.noContent().build();
	
		ILoginUser user = dispatcher.getLoginUser(userId, security);
		Gson gson = new Gson();
		ChuruataOrganisationData od = gson.fromJson(data, ChuruataOrganisationData.class);
		TransactionManager t = new TransactionManager( Dispatcher.getInstance() );
		try {
			t.open();
			OrganisationService os = new OrganisationService(); 
			PersonService ps = new PersonService(); 
			ContactService cs = new ContactService();

			Collection<Person> persons = ps.findForLogin(userId); 
			IContactPerson person = null;
			if( Utils.assertNull(persons)) {
				IContact contact = cs.createContact(IContact.ContactTypes.EMAIL, user.getEmail());
				person = ps.create(user, contact);
			}else
				person = persons.iterator().next();
			Organisation organisation = os.create(person, od);
			od = new ChuruataOrganisationData(organisation);
			String str = gson.toJson(od, PersonData.class);
			return Response.ok( str ).build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			t.close();
		}
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/register")
	public Response register( @QueryParam("person-id") long personId, String data) {

		if( StringUtils.isEmpty(data)) 
			return Response.noContent().build();
	
		Gson gson = new Gson();
		ChuruataOrganisationData od = gson.fromJson(data, ChuruataOrganisationData.class);
		TransactionManager t = new TransactionManager( Dispatcher.getInstance() );
		try {
			t.open();
			OrganisationService os = new OrganisationService(); 
			PersonService ps = new PersonService(); 
			Person person = ps.find( personId); 
			if( person == null )
				return Response.status( Status.NOT_FOUND).build();
			od.setPrincipal(true);
			Organisation org = os.create(person, od);
			od = new ChuruataOrganisationData(org);
			String str = gson.toJson(od, ChuruataOrganisationData.class);
			return Response.ok( str ).build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			t.close();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/set-location")
	public Response setLocation( @QueryParam("person-id") long personId, @QueryParam("organisation-id") long organisationId, 
			@QueryParam("latitude") double latitude, @QueryParam("longitude") double longitude) {

		if(( latitude < 0 ) || ( longitude < 0)) 
			return Response.status( Status.NOT_ACCEPTABLE).build();
	
		Gson gson = new Gson();
		TransactionManager t = new TransactionManager( Dispatcher.getInstance() );
		try {
			t.open();
			OrganisationService os = new OrganisationService(); 
			PersonService ps = new PersonService(); 
			Person person = ps.find( personId); 
			if( person == null )
				return Response.status( Status.NOT_FOUND).build();
			
			Organisation org = os.find(organisationId);
			if( org == null )
				return Response.status( Status.NOT_FOUND).build();
			org.setLocation(latitude, longitude);		
			os.update(org);
			ChuruataOrganisationData od = new ChuruataOrganisationData(org);
			String str = gson.toJson(od, ChuruataOrganisationData.class);
			return Response.ok( str ).build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			t.close();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/find")
	public Response find( @QueryParam("user-id") long userId, @QueryParam("security") long security, @QueryParam("organisation-id") long organisationId) {
		logger.info( "ATTEMPT Get " );

		AuthenticationDispatcher dispatcher=  AuthenticationDispatcher.getInstance();
		if( !dispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED).build();

		TransactionManager t = new TransactionManager( Dispatcher.getInstance() );
		try {
			t.open();
			OrganisationService os = new OrganisationService(); 
			Organisation organisation = os.find( organisationId );
			Gson gson = new Gson();
			String str = gson.toJson( new ChuruataOrganisationData( organisation ), ChuruataOrganisationData.class);
			return Response.ok( str ).build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			t.close();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/get-all")
	public Response getAll( @QueryParam("user-id") long userId, @QueryParam("security") long security, @QueryParam("verified") String verification) {
		logger.info( "ATTEMPT Get " );

		AuthenticationDispatcher dispatcher=  AuthenticationDispatcher.getInstance();
		if( !dispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED).build();

		IOrganisation.Verification verify = StringUtils.isEmpty(verification)? IOrganisation.Verification.ALL:
			IOrganisation.Verification.valueOf( String.valueOf( StringStyler.styleToEnum(verification)));
		
		TransactionManager t = new TransactionManager( Dispatcher.getInstance() );
		try {
			t.open();
			OrganisationService os = new OrganisationService(); 
			ChuruataOrganisationData[] organisations = OrganisationService.toOrganisationData( os.findAll( verify ));
			Gson gson = new Gson();
			String str = gson.toJson( organisations, ChuruataOrganisationData[].class);
			return Response.ok( str ).build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			t.close();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/find-all")
	public Response findAll() {
		TransactionManager t = new TransactionManager( Dispatcher.getInstance() );
		try {
			t.open();
			OrganisationService os = new OrganisationService(); 
			ChuruataOrganisationData[] organisations = OrganisationService.toOrganisationData( os.findAll( IOrganisation.Verification.VERIFIED ));
			Gson gson = new Gson();
			String str = gson.toJson( organisations, ChuruataOrganisationData[].class);
			return Response.ok( str ).build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			t.close();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/find-in-range")
	public Response findAll( @QueryParam("latitude") double latitude, @QueryParam("longitude") double longitude, @QueryParam("range") int range,  @QueryParam("verified") String verifiedstr ) {
		logger.info( "ATTEMPT Get " );

		TransactionManager t = new TransactionManager( Dispatcher.getInstance() );
		try {
			t.open();
			OrganisationService os = new OrganisationService(); 
			IOrganisation.Verification verified = IOrganisation.Verification.getVerification(verifiedstr);

			List<IOrganisation> orgs = os.findAll( verified );
			Collections.sort(orgs, new LocationComparator<IOrganisation>( latitude, longitude));
			if( Utils.assertNull(orgs))
				return Response.noContent().build();
			
			Collection<SimpleOrganisationData> results = new ArrayList<>();
			orgs.forEach( o->{ results.add( new SimpleOrganisationData( o )); });
			Gson gson = new Gson();
			String str = gson.toJson( results.toArray( new SimpleOrganisationData[ results.size()]), SimpleOrganisationData[].class);
			return Response.ok( str ).build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			t.close();
		}
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/update")
	public Response updateOrganisation( @QueryParam("user-id") long userId, @QueryParam("security") long security,
			@QueryParam("organisation-id") long organisationId, String data) {

		AuthenticationDispatcher dispatcher=  AuthenticationDispatcher.getInstance();
		if( !dispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED).build();

		if( StringUtils.isEmpty(data)) 
			return Response.status( Status.NO_CONTENT ).build();
		
		Gson gson = new Gson();
		ChuruataOrganisationData od = gson.fromJson(data, ChuruataOrganisationData.class);
		TransactionManager t = new TransactionManager( Dispatcher.getInstance() );
		try {
			t.open();
			OrganisationService os = new OrganisationService(); 
			Organisation org = os.update(od);
			if( org == null )
				return Response.noContent().build();
			String str = gson.toJson( new ChuruataOrganisationData( org ), ChuruataOrganisationData.class);
			return Response.ok( str ).build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			t.close();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/add-service")
	public Response addService( @QueryParam("person-id") long personId,
			@QueryParam("organisation-id") long organisationId, @QueryParam("type") String type, @QueryParam("name") String name,
			@QueryParam("description") String description, @QueryParam("from") long from, @QueryParam("to") long to) {

		if( StringUtils.isEmpty(name) || StringUtils.isEmpty( type )) 
			return Response.notModified( ErrorMessages.NO_NAME_OR_TYPE.name()).build();
		IChuruataService.Services st = IChuruataService.Services.valueOf(type);
		
		TransactionManager t = new TransactionManager( Dispatcher.getInstance() );
		try {
			t.open();
			PersonService ps = new PersonService();
			IContactPerson person = ps.find(personId);
			if( person == null )
				return Response.status( Status.NOT_FOUND).build();
			OrganisationService os = new OrganisationService(); 
			ServicesService cs = new ServicesService();
			Organisation organisation = os.find( organisationId );
			if( organisation == null )
				return Response.noContent().build();
			Service service = cs.createService( st, name);
			service.setDescription(description);
			service.setFromDate(from);
			service.setToDate( to);
			organisation.addService(service);
			os.update(organisation);
			Gson gson = new Gson();
			String str = gson.toJson( new ChuruataOrganisationData( organisation ), ChuruataOrganisationData.class);
			return Response.ok( str ).build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			t.close();
		}
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/update-service")
	public Response updateService( @QueryParam("user-id") long userId, @QueryParam("security") long security, 
			@QueryParam("organisation-id") long organisationId, String data) {

		AuthenticationDispatcher dispatcher=  AuthenticationDispatcher.getInstance();
		if( !dispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED).build();
		
		TransactionManager t = new TransactionManager( Dispatcher.getInstance() );
		try {
			t.open();
			ServicesService cs = new ServicesService();
			Gson gson = new Gson();
			ServiceData serviceData = gson.fromJson(data, ServiceData.class);
			Service service = cs.update( serviceData );

			OrganisationService os = new OrganisationService(); 
			Organisation organisation = os.find( organisationId );
			if( organisation == null )
				return Response.noContent().build();
			service.setOrganisation(organisation);
			os.update(organisation);
			String str = gson.toJson( new ChuruataOrganisationData( organisation ), ChuruataOrganisationData.class);
			return Response.ok( str ).build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			t.close();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/remove-service")
	public Response removeService( @QueryParam("user-id") long userId, @QueryParam("security") long security,
			@QueryParam("organisation-id") long organisationId, @QueryParam("service-id") long serviceId) {

		AuthenticationDispatcher dispatcher=  AuthenticationDispatcher.getInstance();
		if( !dispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED).build();

		if( serviceId < 0 ) 
			return Response.status( Status.BAD_REQUEST ).build();
		
		TransactionManager t = new TransactionManager( Dispatcher.getInstance() );
		try {
			t.open();
			OrganisationService os = new OrganisationService(); 
			Organisation organisation = os.find( organisationId );
			if( organisation == null )
				return Response.noContent().build();
			organisation.removeService( serviceId);
			Gson gson = new Gson();
			String str = gson.toJson( new ChuruataOrganisationData( organisation ), ChuruataOrganisationData.class);
			return Response.ok( str ).build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			t.close();
		}
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/remove-services")
	public Response removeServices( @QueryParam("user-id") long userId, @QueryParam("security") long security,
			@QueryParam("organisation-id") long organisationId, String data) {

		AuthenticationDispatcher dispatcher=  AuthenticationDispatcher.getInstance();
		if( !dispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED).build();

		if( StringUtils.isEmpty(data)) 
			return Response.status( Status.NO_CONTENT ).build();
		
		Gson gson = new Gson();
		long[] ids = gson.fromJson(data, long[].class);
		TransactionManager t = new TransactionManager( Dispatcher.getInstance() );
		try {
			t.open();
			OrganisationService os = new OrganisationService(); 
			Organisation organisation = os.find( organisationId );
			if( organisation == null )
				return Response.noContent().build();
			os.removeServices( organisation, ids );
			os.update(organisation);
			String str = gson.toJson( new ChuruataOrganisationData( organisation ), ChuruataOrganisationData.class);
			return Response.ok( str ).build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			t.close();
		}
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/set-verified")
	public Response setVerifies(@QueryParam("user-id") long userId, @QueryParam("security") long security,
			@QueryParam("organisation-id") long organisationId, @QueryParam("verified") boolean verified ) {

		AuthenticationDispatcher dispatcher=  AuthenticationDispatcher.getInstance();
		if( !dispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED).build();
				
		TransactionManager t = new TransactionManager( Dispatcher.getInstance() );
		try {
			t.open();
			OrganisationService os = new OrganisationService(); 
			Organisation organisation = os.find(organisationId);
			if( organisation == null)
				return Response.status(Status.NOT_FOUND).build();
			if( !organisation.isVerified() && verified ) {
				organisation.setVerified(verified);
				os.update(organisation);
			}else if( organisation.isVerified() && !verified ) {
				organisation.setVerified(verified);
				os.update(organisation);				
			}
			Gson gson = new Gson();
			String str = gson.toJson( new ChuruataOrganisationData( organisation ), ChuruataOrganisationData.class);
			return Response.ok( str ).build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			t.close();
		}
	}

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/verify")
	public Response verify(@QueryParam("user-id") long userId, @QueryParam("security") long security,
			@QueryParam("verified") boolean verified, String data ) {

		AuthenticationDispatcher dispatcher=  AuthenticationDispatcher.getInstance();
		if( !dispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED).build();
		
		if( StringUtils.isEmpty(data))
			return Response.noContent().build();
		
		TransactionManager t = new TransactionManager( Dispatcher.getInstance() );
		Gson gson = new Gson();
		ChuruataOrganisationData[] organisations = gson.fromJson(data, ChuruataOrganisationData[].class);
		try {
			t.open();
			OrganisationService os = new OrganisationService(); 
			for( ChuruataOrganisationData od: organisations  ){
				Organisation organisation = os.find(od.getId());
				if( organisation == null)
					return Response.status(Status.NOT_FOUND).build();
				if( !organisation.isVerified() && verified ) {
					organisation.setVerified(verified);
					os.update(organisation);
				}else if( organisation.isVerified() && !verified ) {
					organisation.setVerified(verified);
					os.update(organisation);				
				}
			}
			return Response.ok().build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			t.close();
		}
	}

	@PUT
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/set-address")
	public Response setAddress(@QueryParam("user-id") long userId, @QueryParam("security") long security, 
			@QueryParam("organisation-id") long organisationId, String data ) {

		AuthenticationDispatcher dispatcher=  AuthenticationDispatcher.getInstance();
		if( !dispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED).build();
		
		if( StringUtils.isEmpty(data))
			return Response.noContent().build();
		
		TransactionManager t = new TransactionManager( Dispatcher.getInstance() );
		Gson gson = new Gson();
		try {
			t.open();
			OrganisationService os = new OrganisationService(); 
			Organisation organisation = null;
			if( organisationId <=0 ) {
				PersonService ps = new PersonService();
				Collection<Person> cps = ps.findForLogin(userId);
				if( Utils.assertNull(cps))
					return Response.status( Status.NOT_FOUND).build();
				IContactPerson cp = cps.iterator().next();
				organisation = os.findPrincipal(cp);
			}else
				organisation = os.find(organisationId);
			if( organisation == null )
				return Response.status( Status.NOT_FOUND).build();
			
			AddressData ad = gson.fromJson(data, AddressData.class);
			AddressService as = new AddressService();
			Address address = as.find(ad.getAddressId());
			if( address == null )
				address = as.create(ad);
			else
				AddressService.update(address, ad);
			organisation.setAddress( address );
			os.update(organisation);
			return Response.ok().build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			t.close();
		}
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/remove-service-by-name")
	public Response removeService( @QueryParam("user-id") long userId, @QueryParam("security") long security,
			@QueryParam("organisation-id") long organisationId, @QueryParam("type") String type, @QueryParam("name") String name) {

		AuthenticationDispatcher dispatcher=  AuthenticationDispatcher.getInstance();
		if( !dispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED).build();

		if( StringUtils.isEmpty(name) || StringUtils.isEmpty( type )) 
			return Response.notModified( ErrorMessages.NO_NAME_OR_TYPE.name()).build();
		
		TransactionManager t = new TransactionManager( Dispatcher.getInstance() );
		try {
			t.open();
			OrganisationService os = new OrganisationService(); 
			Organisation organisation = os.find( organisationId );
			if( organisation == null )
				return Response.noContent().build();
			organisation.removeService( type, name);
			return Response.ok().build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			t.close();
		}
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/remove-organisation")
	public Response removeOrganisation( @QueryParam("user-id") long userId, @QueryParam("security") long security,
			@QueryParam("organisation-id") long organisationId) {

		AuthenticationDispatcher dispatcher=  AuthenticationDispatcher.getInstance();
		if( !dispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED).build();
		
		TransactionManager t = new TransactionManager( Dispatcher.getInstance() );
		try {
			t.open();
			OrganisationService os = new OrganisationService(); 
			boolean result = os.remove( organisationId );
			return Response.ok( result ).build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			t.close();
		}
	}

	@DELETE
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/remove-organisations")
	public Response removeAll( @QueryParam("user-id") long userId, @QueryParam("security") long security, String data ) {

		AuthenticationDispatcher dispatcher=  AuthenticationDispatcher.getInstance();
		if( !dispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED).build();
		
		if( StringUtils.isEmpty(data))
			return Response.noContent().build();
		
		Gson gson = new Gson();
		long[] ids = gson.fromJson(data, long[].class);
		TransactionManager t = new TransactionManager( Dispatcher.getInstance() );
		try {
			t.open();
			OrganisationService os = new OrganisationService(); 
			os.removeAll( ids );
			return Response.ok().build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			t.close();
		}
	}
}