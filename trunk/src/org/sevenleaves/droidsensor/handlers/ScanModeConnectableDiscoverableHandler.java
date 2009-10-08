package org.sevenleaves.droidsensor.handlers;

public class ScanModeConnectableDiscoverableHandler extends
		AbstractBluetoothEventHandler {

	// TODO implements onRemoteDeviceFound

	// TODO implements onRemoteDeviceDisapppeared

	// TODO implements onRemoteNameUpdate

	@Override
	public void onScanModeChangedConnectable() {

		setBluetoothEvent(BluetoothEvent.STATE_ON);

		getBluetoothDevice().setScanMode(0x3);
	}

	@Override
	public void onScanModeChangedNone() {

		onScanModeChangedConnectable();
	}
}
