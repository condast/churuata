package org.churuata.digital.core.utils;

import org.churuata.digital.core.model.IOrganisation;
import org.condast.commons.na.model.IContactPerson;

public class ChatUtils {

	public static final String S_HI = "Hi ";
	public static final String S_WELCOME= "Son nice of you to contact use. We will get back to you as soon as possible!";

	public static String createChatMessage( IOrganisation organisation, String name ) {
		IContactPerson person = organisation.getContact();
		String contact = ( person == null )?IContactPerson.S_MARIA: person.getName();
		StringBuilder builder = new StringBuilder();
		builder.append( contact );
		builder.append(": " + S_HI);
		builder.append(contact);
		builder.append(", ");
		builder.append(S_WELCOME);
		return builder.toString();
	}
}
