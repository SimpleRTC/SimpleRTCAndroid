package com.asb.restapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public abstract class AbstractJsonJob implements Runnable {
	protected OnJsonJobListener mCallback = null;
	protected String mJsonUrl = null;
	protected JSONObject mData = null;
	
	public AbstractJsonJob(String url) {
		mJsonUrl = url;
	}
	
	public void setListener(OnJsonJobListener listener) {
		mCallback = listener;
	}
	
	//TODO 각 HTTP 메서드(GET, POST...)등에 대한 예외처리 추가 필요
	protected String getRawJsonString() {
		String rawString = null;
		
		try {
			Log.e("jphong", "[REST]C->S:"+mData.toString());
			
			//POST param
			ArrayList<NameValuePair> postData = new ArrayList<NameValuePair>();
			postData.add(new BasicNameValuePair("data", mData.toString()));
			
			//HTTP Client 생성
			HttpClient client = new DefaultHttpClient();
			
			//HTTP POST 생성
			HttpPost post = new HttpPost(mJsonUrl);
			
			//
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postData, "UTF-8");
			post.setEntity(entity);
			
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();
			
			if(resEntity != null){
				String result = EntityUtils.toString(resEntity);
				Log.e("jphong", "[REST]S->C: API="+mJsonUrl+", Response="+result);
				
				rawString = result;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rawString;
	}
	
	protected void setData(String name, Object value) {
		try {
			
			if(mData == null)
				mData = new JSONObject();
			
			mData.put(name, value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
