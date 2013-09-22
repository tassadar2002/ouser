package com.ouser.protocol;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.util.UrlUtil;

/**
 * 获得友约热门标签
 * @author hanlixin
 *
 */
public class GetAppointCloudTagProcess extends BaseProcess {
	
	private static final String URL = "http://app.zhengre.com/servlet/GetHotTabListServlet_Android_V2_0";
	
	private List<String> mResult = new ArrayList<String>();

	public List<String> getResult() {
		return mResult;
	}

	@Override
	protected String getRequestUrl() {
		return URL;
	}

	@Override
	protected String getInfoParameter() {
		return "";
	}

	@Override
	protected void onResult(String result) {
		try {
			List<String> tags = new ArrayList<String>();
			JSONArray array = new JSONArray(result);
			for(int i = 0; i < array.length(); ++i) {
				JSONObject o = array.optJSONObject(i);
				tags.add(UrlUtil.decode(o.optString("tab")));
			}
			mResult = tags;
		} catch(JSONException e) {
			e.printStackTrace();
			setStatus(ProcessStatus.ErrUnkown);
		}
	}

	@Override
	protected String getFakeResult() {
		try {
			JSONArray array = new JSONArray();
			for(int i = 0; i < 10; ++i){
				JSONObject o = new JSONObject();
				o.put("tab", UrlUtil.encode("喝酒"));
				array.put(o);
			}
			return array.toString();
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return "";
	}

}
