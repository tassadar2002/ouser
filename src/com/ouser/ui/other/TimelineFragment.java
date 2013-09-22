package com.ouser.ui.other;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.cache.Cache;
import com.ouser.image.PhotoManager;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.logic.event.TimelinesEventArgs;
import com.ouser.module.Appoint;
import com.ouser.module.Photo;
import com.ouser.module.Timeline;
import com.ouser.ui.appoint.AppointValidChecker;
import com.ouser.ui.base.BaseListFragment;
import com.ouser.ui.helper.ActivitySwitch;
import com.ouser.ui.helper.Formatter;
import com.ouser.ui.topframework.TopFragment;
import com.ouser.ui.topframework.TopFragmentFactory;
import com.ouser.ui.topframework.TopFragmentType;

public class TimelineFragment extends TopFragment {

	public static class Factory implements TopFragmentFactory {

		@Override
		public TopFragment create() {
			return new TimelineFragment();
		}
	}

	private List<Timeline> mItems = new ArrayList<Timeline>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.actvy_frgmt_timeline, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getHeadbar().setTitle("关注动态");
	}

	@Override
	public void onSaveState() {
		super.onSaveState();
		Cache.self().setTopFragmentData(TopFragmentType.Timeline, mItems);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onRestoreState() {
		super.onRestoreState();
		List<Timeline> value = (List<Timeline>) Cache.self().getTopFragmentData(
				TopFragmentType.Timeline);
		if (value != null) {
			mItems = value;
		}
	}

	@Override
	public void syncInitData(Bundle bundle) {
		super.syncInitData(bundle);

		TimelineListFragment fragment = new TimelineListFragment();
		replaceFragment(R.id.layout_timeline_list, fragment);
		fragment.asyncInitData();
	}

	@SuppressLint("ValidFragment")
	private class TimelineListFragment extends BaseListFragment {

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			observePhotoDownloadEvent(true);
			enableAppend(false);
			enableEdit(true);
		}

		@Override
		public void refreshData(boolean append) {
			LogicFactory.self().getTimeline().getAll(getMainEventListener());
		}

		@Override
		protected int getItemCount() {
			return mItems.size();
		}

		@Override
		protected boolean handleMainEvent(StatusEventArgs statusArgs, boolean append) {
			TimelinesEventArgs listArgs = (TimelinesEventArgs) statusArgs;
			if (append) {
				mItems.addAll(listArgs.getTimelines());
			} else {
				mItems = listArgs.getTimelines();
			}
			return listArgs.getTimelines().isEmpty();
		}

		@Override
		protected void onClickItem(int index) {
			Timeline timeline = mItems.get(index);
			switch (timeline.getType()) {
			case eOuser:
				ActivitySwitch.toProfile(getActivity(), timeline.getUid());
				break;
			case eAppoint:
				AppointValidChecker.check(timeline.getAppointId(), 
						getFragment(), new AppointValidChecker.OnValidListener() {
					
					@Override
					public void onValid(Appoint appoint) {
						ActivitySwitch.toAppointDetailForResult(getActivity(), appoint);
					}
				});
				break;
			default:
				break;
			}
		}

		@Override
		protected String getEditText(int index) {
			return "确认删除？";
		}

		@Override
		protected void onEdit(int index) {
			Timeline timeline = mItems.get(index);
			LogicFactory.self().getTimeline().remove(timeline);
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
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.lvitem_timeline,
						null);
				holder.imagePortrait = (ImageView) convertView.findViewById(R.id.image_portrait);
				holder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
				holder.txtContent = (TextView) convertView.findViewById(R.id.txt_content);
				holder.txtTime = (TextView) convertView.findViewById(R.id.txt_time);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Timeline timeline = mItems.get(position);
			holder.imagePortrait.setImageBitmap(PhotoManager.self().getBitmap(
					timeline.getPortrait(), Photo.Size.Large));
			holder.imagePortrait.setOnClickListener(
					ActivitySwitch.getToProfileClickListener(getActivity(), timeline.getUid()));
			holder.txtName.setText(timeline.getName());
			holder.txtContent.setText(timeline.getContent());
			holder.txtTime.setText(Formatter.formatCurrentMinuteToInterval(timeline.getTime()));
			return convertView;
		}

		private class ViewHolder {
			public ImageView imagePortrait = null;
			public TextView txtName = null;
			public TextView txtContent = null;
			public TextView txtTime = null;
		}
	}
}
