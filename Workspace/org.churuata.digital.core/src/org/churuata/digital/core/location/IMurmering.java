package org.churuata.digital.core.location;

import java.util.Date;
import java.util.Map;


public interface IMurmering {

	public enum Attributes{
		CONTRIBUTOR,
		TEXT,
		TYPE,
		LINK;
	}

	public static final String S_ANONYMOUS = "anonymous";

	String getText();

	void setText(String text);

	long getId();

	IChuruata getChuruata();

	String getContributor();

	Date getCreateDate();

	Date getUpdateDate();

	Map<String, String> toAttributes();

}