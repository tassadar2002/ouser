package com.ouser.net;

public interface HttpResultCallback 
{
	public enum HttpDownloaderResult 
	{
		eNone,
		eSuccessful,
		eReadError,
		eWriteError,
		eUrlIllegal,
		eOpenUrlError,
	}
	void onResponse(HttpDownloaderResult result, String url, String message);
	void onProgress(String url, float rate);
}
