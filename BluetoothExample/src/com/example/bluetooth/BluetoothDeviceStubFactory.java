package com.example.bluetooth;

import android.content.Context;

public abstract class BluetoothDeviceStubFactory {

	private static final String SERVICE_NAME = "bluetooth";

	private static BluetoothDeviceStub CACHED_INSTANCE;

	public static synchronized BluetoothDeviceStub createBluetoothDeviceStub(
			Context context) {

		if (CACHED_INSTANCE != null) {

			return CACHED_INSTANCE;
		}

		Object bluetoothService = context.getSystemService(SERVICE_NAME);
		BluetoothDeviceStub res = DelegatingProxyFactory.createProxy(
				BluetoothDeviceStub.class, bluetoothService);

		if (CACHED_INSTANCE == null) {

			CACHED_INSTANCE = res;
		}

		return res;
	}
}
