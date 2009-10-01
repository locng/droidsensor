package org.sevenleaves.droidsensor;

import org.sevenleaves.droidsensor.bluetooth.RemoteBluetoothDevice;

import twitter4j.Twitter;
import twitter4j.TwitterException;

abstract class TwitterUtils {

	private static boolean isEmpty(String s) {

		if (s == null) {

			return true;
		}

		if (s.trim().length() == 0) {

			return true;
		}

		return false;
	}

	public static boolean verifyCredentials(String twitterId,
			String twitterPassword) {

		if (isEmpty(twitterId) || isEmpty(twitterPassword)) {

			return false;
		}

		Twitter twitter = new Twitter(twitterId, twitterPassword);

		try {

			twitter.verifyCredentials();

			return true;

		} catch (TwitterException e) {

			return false;
		}
	}

	public static String tweetDeviceFound(RemoteBluetoothDevice device,
			String twitterId, String twitterPassword, String apiUrl,
			String userTemplate, String deviceTemplate, boolean allDevices,
			String tags) {

		String address = device.getAddress();

		String id = DroidSensorUtils.getTwitterId(apiUrl, address);

		if (!allDevices && id == null) {

			return null;
		}

		String template = userTemplate;

		if (id == null) {

			id = device.getName();

			if (id == null) {

				id = "UNKNOWN";
			}

			template = deviceTemplate;
		}

		Twitter twitter = new Twitter(twitterId, twitterPassword);
		String forNotify;

		try {

			String text = template.replace("$id", id);

			// apiの回数制限により、使わない。
			// if (template.contains("$name")) {
			//
			// User user = twitter.showUser(id);
			// String name = user.getName();
			// text = text.replace("$name", name);
			// }

			forNotify = text;

			if (userTemplate.contains("$tags")) {

				text = text.replace("$tags", (tags.startsWith(" ") ? "" : " ")
						+ tags);
				forNotify = forNotify.replace("$tags", "");
			}

			twitter.updateStatus(text);

			return forNotify;

		} catch (TwitterException e) {

			throw new RuntimeException(e);
		}

	}

}
