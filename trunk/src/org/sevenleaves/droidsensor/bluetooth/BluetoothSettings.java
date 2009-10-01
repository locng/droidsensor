package org.sevenleaves.droidsensor.bluetooth;

public class BluetoothSettings {

	boolean _enabled = false;;

	int _scanMode = BluetoothServiceStub.SCAN_MODE_CONNECTABLE;

	/**
	 * TODO get default value from system property
	 */
	int _discoverableTimeout = 120;

	public void save(BluetoothServiceStub stub) {

		_enabled = stub.isEnabled();
		_scanMode = stub.getScanMode();
		_discoverableTimeout = stub.getDiscoverableTimeout();
	}

	public void load(BluetoothServiceStub stub) {

		stub.setScanMode(_scanMode);
		stub.setDiscoverableTimeout(_discoverableTimeout);

		if (_enabled && !stub.isEnabled()) {

			stub.enable();
		} else if (!_enabled && stub.isEnabled()) {

			stub.disable();
		}
	}
}