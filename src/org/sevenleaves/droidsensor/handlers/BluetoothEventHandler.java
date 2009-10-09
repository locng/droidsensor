package org.sevenleaves.droidsensor.handlers;

public interface BluetoothEventHandler {

	BluetoothState getResponsibility();

	void setBluetoothEventController(BluetoothEventController controller);

	void onStateChangedOn();

	void onStateChangedOff();

	void onStateChangedTurningOn();

	void onStateChangedTurningOff();

	void onScanModeChangedNone();

	void onScanModeChangedConnectable();

	void onScanModeChangedConnectableDiscoverable();

	void onRemoteDeviceFound(String address);

	void onRemoteDeviceDisappeared(String address);

	void onRemoteNameUpdated(String address, String name);
	
	void onDiscoveryStarted();
	
	void onDiscoveryCompleted();
}
