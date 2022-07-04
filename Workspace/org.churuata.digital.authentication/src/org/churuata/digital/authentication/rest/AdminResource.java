package org.churuata.digital.authentication.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import org.churuata.digital.authentication.model.Login;
import org.churuata.digital.authentication.services.AdminService;
import org.churuata.digital.authentication.services.LoginService;
import org.condast.commons.authentication.core.AdminData;
import org.condast.commons.authentication.core.LoginData;
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
			IAdmin admin = as.find(user);
			if(( admin == null ) || !admin.isAdmin() )
				return Response.status( Status.UNAUTHORIZED).build();
			LoginService ls = new LoginService( dispatcher);
			Collection<LoginData> results = new ArrayList<>();
			List<Login> users = ls.findAll();
			users.forEach( u->{ 
				LoginData data = new LoginData(u);
				IAdmin adm = as.find(u);
				if(( adm != null ) && ( Roles.UNKNOWN.equals(role) || role.equals(adm.getRole())))
					data.setAdmin( new AdminData( adm ));
				results.add(data);
			});
			Gson gson = new Gson();
			
			String str = gson.toJson( results.toArray(new LoginData[ results.size()]), LoginData[].class );
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
			@QueryParam("login-user") long loginUser, @QueryParam("role") String rolestr ) {
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
			if(( admin == null ) || !admin.isAdmin() )
				return Response.status( Status.UNAUTHORIZED).build();

			LoginService ls = new LoginService( dispatcher );
			ILoginUser client = ls.find(loginUser);
			if( client == null )
				return Response.status( Status.NOT_FOUND).build();
			
			admin = (Admin) as.find(client);
			if(IAdmin.Roles.GUEST.getIndex() >= role.getIndex()){
				if( admin != null ) {
					as.remove(admin.getId());
				}
			}else if( admin == null )
				admin = (Admin) as.create(client, role );
			else
				admin.setRole(role);
			as.update(admin);
			
			LoginData result = new LoginData(client);
			if( admin != null )
				result.setAdmin( new AdminData( admin ));
			Gson gson = new Gson();
			
			String str = gson.toJson( result, LoginData.class );
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