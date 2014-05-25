package com.asb.simplertc.session;

import java.util.ArrayList;

import org.appspot.apprtc.VideoStreamsView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.asb.restapi.OnJsonJobListener;
import com.asb.simplertc.Constants.CHANNEL.SIG_ACTION;
import com.asb.simplertc.Constants.CHANNEL.SIG_TYPE;
import com.asb.simplertc.Constants.WEBRTC.ROLE;
import com.asb.simplertc.Constants.WEBRTC.STEP;
import com.asb.simplertc.signaling.Channel;
import com.asb.simplertc.signaling.Channel.OnChannelListener;
import com.asb.simplertc.signaling.Channel.OnMessagedListener;
import com.asb.simplertc.utils.SLog;
import com.asb.simplertc.webrtc.WebRTC;

public class Session {
	
	private Context mContext;
	
	private User mLocalUser;
	private User mRemoteUser;
	private ArrayList<User> mRemoteUsers;
	
	private SessionManager mSessionManager;
	
	private Channel mChannel;
	
	private WebRTC mWebRtc;
	
	private MessageHookingListener mMessageHookingListener;
	private WebRTCStepListener mWebRtcStepListener;
	
	private enum CALLBACK {
		CONNECTED, CALL_REQUESTED, CALL_CONNECTED, CALL_DISCONNECTED, MESSAGED, ERRORED, REFRESHED
	};
	private OnSessionListener mSessionListener;
	public interface OnSessionListener {
		public abstract void onConnected(String sessionId);
		public abstract void onCallRequested(User remoteUser);
		public abstract void onCallConnected();
		public abstract void onCallDisconnected();
		public abstract void onMessaged();
		public abstract void onError();
		public abstract void onRefreshed(ArrayList<User> remoteUserList);
	}
	
	public Session(Context context, String name, String instanceId, SessionConfig sessionConfig) {
		mContext = context;
		
		mLocalUser = new User(name, instanceId, sessionConfig);
		mRemoteUsers = new ArrayList<User>();
		
		mSessionManager = new SessionManager(this.mLocalUser);
		
		mMessageHookingListener = new MessageHookingListener();
		mWebRtcStepListener = new WebRTCStepListener();
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
					
					mWebRtc = new WebRTC(
							mContext,
							mLocalUser.bIsCaller?ROLE.CALLER:ROLE.CALLEE, 
							mLocalUser, 
							mWebRtcStepListener, 
							(VideoStreamsView)mLocalUser.mSessionConfig.configs.get(SessionConfig.CONFIG_VIDEOVIEW)
					);
					
					OnMessagedListener messageListener = mWebRtc.initChannel(mMessageHookingListener);
					
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
					}, messageListener);
					
					triggerCallback(CALLBACK.CONNECTED, mLocalUser.mSessionId);
					
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
				
				triggerCallback(CALLBACK.REFRESHED, mLocalUser.mSessionId);
			}
			
			@Override
			public void onJonFinished(JSONObject response) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void changeStatus(User.STATUS status) {
		mLocalUser.mStatus = status;
		
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
	
	public void call(User remoteUser) {
		mRemoteUser = remoteUser;
		mLocalUser.bIsCaller = true;
		
		mChannel.requestCall(mRemoteUser.mSessionId);
	}
	
	public void acceptCall() {
		mChannel.receiveCall(mRemoteUser.mSessionId);
	}
	
	public void rejectCall() {
		
	}
	
	public void hangUp() {
		mWebRtc.stopWebRtc(true, mRemoteUser);
		
		mChannel.finishCall(mRemoteUser.mSessionId);
	}
	
	private class MessageHookingListener implements WebRTC.OnMessageHookingListener {

		@Override
		public void onMessaged(String message) {
			try {
				JSONObject jsonMessage = new JSONObject(message);
				
				String mSender = jsonMessage.getString("SENDER");
				String mReceiver = jsonMessage.getString("RECEIVER");
				String msgAction = jsonMessage.getString("ACTION");
				String msgType = jsonMessage.getString("TYPE");
				String msgMessage = jsonMessage.getString("MESSAGE");
				
				if(msgAction.equals("SEND") && msgType.equals("SIG")) {
					int fullSignal = Integer.parseInt(msgMessage);
					
					int sigActionNum = generateSignal(fullSignal, 0);
					int sigTypeNum = generateSignal(fullSignal, 1);
					
					SIG_ACTION sigAction = SIG_ACTION.fromValue(sigActionNum);
					SIG_TYPE sigType = SIG_TYPE.fromValue(sigTypeNum);
					
					if(sigAction == SIG_ACTION.CALL && sigType == SIG_TYPE.REQUESTCALL) 
						mLocalUser.bIsCaller = false;
					
					switch(sigType) {
					case REQUESTCALL:
						if(mLocalUser.bIsCaller) {
							SLog.LOGE("Callee accept call request");
							
							mWebRtc.initWebRtc(true, true, mRemoteUser);
						}
						else {
							SLog.LOGE("Caller send call request");
							
							mRemoteUser = new User("", "", new SessionConfig());
							mRemoteUser.mSessionId = mSender;
							
							triggerCallback(CALLBACK.CALL_REQUESTED, mRemoteUser);
						}
						break;
					case CONNECT:
						triggerCallback(CALLBACK.CALL_CONNECTED);
						break;
					case GUM:
						if(mLocalUser.bIsCaller) {
							mWebRtc.startWebRtc();
						}
						else {
							mWebRtc.initWebRtc(true, true, mRemoteUser);
						}
						break;
					}
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public int generateSignal(int fullSignal, int type) {
		if(type == 0) {
			return fullSignal/1000000;
		}
		else if(type == 1) {
			return (fullSignal - ((fullSignal/1000000) * 1000000))/1000;
		}
		
		return -1;
	}
	
	private class WebRTCStepListener implements WebRTC.OnWebRTCStepListener {

		@Override
		public void onStepChanged(STEP step) {
			SLog.LOGE("WebRTC Status : " + step.name());
			
			switch(step) {
			case INIT:
				SLog.LOGE("WebRTC initializing...");
				break;
			case SIGNALING:
				SLog.LOGE("WebRTC signaling...");
				break;
			case CONNECTED:
				triggerCallback(CALLBACK.CALL_CONNECTED);
				break;
			case DISCONNECTED:
				triggerCallback(CALLBACK.CALL_DISCONNECTED);
				break;
			case GUM_SUCCESS:
				if(mLocalUser.bIsCaller) {
					mChannel.requestGUM(mRemoteUser.mSessionId);
				}
				else {
					mWebRtc.startWebRtc();
					mChannel.completedGUM(mRemoteUser.mSessionId);
				}
				break;
			case GUM_FAILED:
				break;
			}
		}
		
	}
	
	private void triggerCallback(CALLBACK type) {
		triggerCallback(type, new Object[]{});
	}
	
	private void triggerCallback(CALLBACK type, Object... objects) {
		if(mSessionListener == null) 
			return;
		
		switch(type) {
		case CONNECTED:
			mSessionListener.onConnected((String)objects[0]);
			break;
		case CALL_REQUESTED:
			mSessionListener.onCallRequested((User)objects[0]);
			break;
		case CALL_CONNECTED:
			mSessionListener.onCallConnected();
			break;
		case CALL_DISCONNECTED:
			mSessionListener.onCallDisconnected();
			break;
		case MESSAGED:
			mSessionListener.onMessaged();
			break;
		case ERRORED:
			mSessionListener.onMessaged();
			break;
		case REFRESHED:
			mSessionListener.onRefreshed(mRemoteUsers);
			break;
		default:
			return;
		}
	}
}
