package org.churuata.digital.core.data;

import java.util.HashMap;
import java.util.Map;

import org.condast.commons.data.binary.bool.BooleanUtils;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.strings.StringStyler;

public class OptionsData {

	public enum Options{
		NAME(0),
		LOCALE(1),
		ENABLE(2),
		LOG(9),
		DEBUG(10),
		OPTIONS(11),
		DATA(16),
		IP_ADDRESS(39),
		PORT(40),
		INITIAL_LOCATION(41);

		private byte index;

		private Options(int index) {
			this.index = (byte)index;
		}

		public byte getIndex() {
			return index;
		}

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	private Map<String, String> data;

	public OptionsData() {
		this.data = new HashMap<>();
	}

	public OptionsData(String name, int options, Map<String, String> data) {
		this();
		this.data = data;
		putSettings(Options.NAME, name);
		putSettings(Options.OPTIONS, String.valueOf( options));
	}

	public String getName() {
		return getSettings(Options.NAME);
	}

	public boolean isEnabled() {
		return getBoolean(OptionsData.Options.ENABLE);
	}

	public void setEnabled( boolean enabled ) {
		setBoolean(OptionsData.Options.ENABLE, enabled);
	}

	public void setInitiallocation( LatLng location ) {
		data.put( Options.INITIAL_LOCATION.name(), location.toLocation() );
	}

	public boolean isLogging() {
		return getBoolean(Options.LOG);
	}

	public void setLogging( boolean choice ) {
		setBoolean(Options.LOG, choice);
	}

	public boolean isDebugging() {
		return getBoolean(Options.DEBUG);
	}

	public void setDebugging( boolean choice ) {
		setBoolean(Options.DEBUG, choice);
	}

	public String getIpAddress() {
		return data.get( Options.IP_ADDRESS.name());
	}

	public void setIPAddress(String address) {
		this.data.put( Options.IP_ADDRESS.name(), address);
	}

	public int getPort() {
		int port = Integer.parseInt( data.get( Options.PORT.name() ));
		return port;
	}

	public void setPort( int port ) {
		this.data.put( Options.PORT.name(), String.valueOf(port));
	}

	/**
	 * retrieve the boolean represented by a bit located on an int value
	 * @param name
	 * @param position
	 * @return
	 */
	protected boolean getBoolean( String name, int position ) {
		String str = data.get( Options.OPTIONS.name());
		return BooleanUtils.getBoolean(name, str, position);
	}

	/**
	 * Set a boolean, represented by a bit on an int value
	 * @param name
	 * @param position
	 * @param choice
	 */
	public void setBoolean( String name, int position, boolean choice ) {
		String str = data.get( Options.OPTIONS.name() );
		int options = BooleanUtils.setBoolean(name, str, position, choice );
		data.put(Options.OPTIONS.name(), String.valueOf( options ));
	}

	/**
	 * retrieve the boolean represented by a bit located on an int value
	 * @param name
	 * @param position
	 * @return
	 */
	protected boolean getBoolean( Options option) {
		return getBoolean( option.name(), option.getIndex());
	}

	/**
	 * Set a boolean, represented by a bit on an int value
	 * @param name
	 * @param position
	 * @param choice
	 */
	public void setBoolean( Options option, boolean choice ) {
		setBoolean( option.name(), option.getIndex(), choice);
	}

	public String getSettings(Options key) {
		return data.get( key.name());
	}

	public Map<String, String> getData() {
		return data;
	}

	public void putSettings(Options key, boolean selection) {
		setBoolean(key, selection);
	}

	public void putSettings(Options key, int selection) {
		data.put( key.name(), String.valueOf(selection));
	}

	public void putSettings(Options key, String name) {
		data.put( key.name(), String.valueOf(name));
	}
}
