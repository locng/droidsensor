package com.example.bluetooth;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class BluetoothSyncActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		BluetoothDeviceStub bluetooth = BluetoothDeviceStubFactory
				.createBluetoothDeviceStub(BluetoothSyncActivity.this);
		bluetooth.enable();
		String name = bluetooth.getName();
		Toast.makeText(BluetoothSyncActivity.this, name, Toast.LENGTH_SHORT)
				.show();
	}
}