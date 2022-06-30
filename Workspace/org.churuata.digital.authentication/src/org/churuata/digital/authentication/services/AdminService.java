package org.churuata.digital.authentication.services;

import java.util.Collection;
import java.util.List;

import javax.persistence.TypedQuery;

import org.churuata.digital.authentication.model.Admin;
import org.condast.commons.Utils;
import org.condast.commons.authentication.user.IAdmin;
import org.condast.commons.authentication.user.IAdmin.Roles;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.persistence.service.AbstractEntityService;
import org.condast.commons.persistence.service.IPersistenceService;

public class AdminService extends AbstractEntityService<Admin>{

	private static final String SELECT = "SELECT a FROM Admin a ";
	private static final String SELECT_ADMIN = SELECT + "WHERE a.loginId=:loginId";

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
		TypedQuery<IAdmin> query = super.getManager().createQuery( SELECT_ADMIN, IAdmin.class);
		query.setParameter("loginId", user.getId());
		Collection<IAdmin> users = query.getResultList();
		Admin admin = Utils.assertNull( users)? null: (Admin) users.iterator().next();
		admin.setLogin(user);
		return admin;
	}

	public IAdmin[] findAll( ILoginUser user ) {
		if( user == null )
			return null;
		TypedQuery<Admin> query = super.getManager().createQuery( SELECT_ADMIN, Admin.class);
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
}