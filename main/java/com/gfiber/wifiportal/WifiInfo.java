package com.gfiber.wifiportal;

import org.json.JSONArray;
import org.json.JSONObject;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class WifiInfo {
  @Id
  public String customerId;

  public String addressId;
  public String controllerSiteName;
  public String controllerSiteId;
  public String controllerWlangroupName;
  public String controllerWlangroupId;
  public String controllerUsergroupName;
  public String controllerUsergroupId;

  public boolean ExtractWlangroupInfo(String jsonStr) {
    if (controllerSiteId == null)
      return false;
    JSONObject obj = new JSONObject(jsonStr);
    JSONArray jarray = obj.getJSONArray("data");
    for (int i = 0; i < jarray.length(); ++i) {
      JSONObject jo = jarray.getJSONObject(i);
      if (controllerSiteId.equalsIgnoreCase(jo.getString("site_id"))) {
        if ("default".equalsIgnoreCase(jo.getString("name"))) {
          this.controllerWlangroupId = jo.getString("_id");
          this.controllerWlangroupName = jo.getString("name");
          return true;
        }
      }
    }
    return false;
  }
  
  @Override
  public String toString() {
    return customerId + "," + addressId + "," + controllerSiteName + "," + controllerSiteId
        + "," + controllerWlangroupId + "," + controllerWlangroupName;
  }
}
