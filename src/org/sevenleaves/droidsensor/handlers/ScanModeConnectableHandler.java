package org.sevenleaves.droidsensor.handlers;

public class ScanModeConnectableHandler extends AbstractBluetoothEventHandler {

	@Override
	public void onScanModeChangedConnectableDiscoverable() {
	
		setBluetoothEvent(BluetoothEvent.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
	}
}
