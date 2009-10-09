package org.sevenleaves.droidsensor.handlers;

import org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStub;

public abstract class AbstractBluetoothEventHandler implements
		BluetoothEventHandler {

	private BluetoothEventController _controller;

	public void setBluetoothEventController(BluetoothEventController controller) {

		_controller = controller;
	}

	public void onRemoteDeviceFound(String address) {

		// nop.
	}

	public void onRemoteDeviceDisappeared(String address) {

		// nop.
	}

	public void onRemoteNameUpdated(String address, String name) {

		// nop.
	}

	public void onScanModeChangedConnectable() {

		// nop.
	}

	public void onScanModeChangedConnectableDiscoverable() {

		// nop.
	}

	public void onScanModeChangedNone() {

		// nop.
	}

	public void onStateChangedOff() {

		// nop.
	}

	public void onStateChangedOn() {

		// nop.
	}

	public void onStateChangedTurningOn() {

		// nop.
	}

	public void onStateChangedTurningOff() {

		// nop.
	}

	public void onDiscoveryStarted() {

		// nop.
	}
	
	public void onDiscoveryCompleted() {

		// nop.
	}
	
	protected void setCurrentState(BluetoothState state) {

		_controller.setCurrentState(state);
	}

	protected BluetoothDeviceStub getBluetoothDevice(){
		
		return _controller.getBluetoothDevice();
	}
	
}
