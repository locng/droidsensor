package org.sevenleaves.droidsensor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler.Callback;
import android.util.Log;

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

		public RequestWorker(String apiUrl, Inquiry inquiry, Callback callback) {

			_apiUrl = apiUrl;
			_inquiry = inquiry;
			_callback = callback;
		}

		public void run() {

			String result = DroidSensorUtils.getTwitterId(_apiUrl, _inquiry
					.getAddress(), _inquiry.getUser());
			Message msg = createMessage(_inquiry, result);

			if (_callback != null) {

				_callback.handleMessage(msg);
			}

			_inquiries.remove(_inquiry);
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

	public void getTwitterUser(String address, String user, Callback callback) {

		String apiUrl = getApiUrl();
		Inquiry inquiry = new Inquiry(address, user);
		boolean success = registerInquiry(inquiry);

		if (!success) {

			return;
		}

		RequestWorker worker = new RequestWorker(apiUrl, inquiry, callback);
		CancellableThread th = new CancellableThread(worker);
		_running.add(th);
		th.start();
	}

	public void cancelAll() {

		for (CancellableThread e : _running) {

			e.cancel();
		}

		_running.clear();
		_inquiries.clear();
	}

	@Override
	protected void finalize() throws Throwable {

		super.finalize();
		cancelAll();
	}

	private void initSelf() {

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

		DroidSensorSettings settings = DroidSensorSettings
				.getInstance(_context);
		String res = settings.getApiUrl();

		return res;
	}

	private boolean registerInquiry(Inquiry inquiry) {


		int threads = _inquiries.size();

		Log.d("DroidSensorInquiry", _inquiries.size()
				+ " inquiries in progress");

		if(threads >= _maxThreas){
			
			return false;
		}
		
		boolean contains = _inquiries.contains(inquiry);

		if (contains) {

			return false;
		}

		_inquiries.add(inquiry);

		return true;
	}
}
