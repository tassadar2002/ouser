package com.ouser.ui.appoint;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.image.PhotoManager;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.AppointsWithPublisherEventArgs;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.module.Appoint;
import com.ouser.module.AppointsWithPublisher;
import com.ouser.module.Ouser;
import com.ouser.module.Photo;
import com.ouser.ui.base.BaseListFragment;
import com.ouser.ui.helper.ActivitySwitch;
import com.ouser.ui.helper.Alert;
import com.ouser.ui.helper.Formatter;
import com.ouser.ui.helper.HideKeyboard;
import com.ouser.ui.topframework.TopFragment;
import com.ouser.ui.topframework.TopFragmentFactory;
import com.ouser.ui.widget.AppointEditText;
import com.ouser.util.Const;

/**
 * 搜索友约
 * @author hanlixin
 *
 */
public class SearchAppointFragment extends TopFragment {
	
	public static class Factory implements TopFragmentFactory {

		@Override
		public TopFragment create() {
			return new SearchAppointFragment();
		}
	}
	
	public static final String IntentKeyword = "text";
	
	private AppointEditText mEditKeyword = null;
	private Button mBtnSearch = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.actvy_frgmt_search_appoint, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getHeadbar().setTitle("搜索友约");

		mEditKeyword = (AppointEditText)getView().findViewById(R.id.edit_keyword);
		mEditKeyword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(mEditKeyword.isEmptyText()) {
					setButton(true);
				} else {
					setButton(hasFocus);
				}
			}
		});
		setButton(true);
		
		HideKeyboard.setupUI(getActivity(), getView());
	}
	
	@Override
	public void syncInitData(Bundle bundle) {
		super.syncInitData(bundle);
		
		String searchKeyword = "";
		if(bundle != null) {
			searchKeyword = bundle.getString(IntentKeyword);
		}
		
		// no suggestion
		if(!"".equals(searchKeyword)) {
			setEditTextValue(searchKeyword);
		}
		
		search();
	}

	@Override
	public void onSaveState() {
		super.onSaveState();
		// 不需要保存数据
	}

	private void setButton(boolean search) {
		mBtnSearch = (Button)getView().findViewById(R.id.btn_search_action);
		
		if(search) {
			mBtnSearch.setText("搜索");
			mBtnSearch.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {				
					search();
				}
			});
		} else {
			mBtnSearch.setText("关注");
			mBtnSearch.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					LogicFactory.self().getAppoint().addFolow(mEditKeyword.getText().toString(), 
							createUIEventListener(new EventListener() {
						
						@Override
						public void onEvent(EventId id, EventArgs args) {
							stopLoading();
							StatusEventArgs statusArgs = (StatusEventArgs)args;
							if(statusArgs.getErrCode() == OperErrorCode.Success) {
								Alert.Toast("关注成功");
							} else {
								Alert.handleErrCode(statusArgs.getErrCode());
							}
						}
					}));
					startLoading();
				}
			});
		}
	}
	
	private void setEditTextValue(String value) {
		mEditKeyword.requestFocus();
		mEditKeyword.setText(value);
		mEditKeyword.setSelection(mEditKeyword.getText().toString().length());
	}
	
	private void search() {
		
		mEditKeyword.clearFocus();
		Fragment fragment = findFragment(R.id.layout_search_appoint_list);
		if(mEditKeyword.isEmptyText()) {
			// 变为变标签云
			CloudTagFragment cloudFragment = null;
			if(fragment == null || fragment instanceof SearchListFragment) {
				cloudFragment = new CloudTagFragment();
				cloudFragment.setOnSelectListener(new CloudTagFragment.OnSelectTagListener() {
					
					@Override
					public void onSelect(String tag) {
						setEditTextValue("#" + tag);
						search();
					}
				});
				replaceFragment(R.id.layout_search_appoint_list, cloudFragment);
			} else {
				cloudFragment = (CloudTagFragment)fragment;
			}
			cloudFragment.asyncInitData();
		} else {
			// 变为搜索
			SearchListFragment searchListFragment = null;
			if(fragment == null || fragment instanceof CloudTagFragment) {
				searchListFragment = new SearchListFragment();
				replaceFragment(R.id.layout_search_appoint_list, searchListFragment);
			} else {
				searchListFragment = (SearchListFragment)fragment;
				searchListFragment.clear();
			}
			String keyword = mEditKeyword.getText().toString();
			searchListFragment.search(keyword);
		}
	}

	@SuppressLint("ValidFragment")
	private class SearchListFragment extends BaseListFragment {

		private AppointsWithPublisher mItems = new AppointsWithPublisher();
		
		public void search(String value) {
			asyncInitData();
		}
		
		public void clear() {
			mItems.clear();
			mAdapter.notifyDataSetChanged();
		}

		public SearchListFragment() {
			observePhotoDownloadEvent(true);
		}

		@Override
		protected void refreshData(boolean append) {
			LogicFactory.self().getAppoint().search(mEditKeyword.getText().toString(), 
					append ? getCurrentPageIndex() + 1 : 0, getMainEventListener());
		}

		@Override
		protected boolean handleMainEvent(StatusEventArgs statusArgs, boolean append) {
			AppointsWithPublisherEventArgs listArgs = (AppointsWithPublisherEventArgs)statusArgs;
			if (append) {
				mItems.addAll(listArgs.getAppoints());
			} else {
				mItems = listArgs.getAppoints();
			}
			return listArgs.getAppoints().isEmpty();
		}

		@Override
		protected int getItemCount() {
			return mItems.size();
		}

		@Override
		protected void onClickItem(int index) {
			ActivitySwitch.toAppointDetailForResult(getActivity(), mItems.getAppoint(index));
		}
		
		@Override
		public View getItemView(int position, View convertView) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.lvitem_search_appoint, null);
				holder.imagePortrait = (ImageView) convertView.findViewById(R.id.image_portrait);
				holder.imageGender = (ImageView)convertView.findViewById(R.id.image_gender);
				holder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
				holder.txtContent = (TextView) convertView.findViewById(R.id.txt_content);
				holder.txtViewCount = (TextView) convertView.findViewById(R.id.txt_view_count);
				holder.txtJoinCount = (TextView) convertView.findViewById(R.id.txt_join_count);
				holder.txtAge = (TextView)convertView.findViewById(R.id.txt_age);
				holder.txtDistance = (TextView)convertView.findViewById(R.id.txt_distance);
				convertView.setTag(holder);
				
				HideKeyboard.setupUI(getActivity(), convertView);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Pair<Appoint, Ouser> item = mItems.get(position);
			Appoint appoint = item.first;
			Ouser publisher = item.second;
			holder.imagePortrait.setImageBitmap(PhotoManager.self().getBitmap(
					publisher.getPortrait(), Photo.Size.Large));
			holder.imagePortrait.setOnClickListener(
					ActivitySwitch.getToProfileClickListener(getActivity(), publisher.getUid()));
			holder.imageGender.setImageResource(Formatter.getGenderIcon(publisher.getGender()));
			holder.txtName.setText(publisher.getNickName());
			holder.txtContent.setText(Formatter.getAppointContent(appoint));
			holder.txtViewCount.setText(Formatter.getViewCount(appoint));
			holder.txtJoinCount.setText(Formatter.getJoinCount(appoint));
			holder.txtAge.setText(String.valueOf(publisher.getAge()));
			if(appoint.getDistance() == Const.DefaultValue.Distance) {
				holder.txtDistance.setVisibility(View.GONE);
			} else {
				holder.txtDistance.setVisibility(View.VISIBLE);
				holder.txtDistance.setText(Formatter.formatDistance(appoint.getDistance()));
			}
			return convertView;
		}

		private class ViewHolder {
			public ImageView imagePortrait = null;
			public ImageView imageGender = null;
			public TextView txtName = null;
			public TextView txtContent = null;
			public TextView txtViewCount = null;
			public TextView txtJoinCount = null;
			public TextView txtAge = null;
			public TextView txtDistance = null;
		}
	}
}
