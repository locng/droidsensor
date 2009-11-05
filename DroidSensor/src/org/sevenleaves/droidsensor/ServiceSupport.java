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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;

/**
 * @author esmasui@gmail.com
 * 
 */
public abstract class ServiceSupport extends Service {

	protected void hideNotification() {

		NotificationManager notificationManager = ServiceUtils
				.getNotificationManager(this);
		notificationManager.cancel(R.string.service_name);
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

	protected void setVibrationIfNeccesary(SettingsManager settings,
			Notification notification) {

		if (!settings.isNotificationVibration()) {

			return;
		}

		notification.defaults |= Notification.DEFAULT_VIBRATE;
		// notification.vibrate = new long[] { 0, 200, 100, 200 };
	}

	protected void setLEDFlashIfNeccesary(SettingsManager settings,
			Notification notification) {

		if (!settings.isNotificationLedFlash()) {

			return;
		}

		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.ledARGB = 0xff0000FF;
		notification.ledOnMS = 300;
		notification.ledOffMS = 1000;
	}

	protected void setRingToneIfNeccesary(SettingsManager settings,
			Notification notification) {

		if (settings.getNotificationRingtone() == null) {

			return;
		}

		// notification.defaults |= Notification.DEFAULT_SOUND;
		notification.sound = settings.getNotificationRingtone();
	}

	protected void showDeviceFound(String tweeted) {

		SettingsManager settings = SettingsManager.getInstance(this);
		Notification notification = new Notification(R.drawable.notify,
				tweeted, System.currentTimeMillis());
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		setVibrationIfNeccesary(settings, notification);
		setLEDFlashIfNeccesary(settings, notification);
		setRingToneIfNeccesary(settings, notification);
		// notification.flags = Notification.FLAG_AUTO_CANCEL;
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, DroidSensorActivity.class), 0);
		notification.setLatestEventInfo(this, getString(R.string.app_name),
				tweeted, contentIntent);
		NotificationManager notificationManager = ServiceUtils
				.getNotificationManager(this);
		notificationManager.notify(R.string.service_name, notification);
	}

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

}
