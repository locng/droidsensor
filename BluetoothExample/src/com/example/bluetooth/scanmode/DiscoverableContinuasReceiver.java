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

package com.example.bluetooth.scanmode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.example.bluetooth.BluetoothDeviceStub;

/**
 * デバイスを検出可能状態にし続ける {@link BroadcastReceiver}の実装.
 * 
 * @author esmasui@gmail.com
 * 
 */
public class DiscoverableContinuasReceiver extends BroadcastReceiver {

	/**
	 * scanMode変更通知.
	 */
	private static final String SCAN_MODE_CHANGED_ACTION = "android.bluetooth.intent.action.SCAN_MODE_CHANGED";

	/**
	 * scanMode変更後の値.
	 */
	private static final String SCAN_MODE_EXTRA = "android.bluetooth.intent.SCAN_MODE";

	/**
	 * 検出可能状態.
	 */
	private static final int SCAN_MODE_DISCOVERABLE = 0x3;

	/**
	 * BluetoothDeviceServiceのスタブ.
	 */
	private BluetoothDeviceStub _bluetooth;

	public DiscoverableContinuasReceiver(BluetoothDeviceStub bluetooth) {

		_bluetooth = bluetooth;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		int scanMode = intent.getIntExtra(SCAN_MODE_EXTRA,
				SCAN_MODE_DISCOVERABLE);

		if (scanMode == SCAN_MODE_DISCOVERABLE) {

			return;
		}

		_bluetooth.setScanMode(SCAN_MODE_DISCOVERABLE);
	}

	/**
	 * このDiscoveryEnablerをContextに登録する.
	 * 
	 * @param context
	 */
	public void registerSelf(Context context) {

		IntentFilter filter = new IntentFilter();
		filter.addAction(SCAN_MODE_CHANGED_ACTION);
		context.registerReceiver(this, filter);

		// 検出可能状態にする.
		_bluetooth.setScanMode(SCAN_MODE_DISCOVERABLE);
	}
}
