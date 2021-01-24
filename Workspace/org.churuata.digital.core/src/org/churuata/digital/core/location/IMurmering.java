package org.churuata.digital.core.location;

import java.util.Date;


public interface IMurmering {

	String getText();

	void setText(String text);

	long getId();

	IChuruata getChuruata();

	long getUserId();

	Date getCreateDate();

	Date getUpdateDate();

}