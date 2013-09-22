package com.ouser.ui.helper;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;

import com.ouser.util.Const;

public class PhotoFetcher {
	
	public interface OnActionListener {
		void onComplete(Parcelable data);
		void onCancel();
	}
	
	private static final int OutputX = 300;
	private static final int OutputY = 300;
	private static final String ImagePath = Const.WorkDir + "temp/";
	private static final String ImageFile = ImagePath + "photo.jpg";
	
	private Activity mActivity = null;
	private OnActionListener mListener = null;
	
	public void setActivity(Activity value) {
		mActivity = value;
	}
	public void setOnActionListener(OnActionListener value) {
		mListener = value;
	}

	public void getFromCamera() {		
		// 创建临时目录
		File path = new File(ImagePath);
		if (!path.exists()) {
			path.mkdirs();
		}

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 下面这句指定调用相机拍照后的照片存储的路径
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(ImageFile)));
		mActivity.startActivityForResult(intent, Const.RequestCode.PhotoFromCamera);
	}
	
	public void getFromAlbum() {
		Intent intent = new Intent(Intent.ACTION_PICK, null);

		/**
		 * 下面这句话，与其它方式写是一样的效果，如果：
		 * intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		 * intent.setType(""image/*");设置数据类型
		 * 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
		 */
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
		mActivity.startActivityForResult(intent, Const.RequestCode.PhotoFromAlbum);
	}
	
	public void handleActivityResult(int type, int result, Intent intent) {
		switch (type) {
		case Const.RequestCode.PhotoFromAlbum:
        	if(intent != null) {
        		startPhotoZoom(intent.getData());
        	} else {
        		if(mListener != null) {
        			mListener.onCancel();
        		}
        	}
			break;
		case Const.RequestCode.PhotoFromCamera:
            File temp = new File(ImageFile);
            if(temp.exists()) {
            	startPhotoZoom(Uri.fromFile(temp));
            } else {
        		if(mListener != null) {
        			mListener.onCancel();
        		}
        	}
			break;
		case Const.RequestCode.PhotoCrop:
            /**  
             * 非空判断大家一定要验证，如果不验证的话，  
             * 在剪裁之后如果发现不满意，要重新裁剪，丢弃  
             * 当前功能时，会报NullException
             */ 
    		if(mListener != null) {
    			if(intent != null){  
                	mListener.onComplete(intent.getParcelableExtra("data"));
                } else {
                	mListener.onCancel();
                }
    		}
            new File(ImageFile).delete();
			break;
		}
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	private void startPhotoZoom(Uri uri) {
		// 直接调本地库
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", OutputX);
		intent.putExtra("outputY", OutputY);
		intent.putExtra("return-data", true);
		mActivity.startActivityForResult(intent, Const.RequestCode.PhotoCrop);
	}
}
