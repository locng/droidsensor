package org.sevenleaves.droidsensor;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class DroidSensorSettings {

	public static final String TWITTER_ID = "droidsensor_twitter_id";

	public static final String TWITTER_PASSWORD = "droidsensor_twitter_password";

	public static final String ALL_BLUETOOTH_DEVICES = "droidsensor_discovery_all_bluetooth_devices";

	public static final String OPTIONAL_TWITTER_ID = "droidsensor_optional_twitter_id";

	public static final String OPTIONAL_TWITTER_PASSWORD = "droidsensor_optional_twitter_password";

	public static final String DISPATCH_USER = "droidsensor_dispatch_user";

	public static final String DISPATCH_DEVICE = "droidsensor_dispatch_device";

	private String _twitterId;

	private String _twitterPassword;

	private boolean _allBluetoothDevices;

	private String _optionalTwitterId;

	private String _optionalTwitterPassword;

	private int _dispatchUser;

	private int _dispatchDevice;

	public int getDispatchDevice() {
		return _dispatchDevice;
	}

	public void setDispatchDevice(int dispatchDevice) {
		_dispatchDevice = dispatchDevice;
	}

	public String getOptionalTwitterId() {
		return _optionalTwitterId;
	}

	public void setOptionalTwitterId(String optionalTwitterId) {
		_optionalTwitterId = optionalTwitterId;
	}

	public String getOptionalTwitterPassword() {
		return _optionalTwitterPassword;
	}

	public void setOptionalTwitterPassword(String optionalTwitterPassword) {
		_optionalTwitterPassword = optionalTwitterPassword;
	}

	public int getDispatchUser() {
		return _dispatchUser;
	}

	public void setDispatchUser(int dispatchUser) {
		_dispatchUser = dispatchUser;
	}

	public int getDispatchPassword() {
		return _dispatchPassword;
	}

	public void setDispatchPassword(int dispatchPassword) {
		_dispatchPassword = dispatchPassword;
	}

	private int _dispatchPassword;

	private String _apiUrl;

	private String _userTemplate;

	private String _deviceTemplate;

	private String _tags;

	private static final DroidSensorSettings SINGLETON = new DroidSensorSettings();

	public static DroidSensorSettings getInstance(Context context) {

		SINGLETON.refresh(context);

		return SINGLETON;
	}

	private DroidSensorSettings() {

	}

	private SharedPreferences getSharedPreferences(Context context) {

		SharedPreferences res = PreferenceManager
				.getDefaultSharedPreferences(context);

		return res;
	}

	private int getInt(SharedPreferences prefs, String key, int defaultValue) {

		String v = prefs.getString(key, Integer.toString(defaultValue));
		int i = Integer.parseInt(v);

		return i;
	}

	public synchronized void refresh(Context context) {

		SharedPreferences prefs = getSharedPreferences(context);
		_twitterId = prefs.getString(TWITTER_ID, "");
		_twitterPassword = prefs.getString(TWITTER_PASSWORD, "");
		_allBluetoothDevices = prefs.getBoolean(ALL_BLUETOOTH_DEVICES, false);
		_optionalTwitterId = prefs.getString(OPTIONAL_TWITTER_ID, "");
		_optionalTwitterPassword = prefs.getString(OPTIONAL_TWITTER_PASSWORD,
				"");
		_dispatchUser = getInt(prefs, DISPATCH_USER, 0);
		_dispatchDevice = getInt(prefs, DISPATCH_DEVICE, 0);
		_apiUrl = context.getString(R.string.property_server_url);
		_userTemplate = context.getString(R.string.template_user);
		_deviceTemplate = context.getString(R.string.template_device);
		_tags = context.getString(R.string.tags);
	}

	@Override
	public String toString() {

		StringBuilder b = new StringBuilder();
		b.append("twitterId=");
		b.append(_twitterId);
		b.append(',');
		b.append("twitterPassword=");
		b.append(_twitterPassword);
		b.append(',');
		b.append("allBluetoothDevices=");
		b.append(_allBluetoothDevices);
		b.append(',');
		b.append("optionalTwitterId=");
		b.append(_optionalTwitterId);
		b.append(',');
		b.append("optionalTwitterPassword=");
		b.append(_optionalTwitterPassword);
		b.append(',');
		b.append("dispathUser=");
		b.append(_dispatchUser);
		b.append(',');
		b.append("dispatchDevice=");
		b.append(_dispatchDevice);
		b.append(',');
		b.append("apiUrl=");
		b.append(_apiUrl);
		b.append(',');
		b.append("userTemplate=");
		b.append(_userTemplate);
		b.append(',');
		b.append("deviceTemplate=");
		b.append(_deviceTemplate);
		String res = b.toString();

		return res;
	}

	public String getTwitterId() {

		return _twitterId;
	}

	public void setTwitterId(String twitterId) {

		_twitterId = twitterId;
	}

	public String getTwitterPassword() {

		return _twitterPassword;
	}

	public void setTwitterPassword(String twitterPassword) {

		_twitterPassword = twitterPassword;
	}

	public boolean isAllBluetoothDevices() {

		return _allBluetoothDevices;
	}

	public void setAllBluetoothDevices(boolean allBluetoothDevices) {

		_allBluetoothDevices = allBluetoothDevices;
	}

	public String getApiUrl() {

		return _apiUrl;
	}

	public void setApiUrl(String apiUrl) {

		_apiUrl = apiUrl;
	}

	public String getUserTemplate() {

		return _userTemplate;
	}

	public void setUserTemplate(String userTemplate) {

		_userTemplate = userTemplate;
	}

	public String getDeviceTemplate() {

		return _deviceTemplate;
	}

	public void setDeviceTemplate(String deviceTemplate) {

		_deviceTemplate = deviceTemplate;
	}

	public String getTags() {

		return _tags;
	}

	public void setTags(String tags) {

		_tags = tags;
	}
}
