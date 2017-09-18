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
<link
 type="text/css"
 rel="stylesheet"
 href="/stylesheets/main.css"
/>
</head>
<body>
 <div>
  <div class="title-container">
   <span class="title">Google Fiber Wifi Management Portal Admin</span>
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
    if (!Constants.ADMIN_WHITELIST.contains(user.getEmail())) {
      %>
      <div class="center">
        <div class="lbb-title">You are not authorized!</div>
      </div>
      <%
      return;
    }
  %>
  <div class="cb-container">
  <form style="margin-bottom:0" action="/createaccount" method="post" >
   <div class="cb-title"> Create New Account </div>
   <div class="cb-row">
     <span class="cb-row-title">Customer ID</span>
     <input  type="text"  name="cid" />
   </div>
   <div class="cb-row">
     <span class="cb-row-title">Fiber ID</span>
     <input type="text" name="fid" />
   </div>
   <div class="cb-row">
     <input type="submit" value="Create" id="createbtn" />
   </div>
  </form>
  </div>
  <%
  	// Look up user.
  List<WifiInfo> accounts = ObjectifyService.ofy().load()
      .type(WifiInfo.class).limit(200).list();
  if (accounts != null) {
    for (WifiInfo wifiInfo : accounts) {
      pageContext.setAttribute("cid", wifiInfo.customerId);
      pageContext.setAttribute("aid", wifiInfo.addressId);
      pageContext.setAttribute("csid", wifiInfo.controllerSiteId);
      pageContext.setAttribute("csname", wifiInfo.controllerSiteName);
      pageContext.setAttribute("cwname", wifiInfo.controllerWlangroupName);
      pageContext.setAttribute("cwid", wifiInfo.controllerWlangroupId);
      pageContext.setAttribute("cuname", wifiInfo.controllerUsergroupName);
      pageContext.setAttribute("cuid", wifiInfo.controllerUsergroupId);
  %>
  <div class="cb-container">
  <ul>
   <li>Customer ID: <b>${fn:escapeXml(cid)}</b></li>
   <li>Address ID: <b>${fn:escapeXml(aid)}</b></li>
   <li>Site ID: ${fn:escapeXml(csid)}</li>
   <li>Site Name: ${fn:escapeXml(csname)}</li>
   <li>Wlangroup ID: ${fn:escapeXml(cwid)}</li>
   <li>Wlangroup Name: ${fn:escapeXml(cwname)}</li>
  </ul>
  <div>
    <form action="/link" method="post">
      <input type="hidden" name="cid" value="${fn:escapeXml(cid)}" />
      <input type="hidden" name="fid" value="${fn:escapeXml(aid)}" />
      <input type="submit" value="Link/Update" id="createbtn" />
    </form>
    
    <form action="/deleteaccount" method="post">
      <input type="hidden" name="cid" value="${fn:escapeXml(cid)}" />
      <input type="hidden" name="fid" value="${fn:escapeXml(aid)}" />
      <input type="submit" value="Delete Account" id="createbtn" />
    </form>
  </div>
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
