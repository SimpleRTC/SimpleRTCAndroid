package com.asb.simplertc.session;

import java.util.HashMap;

public class SessionConfig {
	
	public static final String CONFIG_SERVER = "config_server";
	
	public HashMap<String, String> configs;
	
	public SessionConfig() {
		configs = new HashMap<String, String>();
	}
}
