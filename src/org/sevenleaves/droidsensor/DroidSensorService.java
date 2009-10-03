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
import android.os.RemoteException;
import android.util.Log;

public class DroidSensorService extends Service implements
		BluetoothDeviceListener {

	private static final long INTERVAL_SECONDS = 60L;

	private BluetoothBroadcastReceiver _receiver = BluetoothBroadcastReceiver
			.getInstance();

	private static final BluetoothSettings SETTINGS = new BluetoothSettings();

	private volatile boolean _started;

	private static final Set<String> DEVICES = Collections
			.synchronizedSet(new LinkedHashSet<String>());

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

			if (ServiceUtils.isStartService(intent)) {

				_receiver.addListener(this);
				_receiver.registerSelf(this, SETTINGS);
				showNotification();
			}

			Log.d("DroidSensorService", "running");

			if (_started) {

				callLater(DroidSensorService.this, IDroidSensorService.class,
						INTERVAL_SECONDS);
			}
		}
	}

	@Override
	public void onDestroy() {

		Log.d("DroidSensorService", "service destroy");

		super.onDestroy();

		DEVICES.clear();

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

		DEVICES.remove(address);
		// showDeviceDisappeared(address);
	}

	private boolean isDiscoverable(BluetoothDeviceStub bluetooth) {

		boolean res = (bluetooth.getScanMode() == BluetoothDeviceStub.SCAN_MODE_CONNECTABLE_DISCOVERABLE);

		return res;
	}

	public void onRemoteNameUpdated(Context context,
			RemoteBluetoothDevice device) {

		String address = device.getAddress();

		if (DEVICES.contains(address)) {

			return;
		}

		onRemoteDeviceFound(context, device);
	}

	public void onRemoteDeviceFound(final Context context,
			final RemoteBluetoothDevice device) {

		new Thread() {
			@Override
			public void run() {

				String address = device.getAddress();

				if (DEVICES.contains(address)) {

					return;
				}

				BluetoothDeviceStub bluetooth = BluetoothDeviceStubFactory
						.createBluetoothServiceStub(DroidSensorService.this);

				// すれ違い通信という名目のため、自分も検出可能モードでなければ通知しないよ。
				if (!isDiscoverable(bluetooth)) {

					return;
				}

				DroidSensorSettings settings = DroidSensorSettings
						.getInstance(context);

				String tweeted;

				try {

					tweeted = TwitterUtils.tweetDeviceFound(device, settings);
				} catch (TwitterException e) {

					// _handler.post(new Runnable() {
					//
					// public void run() {
					//
					// Toast.makeText(DroidSensorService.this, "tweet failed",
					// Toast.LENGTH_SHORT).show();
					// }
					// });
					Log.e("DroidSensorService", e.getLocalizedMessage());
					return;
				}

				if (tweeted == null) {

					return;
				}

				Log.d("DroidSensorService", device.getAddress() + "("
						+ device.getName() + ")" + " found.");

				DEVICES.add(address);

				showDeviceFound(tweeted);
			}
		}.start();
	}

	public void onScanModeConnectable(Context context) {

	}

	public void onScanModeConnectableDiscoverable(Context context) {

	}

	public void onScanModeNone(Context context) {

	}

}
