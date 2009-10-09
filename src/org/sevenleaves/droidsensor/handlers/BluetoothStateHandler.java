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
 * Bluetoothデバイスの状態遷移を処理するためのハンドラーインターフェイス.
 * 
 * @author esmasui@gmail.com
 * 
 */
public interface BluetoothStateHandler {

	/**
	 * このインターフェイスを実装するハンドラークラスが受け持つ状態を返す.
	 * 
	 * @return
	 */
	BluetoothState getResponsibility();

	/**
	 * コントローラーを受け取る.
	 * 
	 * @param controller
	 */
	void setBluetoothEventController(BluetoothEventController controller);

	/**
	 * デバイスが有効になった時に呼ばれる.
	 */
	void onStateChangedOn();

	/**
	 * デバイスが無効になった時に呼ばれる.
	 */
	void onStateChangedOff();

	/**
	 * デバイスが有効になろうとしている時に呼ばれる.
	 */
	void onStateChangedTurningOn();

	/**
	 * デバイスが無効になろうとしている時に呼ばれる.
	 */
	void onStateChangedTurningOff();

	/**
	 * デバイスが接続・検出ともに不可能になった時に呼ばれる.
	 */
	void onScanModeChangedNone();

	/**
	 * デバイスが接続可能になった時に呼ばれる.
	 */
	void onScanModeChangedConnectable();

	/**
	 * デバイスが接続・検出可能になった時に呼ばれる.
	 */
	void onScanModeChangedConnectableDiscoverable();

	/**
	 * リモートデバイスを検出した時に呼ばれる.
	 * 
	 * @param address
	 */
	void onRemoteDeviceFound(String address);

	/**
	 * 検出したリモートデバイスが消失した時に呼ばれる.
	 * 
	 * @param address
	 */
	void onRemoteDeviceDisappeared(String address);

	/**
	 * 検出したリモートデバイスの名前が返された時に呼ばれる.
	 * 
	 * @param address
	 * @param name
	 */
	void onRemoteNameUpdated(String address, String name);

	/**
	 * リモートデバイスの検索を開始した時に呼ばれる.
	 */
	void onDiscoveryStarted();

	/**
	 * リモートデバイスの検索を終了した時に呼ばれる.
	 */
	void onDiscoveryCompleted();
}
