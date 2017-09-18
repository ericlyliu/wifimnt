package com.gfiber.wifiportal;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class WlanConfig {
	// Default config.
	public static final String DEFAULT_CONFIG =
			"{\"enabled\":true,"
			+ "\"ratectrl_ng_mode\":\"default\",\"ratectrl_na_mode\":\"default\","
			+ "\"ratectrl_ng_cck_1\":\"disabled\","
			+ "\"ratectrl_ng_cck_2\":\"disabled\","
			+ "\"ratectrl_ng_cck_5_5\":\"disabled\","
			+ "\"ratectrl_ng_cck_11\":\"disabled\","
			+ "\"ratectrl_ng_6\":\"basic\",\"ratectrl_ng_9\":\"supported\","
			+ "\"ratectrl_ng_12\":\"basic\",\"ratectrl_ng_18\":\"supported\","
			+ "\"ratectrl_ng_24\":\"basic\",\"ratectrl_ng_36\":\"supported\","
			+ "\"ratectrl_ng_48\":\"supported\",\"ratectrl_ng_54\":\"supported\","
			+ "\"ratectrl_na_6\":\"basic\",\"ratectrl_na_9\":\"supported\","
			+ "\"ratectrl_na_12\":\"basic\",\"ratectrl_na_18\":\"supported\","
			+ "\"ratectrl_na_24\":\"basic\",\"ratectrl_na_36\":\"supported\","
			+ "\"ratectrl_na_48\":\"supported\",\"ratectrl_na_54\":\"supported\","
			+ "\"dtim_mode\":\"default\",\"dtim_ng\":1,\"dtim_na\":1,"
			+ "\"mac_filter_enabled\":false,\"mac_filter_policy\":\"deny\","
			+ "\"mac_filter_list\":[],\"bc_filter_enabled\":false,"
			+ "\"bc_filter_list\":[],"
			// --------- Below are the params that needs to be set -------
			+ "\"security\":\"wpapsk\","
			+ "\"wep_idx\":1,"
			+ "\"wpa_mode\":\"wpa2\",\"wpa_enc\":\"ccmp\","
			+ "\"usergroup_id\":\"58ffbc05e4b0f4424d0eb171\","
			+ "\"name\":\"anothertest\","
			+ "\"x_passphrase\":\"11111111\","
			+ "\"wlangroup_id\":\"590bba99ef0ad31f7c6a9ea8\",\"schedule\":[]}";
	
	public String id;
	public String name;
	public String siteId;
	public String usergroupId;
	public String wlangroupId;
	public String security;

	public String wpaEnc;
	public String wpaMode;
	public String xIappKey;
	public String xPassphrase;

	public JSONObject toCreateSSIDJson() {
		JSONObject obj = new JSONObject(DEFAULT_CONFIG);
		obj.put("name", name);
		obj.put("x_passphrase", xPassphrase);
		obj.put("wlangroup_id", wlangroupId);
		obj.put("usergroup_id", usergroupId);
		return obj;
	}

	public static JSONObject createSSIDRequestJson(String name,
			String passCode, WifiInfo wifiInfo) {
		JSONObject obj = new JSONObject(DEFAULT_CONFIG);
		obj.put("name", name);
		obj.put("x_passphrase", passCode);
		obj.put("wlangroup_id", wifiInfo.controllerWlangroupId);
		obj.put("usergroup_id", wifiInfo.controllerUsergroupId);
		return obj;
	}
	
	public static JSONObject editSSIDRequestJson(String id, String name,
      String passCode, String iappKey, WifiInfo wifiInfo) {
    JSONObject obj = new JSONObject(DEFAULT_CONFIG);
    obj.put("_id", id);
    obj.put("name", name);
    obj.put("x_iapp_key", iappKey);
    obj.put("x_passphrase", passCode);
    obj.put("wlangroup_id", wifiInfo.controllerWlangroupId);
    obj.put("usergroup_id", wifiInfo.controllerUsergroupId);
    return obj;
  }
	
	public static List<WlanConfig> extractWlanConfigs(String jsonStr) {
		JSONObject jsonObj = new JSONObject(jsonStr);
		if (!jsonObj.has("data"))
			return null;
		JSONArray wlanListJson = jsonObj.getJSONArray("data");
		if (wlanListJson.length() == 0)
			return null;
		List<WlanConfig> wlanList = new ArrayList<WlanConfig>();
		for (int i = 0; i < wlanListJson.length(); ++i) {
			WlanConfig conf = new WlanConfig();
			JSONObject obj = wlanListJson.getJSONObject(i);
			conf.id = obj.getString("_id");
			conf.name = obj.getString("name");
			conf.siteId = obj.getString("site_id");
			conf.xIappKey = obj.getString("x_iapp_key");
			if (obj.has("usergroup_id")) {
				conf.usergroupId = obj.getString("usergroup_id");
			}
			conf.wlangroupId = obj.getString("wlangroup_id");
			conf.security = obj.getString("security");
			wlanList.add(conf);
		}
		return wlanList;
	}
}
