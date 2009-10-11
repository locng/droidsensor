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

/**
 * Bluetoothデバイスがとりうる状態の列挙. 但し、アプリケーションで必要とする状態のみに限定している. <br />
 * 
 * @author esmasui@gmail.com
 * 
 */
public enum BluetoothState {

	/**
	 * Bluetoothが有効になっている状態.
	 */
	BLUETOOTH_STATE_ON,

	/**
	 * Bluetoothが無効になろうとしている状態.
	 */
	BLUETOOTH_STATE_TURNING_OFF,

	/**
	 * Bluetoothが無効になっている状態.
	 */
	BLUETOOTH_STATE_OFF,

	/**
	 * Bluetoothが有効になろうとしている状態.
	 */
	BLUETOOTH_STATE_TURNING_ON,

	/**
	 * 接続・検出のいずれも不可能な状態.
	 */
	SCAN_MODE_NONE,

	/**
	 * 接続が可能な状態.
	 */
	SCAN_MODE_CONNECTABLE,

	/**
	 * 接続・検出ともに可能な状態.
	 */
	SCAN_MODE_CONNECTABLE_DISCOVERABLE,

	/**
	 * デバイスの検索が完了した状態.
	 */
	DISCOVERY_COMPLETED,

	/**
	 * デバイスの検索を開始した状態.
	 */
	DISCOVERY_STARTED

}
