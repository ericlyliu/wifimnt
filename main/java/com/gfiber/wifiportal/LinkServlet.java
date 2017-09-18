//[START all]
package com.gfiber.wifiportal;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.ObjectifyService;

@SuppressWarnings("serial")
public class LinkServlet extends HttpServlet {

  private static final Logger log = Logger.getLogger(LinkServlet.class.getName());
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
    WifiInfo wifiInfo = ObjectifyService.ofy().load().type(WifiInfo.class).id(cid).now();
    if (wifiInfo == null) {
      resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Account doesn't exist");
      return;
    }
    ControllerHandler ch = new ControllerHandler();
    // Try to load all site to see if there's a match email.
    String siteStr = ch.getSite();
    boolean found = false;
    if (siteStr != null) {
      Map<String, SiteInfo> sites = SiteInfo.fromJson(siteStr);
      if (sites.containsKey(fid)) {
        log.info("Site exists: " + fid);
        found = true;
        SiteInfo siteInfo = sites.get(fid);
        wifiInfo.controllerSiteId = siteInfo.id;
        wifiInfo.controllerSiteName = siteInfo.name;
        // Now check Wlangroup.
        String wlanStr = ch.getWlangroup(siteInfo.name);
        wifiInfo.ExtractWlangroupInfo(wlanStr);
        log.info("Site synced: " + wifiInfo.toString());
        ObjectifyService.ofy().save().entity(wifiInfo).now();
      }
    }
    if (!found) {
      // Create site.
      log.info("Create new site: " + fid);
      String jsonStr = ch.createSite(fid);
      JSONObject obj = new JSONObject(jsonStr);
      if (obj.has("data")) {
        JSONArray data = obj.getJSONArray("data");
        if (data.length() > 0) {
          JSONObject site = data.getJSONObject(0);
          wifiInfo.controllerSiteId = site.getString("_id");
          wifiInfo.controllerSiteName = site.getString("name");
          String wlanStr = ch.getWlangroup(wifiInfo.controllerSiteName);
          wifiInfo.ExtractWlangroupInfo(wlanStr);
          log.info("Site created: " + wifiInfo.toString());
          ObjectifyService.ofy().save().entity(wifiInfo).now();
        }
      }
    }
    resp.sendRedirect("/admin.jsp");
  }
}
// [END all]
