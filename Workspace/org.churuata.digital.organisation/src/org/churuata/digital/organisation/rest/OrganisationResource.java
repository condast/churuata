package org.churuata.digital.organisation.rest;

import java.util.Collection;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.churuata.digital.core.data.OrganisationData;
import org.churuata.digital.core.data.PersonData;
import org.churuata.digital.core.location.IChuruataType;
import org.churuata.digital.organisation.core.AuthenticationDispatcher;
import org.churuata.digital.organisation.model.Organisation;
import org.churuata.digital.organisation.model.Person;
import org.churuata.digital.organisation.services.ContactService;
import org.churuata.digital.organisation.services.OrganisationService;
import org.churuata.digital.organisation.services.PersonService;
import org.churuata.digital.organisation.services.ServicesService;
import org.condast.commons.Utils;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.na.model.IContact;
import org.condast.commons.na.model.IContactPerson;
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
		NO_USERNAME_OR_EMAIL,
		NO_USERNAME_OR_PASSWORD,
		INVALID_USERNAME,
		INVALID_PASSWORD,
		USERNAME_ALREADY_EXISTS, 
		INVALID_EMAIL, 
		INVALID_REGISTRATION, PASSWORD_DOES_NOT_MATCH_CONFIRMATION;
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
		OrganisationService os = new OrganisationService(); 
		PersonService ps = new PersonService(); 
		ContactService cs = new ContactService();
		Organisation organisation = null;
		try {
			Collection<Person> persons = ps.findForLogin(userId); 
			IContactPerson person = null;
			if( Utils.assertNull(persons)) {
				IContact contact = cs.createContact(IContact.ContactTypes.EMAIL, user.getEmail());
				person = ps.create(user, contact);
			}else
				person = persons.iterator().next();
			os.open();
			organisation = os.create(person, od);
			
			OrganisationData pd = new OrganisationData(organisation);
			String str = gson.toJson(pd, PersonData.class);
			return Response.ok( str ).build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			ps.close();
			os.close();
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/find")
	public Response createContact( @QueryParam("user-id") long userId, @QueryParam("security") long security, @QueryParam("organisation-id") long organisationId) {
		logger.info( "ATTEMPT Get " );

		AuthenticationDispatcher dispatcher=  AuthenticationDispatcher.getInstance();
		if( !dispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED).build();

		OrganisationService os = new OrganisationService(); 
		try {
			os.open();
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
			os.close();
		}
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/add-service")
	public Response addService( @QueryParam("user-id") long userId, @QueryParam("security") long security,
			@QueryParam("organisation-id") long organisationId, @QueryParam("type") String type, @QueryParam("name") String name) {

		AuthenticationDispatcher dispatcher=  AuthenticationDispatcher.getInstance();
		if( !dispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED).build();

		if( StringUtils.isEmpty(name) || StringUtils.isEmpty( type )) 
			return Response.notModified( ErrorMessages.NO_USERNAME_OR_EMAIL.name()).build();
		IChuruataType.Types st = IChuruataType.Types.valueOf(type);
		
		
		ServicesService cs = new ServicesService();
		OrganisationService os = new OrganisationService(); 
		try {
			cs.open();
			os.open();
			Organisation organisation = os.find( organisationId );
			if( organisation == null )
				return Response.noContent().build();
			IChuruataType service = cs.createService( organisation.getServicesSize(), st, name);
			organisation.addService(service);
			return Response.ok(service.getId()).build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			os.close();
		}
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/remove-service")
	public Response removeService( @QueryParam("user-id") long userId, @QueryParam("security") long security,
			@QueryParam("organisation-id") long organisationId, @QueryParam("service-id") long serviceId) {

		AuthenticationDispatcher dispatcher=  AuthenticationDispatcher.getInstance();
		if( !dispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED).build();

		if( serviceId < 0 ) 
			return Response.status( Status.BAD_REQUEST ).build();
		
		OrganisationService os = new OrganisationService(); 
		try {
			os.open();
			Organisation organisation = os.find( organisationId );
			if( organisation == null )
				return Response.noContent().build();
			organisation.removeService( serviceId);
			return Response.ok().build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			os.close();
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
			return Response.notModified( ErrorMessages.NO_USERNAME_OR_EMAIL.name()).build();
		IChuruataType.Types st = IChuruataType.Types.valueOf(type);
		
		OrganisationService os = new OrganisationService(); 
		try {
			os.open();
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
			os.close();
		}
	}

}