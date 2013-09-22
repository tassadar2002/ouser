package com.ouser.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

import com.ouser.net.HttpResultCallback.HttpDownloaderResult;

public class HttpComm {

	/** 访问地址 */
	private String mUrl = "";
	
	/** 选择异步还是同步 */
	private boolean mAsync = true;
	
	/** 网络回调 */
	private HttpResultCallback mCallback = null;
	
	public HttpComm() {
		
	}
	
	public HttpComm(boolean async) {
		mAsync = async;
	}

	public void get(String url, HttpResultCallback callback) {
		this.mUrl = url;
		this.mCallback = callback;
		
		HttpGet request = new HttpGet(url);
		
		if(mAsync) {
			new HttpAsyncTask().execute(request);
		} else {
			postEvent(work(request));
		}
	}

	/**
	 * 
	 * @param url
	 * @param messages
	 * @param callback
	 * @param encode 是否转码
	 */
	public void post(String url, Map<String, String> messages, boolean encode, HttpResultCallback callback) {
		this.mUrl = url;
		this.mCallback = callback;
		
		HttpPost request = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		if(messages != null) {
			for(Map.Entry<String, String> pair: messages.entrySet()) {
				params.add(new BasicNameValuePair(pair.getKey(), pair.getValue()));
			}
		}
		
		if(encode) {
			try {
				// 转码
				HttpEntity charset = new UrlEncodedFormEntity(params, "utf8");
				request.setEntity(charset);
				
			} catch(Exception e) {
				this.postEvent(null);
				return;
			}
		}

		if(mAsync) {
			new HttpAsyncTask().execute(request);
		} else {
			postEvent(work(request));
		}
	}
	
	private void postEvent(String result) {
		if(this.mCallback != null) {
			this.mCallback.onResponse(result != null ? HttpDownloaderResult.eSuccessful : HttpDownloaderResult.eNone, this.mUrl, result);
		}
	}
	
	private String work(HttpUriRequest request) {
		try{
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(request);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return EntityUtils.toString(response.getEntity(), "UTF-8");
			}
			
		} catch(ClientProtocolException e) {
			//e.printStackTrace();
		} catch(IOException e) {
			//e.printStackTrace();
		} catch(Exception e) {
			//e.printStackTrace();
		}
		return null;
	}

	private class HttpAsyncTask extends AsyncTask<HttpUriRequest, Void, String> {

		@Override
		protected String doInBackground(HttpUriRequest... requests) {
			return work(requests[0]);
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onPostExecute(String result) {
			postEvent(result);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}
}
