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

import android.app.Activity;
import android.os.Bundle;

import com.example.bluetooth.BluetoothDeviceStub;
import com.example.bluetooth.BluetoothDeviceStubFactory;

public class DiscoverableContinuasActivity extends Activity {

	private DiscoverableContinuasReceiver _receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// BluetoothDeviceのスタブを作成(1),
		// - http://d.hatena.ne.jp/esmasui/20091003/1254547708
		// BluetoothDeviceのスタブを作成(2),
		// - http://d.hatena.ne.jp/esmasui/20091003/1254548029
		// BluetoothDeviceのスタブを作成(3),
		// - http://d.hatena.ne.jp/esmasui/20091003/1254548283
		BluetoothDeviceStub bluetooth = BluetoothDeviceStubFactory
				.createBluetoothDeviceStub(this);

		_receiver = new DiscoverableContinuasReceiver(bluetooth);
		_receiver.registerSelf(this);
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		unregisterReceiver(_receiver);
	}
}
