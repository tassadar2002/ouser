package com.ouser.ui.profile;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.meetme.android.horizontallistview.HorizontalListView;
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
import com.ouser.module.Photo;
import com.ouser.ui.helper.Alert;
import com.ouser.ui.helper.PhotoDelayedRefresh;
import com.ouser.ui.ouser.PhotoActivity;
import com.ouser.util.Const;
import com.ouser.util.ListUtil;

class PhotoHandler extends BaseHandler {

	/** 照片数据 */
	private Adapter mAdapter = new Adapter();
	private List<Photo> mPhotos = new ArrayList<Photo>();

	private PhotoDelayedRefresh mRefresh = new PhotoDelayedRefresh(mAdapter);

	private EventListener mGetOuseEventListener = new EventListener() {

		@Override
		public void onEvent(EventId id, EventArgs args) {
			OuserEventArgs ouserArgs = (OuserEventArgs) args;
			if (ouserArgs.getOuser().isSame(getOuser())) {
				if (ouserArgs.getErrCode() == OperErrorCode.Success) {
					fillPhoto(ouserArgs.getOuser().getPhotos());
				}
			}
		}
	};

	/** 下载照片的监听器 */
	private EventListener mPhotoEventListener = new EventListener() {

		@Override
		public void onEvent(EventId id, EventArgs args) {
			PhotoDownloadCompleteEventArgs photoArgs = (PhotoDownloadCompleteEventArgs) args;
			if (!photoArgs.getPhoto().isSame(getOuser().getPortrait())) {
				if (photoArgs.isSuccess()) {
					mRefresh.notifyDataSetChanged();
				}
			}
		}
	};

	/** 添加删除的监听器 */
	private EventListener mActionEventListener = new EventListener() {

		@Override
		public void onEvent(EventId id, EventArgs args) {
			stopLoading();
			StatusEventArgs statusArgs = (StatusEventArgs) args;
			if (statusArgs.getErrCode() == OperErrorCode.Success) {
				Alert.Toast("操作成功");
				LogicFactory.self().getProfile().get(getOuser().getUid());
				startLoading();
			} else {
				Alert.handleErrCode(statusArgs.getErrCode());
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();

		// list view
		HorizontalListView listview = (HorizontalListView) getActivity()
				.findViewById(R.id.list_photo);
		listview.setAdapter(mAdapter);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				Photo selected = mPhotos.get(arg2);
				if (isAddPhoto(selected)) {
					getHandlerFactory().getNewPhoto().toNewPhoto(
							RequestFrom.AddPhoto);
				} else {
					List<Photo> photos = getRawPhoto();
					int count = photos.size();

					Intent intent = new Intent(getActivity(),
							PhotoActivity.class);
					intent.putExtra("uid", getOuser().getUid());
					intent.putExtra("select", selected.toBundle());
					intent.putExtra("count", count);
					intent.putExtra("islist", true);
					for (int i = 0; i < count; ++i) {
						intent.putExtra(String.valueOf(i), photos.get(i)
								.toBundle());
					}
					getActivity().startActivityForResult(intent,
							Const.RequestCode.AnyOne);
				}
			}
		});
		if (getOuser().getUid().equals(Cache.self().getMyUid())) {
			listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Photo selected = mPhotos.get(arg2);
					if (!isAddPhoto(selected)) {
						toRemove(selected);
						return true;
					}
					return false;
				}
			});
		}

		getActivity().findViewById(R.id.txt_invite_upload).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						LogicFactory.self().getOuser()
								.inviteUploadPhoto(getOuser());
						getActivity().findViewById(R.id.txt_invite_upload)
								.setVisibility(View.GONE);
						Alert.Toast("邀请成功");
					}
				});

		// logic
		getActivity().addUIEventListener(EventId.eGetOuserProfile,
				mGetOuseEventListener);
		getActivity().addUIEventListener(EventId.ePhotoDownloadComplete,
				mPhotoEventListener);
	}

	public void onNewPhotoResult(Bitmap bitmap) {
		LogicFactory
				.self()
				.getPhoto()
				.add(bitmap,
						getActivity().createUIEventListener(
								mActionEventListener));
		startLoading();
	}

	private void toRemove(final Photo photo) {
		new AlertDialog.Builder(getActivity()).setMessage("确定删除？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						LogicFactory
								.self()
								.getPhoto()
								.remove(photo.getUrl(),
										getActivity().createUIEventListener(
												mActionEventListener));
						startLoading();
					}
				}).setNegativeButton("取消", null).create().show();

	}

	/** 填充照片 */
	private void fillPhoto(List<Photo> photos) {

		mPhotos = ListUtil.clone(photos);
		if (isMySelf()) {
			// 本人的需要添加一张"添加照片"
			Photo addPhoto = new Photo();
			addPhoto.setResId(R.drawable.add_photo);
			mPhotos.add(0, addPhoto);
		}

		HorizontalListView listview = (HorizontalListView) getActivity()
				.findViewById(R.id.list_photo);
		View txtAddPhoto = getActivity().findViewById(R.id.txt_invite_upload);
		if (mPhotos.isEmpty()) {
			listview.setVisibility(View.GONE);
			txtAddPhoto.setVisibility(Cache.self().isInviteUpdatePhoto(
					getOuser().getUid()) ? View.GONE : View.VISIBLE);
		} else {
			listview.setVisibility(View.VISIBLE);
			txtAddPhoto.setVisibility(View.GONE);
		}

		listview.setAdapter(mAdapter);
	}

	private List<Photo> getRawPhoto() {
		List<Photo> result = new ArrayList<Photo>();
		for (Photo photo : mPhotos) {
			if (isAddPhoto(photo)) {
				continue;
			}
			result.add(photo);
		}
		return result;
	}

	private boolean isAddPhoto(Photo photo) {
		return photo.getResId() == R.drawable.add_photo;
	}

	private class Adapter extends BaseAdapter {

		private class ViewHolder {
			public ImageView imagePhoto;
		}

		@Override
		public int getCount() {
			return mPhotos.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.lvitem_photo, null);
				holder = new ViewHolder();
				holder.imagePhoto = (ImageView) convertView
						.findViewById(R.id.image_photo);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Bitmap bitmap = PhotoManager.self().getBitmap(
					mPhotos.get(position), Photo.Size.Normal);
			holder.imagePhoto.setImageBitmap(bitmap);
			return convertView;
		}
	}
}
