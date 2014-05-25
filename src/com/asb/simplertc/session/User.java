package com.asb.simplertc.session;

public class User {
	
	public static enum STATUS {
		NONE(2000),
		DISCONNECTED(2001),
		SUSPENDED(2002),
		CONNECTED(2003),
		STABLE(2004);
		
		private int value;
		
		private STATUS(int value) {
			this.value = value;
		}
		
		public int value() {
			return this.value;
		}
	};
	
	public static STATUS getStatus(int value) {
		switch(value) {
		case 2001:
			return STATUS.DISCONNECTED;
		case 2002:
			return STATUS.SUSPENDED;
		case 2003:
			return STATUS.CONNECTED;
		case 2004:
			return STATUS.STABLE;
		default:
			return STATUS.NONE;
		}
	}
	
	public String mName;
	public String mInstanceId;
	public String mSessionId;
	public SessionConfig mSessionConfig;
	public boolean bIsCaller;
	public STATUS mStatus;
	
	public User(String name, String instanceId, SessionConfig sessionConfig) {
		mName = name;
		mInstanceId = instanceId;
		mSessionConfig = sessionConfig;
		bIsCaller = false;
	}
	
	public boolean isCallable() {
		return mStatus == STATUS.STABLE;
	}

}
