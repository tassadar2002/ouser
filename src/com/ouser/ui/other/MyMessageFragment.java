package com.ouser.ui.other;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.cache.Cache;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.image.PhotoManager;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.ListMessagesEventArgs;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.module.Appoint;
import com.ouser.module.ListMessage;
import com.ouser.module.Photo;
import com.ouser.ui.appoint.AppointValidChecker;
import com.ouser.ui.base.BaseListFragment;
import com.ouser.ui.component.HeadBar;
import com.ouser.ui.component.MenuDialogBuilder;
import com.ouser.ui.helper.ActivitySwitch;
import com.ouser.ui.helper.AudioHandler;
import com.ouser.ui.helper.Formatter;
import com.ouser.ui.topframework.TopFragment;
import com.ouser.ui.topframework.TopFragmentFactory;
import com.ouser.ui.topframework.TopFragmentType;
import com.ouser.ui.widget.EmotionTextView;
import com.ouser.util.Const;
import com.readystatesoftware.viewbadger.BadgeView;

public class MyMessageFragment extends TopFragment {

	public static class Factory implements TopFragmentFactory {

		@Override
		public TopFragment create() {
			return new MyMessageFragment();
		}
	}

	private List<ListMessage> mItems = new ArrayList<ListMessage>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.actvy_frgmt_my_message, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getHeadbar().setTitle("我的消息");
		getHeadbar().setActionButton(R.drawable.title_more, new HeadBar.OnActionListener() {
			
			@Override
			public void onClick() {
				showMenu();
			}
		});
	}

	@Override
	public void onSaveState() {
		super.onSaveState();
		Cache.self().setTopFragmentData(TopFragmentType.MyMessage, mItems);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onRestoreState() {
		super.onRestoreState();
		List<ListMessage> value = (List<ListMessage>) Cache.self().getTopFragmentData(
				TopFragmentType.MyMessage);
		if (value != null) {
			mItems = value;
		}
	}

	@Override
	public void syncInitData(Bundle bundle) {
		super.syncInitData(bundle);

		MyMessageListFragment fragment = new MyMessageListFragment();
		replaceFragment(R.id.layout_my_message_list, fragment);
		fragment.asyncInitData();
	}

	@Override
	public boolean onFragmentActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == Const.RequestCode.Chat) {
			// 不刷新，等待eMessageListChanged事件
		}
		return false;
	}
	
	private void showMenu() {
		final boolean isSpeakerPhone = AudioHandler.isSpeakerPhone();
		
		List<Pair<Integer, String>> items = new ArrayList<Pair<Integer, String>>();
		items.add(new Pair<Integer, String>(0, isSpeakerPhone ? "听筒模式" : "扬声器模式"));
		items.add(new Pair<Integer, String>(1, "忽略全部未读消息"));
		
		new MenuDialogBuilder(getActivity())
		.setMenuItem(items, new MenuDialogBuilder.Callback() {
			
			@Override
			public void onClick(int key) {
				switch(key) {
				case 0:
					AudioHandler.setSpeakerPhone(!isSpeakerPhone);
					break;
				case 1:
					LogicFactory.self().getChat().removeMyMessages(
							createUIEventListener(new EventListener() {
								
								@Override
								public void onEvent(EventId id, EventArgs args) {
									stopLoading();
									StatusEventArgs statusArgs = (StatusEventArgs)args;
									if(statusArgs.getErrCode() == OperErrorCode.Success) {
										((BaseListFragment)findFragment(R.id.layout_my_message_list)).asyncInitData();
									}
								}
							}));
					startLoading();
					break;
				}
			}
		})
		.setTop(getActivity().findViewById(R.id.layout_head_bar).getHeight())
		.create().show();
	}

	@SuppressLint("ValidFragment")
	private class MyMessageListFragment extends BaseListFragment {

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			observePhotoDownloadEvent(true);
			enableAppend(false);
			enableEdit(true);
			
			addUIEventListener(EventId.eMessageListChanged, new EventListener() {
				
				@Override
				public void onEvent(EventId id, EventArgs args) {
					LogicFactory.self().getChat().getMyMessageFromLocal(getMainEventListener());
					startLoading();
				}
			});
		}

		@Override
		public void refreshData(boolean append) {
			LogicFactory.self().getChat().getMyMessageFromRemote(getMainEventListener());
		}

		@Override
		protected int getItemCount() {
			return mItems.size();
		}

		@Override
		protected boolean handleMainEvent(StatusEventArgs statusArgs, boolean append) {
			ListMessagesEventArgs listArgs = (ListMessagesEventArgs) statusArgs;
			if (append) {
				mItems.addAll(listArgs.getListMessages());
			} else {
				mItems = listArgs.getListMessages();
			}
			return listArgs.getListMessages().isEmpty();
		}

		@Override
		protected void onClickItem(int index) {
			final ListMessage message = mItems.get(index);
			if(mItems.get(index).isSingle()) {
				ActivitySwitch.toChatForResult(getActivity(), message.getChatId());
			} else {
				AppointValidChecker.check(message.getAppoint(), 
						getFragment(), new AppointValidChecker.OnValidListener() {
					
					@Override
					public void onValid(Appoint appoint) {
						ActivitySwitch.toGroupChatForResult(
								getActivity(), message.getChatId(), appoint.getContent());
					}
				});
			}
		}

		@Override
		protected String getEditText(int index) {
			return "确认删除？";
		}

		@Override
		protected void onEdit(int index) {
			ListMessage message = mItems.get(index);
			LogicFactory.self().getChat().removeMyMessage(message);
			new Handler().post(new Runnable() {
				
				@Override
				public void run() {
					mActionListener.onEditingComplete();
				}
			});
			mItems.remove(index);
		}

		@Override
		public View getItemView(int position, View convertView) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.lvitem_my_message, null);
				holder.imagePortrait = (ImageView) convertView.findViewById(R.id.image_portrait);
				holder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
				holder.txtContent = (EmotionTextView) convertView.findViewById(R.id.txt_content);
				holder.txtTime = (TextView) convertView.findViewById(R.id.txt_time);
				holder.badgeCount = new BadgeView(getActivity(), holder.imagePortrait);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ListMessage message = mItems.get(position);
			holder.imagePortrait.setImageBitmap(PhotoManager.self().getBitmap(
					message.getOuser().getPortrait(), Photo.Size.Large));
			holder.imagePortrait.setOnClickListener(
					ActivitySwitch.getToProfileClickListener(getActivity(), message.getOuser().getUid()));
			if(message.isSingle()) {
				holder.txtName.setText(message.getOuser().getNickName());
				holder.txtContent.setText(message.getContent());
			} else {
				holder.txtName.setText(message.getAppoint().getContent());
				holder.txtContent.setText(
						message.getOuser().getNickName() + "说：" + message.getContent());
			}

			holder.txtTime.setText(Formatter.formatCurrentSecondToInterval(message.getTime()));

			if (message.getCount() == 0) {
				holder.badgeCount.setVisibility(View.GONE);
			} else {
				holder.badgeCount.setVisibility(View.VISIBLE);
				holder.badgeCount.setText(String.valueOf(message.getCount()));
				holder.badgeCount.setTextSize(8);
				holder.badgeCount.show();
			}

			return convertView;
		}

		private class ViewHolder {
			public ImageView imagePortrait = null;
			public TextView txtName = null;
			public EmotionTextView txtContent = null;
			public TextView txtTime = null;
			public BadgeView badgeCount = null;
		}
	}
}
