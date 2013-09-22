package com.ouser.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.ChatIdFactory;
import com.ouser.module.ChatMessage;

public class GetSingleMessageProcess extends GetMessageBaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/GetChatServlet_Android_V2_0";

	private String mTargetUid = "";

	public void setTargetUid(String value) {
		mTargetUid = value;
	}

	@Override
	protected String getRequestUrl() {
		return URL;
	}

	@Override
	protected void fillOwnerParam(JSONObject o) throws JSONException {
		o.put("from", mTargetUid);
	}

	@Override
	protected void fillOwnerResult(JSONObject o, ChatMessage message) throws JSONException {
		
		// 设置说话人
		message.getOuser().setUid(mTargetUid);
		
		// 设置chatid
		message.setChatId(ChatIdFactory.create(mTargetUid));
	}
}
