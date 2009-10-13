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

import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * @author esmasui@gmail.com
 * 
 */
abstract class TwitterUtils {

	/**
	 * @param device
	 * @param settings
	 * @return
	 * @throws TwitterException
	 */
	public static String tweetDeviceFound(String address, String name,
			String id, SettingsManager settings) throws TwitterException {

		String template = settings.getUserTemplate();

		String target = id;
		boolean isUser = true;

		if (target == null) {

			target = name;
			template = settings.getDeviceTemplate();
			isUser = false;
		}

		if (target != null && target.startsWith("@")) {

			target = target.substring(1);
			template = settings.getPassedDeviceTemplate();

			if (target.equals(settings.getTwitterId())) {

				template = settings.getPassedDeviceAgainTemplate();
			}

			isUser = false;
		}

		String forNotify;

		String text = template.replace("$id", target);

		// apiの回数制限により、使わない。
		// if (template.contains("$name")) {
		//
		// User user = twitter.showUser(id);
		// String name = user.getName();
		// text = text.replace("$name", name);
		// }

		if (text.contains("$device")) {

			if (name == null) {

				return null;
			}

			text = text.replace("$device", name);
		}

		forNotify = text;

		if (text.contains("$tags")) {

			text = text.replace("$tags",
					(settings.getTags().startsWith(" ") ? "" : " ")
							+ settings.getTags());
			forNotify = forNotify.replace("$tags", "");
		}

		List<Twitter> twitters = createTwitters(settings, isUser);

		for (int i = 0; i < twitters.size(); ++i) {

			Twitter t = twitters.get(i);
			t.updateStatus(text);
		}

		return forNotify;
	}

	public static boolean verifyCredentials(String twitterId,
			String twitterPassword) {

		if (isEmpty(twitterId) || isEmpty(twitterPassword)) {

			return false;
		}

		//return true;
		Twitter twitter = createTwitter(twitterId, twitterPassword);

		try {

			twitter.verifyCredentials();

			return true;

		} catch (TwitterException e) {

			return false;
		}
	}

	private static List<Twitter> createDispatchTwitters(
			SettingsManager settings, int dispatch) {

		Twitter t;
		List<Twitter> accounts = new ArrayList<Twitter>();

		switch (dispatch) {
		case 1:

			t = createTwitterFromMainAccount(settings);
			accounts.add(t);

			break;
		case 2:

			t = createTwitterFromOptionalAccount(settings);
			accounts.add(t);

			break;
		default:

			t = createTwitterFromMainAccount(settings);
			accounts.add(t);
			t = createTwitterFromOptionalAccount(settings);
			accounts.add(t);

			break;
		}

		return accounts;
	}

	private static Twitter createTwitter(String u, String p) {

		Twitter t = new Twitter(u, p);

		return t;
	}

	private static Twitter createTwitterFromMainAccount(SettingsManager settings) {

		String u = settings.getTwitterId();
		String p = settings.getTwitterPassword();
		Twitter t = createTwitter(u, p);

		return t;
	}

	private static Twitter createTwitterFromOptionalAccount(
			SettingsManager settings) {

		String u = settings.getOptionalTwitterId();
		String p = settings.getOptionalTwitterPassword();
		Twitter t = createTwitter(u, p);

		return t;
	}

	private static List<Twitter> createTwitters(SettingsManager settings,
			boolean isUser) {

		List<Twitter> accounts;

		if (isUser) {

			int d = settings.getDispatchUser();
			accounts = createDispatchTwitters(settings, d);

			return accounts;
		}

		int d = settings.getDispatchDevice();
		accounts = createDispatchTwitters(settings, d);

		return accounts;
	}

	private static boolean isEmpty(String s) {

		if (s == null) {

			return true;
		}

		if (s.trim().length() == 0) {

			return true;
		}

		return false;
	}

}
