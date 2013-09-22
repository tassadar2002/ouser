package com.ouser.ui.ouser;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
import com.ouser.image.PhotoDownloadCompleteEventArgs;
import com.ouser.image.PhotoManager;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.module.Photo;
import com.ouser.ui.base.BaseActivity;
import com.ouser.ui.base.BaseFragment;
import com.ouser.ui.base.BaseViewPager;
import com.ouser.ui.helper.Alert;

public class PhotoActivity extends BaseActivity {
	
	private BaseViewPager mViewPager = new BaseViewPager();

	/** 数据项 */
	private List<Photo> mItems = new ArrayList<Photo>();
	
	private boolean mIsList = true;
	
	/** 当前uid */
	private String mUid = "";

	private EventListener mListener = new EventListener() {

		@Override
		public void onEvent(EventId id, EventArgs args) {
			handleDownloadCompleteEvent((PhotoDownloadCompleteEventArgs) args);
		}
	};
	
	private BaseViewPager.OnActionListener mActionListener = new BaseViewPager.OnActionListener() {
		
		@Override
		public void onPageChanged() {
			PhotoActivity.this.onPageChanged();
		}
		
		@Override
		public int getPageCount() {
			return mItems.size();
		}
		
		@Override
		public Fragment createFragment(int index) {
			return new PhotoFragment();
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.actvy_photo);

		parseArgument();
		initTopBar();
		initViewPager();

		addUIEventListener(EventId.ePhotoDownloadComplete, mListener);
	}

	/**
	 * 解析intent参数
	 */
	private void parseArgument() {
		Bundle extra = getIntent().getExtras();
		
		mUid = extra.getString("uid");
		mIsList = extra.getBoolean("islist");

		Photo selected = new Photo();
		selected.fromBundle(extra.getBundle("select"));

		int count = extra.getInt("count");
		for (int i = 0; i < count; ++i) {
			Photo photo = new Photo();
			photo.fromBundle(extra.getBundle(String.valueOf(i)));
			mItems.add(photo);

			if (photo.isSame(selected)) {
				mViewPager.setCurrentIndex(i);
			}
		}
	}
	
	/**
	 * 初始化viewpager
	 */
	private void initViewPager() {
		mViewPager.init(this, (ViewPager) findViewById(R.id.viewpager_photo), mActionListener);
	}

	/**
	 * 初始化菜顶部控制栏
	 */
	private void initTopBar() {
		findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		View btnPortrait = findViewById(R.id.btn_protrait);
		if(mIsList) {
			if(!Cache.self().getMyUid().equals(mUid)) {
				btnPortrait.setVisibility(View.GONE);
			} else {
				btnPortrait.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						setPortrait();
					}
				});
			}
		} else {
			btnPortrait.setVisibility(View.GONE);
			findViewById(R.id.txt_index).setVisibility(View.GONE);
		}
	}

	private void setPortrait() {
		Photo photo = mItems.get(mViewPager.getCurrentIndex());
		if(photo == null) {
			return;
		}
		LogicFactory.self().getProfile().setPortraitUrl(photo.getUrl(), 
				createUIEventListener(new EventListener() {
			
			@Override
			public void onEvent(EventId id, EventArgs args) {
				stopLoading();
				StatusEventArgs statusArgs = (StatusEventArgs)args;
				if(statusArgs.getErrCode() == OperErrorCode.Success) {
					Alert.Toast("设置成功");
					
					// 通知前界面有变化
					setResult(RESULT_OK);
				} else {
					Alert.Toast("设置失败");
				}
			}
		}));
		startLoading();
	}
	
	private void onPageChanged() {
		
		// 设置标题
		((TextView)findViewById(R.id.txt_index)).setText(
				String.format("%d/%d", mViewPager.getCurrentIndex() + 1, mItems.size()));

		// 设置图片
		Photo photo = mItems.get(mViewPager.getCurrentIndex());
		Pair<Bitmap, Boolean> Pair = PhotoManager.self().getBitmapAndNeedDownload(photo,
				Photo.Size.XLarge);
		((PhotoFragment) mViewPager.getFragment(mViewPager.getCurrentIndex())).setImage(Pair.first);
		if (Pair.second) {
			// TODO 向下查找，large，normal。。。先显示缩略图的模糊的
			startLoading();
		}
	}

	private void handleDownloadCompleteEvent(PhotoDownloadCompleteEventArgs args) {

		if (args.getSize() != Photo.Size.XLarge) {
			return;
		}

		// 查找该照片的下标
		int index = 0;
		for (; index < mItems.size(); ++index) {
			if (mItems.get(index).isSame(args.getPhoto())) {
				break;
			}
		}
		if (index == mItems.size()) {
			return;
		}

		// 成功则更新界面
		if (args.isSuccess()) {
			((PhotoFragment) mViewPager.getFragment(index)).setImage(PhotoManager.self().getBitmap(
					args.getPhoto(), args.getSize()));
		}

		// 是当前fragment，停止loading
		if (index == mViewPager.getCurrentIndex()) {
			stopLoading();
		}
	}

	@SuppressLint("ValidFragment")
	public static class PhotoFragment extends BaseFragment {

		private Bitmap mBitmap = null;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			return inflater.inflate(R.layout.frgmt_photo, null);
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			setImage();
		}

		@Override
		public void onDestroyView() {
			mBitmap = null;
			super.onDestroyView();
		}

		public void setImage(final Bitmap bitmap) {
			mBitmap = bitmap;
			setImage();
		}

		private void setImage() {
			if (mBitmap != null && getView() != null) {
				ImageView image = (ImageView) getView().findViewById(R.id.image_photo);
				image.setImageBitmap(mBitmap);
			}
		}
	}
}
