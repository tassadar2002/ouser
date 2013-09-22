package com.ouser.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;

import com.ouser.net.HttpResultCallback.HttpDownloaderResult;

public class HttpDownloader {
	
	/** 访问地址 */
	private String mUrl = "";
	
	/** 保存文件名 */
	private String mFileName = "";
	
	/** 异步还是同步 */
	private boolean mAsync = true;
	
	/** 网络回调 */
	private HttpResultCallback mCallback = null;

	public HttpDownloader() {
	}

	public HttpDownloader(boolean async) {
		mAsync = async;
	}

	public void download(String url, String fileName,
			HttpResultCallback callback) {
		this.mUrl = url;
		this.mFileName = fileName;
		this.mCallback = callback;

		if (mAsync) {
			new DownloadAsyncTask().execute();
		} else {
			postEvent(work());
		}
	}

	private void postEvent(HttpDownloaderResult success) {
		if (mCallback != null) {
			mCallback.onResponse(success, this.mUrl, this.mFileName);
		}
	}

	private HttpDownloaderResult work() {
		HttpDownloaderResult result = HttpDownloaderResult.eNone;
		OutputStream output = null;
		try {
			/*
			 * 通过URL取得HttpURLConnection 要网络连接成功，需在AndroidMainfest.xml中进行权限配置
			 * <uses-permission android:name="android.permission.INTERNET"
			 * />
			 */
			HttpURLConnection conn = null;
			try {
				URL url = null;
				try {
					url = new URL(mUrl);
				} catch (MalformedURLException e) {
					e.printStackTrace();
					return HttpDownloaderResult.eUrlIllegal;
				}
				conn = (HttpURLConnection) url.openConnection();
			} catch (IOException e) {
				e.printStackTrace();
				return HttpDownloaderResult.eOpenUrlError;
			}

			conn.setRequestMethod("POST");
			conn.setRequestProperty("Referer", mUrl);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setDefaultUseCaches(false);
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			int totalsize = conn.getContentLength();
			
//			if(conn.getHeaderFields() != null) {
//				StringBuilder sb = new StringBuilder();
//				for(Map.Entry<String, List<String>> entry : conn.getHeaderFields().entrySet()) {
//					sb.append(entry.getKey()).append(":");
//					for(String str : entry.getValue()) {
//						sb.append(str).append(",");
//					}
//					sb.append(";");
//				}
//				String a = sb.toString();
//			}
			
//			 int responseCode = conn.getResponseCode();
//			 if (responseCode == 404)
//			 {
//			 return false;
//			 }

			// 取得inputStream，并将流中的信息写入SDCard

			/*
			 * 写前准备 1.在AndroidMainfest.xml中进行权限配置 <uses-permission
			 * android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
			 * 取得写入SDCard的权限 2.取得SDCard的路径：
			 * Environment.getExternalStorageDirectory() 3.检查要保存的文件上是否已经存在
			 * 4.不存在，新建文件夹，新建文件 5.将input流中的信息写入SDCard 6.关闭流
			 */
			InputStream input = null;
			try {
				input = conn.getInputStream();
			} catch (IOException e) {
				return HttpDownloaderResult.eReadError;
			}

			File file = new File(mFileName);
			file.createNewFile();
			output = new FileOutputStream(file);

			// 读取大文件
			int readed = 0;
			int downloadsize = 0;
			byte[] buffer = new byte[100 * 1024];
			while (readed != -1) {
				if (readed > 0) {
					try {
						output.write(buffer, 0, readed);
					} catch (IOException e) {
						result = HttpDownloaderResult.eWriteError;
						break;
					}

					downloadsize += readed;
					if (totalsize > 0) {
						mCallback.onProgress(mUrl, downloadsize
								/ (float) totalsize);
					}
				}

				try {
					readed = input.read(buffer);
				} catch (IOException e) {
					result = HttpDownloaderResult.eReadError;
					break;
				}
			}
			output.flush();
			if (result == HttpDownloaderResult.eNone) {
				result = HttpDownloaderResult.eSuccessful;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		finally {
			try {
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	private class DownloadAsyncTask extends
			AsyncTask<Void, Void, HttpDownloaderResult> {

		@Override
		protected HttpDownloaderResult doInBackground(Void... params) {
			return work();
		}

		@Override
		protected void onPostExecute(HttpDownloaderResult result) {
			postEvent(result);
		}
	}
}
