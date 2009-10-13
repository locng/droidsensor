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

import com.google.appengine.repackaged.com.google.common.labs.misc.ToStringBuilder;

public class ApiServlet extends HttpServlet {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 8668774458868358942L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		req.setCharacterEncoding("utf-8");
		String address = req.getParameter("a");
		String user = req.getParameter("u");

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

				processGetAndPut(req, resp, address, user);

				return;
			}

			processPut(req, resp, address, user);

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
			HttpServletResponse resp, String address, String user)
			throws IOException {

		PersistenceManager pm = PersistenceManagerFactoryFactory
				.createManager();

		BluetoothDevice u = null;

		boolean json = "json".equalsIgnoreCase(req.getParameter("t"));

		Long id = addressToId(address);

		try {

			u = pm.getObjectById(BluetoothDevice.class, id);
			updateCount(u);
		} catch (JDOObjectNotFoundException e) {

			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);

			return;
		} finally {

			if (u == null || u.getTwitterUser().startsWith("@")) {

				try {

					if (u == null) {

						u = buildBluetoothDevice(address, user);
						updateCount(u);
						pm.makePersistent(u);
					} else {

						u.setTwitterUser(user);
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

			resp.setContentType("application/x-json");
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

					writer.write(toJSON(u));
				} else {
					writer.write(u.getTwitterUser().substring(1));
				}

			} else {

				if (json) {

					writer.write(toJSON(u));
				} else {

					writer.write(u.getTwitterUser());
				}
			}

			writer.flush();
			writer.close();

		} catch (IOException e) {

			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}

	}

	private String toJSON(BluetoothDevice device) {

		StringBuilder sb = new StringBuilder();
		sb.append('{');
		sb.append("twitterUser:");
		sb.append("'");
		sb.append(removePrefix(device.getTwitterUser()));
		sb.append("'");
		sb.append(',');
		sb.append("count:");
		sb.append(device.getCount());
		sb.append('}');

		return sb.toString();
	}

	private String removePrefix(String s) {

		if (s.startsWith("@")) {

			return s.substring(1);
		}

		return s;
	}

	private void processPut(HttpServletRequest req, HttpServletResponse resp,
			String address, String user) {

		BluetoothDevice u;
		PersistenceManager pm = PersistenceManagerFactoryFactory
				.createManager();
		Long id = addressToId(address);

		try {

			u = pm.getObjectById(BluetoothDevice.class, id);
			pm.detachCopy(u);

		} catch (JDOObjectNotFoundException e) {

			u = buildBluetoothDevice(address, user);
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

	private String deviceToString(BluetoothDevice device) {

		ToStringBuilder builder = new ToStringBuilder(BluetoothDevice.class);
		builder.add("address", device.getBluetoothAddress());
		builder.add("twitterUser", device.getTwitterUser());
		builder.add("count", device.getCount());

		return builder.toString();
	}
}
