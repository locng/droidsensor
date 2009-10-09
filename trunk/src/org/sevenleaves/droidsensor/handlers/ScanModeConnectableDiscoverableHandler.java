package org.sevenleaves.droidsensor.handlers;

public class ScanModeConnectableDiscoverableHandler extends
		AbstractBluetoothEventHandler {

	public BluetoothState getResponsibility() {
	
		return BluetoothState.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
	}
	
	// TODO implements onRemoteDeviceFound

	// TODO implements onRemoteDeviceDisapppeared

	// TODO implements onRemoteNameUpdate

	@Override
	public void onScanModeChangedConnectable() {

		getBluetoothDevice().stopPeriodicDiscovery();
		getBluetoothDevice().setScanMode(0x3);
	}

	@Override
	public void onScanModeChangedNone() {

		onScanModeChangedConnectable();
	}
}
