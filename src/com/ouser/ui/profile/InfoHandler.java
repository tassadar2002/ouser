package com.ouser.ui.profile;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.cache.Cache;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.image.PhotoDownloadCompleteEventArgs;
import com.ouser.image.PhotoManager;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.OuserEventArgs;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.module.ChatId;
import com.ouser.module.ChatIdFactory;
import com.ouser.module.Enums;
import com.ouser.module.Ouser;
import com.ouser.module.Photo;
import com.ouser.ui.component.MenuDialogBuilder;
import com.ouser.ui.helper.ActivitySwitch;
import com.ouser.ui.helper.Alert;
import com.ouser.ui.helper.Formatter;
import com.ouser.ui.ouser.PhotoActivity;
import com.ouser.util.Const;

class InfoHandler extends BaseHandler {

	private static final int MenuBlack = 1;
	private static final int MenuBlackReport = 2;

	/** 是否可以编辑 防止loading中，返回，loading对话框消失，这时进行编辑 */
	private boolean mCanEdit = false;
	
	/** 获得藕丝事件的监听器 */
	private EventListener mGetOuserEventListener = new EventListener() {
		
		@Override
		public void onEvent(EventId id, EventArgs args) {
			OuserEventArgs ouserArgs = (OuserEventArgs) args;
			if(ouserArgs.getOuser().isSame(getOuser())) {
				stopLoading();
				if (ouserArgs.getErrCode() == OperErrorCode.Success) {
					setOuser(ouserArgs.getOuser());
					fill();
				} else {
					Alert.handleErrCode(ouserArgs.getErrCode());
				}
			}
		}
	};

	/** 头像事件的监听器 */
	private EventListener mPhotoEventListener = new EventListener() {

		@Override
		public void onEvent(EventId id, EventArgs args) {
			PhotoDownloadCompleteEventArgs photoArgs = (PhotoDownloadCompleteEventArgs) args;
			if (photoArgs.getPhoto().isSame(getOuser().getPortrait())) {
				if (photoArgs.isSuccess()) {
					ImageView imagePortrait = (ImageView) getActivity().findViewById(
							R.id.image_portrait);
					imagePortrait.setImageBitmap(PhotoManager.self().getBitmap(
							getOuser().getPortrait(), Photo.Size.Normal));
				} else {
					Alert.Toast("下载头像失败");
				}
			}
		}
	};

	/** 行为按钮的监听器 */
	private EventListener mActionEventListener = new EventListener() {

		@Override
		public void onEvent(EventId id, EventArgs args) {
			stopLoading();
			StatusEventArgs statusArgs = (StatusEventArgs) args;
			if (statusArgs.getErrCode() == OperErrorCode.Success) {
				Alert.Toast("操作成功");
				LogicFactory.self().getProfile().get(getOuser().getUid());
				startLoading();
			} else if (statusArgs.getErrCode() == OperErrorCode.InBlack) {
				Alert.Toast("对方在您的黑名单中，请先解除黑名单");
			} else {
				Alert.handleErrCode(statusArgs.getErrCode());
			}
		}
	};

	@Override
	public void onCreate() {

		// about me
		if (isMySelf()) {
			View txtAboutme = getActivity().findViewById(R.id.txt_aboutme);
			txtAboutme.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mCanEdit) {
						Intent intent = new Intent(getActivity(), EditAboutmeActivity.class);
						intent.putExtra("data", getOuser().toBundle());
						getActivity().startActivityForResult(intent, Const.RequestCode.AnyOne);
					}
				}
			});
		} else {
			getActivity().findViewById(R.id.image_right_arrow).setVisibility(View.GONE);
		}

		// portrait
		View imagePortrait = getActivity().findViewById(R.id.image_portrait);
		imagePortrait.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				if (isMySelf()) {
					getHandlerFactory().getNewPhoto().toNewPhoto(RequestFrom.ChangePortait);
				} else {
					black();
				}
				return true;
			}
		});
		imagePortrait.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), PhotoActivity.class);
				intent.putExtra("uid", getOuser().getUid());
				intent.putExtra("select", getOuser().getPortrait().toBundle());
				intent.putExtra("count", 1);
				intent.putExtra("islist", false);
				intent.putExtra("0", getOuser().getPortrait().toBundle());
				getActivity().startActivity(intent);
			}
		});

		// follow button
		View btnFollow = getActivity().findViewById(R.id.btn_follow);
		btnFollow.setVisibility(View.GONE);
		btnFollow.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				LogicFactory.self().getOuser().addFollow(getOuser(), getActivity().createUIEventListener(mActionEventListener));
				startLoading();
			}
		});

		initCountInfo(R.id.layout_publish_appoint, ProfileListActivity.PublishAppoint);
		initCountInfo(R.id.layout_follow_appoint, ProfileListActivity.FollowAppoint);
		initCountInfo(R.id.layout_befollower, ProfileListActivity.BeFollower);
		initCountInfo(R.id.layout_follower, ProfileListActivity.Follower);

		// logic
		LogicFactory.self().getProfile().seeProfile(getOuser().getUid());
		startLoading();

		// 因为存在force get profile，所以这里要一直关注
		getActivity().addUIEventListener(EventId.eGetOuserProfile, mGetOuserEventListener);
		getActivity().addUIEventListener(EventId.ePhotoDownloadComplete, mPhotoEventListener);
	}

	@Override
	public void onActivityResult(int type, int result, Intent intent) {
		LogicFactory.self().getProfile().get(getOuser().getUid());
		startLoading();
	}

	public void clickActionButton() {
		if (isMySelf()) {
			if (mCanEdit) {
				Intent intent = new Intent(getActivity(), EditProfileActivity.class);
				intent.putExtra("data", getOuser().toBundle());
				getActivity().startActivityForResult(intent, Const.RequestCode.AnyOne);
			}
		} else {
			ChatId chatId = ChatIdFactory.create(getOuser().getUid());
			ActivitySwitch.toChatForResult(getActivity(), chatId);
		}
	}
	
	public void onNewPhotoResult(Bitmap bitmap) {
		LogicFactory.self().getProfile().setPortraitImage(bitmap, mActionEventListener);
		startLoading();
	}

	private void initCountInfo(final int layoutId, final int listType) {
		getActivity().findViewById(layoutId).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ProfileListActivity.class);
				intent.putExtra(Const.Intent.Uid, getOuser().getUid());
				intent.putExtra(Const.Intent.Type, listType);
				intent.putExtra(Const.Intent.NickName, getOuser().getNickName());
				getActivity().startActivityForResult(intent, Const.RequestCode.AnyOne);
			}
		});
	}

	/** 填充个人信息 */
	private void fill() {
		mCanEdit = true;

		getActivity().setHeadBarTitle(getOuser().getNickName());

		ImageView imagePortrait = (ImageView) getActivity().findViewById(R.id.image_portrait);
		imagePortrait.setImageBitmap(PhotoManager.self().getBitmap(getOuser().getPortrait(),
				Photo.Size.Normal));

		setTextViewValue(R.id.txt_age, String.valueOf(getOuser().getAge()));
		setTextViewValue(R.id.txt_merry,
				Enums.self().getText(Enums.Type.Merry, getOuser().getMerry()));
		setTextViewValue(R.id.txt_star, getOuser().getStar());
		setTextViewValue(R.id.txt_hobby, getOuser().getHobby());
		setTextViewValue(R.id.txt_level, "等级" + getOuser().getLevel());
		setTextViewValue(R.id.txt_aboutme, getOuser().getAboutme());
		setTextViewValue(R.id.txt_appearance,
				Enums.self().getText(Enums.Type.Height, getOuser().getHeight()) + 
						" " + Enums.self().getText(Enums.Type.Body, getOuser().getBody()));
		setTextViewValue(
				R.id.txt_school,
				getOuser().getSchool() + " "
						+ Enums.self().getText(Enums.Type.Education, getOuser().getEducation()));
		setTextViewValue(R.id.txt_work, getOuser().getCompany() + " " + getOuser().getJobTitle());
		setTextViewValue(R.id.txt_publish_appoint_count,
				String.valueOf(getOuser().getPublishAppointCount()));
		setTextViewValue(R.id.txt_follow_appoint_count,
				String.valueOf(getOuser().getFollowAppointCount()));
		setTextViewValue(R.id.txt_befollower_count, String.valueOf(getOuser().getBeFollowerCount()));
		setTextViewValue(R.id.txt_follower_count, String.valueOf(getOuser().getFollowerCount()));

		ImageView imageGender = (ImageView) getActivity().findViewById(R.id.image_gender);
		imageGender.setImageResource(Formatter.getGenderIcon(getOuser().getGender()));

		View btnFollow = getActivity().findViewById(R.id.btn_follow);
		btnFollow.setVisibility((getOuser().getUid().equals(Cache.self().getMyUid()) || getOuser()
				.isReleation(Ouser.Relation.Follow)) ? View.GONE : View.VISIBLE);
	}

	private void setTextViewValue(int resId, String value) {
		((TextView) getActivity().findViewById(resId)).setText(value);
	}

	private void black() {
		
		List<Pair<Integer, String>> items = new ArrayList<Pair<Integer, String>>();
		items.add(new Pair<Integer, String>(MenuBlack, "拉黑"));
		items.add(new Pair<Integer, String>(MenuBlackReport, "拉黑并举报"));
		
		new MenuDialogBuilder(getActivity())
			.setMenuItem(items, new MenuDialogBuilder.Callback() {
				
				@Override
				public void onClick(int key) {
					switch(key) {
					case MenuBlack:
						LogicFactory.self().getOuser().addBlack(getOuser(), false, mActionEventListener);
						startLoading();
						break;
					case MenuBlackReport:
						LogicFactory.self().getOuser().addBlack(getOuser(), true, mActionEventListener);
						startLoading();
						break;
					}
				}
			})
			.setTop(getActivity().findViewById(R.id.layout_head_bar).getHeight())
			.create().show();
	}
}
