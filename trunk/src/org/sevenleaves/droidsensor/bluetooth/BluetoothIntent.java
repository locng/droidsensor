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
 * TODO BluetoothIntentConstantsの定数を参照するように変更する.
 * 
 * <br />
 * Bluetoothイベントで通知されるIntentのアクションの列挙.
 * 
 * @author esmasui@gmail.com
 * 
 */
public enum BluetoothIntent {

	REMOTE_DEVICE_FOUND("android.bluetooth.intent.action.REMOTE_DEVICE_FOUND"),

	DISCOVERY_COMPLETED("android.bluetooth.intent.action.DISCOVERY_COMPLETED"),

	DISCOVERY_STARTED("android.bluetooth.intent.action.DISCOVERY_STARTED"),

	BLUETOOTH_STATE_CHANGED(
			"android.bluetooth.intent.action.BLUETOOTH_STATE_CHANGED"),

	NAME_CHANGED("android.bluetooth.intent.action.NAME_CHANGED"),

	SCAN_MODE_CHANGED("android.bluetooth.intent.action.SCAN_MODE_CHANGED"),

	PAIRING_REQUEST("android.bluetooth.intent.action.PAIRING_REQUEST"),

	PAIRING_CANCEL("android.bluetooth.intent.action.PAIRING_CANCEL"),

	REMOTE_DEVICE_DISAPPEARED(
			"android.bluetooth.intent.action.REMOTE_DEVICE_DISAPPEARED"),

	REMOTE_DEVICE_CLASS_UPDATED(
			"android.bluetooth.intent.action.REMOTE_DEVICE_CLASS_UPDATED"),

	REMOTE_DEVICE_CONNECTED(
			"android.bluetooth.intent.action.REMOTE_DEVICE_CONNECTED"),

	REMOTE_DEVICE_DISCONNECT_REQUESTED(
			"android.bluetooth.intent.action.REMOTE_DEVICE_DISCONNECT_REQUESTED"),

	REMOTE_DEVICE_DISCONNECTED(
			"android.bluetooth.intent.action.REMOTE_DEVICE_DISCONNECTED"),

	REMOTE_NAME_UPDATED("android.bluetooth.intent.action.REMOTE_NAME_UPDATED"),

	REMOTE_NAME_FAILED("android.bluetooth.intent.action.REMOTE_NAME_FAILED"),

	BOND_STATE_CHANGED("android.bluetooth.intent.action.BOND_STATE_CHANGED"),

	HEADSET_STATE_CHANGED(
			"android.bluetooth.intent.action.HEADSET_STATE_CHANGED"),

	HEADSET_AUDIO_STATE_CHANGED(
			"android.bluetooth.intent.action.HEADSET_ADUIO_STATE_CHANGED");

	private String _action;

	private BluetoothIntent(String action) {

		_action = action;
	}

	public String getAction() {

		return _action;
	}

	public static BluetoothIntent fromAction(String action) {

		for (BluetoothIntent e : values()) {

			if (e.getAction().equals(action)) {

				return e;
			}
		}

		return null;
	}
}
