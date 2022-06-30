package org.churuata.digital.core;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.churuata.digital.BasicApplication;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.eclipse.rap.rwt.application.EntryPoint;

public class Entries {

	public static final String S_HOME = "churuata";

	public static final String S_ENTRY_POITNT = "EntryPoint";
	private static final String S_PAGE_RESOURCE = "/design/pages.txt";
	
	public enum Pages{
		ACCOUNT,
		ADDRESS,
		ACTIVE,
		BANNER,
		CONTACTS,
		CREATE,
		EDIT,
		EDIT_PROFILE,
		LOCATION,
		LOGIN,
		LOGOFF,
		MAP,
		ORGANISATION,
		SERVICES,
		READY, 
		REGISTER,
		GET_EMAIL,
		REGISTER_SERVICE,
		ADD_CONTACT,
		ADD_ORGANISATION,
		ADD_SERVICES,
		SHOW_LEGAL,
		CONFIRM_SERVICE,
		ADMIN,
		USERS,
		ACCEPTANCE;
	
		@Override
		public String toString() {
			return "/" + StringStyler.xmlStyleString( name());
		}

		public String toPretty() {
			return StringStyler.prettyString(name());
		}

		public String toPath() {
			return BasicApplication.S_CHURUATA + "/" + StringStyler.xmlStyleString( name());
		}
			
		public static boolean isValid( String str ) {
			if( StringUtils.isEmpty(str))
				return false;
			for( Pages page: values()) {
				if( page.name().toLowerCase().equals(str.toLowerCase()))
					return true;
			}
			return false;
		}
	}

	private Map<Pages,PageEntries> pages;
	
	private Class<?> clss;

	private static Entries entry = new Entries();
	
	private Entries() {
		this( Entries.class, Entries.class.getResourceAsStream( S_PAGE_RESOURCE));
	}

	public Entries( Class<?> clss, String path ) {
		this( clss, clss.getResourceAsStream(path));
	}
	
	public Entries( Class<?> clss, InputStream in ) {
		this.clss = clss;
		pages = new HashMap<>();
		Scanner scanner = new Scanner( in );
		try {
			while( scanner.hasNextLine()) {
				String line=  scanner.nextLine();
				if( StringUtils.isEmpty(line) || StringUtils.isComment(line))
					continue;
				String[] split = line.split("[|]");
				PageEntries entry = new PageEntries(split);
				pages.put(entry.page, entry);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			scanner.close();
		}
	}

	public static Entries getInstance() {
		return entry;
	}
	
	public IPageEntry getPageEntry( Pages page ) {
		return this.pages.get(page);
	}
	
	private class PageEntries implements IPageEntry{
		
		private Pages page;
		private String title;
		private String link;
		private String className;

		public PageEntries(String[] split ) {
			String str = StringStyler.styleToEnum(split[0].trim());
			str = str.replace(";", "");
			this.page = Pages.valueOf(str);
			this.className = ( split.length > 1)?split[1].trim(): page.toPretty() + S_ENTRY_POITNT;
			this.title = ( split.length > 2)? split[2].trim(): page.toPretty();
			if( split.length > 3)
				this.link = split[3].trim();
		}

		@Override
		public Pages getPage() {
			return page;
		}

		@Override
		public String getTitle() {
			return title;
		}

		@Override
		public String getLink() {
			return link;
		}

		@Override
		public String getClassName() {
			return className;
		}

		@SuppressWarnings("unchecked")
		@Override
		public EntryPoint getEntryPoint() {
			if( StringUtils.isEmpty( className ))
				return null;
			Class<EntryPoint> builderClass;
			EntryPoint bentryPoint = null;
			try {
				builderClass = (Class<EntryPoint>) clss.getClassLoader().loadClass( className );
				Constructor<EntryPoint> constructor = builderClass.getConstructor();
				bentryPoint = constructor.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return bentryPoint;
		}
	}
}