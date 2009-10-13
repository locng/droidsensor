package com.example.bluetooth;

import android.app.Activity;
import android.bluetooth.IBluetoothDeviceCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class BluetoothSocketActivity extends Activity {

	private BluetoothDeviceStub _bluetooth;

	// socket->bind->WSASetService->listen->accept ですよー

	private static final String HT03A = "00:23:76:1a:2:e3";

	private static final String S11HT = "00:17:83:D0:B8:33";

	private BroadcastReceiver _receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		_bluetooth = BluetoothDeviceStubFactory
				.createBluetoothDeviceStub(BluetoothSocketActivity.this);
		final short uuid16 = 0x0003;

		Log.d("@uuid16", Short.toString(uuid16));
		// _bluetooth.stopPeriodicDiscovery();
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothIntent.SCAN_MODE_CHANGED.toString());
		_receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub

				_bluetooth.getRemoteServiceChannel(S11HT, uuid16,
						new IBluetoothDeviceCallback() {

							public void onGetRemoteServiceChannelResult(
									String arg0, int arg1)
									throws RemoteException {

								Log.d("@serviceChannel", "adress=" + arg0
										+ ",ch=" + Integer.toString(arg1));
							}

							public IBinder asBinder() {

								return null;
							}
						});
			}
		};
		registerReceiver(_receiver, filter);
		_bluetooth.setScanMode(0x1);
		_bluetooth.setDiscoverableTimeout(0);
		_bluetooth.cancelDiscovery();
		_bluetooth.getRemoteName(S11HT);
		

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();

		unregisterReceiver(_receiver);
	}
}