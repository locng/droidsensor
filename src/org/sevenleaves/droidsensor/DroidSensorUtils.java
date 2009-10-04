package org.sevenleaves.droidsensor;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

abstract class DroidSensorUtils {

	private static final int RETRY_COUNT = 3;

	private static final long RETRY_INTERVAL = 5;

	public static String encodeString(String str) throws Exception {

		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] input = str.getBytes("utf-8");
		md.update(input);
		byte[] digest = md.digest();

		StringBuilder buf = new StringBuilder();

		for (int i = 0; i < digest.length; ++i) {

			int d = digest[i] & 0xff;
			String h = Integer.toHexString(d);

			if (h.length() == 1) {

				buf.append('0');
			}

			buf.append(h);
		}

		String res = buf.toString();

		return res;
	}

	public static String getTwitterId(String apiUrl, String address) {

		String res = null;

		//for (int i = 0; i < RETRY_COUNT; ++i) {
		for (int i = 0; i < 1; ++i) {

			try {

				res = getTwitterIdInternal(apiUrl, address);

				break;
			} catch (Exception e) {

				// Log.d("DroidSensorUtils", "retry to get twitter id");
				//
				// try {
				//
				// Thread.sleep(RETRY_INTERVAL * 1000L);
				// } catch (InterruptedException ie) {
				// // nop.
				// }
			}
		}

		return res;
	}

	public static boolean putTwitterId(String apiUrl, String address, String id) {

		boolean res = false;

		for (int i = 0; i < RETRY_COUNT; ++i) {

			try {

				putTwitterIdInternal(apiUrl, address, id);

				res = true;

				break;
			} catch (Exception e) {

				Log.d("DroidSensorUtils", "retry to put twitter id");

				try {

					Thread.sleep(RETRY_INTERVAL * 1000L);
				} catch (InterruptedException ie) {
					// nop.
				}
			}
		}

		return res;
	}

	public static void putTwitterIdInternal(String apiUrl, String address,
			String id) {

		HttpClient client = new DefaultHttpClient();

		// String baseUrl = getString(R.string.property_server_url);
		String encoded;

		try {

			encoded = DroidSensorUtils.encodeString(address);
		} catch (Exception e) {

			throw new RuntimeException(e);
		}

		HttpGet request = new HttpGet(apiUrl + "?a=" + encoded + "&u=" + id);

		request.setHeader("User-Agent", client.getClass().getSimpleName());

		try {
			HttpResponse response = client.execute(request);
			StatusLine status = response.getStatusLine();

			if (status.getStatusCode() != 200) {

				// if (status.getStatusCode() == 404) {
				//
				// return;
				// }

				throw new RuntimeException("HTTP_STATUS_CODE is "
						+ status.getStatusCode());
			}

			return;
		} catch (Exception e) {

			request.abort();

			throw new RuntimeException(e);
		}

	}

	public static String getTwitterIdInternal(String apiUrl, String address) {

		HttpClient client = new DefaultHttpClient();

		// String baseUrl = getString(R.string.property_server_url);
		String encoded;

		try {

			encoded = DroidSensorUtils.encodeString(address);
		} catch (Exception e) {

			throw new RuntimeException(e);
		}

		HttpGet request = new HttpGet(apiUrl + "?a=" + encoded);

		request.setHeader("User-Agent", client.getClass().getSimpleName());

		try {
			HttpResponse response = client.execute(request);
			StatusLine status = response.getStatusLine();

			if (status.getStatusCode() != 200) {

				if (status.getStatusCode() == 404) {

					return null;
				}

				throw new RuntimeException("HTTP_STATUS_CODE is "
						+ status.getStatusCode());
			}

			HttpEntity entity = response.getEntity();
			InputStream inputStream = entity.getContent();

			byte[] buf = new byte[1024];
			ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
			int len = -1;

			for (; (len = inputStream.read(buf)) != -1;) {

				baos.write(buf, 0, len);
			}

			String name = new String(baos.toByteArray(), "utf-8");

			return name;
		} catch (Exception e) {

			request.abort();

			throw new RuntimeException(e);
		}

	}

}
