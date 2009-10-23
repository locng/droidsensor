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
 * TODO スレッドの停止にintrupt()を使用する. <br />
 * TODO RequestWorkerとRequestThreadをまとめる.
 * 
 * <br />
 * API呼び出しのキューを管理するクラス. 実行結果は {@link Callback} へ通知する.
 * 
 * @author esmasui@gmail.com
 * 
 */
public class DroidSensorInquiry {

	/**
	 * API呼び出しのパラメーター.
	 * 
	 */
	private static final class Inquiry {

		private String _address;

		private String _user;

		private String _message;

		public Inquiry(String address, String user, String message) {

			_address = address;
			_user = user;
			_message = message;
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

		public String getAddress() {

			return _address;
		}

		public String getUser() {

			return _user;
		}

		public String getMessage() {

			return _message;
		}

		@Override
		public int hashCode() {

			int res = getAddress().hashCode();
			res += getUser().hashCode();

			return res;
		}
	}

	private final class RequestThread extends Thread {

		private RequestWorker _worker;

		public RequestThread(RequestWorker worker) {

			super(worker);
			_worker = worker;
		}

		public void cancel() {

			_worker.cancel();
		}
	}

	/**
	 * API呼び出しを実行するスレッドの.
	 * 
	 */
	private final class RequestWorker implements Runnable {

		private String _apiUrl;

		private Inquiry _inquiry;

		private volatile Callback _callback;

		private RequestThread _owner;

		public RequestWorker(String apiUrl, Inquiry inquiry, Callback callback) {

			_apiUrl = apiUrl;
			_inquiry = inquiry;
			_callback = callback;
		}

		/**
		 * callbackを解除する.
		 */
		public void cancel() {

			_callback = null;
		}

		public void run() {

			APIResponse result = DroidSensorUtils.getTwitterId(_apiUrl,
					_inquiry.getAddress(), _inquiry.getUser(), _inquiry
							.getMessage());
			Message msg = createMessage(_inquiry, result);

			if (_callback != null) {

				_callback.handleMessage(msg);
			}

			_running.remove(_owner);
			_inquiries.remove(_inquiry);
			pollAndStart();
		}

		public void setOwner(RequestThread owner) {

			_owner = owner;
		}
	}

	/**
	 * メッセージにバンドルするBluetoothアドレスのキー.
	 */
	public static final String BLUETOOTH_ADDRESS = "BLUETOOTH_ADDRESS";

	/**
	 * メッセージにバンドルするTwitterユーザー名のキー.
	 */
	public static final String TWITTER_USER = "TWITTER_USER";

	/**
	 * メッセージにバンドルするメッセージのキー.
	 */
	public static final String MESSAGE = "MESSAGE";

	/**
	 * メッセージにバンドルするカウントのキー.
	 */
	public static final String COUNT = "COUNT";

	/**
	 * API呼び出しの同時実行可能数のデフォルト値.
	 */
	private static final int DEFAULT_MAX_THREADS = 3;

	/**
	 * 受付済みのAPI呼び出し.
	 */
	private Set<Inquiry> _inquiries;

	/**
	 * 実行中のAPI呼び出し.
	 */
	private Set<RequestThread> _running;

	private Context _context;

	/**
	 * 待機中のAPI呼び出し.
	 */
	private Queue<RequestThread> _queue;

	/**
	 * API呼び出しの同時実行可能数.
	 */
	private int _maxThreas = DEFAULT_MAX_THREADS;

	/**
	 * デフォルトの同時実行可能数を使用してインスタンス化する.
	 * 
	 * @param context
	 */
	public DroidSensorInquiry(Context context) {

		_context = context;
		initSelf();
	}

	/**
	 * 同時実行可能数を明示してインスタンス化する.
	 * 
	 * @param context
	 * @param maxThreads
	 */
	public DroidSensorInquiry(Context context, int maxThreads) {

		_context = context;
		_maxThreas = maxThreads;
		initSelf();
	}

	/**
	 * 実行中のすべてのAPI呼び出しを取り消す.
	 */
	public void cancelAll() {

		for (RequestThread e : _running) {

			e.cancel();
		}

		_queue.clear();
		_running.clear();
		_inquiries.clear();
	}

	/**
	 * デバイスの所有者をAPIを呼び出して得る. APIの呼び出し結果は callbackパラメーターに通知される.
	 * 
	 * @param address
	 * @param user
	 * @param callback
	 */
	public synchronized void getTwitterUser(String address, String user,
			String message, Callback callback) {

		String apiUrl = getApiUrl();
		Inquiry inquiry = new Inquiry(address, user, message);
		boolean success = registerInquiry(inquiry);

		if (!success) {

			return;
		}

		RequestWorker worker = new RequestWorker(apiUrl, inquiry, callback);
		startSequentially(worker);
	}

	/**
	 * メッセージにバンドルするデータを作成する.
	 * 
	 * @param address
	 * @param user
	 * @return
	 */
	private Bundle createBundle(String address, APIResponse resp) {

		Bundle res = new Bundle(2);
		res.putString(BLUETOOTH_ADDRESS, address);
		res.putString(TWITTER_USER, resp.getTwitterUser());
		res.putInt(COUNT, resp.getCount());

		if (resp.getMessage() != null) {

			res.putString(MESSAGE, resp.getMessage());
		}

		return res;
	}

	/**
	 * ハンドラーに通知するメッセージを作成する.
	 * 
	 * @param inquiry
	 * @param result
	 * @return
	 */
	private Message createMessage(Inquiry inquiry, APIResponse result) {

		Message res = new Message();
		Bundle bundle = createBundle(inquiry.getAddress(), result);
		res.setData(bundle);

		return res;
	}

	/**
	 * APIのURLを得る.
	 * 
	 * @return
	 */
	private String getApiUrl() {

		SettingsManager settings = SettingsManager.getInstance(_context);
		String res = settings.getApiUrl();

		return res;
	}

	/**
	 * 初期化処理を行う.
	 */
	private void initSelf() {

		_queue = new LinkedBlockingQueue<RequestThread>();
		_inquiries = Collections.synchronizedSet(new HashSet<Inquiry>());
		_running = Collections.synchronizedSet(new HashSet<RequestThread>());
	}

	/**
	 * 待機状態の問い合わせを1件、実行する.
	 */
	private void pollAndStart() {

		RequestThread th = _queue.poll();

		if (th == null) {

			return;
		}

		_running.add(th);
		th.start();
	}

	/**
	 * API呼び出しを受け付ける.
	 * 
	 * @param inquiry
	 * @return
	 */
	private boolean registerInquiry(Inquiry inquiry) {

		boolean contains = _inquiries.contains(inquiry);

		if (contains) {

			return false;
		}

		_inquiries.add(inquiry);

		return true;
	}

	/**
	 * キューに入っているAPI呼び出しを1件実行する.
	 * 
	 * @param worker
	 */
	private void startSequentially(RequestWorker worker) {

		RequestThread th = new RequestThread(worker);
		worker.setOwner(th);
		_queue.add(th);

		int threads = _running.size();
		if (threads <= _maxThreas) {

			pollAndStart();
		}
	}
}
