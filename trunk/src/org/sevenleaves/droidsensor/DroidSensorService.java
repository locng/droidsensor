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
import java.util.LinkedHashSet;
import java.util.Set;

import org.sevenleaves.droidsensor.bluetooth.BluetoothBroadcastReceiver;
import org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceListener;
import org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStub;
import org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceStubFactory;
import org.sevenleaves.droidsensor.bluetooth.BluetoothSettings;
import org.sevenleaves.droidsensor.bluetooth.RemoteBluetoothDevice;

import twitter4j.TwitterException;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

public class DroidSensorService extends Service implements
		BluetoothDeviceListener {

	private static final long INTERVAL_SECONDS = 120L;

	private BluetoothBroadcastReceiver _receiver;

	private static final BluetoothSettings SETTINGS = new BluetoothSettings();

	private volatile boolean _started;

	private final RemoteCallbackList<IDroidSensorCallbackListener> _listeners = new RemoteCallbackList<IDroidSensorCallbackListener>();

	private final Set<String> _devices = Collections
			.synchronizedSet(new LinkedHashSet<String>());

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

	private void showNotification() {

		Notification notification = new Notification(R.drawable.notify,
				getString(R.string.app_name), System.currentTimeMillis());
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		// notification.flags = Notification.FLAG_AUTO_CANCEL;
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, DroidSensorActivity.class), 0);
		notification.setLatestEventInfo(this, getString(R.string.app_name),
				"Service started", contentIntent);
		NotificationManager notificationManager = ServiceUtils
				.getNotificationManager(this);
		notificationManager.notify(R.string.service_name, notification);
	}

	public void showDeviceFound(String tweeted) {

		Notification notification = new Notification(R.drawable.notify,
				tweeted, System.currentTimeMillis());
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		// notification.flags = Notification.FLAG_AUTO_CANCEL;
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, DroidSensorActivity.class), 0);
		notification.setLatestEventInfo(this, getString(R.string.app_name),
				tweeted, contentIntent);
		NotificationManager notificationManager = ServiceUtils
				.getNotificationManager(this);
		notificationManager.notify(R.string.service_name, notification);
	}

	public void showDeviceDisappeared(String address) {

		String message = address + " was disappeared.";

		Notification notification = new Notification(R.drawable.notify,
				message, System.currentTimeMillis());
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		// notification.flags = Notification.FLAG_AUTO_CANCEL;
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, DroidSensorActivity.class), 0);
		notification.setLatestEventInfo(this, getString(R.string.app_name),
				message, contentIntent);
		NotificationManager notificationManager = ServiceUtils
				.getNotificationManager(this);
		notificationManager.notify(R.string.service_name, notification);
	}

	private void hideNotification() {

		NotificationManager notificationManager = ServiceUtils
				.getNotificationManager(this);
		notificationManager.cancel(R.string.service_name);
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

			_receiver.unregisterSelf(this, SETTINGS);
		} catch (Exception e) {
			// nop.
		}

		_receiver = null;
		hideNotification();

		_started = false;
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

				if (_receiver == null) {

					showNotification();
					_receiver = BluetoothBroadcastReceiver.getInstance();
					_receiver.addListener(this);
					_receiver.registerSelf(this, SETTINGS);
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

		_devices.clear();

		try {

			_receiver.unregisterSelf(this, SETTINGS);
		} catch (Exception e) {
			// nop.
		}

		hideNotification();
	}

	public void onDisabled(Context context) {

	}

	public void onEnabled(Context context) {

		BluetoothDeviceStub stub = BluetoothDeviceStubFactory
				.createBluetoothServiceStub(this);
		DroidSensorSettings settings = DroidSensorSettings.getInstance(this);
		String address = stub.getAddress();
		DroidSensorUtils.putTwitterId(settings.getApiUrl(), address, settings
				.getTwitterId());
	}

	public void onRemoteDeviceDisappeared(Context context, String address) {

		// TODO ここではフラグをたてるだけで、一定時間たったら消すような処理にすること。
		_devices.remove(address);
	}

	private boolean isDiscoverable(BluetoothDeviceStub bluetooth) {

		boolean res = (bluetooth.getScanMode() == BluetoothDeviceStub.SCAN_MODE_CONNECTABLE_DISCOVERABLE);

		return res;
	}

	public void onRemoteNameUpdated(Context context,
			RemoteBluetoothDevice device) {

		String address = device.getAddress();

		if (_devices.contains(address)) {

			return;
		}

		onRemoteDeviceFound(context, device);
	}

	public void onRemoteDeviceFound(final Context context,
			final RemoteBluetoothDevice device) {

		String address = device.getAddress();

		if (_devices.contains(address)) {

			return;
		}

		BluetoothDeviceStub bluetooth = BluetoothDeviceStubFactory
				.createBluetoothServiceStub(DroidSensorService.this);

		// すれ違い通信という名目のため、自分も検出可能モードでなければ通知しないよ。
		if (!isDiscoverable(bluetooth)) {

			return;
		}

		DroidSensorSettings settings = DroidSensorSettings.getInstance(context);

		String id = DroidSensorUtils
				.getTwitterId(settings.getApiUrl(), address);

		if (!settings.isAllBluetoothDevices() && id == null) {

			_devices.add(address);

			return;
		}

		String deviceName = device.getName();

		if (id == null && deviceName == null) {

			return;
		}

		String tweeted;

		try {

			tweeted = TwitterUtils.tweetDeviceFound(device, id, settings);
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

		Log.d("DroidSensorService", device.getAddress() + "("
				+ device.getName() + ")" + " found.");

		_devices.add(address);

		showDeviceFound(tweeted);
	}

	public void onScanModeConnectable(Context context) {

	}

	public void onScanModeConnectableDiscoverable(Context context) {

	}

	public void onScanModeNone(Context context) {

	}

}
