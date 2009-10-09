package org.sevenleaves.droidsensor.handlers;

public class ScanModeConnectableHandler extends AbstractBluetoothEventHandler {

	public BluetoothState getResponsibility() {
		
		return BluetoothState.SCAN_MODE_CONNECTABLE;
	}

	@Override
	public void onScanModeChangedConnectableDiscoverable() {

		getBluetoothDevice().startPeriodicDiscovery();
	}
}