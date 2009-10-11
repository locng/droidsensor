package com.google.appengine.droidsensorserver;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ApiServlet extends HttpServlet {

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

		// try {
		// address = encodeString(address);
		// } catch (Exception e) {
		//
		// throw new ServletException(e);
		// }

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

	private User buildUser(String address, String user) {

		User u = new User(address, user);
		u.setUpdateTime(System.currentTimeMillis());

		return u;
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

		User u = null;

		try {

			u = pm.getObjectById(User.class, address);
			u = pm.detachCopy(u);

		} catch (JDOObjectNotFoundException e) {

			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);

			return;
		} finally {

			if (u == null || u.getTwitterUser().startsWith("@")) {

				User su = buildUser(address, user);

				try {

					pm.makePersistent(su);
				} finally {

					pm.close();
				}
			} else {

				pm.close();
			}
		}

		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/plain");

		try {

			PrintWriter writer = resp.getWriter();
			// String name = getTwitterNickname(u.getTwitterUser());
			// writer.write(name);
			if (u.getTwitterUser().startsWith("@")) {

				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				writer.write(u.getTwitterUser().substring(1));
			} else {

				writer.write(u.getTwitterUser());
			}

			writer.flush();
			writer.close();

		} catch (IOException e) {

			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}

	}

	private void processPut(HttpServletRequest req, HttpServletResponse resp,
			String address, String user) {

		User u = buildUser(address, user);
		PersistenceManager pm = PersistenceManagerFactoryFactory
				.createManager();

		try {

			pm.makePersistent(u);

		} finally {
			pm.close();
		}
	}

	private void processDelete(HttpServletRequest req,
			HttpServletResponse resp, String address) {

		User u;
		PersistenceManager pm = PersistenceManagerFactoryFactory
				.createManager();

		try {

			u = pm.getObjectById(User.class, address);
			pm.deletePersistent(u);
		} catch (JDOObjectNotFoundException e) {

			return;
		} finally {

			pm.close();
		}
	}

	private void processGet(HttpServletRequest req, HttpServletResponse resp,
			String address) throws IOException {

		PersistenceManager pm = PersistenceManagerFactoryFactory
				.createManager();

		User u;

		try {

			u = pm.getObjectById(User.class, address);
			u = pm.detachCopy(u);
			
			if(u.getTwitterUser().startsWith("@")){
				
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
}
