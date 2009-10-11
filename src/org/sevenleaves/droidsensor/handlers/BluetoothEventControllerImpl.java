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

package org.sevenleaves.droidsensor.handlers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStub;
import org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStubFactory;

import android.content.Context;
import android.util.Log;

/**
 * 
 * 
 * @author esmasui@gmail.com
 * 
 */
public class BluetoothEventControllerImpl implements BluetoothStateHandler,
		BluetoothEventController {

	private static final String TAG = BluetoothEventController.class
			.getSimpleName();

	private Map<BluetoothState, BluetoothStateHandler> _handlers;

	private BluetoothDeviceStub _bluetoothDevice;

	/**
	 * default pseudo handler
	 */
	private BluetoothStateHandler _activeHandler = new AbstractBluetoothStateHandler() {

		public BluetoothState getResponsibility() {

			return null;
		}
	};

	public BluetoothEventControllerImpl(Context context) {

		int size = BluetoothState.values().length;
		_handlers = new ConcurrentHashMap<BluetoothState, BluetoothStateHandler>(
				size);
		_bluetoothDevice = BluetoothDeviceStubFactory
				.createBluetoothServiceStub(context);
	}

	public BluetoothDeviceStub getBluetoothDevice() {

		return _bluetoothDevice;
	}

	public void setBluetoothEventController(BluetoothEventController controller) {

		throw new UnsupportedOperationException();
	}

	public BluetoothState getResponsibility() {

		throw new UnsupportedOperationException();
	}

	public void addHandler(BluetoothStateHandler handler) {

		handler.setBluetoothEventController(this);
		_handlers.put(handler.getResponsibility(), handler);
	}

	public void setCurrentState(BluetoothState currentState) {

		if (!_handlers.containsKey(currentState)) {

			return;
		}

		_activeHandler = _handlers.get(currentState);
	}

	public void onRemoteDeviceDisappeared(String address) {

		Log.d(TAG, _activeHandler.getClass().getSimpleName()
				+ "#onRemoteDeviceDisappeared");

		_activeHandler.onRemoteDeviceDisappeared(address);
	}

	public void onRemoteDeviceFound(String address) {

		Log.d(TAG, _activeHandler.getClass().getSimpleName()
				+ "#onRemoteDeviceFound");

		_activeHandler.onRemoteDeviceFound(address);
	}

	public void onRemoteNameUpdated(String address, String name) {

		Log.d(TAG, _activeHandler.getClass().getSimpleName()
				+ "#onRemoteNameUpdate");

		_activeHandler.onRemoteNameUpdated(address, name);
	}

	public void onScanModeChangedConnectable() {

		Log.d(TAG, _activeHandler.getClass().getSimpleName()
				+ "#onScanModeChangedConnectable");

		BluetoothStateHandler h = _activeHandler;
		setCurrentState(BluetoothState.SCAN_MODE_CONNECTABLE);
		h.onScanModeChangedConnectable();
	}

	public void onScanModeChangedConnectableDiscoverable() {

		Log.d(TAG, _activeHandler.getClass().getSimpleName()
				+ "#onScanModeChangedConnectableDisable");

		BluetoothStateHandler h = _activeHandler;
		setCurrentState(BluetoothState.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
		h.onScanModeChangedConnectableDiscoverable();
	}

	public void onScanModeChangedNone() {

		Log.d(TAG, _activeHandler.getClass().getSimpleName()
				+ "#onScanModeChangedNone");

		BluetoothStateHandler h = _activeHandler;
		setCurrentState(BluetoothState.SCAN_MODE_NONE);
		h.onScanModeChangedNone();
	}

	public void onStateChangedOff() {

		Log.d(TAG, _activeHandler.getClass().getSimpleName()
				+ "#onStateChangedOff");

		BluetoothStateHandler h = _activeHandler;
		setCurrentState(BluetoothState.STATE_OFF);
		h.onStateChangedOff();
	}

	public void onStateChangedOn() {

		Log.d(TAG, _activeHandler.getClass().getSimpleName()
				+ "#onStateChangedOn");

		BluetoothStateHandler h = _activeHandler;
		setCurrentState(BluetoothState.STATE_ON);
		h.onStateChangedOn();
	}

	public void onStateChangedTurningOff() {

		Log.d(TAG, _activeHandler.getClass().getSimpleName()
				+ "#onStateChangedTurningOff");

		BluetoothStateHandler h = _activeHandler;
		setCurrentState(BluetoothState.STATE_TURNING_OFF);
		h.onStateChangedTurningOff();
	}

	public void onStateChangedTurningOn() {

		Log.d(TAG, _activeHandler.getClass().getSimpleName()
				+ "#onStateChangedTurningOn");

		BluetoothStateHandler h = _activeHandler;
		setCurrentState(BluetoothState.STATE_TURNING_ON);
		h.onStateChangedTurningOn();
	}

	public void onDiscoveryStarted() {

		Log.d(TAG, _activeHandler.getClass().getSimpleName()
				+ "#onDiscoveryStarted");

		BluetoothStateHandler h = _activeHandler;
		setCurrentState(BluetoothState.DISCOVERY_STARTED);
		h.onDiscoveryStarted();
	}

	public void onDiscoveryCompleted() {

		Log.d(TAG, _activeHandler.getClass().getSimpleName()
				+ "#onDiscoveryCompleted");

		BluetoothStateHandler h = _activeHandler;
		setCurrentState(BluetoothState.DISCOVERY_COMPLETED);
		h.onDiscoveryCompleted();
	}
}
