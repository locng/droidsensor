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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.util.Log;

/**
 * @author esmasui@gmail.com
 * 
 */
abstract class DroidSensorUtils {

	private static final int RETRY_COUNT = 3;

	private static final long RETRY_INTERVAL = 3;

	public static String encodeString(String str) throws Exception {

		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] input = str.toUpperCase().getBytes("utf-8");
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

	public static APIResuponse getTwitterId(String apiUrl, String address,
			String user, String message) {

		Log.d("DroidSensorUtils", "address=" + address + ",user=" + user
				+ ",message=" + message);

		APIResuponse res = null;

		for (int i = 0; i < RETRY_COUNT; ++i) {

			try {

				res = getTwitterIdInternal(apiUrl, address, user, message);

				break;
			} catch (Exception e) {

				Log.d("DroidSensorUtils", "retry to get twitter id");

				try {

					Thread.sleep(RETRY_INTERVAL * 1000L);
				} catch (InterruptedException ie) {
					// nop.
				}
			}
		}

		return res;
	}

	public static APIResuponse getTwitterIdInternal(String apiUrl,
			String address, String user, String message) {

		HttpClient client = new DefaultHttpClient();

		// String baseUrl = getString(R.string.property_server_url);
		String encoded;

		try {

			encoded = DroidSensorUtils.encodeString(address);
		} catch (Exception e) {

			return new APIResuponse();
		}

		HttpPost request = new HttpPost(apiUrl);

		try {
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("a", encoded));
			nameValuePairs.add(new BasicNameValuePair("u", "@" + user));
			if (message != null) {
				nameValuePairs.add(new BasicNameValuePair("m", message));
			}
			nameValuePairs.add(new BasicNameValuePair("t", "json"));
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
			request.setHeader("User-Agent", client.getClass().getSimpleName());

			HttpResponse response = client.execute(request);
			StatusLine status = response.getStatusLine();

			if (status.getStatusCode() != 200 && status.getStatusCode() != 404) {

				return new APIResuponse();
				// throw new RuntimeException("HTTP_STATUS_CODE is "
				// + status.getStatusCode());
			}

			HttpEntity entity = response.getEntity();
			InputStream inputStream = entity.getContent();

			byte[] buf = new byte[1024];
			ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
			int len = -1;

			for (; (len = inputStream.read(buf)) != -1;) {

				baos.write(buf, 0, len);
			}

			JSONObject json = new JSONObject(new String(baos.toByteArray(),
					"utf-8"));
			String resName = json.getString("twitterUser");
			int resCount = json.getInt("count");
			String resMessage = json.getString("message");

			if (resName == null || resName.trim().length() == 0) {

				return new APIResuponse();
			}

			if (status.getStatusCode() == 404) {

				resName = "@" + resName;
			}

			APIResuponse res = new APIResuponse();
			res.setTwitterUser(resName);
			res.setCount(resCount);
			res.setMessage(resMessage);

			return res;
		} catch (Exception e) {

			request.abort();

			return new APIResuponse();
			// throw new RuntimeException(e);
		}

	}

	public static boolean putTwitterId(String apiUrl, String address,
			String id, String message) {

		Log.d("DroidSensorUtils", "address=" + address + ",id=" + id
				+ ",message=" + message);

		boolean res = false;

		for (int i = 0; i < RETRY_COUNT; ++i) {

			try {

				putTwitterIdInternal(apiUrl, address, id, message);
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

	public static boolean putTwitterIdInternal(String apiUrl, String address,
			String id, String message) {

		HttpClient client = new DefaultHttpClient();

		// String baseUrl = getString(R.string.property_server_url);
		String encoded;

		try {

			encoded = DroidSensorUtils.encodeString(address);
		} catch (Exception e) {

			return false;
		}

		HttpPost request = new HttpPost(apiUrl);

		try {

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("a", encoded));
			nameValuePairs.add(new BasicNameValuePair("u", id));
			if (message != null) {

				nameValuePairs.add(new BasicNameValuePair("m", message));
			}
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
			request.setHeader("User-Agent", client.getClass().getSimpleName());
			HttpResponse response = client.execute(request);
			StatusLine status = response.getStatusLine();

			if (status.getStatusCode() != 200) {

				// if (status.getStatusCode() == 404) {
				//
				// return;
				// }

				return false;
			}

			return true;
		} catch (Exception e) {

			request.abort();

			return false;
		}

	}

	private static final String encodeUri(String s) {

		try {

			return URLEncoder.encode(s, "utf-8");
		} catch (UnsupportedEncodingException e) {

			return s;
		}
	}

}
