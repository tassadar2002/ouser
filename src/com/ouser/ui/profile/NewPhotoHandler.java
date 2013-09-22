package com.ouser.ui.profile;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.util.Pair;

import com.ouser.R;
import com.ouser.ui.component.MenuDialogBuilder;
import com.ouser.ui.helper.PhotoFetcher;

class NewPhotoHandler extends BaseHandler {
	
	private static final int MenuCamera = 1;
	private static final int MenuAlbum = 2;

	private int mFrom = 0;
	
	private PhotoFetcher mFetcher = new PhotoFetcher();

	@Override
	public void onCreate() {
		super.onCreate();

		mFetcher.setActivity(getActivity());
		mFetcher.setOnActionListener(new PhotoFetcher.OnActionListener() {
			
			@Override
			public void onComplete(Parcelable data) {
				notifyFrom(data);
			}
			
			@Override
			public void onCancel() {
			}
		});
	}

	@Override
	public void onActivityResult(int type, int result, Intent intent) {
		mFetcher.handleActivityResult(type, result, intent);
	}

	public void toNewPhoto(int from) {
		mFrom = from;
		
		List<Pair<Integer, String>> items = new ArrayList<Pair<Integer, String>>();
		items.add(new Pair<Integer, String>(MenuCamera, "拍照"));
		items.add(new Pair<Integer, String>(MenuAlbum, "相册"));
		
		new MenuDialogBuilder(getActivity())
			.setMenuItem(items, new MenuDialogBuilder.Callback() {
				
				@Override
				public void onClick(int key) {
					switch(key) {
					case MenuCamera:
						mFetcher.getFromCamera();
						break;
					case MenuAlbum:
						mFetcher.getFromAlbum();
						break;
					}
				}
			})
			.setTop(getActivity().findViewById(R.id.layout_head_bar).getHeight())
			.create().show();
	}

    /**  
     * 保存裁剪之后的图片数据  
     * @param picdata  
     */  
    private void notifyFrom(Parcelable data) {  
        Bitmap bitmap = (Bitmap)data;
        switch(mFrom) {
        case RequestFrom.ChangePortait:
        	getHandlerFactory().getInfo().onNewPhotoResult(bitmap);
        	break;
        case RequestFrom.AddPhoto:
        	getHandlerFactory().getPhoto().onNewPhotoResult(bitmap);
        	break;
        }
    }
}
