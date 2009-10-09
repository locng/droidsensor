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

import static org.sevenleaves.droidsensor.bluetooth.BluetoothIntentConstants.ADDRESS;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothIntentConstants.NAME;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothIntentConstants.RSSI;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothIntentConstants.SCAN_MODE;
import static org.sevenleaves.droidsensor.bluetooth.BluetoothIntentConstants.BLUETOOTH_STATE;

import android.content.Intent;

public abstract class BluetoothUtils {

	public static String getAddress(Intent intent) {

		String res = intent.getStringExtra(ADDRESS);

		return res;
	}

	public static String getName(Intent intent) {

		String res = intent.getStringExtra(NAME);

		return res;
	}

	public static short getRssi(Intent intent) {

		short res = intent.getShortExtra(RSSI, Short.MIN_VALUE);

		return res;
	}

	public static int getScanMode(Intent intent) {

		int res = intent.getIntExtra(SCAN_MODE, Short.MIN_VALUE);

		return res;
	}

	public static int getState(Intent intent) {

		int res = intent.getIntExtra(BLUETOOTH_STATE, Integer.MIN_VALUE);

		return res;
	}
}
