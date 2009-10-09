package org.sevenleaves.droidsensor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;

public abstract class DroidSensorServiceBase extends Service {

	protected void showNotification() {

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

	protected void showDeviceFound(String tweeted) {

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

	protected void showDeviceDisappeared(String address) {

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

	protected void hideNotification() {

		NotificationManager notificationManager = ServiceUtils
				.getNotificationManager(this);
		notificationManager.cancel(R.string.service_name);
	}

}
