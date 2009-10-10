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

import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler.Callback;

/**
 * @author esmasui@gmail.com
 *
 */
public class DroidSensorInquiry {

	public static final String BLUETOOTH_ADDRESS = "BLUETOOTH_ADDRESS";

	public static final String TWITTER_USER = "TWITTER_USER";

	private static final int DEFAULT_MAX_THREADS = 3;

	private static final class Inquiry {

		private String _address;

		private String _user;

		public Inquiry(String address, String user) {

			_address = address;
			_user = user;
		}

		public String getAddress() {

			return _address;
		}

		public String getUser() {

			return _user;
		}

		@Override
		public boolean equals(Object o) {

			if (o == this) {

				return true;
			}

			if (!(o instanceof Inquiry)) {

				return false;
			}

			Inquiry co = (Inquiry) o;
			boolean res = getAddress().equals(co.getAddress());
			res &= getUser().equals(co.getUser());

			return res;
		}

		@Override
		public int hashCode() {

			int res = getAddress().hashCode();
			res += getUser().hashCode();

			return res;
		}
	}

	private final class RequestWorker implements Runnable {

		private String _apiUrl;

		private Inquiry _inquiry;

		private volatile Callback _callback;

		private CancellableThread _owner;

		public RequestWorker(String apiUrl, Inquiry inquiry, Callback callback) {

			_apiUrl = apiUrl;
			_inquiry = inquiry;
			_callback = callback;
		}

		public void setOwner(CancellableThread owner) {

			_owner = owner;
		}

		public void run() {

			String result = DroidSensorUtils.getTwitterId(_apiUrl, _inquiry
					.getAddress(), _inquiry.getUser());
			Message msg = createMessage(_inquiry, result);

			if (_callback != null) {

				_callback.handleMessage(msg);
			}

			_running.remove(_owner);
			_inquiries.remove(_inquiry);
			pollAndStart();
		}

		public void cancel() {

			_callback = null;
		}
	}

	private final class CancellableThread extends Thread {

		private RequestWorker _worker;

		public CancellableThread(RequestWorker worker) {

			super(worker);
			_worker = worker;
		}

		public void cancel() {

			_worker.cancel();
		}
	}

	private Set<Inquiry> _inquiries;

	private Set<CancellableThread> _running;

	private Context _context;

	private Queue<CancellableThread> _queue;

	private int _maxThreas = DEFAULT_MAX_THREADS;

	public DroidSensorInquiry(Context context) {

		_context = context;
		initSelf();
	}

	public DroidSensorInquiry(Context context, int maxThreads) {

		_context = context;
		_maxThreas = maxThreads;
		initSelf();
	}

	public synchronized void getTwitterUser(String address, String user,
			Callback callback) {

		String apiUrl = getApiUrl();
		Inquiry inquiry = new Inquiry(address, user);
		boolean success = registerInquiry(inquiry);

		if (!success) {

			return;
		}

		RequestWorker worker = new RequestWorker(apiUrl, inquiry, callback);
		startSequentially(worker);
	}

	public void cancelAll() {

		for (CancellableThread e : _running) {

			e.cancel();
		}

		_queue.clear();
		_running.clear();
		_inquiries.clear();
	}

	@Override
	protected void finalize() throws Throwable {

		super.finalize();
		cancelAll();
	}

	private void startSequentially(RequestWorker worker) {

		CancellableThread th = new CancellableThread(worker);
		worker.setOwner(th);
		_queue.add(th);

		int threads = _running.size();
		if (threads <= _maxThreas) {

			pollAndStart();
		}
	}

	private void pollAndStart() {

		CancellableThread th = _queue.poll();

		if (th == null) {

			return;
		}

		_running.add(th);
		th.start();
	}

	private void initSelf() {

		_queue = new LinkedBlockingQueue<CancellableThread>();
		_inquiries = Collections.synchronizedSet(new HashSet<Inquiry>());
		_running = Collections
				.synchronizedSet(new HashSet<CancellableThread>());
	}

	private Bundle createBundle(String address, String user) {

		Bundle res = new Bundle(2);
		res.putString(BLUETOOTH_ADDRESS, address);
		res.putString(TWITTER_USER, user);

		return res;
	}

	private Message createMessage(Inquiry inquiry, String result) {

		Message res = new Message();
		Bundle bundle = createBundle(inquiry.getAddress(), result);
		res.setData(bundle);

		return res;
	}

	private String getApiUrl() {

		SettingsManager settings = SettingsManager
				.getInstance(_context);
		String res = settings.getApiUrl();

		return res;
	}

	private boolean registerInquiry(Inquiry inquiry) {

		boolean contains = _inquiries.contains(inquiry);

		if (contains) {

			return false;
		}

		_inquiries.add(inquiry);

		return true;
	}
}
