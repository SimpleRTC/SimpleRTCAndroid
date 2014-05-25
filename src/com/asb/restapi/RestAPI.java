package com.asb.restapi;

public class RestAPI {
	
	public static final String SERVER_DOMAIN = "http://211.188.198.45:8080/CallMe";
	
	public static void startJsonJob(AbstractJsonJob jsonJob, OnJsonJobListener listener) {
		
		jsonJob.setListener(listener);
		
		if(jsonJob != null) {
			Thread t = new Thread(jsonJob);
			t.start();
		}
	}
}
