package org.churuata.digital.authentication.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.TypedQuery;

import org.churuata.digital.authentication.model.Admin;
import org.condast.commons.Utils;
import org.condast.commons.authentication.core.AdminData;
import org.condast.commons.authentication.user.IAdmin;
import org.condast.commons.authentication.user.IAdmin.Roles;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.persistence.service.AbstractEntityService;
import org.condast.commons.persistence.service.IPersistenceService;

public class AdminService extends AbstractEntityService<Admin>{

	private static final String S_QUERY_SELECT = "SELECT a FROM Admin a ";
	private static final String S_QUERY_SELECT_ADMIN = S_QUERY_SELECT + "WHERE a.loginId=:loginId";
	private static final String S_QUERY_SELECT_ROLE = S_QUERY_SELECT + "WHERE a.role=:role";

	public AdminService( IPersistenceService service) {
		super( Admin.class, service);
	}

	public IAdmin create( ILoginUser user, Roles role ) {
		Admin admin = new Admin( user, role );
		super.create(admin);
		return admin;
	}
	
	public IAdmin find( ILoginUser user ) {
		if( user == null )
			return null;
		TypedQuery<IAdmin> query = super.getManager().createQuery( S_QUERY_SELECT_ADMIN, IAdmin.class);
		query.setParameter("loginId", user.getId());
		Collection<IAdmin> users = query.getResultList();
		Admin admin = Utils.assertNull( users)? null: (Admin) users.iterator().next();
		if( admin == null )
			return admin;
		admin.setLogin(user);
		return admin;
	}

	public IAdmin[] findAll( ILoginUser user ) {
		if( user == null )
			return null;
		TypedQuery<Admin> query = super.getManager().createQuery( S_QUERY_SELECT_ADMIN, Admin.class);
		query.setParameter("loginId", user.getId());
		Collection<Admin> users = query.getResultList();
		if( Utils.assertNull(users))
			return null;
		users.forEach(a->{ a.setLogin(user);});
		return users.toArray( new IAdmin[ users.size()]);
	}

	public List<Admin> findAll() {
		List<Admin> admins = super.findAll();
		LoginService ls = new LoginService( super.getService());
		admins.forEach( a -> { 
			ILoginUser login = ls.find(a.getLoginId());
			a.setLogin(login);
		});
		return admins;
	}

	public List<Admin> getAll( Roles role ) {
		String queryStr = S_QUERY_SELECT;
		TypedQuery<Admin> query = null;
		switch( role) {
		case UNKNOWN:
			query = super.getTypedQuery( queryStr );
			break;
		default:
			queryStr = S_QUERY_SELECT_ROLE;
			query = super.getTypedQuery( queryStr );
			query.setParameter("role", false);
			break;
		}
		List<Admin> results = query.getResultList();
		LoginService ls = new LoginService( super.getService());
		results.forEach( a -> { 
			ILoginUser login = ls.find(a.getLoginId());
			a.setLogin(login);
		});
		return results;
	}

	public static AdminData[] toAdminData( Collection<Admin> input ){
		Collection<AdminData> results = new ArrayList<>();
		if( Utils.assertNull(input))
			return results.toArray( new AdminData[ results.size()]);
		input.forEach( o->{ results.add(new AdminData( o ));});
		return results.toArray( new AdminData[ results.size()]);
	}

}