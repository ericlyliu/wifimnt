package com.gfiber.wifiportal;

import static com.google.appengine.api.urlfetch.FetchOptions.Builder.doNotValidateCertificate;

import java.net.URL;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

public class ControllerHandler {

	private static final Logger log = Logger.getLogger(ControllerHandler.class.getName());

	private static final String URL_FORMAT = "https://%s:8443%s";
	private static final String DEFAULT_SITE = "default";
	private static final String DEFAULT_HOST = "www.example.com";
	private static final String DEFAULT_USER = "default_user";
	private static final String DEFAULT_PASS = "default_pass";
	private String host;
	private String username;
	private String password;
	private URLFetchService fetcher;

	private String cookie;

	public ControllerHandler() {
		this.host = DEFAULT_HOST;
		this.username = DEFAULT_USER;
		this.password = DEFAULT_PASS;
		fetcher = URLFetchServiceFactory.getURLFetchService();
		login();
	}

	public ControllerHandler(String host, String name, String pwd) {
		this.host = host;
		this.username = name;
		this.password = pwd;
		fetcher = URLFetchServiceFactory.getURLFetchService();
		login();
	}

	private void login() {
		try {
			cookie = "";
			JSONObject jsonObj = new JSONObject().put("username", username).put("password", password);
			HTTPResponse response = postFetch("/api/login", jsonObj);
			logResponse(response);
			if (response.getResponseCode() == 200) {
				for (HTTPHeader header : response.getHeaders()) {
					if ("Set-Cookie".equalsIgnoreCase(header.getName())) {
						cookie = header.getValue();
					}
				}
			} else {
				log.severe("Can't login in controller as: " + username + "@" + host);
			}
		} catch (JSONException e) {
			log.warning(e.getMessage());
		}

	}

	private HTTPResponse putFetch(String api, JSONObject jsonObj) {
    try {
      URL url = new URL(String.format(URL_FORMAT, host, api));
      HTTPRequest request = new HTTPRequest(url, HTTPMethod.PUT, doNotValidateCertificate());
      request.setPayload(jsonObj.toString().getBytes());
      if (cookie != null && !cookie.isEmpty()) {
        request.addHeader(new HTTPHeader("Cookie", cookie));
      }
      HTTPResponse response;
      response = fetcher.fetch(request);
      logResponse(response);
      return response;
    } catch (Exception e) {
      log.warning(e.getMessage());
    }
    return null;
  }
	
	private HTTPResponse postFetch(String api, JSONObject jsonObj) {
		try {
			URL url = new URL(String.format(URL_FORMAT, host, api));
			HTTPRequest request = new HTTPRequest(url, HTTPMethod.POST, doNotValidateCertificate());
			request.setPayload(jsonObj.toString().getBytes());
			if (cookie != null && !cookie.isEmpty()) {
				request.addHeader(new HTTPHeader("Cookie", cookie));
			}
			HTTPResponse response;
			response = fetcher.fetch(request);
			logResponse(response);
			return response;
		} catch (Exception e) {
			log.warning(e.getMessage());
		}
		return null;
	}

	private HTTPResponse getFetch(String api) {
		try {
			log.info(String.format(URL_FORMAT, host, api));
			URL url = new URL(String.format(URL_FORMAT, host, api));
			log.info("Requesting (GET): " + url);
			HTTPRequest request = new HTTPRequest(url, HTTPMethod.GET, doNotValidateCertificate());
			if (cookie != null && !cookie.isEmpty()) {
				request.addHeader(new HTTPHeader("Cookie", cookie));
			}
			log.info(request.getHeaders().toString());
			HTTPResponse response;
			response = fetcher.fetch(request);
			logResponse(response);
			return response;
		} catch (Exception e) {
			log.warning(e.getMessage());
		}
		return null;
	}

	private HTTPResponse deleteFetch(String api) {
		try {
			log.info(String.format(URL_FORMAT, host, api));
			URL url = new URL(String.format(URL_FORMAT, host, api));
			log.info("Requesting (Delete): " + url);
			HTTPRequest request = new HTTPRequest(url, HTTPMethod.DELETE, doNotValidateCertificate());
			if (cookie != null && !cookie.isEmpty()) {
				request.addHeader(new HTTPHeader("Cookie", cookie));
			}
			log.info(request.getHeaders().toString());
			HTTPResponse response;
			response = fetcher.fetch(request);
			logResponse(response);
			return response;
		} catch (Exception e) {
			log.warning(e.getMessage());
		}
		return null;
	}

	private void logResponse(HTTPResponse response) {
		String s = "HTTPSResponse: " + response.getResponseCode() + " - ";
		for (int i = 0; i < response.getHeaders().size(); ++i) {
			s += response.getHeaders().get(i).getName() + ":" + response.getHeaders().get(i).getValue();
		}
		s += "|||" + new String(response.getContent());
		log.info(s);
	}

	public String getSite() {
		log.info("get sites");
		String apiUrl = "/api/stat/sites";
		HTTPResponse response = getFetch(apiUrl);
		if (response != null) {
			return new String(response.getContent());
		}
		return null;
	}
	
	public String createSite(String siteid) {
	  String apiUrl = "/api/s/default/cmd/sitemgr";
	  JSONObject obj = new JSONObject();
	  obj.put("desc", siteid);
	  obj.put("cmd", "add-site");
    HTTPResponse response = postFetch(apiUrl, obj);
    if (response != null) {
      return new String(response.getContent());
    }
    return null;
	}
	
	public String deleteSite(String siteid) {
    String apiUrl = "/api/s/default/cmd/sitemgr";
    JSONObject obj = new JSONObject();
    obj.put("site", siteid);
    obj.put("cmd", "delete-site");
    HTTPResponse response = postFetch(apiUrl, obj);
    if (response != null) {
      return new String(response.getContent());
    }
    return null;
  }
	
	public String getWlangroup(String siteName) {
		log.info("get wlangroup info");
		String apiUrl = "/api/s/" + siteName + "/rest/wlangroup";
		HTTPResponse response = getFetch(apiUrl);
		if (response != null) {
			return new String(response.getContent());
		}
		return null;
	}
	
	public String getWlanConf(String siteName) {
		log.info("get wlanconfig");
		String apiUrl = "/api/s/" + siteName + "/rest/wlanconf";
		HTTPResponse response = getFetch(apiUrl);
		if (response != null) {
			return new String(response.getContent());
		}
		return null;
	}

	public String deleteSSID(String siteName, String ssidId) {
		String apiUrl = "/api/s/" + siteName + "/rest/wlanconf/" + ssidId;
		log.info("--Delete url: " + apiUrl);
		HTTPResponse response = deleteFetch(apiUrl);
		if (response != null) {
			return new String(response.getContent());
		}
		return null;
	}
	
	public String createSSID(String siteName, JSONObject jsonObj) {
		String apiUrl = "/api/s/" + siteName + "/rest/wlanconf";
		HTTPResponse response = postFetch(apiUrl, jsonObj);
		if (response != null) {
			return new String(response.getContent());
		}
		return null;
	}
	
	public String editSSID(String siteName, String ssidId, JSONObject jsonObj) {
	  String apiUrl = "/api/s/" + siteName + "/rest/wlanconf/" + ssidId;
    HTTPResponse response = putFetch(apiUrl, jsonObj);
    if (response != null) {
      return new String(response.getContent());
    }
    return null;
	}
}
