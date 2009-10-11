package com.google.appengine.droidsensorserver;

import java.io.IOException;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.datanucleus.store.query.QueryResult;

public class MigrateServlet extends HttpServlet {

	private static final long serialVersionUID = 8668774458868358942L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		doGet(req, resp);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		req.setCharacterEncoding("utf-8");
		String pass = req.getParameter("p");

		if (!"hogepiyo".equals(pass)) {

			return;
		}

		processCopy(req, resp);
	}

	private BluetoothDevice buildData(User user) {

		BluetoothDevice res = new BluetoothDevice(user.getBluetoothAddress(),
				user.getTwitterUser());
		res.setUpdated(Long.valueOf(user.getUpdateTime()));

		return res;
	}

	private void processCopy(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		PersistenceManager pm = PersistenceManagerFactoryFactory
				.createManager();

		// User u = null;
		int size = -1;

		String r = req.getParameter("r");

		if (r == null) {

			r = "0";
		}

		Integer start = Integer.valueOf(r);
		Extent<User> extent = null;
		int count = 0;
		int update = 0;
		try {

			// Query query = pm.newQuery(User.class);
			// query.setRange(0, 50);
			// query.setOrdering("bluetoothAddress asc");
			// query.setRange(start, start + 50);
			// QueryResult result = (QueryResult) query.execute();
			// result.i

			extent = pm.getExtent(User.class, false);

			for (User u : extent) {

				// pm.detachCopy(u);

				if (count < start) {

					++count;
					continue;
				}

				if (count > (start + 50)) {

					break;
				}

				BluetoothDevice data = buildData(u);
				pm.makePersistent(data);
				++update;
				// pm.deletePersistent(u);

				++count;
				// System.out.println(data);
			}
			// query.
			// u = pm.detachCopy(u);

			// } catch (JDOObjectNotFoundException e) {

			// resp.setStatus(HttpServletResponse.SC_NOT_FOUND);

			// return;
			// size = users.length;
		} finally {

			if (extent != null) {

				extent.closeAll();
			}

			pm.close();
		}

		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/plain");

		if (update == 0) {

			throw new RuntimeException("no data to update");

		}
	}
}
