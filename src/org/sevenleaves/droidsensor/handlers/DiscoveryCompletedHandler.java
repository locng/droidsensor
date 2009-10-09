package org.sevenleaves.droidsensor.handlers;

public class DiscoveryCompletedHandler extends AbstractBluetoothEventHandler {

	public BluetoothState getResponsibility() {

		return BluetoothState.DISCOVERY_COMPLETED;
	}

	@Override
	public void onScanModeChangedConnectableDiscoverable() {
	
		getBluetoothDevice().startPeriodicDiscovery();
	}
}
