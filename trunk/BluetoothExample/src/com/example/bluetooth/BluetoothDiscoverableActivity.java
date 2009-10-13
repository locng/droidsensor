package com.example.bluetooth;

import android.app.Activity;
import android.os.Bundle;

public class BluetoothDiscoverableActivity extends Activity {

	private BluetoothDeviceStub _bluetooth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		_bluetooth = BluetoothDeviceStubFactory
				.createBluetoothDeviceStub(BluetoothDiscoverableActivity.this);
		
		//デバイスを検出可能にする
		_bluetooth.setScanMode(0x3);
		
		//検出可能時間を無期限に設定する。
		_bluetooth.setDiscoverableTimeout(0);
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		
		//設定を元に戻す
		_bluetooth.setScanMode(0x1);
		_bluetooth.setDiscoverableTimeout(120);
	}
}
