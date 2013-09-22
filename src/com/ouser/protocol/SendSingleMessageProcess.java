package com.ouser.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class SendSingleMessageProcess extends SendMessageBaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/SendChatServlet_Android_V2_0";

	@Override
	protected String getRequestUrl() {
		return URL;
	}

	@Override
	protected void fillOwnerParam(JSONObject o) throws JSONException {
		o.put("dest", mMessage.getChatId().getSingleId());
	}
}
