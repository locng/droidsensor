package org.sevenleaves.droidsensor.handlers;

public class StateTurningOnHandler extends AbstractBluetoothEventHandler {

	public BluetoothState getResponsibility() {
		
		return BluetoothState.STATE_TURNING_ON;
	}
	
	@Override
	public void onStateChangedOn() {
	
		getBluetoothDevice().setScanMode(0x3);
	}
}