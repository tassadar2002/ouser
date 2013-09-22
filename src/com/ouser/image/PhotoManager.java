package com.ouser.image;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;

import com.ouser.R;
import com.ouser.event.EventCenter;
import com.ouser.event.EventId;
import com.ouser.logger.Logger;
import com.ouser.module.Photo;
import com.ouser.net.HttpDownloader;
import com.ouser.net.HttpResultCallback;
import com.ouser.util.Const;
import com.ouser.util.StringUtil;

@SuppressLint("HandlerLeak")
public class PhotoManager {

	private static final PhotoManager ins = new PhotoManager();
	public static final PhotoManager self() {
		return ins;
	}

	private static final Logger logger = new Logger("photomgr");
	
	private static final String BaseUrl = "http://image.zhengre.com";
	private static final String ImagePath = Const.WorkDir + "image/";

	private boolean mInited = false;
	
	/** 下载photo的数据 */
	private List<Element> mElements = new LinkedList<Element>();
	
	/** 下载服务线程池 */
	private ExecutorService mService = 
			new ThreadPoolExecutor(2, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Element element = (Element)msg.obj;
			mElements.remove(element);
			EventCenter.self().fireEvent(EventId.ePhotoDownloadComplete, 
					new PhotoDownloadCompleteEventArgs(element.photo, element.size, element.success));
		}
	};
	
	private PhotoManager() {
	}
	
	public Pair<Bitmap, Boolean> getBitmapAndNeedDownload(Photo photo, Photo.Size size) {
		
		init();
		
		if(StringUtil.isEmpty(photo.getUrl())) {
			if(photo.getResId() == 0) {
				logger.e("empty url and 0 res id.set default res id");
				photo.setResId(R.drawable.default_photo);
			}
			// 带有资源id的照片
			return new Pair<Bitmap, Boolean>(
					BitmapFactory.decodeResource(Const.Application.getResources(), photo.getResId()), 
					false);
		}
		
		// 带有url的照片
		fillPhotoPath(photo, size);
		
		String path = photo.getPath(size);
		if(new File(path).exists()) {
			return new Pair<Bitmap, Boolean>(
					BitmapFactory.decodeFile(path),	false);
		}

		addDownload(new Element(photo, size));
		return new Pair<Bitmap, Boolean>(
				BitmapFactory.decodeResource(Const.Application.getResources(), R.drawable.default_photo),
				true);
	}

	public Bitmap getBitmap(Photo photo, Photo.Size size) {
		return getBitmapAndNeedDownload(photo, size).first;
	}
	
	private void init() {
		if(mInited) {
			return;
		}
		mInited = true;
		File dir = new File(ImagePath);
		if(!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
		}
	}
	
	private void addDownload(Element element) {
		for(Element e : mElements) {
			if(e.photo.isSame(element.photo) && e.size == element.size) {
				return;
			}
		}
		logger.d("in queue to download " + element.photo.getUrl());
		mElements.add(element);
		mService.submit(new DownloadThread(element, this));
	}

	private void download(final Element element) {
		String path = element.photo.getPath(element.size);
		if(new File(path).exists()) {
			success(element);
			return;
		}

		String url = getSuitableUrl(element.photo.getUrl(), element.size);
		new HttpDownloader(false).download(url, path,
				new HttpResultCallback() {

					@Override
					public void onResponse(HttpDownloaderResult result,
							String url, String message) {
						logger.d("download complete " + url + " " + result);
						if (result == HttpDownloaderResult.eSuccessful) {
							success(element);
						} else {
							fail(element);
						}
					}

					@Override
					public void onProgress(String url, float rate) {
					}
				});
	}
	
	private String getSuitableUrl(String url, Photo.Size size) {
		int number = size.getSize();
		
		int index = url.lastIndexOf('/');
		if(index == -1) {
			return url;
		}
		return BaseUrl + url.substring(0, index + 1) + number + "_" + url.substring(index + 1);
	}
	
	/** 填充对象的 全路径 属性 */
	private void fillPhotoPath(Photo photo, Photo.Size size) {
		
		// 已经存在值，忽略
		if(!StringUtil.isEmpty(photo.getPath(size))) {
			return;
		}
		
		// 使用urlencode生成路径
		String url = photo.getUrl();
		try {
			String fileName = size.getSize() + "_" + URLEncoder.encode(url, "UTF-8");
			photo.setPath(ImagePath + fileName, size);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private void success(Element element) {
		element.success = true;
		mHandler.sendMessage(mHandler.obtainMessage(0, element));
	}

	private void fail(Element element) {		
		logger.d("download fail.url:" + element.photo.getUrl());
		int tryTime = element.photo.getTryTime(element.size) + 1;
		element.photo.setTryTime(tryTime, element.size);
		if (tryTime < Const.PhotoMaxTryTime) {
			addDownload(element);
		} else {
			mHandler.sendMessage(mHandler.obtainMessage(0, element));
		}
	}
	
	/**
	 * 下载线程
	 * @author Administrator
	 *
	 */
	private static class DownloadThread implements Runnable {
		
		private Element mElement = null;
		private PhotoManager mManager = null;
		
		public DownloadThread(Element element, PhotoManager manager) {
			mElement = element;
			mManager = manager;
		}

		@Override
		public void run() {
			mManager.download(mElement);
		}
	}
	
	private static class Element {
		public Photo photo = null;
		public Photo.Size size = null;
		public boolean success = false;
		public Element(Photo photo, Photo.Size size) {
			this.photo = photo;
			this.size = size;
		}
	}
}
