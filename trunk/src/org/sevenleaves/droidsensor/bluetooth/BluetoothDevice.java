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
