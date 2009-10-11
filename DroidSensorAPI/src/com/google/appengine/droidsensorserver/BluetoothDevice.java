package com.google.appengine.droidsensorserver;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class BluetoothDevice {

	@Persistent
	@PrimaryKey
	private Long _key;

	@Persistent(defaultFetchGroup = "true")
	private String _bluetoothAddress;

	@Persistent(defaultFetchGroup = "true")
	private String _twitterUser;

	@Persistent(defaultFetchGroup = "true")
	private String _message;

	@Persistent(defaultFetchGroup = "true")
	private Long _updated;

	public BluetoothDevice() {

	}

	public BluetoothDevice(String bluetoothAddress, String twitterUser) {

		_key = Long.valueOf(bluetoothAddress.hashCode());
		_bluetoothAddress = bluetoothAddress;
		_twitterUser = twitterUser;
	}

	public String getBluetoothAddress() {

		return _bluetoothAddress;
	}

	public void setBluetoothAddress(String bluetoothAddress) {

		_bluetoothAddress = bluetoothAddress;
	}

	public String getTwitterUser() {

		return _twitterUser;
	}

	public void setTwitterUser(String twitterUser) {

		_twitterUser = twitterUser;
	}

	public Long getKey() {

		return _key;
	}

	public void setKey(Long key) {

		_key = key;
	}

	public String getMessage() {

		return _message;
	}

	public void setMessage(String message) {

		_message = message;
	}

	public Long getUpdated() {

		return _updated;
	}

	public void setUpdated(Long updated) {

		_updated = updated;
	}
}
