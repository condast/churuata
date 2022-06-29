package org.churuata.digital.authentication.services;

import org.churuata.digital.authentication.model.Admin;
import org.condast.commons.authentication.user.IAdmin;
import org.condast.commons.authentication.user.IAdmin.Roles;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.persistence.service.AbstractEntityService;
import org.condast.commons.persistence.service.IPersistenceService;

public class AdminService extends AbstractEntityService<Admin>{

	public AdminService( IPersistenceService service) {
		super( Admin.class, service);
	}

	public IAdmin create( ILoginUser user, Roles role ) {
		Admin admin = new Admin( user, role );
		super.create(admin);
		return admin;
	}
}
