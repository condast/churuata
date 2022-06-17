package org.churuata.digital.authentication.rest;

import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.churuata.digital.authentication.core.Dispatcher;
import org.churuata.digital.authentication.model.Login;
import org.churuata.digital.authentication.services.LoginService;
import org.condast.commons.Utils;
import org.condast.commons.authentication.core.LoginData;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.authentication.utils.AuthenticationUtils;
import org.condast.commons.messaging.http.IHttpRequest.HttpStatus;
import org.condast.commons.persistence.service.TransactionManager;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.verification.IVerification;
import org.condast.commons.verification.IVerification.VerificationTypes;
import org.javax.mail.utils.MailUtils;
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
		NO_USERNAME_OR_PASSWORD,
		INVALID_USERNAME,
		INVALID_PASSWORD,
		USERNAME_ALREADY_EXISTS, 
		INVALID_EMAIL, 
		INVALID_REGISTRATION, PASSWORD_DOES_NOT_MATCH_CONFIRMATION;
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

		TransactionManager t = new TransactionManager( dispatcher );
		LoginService service = new LoginService( dispatcher );
		try{
			ILoginUser user = service.login(name, password);
			if( user != null )
				return Response.notModified( ErrorMessages.USERNAME_ALREADY_EXISTS.name() ).build();
			
			t.open();
			user = (ILoginUser) service.create(name, password, email);
			user.setSecurity(AuthenticationUtils.generateSecurityCode(user));
			dispatcher.addUser(user);
			String str = AuthenticationUtils.createDictionaryString(user);
			retval = Response.ok( str ).build();

			LoginData loginData = new LoginData( name, password, email );
			long confirmation = dispatcher.addConfirmRegistration(loginData);	
			Properties props = MailUtils.createProperties(getClass().getResourceAsStream(MailUtils.S_DEFAULT_MAIL_RESOURCE));
			MailUtils.sendConfirmationdMail(getClass().getResourceAsStream(MailUtils.S_RESOURCE_CONFIRM), props, loginData, email, Dispatcher.S_CHURUATA, confirmation );
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			t.close();
		}
		return retval;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/confirm-registration")
	public Response confirmRegistration( @QueryParam("confirm") long confirmation) {
		logger.info( "ATTEMPT Confirmation " + confirmation );
		Response retval = Response.noContent().build();
		if( confirmation < 0 ) {
			retval = Response.status( Status.BAD_REQUEST).build();
			return retval;
		}
		Dispatcher dispatcher=  Dispatcher.getInstance();
		LoginData loginData = dispatcher.getStoredUser(confirmation);
		if( loginData == null )
			return Response.noContent().build();

		TransactionManager t = new TransactionManager( dispatcher );
		LoginService service = new LoginService( dispatcher ); 
		try{
			t.open();
			ILoginUser user = service.create(loginData.getNickName(), loginData.getPassword(), loginData.getEmail());
			user.setSecurity(AuthenticationUtils.generateSecurityCode(user));
			user.setRegistered(true);
			dispatcher.addUser(user);
			String str = AuthenticationUtils.createDictionaryString(user);
			retval = Response.ok( str ).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			t.close();
		}
		return retval;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/login")
	public Response login( @QueryParam("name") String name, @QueryParam("password") String password ) {
		Dispatcher dispatcher=  Dispatcher.getInstance();
		TransactionManager t = new TransactionManager( dispatcher );
		Response retval = Response.noContent().build();
		try{
			t.open();
			logger.info( "ATTEMPT Login " + name );
			if( StringUtils.isEmpty(name) || StringUtils.isEmpty(password )) {
				retval = Response.notModified( ErrorMessages.NO_USERNAME_OR_PASSWORD.name()).build();
				return retval;
			}else if( StringUtils.isEmpty( password )) {
				return Response.status( Status.BAD_REQUEST).build();
			}

			logger.info( "Login " + name );

			LoginService service = new LoginService( dispatcher );
			Login user = (Login) service.login(name, password);//new AnonymousUser( name, password);

			if( user == null )
				return Response.status( Status.NOT_FOUND).build();
			user.setSecurity(AuthenticationUtils.generateSecurityCode(user));
			dispatcher.addUser(user);

			Properties props = MailUtils.createProperties(getClass().getResourceAsStream(MailUtils.S_DEFAULT_MAIL_RESOURCE));
			InputStream in = this.getClass().getResourceAsStream(MailUtils.S_RESOURCE_CONFIRM_CODE);
			MailUtils.sendConfirmCodeMail( in, props, user, Dispatcher.S_CHURUATA );

			String str = AuthenticationUtils.createDictionaryString(user);
			retval = Response.ok( str ).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally{
			t.close();
		}
		return retval;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/login-by-id")
	public Response loginByUserId( @QueryParam("user-id") long userId, @QueryParam("password") String password ) {
		Dispatcher dispatcher=  Dispatcher.getInstance();
		TransactionManager t = new TransactionManager( dispatcher );
		try{
			logger.info( "ATTEMPT Login: " + userId );
			if(StringUtils.isEmpty(password )) {
				return Response.notModified( ErrorMessages.INVALID_PASSWORD.name()).build();
			}else if( StringUtils.isEmpty( password )) {
				return Response.status( Status.BAD_REQUEST).build();
			}

			logger.info( "Login: " + userId );

			LoginService service = new LoginService( dispatcher );
			Login user = (Login) service.find( userId);

			if( user == null )
				return Response.status( Status.NOT_FOUND).build();
			user.setSecurity(AuthenticationUtils.generateSecurityCode(user));
			dispatcher.addUser(user);

			try{
				Properties props = MailUtils.createProperties(getClass().getResourceAsStream(MailUtils.S_DEFAULT_MAIL_RESOURCE));
				InputStream in = this.getClass().getResourceAsStream(MailUtils.S_RESOURCE_CONFIRM_CODE);
				MailUtils.sendConfirmCodeMail( in, props, user, Dispatcher.S_CHURUATA );
			}
			catch( Exception ex ){
				ex.printStackTrace();
				//return Response.serverError().build();
			}

			String str = AuthenticationUtils.createDictionaryString(user);
			return Response.ok( str ).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally{
			t.close();
		}
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/get-security")
	public Response getSecurityCode( @QueryParam("user-id") long userId, @QueryParam("password") String password ) {
		try{
			logger.info( "ATTEMPT Login: " + userId );
			if(StringUtils.isEmpty(password )) {
				return Response.notModified( ErrorMessages.INVALID_PASSWORD.name()).build();
			}else if( StringUtils.isEmpty( password )) {
				return Response.status( Status.BAD_REQUEST).build();
			}

			logger.info( "Login: " + userId );
			Dispatcher dispatcher=  Dispatcher.getInstance();
			
			if( !dispatcher.isLoggedIn(userId))
				return Response.status( HttpStatus.UNAUTHORISED.getStatus()).build();
			
			ILoginUser user = dispatcher.getUser( userId);
			if( user == null )
				return Response.status( Status.NOT_FOUND).build();
			String str = AuthenticationUtils.createDictionaryString(user);
			return Response.ok( str ).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/confirm")
	public Response confirm( @QueryParam("confirm") long code ) {
		Response retval = Response.noContent().build();
		try{
			Dispatcher dispatcher=  Dispatcher.getInstance();
			ILoginUser user = dispatcher.getUserFromSecurity(code);
			if( user == null )
				return Response.noContent().build();
			user.setConfirmed(true);
			String str = AuthenticationUtils.createDictionaryString(user);
			retval = Response.ok( str ).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
		return retval;
	}

	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/forgotten")
	public Response forgotPassword( String email ) {
		Dispatcher dispatcher=  Dispatcher.getInstance();
		TransactionManager t = new TransactionManager( dispatcher );
		try{
			if( StringUtils.isEmpty(email))
				return Response.notModified( ErrorMessages.INVALID_EMAIL.name()).build();

			logger.info( "Request email: " + email );

			t.open();
			LoginService service = new LoginService( dispatcher ); 
			ILoginUser[] users = service.hasEmail( email.trim());
			boolean result =  !Utils.assertNull(users);
			if(!result )
				return Response.status( Status.NOT_FOUND).build();
			ILoginUser user = users[0];
			user.setSecurity(AuthenticationUtils.generateSecurityCode(user));
			dispatcher.addForgotPassword(user);
			
			Properties props = MailUtils.createProperties(getClass().getResourceAsStream(MailUtils.S_DEFAULT_MAIL_RESOURCE));
			InputStream in = this.getClass().getResourceAsStream(MailUtils.S_RESOURCE_FORGOT_PASSWORD_CODE);
			MailUtils.sendForgotPasswordMail( in, props, users[0], Dispatcher.S_CHURUATA );	
			return Response.ok().build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			t.close();
		}
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/restore-password")
	public Response restorePassword( @QueryParam("confirm") long confirmation, @QueryParam("password") String password, @QueryParam("confirm-password") String confirmPassword) {
		logger.info( "ATTEMPT Confirm password " + confirmation );
		if( confirmation <= 0 )
			return Response.notModified( ErrorMessages.INVALID_REGISTRATION.name()).build();
		else if( StringUtils.isEmpty(password) || StringUtils.isEmpty( confirmPassword )) 
			return Response.notModified( ErrorMessages.INVALID_PASSWORD.name()).build();
		else if( !password.trim().equals( confirmPassword.trim() )) 
			return Response.notModified( ErrorMessages.PASSWORD_DOES_NOT_MATCH_CONFIRMATION.name()).build();

		Dispatcher dispatcher=  Dispatcher.getInstance();
		LoginData loginData = dispatcher.getStoredUser(confirmation);
		if( loginData == null )
			return Response.noContent().build();

		TransactionManager t = new TransactionManager( dispatcher );
		LoginService service = new LoginService( dispatcher ); 
		try{
			t.open();
			Login user = service.find(loginData.getId());
			if( user == null )
				return Response.status( Status.NOT_FOUND).build();
			
			user.setPassword( password );
			return Response.ok().build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			t.close();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/logoff")
	public Response logoff( @QueryParam("user-id") long userid, @QueryParam("security") long security) {
		Dispatcher dispatcher=  Dispatcher.getInstance();
		try{
			if( !dispatcher.isLoggedIn( userid, security ))
				return Response.noContent().build();
			else if( security<=0 )
				return Response.status(Status.BAD_REQUEST).build();
			else {
				ILoginUser user = dispatcher.getLoginUser(userid, security);
				dispatcher.removeUser(user);
				return Response.ok(String.valueOf( user.getId() )).build();
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/unregister")
	public Response unregister( @QueryParam("user-id") long userId, @QueryParam("security") long security ) {
		Dispatcher dispatcher=  Dispatcher.getInstance();
		if( !dispatcher.isRegistered(userId))
			return Response.noContent().build();

		LoginService service = new LoginService( dispatcher );
		TransactionManager t = new TransactionManager( dispatcher );
		try{
			t.open();
			ILoginUser user = dispatcher.getLoginUser( userId, security );
			logger.info( "Unregister " + user.getUserName() );

			dispatcher.removeUser(user);
			service.remove(user.getId());
			return Response.ok(String.valueOf( user.getId() )).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally{
			t.close();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getall")
	public Response getAll( @QueryParam("name") String userName, @QueryParam("security") long security ) {
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
		str[1] = user.getSecurity();
		Gson gson = new Gson();
		return gson.toJson(str, long[].class);
	}

}