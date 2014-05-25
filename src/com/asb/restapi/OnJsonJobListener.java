package com.asb.restapi;

import org.json.JSONArray;
import org.json.JSONObject;

public interface OnJsonJobListener {
	
	public abstract void onJonFinished(JSONObject response);
	public abstract void onJonFinished(JSONArray responseArray);
	
}
