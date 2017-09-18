//[START all]
package com.gfiber.wifiportal;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.ObjectifyService;

@SuppressWarnings("serial")
public class CreateWifiInfoServlet extends HttpServlet {

	// Process the http POST of the form
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	  UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser(); // Find out who the user is.
    if (!Constants.ADMIN_WHITELIST.contains(user.getEmail())) {
      resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Not authorized");
      return;
    }
	  String cid = req.getParameter("cid");
	  String fid = req.getParameter("fid");
	  if (cid == null || fid == null) {
	    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Need customer id or Fiber id");
	    return;
	  }
	  WifiInfo wifiInfo = ObjectifyService.ofy().load().type(WifiInfo.class)
        .id(cid).now();
		if (wifiInfo != null) {
		  resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Account already exist");
		  return;
		}
		wifiInfo = new WifiInfo();
		wifiInfo.customerId = cid;
		wifiInfo.addressId = fid;
		ObjectifyService.ofy().save().entity(wifiInfo).now();

		resp.sendRedirect("/admin.jsp");
	}
}
// [END all]
