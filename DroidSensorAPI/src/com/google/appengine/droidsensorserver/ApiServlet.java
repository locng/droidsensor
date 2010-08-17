package com.google.appengine.droidsensorserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

public class ApiServlet extends HttpServlet {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 8668774458868358942L;

	private static final String TYPE_JSON = "application/x-json";

	// private static final String TYPE_JSON = "text/plain";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		req.setCharacterEncoding("utf-8");
		String address = req.getParameter("a");
		String user = req.getParameter("u");
		String message = req.getParameter("m");

		if (address == null) {

			processIgnore(req, resp);
			return;
		}

		if (user == null) {

			processGet(req, resp, address);

			return;
		}

		if (user.length() == 0) {

			processDelete(req, resp, address);

			return;
		}

		if (user.length() > 0) {

			if (user.startsWith("@")) {

				processGetAndPut(req, resp, address, user, message);

				return;
			}

			processPut(req, resp, address, user, message);

			return;
		}

		processIgnore(req, resp);
	}

	private BluetoothDevice buildBluetoothDevice(String address, String user) {

		BluetoothDevice res = new BluetoothDevice(address, user);
		res.setUpdated(System.currentTimeMillis());
		res.setCount(0);

		return res;
	}

	private void processIgnore(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
	}

	private void processGetAndPut(HttpServletRequest req,
			HttpServletResponse resp, String address, String user,
			String message) throws IOException {

		PersistenceManager pm = PersistenceManagerFactoryFactory
				.createManager();

		BluetoothDevice u = null;
		String prevMessage = null;
		String prevUser = null;

		boolean json = "json".equalsIgnoreCase(req.getParameter("t"));

		Long id = addressToId(address);
		boolean ignore = false;

		try {

			u = pm.getObjectById(BluetoothDevice.class, id);

			Calendar cal = Calendar.getInstance();
			int day = cal.get(Calendar.DAY_OF_MONTH);
			cal.set(Calendar.DAY_OF_MONTH, day - 3);
			long time = cal.getTime().getTime();
			if (u.getUpdated() < time) {
				ignore = true;
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			updateCount(u);
			prevUser = u.getTwitterUser();
			prevMessage = u.getMessage();
		} catch (JDOObjectNotFoundException e) {

			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);

			return;
		} finally {

			if (ignore) {

				u.setTwitterUser(user);
				u.setCount(0);
				if (isEmpty(message)) {
					u.setMessage(null);
				} else {
					u.setMessage(message);
				}
				updateCount(u);

				pm.close();
				return;
			}
			if (u == null || u.getTwitterUser().startsWith("@")) {

				try {

					if (u == null) {

						u = buildBluetoothDevice(address, user);
						updateCount(u);
						pm.makePersistent(u);
					} else {

						u.setTwitterUser(user);
					}

					if (isEmpty(message)) {

						u.setMessage(null);
					} else {

						u.setMessage(message);
					}
				} finally {

					pm.close();
				}
			} else {

				pm.close();
			}
		}

		resp.setCharacterEncoding("utf-8");

		if (json) {

			resp.setContentType(TYPE_JSON);
		} else {

			resp.setContentType("text/plain");
		}

		try {

			PrintWriter writer = resp.getWriter();
			// String name = getTwitterNickname(u.getTwitterUser());
			// writer.write(name);
			if (u.getTwitterUser().startsWith("@")) {

				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);

				if (json) {

					writer.write(toJSON(prevUser, u.getCount(), prevMessage));
				} else {
					writer.write(prevUser.substring(1));
				}

			} else {

				if (json) {

					writer.write(toJSON(prevUser, u.getCount(), prevMessage));
				} else {

					writer.write(prevUser);
				}
			}

			writer.flush();
			writer.close();

		} catch (IOException e) {

			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}

	}

	@SuppressWarnings("unchecked")
	private String toJSON(String twitterUser, int count, String message) {

		JSONObject obj = new JSONObject();
		obj.put("twitterUser", removePrefix(twitterUser));
		obj.put("count", new Integer(count));

		if (!isEmpty(message)) {

			obj.put("message", message);
		}

		return obj.toJSONString();
	}

	private String removePrefix(String s) {

		if (s.startsWith("@")) {

			return s.substring(1);
		}

		return s;
	}

	private boolean isEmpty(String s) {

		if (s == null) {

			return true;
		}

		return s.trim().length() == 0;
	}

	private void processPut(HttpServletRequest req, HttpServletResponse resp,
			String address, String user, String message) {

		BluetoothDevice u;
		PersistenceManager pm = PersistenceManagerFactoryFactory
				.createManager();
		Long id = addressToId(address);

		try {

			u = pm.getObjectById(BluetoothDevice.class, id);

			if (isEmpty(message)) {

				u.setMessage(null);
			} else {

				u.setMessage(message);
			}
		} catch (JDOObjectNotFoundException e) {

			u = buildBluetoothDevice(address, user);
			u.setMessage(message);
			pm.makePersistent(u);

		} finally {
			pm.close();
		}
	}

	private void processDelete(HttpServletRequest req,
			HttpServletResponse resp, String address) {

		BluetoothDevice u;
		PersistenceManager pm = PersistenceManagerFactoryFactory
				.createManager();
		Long id = addressToId(address);

		try {

			u = pm.getObjectById(BluetoothDevice.class, id);
			pm.deletePersistent(u);
		} catch (JDOObjectNotFoundException e) {

			return;
		} finally {

			pm.close();
		}
	}

	private Long addressToId(String address) {

		return Long.valueOf(address.hashCode());
	}

	private void processGet(HttpServletRequest req, HttpServletResponse resp,
			String address) throws IOException {

		PersistenceManager pm = PersistenceManagerFactoryFactory
				.createManager();

		BluetoothDevice u;
		Long id = addressToId(address);

		try {

			u = pm.getObjectById(BluetoothDevice.class, id);

			Calendar cal = Calendar.getInstance();
			int day = cal.get(Calendar.DAY_OF_MONTH);
			cal.set(Calendar.DAY_OF_MONTH, day - 3);
			long time = cal.getTime().getTime();
			if (u.getUpdated() < time) {
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			updateCount(u);

			if (u.getTwitterUser().startsWith("@")) {

				throw new JDOObjectNotFoundException();
			}
		} catch (JDOObjectNotFoundException e) {

			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);

			return;
		} finally {

			pm.close();
		}

		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/plain");

		try {

			PrintWriter writer = resp.getWriter();
			// String name = getTwitterNickname(u.getTwitterUser());
			// writer.write(name);
			writer.write(u.getTwitterUser());
			writer.flush();
			writer.close();
		} catch (IOException e) {

			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}

	}

	private void updateCount(BluetoothDevice device) {

		Integer count = device.getCount();
		int newCount = nullToZero(count) + 1;
		device.setCount(newCount);
		device.setUpdated(Calendar.getInstance().getTimeInMillis());
	}

	private int nullToZero(Integer i) {

		if (i == null) {

			return 1;
		}

		return i.intValue();
	}

}
