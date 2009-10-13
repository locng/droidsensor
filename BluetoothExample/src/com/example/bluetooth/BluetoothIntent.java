package com.example.bluetooth;

public enum BluetoothIntent {

	REMOTE_DEVICE_FOUND("android.bluetooth.intent.action.REMOTE_DEVICE_FOUND"),
	
	DISCOVERY_COMPLETED("android.bluetooth.intent.action.DISCOVERY_COMPLETED"),
	
	DISCOVERY_STARTED("android.bluetooth.intent.action.DISCOVERY_STARTED"),
	
	BLUETOOTH_STATE_CHANGED("android.bluetooth.intent.action.BLUETOOTH_STATE_CHANGED"),
	
	NAME_CHANGED("android.bluetooth.intent.action.NAME_CHANGED"),
	
	SCAN_MODE_CHANGED("android.bluetooth.intent.action.SCAN_MODE_CHANGED"),
	
	PAIRING_REQUEST("android.bluetooth.intent.action.PAIRING_REQUEST"),
	
	PAIRING_CANCEL("android.bluetooth.intent.action.PAIRING_CANCEL"),
	
	REMOTE_DEVICE_DISAPPEARED("android.bluetooth.intent.action.REMOTE_DEVICE_DISAPPEARED"),
	
	REMOTE_DEVICE_CLASS_UPDATED("android.bluetooth.intent.action.REMOTE_DEVICE_CLASS_UPDATED"),
	
	REMOTE_DEVICE_CONNECTED("android.bluetooth.intent.action.REMOTE_DEVICE_CONNECTED"),
	
	REMOTE_DEVICE_DISCONNECT_REQUESTED("android.bluetooth.intent.action.REMOTE_DEVICE_DISCONNECT_REQUESTED"),
	
	REMOTE_DEVICE_DISCONNECTED("android.bluetooth.intent.action.REMOTE_DEVICE_DISCONNECTED"),
	
	REMOTE_NAME_UPDATED("android.bluetooth.intent.action.REMOTE_NAME_UPDATED"),
	
	REMOTE_NAME_FAILED("android.bluetooth.intent.action.REMOTE_NAME_FAILED"),
	
	BOND_STATE_CHANGED("android.bluetooth.intent.action.BOND_STATE_CHANGED"),
	
	HEADSET_STATE_CHANGED("android.bluetooth.intent.action.HEADSET_STATE_CHANGED"),
	
	HEADSET_AUDIO_STATE_CHANGED("android.bluetooth.intent.action.HEADSET_ADUIO_STATE_CHANGED");

	private String _action;

	private BluetoothIntent(String action) {

		_action = action;
	}
	
	public String getAction() {
		
		return _action;
	}
}
