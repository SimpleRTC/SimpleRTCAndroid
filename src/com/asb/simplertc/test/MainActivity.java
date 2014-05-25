package com.asb.simplertc.test;

import java.util.ArrayList;

import org.appspot.apprtc.VideoStreamsView;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.asb.simplertc.R;
import com.asb.simplertc.SimpleRTC;
import com.asb.simplertc.session.Session;
import com.asb.simplertc.session.Session.OnSessionListener;
import com.asb.simplertc.session.SessionConfig;
import com.asb.simplertc.session.User;
import com.asb.simplertc.session.User.STATUS;
import com.asb.simplertc.utils.SLog;

public class MainActivity extends Activity {
	
	private VideoStreamsView vsv;
	private Session mSession;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initLayout();
	}
	
	private void initLayout() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		Point displaySize = new Point();
		getWindowManager().getDefaultDisplay().getSize(displaySize);
		vsv = new VideoStreamsView(this, displaySize);
		
		setContentView(vsv);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		switch(item.getItemId()) {
		case R.id.action_connect:
			StartSimpleRtc();
			break;
		case R.id.action_about:
			showToast("SimpleRTC for Android TEST");
			break;
		}
		
		return true;
	}
	
	private void StartSimpleRtc() {
		SessionConfig sessionConfig = new SessionConfig();
		sessionConfig.configs.put(SessionConfig.CONFIG_VIDEOVIEW, vsv);
		
		mSession = SimpleRTC.createSession(getApplicationContext(), "Android", "VZueniAypc", sessionConfig);
		mSession.setListener(new OnSessionListener() {
			
			@Override
			public void onRefreshed(ArrayList<User> remoteUserList) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onMessaged() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onError() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onConnected(String sessionId) {
				showToast("Session connected... sessionId:"+sessionId);
				
				mSession.changeStatus(STATUS.STABLE);
			}
			
			@Override
			public void onCallRequested(User remoteUser) {
				showToast("User name:"+remoteUser.mName+" request calling...");
				
				//TODO TEST
				mSession.acceptCall();
			}
			
			@Override
			public void onCallDisconnected() {
				showToast("WebRTC call disconnected...");
			}
			
			@Override
			public void onCallConnected() {
				showToast("WebRTC call connected...");
			}
		});
		
		mSession.connect();
	}
	
	public void showToast(String message) {
		//Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
		SLog.LOGE(message);
	}
}
