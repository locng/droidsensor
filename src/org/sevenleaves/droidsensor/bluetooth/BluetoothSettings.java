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

public class BluetoothSettings {

	boolean _saved = true;

	boolean _enabled = false;;

	int _scanMode = BluetoothDeviceStub.SCAN_MODE_CONNECTABLE;

	/**
	 * TODO get default value from system property
	 */
	int _discoverableTimeout = 120;

	public void save(BluetoothDeviceStub stub) {

		_saved = true;
		_enabled = stub.isEnabled();
		_scanMode = stub.getScanMode();
		_discoverableTimeout = stub.getDiscoverableTimeout();
	}

	public void load(BluetoothDeviceStub stub) {

		if (!_saved) {

			return;
		}

		stub.setScanMode(_scanMode);
		stub.setDiscoverableTimeout(_discoverableTimeout);

		if (_enabled && !stub.isEnabled()) {

			stub.enable();
		} else if (!_enabled && stub.isEnabled()) {

			stub.disable();
		}
	}

	public boolean isEnabled() {

		return _enabled;
	}

	public int getScanMode() {

		return _scanMode;
	}

	public int getDiscoverableTimeout() {

		return _discoverableTimeout;
	}
}