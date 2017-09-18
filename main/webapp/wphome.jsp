<%-- //[START all]--%>
<%@ page
 contentType="text/html;charset=UTF-8"
 language="java"
%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%-- //[START imports]--%>
<%@ page import="com.gfiber.wifiportal.ControllerHandler"%>
<%@ page import="com.gfiber.wifiportal.WifiInfo"%>
<%@ page import="com.gfiber.wifiportal.WlanConfig"%>
<%@ page import="com.gfiber.wifiportal.SiteInfo"%>
<%@ page import="com.gfiber.wifiportal.Constants"%>
<%@ page import="java.util.Map"%>
<%@ page import="com.googlecode.objectify.Key"%>
<%@ page import="com.googlecode.objectify.ObjectifyService"%>
<%-- //[END imports]--%>
<%@ page import="java.util.List"%>
<%@ taglib
 prefix="fn"
 uri="http://java.sun.com/jsp/jstl/functions"
%>
<html>
<head>
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
</head>
<body>
 <div>
  <div class="title-container">
   <span class="title">Google Fiber Wifi Management Portal</span>
   <span class="sign-in">
     <%
 	     UserService userService = UserServiceFactory.getUserService();
       User user = userService.getCurrentUser();
       if (user != null) {
         pageContext.setAttribute("user", user);
     %>
   ${fn:escapeXml(user.nickname)} (<a style="color: white"
   href="<%=userService.createLogoutURL(request.getRequestURI())%>">sign out</a>)
   <%
 	     } else {
   %>
   <a style="color: white"
   href="<%=userService.createLoginURL(request.getRequestURI())%>">sign in</a>
   <%
 	       return;
       }
   %>
   </span>
  </div>
  <%
    // Look up user.
    WifiInfo wifiInfo = ObjectifyService.ofy().load()
      .type(WifiInfo.class).id(user.getEmail()).now();
    if (wifiInfo == null) {
  %>
   <div class="center">
     <div class="cb-title"> You are not authorized to access. </div>
   </div>
  <% 
      return;
    }
  %>
  <%-- //[START datastore]--%>
  <%
  if (wifiInfo != null) {
    pageContext.setAttribute("cid", wifiInfo.customerId);
    pageContext.setAttribute("aid", wifiInfo.addressId);
    pageContext.setAttribute("csid", wifiInfo.controllerSiteId);
    pageContext.setAttribute("csname", wifiInfo.controllerSiteName);
    pageContext.setAttribute("cwname", wifiInfo.controllerWlangroupName);
    pageContext.setAttribute("cwid", wifiInfo.controllerWlangroupId);
    pageContext.setAttribute("cuname", wifiInfo.controllerUsergroupName);
    pageContext.setAttribute("cuid", wifiInfo.controllerUsergroupId);
    if (Constants.ADMIN_WHITELIST.contains(user.getEmail())) {
  %>
  <p>Debug Info</p>
  <ul>
   <li>Customer ID: ${fn:escapeXml(cid)}</li>
   <li>Address ID: ${fn:escapeXml(aid)}</li>
   <li>Site ID: ${fn:escapeXml(csid)}</li>
   <li>Site Name: ${fn:escapeXml(csname)}</li>
   <li>Wlangroup ID: ${fn:escapeXml(cwid)}</li>
   <li>Wlangroup Name: ${fn:escapeXml(cwname)}</li>
   <li>Usergroup Name: ${fn:escapeXml(cuname)}</li>
   <li>Usergroup ID: ${fn:escapeXml(cuid)}</li>
  </ul>
  <%
    }
  %>
  <div class="center">
  <%
    ControllerHandler ch = new ControllerHandler();
  	  String wlanconfig = ch.getWlanConf(wifiInfo.controllerSiteName);
    List<WlanConfig> configs = WlanConfig.extractWlanConfigs(wlanconfig);
    int ssidLeft = 1;
    if (configs == null) {
  %>
  <div style="word-break: break-all; word-wrap: break-word;padding: 10px">
  No SSID configured. You need to configure a SSID to access internet.
  </div>
  <div class="cb-container">
  <form style="margin-bottom:0" action="/createssid" method="post" >
   <div class="cb-title"> Create New SSID </div>
   <div class="cb-row">
     <span class="cb-row-title">Name</span>
     <input  type="text"  name="ssid" />
   </div>
   <div class="cb-row">
     <span class="cb-row-title">Passcode</span>
     <input type="password" name="passcode" />
     <span> (at least 8 characters)</span>
   </div>
   <div class="cb-row">
     <input type="submit" value="Create" id="createbtn" />
   </div>
  </form>
  </div>
  <%
    	} else {
  %>
  <div class="lb-container">
  <%
    if (configs.size() > 0) {
      pageContext.setAttribute("sid", configs.get(0).id);
      pageContext.setAttribute("sname", configs.get(0).name);
      pageContext.setAttribute("ssecurity", configs.get(0).security);
      pageContext.setAttribute("ssidid", configs.get(0).id);
      pageContext.setAttribute("siappkey", configs.get(0).xIappKey);
  %>
  <div class="lbb-title">Current SSID</div>
  <div class="lb-box">
    <span class="lb-title">${fn:escapeXml(sname)}</span>
  </div>
  </div>
  <div class="cb-container">
  <form style="margin-bottom:0" action="/editssid" method="post">
   <div class="cb-title"> Edit SSID </div>
   <div class="cb-row">
     <span class="cb-row-title">Name</span>
     <input type="text" name="ssid" value="${fn:escapeXml(sname)}">
   </div>
   <div class="cb-row">
     <span class="cb-row-title">Passcode</span>
     <input type="password" name="passcode"><span>  (at least 8 characters)</span>
   </div>
   <div class="cb-row">
     <input type="hidden" name="id" value="${fn:escapeXml(ssidid)}" />
     <input type="hidden" name="iappkey" value="${fn:escapeXml(siappkey)}" />
     <input type="submit" value="Save Change" id="createbtn" />
   </div>
  </form>
  </div>
  <%
     }
  %>
  </div>
  <%
    	}
  }
  %>
  <%-- //[END datastore]--%>
 </div>
</body>
</html>
<%-- //[END all]--%>
