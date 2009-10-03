package org.sevenleaves.droidsensor;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class DroidSensorSettings {

	public static final String TWITTER_ID = "droidsensor_twitter_id";

	public static final String TWITTER_PASSWORD = "droidsensor_twitter_password";

	public static final String ALL_BLUETOOTH_DEVICES = "droidsensor_discovery_all_bluetooth_devices";

	private String _twitterId;

	private String _twitterPassword;

	private boolean _allBluetoothDevices;

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

	public synchronized void refresh(Context context) {

		SharedPreferences prefs = getSharedPreferences(context);
		_twitterId = prefs.getString(TWITTER_ID, "");
		_twitterPassword = prefs.getString(TWITTER_PASSWORD, "");
		_allBluetoothDevices = prefs.getBoolean(ALL_BLUETOOTH_DEVICES, false);
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
