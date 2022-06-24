package org.churuata.digital.core;

import org.churuata.digital.core.Entries.Pages;
import org.eclipse.rap.rwt.application.EntryPoint;

public interface IPageEntry {

	Pages getPage();

	String getTitle();

	String getLink();

	String getClassName();

	EntryPoint getEntryPoint();

}