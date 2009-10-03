package org.sevenleaves.droidsensor.bluetooth;

public class BluetoothSettings {

	boolean _saved = true;

	boolean _enabled = false;;

	int _scanMode = BluetoothDeviceStub.SCAN_MODE_CONNECTABLE;

	/**
	 * TODO get default value from system property
	 */
	int _discoverableTimeout = 120;

	public void save(BluetoothDeviceStub stub) {

		_saved = true;
		_enabled = stub.isEnabled();
		_scanMode = stub.getScanMode();
		_discoverableTimeout = stub.getDiscoverableTimeout();
	}

	public void load(BluetoothDeviceStub stub) {

		if (!_saved) {

			return;
		}

		stub.setScanMode(_scanMode);
		stub.setDiscoverableTimeout(_discoverableTimeout);

		if (_enabled && !stub.isEnabled()) {

			stub.enable();
		} else if (!_enabled && stub.isEnabled()) {

			stub.disable();
		}
	}
}