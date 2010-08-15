package com.google.appengine.droidsensorserver;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tools.ant.types.LogLevel;

public class RemoveServlet extends HttpServlet {

	private final Logger log = Logger.getLogger("RemoveServlet");

	// 598,182
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH);
		cal.set(Calendar.MONTH, month - 1);
		long time = cal.getTime().getTime();
		PersistenceManager pm = PersistenceManagerFactoryFactory
				.createManager();
		Query query = pm.newQuery(BluetoothDevice.class);
		query.setFilter("_updated < updatedParam");
		query.setOrdering("_updated desc");
		query.declareParameters("Long updatedParam");
		query.setRange(0, 100);
		String msg = "";
		
		try {
			List<BluetoothDevice> results = (List<BluetoothDevice>) query
					.execute(new Long(time));
			msg += results.size();
			log.log(Level.INFO, "removing: " + results.size() + "(rec)");
			if (results.iterator().hasNext()) {
				for (BluetoothDevice e : results) {
					Object device = pm.getObjectById(BluetoothDevice.class, e
							.getKey());
					pm.deletePersistent(device);
					msg += ".";
				}
			} else {
			}
		} finally {
			query.closeAll();
			pm.close();
		}
		
		
		try {
			ServletOutputStream out = resp.getOutputStream();
			out.write(msg.getBytes());
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
