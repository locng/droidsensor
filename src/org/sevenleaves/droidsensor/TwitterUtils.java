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

	/**
	 * @param device
	 * @param settings
	 * @return
	 * @throws TwitterException
	 */
	public static String tweetDeviceFound(RemoteBluetoothDevice device,
			DroidSensorSettings settings) throws TwitterException {

		String address = device.getAddress();

		String id = DroidSensorUtils
				.getTwitterId(settings.getApiUrl(), address);

		if (!settings.isAllBluetoothDevices() && id == null) {

			return null;
		}

		String template = settings.getUserTemplate();

		if (id == null) {

			id = device.getName();

			if (id == null) {

				return null;
			}

			id = id.replace("DCS", "***");
			id = id.replace("DF", "**");
			
			template = settings.getDeviceTemplate();
		}

		Twitter twitter = new Twitter(settings.getTwitterId(), settings
				.getTwitterPassword());
		String forNotify;

		String text = template.replace("$id", id);

		// apiの回数制限により、使わない。
		// if (template.contains("$name")) {
		//
		// User user = twitter.showUser(id);
		// String name = user.getName();
		// text = text.replace("$name", name);
		// }

		forNotify = text;

		if (template.contains("$tags")) {

			text = text.replace("$tags",
					(settings.getTags().startsWith(" ") ? "" : " ")
							+ settings.getTags());
			forNotify = forNotify.replace("$tags", "");
		}

		twitter.updateStatus(text);

		return forNotify;
	}

}
