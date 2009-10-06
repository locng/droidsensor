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
 * http://wiki.bluez.org/wiki/Adapter
 * @author esmasui@gmail.com
 *
 */
public interface BluetoothDeviceStub {

	// android.bluetooth.BluetoothDevice(android.bluetooth.IBluetoothDevice);
	boolean cancelBondProcess(String arg0);

	void cancelDiscovery();

	boolean cancelPin(String arg0);

	boolean createBond(String arg0);

	boolean disable();

	boolean disconnectRemoteDeviceAcl(String arg0);

	boolean enable();

	String getAddress();

	int getBluetoothState();

	int getBondState(String arg0);

	String getCompany();

	int getDiscoverableTimeout();

	String getManufacturer();

	String getName();

	int getRemoteClass(String arg0);

	String getRemoteCompany(String arg0);

	byte[] getRemoteFeatures(String arg0);

	String getRemoteManufacturer(String arg0);

	String getRemoteName(String arg0);

	String getRemoteRevision(String arg0);

	boolean getRemoteServiceChannel(String arg0, short arg1,
			android.bluetooth.IBluetoothDeviceCallback arg2);

	String getRemoteVersion(String address);

	String getRevision();

	int getScanMode();

	String getVersion();

	boolean isAclConnected(String address);

	boolean isDiscovering();

	boolean isEnabled();

	boolean isPeriodicDiscovery();

	String lastSeen(String address);

	String lastUsed(String address);

	String[] listAclConnections();

	String[] listBonds();

	String[] listRemoteDevices();

	boolean removeBond(String address);

	void setDiscoverableTimeout(int timeout);

	boolean setName(String name);

	boolean setPin(String address, byte[] pin);

	void setScanMode(int scanMode);

	boolean startDiscovery();

	boolean startDiscovery(boolean arg0);

	boolean startPeriodicDiscovery();

	boolean stopPeriodicDiscovery();

	/* static */boolean checkBluetoothAddress(String arg0);

	/* static */byte[] convertPinToBytes(String arg0);

	public static final int BLUETOOTH_STATE_OFF = 0;
	public static final int BLUETOOTH_STATE_ON = 2;
	public static final int BLUETOOTH_STATE_TURNING_OFF = 3;
	public static final int BLUETOOTH_STATE_TURNING_ON = 1;
	public static final int BOND_BONDED = 1;
	public static final int BOND_BONDING = 2;
	public static final int BOND_NOT_BONDED = 0;
	public static final int RESULT_FAILURE = -1;
	public static final int RESULT_SUCCESS = 0;
	public static final int SCAN_MODE_CONNECTABLE = 1;
	public static final int SCAN_MODE_CONNECTABLE_DISCOVERABLE = 3;
	public static final int SCAN_MODE_NONE = 0;
	public static final int UNBOND_REASON_AUTH_CANCELED = 3;
	public static final int UNBOND_REASON_AUTH_FAILED = 1;
	public static final int UNBOND_REASON_AUTH_REJECTED = 2;
	public static final int UNBOND_REASON_DISCOVERY_IN_PROGRESS = 5;
	public static final int UNBOND_REASON_REMOTE_DEVICE_DOWN = 4;
	public static final int UNBOND_REASON_REMOVED = 6;
	// private final android.bluetooth.IBluetoothDevice mService;

}
