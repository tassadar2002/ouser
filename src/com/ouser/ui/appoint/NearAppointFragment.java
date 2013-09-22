package com.ouser.ui.appoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.ouser.image.PhotoManager;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.AppointsEventArgs;
import com.ouser.logic.event.NearOusersEventArgs;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.module.AppointId;
import com.ouser.module.NearOuser;
import com.ouser.module.Photo;
import com.ouser.ui.base.BaseListFragment;
import com.ouser.ui.component.HeadBar;
import com.ouser.ui.helper.ActivitySwitch;
import com.ouser.ui.helper.Alert;
import com.ouser.ui.helper.Formatter;
import com.ouser.ui.other.ShakeActivity;
import com.ouser.ui.topframework.TopFragment;
import com.ouser.ui.topframework.TopFragmentFactory;
import com.ouser.ui.topframework.TopFragmentType;

/**
 * 附近藕丝
 * 
 * @author hanlixin
 * 
 */
public class NearAppointFragment extends TopFragment {

	public static class Factory implements TopFragmentFactory {

		@Override
		public TopFragment create() {
			return new NearAppointFragment();
		}
	}

	private List<NearOuser> mItems = new ArrayList<NearOuser>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.actvy_frgmt_near_appoint, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getHeadbar().setTitle("附近友约");
		getHeadbar().setActionButton("摇", new HeadBar.OnActionListener() {
			
			@Override
			public void onClick() {
				startActivity(new Intent(getActivity(), ShakeActivity.class));
			}
		});
	}

	@Override
	public void onSaveState() {
		super.onSaveState();
		Cache.self().setTopFragmentData(TopFragmentType.NearAppoint, mItems);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onRestoreState() {
		super.onRestoreState();
		List<NearOuser> items = (List<NearOuser>) Cache.self().getTopFragmentData(TopFragmentType.NearAppoint);
		if (items != null) {
			mItems = items;
		}
	}

	@Override
	public void syncInitData(Bundle bundle) {
		super.syncInitData(bundle);

		NearListFragment fragment = new NearListFragment();
		replaceFragment(R.id.layout_near_ouser_list, fragment);
		fragment.asyncInitData();
	}

	@SuppressLint("ValidFragment")
	private class NearListFragment extends BaseListFragment {

		public NearListFragment() {
			observePhotoDownloadEvent(true);
		}

		@Override
		protected void refreshData(boolean append) {
			LogicFactory.self().getOuser().getNear(append ? getCurrentPageIndex() + 1 : 0, getMainEventListener());
		}

		@Override
		protected int getItemCount() {
			return mItems.size();
		}

		@Override
		protected boolean handleMainEvent(StatusEventArgs statusArgs, boolean append) {
			NearOusersEventArgs listArgs = (NearOusersEventArgs) statusArgs;
			if (append) {
				mItems.addAll(listArgs.getNearOusers());
			} else {
				mItems = listArgs.getNearOusers();
			}
			Collections.sort(mItems, new NearOuser.ComparatorByDistance());
			return listArgs.getNearOusers().isEmpty();
		}

		@Override
		protected void onClickItem(int index) {
			final AppointId appointId = mItems.get(index).getLastAppoint().getAppointId();			
			final String uid = mItems.get(index).getOuser().getUid();
			LogicFactory.self().getAppoint().getPublish(uid, true, 
					createUIEventListener(new EventListener() {

				@Override
				public void onEvent(EventId id, EventArgs args) {
					stopLoading();
					AppointsEventArgs appointsArgs = (AppointsEventArgs)args;
					if(appointsArgs.getErrCode() == OperErrorCode.Success) {
						if(appointsArgs.getAppoints().isEmpty()) {
							return;
						}
						ActivitySwitch.toAppointDetailForResult(
								getActivity(), appointsArgs.getAppoints(), appointId);
					} else {
						Alert.handleErrCode(appointsArgs.getErrCode());
					}
				}
			}));
			startLoading();
		}

		public View getItemView(int position, View convertView) {

			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity())
						.inflate(R.layout.lvitem_near_appoint, null);
				holder = new ViewHolder();
				holder.imagePortrait = (ImageView) convertView.findViewById(R.id.image_portrait);
				holder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
				holder.txtAppointContent = (TextView) convertView
						.findViewById(R.id.txt_appoint_content);
				holder.txtAppointCount = (TextView)convertView.findViewById(R.id.txt_appoint_count);
				holder.txtAge = (TextView) convertView.findViewById(R.id.txt_age);
				holder.imageGender = (ImageView) convertView.findViewById(R.id.image_gender);
				holder.txtDistance = (TextView) convertView.findViewById(R.id.txt_distance);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			NearOuser item = mItems.get(position);

			holder.imagePortrait.setImageBitmap(PhotoManager.self().getBitmap(
					item.getOuser().getPortrait(), Photo.Size.Large));
			holder.imagePortrait.setOnClickListener(
				ActivitySwitch.getToProfileClickListener(getActivity(), item.getOuser().getUid()));
			holder.txtName.setText(item.getOuser().getNickName());
			if (item.hasLastAppoint()) {
				holder.txtAppointContent.setText(item.getLastAppoint().getContent());
			}
			if(item.getOuser().getPublishAppointCount() != 0) {
				holder.txtAppointCount.setText(
						"共 " + item.getOuser().getPublishAppointCount() + " 条友约等你参加");
			}
			holder.txtAge.setText(String.valueOf(item.getOuser().getAge()));
			holder.imageGender.setImageResource(Formatter.getGenderIcon(item.getOuser()
					.getGender()));
			holder.txtDistance.setText(Formatter.formatDistance(item.getOuser().getDistance()));

			return convertView;
		}

		private class ViewHolder {
			public ImageView imagePortrait = null;
			public TextView txtName = null;
			public TextView txtAppointContent = null;
			public TextView txtAppointCount = null;
			public TextView txtAge = null;
			public ImageView imageGender = null;
			public TextView txtDistance = null;
		}
	}
}
