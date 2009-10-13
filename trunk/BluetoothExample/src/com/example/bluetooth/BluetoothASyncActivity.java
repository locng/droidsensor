package com.example.bluetooth;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

public class BluetoothASyncActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		final BluetoothDeviceStub bluetooth = BluetoothDeviceStubFactory
				.createBluetoothDeviceStub(BluetoothASyncActivity.this);

		final BroadcastReceiver receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {

				String name = bluetooth.getName();
				Toast.makeText(BluetoothASyncActivity.this, name,
						Toast.LENGTH_SHORT).show();
				unregisterReceiver(this);
			}
		};

		String action = BluetoothIntent.BLUETOOTH_STATE_CHANGED.getAction();
		IntentFilter filter = new IntentFilter(action);
		registerReceiver(receiver, filter);
		bluetooth.enable();
	}
}