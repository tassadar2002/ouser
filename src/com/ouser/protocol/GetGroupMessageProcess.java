package com.ouser.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.AppointId;
import com.ouser.module.ChatIdFactory;
import com.ouser.module.ChatMessage;

public class GetGroupMessageProcess extends GetMessageBaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/GetChatFromGroupServlet_Android_V2_0";

	private AppointId mAppointId = null;
	public void setAppointId(AppointId value) {
		mAppointId = value;
	}

	@Override
	protected String getRequestUrl() {
		return URL;
	}

	@Override
	protected void fillOwnerParam(JSONObject o) throws JSONException {
		o.put("owner", mAppointId.getUid());
		o.put("desireid", mAppointId.getAid());
	}

	@Override
	protected void fillOwnerResult(JSONObject o, ChatMessage message) throws JSONException {
		
		// 设置说话人
		message.getOuser().setUid(o.optString("from"));
		
		// 设置chatid
		message.setChatId(ChatIdFactory.create(mAppointId));
	}
}
