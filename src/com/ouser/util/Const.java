package com.ouser.util;

import android.os.Environment;

import com.ouser.OuserApplication;

public class Const {
	
	/** startyActivityForResult中的request code */
	public static class RequestCode {
		
		// 不关注，仅仅占位
		public static final int AnyOne = 0;
		
		// 从相册获取照片
		public static final int PhotoFromAlbum = 1000;
		
		// 拍照过去照片
		public static final int PhotoFromCamera = 1001;
		
		// 剪裁照片
		public static final int PhotoCrop = 1002;
		
		// 地图位置选择
		public static final int Location = 1003;
		
		// 发布友约
		public static final int PublishAppoint = 1004;
		
		// 友约详情
		public static final int AppointDetail = 1005;
		
		// 聊天
		public static final int Chat = 1006;
	}
	
	/** intent中的extra的key */
	public static class Intent {
		/** ouser的uid */
		public static final String Uid = "uid";

		/** ouser的昵称 */
		public static final String NickName = "nickname";
		
		/** 聊天对象的id */
		public static final String ChatId = "chatid";
		
		/** 友约对象 */
		public static final String Appoint = "appoint";

		/** 友约发布者uid */
		public static final String PublisherUid = "publisheruid";
		
		/** 友约内容 */
		public static final String AppointContent = "aname";
		
		/** 针对不同页有不同的含义 */
		public static final String Type = "type";
		
		/** 转到top时需要选择的页面 */
		public static final String SwitchPage = "page";
	}
	
	public static class DefaultValue {
		
		public static final int Age = -1;
		public static final int Time = -1;
		public static final int Distance = -1;
	}
	
	public static class SharedPreferenceKey {
		public static final String DefaultName = "ouser";
		
		public static final String FirstStartup = "v1_0_first";
	}
	
	/** 升级服务器地址 */
	public static final String UpgradeServer = "http://ouser.zhengre.com/upgrade";

	public static OuserApplication Application = null;

	// 调试
	public static final boolean FakeProtocol = false;

	public static final String WorkDir = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/ouser/";

	// 地图缩放
	public static final int MapZoom = 16;
	public static final int PoiResultCount = 25;
	public static final int PoiSearchRaduis = 5 * 1000;
	
	/**
	 * time
	 */
	public static final int MessageCountInterval = 5;
	public static final int MessageChatInterval = 10;
	
	/** 消息会话间隔 */
	public static final int SessionInterval = 15 * 60 * 1000;
	
	/** 语音聊天的最小间隔 */
	public static final int ChatVoiceMinDuring = 1000;
	
	/** 两次点击back退出应用的最小应用 */
	public static final int ExitAppClickBackMinInterval = 2000;

	/**
	 * limit
	 */
	/** 获取藕丝每次获取的条数 */
	public static final int OuserFetchCount = 20;
	
	/** 所有友约每次获取的条数 */
	public static final int AppointsFetchCount = 20;
	
	/** 动态每次获取条数 */
	public static final int TimelineFetchCount = 20;
	
	/** 消息每次获取条数 */
	public static final int MessagFetchCount = 20;
	
	
	/**
	 * cache
	 */
	/** 照片下载最大失败次数 */
	public static final int PhotoMaxTryTime = 3;

	/** 藕丝数据的失效时间间隔，毫秒 */
	public static final int OuserInvalidTimeout = 5 * 60 * 1000;
}
