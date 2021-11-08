package org.churuata.digital.authentication.services;

import org.churuata.digital.authentication.core.Dispatcher;
import org.churuata.digital.authentication.model.Name;
import org.churuata.digital.authentication.model.Person;
import org.condast.commons.na.model.IName;
import org.condast.commons.persistence.service.AbstractEntityService;

public class PersonService extends AbstractEntityService<Person>{

	private static Dispatcher dispatcher = Dispatcher.getInstance();
	
	public PersonService() {
		super( Person.class, dispatcher );
	}
	
	public Person create( String firstName, String prefix, String lastName ) {
		IName name=  new Name( firstName, prefix, lastName);
		Person person = new Person( name );
		super.create(person);
		return person;
	}
}
