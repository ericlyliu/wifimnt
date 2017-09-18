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
 * Edit SSID.
 */
@SuppressWarnings("serial")
public class EditSsidServlet extends HttpServlet {

  private static final Logger log = Logger.getLogger(EditSsidServlet.class.getName());
	// Process the http POST of the form
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser(); // Find out who the user is.

		if (user == null /* || !Constants.ACCOUNT_WHITELIST.containsKey(user.getEmail()) */) {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		WifiInfo wifiInfo = ObjectifyService.ofy().load().type(WifiInfo.class)
		    .id(user.getEmail()).now();
		if (wifiInfo == null) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		String id = req.getParameter("id");
		String name = req.getParameter("ssid");
		String passCode = req.getParameter("passcode");
		if (passCode.length() < 8) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
          "Passcode requires at least 8 charactors");
      return;
    }
		String iappkey = req.getParameter("iappkey");
		ControllerHandler ch = new ControllerHandler();
		String response = ch.editSSID(wifiInfo.controllerSiteName, id,
				WlanConfig.editSSIDRequestJson(id, name, passCode, iappkey, wifiInfo));
		log.info("Customer " + wifiInfo.addressId + " edit existing SSID: \n"
        + response + "\n");
		resp.sendRedirect("/wphome.jsp");
	}
}
// [END all]
