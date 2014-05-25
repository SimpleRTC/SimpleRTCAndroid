package com.asb.simplertc.session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.asb.restapi.AbstractJsonJob;
import com.asb.restapi.OnJsonJobListener;
import com.asb.restapi.RestAPI;

public class SessionManager {
	
	private User mLocalUser; 
	
	public SessionManager(User localUser) {
		mLocalUser = localUser;
	}
	
	public void createSession(OnJsonJobListener listener) {
		Log.e("jphong", "createSession was called!");
		
		RestAPI.startJsonJob(new CreateSession(), listener);
	}
	
	public void createGroupSession(OnJsonJobListener listener) {
		Log.e("jphong", "createGroupSession was called!");
		
		RestAPI.startJsonJob(new CreateGroupSession(), listener);
	}
	
	public void getGroupSessionInfo(OnJsonJobListener listener) {
		Log.e("jphong", "createGroupSession was called!");
		
		RestAPI.startJsonJob(new GetGroupSessionInfo(), listener);
	}
	
	public void changeSessionStatus(OnJsonJobListener listener) {
		Log.e("jphong", "createGroupSession was called!");
		
		RestAPI.startJsonJob(new ChangeSessionStatus(), listener);
	}
	
	private class CreateSession extends AbstractJsonJob {

		public CreateSession() {
			super(RestAPI.SERVER_DOMAIN + "/API/session/create");
			
			Log.e("jphong", mJsonUrl);
			
			setData("name", mLocalUser.mName);
			setData("instanceid", mLocalUser.mInstanceId);
		}

		@Override
		public void run() {
			String rawString = getRawJsonString();
			try {
				mCallback.onJonFinished(new JSONObject(rawString));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private class CreateGroupSession extends AbstractJsonJob {

		public CreateGroupSession() {
			super(RestAPI.SERVER_DOMAIN + "/API/groupsession/create");
			Log.e("jphong", mJsonUrl);
			
			setData("instanceid", mLocalUser.mInstanceId);
		}

		@Override
		public void run() {
			if(mCallback != null) {
				try {
					mCallback.onJonFinished(new JSONObject(getRawJsonString()));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private class GetGroupSessionInfo extends AbstractJsonJob {

		public GetGroupSessionInfo() {
			super(RestAPI.SERVER_DOMAIN + "/API/groupsession/info");
			Log.e("jphong", mJsonUrl);
			
			setData("instanceid", mLocalUser.mInstanceId);
		}

		@Override
		public void run() {
			try {
				JSONObject fullResponse = new JSONObject(getRawJsonString());
				mCallback.onJonFinished(fullResponse.getJSONArray("userList"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	private class ChangeSessionStatus extends AbstractJsonJob {

		public ChangeSessionStatus() {
			super(RestAPI.SERVER_DOMAIN + "/API/session/change/status");
			Log.e("jphong", mJsonUrl);
			
			setData("instanceid", mLocalUser.mInstanceId);
		}

		@Override
		public void run() {
			getRawJsonString();
			
			if(mCallback != null) {
				mCallback.onJonFinished(new JSONObject());
			}
		}
		
	}
	
}
