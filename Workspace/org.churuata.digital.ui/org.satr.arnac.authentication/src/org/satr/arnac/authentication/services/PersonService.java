package org.satr.arnac.authentication.services;

import org.condast.commons.na.model.IName;
import org.condast.commons.persistence.service.AbstractEntityService;
import org.satr.arnac.authentication.core.Dispatcher;
import org.satr.arnac.authentication.model.Name;
import org.satr.arnac.authentication.model.Person;

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
