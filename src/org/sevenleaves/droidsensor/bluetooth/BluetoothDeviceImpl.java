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

package org.sevenleaves.droidsensor.bluetooth;

public class BluetoothDeviceImpl implements BluetoothDevice {

	private BluetoothDeviceStub _stub;

	private String _address;

	private String _name;

	private int _productId;

	private int _vendorId;

	public BluetoothDeviceImpl(BluetoothDeviceStub stub) {

		_stub = stub;
	}

	public BluetoothDeviceImpl(BluetoothDeviceStub stub, String address) {

		this(stub);
		setAddress(address);
	}

	public BluetoothDeviceStub getStub() {

		return _stub;
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

		if (_name != null) {

			return _stub.getRemoteName(getAddress());
		}

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
