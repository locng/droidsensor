package org.sevenleaves.droidsensor.handlers;

public class StateTurningOffHandler extends AbstractBluetoothEventHandler {

	public BluetoothState getResponsibility() {
		
		return BluetoothState.STATE_TURNING_OFF;
	}

}
