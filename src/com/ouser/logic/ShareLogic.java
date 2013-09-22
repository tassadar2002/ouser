package com.ouser.logic;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.ouser.event.EventListener;
import com.ouser.module.Appoint;
import com.ouser.net.HttpComm;
import com.ouser.net.HttpResultCallback;
import com.ouser.util.Const;
import com.ouser.util.StringUtil;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

public class ShareLogic extends BaseLogic {
	
	static class Factory implements BaseLogic.Factory {

		@Override
		public BaseLogic create() {
			return new ShareLogic();
		}
	}
	
	ShareLogic() {
	}
	
	private enum Type {
		Sms,
		Weixin,
		Weibo,
		None,
	}
	
	private EventListener mCurrentListener = null;
	private Type mCurrentType = Type.None;
	
	private Weixin mWeixin = null;
	private Weixin getWeixin() {
		if(mWeixin == null) {
			mWeixin = new Weixin();
		}
		return mWeixin;
	}
	
	private Sms mSms = null;
	private Sms getSms() {
		if(mSms == null) {
			mSms = new Sms();
		}
		return mSms;
	}
	
	
	public boolean isShareToWeixin() {
		SharedPreferences sp = Const.Application.getSharedPreferences("setting", Context.MODE_PRIVATE);
		return sp.getBoolean("share_to_weixin", true);
	}
	
	public void setShareToWeixin(boolean value) {
		SharedPreferences sp = Const.Application.getSharedPreferences("setting", Context.MODE_PRIVATE);
		sp.edit().putBoolean("share_to_weixin", value).commit();
	}
	
	public void shareToSms(final Appoint appoint, final EventListener listener) {
		mCurrentType = Type.Sms;
		mCurrentListener = listener;
		getInvitString(appoint);
	}
	
	public void shareToWeixin(final Appoint appoint, final EventListener listener) {
		mCurrentType = Type.Weixin;
		mCurrentListener = listener;
		getInvitString(appoint);
	}
	
	// TODO 错误码
	public void completeShare(boolean success) {
		if(mCurrentListener != null) {
			fireStatusEvent(mCurrentListener, success ? OperErrorCode.Success : OperErrorCode.Unknown);
		}
		mCurrentListener = null;
		mCurrentType = Type.None;
	}
	
	public void handleIntent(Intent intent, IWXAPIEventHandler handler) {
		getWeixin().handleIntent(intent, handler);
	}
	
	private void handleFetchShareString(String content) {
		if(StringUtil.isEmpty(content)) {
			if(mCurrentListener != null) {
				fireStatusEvent(mCurrentListener, OperErrorCode.Unknown);
			}
			mCurrentListener = null;
			mCurrentType = Type.None;
		} else {
			switch(mCurrentType) {
			case Sms:
				getSms().share(content);
				completeShare(true);
				break;
			case Weixin:
				getWeixin().share(content);
				break;
			default:
				break;
			}
		}
	}
	

	/**
	 * 获得短信邀请的内容
	 * @param appoint
	 * @param listener
	 * @remark http://126.am/client/api_register_new.jsp
	 */
	private void getInvitString(final Appoint appoint) {
		String url = String.format("http://app.zhengre.com/jsp/location.jsp?owner=%s&desireid=%s", 
				appoint.getAppointId().getUid(), appoint.getAppointId().getAid());
		Map<String, String> messages = new HashMap<String, String>();
		messages.put("key", "5ffa4afddee04932a4922848e6eb04d3");
		messages.put("longUrl", url);
		
		HttpComm httpComm = new HttpComm();
		httpComm.post("http://126.am/api!shorten.action", messages, true, new HttpResultCallback() {

			@Override
			public void onResponse(HttpDownloaderResult result, String url, String message) {
				String text = null;
				if(result == HttpDownloaderResult.eSuccessful) {
					try {
						JSONObject o = new JSONObject(message);
						if(o.optInt("status_code") == 200) {
							if(StringUtil.isEmpty(appoint.getPlace())) {
								text = String.format("%s，快来参加吧！地点：http://%s 【用藕丝儿，交朋友，约生活】", 
									appoint.getContent(), 
									o.optString("url"));
							} else {
								text = String.format("%s，快来参加吧！地点：%s，参加地图：http://%s 【用藕丝儿，交朋友，约生活】", 
										appoint.getContent(), 
										appoint.getPlace(), 
										o.optString("url"));
							}
						}
					} catch(JSONException e) {
						e.printStackTrace();
					}
				}
				handleFetchShareString(text);
			}

			@Override
			public void onProgress(String url, float rate) {
			}
		});
	}
	
	private class Sms {
		public void share(String content) {
			Uri uri = Uri.parse("smsto:");
			Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
			intent.putExtra("sms_body", content);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Const.Application.startActivity(intent);
		}
	}
	
	/**
	 * 微信分享
	 * @author Administrator
	 *
	 */
	private class Weixin {

		private static final String AppId = "wxcc211681962cff1a";
		private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
		
		private IWXAPI mApi = null;
		
		public boolean share(String content) {
			if(!register()) {
				return false;
			}
			
			WXTextObject obj = new WXTextObject();
			obj.text = content;
			
			WXMediaMessage msg = new WXMediaMessage();
			msg.mediaObject = obj;
			msg.description = content;
			
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = String.valueOf(System.currentTimeMillis());
			req.scene = SendMessageToWX.Req.WXSceneTimeline;
			req.message = msg;
			
			return mApi.sendReq(req);
		}

		public void handleIntent(Intent intent, IWXAPIEventHandler handler) {
			mApi.handleIntent(intent, handler);
		}
		
		private boolean register() {
			if(mApi == null) {
				mApi = WXAPIFactory.createWXAPI(Const.Application, AppId, false);
				if(mApi == null) {
					return false;
				}
			}
			
			if(!mApi.registerApp(AppId)) {
				return false;
			}
			
			int wxSdkVersion = mApi.getWXAppSupportAPI();
			if (wxSdkVersion < TIMELINE_SUPPORTED_VERSION) {
				return false;
			}
			return true;
		}
	}
	
}
