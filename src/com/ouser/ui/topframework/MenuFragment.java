package com.ouser.ui.topframework;

import android.os.Bundle;
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
import com.ouser.image.PhotoDownloadCompleteEventArgs;
import com.ouser.image.PhotoManager;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.OuserEventArgs;
import com.ouser.module.Ouser;
import com.ouser.module.Photo;
import com.ouser.ui.base.BaseFragment;
import com.ouser.ui.topframework.MenuItemCreator.MenuItem;
import com.readystatesoftware.viewbadger.BadgeView;

public class MenuFragment extends BaseFragment {

	private OnMenuSelectedListener mListener = null;

	private BadgeView mMessageCount = null;
	private BadgeView mTimelineCount = null;

	private EventListener mEventListener = new EventListener() {

		@Override
		public void onEvent(EventId id, EventArgs args) {
			if (id == EventId.eGetOuserProfile) {

				OuserEventArgs ouserArgs = (OuserEventArgs) args;
				if (ouserArgs.getOuser().getUid().equals(Cache.self().getMyUid())) {
					if (ouserArgs.getErrCode() == OperErrorCode.Success) {
						setMyself();
					}
				}

			} else if (id == EventId.ePhotoDownloadComplete) {

				Photo myPortrait = Cache.self().getMySelfOuser().getPortrait();
				PhotoDownloadCompleteEventArgs photoArgs = (PhotoDownloadCompleteEventArgs) args;

				// 是菜单所需的头像
				if (photoArgs.getPhoto().isSame(myPortrait)
						&& photoArgs.getSize() == Photo.Size.Small) {
					if (photoArgs.isSuccess()) {
						ImageView imagePortrait = (ImageView) getView().findViewById(
								R.id.image_portrait);
						imagePortrait.setImageBitmap(PhotoManager.self().getBitmap(
								photoArgs.getPhoto(), photoArgs.getSize()));
					}
				}
			} else if (id == EventId.ePushMessageAndTimelineCount) {
				setBadgeCount(TopFragmentType.MyMessage);
				setBadgeCount(TopFragmentType.Timeline);
			}
		}
	};

	public void setListener(OnMenuSelectedListener listener) {
		mListener = listener;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.sliding_menu, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// 头像
		Ouser mySelf = Cache.self().getMySelfOuser();
		if (mySelf != null) {
			setMyself();
		} else {
			LogicFactory.self().getProfile().get(Cache.self().getMyUid());
		}

		View view = getView().findViewById(R.id.layout_menu_head);
		view.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mListener.onHeaderClick();
			}
		});

		for (MenuItemCreator.MenuItem item : MenuItemCreator.self().getItems()) {
			getView().findViewById(item.getLayoutId()).setOnClickListener(
					new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							if (mListener != null) {
								MenuItem item = MenuItemCreator.self().getItem(v.getId());
								TopFragment fragment = TopFragmentCreator.self().getFragment(
										item.getType());
								mListener.onSelected(fragment);
							}
						}
					});
		}

		mMessageCount = new BadgeView(getActivity(), getView()
				.findViewById(R.id.menu_message_count));
		mTimelineCount = new BadgeView(getActivity(), getView().findViewById(
				R.id.menu_timeline_count));
		setBadgeCount(TopFragmentType.MyMessage);
		setBadgeCount(TopFragmentType.Timeline);

		addUIEventListener(EventId.eGetOuserProfile, mEventListener);
		addUIEventListener(EventId.ePhotoDownloadComplete, mEventListener);
		addUIEventListener(EventId.ePushMessageAndTimelineCount, mEventListener);
	}

	private void setMyself() {
		Ouser mySelf = Cache.self().getMySelfOuser();

		ImageView imagePortrait = (ImageView) getView().findViewById(R.id.image_portrait);
		imagePortrait.setImageBitmap(PhotoManager.self().getBitmap(mySelf.getPortrait(),
				Photo.Size.Small));

		TextView txtName = (TextView) getView().findViewById(R.id.txt_name);
		txtName.setText(mySelf.getNickName());
	}

	private void setBadgeCount(TopFragmentType type) {
		int count = 0;
		BadgeView view = null;
		if (type == TopFragmentType.MyMessage) {
			count = Cache.self().getUnReadedCount();
			view = mMessageCount;
		} else {
			count = Cache.self().getTimelineCount();
			view = mTimelineCount;
		}

		if (count == 0) {
			view.setVisibility(View.GONE);
		} else {
			view.setVisibility(View.VISIBLE);
			view.setText(String.valueOf(count));
			view.setTextSize(10);
			view.setBadgePosition(BadgeView.POSITION_TOP_LEFT);
			view.show();
		}
	}
}
