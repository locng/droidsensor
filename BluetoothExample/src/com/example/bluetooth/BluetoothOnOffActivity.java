package com.example.bluetooth;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class BluetoothOnOffActivity extends Activity {

	private BluetoothDeviceStub _bluetooth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		_bluetooth = BluetoothDeviceStubFactory
				.createBluetoothDeviceStub(BluetoothOnOffActivity.this);
		//_bluetooth.enable();
		Log.d("@address", "#" + _bluetooth.getAddress());
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
//		_bluetooth.disable();
	}
}