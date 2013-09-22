package com.ouser.stat;

import com.ouser.util.Const;
import com.umeng.analytics.MobclickAgent;

public class Stat {

	public static void onEvent(StatId id) {
		
		switch(id) {
		case SingleChatImage:
		case SingleChatLocation:
		case SingleChatText:
		case SingleChatVoice:
			MobclickAgent.onEvent(Const.Application, "SINGLE_CHAT", id.getValue());
			break;
		case GroupChatImage:
		case GroupChatLocation:
		case GroupChatText:
		case GroupChatVoice:
			MobclickAgent.onEvent(Const.Application, "GROUP_CHAT", id.getValue());
			break;
		default:
			MobclickAgent.onEvent(Const.Application, id.getValue());
			break;
		}
	}
}
