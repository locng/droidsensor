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

import org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStub;

/**
 * Bluetoothデバイスの状態遷移を処理するためのハンドラーインターフェイスを実装する抽象基底クラス.
 * ハンドラーはこのクラスを継承し、処理する必要のある遷移をオーバーライドすること.
 * 
 * @author esmasui@gmail.com
 * 
 */
public abstract class AbstractBluetoothStateHandler implements
		BluetoothStateHandler {

	private BluetoothEventController _controller;

	public void setBluetoothEventController(BluetoothEventController controller) {

		_controller = controller;
	}

	public void onRemoteDeviceFound(String address) {

		// nop.
	}

	public void onRemoteDeviceDisappeared(String address) {

		// nop.
	}

	public void onRemoteNameUpdated(String address, String name) {

		// nop.
	}

	public void onScanModeChangedConnectable() {

		// nop.
	}

	public void onScanModeChangedConnectableDiscoverable() {

		// nop.
	}

	public void onScanModeChangedNone() {

		// nop.
	}

	public void onStateChangedOff() {

		// nop.
	}

	public void onStateChangedOn() {

		// nop.
	}

	public void onStateChangedTurningOn() {

		// nop.
	}

	public void onStateChangedTurningOff() {

		// nop.
	}

	public void onDiscoveryStarted() {

		// nop.
	}

	public void onDiscoveryCompleted() {

		// nop.
	}

	protected BluetoothDeviceStub getBluetoothDevice() {

		return _controller.getBluetoothDevice();
	}

}
