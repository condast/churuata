package org.churuata.digital.organisation.services;

import java.util.List;

import javax.persistence.TypedQuery;

import org.churuata.digital.organisation.core.Dispatcher;
import org.churuata.digital.organisation.model.Person;
import org.condast.commons.na.model.IContact;
import org.condast.commons.persistence.service.AbstractEntityService;

public class PersonService extends AbstractEntityService<Person>{

	public static final String S_QUERY_FIND_LOGGED_IN_PERSONS = S_SELECT_QUERY + "SELECT p FROM PERSON WHERE p.userId = :userId";

	private static Dispatcher dispatcher = Dispatcher.getInstance();
	
	public PersonService() {
		super( Person.class, dispatcher );
	}
	
	public Person create( long userId, String name, String title, String description, IContact contact ) {
		Person person = new Person( userId, name, title, description, contact );
		super.create(person);
		return person;
	}

	public List<Person> findForLogin( long userId ) {
		TypedQuery<Person> query = super.getTypedQuery( S_QUERY_FIND_LOGGED_IN_PERSONS );
		query.setParameter("userId", userId);
		List<Person> persons = query.getResultList();
		return persons;
	}
}
