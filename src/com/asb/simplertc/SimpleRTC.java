package com.asb.simplertc;

import com.asb.simplertc.session.Session;
import com.asb.simplertc.session.SessionConfig;

public class SimpleRTC {
	
	public static Session createSession(String name, String instanceId, SessionConfig sessionConfig) {
		return new Session(name, instanceId, sessionConfig);
	}
}