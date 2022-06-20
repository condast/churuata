package org.churuata.digital.organisation.rest;

import java.util.ArrayList;
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
import org.churuata.digital.core.data.ProfileData;
import org.churuata.digital.organisation.core.AuthenticationDispatcher;
import org.churuata.digital.organisation.core.Dispatcher;
import org.churuata.digital.organisation.model.Organisation;
import org.churuata.digital.organisation.model.Person;
import org.churuata.digital.organisation.services.ContactService;
import org.churuata.digital.organisation.services.OrganisationService;
import org.churuata.digital.organisation.services.PersonService;
import org.condast.commons.Utils;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.io.IOUtils;
import org.condast.commons.na.data.PersonData;
import org.condast.commons.na.model.IContact;
import org.condast.commons.na.model.IContact.ContactTypes;
import org.condast.commons.persistence.service.TransactionManager;
import org.condast.commons.na.model.IContactPerson;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.verification.IVerification;
import org.condast.commons.verification.IVerification.VerificationTypes;
import com.google.gson.Gson;

//Sets the path to base URL + /contact
@Path("/contact")
public class ContactPersonResource{

	public static final String S_ERR_UNKNOWN_REQUEST = "An invalid request was rertrieved: ";
	public static final String S_ERR_INVALID_USER    = "The provided credentials are invalid:";

	private enum ErrorMessages{
		NO_USERNAME_OR_EMAIL,
		NO_USERNAME_OR_PASSWORD,
		NO_TITLE
	}

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public ContactPersonResource() {
		super();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/create")
	public Response createContact( @QueryParam("user-id") long userid, @QueryParam("security") long security, 
			@QueryParam("name") String name, @QueryParam("title") String title,
			@QueryParam("description") String description, @QueryParam("email") String email) {
		logger.info( "ATTEMPT Register " + name );

		AuthenticationDispatcher dispatcher=  AuthenticationDispatcher.getInstance();
		if( !dispatcher.isLoggedIn(userid, security))
			return Response.status( Status.UNAUTHORIZED).build();

		if( StringUtils.isEmpty(name) || StringUtils.isEmpty( email )) 
			return Response.notModified( ErrorMessages.NO_USERNAME_OR_EMAIL.name()).build();
		
		if( StringUtils.isEmpty(title ))
			return Response.notModified( ErrorMessages.NO_TITLE.name()).build();
		
		if( !IVerification.VerificationTypes.verify(VerificationTypes.EMAIL, email))
			return Response.notModified( ErrorMessages.NO_USERNAME_OR_EMAIL.name()).build();
		
		if( StringUtils.isEmpty( name )) {
			name = email.split("[@]")[0];
		}
		if( StringUtils.isEmpty( description ))
			return Response.status( Status.BAD_REQUEST).build();

		logger.info( "Adding contact" + name + "(" + email + ")");

		TransactionManager t = new TransactionManager( Dispatcher.getInstance() );
		IContactPerson person = null;
		try {
			t.open();
			PersonService ps = new PersonService(); 
			ContactService cs = new ContactService(); 

			IContact contact = cs.createContact(ContactTypes.EMAIL, email);
			person = ps.create(userid, name, title, description, contact);
			
			Gson gson = new Gson();
			PersonData pd = new PersonData(person);
			String str = gson.toJson(pd, PersonData.class);
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
	@Path("/get")
	public Response getContact( @QueryParam("user-id") long userId, @QueryParam("security") long security) {
		logger.info( "ATTEMPT Get " );

		AuthenticationDispatcher dispatcher=  AuthenticationDispatcher.getInstance();
		if( !dispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED).build();

		TransactionManager t = new TransactionManager( Dispatcher.getInstance() );
		try {
			t.open();
			PersonService ps = new PersonService(); 
			Collection<Person> persons = ps.findForLogin( userId );
			if( Utils.assertNull(persons))
				return Response.noContent().build();
			Collection<PersonData> data = new ArrayList<>();
			persons.forEach(p->data.add( new PersonData(p)));
			Gson gson = new Gson();
			String str = gson.toJson(data.toArray( new PersonData[ data.size()]), PersonData.class);
			return Response.ok( str ).build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			IOUtils.close( t );
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/get-profile")
	public Response getProfile( @QueryParam("user-id") long userId, @QueryParam("security") long security) {
		logger.info( "ATTEMPT Get " );

		AuthenticationDispatcher dispatcher=  AuthenticationDispatcher.getInstance();
		if( !dispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED).build();

		ILoginUser user = dispatcher.getLoginUser(userId, security);
		TransactionManager t = new TransactionManager( Dispatcher.getInstance() );
		IContactPerson person = null;
		try {
			t.open();
			PersonService ps = new PersonService(); 
			OrganisationService os = new OrganisationService(); 
			Collection<Person> persons = ps.findForLogin( userId );
			Collection<Organisation> orgs = new ArrayList<>();
			if( Utils.assertNull(persons)) {
				person = ps.create( user);
			}else {
				person = persons.iterator().next();
				orgs = os.getAll(person);
			}
			ProfileData profile = new ProfileData( person );
			orgs.forEach(o->profile.addOrganisation( new OrganisationData(o)));
			Gson gson = new Gson();
			String str = gson.toJson(profile, ProfileData.class);
			return Response.ok( str ).build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			IOUtils.close( t );
		}
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/update-person")
	public Response updatePerson( @QueryParam("user-id") long userId, @QueryParam("security") long security, String data) {
		logger.info( "ATTEMPT Get " );

		AuthenticationDispatcher dispatcher=  AuthenticationDispatcher.getInstance();
		if( !dispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED).build();

		if( !dispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED ).build();
		
		TransactionManager t = new TransactionManager( Dispatcher.getInstance() );
		try {
			t.open();
			PersonService ps = new PersonService(); 
			
			Gson gson = new Gson();
			PersonData personData = gson.fromJson(data, PersonData.class);
			
			IContactPerson person = ps.update(personData );
			return ( person == null )? Response.status( Status.NOT_FOUND).build():
				Response.ok().build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			IOUtils.close( t );
		}
	}

}