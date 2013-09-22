package com.ouser.ui.appoint;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.image.PhotoDownloadCompleteEventArgs;
import com.ouser.image.PhotoManager;
import com.ouser.module.Ouser;
import com.ouser.module.Photo;
import com.ouser.ui.base.BaseFragment;
import com.ouser.ui.helper.ActivitySwitch;
import com.ouser.ui.helper.PhotoDelayedRefresh;
import com.ouser.util.Const;

/**
 * 友约详情中的参与人和查看人的面板
 * @author hanlixin
 *
 */
abstract public class PhotosLayout {
	
	abstract protected int getHeadLayoutId();
//	abstract protected int getFrozenHeadLayoutId();
	abstract protected int getGridViewId();
	abstract protected String getTitleString();
	
	private BaseFragment mParent = null;
	private View mView = null;
	
	private View mOrginalView = null;
//	private View mFrozenView = null;

	private Adapter mAdapter = new Adapter(Const.Application);
	
	private PhotoDelayedRefresh mRefresh = new PhotoDelayedRefresh(mAdapter);
	
	private EventListener mListener = new EventListener() {

		@Override
		public void onEvent(EventId id, EventArgs args) {
			if(id == EventId.ePhotoDownloadComplete) {
				PhotoDownloadCompleteEventArgs photoArgs = (PhotoDownloadCompleteEventArgs)args;
				if(photoArgs.isSuccess()) {
					mRefresh.notifyDataSetChanged();
				}
			}
		}
	};
	
	public PhotosLayout(BaseFragment parent) {
		mParent = parent;
	}
	
	public View getOrginalLayout() {
		return mOrginalView;
	}
	
//	public View getFrozenView() {
//		return mFrozenView;
//	}

	public void onCreate(int count) {
		
		mView = mParent.getView().findViewById(R.id.layout_detail);
		
		mOrginalView = mView.findViewById(getHeadLayoutId());
//		mFrozenView = mView.findViewById(getFrozenHeadLayoutId());

		setText(count);
		
		mAdapter.setNotifyOnChange(false);
		GridView gv = (GridView)mView.findViewById(getGridViewId());
		gv.setAdapter(mAdapter);
		gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Ouser ouser = mAdapter.getItem(arg2);
				ActivitySwitch.toProfile(mParent.getActivity(), ouser);
			}
		});
		mParent.addUIEventListener(EventId.ePhotoDownloadComplete, mListener);
	}
	
	public void refresh(List<Ouser> ousers) {
		mAdapter.clear();
		for(Ouser ouser : ousers) {
			mAdapter.add(ouser);
		}
		mAdapter.notifyDataSetChanged();
		setText(ousers.size());
	}
	
	private void setText(int count) {
		String text = String.format(getTitleString(), count);
		((TextView)mOrginalView.findViewById(R.id.txt_title)).setText(text);
//		((TextView)mFrozenView.findViewById(R.id.txt_title)).setText(text);
	}
	
	public static class JoinPhotosLayout extends PhotosLayout {
		
		public JoinPhotosLayout(BaseFragment fragment) {
			super(fragment);
		}

		@Override
		protected int getHeadLayoutId() {
			return R.id.layout_join_head;
		}

//		@Override
//		protected int getFrozenHeadLayoutId() {
//			return R.id.layout_join_head_frozen;
//		}

		@Override
		protected int getGridViewId() {
			return R.id.gv_join;
		}

		@Override
		protected String getTitleString() {
			return "%d人参加";
		}
	}
	
	public static class ViewPhotosLayout extends PhotosLayout {
		
		public ViewPhotosLayout(BaseFragment fragment) {
			super(fragment);
		}

		@Override
		protected int getHeadLayoutId() {
			return R.id.layout_view_head;
		}

//		@Override
//		protected int getFrozenHeadLayoutId() {
//			return R.id.layout_view_head_frozen;
//		}

		@Override
		protected int getGridViewId() {
			return R.id.gv_view;
		}

		@Override
		protected String getTitleString() {
			return "%d人浏览";
		}
	}
	
	private static class Adapter extends ArrayAdapter<Ouser> {
		
		private class ViewHolder {
			public ImageView imagePortrait = null;
		}
		
		public Adapter(Context context) {
			super(context, 0);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.gvitem_photo, null);
				holder = new ViewHolder();
				holder.imagePortrait = (ImageView)convertView.findViewById(R.id.image_portrait);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}
			Bitmap bitmap = PhotoManager.self().getBitmap(getItem(position).getPortrait(), Photo.Size.Small);
			holder.imagePortrait.setImageBitmap(bitmap);
			return convertView;
		}
		
	}
}
