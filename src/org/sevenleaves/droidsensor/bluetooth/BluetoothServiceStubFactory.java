package org.sevenleaves.droidsensor.bluetooth;

import android.content.Context;

public abstract class BluetoothServiceStubFactory {

	private static final String SERVICE_NAME = "bluetooth";

	private static BluetoothServiceStub CACHED_INSTANCE;

	public static synchronized BluetoothServiceStub createBluetoothServiceStub(
			Context context) {

		if (CACHED_INSTANCE != null) {

			return CACHED_INSTANCE;
		}

		Object bluetoothService = context.getSystemService(SERVICE_NAME);
		BluetoothServiceStub res = DelegatingProxyFactory.createProxy(
				BluetoothServiceStub.class, bluetoothService);

		if (CACHED_INSTANCE == null) {

			CACHED_INSTANCE = res;
		}

		return res;
	}
}
