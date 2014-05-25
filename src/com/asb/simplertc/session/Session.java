package com.asb.simplertc.session;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.asb.restapi.OnJsonJobListener;
import com.asb.simplertc.signaling.Channel;
import com.asb.simplertc.signaling.Channel.OnChannelListener;
import com.asb.simplertc.signaling.Channel.OnMessagedListener;

public class Session {
	
	private User mLocalUser;
	private ArrayList<User> mRemoteUsers;
	
	private SessionManager mSessionManager;
	
	private Channel mChannel;
	
	private enum CALLBACK {
		CONNECTED, CALL_REQUESTED, CALL_CONNECTED, CALL_DISCONNECTED, MESSAGED, ERRORED, REFRESHED
	};
	private OnSessionListener mSessionListener;
	public interface OnSessionListener {
		public abstract void onConnected();
		public abstract void onCallRequested();
		public abstract void onCallConnected();
		public abstract void onCallDisconnected();
		public abstract void onMessaged();
		public abstract void onError();
		public abstract void onRefreshed(ArrayList<User> remoteUserList);
	}
	
	public Session(String name, String instanceId, SessionConfig sessionConfig) {
		mLocalUser = new User(name, instanceId, sessionConfig);
		mRemoteUsers = new ArrayList<User>();
		
		mSessionManager = new SessionManager(this.mLocalUser);
	}
	
	public void setListener(OnSessionListener listener) {
		mSessionListener = listener;
	}
	
	public void connect() {
		mSessionManager.createSession(new OnJsonJobListener() {
			
			@Override
			public void onJonFinished(JSONArray responseArray) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onJonFinished(JSONObject response) {
				try {
					mLocalUser.mSessionId = response.getString("sessionId");
					
					mChannel = Channel.getInstance(mLocalUser);
					mChannel.initChannel(new OnChannelListener() {
						
						@Override
						public void onOpen() {
							// TODO Auto-generated method stub
							mChannel.regist();
						}
						
						@Override
						public void onError() {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onClose() {
							// TODO Auto-generated method stub
							
						}
					}, new OnMessagedListener() {
						
						@Override
						public void onMessage(String message) {
							// TODO Auto-generated method stub
							
						}
					});
					
					triggerCallback(CALLBACK.CONNECTED, new Object[1]);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
	}
	
	public void refresh() {
		mSessionManager.getGroupSessionInfo(new OnJsonJobListener() {
			
			@Override
			public void onJonFinished(JSONArray responseArray) {
				String localSessionId = mLocalUser.mSessionId;
				
				try {
					int arrayLength = responseArray.length();
					for(int i=0; i<arrayLength; i++) {
						JSONObject tmpObject = responseArray.getJSONObject(i);
						
						String sessionId = tmpObject.getString("sessionId");
						
						if(sessionId.equals(localSessionId))
							continue;
						
						User tmpUser = new User(tmpObject.getString("name"), mLocalUser.mInstanceId, null);
						tmpUser.mSessionId = sessionId;
						tmpUser.mStatus = User.getStatus(tmpObject.getInt("status"));
						
						mRemoteUsers.add(tmpUser);
					}
				}
				catch (JSONException e) {
					e.printStackTrace();
				}
				
				triggerCallback(CALLBACK.REFRESHED);
			}
			
			@Override
			public void onJonFinished(JSONObject response) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void changeStatus(User.STATUS status) {
		mSessionManager.changeSessionStatus(new OnJsonJobListener() {
			
			@Override
			public void onJonFinished(JSONArray responseArray) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onJonFinished(JSONObject response) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	public void call(User user) {
		
	}
	
	public void acceptCall() {
		
	}
	
	public void rejectCall() {
		
	}
	
	public void hangUp() {
		
	}
	
	private void triggerCallback(CALLBACK type) {
		triggerCallback(type, new Object[]{});
	}
	
	private void triggerCallback(CALLBACK type, Object... objects) {
		if(mSessionListener == null) 
			return;
		
		switch(type) {
		case CONNECTED:
			mSessionListener.onConnected();
			break;
		case CALL_REQUESTED:
			break;
		case CALL_CONNECTED:
			break;
		case CALL_DISCONNECTED:
			break;
		case MESSAGED:
			break;
		case ERRORED:
			break;
		case REFRESHED:
			mSessionListener.onRefreshed(mRemoteUsers);
			break;
		default:
			break;
		}
	}

}
