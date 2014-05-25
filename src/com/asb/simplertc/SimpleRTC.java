package com.asb.simplertc;

import android.content.Context;

import com.asb.simplertc.session.Session;
import com.asb.simplertc.session.SessionConfig;

public class SimpleRTC {
	
	public static Session createSession(Context contxt, String name, String instanceId, SessionConfig sessionConfig) {
		return new Session(contxt, name, instanceId, sessionConfig);
	}
}