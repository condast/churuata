package org.churuata.digital.authentication.rest;

import java.util.Collection;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.churuata.digital.authentication.core.Dispatcher;
import org.churuata.digital.authentication.model.Login;
import org.churuata.digital.authentication.services.LoginService;
import org.condast.commons.authentication.user.ILoginUser;
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
@Path("/")
public class AuthenticationResource{

	public static final String S_ERR_UNKNOWN_REQUEST = "An invalid request was rertrieved: ";
	public static final String S_ERR_INVALID_USER    = "The provided credentials are invalid:";

	private enum ErrorMessages{
		NO_USERNAME_OR_EMAIL,
		INVALID_USERNAME,
		INVALID_PASSWORD;
	}
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public AuthenticationResource() {
		super();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/register")
	public Response register( @QueryParam("name") String name, @QueryParam("password") String password, @QueryParam("email") String email) {
		logger.info( "ATTEMPT Register " + name );
		Response retval = Response.noContent().build();
		if( StringUtils.isEmpty(name) || StringUtils.isEmpty( email )) {
			retval = Response.notModified( ErrorMessages.NO_USERNAME_OR_EMAIL.name()).build();
			return retval;
		}else if( !IVerification.VerificationTypes.verify(VerificationTypes.EMAIL, email)){
			retval = Response.notModified( ErrorMessages.NO_USERNAME_OR_EMAIL.name()).build();
			return retval;
		}else if( StringUtils.isEmpty( name )) {
			name = email.split("[@]")[0];
		}else if( StringUtils.isEmpty( password )) {
			return Response.status( Status.BAD_REQUEST).build();
		}

		logger.info( "Registering " + name + "(" + email + ")");
		Dispatcher dispatcher=  Dispatcher.getInstance();

		LoginService service = new LoginService( dispatcher ); 
		try{
			service.open();
			ILoginUser user = service.create(name, password, email);
			dispatcher.addUser(user);
			retval = Response.ok( String.valueOf( user.getId())).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			service.close();
		}
		return retval;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/login")
	public Response login( @QueryParam("name") String name, @QueryParam("password") String password ) {
		Response retval = Response.noContent().build();
		try{
			logger.info( "ATTEMPT Login " + name );
			if( StringUtils.isEmpty(name) || StringUtils.isEmpty(password )) {
				retval = Response.notModified( ErrorMessages.NO_USERNAME_OR_EMAIL.name()).build();
				return retval;
			}else if( StringUtils.isEmpty( password )) {
				return Response.status( Status.BAD_REQUEST).build();
			}

			logger.info( "Login " + name );
			Dispatcher dispatcher=  Dispatcher.getInstance();

			LoginService service = new LoginService( dispatcher ); 
			ILoginUser user = service.login(name, password);//new AnonymousUser( name, password);
			if( user == null )
				return Response.status( Status.NOT_FOUND).build();
			dispatcher.addUser(user);
			retval = Response.ok( String.valueOf( user.getId() )).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
		return retval;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/logoff")
	public Response logoff( @QueryParam("userid") long id, @QueryParam("token") long token) {

		Response retval = null;
		Dispatcher dispatcher=  Dispatcher.getInstance();
		try{
			if( !dispatcher.isLoggedIn( id))
				retval = Response.noContent().build();
			else {
				ILoginUser user = dispatcher.getLoginUser(id, token);
				dispatcher.removeUser(user);
				return Response.ok(String.valueOf( user.getId() )).build();
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
		return retval;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/unregister")
	public Response unregister( @QueryParam("id") long id ) {

		Response retval = null;
		Dispatcher dispatcher=  Dispatcher.getInstance();
		if( !dispatcher.isRegistered(id)) {
			retval = Response.noContent().build();
			return retval;
		}

		LoginService service = new LoginService( dispatcher ); 
		try{
			service.open();	
			ILoginUser user = dispatcher.getUser( id );
			logger.info( "Unregister " + user.getUserName() );			

			service.remove(user.getId());
			//manager.unregisterUser(user);
			retval = Response.ok(String.valueOf( user.getId() )).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally{ 
			service.close();
		}
		return retval;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getall")
	public Response getAll( @QueryParam("name") String userName, @QueryParam("token") long token ) {
		Response retval = null;
		try{
			//if(!AuthenticationUtils.isAdmin(userName, token)) {
			//	retval = Response.status( Status.UNAUTHORIZED).build();
			//	return retval;
			//}
			Dispatcher dispatcher = Dispatcher.getInstance();
			LoginService service = new LoginService( dispatcher );
			Collection<Login> Logins = service.findAll();
			Gson gson = new Gson();
			String str = gson.toJson(Logins.toArray( new Login[Logins.size()] ), Login[].class);
			retval = Response.ok( str).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
		return retval;
	}


	/**
	 * Create a response object of the user
	 * @param user
	 * @return
	 */
	public static String toResponseString( ILoginUser user ) {
		long[] str = new long[2];
		str[0] = user.getId();
		str[1] = user.getToken();
		Gson gson = new Gson();
		return gson.toJson(str, long[].class);
	}

}