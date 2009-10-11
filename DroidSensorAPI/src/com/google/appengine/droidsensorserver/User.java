package com.google.appengine.droidsensorserver;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class User {

	@PrimaryKey
	@Persistent
	private String _bluetoothAddress;

	@Persistent(defaultFetchGroup="true")
	private String _twitterUser;

	@Persistent(defaultFetchGroup="true")
	private long _updateTime;
	
	public User() {

	}

	public User(String bluetoothAddress, String twitterUser) {

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

	public long getUpdateTime() {
		return _updateTime;
	}

	public void setUpdateTime(long updateTime) {
		_updateTime = updateTime;
	}

	
}
