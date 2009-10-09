package org.sevenleaves.droidsensor.handlers;

public class DiscoveryStartedHandler extends AbstractBluetoothEventHandler {

	public BluetoothState getResponsibility() {

		return BluetoothState.DISCOVERY_STARTED;
	}
	
	@Override
	public void onDiscoveryCompleted() {
	
		getBluetoothDevice().setScanMode(0x3);
	}
}
