package com.gfiber.wifiportal;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

public class SiteInfo {
	private static final Logger log = Logger.getLogger(SiteInfo.class.getName());
	
	public String name;
	public String id;
	// This is the visible field that should match with
	// Cusomter's fiber email or account id.
	public String description;
	public int numAdopted;
	public int numAp;
	public int numDisconnected;
	public int numNewAlarms;

	public static Map<String, SiteInfo> fromJson(String jsonStr) {
		Map<String, SiteInfo> sites = new HashMap<String, SiteInfo>();
		JSONObject obj = new JSONObject(jsonStr);
		//log.info(jsonStr);
		JSONArray jsonArray = obj.getJSONArray("data");
		for (int i = 0; i < jsonArray.length(); ++i) {
			JSONObject jo = jsonArray.getJSONObject(i);
			if ("default".equalsIgnoreCase(jo.getString("name"))) {
				continue;
			}
			log.info(jo.getString("desc"));
			SiteInfo info = new SiteInfo();
			info.name = jo.getString("name");
			info.id = jo.getString("_id");
			info.description = jo.getString("desc");
			info.numNewAlarms = jo.getInt("num_new_alarms");
			JSONArray healthArray = jo.getJSONArray("health");
			for (int j = 0; j < healthArray.length(); ++j) {
				jo = healthArray.getJSONObject(j);
				if ("wlan".equalsIgnoreCase(jo.getString("subsystem"))) {
					info.numAdopted = jo.getInt("num_adopted");
					info.numAp = jo.getInt("num_ap");
					info.numDisconnected = jo.getInt("num_disconnected");
				}
			}
			sites.put(info.description, info);
		}
		log.info(sites.keySet().toString());
		return sites;
	}
}
