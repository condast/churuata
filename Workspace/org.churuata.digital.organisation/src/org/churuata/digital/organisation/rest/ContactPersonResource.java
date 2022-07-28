package org.churuata.digital.organisation.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.churuata.digital.core.data.ChuruataOrganisationData;
import org.churuata.digital.core.data.ProfileData;
import org.churuata.digital.organisation.core.AuthenticationDispatcher;
import org.churuata.digital.organisation.core.Dispatcher;
import org.churuata.digital.organisation.model.Organisation;
import org.churuata.digital.organisation.model.Person;
import org.churuata.digital.organisation.services.ContactService;
import org.churuata.digital.organisation.services.OrganisationService;
import org.churuata.digital.organisation.services.PersonService;
import org.condast.commons.Utils;
import org.condast.commons.authentication.core.LoginData;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.io.IOUtils;
import org.condast.commons.na.data.ContactPersonData;
import org.condast.commons.na.model.IContact;
import org.condast.commons.na.model.IContact.ContactTypes;
import org.condast.commons.persistence.service.TransactionManager;
import org.condast.commons.na.model.IContactPerson;
import org.condast.commons.strings.StringStyler;
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

	private Dispatcher dispatcher = Dispatcher.getInstance();
	
	public ContactPersonResource() {
		super();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/register")
	public Response registerContact( @QueryParam("name") String name, @QueryParam("surname") String surname,
			@QueryParam("prefix") String prefix, @QueryParam("email") String email) {
		logger.info( "ATTEMPT Request registration " + name );

		if( StringUtils.isEmpty(name) || StringUtils.isEmpty( email )) 
			return Response.notModified( ErrorMessages.NO_USERNAME_OR_EMAIL.name()).build();
		
		if( StringUtils.isEmpty(surname ))
			return Response.notModified( ErrorMessages.NO_TITLE.name()).build();
		
		if( !IVerification.VerificationTypes.verify(VerificationTypes.EMAIL, email))
			return Response.notModified( ErrorMessages.NO_USERNAME_OR_EMAIL.name()).build();
		
		if( StringUtils.isEmpty( name )) {
			name = email.split("[@]")[0];
		}

		logger.info( "Adding contact" + name + "(" + email + ")");

		TransactionManager t = new TransactionManager( dispatcher );
		IContactPerson person = null;
		try {
			t.open();
			PersonService ps = new PersonService(); 
			ContactService cs = new ContactService(); 

			IContact contact = cs.createContact(ContactTypes.EMAIL, email);
			person = ps.create(-1, name, surname, prefix, contact);
			
			Gson gson = new Gson();
			ContactPersonData pd = new ContactPersonData(person);
			String str = gson.toJson(pd, ContactPersonData.class);
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
	@Path("/create")
	public Response createContact( @QueryParam("user-id") long userid, @QueryParam("security") long security, 
			@QueryParam("name") String name, @QueryParam("title") String title,
			@QueryParam("description") String description, @QueryParam("email") String email) {
		logger.info( "ATTEMPT Register " + name );

		AuthenticationDispatcher adispatcher=  AuthenticationDispatcher.getInstance();
		if( !adispatcher.isLoggedIn(userid, security))
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

		TransactionManager t = new TransactionManager( dispatcher );
		IContactPerson person = null;
		try {
			t.open();
			PersonService ps = new PersonService(); 
			ContactService cs = new ContactService(); 

			IContact contact = cs.createContact(ContactTypes.EMAIL, email);
			person = ps.create(userid, name, title, description, contact);
			
			Gson gson = new Gson();
			ContactPersonData pd = new ContactPersonData(person);
			String str = gson.toJson(pd, ContactPersonData.class);
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

		AuthenticationDispatcher adispatcher=  AuthenticationDispatcher.getInstance();
		if( !adispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED).build();

		TransactionManager t = new TransactionManager( dispatcher );
		try {
			t.open();
			PersonService ps = new PersonService(); 
			Collection<Person> persons = ps.findForLogin( userId );
			if( Utils.assertNull(persons))
				return Response.noContent().build();
			Collection<ContactPersonData> data = new ArrayList<>();
			persons.forEach(p->data.add( new ContactPersonData(p)));
			Gson gson = new Gson();
			String str = gson.toJson(data.toArray( new ContactPersonData[ data.size()]), ContactPersonData.class);
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

		AuthenticationDispatcher adispatcher=  AuthenticationDispatcher.getInstance();
		if( !adispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED).build();

		ILoginUser user = adispatcher.getLoginUser(userId, security);
		TransactionManager t = new TransactionManager( dispatcher );
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
			ProfileData profile = new ProfileData( new LoginData( user ), person );
			orgs.forEach(o->profile.addOrganisation( new ChuruataOrganisationData(o)));
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
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/update-person")
	public Response updatePerson( @QueryParam("user-id") long userId, @QueryParam("security") long security, @QueryParam("person-id") long personId, String data) {
		logger.fine( "ATTEMPT Update " );
		
		TransactionManager t = new TransactionManager( dispatcher );
		try {
			t.open();

			AuthenticationDispatcher adispatcher=  AuthenticationDispatcher.getInstance();
			ILoginUser user = adispatcher.getLoginUser(userId, security);

			Gson gson = new Gson();
			ContactPersonData personData = gson.fromJson(data, ContactPersonData.class);
			if( personData.getId() != personId)
				return Response.status( Status.BAD_REQUEST).build();

			PersonService ps = new PersonService(); 
			Person person = ps.find(personId);
			if( person == null )
				return Response.status( Status.NOT_FOUND).build();
			
			
			person = ps.update(personData );
			if( person == null )
				return Response.status( Status.NOT_FOUND).build();
			
			ProfileData profile = new ProfileData( user, new ContactPersonData( person ));
			String str = gson.toJson( profile, ProfileData.class);	
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
	@Path("/add-contact-type")
	public Response addContacttype( @QueryParam("person-id") long personId, @QueryParam("contact-type") String type,
			@QueryParam("value") String value, @QueryParam("restricted") boolean restricted) {
		logger.info( "ATTEMPT Add contact " + type );

		if( StringUtils.isEmpty(value) || StringUtils.isEmpty( type )) 
			return Response.status( Status.BAD_REQUEST ).build();
		
		ContactTypes ct = ContactTypes.valueOf( StringStyler.styleToEnum(type));
		if( !ContactTypes.verify(ct, value))
			return Response.status( Status.BAD_REQUEST ).build();
		
		TransactionManager t = new TransactionManager( dispatcher );
		IContactPerson person = null;
		try {
			t.open();
			PersonService ps = new PersonService(); 
			person = ps.find(personId);
			if( person == null )
				return Response.status(Status.NOT_FOUND).build();
			
			ContactService cs = new ContactService(); 

			IContact contact = cs.createContact(ct, value);
			person.addContact(contact);
			Gson gson = new Gson();
			ContactPersonData pd = new ContactPersonData(person);
			String str = gson.toJson(pd, ContactPersonData.class);
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
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/remove-contacts")
	public Response removeContacts( @QueryParam("person-id") long personId, String data ) {
		
		if( StringUtils.isEmpty(data))
			return Response.noContent().build();
		
		Gson gson = new Gson();
		long[] ids = gson.fromJson(data, long[].class);
		TransactionManager t = new TransactionManager( Dispatcher.getInstance() );
		try {
			t.open();
			PersonService ps = new PersonService(); 
			if( !ps.removeContacts( personId, ids ))
				return Response.status( Status.NOT_FOUND).build();

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