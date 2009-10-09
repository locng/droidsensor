package org.sevenleaves.droidsensor.handlers;

public class ScanModeNoneHandler extends ScanModeConnectableHandler {

	public BluetoothState getResponsibility() {

		return BluetoothState.SCAN_MODE_NONE;
	}

}
