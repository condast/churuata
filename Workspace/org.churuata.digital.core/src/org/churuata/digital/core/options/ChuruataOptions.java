package org.churuata.digital.core.options;

import java.util.Locale;
import java.util.Map;

import org.churuata.digital.core.data.OptionsData;
import org.condast.commons.settings.ISettingsSupport;
import org.condast.commons.strings.StringUtils;

public class ChuruataOptions{

	private ISettingsSupport settingsSupport;
	
	private Locale locale;
	
	public ChuruataOptions( ISettingsSupport settingsSupport ) {
		this.settingsSupport = settingsSupport;
		this.locale = Locale.getDefault();
	}

	
	protected Locale getLocale() {
		String locstr = getSettings( OptionsData.Options.LOCALE );
		if( StringUtils.isEmpty(locstr))
			locale = new Locale.Builder().setLanguageTag(locstr).build();
		return locale;
	}


	protected void setLocale(Locale locale) {
		putSettings( OptionsData.Options.LOCALE, locale.toString());
		this.locale = locale;
	}


	public boolean isEnabled() {
		return getBoolean(OptionsData.Options.ENABLE);
	}

	public void setEnabled( boolean enabled ) {
		setBoolean(OptionsData.Options.ENABLE, enabled);
	}

	public void setDebugging( boolean choice ) {
		setBoolean(OptionsData.Options.OPTIONS, choice);
	}

	public String getIpAddress() {
		return getSettings( OptionsData.Options.IP_ADDRESS);
	}

	public void setIPAddress(String address) {
		putSettings( OptionsData.Options.IP_ADDRESS, address );
	}

	public int getPort() {
		int port = Integer.parseInt( getSettings( OptionsData.Options.PORT ));
		return port;
	}

	public void setPort( int port ) {
		putSettings(OptionsData.Options.PORT, String.valueOf(port ));
	}

	private String getSettings( OptionsData.Options key) {
		Map<String, String> data = settingsSupport.getSettings();
		return data.get(key.name());
	}

	private void putSettings( OptionsData.Options key, String value) {
		Map<String, String> data = settingsSupport.getSettings();
		data.put(key.name(), value);
	}

	/**
	 * retrieve the boolean represented by a bit located on an int value
	 * @param name
	 * @param position
	 * @return
	 */
	protected boolean getBoolean( String name, int position ) {
		Map<String, String> data = settingsSupport.getSettings();
		String str = data.get( OptionsData.Options.OPTIONS.name());
		int options = StringUtils.isEmpty(str)?0: Integer.parseInt(str);
		int mask = 1<<position;
		return ((options&mask) > 0);
	}

	/**
	 * Set a boolean, represented by a bit on an int value
	 * @param name
	 * @param position
	 * @param choice
	 */
	public void setBoolean( String name, int position, boolean choice ) {
		Map<String, String> data = settingsSupport.getSettings();
		String str = data.get( name );
		int options = StringUtils.isEmpty(str)?0: Integer.parseInt(str);
		int mask = 1<<position;
		if ( choice )
			options |=mask;
		else {
			mask ^= 0xFFFF;
			options &= mask;
		}
		data.put(name, String.valueOf( options ));
	}

	/**
	 * retrieve the boolean represented by a bit located on an int value
	 * @param name
	 * @param position
	 * @return
	 */
	protected boolean getBoolean( OptionsData.Options option) {
		return getBoolean( option.name(), option.getIndex());
	}

	/**
	 * Set a boolean, represented by a bit on an int value
	 * @param name
	 * @param position
	 * @param choice
	 */
	public void setBoolean( OptionsData.Options option, boolean choice ) {
		setBoolean( OptionsData.Options.OPTIONS.name(), option.getIndex(), choice);
	}

	protected int getOptions() {
		String str = getSettings( OptionsData.Options.OPTIONS );
		int options = StringUtils.isEmpty(str)?0: Integer.parseInt(str);
		return options;
	}

	@Override
	public String toString() {
		return String.valueOf( getOptions() );
	}
}
