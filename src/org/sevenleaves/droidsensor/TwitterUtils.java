package org.sevenleaves.droidsensor;

import it.gerdavax.android.bluetooth.RemoteBluetoothDevice;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

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
			String template, String templateOther, boolean allDevices,
			String tags) {

		String address = device.getAddress();

		String id = DroidSensorUtils.getTwitterId(apiUrl, address);

		if (!allDevices && id == null) {

			return null;
		}

		String fixedTemplate = template;

		if (id == null) {

			id = device.getName();
			fixedTemplate = templateOther;
		}

		Twitter twitter = new Twitter(twitterId, twitterPassword);

		try {

			String text = fixedTemplate.replace("$id", id);

			if (template.contains("$name")) {

				User user = twitter.showUser(id);
				String name = user.getName();
				text = text.replace("$name", name);
			}

			if (template.contains("$tags")) {

				text = text.replace("$tags", (tags.startsWith(" ") ? "" : " ")
						+ tags);
			}

			// Status status = twitter.updateStatus(text);
			twitter.updateStatus(text);
			// Toast.
			return text;

		} catch (TwitterException e) {

			throw new RuntimeException(e);
		}

	}

}
