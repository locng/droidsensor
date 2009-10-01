package org.sevenleaves.droidsensor.bluetooth;

public class RemoteBluetoothDeviceImpl extends BluetoothDeviceImpl implements
		RemoteBluetoothDevice {

	private short _rssi;

	public RemoteBluetoothDeviceImpl() {
	
	}
	
	public RemoteBluetoothDeviceImpl(String address, short rssi) {

		super(address);
		_rssi = rssi;
	}



	public void setRssi(short rssi) {
		
		_rssi = rssi;
	}

	public short getRssi() {

		return _rssi;
	}

}
