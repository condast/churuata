package org.churuata.digital.organisation.rest;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.churuata.digital.core.data.OrganisationData;
import org.churuata.digital.core.data.PersonData;
import org.churuata.digital.core.model.IChuruataService;
import org.churuata.digital.organisation.core.Dispatcher;
import org.churuata.digital.organisation.model.Organisation;
import org.churuata.digital.organisation.services.OrganisationService;
import org.churuata.digital.organisation.services.PersonService;
import org.churuata.digital.organisation.services.ServicesService;
import org.condast.commons.na.model.IContactPerson;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.verification.IVerification;
import org.condast.commons.verification.IVerification.VerificationTypes;
import com.google.gson.Gson;


// Plain old Java Object it does not extend as class or implements
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation.
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML.

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /smash
@Path("/organisation")
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

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/create")
	public Response create( @QueryParam("user-id") long userid, @QueryParam("security") long security, 
			@QueryParam("person-id") long personId, @QueryParam("name") String name,
			@QueryParam("description") String description, @QueryParam("email") String email) {
		logger.info( "ATTEMPT Register " + name );

		Dispatcher dispatcher=  Dispatcher.getInstance();
		if( !dispatcher.isLoggedIn(userid, security))
			return Response.status( Status.UNAUTHORIZED).build();

		if( StringUtils.isEmpty(name) || StringUtils.isEmpty( email )) 
			return Response.notModified( ErrorMessages.NO_USERNAME_OR_EMAIL.name()).build();
		
		if( !IVerification.VerificationTypes.verify(VerificationTypes.EMAIL, email))
			return Response.notModified( ErrorMessages.NO_USERNAME_OR_EMAIL.name()).build();
		
		if( StringUtils.isEmpty( name )) {
			name = email.split("[@]")[0];
		}
		if( StringUtils.isEmpty( description ))
			return Response.status( Status.BAD_REQUEST).build();

		logger.info( "Adding organisation" + name + "(" + email + ")");

		OrganisationService os = new OrganisationService(); 
		PersonService ps = new PersonService(); 
		Organisation organisation = null;
		try {
			IContactPerson person = ps.find(personId);
			os.open();
			organisation = os.create(person, name, description);
			
			Gson gson = new Gson();
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

		Dispatcher dispatcher=  Dispatcher.getInstance();
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

		Dispatcher dispatcher=  Dispatcher.getInstance();
		if( !dispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED).build();

		if( StringUtils.isEmpty(name) || StringUtils.isEmpty( type )) 
			return Response.notModified( ErrorMessages.NO_USERNAME_OR_EMAIL.name()).build();
		IChuruataService.ServiceTypes st = IChuruataService.ServiceTypes.valueOf(type);
		
		
		ServicesService cs = new ServicesService();
		OrganisationService os = new OrganisationService(); 
		try {
			cs.open();
			os.open();
			Organisation organisation = os.find( organisationId );
			if( organisation == null )
				return Response.noContent().build();
			IChuruataService service = cs.createService( organisation.getServicesSize(), st, name);
			organisation.addService(service);
			return Response.ok(service.getServiceId()).build();
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

		Dispatcher dispatcher=  Dispatcher.getInstance();
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
	@Path("/remove-service")
	public Response removeService( @QueryParam("user-id") long userId, @QueryParam("security") long security,
			@QueryParam("organisation-id") long organisationId, @QueryParam("type") String type, @QueryParam("name") String name) {

		Dispatcher dispatcher=  Dispatcher.getInstance();
		if( !dispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED).build();

		if( StringUtils.isEmpty(name) || StringUtils.isEmpty( type )) 
			return Response.notModified( ErrorMessages.NO_USERNAME_OR_EMAIL.name()).build();
		IChuruataService.ServiceTypes st = IChuruataService.ServiceTypes.valueOf(type);
		
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