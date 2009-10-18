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
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * @author esmasui@gmail.com
 * 
 */
public class SettingsManager {

	public static final String PREFIX = "droidsensor_";

	public static final String TWITTER_ID = PREFIX + "twitter_id";

	public static final String TWITTER_PASSWORD = PREFIX + "twitter_password";

	public static final String ALL_BLUETOOTH_DEVICES = PREFIX
			+ "discovery_all_bluetooth_devices";

	public static final String OPTIONAL_TWITTER_ID = PREFIX
			+ "optional_twitter_id";

	public static final String OPTIONAL_TWITTER_PASSWORD = PREFIX
			+ "optional_twitter_password";

	public static final String DISPATCH_USER = PREFIX + "dispatch_user";

	public static final String DISPATCH_DEVICE = PREFIX + "dispatch_device";

	public static final String TOGGLE_BLUETOOTH = PREFIX + "toggle_bluetooth";

	public static final String DETAIL_PASSED_USER = PREFIX
			+ "device_detail_passed_user";

	public static final String DETAIL_PASSED_ME = PREFIX
			+ "device_detail_passed_me";

	public static final String DETAIL_PASSED_NO = PREFIX
			+ "device_detail_passed_no";

	public static final String NOTICE_CHECK = PREFIX + "notice_check";

	private static final SettingsManager SINGLETON = new SettingsManager();

	public static SettingsManager getInstance(Context context) {

		SINGLETON.refresh(context);

		return SINGLETON;
	}

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

	private int _dispatchPassword;

	private String _apiUrl;

	private String _userTemplate;

	private String _deviceTemplate;

	private String _passedDeviceTemplate;

	private String _passedDeviceAgainTemplate;

	private String _tags;

	private boolean _noticeCheck;

	private SettingsManager() {

	}

	public String getApiUrl() {

		return _apiUrl;
	}

	public String getDeviceTemplate() {

		return _deviceTemplate;
	}

	public int getDispatchDevice() {

		return _dispatchDevice;
	}

	public int getDispatchPassword() {

		return _dispatchPassword;
	}

	public int getDispatchUser() {

		return _dispatchUser;
	}

	public String getOptionalTwitterId() {

		return _optionalTwitterId;
	}

	public String getOptionalTwitterPassword() {

		return _optionalTwitterPassword;
	}

	public String getPassedDeviceAgainTemplate() {

		return _passedDeviceAgainTemplate;
	}

	public String getPassedDeviceTemplate() {

		return _passedDeviceTemplate;
	}

	public String getTags() {

		return _tags;
	}

	public String getTwitterId() {

		return _twitterId;
	}

	public String getTwitterPassword() {

		return _twitterPassword;
	}

	public String getUserTemplate() {

		return _userTemplate;
	}

	public boolean isAllBluetoothDevices() {

		return _allBluetoothDevices;
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

	public boolean isDetailPassedMe() {

		return _detailPassedMe;
	}

	public boolean isDetailPassedNo() {

		return _detailPassedNo;
	}

	public boolean isDetailPassedUser() {

		return _detailPassedUser;
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

	public boolean isToggleBluetooth() {

		return _toggleBluetooth;
	}

	public boolean isNoticeCheck() {
		return _noticeCheck;
	}

	public void setNoticeCheck(boolean noticeCheck) {
		_noticeCheck = noticeCheck;
	}

	public void save(Context context) {

		SharedPreferences prefs = getSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putBoolean(NOTICE_CHECK, _noticeCheck);
		editor.commit();
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
		_detailPassedMe = false;
		_detailPassedNo = false;
		_tags = context.getString(R.string.tags);
	}

	private int getInt(SharedPreferences prefs, String key, int defaultValue) {

		String v = prefs.getString(key, Integer.toString(defaultValue));
		int i = Integer.parseInt(v);

		return i;
	}

	private SharedPreferences getSharedPreferences(Context context) {

		SharedPreferences res = PreferenceManager
				.getDefaultSharedPreferences(context);

		return res;
	}

}
