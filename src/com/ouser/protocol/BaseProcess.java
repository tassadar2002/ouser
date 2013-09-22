package com.ouser.protocol;

import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;

import com.ouser.logger.Logger;
import com.ouser.net.HttpComm;
import com.ouser.net.HttpResultCallback;
import com.ouser.util.Const;
import com.ouser.util.StringUtil;

/**
 * 服务器协议通信单元基类
 * @author hanlixin
 *
 */
abstract public class BaseProcess {
	
	private final String clazz = getClass().getSimpleName();
	private static final Logger logger = new Logger("protocol");

	/** 获得请求地址 */
	abstract protected String getRequestUrl();
	
	/** 获得 请求时的info参数，派生类实现 */
	abstract protected String getInfoParameter();
	
	/** 通信完成，解析结果，派生类实现 */
	abstract protected void onResult(String result);

	/** 获得测试用假数据 */
	abstract protected String getFakeResult();
	
	/** 通信结果错误码 */
	private ProcessStatus mStatus = ProcessStatus.Success;
	
	public ProcessStatus getStatus() {
		return mStatus;
	}
	
	protected void setStatus(ProcessStatus value) {
		mStatus = value;
	}

	public void run() {
		run(new EmptyResponseListener());
	}
	
	public void run(ResponseListener listener) {
		run(null, listener);
	}
	
	public void run(String requestId, ResponseListener listener) {
		new AsyncComm(requestId, listener).execute();
	}
	
	protected void onCreate() {
	}

	private class AsyncComm extends AsyncTask<Void, Void, Void> {
		
		private String mRequestId = "";
		private ResponseListener mListener = null;
		
		public AsyncComm(String requestId, ResponseListener listener) {
			mRequestId = requestId;
			mListener = listener;
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			onCreate();
			
			String url = getRequestUrl();
			String parameter = getInfoParameter();
			parameter = parameter.replace("\\/", "/");
			logger.d(String.format("send name:%s url:%s param:%s", 
					clazz, url, parameter));
			
			if(StringUtil.isEmpty(url) || parameter == null) {
				mStatus = ProcessStatus.ErrUnkown;
				return null;
			}
			
			if(Const.FakeProtocol) {
				try {
					Thread.sleep(1000);
				} catch(InterruptedException e) {
				}
				onResult(getFakeResult());
				return null;
			}
			
			Map<String, String> postParam = new HashMap<String, String>();
			postParam.put("info", parameter);

			new HttpComm(false).post(url, postParam, true, new HttpResultCallback() {
				
				@Override
				public void onResponse(HttpDownloaderResult success, String url,
						String message) {
					
					if(success == HttpDownloaderResult.eSuccessful) {
						logger.d(String.format("recv name:%s url:%s result:%s", 
								clazz, url, message));
						mStatus = ProcessStatus.Success;
						onResult(message);
					} else {
						logger.d("recv response url:" + url + "; fail");
						mStatus = ProcessStatus.ErrNetDisable;
					}
				}
				
				@Override
				public void onProgress(String url, float rate) {
				}
			});
			return null;
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onPostExecute(Void result) {
			mListener.onResponse(mRequestId);
		}
	}
}
