package com.asb.simplertc.signaling;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.asb.simplertc.Constants;
import com.asb.simplertc.Constants.CHANNEL.SIG_ACTION;
import com.asb.simplertc.Constants.CHANNEL.SIG_TYPE;
import com.asb.simplertc.session.User;

public class Channel extends WebSocketClient {
	
	private static Channel mInstance = null;
	
	private OnChannelListener mChannelListener = null;
	private OnMessagedListener mMessagedListener = null;
	
	private User mLocalUser;
	
	private Channel(URI serverUri, User localUser) {
		super(serverUri, new Draft_17());
		
		mLocalUser = localUser;
	}
	
	public static synchronized Channel getInstance(User localUser) {
		if(mInstance == null) {
			try {
				URI uri = new URI(Constants.CHANNEL.SERVER);
				mInstance = new Channel(uri, localUser);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				return null;
			}
		}
		
		return mInstance;
	}
	
	public void initChannel(OnChannelListener listener, OnMessagedListener messageListener) {
		Log.e("jphong", "Channel.initChannel() was called");
		
		mChannelListener = listener;
		mMessagedListener = messageListener;
		
		connect();
	}
	
	public void closeChannel() {
		close();
	}
	
	public void regist() {
		sendMessage(createMessage("SERVER", "REG", "", ""));
	}
	
	public void requestCall(String receiver) {
		sendSignal(receiver, SIG_ACTION.CALL, SIG_TYPE.REJECTCALL);
	}
	
	public void receiverCall(String receiver) {
		sendSignal(receiver, SIG_ACTION.RETURN, SIG_TYPE.REQUESTCALL);
	}
	
	public void rejectCall(String receiver) {
		sendSignal(receiver, SIG_ACTION.RETURN, SIG_TYPE.REJECTCALL);
	}
	
	public void finishCall(String receiver) {
		sendSignal(receiver, SIG_ACTION.DONE, SIG_TYPE.FINISHCALL);
	}
	
	public void requestGUM(String receiver) {
		sendSignal(receiver, SIG_ACTION.CALL, SIG_TYPE.GUM);
	}
	
	public void completedGUM(String receiver) {
		sendSignal(receiver, SIG_ACTION.RETURN, SIG_TYPE.GUM);
	}
	
	public void notifyConnect(String receiver) {
		sendSignal(receiver, SIG_ACTION.CALL, SIG_TYPE.CONNECT);
	}
	
	public void rtcSignaling(JSONObject message, String receiver) {
		sendMessage(createMessage(receiver, "SEND", "RTC", message.toString()));
	}
	
	/**
	 * Private Methods
	 * 
	 * @param jsonMessage
	 */
	private JSONObject createMessage(String receiver, String action, String type, String message) {
		JSONObject jsonMessage;
		jsonMessage = new JSONObject();
		
		try {
			jsonMessage.put("SENDER", mLocalUser.mSessionId);
			jsonMessage.put("RECEIVER", receiver);
			jsonMessage.put("ACTION", action);
			jsonMessage.put("TYPE", type);
			jsonMessage.put("MESSAGE", message);
		} catch (JSONException e) {
			e.printStackTrace();
			
			return null;
		}
		
		return jsonMessage;
	}
	
	private void sendMessage(JSONObject jsonMessage) {
		Log.e("jphong", "[SOCKET]C->S:"+jsonMessage.toString());
		send(jsonMessage.toString());
	}
	
	private void sendSignal(String receiver, SIG_ACTION action, SIG_TYPE type) {
		sendSignal(receiver, action, null);
	}
	
	private void sendSignal(String receiver, SIG_ACTION action, SIG_TYPE type, SIG_TYPE pre) {
		int fullSignal = (action.value()*1000000) + (type.value()*1000);
		
		if(pre != null) {
			fullSignal += pre.value();
		}
		
		sendMessage(createMessage(receiver, "SEND", "SIG", String.valueOf(fullSignal)));
	}
	
	
	/**
	 * Listener Methods
	 * 
	 * @author jphofasb
	 */
	public interface OnChannelListener {
		public abstract void onOpen();
		public abstract void onError();
		public abstract void onClose();
	}
	
	public interface OnMessagedListener {
		public abstract void onMessage(String message);
	}
	
	@Override
	public void onClose(int arg0, String arg1, boolean arg2) {
		if(mChannelListener != null) {
			mChannelListener.onClose();
		}
	}

	@Override
	public void onError(Exception exception) {
		if(mChannelListener != null) {
			mChannelListener.onError();
		}
	}

	@Override
	public void onMessage(String message) {
		if(mChannelListener != null) {
			mMessagedListener.onMessage(message);
		}
	}

	@Override
	public void onOpen(ServerHandshake handshake) {
		if(mChannelListener != null) {
			mChannelListener.onOpen();
		}
	}

}
