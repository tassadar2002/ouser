package com.ouser.ui.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.ouser.module.Appoint;
import com.ouser.module.AppointId;
import com.ouser.module.Appoints;
import com.ouser.module.ChatId;
import com.ouser.module.ChatIdFactory;
import com.ouser.module.Ouser;
import com.ouser.ui.appoint.AppointDetailActivity;
import com.ouser.ui.appoint.PublishAppointActivity;
import com.ouser.ui.chat.ChatActivity;
import com.ouser.ui.profile.ProfileActivity;
import com.ouser.util.Const;

public class ActivitySwitch {

	public static void toAppointDetailForResult(Activity activity, Appoints appoints, AppointId appointId, String publisherUid) {
		Intent intent = new Intent(activity, AppointDetailActivity.class);
		intent.putExtra("focus", appointId.toBundle());
		intent.putExtra(Const.Intent.PublisherUid, publisherUid);
		intent.putExtra("appoints", appoints.toBundle());
		activity.startActivityForResult(intent, Const.RequestCode.AppointDetail);
	}
	
	public static void toAppointDetail(Activity activity, Appoints appoints) {
		Intent intent = new Intent(activity, AppointDetailActivity.class);
		intent.putExtra("focus", appoints.get(0).getAppointId().toBundle());
		intent.putExtra(Const.Intent.PublisherUid, appoints.get(0).getAppointId().getUid());
		intent.putExtra("appoints", appoints.toBundle());
		activity.startActivity(intent);
	}
	
	public static void toAppointDetailForResult(Activity activity, Appoints appoints, AppointId appointId) {
		toAppointDetailForResult(activity, appoints, appointId, appointId.getUid());
	}
	
	public static void toAppointDetailForResult(Activity activity, Appoint appoint) {
		Appoints appoints = new Appoints();
		appoints.add(appoint);
		toAppointDetailForResult(activity, appoints, appoint.getAppointId(), appoint.getAppointId().getUid());
	}
	
	public static void toProfile(Context context, Ouser ouser) {
		toProfile(context, ouser.getUid());
	}
	
	public static void toProfile(Context context, String uid) {
		Intent intent = new Intent(context, ProfileActivity.class);
		intent.putExtra("uid", uid);
		context.startActivity(intent);
	}

	public static void toChatForResult(Activity activity, ChatId chatId) {
		Intent intent = new Intent(activity, ChatActivity.class);
		intent.putExtra(Const.Intent.ChatId, ChatIdFactory.toBundle(chatId));
		activity.startActivityForResult(intent, Const.RequestCode.Chat);
	}
	
	public static void toGroupChatForResult(Activity activity, ChatId chatId, String appointContent) {
		Intent intent = new Intent(activity, ChatActivity.class);
		intent.putExtra(Const.Intent.ChatId, ChatIdFactory.toBundle(chatId));
		intent.putExtra(Const.Intent.AppointContent, appointContent);
		activity.startActivityForResult(intent, Const.RequestCode.Chat);
	}
	
	public static void toPublishAppointForResult(Activity activity) {
		activity.startActivityForResult(
				new Intent(activity, PublishAppointActivity.class), 
				Const.RequestCode.PublishAppoint);
	}
	
	public static void toPublishAppoint(Activity activity) {
		activity.startActivity(new Intent(activity, PublishAppointActivity.class));
	}
	
	/**
	 * 获得前往profile页面的click listener
	 * @param activity
	 * @param uid
	 * @return
	 */
	public static View.OnClickListener getToProfileClickListener(Activity activity, String uid) {
		return new ToProfileClickListener(activity, uid);
	}
	
	private static class ToProfileClickListener implements View.OnClickListener {
		
		private Activity mActivity = null;
		private String mUid = "";
		public ToProfileClickListener(Activity activity, String uid) {
			mActivity = activity;
			mUid = uid;
		}

		@Override
		public void onClick(View v) {
			toProfile(mActivity, mUid);
		}
	}
}
