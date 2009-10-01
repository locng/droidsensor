package org.sevenleaves.droidsensor.bluetooth;

public class BluetoothDeviceImpl implements BluetoothDevice {

	private String _address;

	private String _name;

	private int _productId;

	private int _vendorId;

	public BluetoothDeviceImpl() {
	
	}
	
	public BluetoothDeviceImpl(String address) {
		
		setAddress(address);
	}

	public void connect() {

		throw new UnsupportedOperationException();
	}

	public void disconnect() {

		throw new UnsupportedOperationException();
	}

	public String getAddress() {

		return _address;
	}

	public void setAddress(String address) {

		_address = address;
	}

	public String getName() {

		return _name;
	}

	public void setName(String name) {

		_name = name;
	}

	public int getProductId() {

		return _productId;
	}

	public void setProductId(int productId) {

		_productId = productId;
	}

	public int getVendorId() {

		return _vendorId;
	}

	public void setVendorId(int vendorId) {

		_vendorId = vendorId;
	}

	public boolean isConnected() {

		throw new UnsupportedOperationException();
	}
}
