package com.example.bluetooth;

import android.app.Activity;
import android.os.Bundle;

public class BluetoothIntentActivity extends Activity {

	private BluetoothBroadcastReceiver _receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		_receiver = new BluetoothBroadcastReceiver();
		_receiver.registerSelf(BluetoothIntentActivity.this);
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		_receiver.unregisterSelf(BluetoothIntentActivity.this);
	}
}