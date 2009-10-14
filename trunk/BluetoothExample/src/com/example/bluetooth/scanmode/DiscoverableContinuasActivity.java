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
