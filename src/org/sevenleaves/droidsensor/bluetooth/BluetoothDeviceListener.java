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

import android.content.Context;

public interface BluetoothDeviceListener {

	// REMOTE_DEVICE_FOUND_ACTION
	void onRemoteDeviceFound(Context context, RemoteBluetoothDevice device);

	// DISCOVERY_COMPLETED_ACTION

	// DISCOVERY_STARTED_ACTION

	// BLUETOOTH_STATE_CHANGED_ACTION
	// BLUETOOTH_STATE_OFF
	// BLUETOOTH_STATE_ON
	// BLUETOOTH_STATE_TURNING_OFF
	// BLUETOOTH_STATE_TURNING_ON
	void onEnabled(Context context);

	void onDisabled(Context context);

	// NAME_CHANGED_ACTION

	// SCAN_MODE_CHANGED_ACTION
	// SCAN_MODE_CONNECTABLE
	// SCAN_MODE_CONNECTABLE_DISCOVERABLE
	// SCAN_MODE_NONE

	void onScanModeConnectable(Context context);

	void onScanModeConnectableDiscoverable(Context context);

	void onScanModeNone(Context context);

	// PAIRING_REQUEST_ACTION

	// PAIRING_CANCEL_ACTION

	// REMOTE_DEVICE_DISAPPEARED_ACTION
	void onRemoteDeviceDisappeared(Context context, String address);

	// REMOTE_DEVICE_CLASS_UPDATED_ACTION

	// REMOTE_DEVICE_CONNECTED_ACTION

	// REMOTE_DEVICE_DISCONNECT_REQUESTED_ACTION

	// REMOTE_DEVICE_DISCONNECTED_ACTION

	// REMOTE_NAME_UPDATED_ACTION
	void onRemoteNameUpdated(Context context, RemoteBluetoothDevice device);
	
	// REMOTE_NAME_FAILED_ACTION

	// BOND_STATE_CHANGED_ACTION

	// HEADSET_STATE_CHANGED_ACTION

	// HEADSET_AUDIO_STATE_CHANGED_ACTION

	static final int BOND_BONDED = 1;
	static final int BOND_BONDING = 2;
	static final int BOND_NOT_BONDED = 0;
	static final int RESULT_FAILURE = -1;
	static final int RESULT_SUCCESS = 0;
	static final int UNBOND_REASON_AUTH_CANCELED = 3;
	static final int UNBOND_REASON_AUTH_FAILED = 1;
	static final int UNBOND_REASON_AUTH_REJECTED = 2;
	static final int UNBOND_REASON_DISCOVERY_IN_PROGRESS = 5;
	static final int UNBOND_REASON_REMOTE_DEVICE_DOWN = 4;
	static final int UNBOND_REASON_REMOVED = 6;
	

}
