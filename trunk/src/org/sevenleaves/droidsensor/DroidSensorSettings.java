/*
 * Copyright (C) 2009, DroidSensor - http://code.google.com/p/droidsensor/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

	public static final String TOGGLE_BLUETOOTH = "droidsensor_toggle_bluetooth";

	public static final String DETAIL_PASSED_USER = "droidsensor_device_detail_passed_user";

	public static final String DETAIL_PASSED_ME = "droidsensor_device_detail_passed_me";

	public static final String DETAIL_PASSED_NO = "droidsensor_device_detail_passed_no";

	private String _twitterId;

	private String _twitterPassword;

	private boolean _allBluetoothDevices;

	private String _optionalTwitterId;

	private String _optionalTwitterPassword;

	private int _dispatchUser;

	private int _dispatchDevice;

	private boolean _detailPassedUser;

	private boolean _detailPassedMe;

	private boolean _detailPassedNo;

	private boolean _toggleBluetooth;

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

	private String _passedDeviceTemplate;

	private String _passedDeviceAgainTemplate;

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
		_dispatchUser = getInt(prefs, DISPATCH_USER, 1);
		_dispatchDevice = getInt(prefs, DISPATCH_DEVICE, 1);
		_apiUrl = context.getString(R.string.property_server_url);
		_userTemplate = context.getString(R.string.template_user);
		_deviceTemplate = context.getString(R.string.template_device);
		_passedDeviceTemplate = context
				.getString(R.string.template_passed_device);
		_passedDeviceAgainTemplate = context
				.getString(R.string.template_passed_device_again);
		_toggleBluetooth = prefs.getBoolean(TOGGLE_BLUETOOTH, false);
		_detailPassedUser = prefs.getBoolean(DETAIL_PASSED_USER, true);
		_detailPassedMe = prefs.getBoolean(DETAIL_PASSED_ME, true);
		_detailPassedNo = prefs.getBoolean(DETAIL_PASSED_NO, true);

		_tags = context.getString(R.string.tags);
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

	public String getPassedDeviceTemplate() {

		return _passedDeviceTemplate;
	}

	public void setPassedDeviceTemplate(String passedDeviceTemplate) {

		_passedDeviceTemplate = passedDeviceTemplate;
	}

	public String getPassedDeviceAgainTemplate() {

		return _passedDeviceAgainTemplate;
	}

	public void setPassedDeviceAgainTemplate(String passedDeviceAgainTemplate) {

		_passedDeviceAgainTemplate = passedDeviceAgainTemplate;
	}

	public boolean isDetailPassedUser() {

		return _detailPassedUser;
	}

	public void setDetailPassedUser(boolean detailPassedUser) {

		_detailPassedUser = detailPassedUser;
	}

	public boolean isDetailPassedMe() {

		return _detailPassedMe;
	}

	public void setDetailPassedMe(boolean detailPassedMe) {

		_detailPassedMe = detailPassedMe;
	}

	public boolean isDetailPassedNo() {

		return _detailPassedNo;
	}

	public void setDetailPassedNo(boolean detailPassedNo) {

		_detailPassedNo = detailPassedNo;
	}

	public boolean isToggleBluetooth() {

		return _toggleBluetooth;
	}

	public void setToggleBluetooth(boolean toggleBluetooth) {

		_toggleBluetooth = toggleBluetooth;
	}

	public String getTags() {

		return _tags;
	}

	public void setTags(String tags) {

		_tags = tags;
	}

	public boolean isOptionalAccountUses() {

		if (getDispatchUser() > 1) {

			return true;
		}

		if (!isAllBluetoothDevices()) {

			return false;
		}

		return getDispatchDevice() > 1;
	}

	public boolean isBasicAccountUses() {

		if (getDispatchUser() != 2) {

			return true;
		}

		if (!isAllBluetoothDevices()) {

			return false;
		}

		return getDispatchDevice() != 2;
	}

}
