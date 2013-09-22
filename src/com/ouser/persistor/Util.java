package com.ouser.persistor;

import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.ChatMessage;
import com.ouser.module.Timeline;
import com.ouser.module.Timeline.Type;

class Util {
	
	/////////////////////common type/////////////////////
	public static boolean convertToBoolean(int value) {
		return value == 0;
	}

	public static int convertFromBoolean(boolean value) {
		return value ? 0 : 1;
	}

	/////////////////////chat type/////////////////////
	public static int convertFromMessageType(ChatMessage.Type value) {
		switch (value) {
		case Text:
			return 1;
		case Image:
			return 2;
		case Location:
			return 3;
		case Audio:
			return 4;
		case Invite:
			return 5;
		default:
			return 0;
		}
	}

	public static ChatMessage.Type convertToMessageType(int value) {
		switch (value) {
		case 1:
			return ChatMessage.Type.Text;
		case 2:
			return ChatMessage.Type.Image;
		case 3:
			return ChatMessage.Type.Location;
		case 4:
			return ChatMessage.Type.Audio;
		case 5:
			return ChatMessage.Type.Invite;
		default:
			return ChatMessage.Type.Text;
		}
	}

	/////////////////////chat content/////////////////////
	public static String convertToContent(ChatMessage message) {
		switch (message.getType()) {
		case Text:
		case Image:
		case Audio:
			return message.getContent();
		case Location: {
			try {
				JSONObject o = new JSONObject();
				o.put("lat", message.getLocation().getLocation().getLat());
				o.put("lng", message.getLocation().getLocation().getLng());
				o.put("pla", message.getLocation().getPlace());
				return o.toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		case Invite: {
			try {
				JSONObject o = new JSONObject();
				o.put("con", message.getInvite().getContent());
				o.put("pub", message.getInvite().getAppointId().getUid());
				o.put("aid", message.getInvite().getAppointId().getAid());
				return o.toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		}
		return "";
	}
	
	public static void convertFromContent(ChatMessage message, String content) {
		switch (message.getType()) {
		case Text:
		case Image:
		case Audio:
			message.setContent(content);
			break;
		case Location: {
			try {
				JSONObject o = new JSONObject(content);
				message.getLocation().getLocation().setValue(o.optDouble("lat"), o.optDouble("lng"));
				message.getLocation().setPlace(o.optString("pla"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		}
		case Invite: {
			try {
				JSONObject o = new JSONObject(content);
				message.getInvite().setContent(o.optString("con"));
				message.getInvite().getAppointId().setUid(o.optString("pub"));
				message.getInvite().getAppointId().setAid(o.optString("aid"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		}
		}
	}
	
	/////////////////////time line type/////////////////////
	public static int convertFromTimelineType(Timeline.Type value) {
		switch(value) {
		case eOuser:
			return 1;
		case eAppoint:
			return 2;
		default:
			return 0;
		}
	}
	
	public static Timeline.Type convertToTimelineType(int value) {
		switch(value) {
		case 1:
			return Type.eOuser;
		case 2:
			return Type.eAppoint;
		default:
			return Type.eNone;
		}
	}
}
