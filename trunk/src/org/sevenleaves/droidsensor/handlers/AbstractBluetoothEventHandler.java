package org.sevenleaves.droidsensor.handlers;

import org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStub;

import android.content.Context;

public abstract class AbstractBluetoothEventHandler implements
		BluetoothEventHandler {

	protected Context getContext(){
	
		return null;
	}

	protected BluetoothDeviceStub getBluetoothDevice(){
		
		return null;
	}
	
	public void setBluetoothEvent(BluetoothEvent event) {
	
		
	}

	public void onRemoteDeviceFound(String address){
		
		// nop.
	}
	
	public void onRemoteDeviceDisappeared(String address){
		
		// nop.
	}
	
	public void onRemoteNameUpdated(String address, String name){
		
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
	
	public void onStateChangedTurningOn(){
		
		// nop.
	}
	
	public void onStateChangedTurningOff(){
		
		// nop.
	}


}
