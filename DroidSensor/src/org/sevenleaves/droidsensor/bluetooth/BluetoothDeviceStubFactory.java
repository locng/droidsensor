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

/**
 * @author esmasui@gmail.com
 * 
 */
public abstract class BluetoothDeviceStubFactory {

	private static final String SERVICE_NAME = "bluetooth";

	private static BluetoothDeviceStub CACHED_INSTANCE;

	public static synchronized BluetoothDeviceStub createBluetoothServiceStub(
			Context context) {

		if (CACHED_INSTANCE != null) {

			return CACHED_INSTANCE;
		}

		Object bluetooth = context.getSystemService(SERVICE_NAME);
		BluetoothDeviceStub res = DelegatingProxyFactory.createProxy(
				BluetoothDeviceStub.class, bluetooth);

		if (CACHED_INSTANCE == null) {

			CACHED_INSTANCE = res;
		}

		return res;
	}
}
