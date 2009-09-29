package org.sevenleaves.droidsensor;

import it.gerdavax.android.bluetooth.RemoteBluetoothDevice;

import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;

public class DroidSensorService extends Service {

	private final IDroidSensorService.Stub _binder = new IDroidSensorService.Stub() {

		public boolean isStarted() throws RemoteException {

			return _started;
		}

		public boolean stopScanning() throws RemoteException {

			_started = false;
			DroidSensorService.this.stopSelf();

			return true;
		}
	};

	private LocalBluetoothDeviceListener _listener = new LocalBluetoothDeviceListener() {

		public void scanStarted() {
		}

		public void scanCompleted(ArrayList<String> devices) {
		}

		public void remoteDeviceUpdate(RemoteBluetoothDevice device) {
		}

		public void remoteDeviceFound(RemoteBluetoothDevice device) {

			try {

				if (!_bluetooth.isEnabled()) {

					return;
				}

				// すれ違い通信という名目のため、自分も検出可能モードでなければ通知しないよ。
				if (_bluetooth.getScanMode() != 0x3) {

					return;
				}

			} catch (Exception e) {

				showError(e.getLocalizedMessage());

				return;
			}

			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(DroidSensorService.this);
			String twitterId = preferences.getString("droidsensor_twitter_id",
					"");
			boolean allDevices = preferences.getBoolean(
					"droidsensor_discovery_all_bluetooth_devices", false);

			String twitterPassword = preferences.getString(
					"droidsensor_twitter_password", "");

			// boolean vibration =
			// preferences.getBoolean("droidsensor_vibration",
			// false);
			// boolean ledFlash =
			// preferences.getBoolean("droidsensor_led_flash",
			// false);
			String url = getString(R.string.property_server_url);
			String template = getString(R.string.template_status);

			String templateOther = getString(R.string.template_status_other);

			String tweeted = TwitterUtils.tweetDeviceFound(device, twitterId,
					twitterPassword, url, template, templateOther, allDevices,
					"#droidsensor");

			if (tweeted == null) {

				return;
			}

			showMessageToStatusBar(tweeted);
		}

		public void remoteDeviceDisappeared(RemoteBluetoothDevice remoteDevice) {
		}

		public void enabled() {

			try {

				if (_bluetooth.isScanning()) {

					_bluetooth.stopScanning();
				}

				if (_bluetooth.isPeriodicScanning()) {

					_bluetooth.stopPeriodicScanning();
				}

				_bluetooth.setScanMode(0x3);
				_bluetooth.setDiscoverableTimeout(0);
				_bluetooth.periodicScan();
				registerTwitterId();
			} catch (Exception e) {

				showError(e.getMessage());
			}
		}

		public void disabled() {
		}
	};

	private void registerTwitterId() {

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(DroidSensorService.this);
		String twitterId = preferences.getString("droidsensor_twitter_id", "");

		putTwitterId(twitterId);
	}

	private void putTwitterId(String twitterId) {

		String url = getString(R.string.property_server_url);
		String address;

		try {

			address = _bluetooth.getAddress();
		} catch (Exception e) {

			showError(e.getLocalizedMessage());

			return;
		}

		DroidSensorUtils.putTwitterId(url, address, twitterId);

	}

	private LocalBluetoothDeviceWrapper _bluetooth;

	private boolean _started;

	private boolean _previousEnabled;

	private int _previousScanMode;

	private int _previousDiscoverableTimeout;

	private NotificationManager _notificationManager;

	@Override
	public IBinder onBind(Intent intent) {

		return _binder;
	}

	@Override
	public void onCreate() {

		super.onCreate();
		_notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	public void onStart(Intent intent, int startId) {

		if (_started) {

			return;
		}

		startStatus();
		_started = true;

		super.onStart(intent, startId);

		initBluetooth();
		savePreviousSettings();
		updateSettingsForDiscoverable();
	}

	private void startStatus() {

		Notification notification = new Notification(R.drawable.notify,
				getString(R.string.app_name), System.currentTimeMillis());
		// notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, DroidSensorActivity.class), 0);
		notification.setLatestEventInfo(this, getString(R.string.app_name),
				"service start", contentIntent);
		_notificationManager.notify(R.string.service_name, notification);
	}

	private void showMessageToStatusBar(String tweeted) {

		Notification notification = new Notification(R.drawable.notify,
				tweeted, System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(DroidSensorService.this, DroidSensorActivity.class),
				0);
		notification.setLatestEventInfo(DroidSensorService.this, tweeted,
				tweeted, contentIntent);
		_notificationManager.notify(R.string.app_name, notification);
	}

	private void stopStatus() {

		Notification notification = new Notification(R.drawable.notify,
				getString(R.string.app_name), System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, DroidSensorActivity.class), 0);
		notification.setLatestEventInfo(this, getString(R.string.app_name),
				"service stop", contentIntent);
		_notificationManager.notify(R.string.app_name, notification);
	}

	private void savePreviousSettings() {

		try {

			_previousEnabled = _bluetooth.isEnabled();
			_previousScanMode = _bluetooth.getScanMode();
			_previousDiscoverableTimeout = _bluetooth.getDiscoverableTimeout();

		} catch (Exception e) {

			showError(e.getLocalizedMessage());
		}
	}

	private void loadPreviousSettings() {

		try {

			_bluetooth.stopPeriodicScanning();
			_bluetooth.setScanMode(_previousScanMode);
			_bluetooth.setDiscoverableTimeout(_previousDiscoverableTimeout);
			_bluetooth.setEnabled(_previousEnabled);
			_bluetooth.close(DroidSensorService.this);
		} catch (Exception e) {

			showError(e.getLocalizedMessage());
		}
	}

	private void updateSettingsForDiscoverable() {

		try {

			if (!_bluetooth.isEnabled()) {

				_bluetooth.setEnabled(true);

				return;
			}

			_listener.enabled();

		} catch (Exception e) {

			showError(e.getLocalizedMessage());
		}
	}

	private void showError(String message) {

		// Toast.makeText(DroidSensorService.this, message, Toast.LENGTH_LONG)
		// .show();
	}

	private void initBluetooth() {

		try {

			_bluetooth = LocalBluetoothDeviceWrapper
					.createInstance(DroidSensorService.this);
		} catch (Exception e) {

			showError(e.getLocalizedMessage());

			return;
		}

		_bluetooth.setListener(_listener);
	}

	@Override
	public void onDestroy() {

		// putTwitterId("");
		// stopStatus();
		_notificationManager.cancel(R.string.service_name);
		_notificationManager.cancel(R.string.app_name);

		_started = false;
		loadPreviousSettings();

		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {

		return super.onUnbind(intent);
	}

}
