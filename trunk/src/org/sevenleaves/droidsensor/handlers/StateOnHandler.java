package org.sevenleaves.droidsensor.handlers;

public class StateOnHandler extends AbstractBluetoothEventHandler {

	@Override
	public void onScanModeChangedConnectableDiscoverable() {

		setBluetoothEvent(BluetoothEvent.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
	}
}
