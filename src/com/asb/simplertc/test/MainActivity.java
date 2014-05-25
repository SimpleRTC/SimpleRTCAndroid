package com.asb.simplertc.test;

import java.net.URI;
import java.net.URISyntaxException;

import org.appspot.apprtc.VideoStreamsView;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRenderer.I420Frame;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import android.app.Activity;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;

import com.asb.simplertc.R;
import com.asb.simplertc.session.Session;
import com.asb.simplertc.signaling.Channel;

public class MainActivity extends Activity {
	
	private MediaConstraints mSdpConstraints;
	
	PeerConnectionFactory mFactory;
	PeerConnection mPc;
	
	private VideoStreamsView vsv;
	private VideoSource mVideoSource;
	private VideoTrack mVideoTrack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		//initWebRTC();
		//getUserMedia();
		
		Session session = new Session("tester2", "hongjunpyo", null);
		session.connect();
	}
	
	private void initWebRTC() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		Point displaySize = new Point();
		getWindowManager().getDefaultDisplay().getSize(displaySize);
		vsv = new VideoStreamsView(this, displaySize);
		
		setContentView(vsv);
		
		Log.e("jphong", "Android global initialized:"+PeerConnectionFactory.initializeAndroidGlobals(this, true, true));
		
		Log.e("jphong", "PeerConnectionFactory is creating...");
		mFactory = new PeerConnectionFactory();
		Log.e("jphong", "PeerConnectionFactory was created!");
		
		mSdpConstraints = new MediaConstraints();
		//mSdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
		
		AudioManager audioManager = ((AudioManager) getSystemService(AUDIO_SERVICE));
		// TODO(fischman): figure out how to do this Right(tm) and remove the
		// suppression.
		@SuppressWarnings("deprecation")
		boolean isWiredHeadsetOn = audioManager.isWiredHeadsetOn();
		audioManager.setMode(isWiredHeadsetOn ? AudioManager.MODE_IN_CALL : AudioManager.MODE_IN_COMMUNICATION);
		audioManager.setSpeakerphoneOn(!isWiredHeadsetOn);
	}
	
	private void getUserMedia() {
		MediaStream lMS = mFactory.createLocalMediaStream("ARDAMS");
		
		//get VideoCapturer
		VideoCapturer capturer = getVideoCapturer();
		mVideoSource = mFactory.createVideoSource(capturer, mSdpConstraints);
		mVideoTrack = mFactory.createVideoTrack("ARDAMSv0", mVideoSource);
		mVideoTrack.addRenderer(new VideoRenderer(new VideoCallbacks(vsv, VideoStreamsView.Endpoint.LOCAL)));
		lMS.addTrack(mVideoTrack);
		
		lMS.addTrack(mFactory.createAudioTrack("ARDAMSa0", mFactory.createAudioSource(mSdpConstraints)));
	}
	
	private VideoCapturer getVideoCapturer() {
		String[] cameraFacing = { "front", "back" };
		int[] cameraIndex = { 0, 1 };
		int[] cameraOrientation = { 0, 90, 180, 270 };
		for (String facing : cameraFacing) {
			for (int index : cameraIndex) {
				for (int orientation : cameraOrientation) {
					String name = "Camera " + index + ", Facing " + facing + ", Orientation " + orientation;
					VideoCapturer capturer = VideoCapturer.create(name);
					if (capturer != null) {
						return capturer;
					}
				}
			}
		}
		throw new RuntimeException("Failed to open capturer");
	}
	
	// Implementation detail: bridge the VideoRenderer.Callbacks interface to
	// the
	// VideoStreamsView implementation.
	private class VideoCallbacks implements VideoRenderer.Callbacks {
		private final VideoStreamsView view;
		private final VideoStreamsView.Endpoint stream;

		public VideoCallbacks(VideoStreamsView view, VideoStreamsView.Endpoint stream) {
			this.view = view;
			this.stream = stream;
		}

		@Override
		public void setSize(final int width, final int height) {
			view.queueEvent(new Runnable() {
				public void run() {
					view.setSize(stream, width, height);
				}
			});
		}

		@Override
		public void renderFrame(I420Frame frame) {
			view.queueFrame(stream, frame);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
