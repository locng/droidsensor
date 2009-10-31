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

		int i = BluetoothClass.Device.getDevice(deviceClass);
		int res;

		switch (i) {
        // Devices in the COMPUTER major class
        case BluetoothClass.Device.COMPUTER_UNCATEGORIZED:
        	
        	res = R.string.class_COMPUTER_UNCATEGORIZED;
        	break;
        case BluetoothClass.Device.COMPUTER_DESKTOP:
        	
        	res = R.string.class_COMPUTER_DESKTOP;
        	break;
        case BluetoothClass.Device.COMPUTER_SERVER:
        	
        	res = R.string.class_COMPUTER_SERVER;
        	break;
        case BluetoothClass.Device.COMPUTER_LAPTOP:
        	
        	res = R.string.class_COMPUTER_LAPTOP;
        	break;
        case BluetoothClass.Device.COMPUTER_HANDHELD_PC_PDA:
        	
        	res = R.string.class_COMPUTER_HANDHELD_PC_PDA;
        	break;
        case BluetoothClass.Device.COMPUTER_PALM_SIZE_PC_PDA:
        	
        	res = R.string.class_COMPUTER_PALM_SIZE_PC_PDA;
        	break;
        case BluetoothClass.Device.COMPUTER_WEARABLE:
        	
        	res = R.string.class_COMPUTER_WEARABLE;
        	break;

        // Devices in the PHONE major class
        case BluetoothClass.Device.PHONE_UNCATEGORIZED:
        	
        	res = R.string.class_PHONE_UNCATEGORIZED;
        	break;
        case BluetoothClass.Device.PHONE_CELLULAR:
        	
        	res = R.string.class_PHONE_CELLULAR;
        	break;
        case BluetoothClass.Device.PHONE_CORDLESS:
        	
        	res = R.string.class_PHONE_CORDLESS;
        	break;
        case BluetoothClass.Device.PHONE_SMART:
        	
        	res = R.string.class_PHONE_SMART;
        	break;
        case BluetoothClass.Device.PHONE_MODEM_OR_GATEWAY:
        	
        	res = R.string.class_PHONE_MODEM_OR_GATEWAY;
        	break;
        case BluetoothClass.Device.PHONE_ISDN:
        	
        	res = R.string.class_PHONE_ISDN;
        	break;
        // Minor classes for the AUDIO_VIDEO major class
        case BluetoothClass.Device.AUDIO_VIDEO_UNCATEGORIZED:
        	
        	res = R.string.class_AUDIO_VIDEO_UNCATEGORIZED;
        	break;
        case BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET:
        	
        	res = R.string.class_AUDIO_VIDEO_WEARABLE_HEADSET;
        	break;
        case BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE:
        	
        	res = R.string.class_AUDIO_VIDEO_HANDSFREE;
        	break;
        //case BluetoothClass.Device.AUDIO_VIDEO_RESERVED              = 0x040C;
        case BluetoothClass.Device.AUDIO_VIDEO_MICROPHONE:
        	
        	res = R.string.class_AUDIO_VIDEO_MICROPHONE;
        	break;
        case BluetoothClass.Device.AUDIO_VIDEO_LOUDSPEAKER:
        	
        	res = R.string.class_AUDIO_VIDEO_LOUDSPEAKER;
        	break;
        case BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES:
        	
        	res = R.string.class_AUDIO_VIDEO_HEADPHONES;
        	break;
        case BluetoothClass.Device.AUDIO_VIDEO_PORTABLE_AUDIO:
        	
        	res = R.string.class_AUDIO_VIDEO_PORTABLE_AUDIO;
        	break;
        case BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO:
        	
        	res = R.string.class_AUDIO_VIDEO_CAR_AUDIO;
        	break;
        case BluetoothClass.Device.AUDIO_VIDEO_SET_TOP_BOX:
        	
        	res = R.string.class_AUDIO_VIDEO_SET_TOP_BOX;
        	break;
        case BluetoothClass.Device.AUDIO_VIDEO_HIFI_AUDIO:
        	
        	res = R.string.class_AUDIO_VIDEO_HIFI_AUDIO;
        	break;
        case BluetoothClass.Device.AUDIO_VIDEO_VCR:
        	
        	res = R.string.class_AUDIO_VIDEO_VCR;
        	break;
        case BluetoothClass.Device.AUDIO_VIDEO_VIDEO_CAMERA:
        	
        	res = R.string.class_AUDIO_VIDEO_VIDEO_CAMERA;
        	break;
        case BluetoothClass.Device.AUDIO_VIDEO_CAMCORDER:
        	
        	res = R.string.class_AUDIO_VIDEO_CAMCORDER;
        	break;
        case BluetoothClass.Device.AUDIO_VIDEO_VIDEO_MONITOR:
        	
        	res = R.string.class_AUDIO_VIDEO_VIDEO_MONITOR;
        	break;
        case BluetoothClass.Device.AUDIO_VIDEO_VIDEO_DISPLAY_AND_LOUDSPEAKER:
        	
        	res = R.string.class_AUDIO_VIDEO_VIDEO_DISPLAY_AND_LOUDSPEAKER;
        	break;
        case BluetoothClass.Device.AUDIO_VIDEO_VIDEO_CONFERENCING:
        	
        	res = R.string.class_AUDIO_VIDEO_VIDEO_CONFERENCING;
        	break;
        //case BluetoothClass.Device.AUDIO_VIDEO_RESERVED              = 0x0444;
        case BluetoothClass.Device.AUDIO_VIDEO_VIDEO_GAMING_TOY:
        	
        	res = R.string.class_AUDIO_VIDEO_VIDEO_GAMING_TOY;
        	break;

        // Devices in the WEARABLE major class
        case BluetoothClass.Device.WEARABLE_UNCATEGORIZED:
        	
        	res = R.string.class_WEARABLE_UNCATEGORIZED;
        	break;
        case BluetoothClass.Device.WEARABLE_WRIST_WATCH:
        	
        	res = R.string.class_WEARABLE_WRIST_WATCH;
        	break;
        case BluetoothClass.Device.WEARABLE_PAGER:
        	
        	res = R.string.class_WEARABLE_PAGER;
        	break;
        case BluetoothClass.Device.WEARABLE_JACKET:
        	
        	res = R.string.class_WEARABLE_JACKET;
        	break;
        case BluetoothClass.Device.WEARABLE_HELMET:
        	
        	res = R.string.class_WEARABLE_HELMET;
        	break;
        case BluetoothClass.Device.WEARABLE_GLASSES:
        	
        	res = R.string.class_WEARABLE_GLASSES;
        	break;

        // Devices in the TOY major class
        case BluetoothClass.Device.TOY_UNCATEGORIZED:
        	
        	res = R.string.class_TOY_UNCATEGORIZED;
        	break;
        case BluetoothClass.Device.TOY_ROBOT:
        	
        	res = R.string.class_TOY_ROBOT;
        	break;
        case BluetoothClass.Device.TOY_VEHICLE:
        	
        	res = R.string.class_TOY_VEHICLE;
        	break;
        case BluetoothClass.Device.TOY_DOLL_ACTION_FIGURE:
        	
        	res = R.string.class_TOY_DOLL_ACTION_FIGURE;
        	break;
        case BluetoothClass.Device.TOY_CONTROLLER:
        	
        	res = R.string.class_TOY_CONTROLLER;
        	break;
        case BluetoothClass.Device.TOY_GAME:
        	
        	res = R.string.class_TOY_GAME;
        	break;

        // Devices in the HEALTH major class
        case BluetoothClass.Device.HEALTH_UNCATEGORIZED:
        	
        	res = R.string.class_HEALTH_UNCATEGORIZED;
        	break;
        case BluetoothClass.Device.HEALTH_BLOOD_PRESSURE:
        	
        	res = R.string.class_HEALTH_BLOOD_PRESSURE;
        	break;
        case BluetoothClass.Device.HEALTH_THERMOMETER:
        	
        	res = R.string.class_HEALTH_THERMOMETER;
        	break;
        case BluetoothClass.Device.HEALTH_WEIGHING:
        	
        	res = R.string.class_HEALTH_WEIGHING;
        	break;
        case BluetoothClass.Device.HEALTH_GLUCOSE:
        	
        	res = R.string.class_HEALTH_GLUCOSE;
        	break;
        case BluetoothClass.Device.HEALTH_PULSE_OXIMETER:
        	
        	res = R.string.class_HEALTH_PULSE_OXIMETER;
        	break;
        case BluetoothClass.Device.HEALTH_PULSE_RATE:
        	
        	res = R.string.class_HEALTH_PULSE_RATE;
        	break;
        case BluetoothClass.Device.HEALTH_DATA_DISPLAY:
        	
        	res = R.string.class_HEALTH_DATA_DISPLAY;
        	break;

		default:
			res = R.string.class_UNCATEGORIZED;
			break;
		}

		return context.getString(res);
	}

}
