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

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings.SettingNotFoundException;

public abstract class BluetoothSettings {

	private static final String SYSTEM_PROPERTIES_BLUETOOTH_ENABLE = "droidsensor_bluetooth_enabled";

	public static void save(Context context) {

		BluetoothDeviceStub stub = BluetoothDeviceStubFactory
				.createBluetoothServiceStub(context);

		System.setProperty(SYSTEM_PROPERTIES_BLUETOOTH_ENABLE, Boolean
				.toString(stub.isEnabled()));
	}

	public static void load(Context context) {

		BluetoothDeviceStub stub = BluetoothDeviceStubFactory
				.createBluetoothServiceStub(context);
		ContentResolver resolver = context.getContentResolver();

		try {

			loadBluetoothDiscoverbilityTimeout(resolver, stub);
			loadBluetoothDiscoverbility(resolver, stub);
			loadBluetoothOn(resolver, stub);

		} catch (SettingNotFoundException e) {

			e.printStackTrace();
		}

		// System.s
		// System.setProperty(SYSTEM_PROPERTIES_BLUETOOTH_ENABLE, null);
	}

	private static void loadBluetoothOn(ContentResolver resolver,
			BluetoothDeviceStub stub) throws SettingNotFoundException {

		String str = System.getProperty(SYSTEM_PROPERTIES_BLUETOOTH_ENABLE);
		boolean v = (str == null ? false : Boolean.valueOf(str));

		if (stub.isEnabled()) {

			if (!v) {

				stub.disable();
			}

			return;
		}

		if (v) {

			stub.enable();
		}

		// Whether bluetooth is enabled/disabled 0=disabled. 1=enabled.
		// int v = Settings.Secure.getInt(resolver,
		// Settings.Secure.BLUETOOTH_ON);
		//
		// switch (v) {
		// case 0:
		//
		// if (stub.isEnabled()) {
		//
		// stub.disable();
		// }
		//
		// break;
		//
		// case 1:
		//
		// if (!stub.isEnabled()) {
		//
		// stub.enable();
		// }
		//
		// break;
		//
		// default:
		//
		// break;
		// }
	}

	private static void loadBluetoothDiscoverbility(ContentResolver resolver,
			BluetoothDeviceStub stub) throws SettingNotFoundException {

		stub.setScanMode(0x1);
		// // 2 -- discoverable and connectable
		// // 1 -- connectable but not discoverable
		// // 0 -- neither connectable nor discoverable
		// int v = Integer.parseInt(Settings.System.getString(resolver,
		// Settings.System.BLUETOOTH_DISCOVERABILITY));
		//
		// switch (v) {
		// case 0:
		//
		// stub.setScanMode(0x0);
		//
		// break;
		//
		// case 1:
		//
		// stub.setScanMode(0x1);
		//
		// break;
		//
		// case 2:
		//
		// stub.setScanMode(0x3);
		//
		// break;
		//
		// default:
		//
		// break;
		// }
	}

	private static void loadBluetoothDiscoverbilityTimeout(
			ContentResolver resolver, BluetoothDeviceStub stub)
			throws SettingNotFoundException {

		stub.setDiscoverableTimeout(120);
		// int v = Settings.System.getInt(resolver,
		// Settings.System.BLUETOOTH_DISCOVERABILITY_TIMEOUT);
		//
		// stub.setDiscoverableTimeout(v);
	}
}