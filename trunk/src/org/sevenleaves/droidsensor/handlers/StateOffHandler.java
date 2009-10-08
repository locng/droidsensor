package org.sevenleaves.droidsensor.handlers;

public class StateOffHandler extends AbstractBluetoothEventHandler {

	@Override
	public void onStateChangedOn() {

		setBluetoothEvent(BluetoothEvent.STATE_ON);
		
		getBluetoothDevice().enable();
	}
}
