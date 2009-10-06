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

/**
 * stolen from android-bluetoothapi.
 * 
 * @author esmasui@gmail.com
 * 
 */
public abstract class BluetoothIntentConstants {

	//extras.
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
	
	//actions.
	public static final String REMOTE_DEVICE_FOUND_ACTION = "android.bluetooth.intent.action.REMOTE_DEVICE_FOUND";
	public static final String DISCOVERY_COMPLETED_ACTION = "android.bluetooth.intent.action.DISCOVERY_COMPLETED";
	public static final String DISCOVERY_STARTED_ACTION = "android.bluetooth.intent.action.DISCOVERY_STARTED";
	public static final String BLUETOOTH_STATE_CHANGED_ACTION = "android.bluetooth.intent.action.BLUETOOTH_STATE_CHANGED";
	public static final String NAME_CHANGED_ACTION = "android.bluetooth.intent.action.NAME_CHANGED";
	public static final String SCAN_MODE_CHANGED_ACTION = "android.bluetooth.intent.action.SCAN_MODE_CHANGED";
	public static final String PAIRING_REQUEST_ACTION = "android.bluetooth.intent.action.PAIRING_REQUEST";
	public static final String PAIRING_CANCEL_ACTION = "android.bluetooth.intent.action.PAIRING_CANCEL";
	public static final String REMOTE_DEVICE_DISAPPEARED_ACTION = "android.bluetooth.intent.action.REMOTE_DEVICE_DISAPPEARED";
	public static final String REMOTE_DEVICE_CLASS_UPDATED_ACTION = "android.bluetooth.intent.action.REMOTE_DEVICE_CLASS_UPDATED";
	public static final String REMOTE_DEVICE_CONNECTED_ACTION = "android.bluetooth.intent.action.REMOTE_DEVICE_CONNECTED";
	public static final String REMOTE_DEVICE_DISCONNECT_REQUESTED_ACTION = "android.bluetooth.intent.action.REMOTE_DEVICE_DISCONNECT_REQUESTED";
	public static final String REMOTE_DEVICE_DISCONNECTED_ACTION = "android.bluetooth.intent.action.REMOTE_DEVICE_DISCONNECTED";
	public static final String REMOTE_NAME_UPDATED_ACTION = "android.bluetooth.intent.action.REMOTE_NAME_UPDATED";
	public static final String REMOTE_NAME_FAILED_ACTION = "android.bluetooth.intent.action.REMOTE_NAME_FAILED";
	public static final String BOND_STATE_CHANGED_ACTION = "android.bluetooth.intent.action.BOND_STATE_CHANGED_ACTION";
	public static final String HEADSET_STATE_CHANGED_ACTION = "android.bluetooth.intent.action.HEADSET_STATE_CHANGED";
	public static final String HEADSET_AUDIO_STATE_CHANGED_ACTION = "android.bluetooth.intent.action.HEADSET_ADUIO_STATE_CHANGED";

}
