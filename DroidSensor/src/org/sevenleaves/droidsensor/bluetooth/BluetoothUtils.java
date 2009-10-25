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

package org.sevenleaves.droidsensor.bluetooth;

import org.sevenleaves.droidsensor.R;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;

public abstract class BluetoothUtils {

	public static final String SCAN_MODE = "android.bluetooth.intent.SCAN_MODE";
	public static final String ADDRESS = "android.bluetooth.intent.ADDRESS";
	public static final String NAME = "android.bluetooth.intent.NAME";
	public static final String ALIAS = "android.bluetooth.intent.ALIAS";
	public static final String RSSI = "android.bluetooth.intent.RSSI";
	public static final String CLASS = "android.bluetooth.intent.CLASS";
	public static final String BLUETOOTH_STATE = "android.bluetooth.intent.BLUETOOTH_STATE";
	public static final String BLUETOOTH_PREVIOUS_STATE = "android.bluetooth.intent.BLUETOOTH_PREVIOUS_STATE";
	public static final String HEADSET_STATE = "android.bluetooth.intent.HEADSET_STATE";
	public static final String HEADSET_PREVIOUS_STATE = "android.bluetooth.intent.HEADSET_PREVIOUS_STATE";
	public static final String HEADSET_AUDIO_STATE = "android.bluetooth.intent.HEADSET_AUDIO_STATE";
	public static final String BOND_STATE = "android.bluetooth.intent.BOND_STATE";
	public static final String BOND_PREVIOUS_STATE = "android.bluetooth.intent.BOND_PREVIOUS_STATE";
	public static final String REASON = "android.bluetooth.intent.REASON";

	public static int getScanMode(Intent intent) {

		int res = intent.getIntExtra(SCAN_MODE, Short.MIN_VALUE);

		return res;
	}

	public static String getAddress(Intent intent) {

		String res = intent.getStringExtra(ADDRESS);

		return res;
	}

	public static void putAddress(Intent intent, String address) {

		intent.putExtra(ADDRESS, address);
	}

	public static String getName(Intent intent) {

		String res = intent.getStringExtra(NAME);

		return res;
	}

	public static String getAlias(Intent intent) {

		String res = intent.getStringExtra(ALIAS);

		return res;
	}

	public static short getRssi(Intent intent) {

		short res = intent.getShortExtra(RSSI, Short.MIN_VALUE);

		return res;
	}

	public static int getBluetoothState(Intent intent) {

		int res = intent.getIntExtra(BLUETOOTH_STATE, Integer.MIN_VALUE);

		return res;
	}

	public static String getMaskedAddress(String address) {

		String vendor = address.substring(0, 8);

		return vendor + ":--:--:--";
	}

	public static String getMajorDeviceClassName(Context context, int deviceClass) {

		int i = BluetoothClass.Device.Major.getDeviceMajor(deviceClass);
		int res;

		switch (i) {
		case BluetoothClass.Device.Major.MISC:
			res = R.string.bt_major_misc;
			break;
		case BluetoothClass.Device.Major.COMPUTER:
			res = R.string.bt_major_computer;
			break;
		case BluetoothClass.Device.Major.PHONE:
			res = R.string.bt_major_phone;
			break;
		case BluetoothClass.Device.Major.NETWORKING:
			res = R.string.bt_major_networking;
			break;
		case BluetoothClass.Device.Major.AUDIO_VIDEO:
			res = R.string.bt_major_audio_video;
			break;
		case BluetoothClass.Device.Major.PERIPHERAL:
			res = R.string.bt_major_peripheral;
			break;
		case BluetoothClass.Device.Major.IMAGING:
			res = R.string.bt_major_imaging;
			break;
		case BluetoothClass.Device.Major.WEARABLE:
			res = R.string.bt_major_wearable;
			break;
		case BluetoothClass.Device.Major.TOY:
			res = R.string.bt_major_toy;
			break;
		case BluetoothClass.Device.Major.HEALTH:
			res = R.string.bt_major_health;
			break;
		default:
			res = R.string.bt_major_uncategorized;
			break;
		}

		return context.getString(res);
	}

}
