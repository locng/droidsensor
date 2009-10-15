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

package com.example.bluetooth.discovery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.example.bluetooth.BluetoothDeviceStub;

/**
 * デバイスを検出し続ける {@link BroadcastReceiver}の実装.
 * 
 * @author esmasui@gmail.com
 * 
 */
public class DiscoveryContinuasReceiver extends BroadcastReceiver {

	/**
	 * discovery終了通知.
	 */
	private static final String DISCOVERY_COMPLETED_ACTION = "android.bluetooth.intent.action.DISCOVERY_COMPLETED";

	/**
	 * BluetoothDeviceServiceのスタブ.
	 */
	private BluetoothDeviceStub _bluetooth;

	public DiscoveryContinuasReceiver(BluetoothDeviceStub bluetooth) {

		_bluetooth = bluetooth;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		_bluetooth.startDiscovery(false);
	}

	/**
	 * このDiscoveryEnablerをContextに登録する.
	 * 
	 * @param context
	 */
	public void registerSelf(Context context) {

		IntentFilter filter = new IntentFilter();
		filter.addAction(DISCOVERY_COMPLETED_ACTION);
		context.registerReceiver(this, filter);

		// 検出を開始する.
		_bluetooth.startDiscovery(false);
	}
}
