package org.churuata.digital.organisation.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.churuata.digital.core.data.PersonData;
import org.churuata.digital.organisation.core.Dispatcher;
import org.churuata.digital.organisation.model.Person;
import org.churuata.digital.organisation.services.ContactService;
import org.churuata.digital.organisation.services.PersonService;
import org.condast.commons.na.model.IContact;
import org.condast.commons.na.model.IContact.ContactTypes;
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

		Dispatcher dispatcher=  Dispatcher.getInstance();
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

		PersonService ps = new PersonService(); 
		ContactService cs = new ContactService(); 
		IContactPerson person = null;
		try {
			cs.open();
			IContact contact = cs.createContact(ContactTypes.EMAIL, email);
			ps.open();
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
			ps.close();
			cs.close();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/get")
	public Response createContact( @QueryParam("user-id") long userId, @QueryParam("security") long security) {
		logger.info( "ATTEMPT Get " );

		Dispatcher dispatcher=  Dispatcher.getInstance();
		if( !dispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED).build();

		PersonService ps = new PersonService(); 
		try {
			Collection<Person> persons = ps.findForLogin( userId );
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
			ps.close();
		}
	}
}