/*
 * Copyright (C) 2009, DroidSensor - http://code.google.com/p/droidsensor/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

	@Override
	public boolean equals(Object o) {

		if (o == this) {

			return true;
		}

		if (!(o instanceof BluetoothDeviceEntity)) {

			return false;
		}

		BluetoothDeviceEntity c = (BluetoothDeviceEntity) o;

		// 要望とりこみなう.
		// ohgro:現状の表示形式の方がお手間だったとは思いますが、個別ログ表示の方が前回がいつか？とか解って良いかもです　つぶやいたかつぶやいてないかも解りますし〜　#droidsensor
		// return getAddress().equals(c.getAddress());
		return getRowID() == c.getRowID();
	}

	public String getAddress() {
		return _address;
	}

	public String getCompany() {
		return _company;
	}

	public int getCount() {
		return _count;
	}

	public int getDeviceClass() {
		return _deviceClass;
	}

	public double getLatitude() {
		return _latitude;
	}

	public double getLongitude() {
		return _longitude;
	}

	public String getManufacturer() {
		return _manufacturer;
	}

	public String getMessage() {
		return _message;
	}

	public String getName() {
		return _name;
	}

	public int getRowID() {
		return _rowID;
	}

	public int getRSSI() {
		return _RSSI;
	}

	public int getStatus() {
		return _status;
	}

	public String getTwitterID() {
		return _twitterID;
	}

	public long getUpdated() {
		return _updated;
	}

	@Override
	public int hashCode() {

		return getRowID();
	}

	public void setAddress(String address) {
		_address = address;
	}

	public void setCompany(String company) {
		_company = company;
	}

	public void setCount(int count) {
		_count = count;
	}

	public void setDeviceClass(int deviceClass) {
		_deviceClass = deviceClass;
	}

	public void setLatitude(double latitude) {
		_latitude = latitude;
	}

	public void setLongitude(double longitude) {
		_longitude = longitude;
	}

	public void setManufacturer(String manufacturer) {
		_manufacturer = manufacturer;
	}

	public void setMessage(String message) {
		_message = message;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setRowID(int rowID) {
		_rowID = rowID;
	}

	public void setRSSI(int rSSI) {
		_RSSI = rSSI;
	}

	public void setStatus(int status) {
		_status = status;
	}

	public void setTwitterID(String twitterID) {
		_twitterID = twitterID;
	}

	public void setUpdated(long updated) {
		_updated = updated;
	}
}
