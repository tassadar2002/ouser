package com.ouser.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.AppointId;

public class SendGroupMessageProcess extends SendMessageBaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/SendChatToGroupServlet_Android_V2_0";

	@Override
	protected String getRequestUrl() {
		return URL;
	}

	@Override
	protected void fillOwnerParam(JSONObject o) throws JSONException {
		AppointId appointId = mMessage.getChatId().getGroupId();
		o.put("owner", appointId.getUid());
		o.put("desireid", appointId.getAid());
	}
}
