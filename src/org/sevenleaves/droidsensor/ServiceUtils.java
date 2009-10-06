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

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public abstract class ServiceUtils {

	public static final String START_ACTION = "start";

	public static final String STOP_ACTION = "stop";

	public static final String INTERVAL_ACTION = "interval";

	public static boolean isActionMatches(Intent intent, String action) {

		String a = intent.getAction();
		boolean res = action.equals(a);

		return res;
	}

	public static boolean isStartAction(Intent intent) {

		return isStartService(intent) || isActionMatches(intent, START_ACTION);
	}

	public static boolean isStartService(Intent intent) {

		return isActionMatches(intent, IDroidSensorService.class.getName());
	}

	public static boolean isStopAction(Intent intent) {

		return isActionMatches(intent, STOP_ACTION);
	}

	public static boolean isIntervalAction(Intent intent) {

		return isActionMatches(intent, INTERVAL_ACTION);
	}

	public static boolean isActionContinue(Intent intent) {

		boolean res = isStartAction(intent);
		res |= isIntervalAction(intent);

		return res;
	}

	public static Intent createIntent(Class<?> clazz, String action) {

		String pkg = clazz.getPackage().getName();
		String name = clazz.getName();
		Intent intent = new Intent();
		intent.setClassName(pkg, name);
		intent.setAction(action);

		return intent;
	}

	public static Intent createStartAction(Class<?> service) {

		return createIntent(service, START_ACTION);
	}

	public static Intent createStopAction(Class<?> service) {

		return createIntent(service, STOP_ACTION);
	}

	public static Intent createIntervalAction(Class<?> service) {

		return createIntent(service, INTERVAL_ACTION);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getSystemService(Context context, String serviceName) {

		T res = (T) context.getSystemService(serviceName);

		return res;
	}

	public static AlarmManager getAlarmManager(Context context) {

		return getSystemService(context, Context.ALARM_SERVICE);
	}

	public static NotificationManager getNotificationManager(Context context) {

		return getSystemService(context, Context.NOTIFICATION_SERVICE);
	}

	public static long toMilliSeconds(long seconds) {

		long res = seconds * 1000L;

		return res;
	}

	public static void callLater(Service service, Class<?> type, long seconds) {

		AlarmManager alarm = getAlarmManager(service);
		Intent si = createIntervalAction(type);
		PendingIntent pi = PendingIntent.getService(service, 0, si, 0);
		long elapsedRealtime = SystemClock.elapsedRealtime();
		long ms = toMilliSeconds(seconds);
		long triggerTime = elapsedRealtime + ms;
		alarm.set(AlarmManager.ELAPSED_REALTIME, triggerTime, pi);
	}

	public static void cancelImmediatly(Service service, Class<?> type) {

		Intent si = createIntervalAction(type);
		PendingIntent pi = PendingIntent.getService(service, 0, si, 0);
		AlarmManager alarm = getAlarmManager(service);
		alarm.cancel(pi);
	}
}
