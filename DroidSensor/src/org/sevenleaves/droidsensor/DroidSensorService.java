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

import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStub;
import org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStubFactory;
import org.sevenleaves.droidsensor.bluetooth.BluetoothSettings;
import org.sevenleaves.droidsensor.handlers.BluetoothEventControllerImpl;
import org.sevenleaves.droidsensor.handlers.BluetoothState;
import org.sevenleaves.droidsensor.handlers.BluetoothStateListenerAdapter;
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
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.Handler.Callback;
import android.util.Log;

/**
 * @author esmasui@gmail.com
 * 
 */
public class DroidSensorService extends ServiceSupport {

	/**
	 * 定期的にBluetoothをOFF/ONするためのハンドラークラス.
	 * DonutでDiscoveryを繰り返すとコアライブラリのメモリリークにより端末がリスタートするため.
	 * 但し、リスタートまでの時間がやや伸びる程度の効果しかないかもしれない.
	 */
	private class DonutRemoteDeviceHandler extends RemoteDeviceHandler {

		@Override
		public void onScanModeChangedConnectable() {

			getBluetoothDevice().stopPeriodicDiscovery();
			getBluetoothDevice().disable();
		}
	}

	/**
	 * 定期的にBluetoothをOFF/ONするためのハンドラークラス.
	 * DonutでDiscoveryを繰り返すとコアライブラリのメモリリークにより端末がリスタートするため.
	 */
	private class DonutScanModeNoneHandler extends ScanModeNoneHandler {

		@Override
		public void onStateChangedOff() {

			getBluetoothDevice().enable();
		}
	}

	/**
	 * 起動時にBluetoothがOFFの場合に、
	 * ONになったタイミングでbluetoothアドレスとユーザー名のペアをサーバーに登録するためのハンドラー.
	 * 
	 */
	private class RegisterAddressHandler extends StateTurningOnHandler {

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

	/**
	 * ローカルBluetoothデバイスが検出可能状態でリモートデバイスを発見した時に通知するためのハンドラー.
	 * 検出可能状態であるConnectableDiscoverableの時のみ通知するのは、すれ違い通信という状況をつくるため.
	 * 
	 */
	private class RemoteDeviceHandler extends
			ScanModeConnectableDiscoverableHandler {

		@Override
		public void onRemoteDeviceDisappeared(String address) {

			_devices.remove(address);
		}

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
	}

	private static final int REMOTE_DEVICE_FOUND_MESSAGE = 1;

	/**
	 * Logのカテゴリー.
	 */
	private static final String TAG = DroidSensorService.class.getSimpleName();

	private static final long INTERVAL_SECONDS = 10L;

	private BluetoothStateListenerAdapter _receiver;

	private BluetoothEventControllerImpl _controller;

	private volatile boolean _started;

	private DroidSensorInquiry _droidSensorInquiry;

	/**
	 * すでにつぶやいたデバイスのBluetoothアドレスを保持する.
	 */
	private Set<String> _devices;

	private final RemoteCallbackList<IDroidSensorCallbackListener> _listeners = new RemoteCallbackList<IDroidSensorCallbackListener>();

	private Handler _handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			if (msg.what != REMOTE_DEVICE_FOUND_MESSAGE) {

				super.dispatchMessage(msg);

				return;
			}

			for (int i = 0, size = _listeners.beginBroadcast(); i < size; ++i) {

				IDroidSensorCallbackListener item = _listeners
						.getBroadcastItem(i);

				try {

					item.deviceFound((String) msg.obj);
				} catch (RemoteException e) {

					Log.e(TAG, e.getLocalizedMessage(), e);
				}
			}

			_listeners.finishBroadcast();
		};
	};

	private final IDroidSensorService.Stub _binder = new IDroidSensorService.Stub() {

		public void addListener(IDroidSensorCallbackListener listener)
				throws RemoteException {

			_listeners.register(listener);
		}

		public boolean isStarted() throws RemoteException {

			return _receiver != null;
		}

		public void removeListener(IDroidSensorCallbackListener listener)
				throws RemoteException {

			_listeners.unregister(listener);
		}

		public void stopService() throws RemoteException {

			DroidSensorService.this.stopService();
		}
	};

	public DroidSensorService() {

		_devices = Collections.synchronizedSet(new HashSet<String>());
	}

	@Override
	public IBinder onBind(Intent intent) {

		return _binder;
	}

	@Override
	public void onCreate() {

		Log.d(TAG, "service create");

		super.onCreate();
	}

	@Override
	public void onDestroy() {

		Log.d(TAG, "service destroy");

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

			Log.d(TAG, "running");

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

	public void stopService() {

		Log.d(TAG, "stopService");
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
	 * コントローラーにハンドラーを登録して初期化する.
	 * 
	 * @return
	 */
	private BluetoothEventControllerImpl createController() {

		BluetoothEventControllerImpl res = new BluetoothEventControllerImpl(
				this);
		// res.addHandler(new ScanModeConnectableDiscoverableHandler());
		RemoteDeviceHandler remoteDeviceHandler = createRemoteDeviceHandler();
		res.addHandler(remoteDeviceHandler);

		res.addHandler(new ScanModeConnectableHandler());
		// res.addHandler(new ScanModeNoneHandler());
		ScanModeNoneHandler scanModeNoneHandler = createScanModeNoneHandler();
		res.addHandler(scanModeNoneHandler);

		res.addHandler(new StateOffHandler());

		// res.addHandler(new StateTurningOnHandler());
		res.addHandler(new RegisterAddressHandler());

		res.addHandler(new StateOnHandler());
		res.addHandler(new StateTurningOffHandler());
		res.addHandler(new DiscoveryStartedHandler());
		res.addHandler(new DiscoveryCompletedHandler());

		return res;
	}

	private BluetoothStateListenerAdapter createReceiver(
			BluetoothEventControllerImpl controller) {

		BluetoothStateListenerAdapter res = new BluetoothStateListenerAdapter();
		res.setHandler(controller);

		return res;
	}

	/**
	 * {@link RemoteDeviceHandler}をインスタンス化する.
	 * toggleBluetoothOnOffオプションが有効な場合はDonut環境で発生するメモリリーク対策のカスタムハンドラーをインスタンス化する.
	 * 
	 * @return
	 */
	private RemoteDeviceHandler createRemoteDeviceHandler() {

		SettingsManager settings = SettingsManager
				.getInstance(DroidSensorService.this);

		if (settings.isToggleBluetooth()) {

			return new DonutRemoteDeviceHandler();
		}

		return new RemoteDeviceHandler();
	}

	/**
	 * 
	 * {@link ScanModeNoneHandler}をインスタンス化する.
	 * toggleBluetoothOnOffオプションが有効な場合はDonut環境で発生するメモリリーク対策のカスタムハンドラーをインスタンス化する.
	 * 
	 * @return
	 */
	private ScanModeNoneHandler createScanModeNoneHandler() {

		SettingsManager settings = SettingsManager
				.getInstance(DroidSensorService.this);

		if (settings.isToggleBluetooth()) {

			return new DonutScanModeNoneHandler();
		}

		return new ScanModeNoneHandler();
	}

	/**
	 * 最初の状態遷移を発生させる.
	 */
	private void fireEvent() {

		BluetoothDeviceStub bt = _controller.getBluetoothDevice();

		if (bt.isEnabled()) {

			if (bt.isDiscovering()) {

				_controller.setCurrentState(BluetoothState.DISCOVERY_STARTED);
				bt.cancelDiscovery();

				return;
			}

			_controller.setCurrentState(BluetoothState.BLUETOOTH_STATE_ON);
			bt.setScanMode(0x3);

			return;
		}

		_controller.setCurrentState(BluetoothState.BLUETOOTH_STATE_OFF);
		bt.enable();
	}

	/**
	 * リモートデバイスがつぶやく対象であることを確認する.
	 * 
	 * @param settings
	 * @param id
	 * @return 対象の場合はtrue、対象外の場合はfalseを返す.
	 */
	private boolean isDeviceWillTweet(SettingsManager settings, String id) {

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

	/**
	 * リモートデバイスの検索がすでに開始されていることを確認する.
	 * 
	 * @param bluetooth
	 * @return すでに開始されている場合はtrue、開始されていない場合はfalseを返す.
	 */
	private boolean isDiscoverable(BluetoothDeviceStub bluetooth) {

		boolean res = (bluetooth.getScanMode() == BluetoothDeviceStub.SCAN_MODE_CONNECTABLE_DISCOVERABLE);

		return res;
	}

	/**
	 * 文字列が空文字であることを確認する.
	 * 
	 * @param str
	 * @return 空文字の場合はtrue、空文字ではない場合はfalseを返す.
	 */
	private boolean isEmpty(String str) {

		if (str == null) {

			return true;
		}

		if (str.trim().length() == 0) {

			return true;
		}

		return false;
	}

	/**
	 * リモートデバイスが過去に自分がすれちがったものであることを確認する.
	 * 
	 * @param id
	 * @param myId
	 * @return
	 */
	private boolean isPassedByMe(String id, String myId) {

		return "@".concat(myId).equals(id);
	}

	/**
	 * リモートデバイスが過去に他のユーザーにすれちがわれたものであることを確認する.
	 * 
	 * @param id
	 * @param myId
	 * @return
	 */
	private boolean isPassedByUser(String id, String myId) {

		String s = safeString(id);

		if (!s.startsWith("@")) {

			return false;
		}

		return !isPassedByMe(id, myId);
	}

	/**
	 * リモートデバイスとペアになったユーザー名が、他のユーザーのアカウントであることを確認する.
	 * 
	 * @param id
	 * @param myId
	 * @return
	 */
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

	/**
	 * リモートデバイスを発見した時に
	 * 
	 * @param address
	 * @param name
	 */
	private void onRemoteDeviceFound(final String address, String name) {

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

		SettingsManager settings = SettingsManager
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

	private void persistBluetoothDevice(String address, String name,
			String twitterID, String message, boolean tweeted) {

		DroidSensorDatabaseOpenHelper dbHelper = new DroidSensorDatabaseOpenHelper(
				DroidSensorService.this);
		SQLiteDatabase db = null;

		try {

			BluetoothDeviceStub stub = BluetoothDeviceStubFactory
					.createBluetoothServiceStub(DroidSensorService.this);
			db = dbHelper.getWritableDatabase();
			BluetoothDeviceEntityDAO dao = new BluetoothDeviceEntityDAO(db);

			BluetoothDeviceEntity e;

			// 要望とりこみなう.
			// ohgro:現状の表示形式の方がお手間だったとは思いますが、個別ログ表示の方が前回がいつか？とか解って良いかもです　つぶやいたかつぶやいてないかも解りますし〜　#droidsensor
			e = new BluetoothDeviceEntity();
			e.setAddress(address);
			// e.setRSSI();
			e.setName(name);
			e.setDeviceClass(stub.getRemoteClass(address));
			e.setCompany(stub.getRemoteCompany(address));
			e.setManufacturer(stub.getRemoteManufacturer(address));
			e.setTwitterID(twitterID);
			e.setMessage(message);
			// e.setLongitude();
			// e.setLatitude();
			e.setCount(1);
			// e.setStatus();
			e.setStatus(tweeted ? 1 : 0);
			e.setUpdated(Calendar.getInstance().getTimeInMillis());
			dao.insert(e);
			// }
		} finally {

			if (db != null) {

				db.close();
			}
		}
	}

	/**
	 * DroidSensorサーバーにBluetoothアドレスとユーザー名のペアを登録する.
	 * 
	 * @return
	 */
	private boolean registerAddress() {

		BluetoothDeviceStub stub = BluetoothDeviceStubFactory
				.createBluetoothServiceStub(this);
		SettingsManager settings = SettingsManager.getInstance(this);
		String address = stub.getAddress();

		try {

			// second-accountは登録しない。(address同じだから)

			Log.d(TAG, "register twitter ID");

			boolean succeed = DroidSensorUtils.putTwitterId(settings
					.getApiUrl(), address, settings.getTwitterId());

			return succeed;
		} catch (Exception e) {

			return false;
		}
	}

	private String safeString(String str) {

		if (isEmpty(str)) {

			return "";
		}

		return str.trim();
	}

	private void sendMessage(String address) {

		Message msg = new Message();
		msg.obj = address;
		msg.what = REMOTE_DEVICE_FOUND_MESSAGE;
		_handler.sendMessage(msg);
	}

	private void tweetDeviceFound(String address, String name, String id) {

		SettingsManager settings = SettingsManager
				.getInstance(DroidSensorService.this);

		if (!isDeviceWillTweet(settings, id)) {

			persistBluetoothDevice(address, name, id, null, false);
			sendMessage(address);
			_devices.add(address);

			return;
		}

		String tweeted;

		try {

			tweeted = TwitterUtils
					.tweetDeviceFound(address, name, id, settings);
		} catch (TwitterException e) {

			if (e.getStatusCode() == 401) {

				String error = "Could not authenticate you.";
				String pref = "Error from Twitter:";

				showDeviceFound(pref + error);
			}

			Log.e(TAG, e.getLocalizedMessage(), e);

			return;
		}

		if (tweeted == null) {

			return;
		}

		Log.d(TAG, address + "(" + name + ")" + " found.");

		persistBluetoothDevice(address, name, id, null, true);
		_devices.add(address);

		sendMessage(address);
		showDeviceFound(tweeted);
	}

}
