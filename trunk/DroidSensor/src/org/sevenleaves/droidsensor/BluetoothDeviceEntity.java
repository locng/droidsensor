package org.sevenleaves.droidsensor;

public class BluetoothDeviceEntity {

	private int _rowID;

	private String _address;

	private int _RSSI;

	private String _name;

	private int _deviceClass;

	private String _company;

	private String _manufacturer;

	private String _twitterID;

	private String _message;

	private double _longitude;

	private double _latitude;

	private int _count;

	private int _status;

	private long _updated;

	public int getRowID() {
		return _rowID;
	}

	public void setRowID(int rowID) {
		_rowID = rowID;
	}

	public String getAddress() {
		return _address;
	}

	public void setAddress(String address) {
		_address = address;
	}

	public int getRSSI() {
		return _RSSI;
	}

	public void setRSSI(int rSSI) {
		_RSSI = rSSI;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public int getDeviceClass() {
		return _deviceClass;
	}

	public void setDeviceClass(int deviceClass) {
		_deviceClass = deviceClass;
	}

	public String getCompany() {
		return _company;
	}

	public void setCompany(String company) {
		_company = company;
	}

	public String getManufacturer() {
		return _manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		_manufacturer = manufacturer;
	}

	public String getTwitterID() {
		return _twitterID;
	}

	public void setTwitterID(String twitterID) {
		_twitterID = twitterID;
	}

	public String getMessage() {
		return _message;
	}

	public void setMessage(String message) {
		_message = message;
	}

	public double getLongitude() {
		return _longitude;
	}

	public void setLongitude(double longitude) {
		_longitude = longitude;
	}

	public double getLatitude() {
		return _latitude;
	}

	public void setLatitude(double latitude) {
		_latitude = latitude;
	}

	public int getCount() {
		return _count;
	}

	public void setCount(int count) {
		_count = count;
	}

	public int getStatus() {
		return _status;
	}

	public void setStatus(int status) {
		_status = status;
	}

	public long getUpdated() {
		return _updated;
	}

	public void setUpdated(long updated) {
		_updated = updated;
	}

	@Override
	public boolean equals(Object o) {

		if (o == this) {

			return true;
		}

		if (!(o instanceof BluetoothDeviceEntity)) {

			return false;
		}

		BluetoothDeviceEntity c = (BluetoothDeviceEntity) o;

		return getAddress().equals(c.getAddress());
	}
	
	@Override
	public int hashCode() {
	
		return getAddress().hashCode();
	}
}
