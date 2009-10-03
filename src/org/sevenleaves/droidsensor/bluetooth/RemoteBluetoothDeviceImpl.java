package org.sevenleaves.droidsensor.bluetooth;

public class RemoteBluetoothDeviceImpl extends BluetoothDeviceImpl implements
		RemoteBluetoothDevice {

	private short _rssi;

	private int _deviceClass;

	public RemoteBluetoothDeviceImpl(BluetoothDeviceStub stub, String address) {

		super(stub, address);
	}

	public RemoteBluetoothDeviceImpl(BluetoothDeviceStub stub) {

		super(stub);
	}

	public RemoteBluetoothDeviceImpl(BluetoothDeviceStub stub, String address,
			short rssi) {

		super(stub, address);
		setRssi(rssi);
	}

	public void setRssi(short rssi) {

		_rssi = rssi;
	}

	public short getRssi() {

		return _rssi;
	}

	public int getDeviceClass() {

		return _deviceClass;
	}

	public void setDeviceClass(int deviceClass) {

		_deviceClass = deviceClass;
	}
}
