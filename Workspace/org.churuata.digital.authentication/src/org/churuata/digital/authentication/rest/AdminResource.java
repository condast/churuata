package org.churuata.digital.authentication.rest;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.churuata.digital.authentication.core.Dispatcher;
import org.churuata.digital.authentication.model.Admin;
import org.churuata.digital.authentication.services.AdminService;
import org.condast.commons.authentication.core.AdminData;
import org.condast.commons.authentication.user.IAdmin;
import org.condast.commons.authentication.user.IAdmin.Roles;
import org.condast.commons.authentication.user.ILoginUser;
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

//Sets the path to base URL + /smash
@Path("/admin")
public class AdminResource{

	public static final String S_ERR_UNKNOWN_REQUEST = "An invalid request was rertrieved: ";
	public static final String S_ERR_INVALID_USER    = "The provided credentials are invalid:";

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public AdminResource() {
		super();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/get-all")
	public Response getall( @QueryParam("user-id") long userId, @QueryParam("security") long security, @QueryParam("role") String rolestr ) {
		Dispatcher adispatcher=  Dispatcher.getInstance();
		if( !adispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED).build();

		IAdmin.Roles role = StringUtils.isEmpty(rolestr)?Roles.UNKNOWN: Roles.valueOf(StringStyler.styleToEnum(rolestr));
		logger.info( "Login " + userId );
		Dispatcher dispatcher=  Dispatcher.getInstance();
		TransactionManager t = new TransactionManager( dispatcher );
		try{
			t.open();
			AdminService as = new AdminService( dispatcher );
			AdminData[] admins = AdminService.toAdminData( as.getAll( role ) );
			Gson gson = new Gson();
			
			String str = gson.toJson( admins, AdminData[].class );
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
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/set-role")
	public Response setRole( @QueryParam("user-id") long userId, @QueryParam("security") long security, 
			@QueryParam("role") String rolestr ) {
		Dispatcher dispatcher=  Dispatcher.getInstance();
		if( !dispatcher.isLoggedIn(userId, security))
			return Response.status( Status.UNAUTHORIZED).build();

		ILoginUser user = dispatcher.getLoginUser(userId, security);
		if( user == null )
			return Response.status(Status.NOT_FOUND).build();
		
		IAdmin.Roles role = StringUtils.isEmpty(rolestr)?Roles.UNKNOWN: Roles.valueOf(StringStyler.styleToEnum(rolestr));
		logger.info( "Login " + userId );
		TransactionManager t = new TransactionManager( dispatcher );
		try{
			t.open();
			AdminService as = new AdminService( dispatcher );
			Admin admin = (Admin) as.find(user);
			if( admin == null )
				return Response.status(Status.NOT_FOUND).build();
			admin.setRole(role);
			as.update(admin);
			AdminData result = new AdminData( admin );
			Gson gson = new Gson();
			
			String str = gson.toJson( result, AdminData.class );
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

}