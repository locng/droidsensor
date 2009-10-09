package org.sevenleaves.droidsensor.handlers;

import org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStub;

public interface BluetoothEventController {

	void setCurrentState(BluetoothState state);
	
	BluetoothDeviceStub getBluetoothDevice();
}
