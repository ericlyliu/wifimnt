//[START all]
package com.gfiber.wifiportal;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.ObjectifyService;

/**
 * Create SSID.
 */
@SuppressWarnings("serial")
public class DeleteSsidServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(DeleteSsidServlet.class.getName());

	// Process the http POST of the form
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.info("Deleting SSID");
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser(); // Find out who the user is.
		if (user == null) {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		WifiInfo wifiInfo = ObjectifyService.ofy().load().type(WifiInfo.class).id(user.getEmail()).now();
		if (wifiInfo == null) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		String ssidId = req.getParameter("ssidid");
		String siteName = req.getParameter("sitename");
		ControllerHandler ch = new ControllerHandler();
		String response = ch.deleteSSID(siteName, ssidId);
		log.info("Deleted SSID:" + response);
		resp.sendRedirect("/wphome.jsp");
	}
}
// [END all]
