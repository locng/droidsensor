package org.sevenleaves.droidsensor;

import static org.sevenleaves.droidsensor.ServiceUtils.callLater;
import static org.sevenleaves.droidsensor.ServiceUtils.cancelImmediatly;
import static org.sevenleaves.droidsensor.ServiceUtils.isActionContinue;
import static org.sevenleaves.droidsensor.ServiceUtils.isStopAction;
import static org.sevenleaves.droidsensor.ServiceUtils.sleep;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.sevenleaves.droidsensor.bluetooth.BluetoothBroadcastReceiver;
import org.sevenleaves.droidsensor.bluetooth.BluetoothDeviceListener;
import org.sevenleaves.droidsensor.bluetooth.BluetoothServiceStub;
import org.sevenleaves.droidsensor.bluetooth.BluetoothServiceStubFactory;
import org.sevenleaves.droidsensor.bluetooth.BluetoothSettings;
import org.sevenleaves.droidsensor.bluetooth.RemoteBluetoothDevice;

import twitter4j.TwitterException;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class DroidSensorService extends Service implements
		BluetoothDeviceListener {

	private static final long INTERVAL_SECONDS = 30L;

	private static final long SLEEP_SECONDS = 30L;

	private static final BluetoothSettings SETTINGS = new BluetoothSettings();

	private volatile boolean _started;

	private static final Set<String> DEVICES = Collections
			.synchronizedSet(new LinkedHashSet<String>());

	private Handler _handler = new Handler();

	private final IDroidSensorService.Stub _binder = new IDroidSensorService.Stub() {

		public boolean isStarted() throws RemoteException {

			return _started;
		}

		public void stopService() throws RemoteException {

			DroidSensorService.this.stopService();
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

			BluetoothBroadcastReceiver receiver = BluetoothBroadcastReceiver
					.getInstance();
			receiver.unregisterSelf(this, SETTINGS);
		} catch (Exception e) {
			// nop.
		}

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

			BluetoothBroadcastReceiver receiver = BluetoothBroadcastReceiver
					.getInstance();
			receiver.registerSelf(this, SETTINGS);

			if (ServiceUtils.isStartService(intent)) {

				showNotification();
			}

			receiver.addListener(this);

			sleep(SLEEP_SECONDS, new Runnable() {

				public void run() {

					Log.d("DroidSensorService", "running");

					if (_started) {

						callLater(DroidSensorService.this,
								IDroidSensorService.class, INTERVAL_SECONDS);
					}

					// stopSelf();
				}
			});
		}
	}

	@Override
	public void onDestroy() {

		Log.d("DroidSensorService", "service destroy");

		super.onDestroy();

		DEVICES.clear();

		try {

			BluetoothBroadcastReceiver receiver = BluetoothBroadcastReceiver
					.getInstance();
			receiver.unregisterSelf(this, SETTINGS);
		} catch (Exception e) {
			// nop.
		}

		hideNotification();
	}

	public void onDisabled(Context context) {

	}

	public void onEnabled(Context context) {

	}

	public void onRemoteDeviceDisappeared(Context context, String address) {

		DEVICES.remove(address);
		// showDeviceDisappeared(address);
	}

	private boolean isDiscoverable(BluetoothServiceStub bluetooth) {

		boolean res = (bluetooth.getScanMode() == BluetoothServiceStub.SCAN_MODE_CONNECTABLE_DISCOVERABLE);

		return res;
	}

	public void onRemoteDeviceFound(Context context,
			RemoteBluetoothDevice device) {

		String address = device.getAddress();

		if (DEVICES.contains(address)) {

			return;
		}

		DEVICES.add(address);

		BluetoothServiceStub bluetooth = BluetoothServiceStubFactory
				.createBluetoothServiceStub(this);

		// すれ違い通信という名目のため、自分も検出可能モードでなければ通知しないよ。
		if (!isDiscoverable(bluetooth)) {

			return;
		}

		Log.d("DroidSensorService", device.getAddress() + " found.");

		DroidSensorSettings s = DroidSensorSettings.getInstance(context);

		String tweeted;

		try {

			tweeted = TwitterUtils.tweetDeviceFound(device, s.getTwitterId(), s
					.getTwitterPassword(), s.getApiUrl(), s.getUserTemplate(),
					s.getDeviceTemplate(), s.isAllBluetoothDevices(),
					"#droidsensor");
		} catch (TwitterException e) {

			// _handler.post(new Runnable() {
			//
			// public void run() {
			//
			// Toast.makeText(DroidSensorService.this, "tweet failed",
			// Toast.LENGTH_SHORT).show();
			// }
			// });

			return;
		}

		if (tweeted == null) {

			return;
		}

		showDeviceFound(tweeted);
	}

	public void onScanModeConnectable(Context context) {

	}

	public void onScanModeConnectableDiscoverable(Context context) {

	}

	public void onScanModeNone(Context context) {

	}

}
