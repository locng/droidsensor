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

/**
 * http://wiki.bluez.org/wiki/Input
 * 
 * @author smasui
 * 
 */
public interface BluetoothDevice {

	/**
	 * Returns the device address.
	 * 
	 * Example: "00:11:22:33:44:55"
	 * 
	 * @return
	 */
	String getAddress();

	/**
	 * Returns the service name.
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Returns the product id.
	 * 
	 * @return
	 */
	int/* uint16 */getProductId();

	/**
	 * Returns the vendor id.
	 * 
	 * @return
	 */
	int /* uint16 */getVendorId();

	/**
	 * Returns the connection status.
	 * 
	 * @return
	 */
	boolean isConnected();

	/**
	 * Connect to the input device.
	 * 
	 */
	void connect();

	/**
	 * Disconnect from the input device.
	 * 
	 */
	void disconnect();
}
