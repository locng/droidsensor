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

import static org.sevenleaves.droidsensor.ServiceUtils.callLater;
import static org.sevenleaves.droidsensor.ServiceUtils.cancelImmediatly;
import static org.sevenleaves.droidsensor.ServiceUtils.isActionContinue;
import static org.sevenleaves.droidsensor.ServiceUtils.isStopAction;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStub;
import org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStubFactory;
import org.sevenleaves.droidsensor.bluetooth.BluetoothSettings;
import org.sevenleaves.droidsensor.handlers.BluetoothEventControllerImpl;
import org.sevenleaves.droidsensor.handlers.BluetoothEventListener;
import org.sevenleaves.droidsensor.handlers.BluetoothState;
import org.sevenleaves.droidsensor.handlers.DiscoveryCompletedHandler;
import org.sevenleaves.droidsensor.handlers.DiscoveryStartedHandler;
import org.sevenleaves.droidsensor.handlers.ScanModeConnectableDiscoverableHandler;
import org.sevenleaves.droidsensor.handlers.ScanModeConnectableHandler;
import org.sevenleaves.droidsensor.handlers.ScanModeNoneHandler;
import org.sevenleaves.droidsensor.handlers.StateOffHandler;
import org.sevenleaves.droidsensor.handlers.StateOnHandler;
import org.sevenleaves.droidsensor.handlers.StateTurningOffHandler;
import org.sevenleaves.droidsensor.handlers.StateTurningOnHandler;

import twitter4j.TwitterException;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.Handler.Callback;
import android.util.Log;

public class DroidSensorService extends DroidSensorServiceBase {

	private static final String TAG = DroidSensorService.class.getSimpleName();
	
	private static final long INTERVAL_SECONDS = 10L;

	private BluetoothEventListener _receiver;

	private BluetoothEventControllerImpl _controller;

	private volatile boolean _started;

	private DroidSensorInquiry _droidSensorInquiry;

	private Set<String> _devices;

	private final RemoteCallbackList<IDroidSensorCallbackListener> _listeners = new RemoteCallbackList<IDroidSensorCallbackListener>();

	private final IDroidSensorService.Stub _binder = new IDroidSensorService.Stub() {

		public boolean isStarted() throws RemoteException {

			return _receiver != null;
		}

		public void stopService() throws RemoteException {

			DroidSensorService.this.stopService();
		}

		public void addListener(IDroidSensorCallbackListener listener)
				throws RemoteException {

			_listeners.register(listener);
		}

		public void removeListener(IDroidSensorCallbackListener listener)
				throws RemoteException {

			_listeners.unregister(listener);
		}
	};

	class RemoteDeviceHandler extends ScanModeConnectableDiscoverableHandler {

		@Override
		public void onRemoteDeviceFound(String address) {

			if (_devices.contains(address)) {

				return;
			}

			BluetoothDeviceStub stub = BluetoothDeviceStubFactory
					.createBluetoothServiceStub(DroidSensorService.this);

			String name = stub.getRemoteName(address);

			if (name == null) {

				return;
			}

			DroidSensorService.this.onRemoteDeviceFound(address, name);
		}

		@Override
		public void onRemoteNameUpdated(String address, String name) {

			DroidSensorService.this.onRemoteDeviceFound(address, name);
		}

		@Override
		public void onRemoteDeviceDisappeared(String address) {

			_devices.remove(address);
		}
	}

	/**
	 * 起動時にBluetoothがOFFの場合に、
	 * ONになったタイミングでbluetoothアドレスとユーザー名のペアをサーバーに登録するためのハンドラー.
	 * 
	 */
	class RegisterAddressHandler extends StateTurningOnHandler {

		boolean _registerd;

		@Override
		public void onStateChangedOn() {

			if (!_registerd) {

				// TODO エラー時のリトライ処理・通知処理などをいれる.
				boolean succeed = registerAddress();
			}

			_registerd = true;
			super.onStateChangedOn();
		}
	}

	private BluetoothEventControllerImpl createController() {

		BluetoothEventControllerImpl res = new BluetoothEventControllerImpl(
				this);
		// res.addHandler(new ScanModeConnectableDiscoverableHandler());
		res.addHandler(new RemoteDeviceHandler());

		res.addHandler(new ScanModeConnectableHandler());
		res.addHandler(new ScanModeNoneHandler());
		res.addHandler(new StateOffHandler());

		// res.addHandler(new StateTurningOnHandler());
		res.addHandler(new RegisterAddressHandler());

		res.addHandler(new StateOnHandler());
		res.addHandler(new StateTurningOffHandler());
		res.addHandler(new DiscoveryStartedHandler());
		res.addHandler(new DiscoveryCompletedHandler());

		return res;
	}

	public DroidSensorService() {

		_devices = Collections.synchronizedSet(new HashSet<String>());
	}

	@Override
	public IBinder onBind(Intent intent) {

		return _binder;
	}

	@Override
	public void onCreate() {

		Log.d("DroidSensorService", "service create");

		super.onCreate();
	}

	public void stopService() {

		Log.d("DroidSensorService", "stopService");
		cancelImmediatly(this, DroidSensorService.class);
		stopSelf();

		try {

			_receiver.unregisterSelf(this);
		} catch (Exception e) {
			// nop.
		}

		if (_droidSensorInquiry != null) {

			_droidSensorInquiry.cancelAll();
			_droidSensorInquiry = null;
		}

		_receiver = null;
		_controller = null;
		hideNotification();
		_started = false;
		_devices.clear();
		BluetoothSettings.load(this);
	}

	/**
	 * DroidSensorサーバーにBluetoothアドレスとユーザー名のペアを登録する.
	 * 
	 * @return
	 */
	private boolean registerAddress() {

		BluetoothDeviceStub stub = BluetoothDeviceStubFactory
				.createBluetoothServiceStub(this);
		DroidSensorSettings settings = DroidSensorSettings.getInstance(this);
		String address = stub.getAddress();

		try {

			// second-accountは登録しない。(address同じだから)

			Log.d("DroidSensorService", "register twitter ID");

			boolean succeed = DroidSensorUtils.putTwitterId(settings
					.getApiUrl(), address, settings.getTwitterId());

			return succeed;
		} catch (Exception e) {

			return false;
		}
	}

	private BluetoothEventListener createReceiver(
			BluetoothEventControllerImpl controller) {

		BluetoothEventListener res = new BluetoothEventListener();
		res.setHandler(controller);

		return res;
	}

	@Override
	public void onStart(Intent intent, int startId) {

		super.onStart(intent, startId);

		if (isStopAction(intent)) {

			if (_started) {

				stopService();
			}

			return;
		}

		if (isActionContinue(intent)) {

			_started = true;

			Log.d("DroidSensorService", "running");

			if (_started) {

				if (_droidSensorInquiry == null) {

					_droidSensorInquiry = new DroidSensorInquiry(
							DroidSensorService.this);
				}

				if (_receiver == null) {

					BluetoothSettings.save(this);
					showNotification();
					_controller = createController();
					_receiver = createReceiver(_controller);
					_receiver.registerSelf(this);
					fireEvent();
				}

				// callLater(DroidSensorService.this, IDroidSensorService.class,
				// INTERVAL_SECONDS);
				callLater(DroidSensorService.this, DroidSensorService.class,
						INTERVAL_SECONDS);
			}
		}
	}

	@Override
	public void onDestroy() {

		Log.d("DroidSensorService", "service destroy");

		super.onDestroy();

		try {

			_receiver.unregisterSelf(this);
		} catch (Exception e) {
			// nop.
		}

		if (_droidSensorInquiry != null) {

			_droidSensorInquiry.cancelAll();
			_droidSensorInquiry = null;
		}

		_receiver = null;
		_controller = null;
		_started = false;
		_devices.clear();
		BluetoothSettings.load(this);
		hideNotification();
	}

	private void fireEvent() {

		BluetoothDeviceStub bt = _controller.getBluetoothDevice();

		if (bt.isEnabled()) {

			if (bt.isDiscovering()) {

				_controller.setCurrentState(BluetoothState.DISCOVERY_STARTED);
				bt.cancelDiscovery();

				return;
			}

			bt.setScanMode(0x3);

			return;
		}

		_controller.setCurrentState(BluetoothState.STATE_OFF);
		bt.enable();
	}

	public void onRemoteDeviceFound(final String address, String name) {

		if (_devices.contains(address)) {

			return;
		}

		// device-found, name-updateで確実にデバイス名がとれる。
		// それぞれでtwitterIDを問い合わせると、最後のすれ違いユーザーを自分で上書きしてしまう。
		if (name == null) {

			return;
		}

		BluetoothDeviceStub bluetooth = BluetoothDeviceStubFactory
				.createBluetoothServiceStub(DroidSensorService.this);

		// すれ違い通信という名目のため、自分も検出可能モードでなければ通知しないよ。
		if (!isDiscoverable(bluetooth)) {

			return;
		}

		DroidSensorSettings settings = DroidSensorSettings
				.getInstance(DroidSensorService.this);

		final String fixedName;

		if (name.trim().length() > 0) {

			fixedName = name;
		} else {

			fixedName = "*unknown*";
		}

		_droidSensorInquiry.getTwitterUser(address, settings.getTwitterId(),
				new Callback() {

					public boolean handleMessage(Message msg) {

						Bundle data = msg.getData();
						String id = data
								.getString(DroidSensorInquiry.TWITTER_USER);
						
						Log.d(TAG, "id:" + id);
						
						tweetDeviceFound(address, fixedName, id);

						return true;
					}
				});

	}

	private boolean isDiscoverable(BluetoothDeviceStub bluetooth) {

		boolean res = (bluetooth.getScanMode() == BluetoothDeviceStub.SCAN_MODE_CONNECTABLE_DISCOVERABLE);

		return res;
	}

	private String safeString(String str) {

		if (isEmpty(str)) {

			return "";
		}

		return str.trim();
	}

	private boolean isEmpty(String str) {

		if (str == null) {

			return true;
		}

		if (str.trim().length() == 0) {

			return true;
		}

		return false;
	}

	private boolean isPassedByUser(String id, String myId) {

		String s = safeString(id);

		if (!s.startsWith("@")) {

			return false;
		}

		return !isPassedByMe(id, myId);
	}

	private boolean isPassedByMe(String id, String myId) {

		return "@".concat(myId).equals(id);
	}

	private boolean isUser(String id, String myId) {

		if (isEmpty(id)) {

			return false;
		}

		if (isPassedByMe(id, myId)) {

			return false;
		}

		if (isPassedByUser(id, myId)) {

			return false;
		}

		return true;
	}

	private boolean isDeviceWillTweet(DroidSensorSettings settings, String id) {

		String myId = settings.getTwitterId();

		if (isUser(id, myId)) {

			return true;
		}

		if (!settings.isAllBluetoothDevices()) {

			return false;
		}

		if (isPassedByUser(id, myId)) {

			return settings.isDetailPassedUser();
		}

		if (isPassedByMe(id, myId)) {

			return settings.isDetailPassedMe();
		}

		return settings.isDetailPassedNo();
	}

	private void tweetDeviceFound(String address, String name, String id) {

		DroidSensorSettings settings = DroidSensorSettings
				.getInstance(DroidSensorService.this);

		if (!isDeviceWillTweet(settings, id)) {

			_devices.add(address);

			return;
		}

		String tweeted;

		try {

			tweeted = TwitterUtils
					.tweetDeviceFound(address, name, id, settings);
		} catch (TwitterException e) {

			// _handler.post(new Runnable() {
			//
			// public void run() {
			//
			// Toast.makeText(DroidSensorService.this, "tweet failed",
			// Toast.LENGTH_SHORT).show();
			// }
			// });

			if (e.getStatusCode() == 401) {

				String error = "Could not authenticate you.";
				String pref = "Error from Twitter:";

				showDeviceFound(pref + error);
			}

			Log.e("DroidSensorService", Integer.toString(e.getStatusCode()));
			// Log.e("DroidSensorService", e.getgetLocalizedMessage());
			return;
		}

		if (tweeted == null) {

			return;
		}

		Log.d("DroidSensorService", address + "(" + name + ")" + " found.");

		_devices.add(address);

		showDeviceFound(tweeted);

	}

}
