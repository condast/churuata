package org.churuata.digital.organisation.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.churuata.digital.core.data.OrganisationData;
import org.churuata.digital.core.data.simple.SimpleOrganisationData;
import org.churuata.digital.core.location.IChuruataService;
import org.churuata.digital.core.model.IOrganisation;
import org.churuata.digital.organisation.core.AuthenticationDispatcher;
import org.churuata.digital.organisation.core.Dispatcher;
import org.churuata.digital.organisation.core.LocationComparator;
import org.churuata.digital.organisation.model.Organisation;
import org.churuata.digital.organisation.model.Person;
import org.churuata.digital.organisation.model.Service;
import org.churuata.digital.organisation.services.ContactService;
import org.churuata.digital.organisation.services.OrganisationService;
import org.churuata.digital.organisation.services.PersonService;
import org.churuata.digital.organisation.services.ServicesService;
import org.condast.commons.Utils;
import org.condast.commons.authentication.user.ILoginUser;
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
		OrganisationData od = gson.fromJson(data, OrganisationData.class);
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
			od = new OrganisationData(organisation);
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
		OrganisationData od = gson.fromJson(data, OrganisationData.class);
		TransactionManager t = new TransactionManager( Dispatcher.getInstance() );
		try {
			t.open();
			OrganisationService os = new OrganisationService(); 
			PersonService ps = new PersonService(); 
			Person person = ps.find( personId); 
			if( person == null )
				return Response.status( Status.NOT_FOUND).build();
			
			Organisation org = os.create(person, od);
			od = new OrganisationData(org);
			String str = gson.toJson(od, OrganisationData.class);
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
			OrganisationData od = new OrganisationData(org);
			String str = gson.toJson(od, OrganisationData.class);
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
			String str = gson.toJson( new OrganisationData( organisation ), OrganisationData.class);
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
			OrganisationData[] organisations = OrganisationService.toOrganisationData( os.findAll( verify ));
			Gson gson = new Gson();
			String str = gson.toJson( organisations, OrganisationData[].class);
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
			OrganisationData[] organisations = OrganisationService.toOrganisationData( os.findAll( IOrganisation.Verification.VERIFIED ));
			Gson gson = new Gson();
			String str = gson.toJson( organisations, OrganisationData[].class);
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
	public Response findAll( @QueryParam("latitude") double latitude, @QueryParam("longitude") double longitude, @QueryParam("range") int range ) {
		logger.info( "ATTEMPT Get " );

		TransactionManager t = new TransactionManager( Dispatcher.getInstance() );
		try {
			t.open();
			OrganisationService os = new OrganisationService(); 
			List<Organisation> orgs = os.findAll();// os.getAll(latitude, longitude, range );
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
			String str = gson.toJson( new OrganisationData( organisation ), OrganisationData.class);
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
			String str = gson.toJson( new OrganisationData( organisation ), OrganisationData.class);
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
		OrganisationData[] organisations = gson.fromJson(data, OrganisationData[].class);
		try {
			t.open();
			OrganisationService os = new OrganisationService(); 
			PersonService ps = new PersonService();
			for( OrganisationData od: organisations  ){
				Organisation organisation = os.find(od.getId());
				if( organisation == null)
					return Response.status(Status.NOT_FOUND).build();
				if( !organisation.isVerified() && verified ) {
					organisation.setVerified(verified);
					IContactPerson person = organisation.getContact();

					//dispatcher.re
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
		IChuruataService.Services st = IChuruataService.Services.valueOf(type);
		
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

}