package com.asb.simplertc.session;

import java.util.HashMap;

public class SessionConfig {
	
	public static final String CONFIG_SERVER = "config_server";
	public static final String CONFIG_VIDEOVIEW = "config_videoview";
	
	public HashMap<String, Object> configs;
	
	public SessionConfig() {
		configs = new HashMap<String, Object>();
	}
}
