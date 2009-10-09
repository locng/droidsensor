package org.sevenleaves.droidsensor.handlers;

public class StateOnHandler extends AbstractBluetoothEventHandler {

	public BluetoothState getResponsibility() {
		
		return BluetoothState.STATE_ON;
	}

	@Override
	public void onScanModeChangedConnectableDiscoverable() {

		getBluetoothDevice().startPeriodicDiscovery();
	}
}
