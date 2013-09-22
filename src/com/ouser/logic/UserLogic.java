package com.ouser.logic;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.ouser.R;
import com.ouser.cache.Cache;
import com.ouser.event.EventArgs;
import com.ouser.event.EventCenter;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.location.LocationManager;
import com.ouser.logger.Logger;
import com.ouser.logic.event.MessageAndTimelineCountEventArgs;
import com.ouser.logic.event.OuserEventArgs;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.module.Location;
import com.ouser.module.Ouser;
import com.ouser.module.User;
import com.ouser.protocol.ForgotPasswordProcess;
import com.ouser.protocol.GetMessageCountProcess;
import com.ouser.protocol.LoginProcess;
import com.ouser.protocol.ModifyPasswordProcess;
import com.ouser.protocol.RegisterProcess;
import com.ouser.protocol.ResponseListener;
import com.ouser.pusher.Pusher;
import com.ouser.pusher.PusherManager;
import com.ouser.stat.Stat;
import com.ouser.stat.StatId;
import com.ouser.ui.topframework.TopFragmentType;
import com.ouser.ui.user.WelcomeActivity;
import com.ouser.util.Const;
import com.ouser.util.ImageUtil;
import com.ouser.util.StringUtil;
import com.ouser.util.SystemUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @author hanlixin
 * @remark 登录失败，要复位，清空所有信息，主要是自动登录要清空，有可能第二次进入app，缓存中已经有数据了。注册不需要
 */
public class UserLogic extends BaseLogic {

	static class Factory implements BaseLogic.Factory {

		@Override
		public BaseLogic create() {
			return new UserLogic();
		}
	}
	
	UserLogic() {
	}
	
	private static final Logger logger = new Logger("userlogic");
	
	/** 聊天消息推送 */
	private static MessageCountPusher mMessageCountPusher = new MessageCountPusher();

	/**
	 * app启动
	 */
	public void appStrat() {

		MobclickAgent.setSessionContinueMillis(60000);
		MobclickAgent.onError(Const.Application);
		
		// TODO
		// move to initialize
		Cache.self().onCreate();
	}
	
	public void showWelcome() {
		Stat.onEvent(StatId.Active);
	}

	/**
	 * 注册
	 * @param user
	 * @param ouser
	 * @param image
	 * @param listener
	 */
	public void register(final User user, final Ouser ouser, final Bitmap image, final EventListener listener) {
		Stat.onEvent(StatId.Register);
		
		Location myLocation = LocationManager.self().getCurrent();
		if (!myLocation.isEmpty()) {
			logger.d("location is not empty, direct login");
			continueRegister(user, ouser, image, listener);
		} else {
			logger.d("location is empty, first fetch location");
			LocationManager.self().fetchCurrent();
			EventCenter.self().addListener(EventId.eLocationChanged, new EventListener() {
				
				@Override
				public void onEvent(EventId id, EventArgs args) {
					logger.d("recv location changed event");
					EventCenter.self().removeListener(this);
					
					StatusEventArgs statusArgs = (StatusEventArgs)args;
					if(statusArgs.getErrCode() == OperErrorCode.Success) {
						continueRegister(user, ouser, image, listener);
					} else {
						fireStatusEvent(listener, OperErrorCode.LocationNotAviable);
					}
				}
			});
		}
	}
	
	/**
	 * 定位成功后继续执行注册
	 * @param user
	 * @param ouser
	 * @param image
	 * @param listener
	 */
	private void continueRegister(final User user, final Ouser ouser, Bitmap image, final EventListener listener) {
		final RegisterProcess process = new RegisterProcess();
		process.setOuser(ouser);
		process.setPhoneUuid(SystemUtil.getPhoneUuid(Const.Application));
		process.setPortraitSuffix("JPEG");
		process.setUser(user);
		process.setPortrait(ImageUtil.toBase64(image));
		process.run(new ResponseListener() {
			
			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				if (errCode == OperErrorCode.Success) {
					// 注册成功，简单起见，直接登录
					continueLogin(user.getUid(), user.getPassword(), listener);
				} else {
					fireStatusEvent(listener, errCode);
				}				
			}
		});
	}

	/**
	 * 是否可以不输入用户名和密码直接登录
	 * @return
	 */
	public boolean canLoginWithLatest() {
		return Cache.self().hasCookie();
	}

	/**
	 * 通过用户名和密码登录
	 * @param uid
	 * @param password
	 */
	public void login(final String uid, final String password, final EventListener listener) {
		logger.d("login with username and password " + uid);

		Stat.onEvent(StatId.Login);
		
		Location myLocation = LocationManager.self().getCurrent();
		if (!myLocation.isEmpty()) {
			logger.d("location is not empty, direct login");
			continueLogin(uid, password, listener);
		} else {
			logger.d("location is empty, first fetch location");
			LocationManager.self().fetchCurrent();
			EventCenter.self().addListener(EventId.eLocationChanged, new EventListener() {
				
				@Override
				public void onEvent(EventId id, EventArgs args) {
					logger.d("recv location changed event");
					EventCenter.self().removeListener(this);
					
					StatusEventArgs statusArgs = (StatusEventArgs)args;
					if(statusArgs.getErrCode() == OperErrorCode.Success) {
						continueLogin(uid, password, listener);
					} else {
						fireStatusEvent(listener, OperErrorCode.LocationNotAviable);
						reset();
					}
				}
			});
		}
	}

	/**
	 * 定位成功后继续执行登录
	 * @param uid
	 * @param password
	 * @param listener
	 */
	private void continueLogin(String uid, String password, final EventListener listener) {

		// 清空当前user信息
		Cache.self().clearMySelf();

		// 创建用于登录过程的数据
		final User user = new User();
		user.setUid(uid);
		user.setPassword(password);
		user.setLocation(LocationManager.self().getCurrent());

		// 登录
		final LoginProcess process = new LoginProcess();
		process.setUser(user);
		process.setPhoneUuid(SystemUtil.getPhoneUuid(Const.Application));
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {

				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				logger.d("login process response, " + errCode);
				
				// 无论是否成功，都设置地理位置
				Cache.self().setMyLocation(user.getLocation());
				
				if (errCode == OperErrorCode.Success) {
					// 登录成功，设置信息，包括user，ouser，message
					Cache.self().setMySelf(user);
					Cache.self().setOuser(process.getMyself());
					Cache.self().setMessageCount(process.getUnReadedMessageCount(),
							process.getTimelineCount());
					
					// 启动message监听
					startListenMessageCount();
					
					// 发送事件
					fireStatusEvent(listener, OperErrorCode.Success);
					fireEvent(EventId.eGetOuserProfile, new OuserEventArgs(process.getMyself()));
					fireEvent(EventId.ePushMessageAndTimelineCount,
							new MessageAndTimelineCountEventArgs(process.getUnReadedMessageCount(),
									process.getTimelineCount()));
				} else {
					fireStatusEvent(listener, errCode);
					reset();
				}
			}
		});
	}

	/**
	 * 使用最近登录成功的用户信息登录
	 */
	public void loginWithLatest(final EventListener listener) {
		logger.d("login with latest");
		if (!Cache.self().hasCookie()) {
			logger.e("has not cookie, but login with latest");
			fireStatusEvent(listener, OperErrorCode.Unknown);
			reset();
			return;
		}
		User userInfo = Cache.self().getMySelfUser();
		login(userInfo.getUid(), userInfo.getPassword(), listener);
	}

	/**
	 * 退出登录 会复位
	 */
	public void logout() {
		logger.d("logout");
		reset();
	}
	
	/**
	 * 修改密码
	 * @param oldPassword
	 * @param newPassword
	 * @param listener
	 */
	public void modifyPasswrod(String oldPassword, final String newPassword, final EventListener listener) {
		final ModifyPasswordProcess process = new ModifyPasswordProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.setOldPassword(oldPassword);
		process.setNewPassword(newPassword);
		process.run(new ResponseListener() {
			
			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				if(errCode == OperErrorCode.Success) {
					Cache.self().getMySelfUser().setPassword(newPassword);
				}
				fireStatusEvent(listener, errCode);
			}
		});
	}
	
	/**
	 * 忘记密码
	 * @param uid
	 * @param listener
	 */
	public void forgotPassword(String uid, final EventListener listener) {
		final ForgotPasswordProcess process = new ForgotPasswordProcess();
		process.setMyUid(uid);
		process.run(new ResponseListener() {
			
			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				fireStatusEvent(listener, errCode);
			}
		});
	}

	/**
	 * 开始监听消息和动态未读数
	 */
	public void startListenMessageCount() {
		mMessageCountPusher.start();
	}

	/**
	 * 停止监听消息和动态未读数
	 */
	public void stopListenMessageCount() {
		mMessageCountPusher.stop();
	}
	
	/**
	 * 设置自动登录
	 * @param value
	 */
	public void setAutoLogin(boolean value) {
		SharedPreferences sp = Const.Application.getSharedPreferences("setting", Context.MODE_PRIVATE);
		sp.edit().putBoolean("autologin", value).commit();
	}
	
	/**
	 * 是否自动登录
	 * @return
	 */
	public boolean isAutoLogin() {
		SharedPreferences sp = Const.Application.getSharedPreferences("setting", Context.MODE_PRIVATE);
		return sp.getBoolean("autologin", true);
	}

	/**
	 * 复位，清空所有信息，停止监听
	 */
	private void reset() {
		stopListenMessageCount();
		Cache.self().clearMySelf();
		LocationManager.self().clear();
	}

	/**
	 * 消息和动态数量的推送器
	 * @author hanlixin
	 */
	@SuppressLint("HandlerLeak")
	private static class MessageCountPusher extends BaseLogic implements Pusher {
		
		// 连续5次符合条件后，才显示通知栏
		private static final int ShowNotificationDelayMaxTime = 5;
		
		private PendingIntent mPendingIntent = null;
		private NotificationManager mManager = null;
		
		private int mShowNotificationDelayTime = 0;
		
		private Handler mDetectHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				check();
				sendEmptyMessageDelayed(0, 1000);
			}
		};
		
		public MessageCountPusher() {
			mDetectHandler.sendEmptyMessageDelayed(0, 1000);
		}

		@Override
		public void onRequest() {
			String myUid = Cache.self().getMyUid();
			if(StringUtil.isEmpty(myUid)) {
				// 找不到uid
				// 可能是application直接被销毁，缓存数据被清空了
				stop();
				return;
			}
			
			final GetMessageCountProcess process = new GetMessageCountProcess();
			process.setMyUid(Cache.self().getMyUid());
			process.run(new ResponseListener() {

				@Override
				public void onResponse(String requestId) {
					OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
					if (errCode != OperErrorCode.Success) {
						return;
					}
					fireEvent(EventId.ePushMessageAndTimelineCount,
							new MessageAndTimelineCountEventArgs(process.getUnReadedMessageCount(),
									process.getTimelineCount()));
					Cache.self().setMessageCount(process.getUnReadedMessageCount(),
							process.getTimelineCount());
				}
			});
		}
		
		public void start() {
			clearNotfication();
			PusherManager.self().startListen(
					this, "messagecount", Const.MessageCountInterval, false);
		}
		
		public void stop() {
			PusherManager.self().stopListen("messagecount");
			mDetectHandler.removeMessages(0);
			clearNotfication();
		}
		
		private void check() {
			if(Cache.self().getUnReadedCount() == 0) {
				clearNotfication();
			} else {
				if(Const.Application.isAllActivityHide()) {
					if(mShowNotificationDelayTime == ShowNotificationDelayMaxTime) {
						setNotification();
					} else {
						++mShowNotificationDelayTime;
					}
				} else {
					mShowNotificationDelayTime = 0;
					clearNotfication();
				}
			}
		}
		
		private void setNotification() {
			String title = "藕丝儿";
			String text = "您有" + Cache.self().getUnReadedCount() + "条未读消息";

			Notification notification = new Notification(
					R.drawable.logo, text, System.currentTimeMillis());
			notification.setLatestEventInfo(Const.Application, title, text, getPendingIntent());
			notification.flags |= Notification.FLAG_AUTO_CANCEL;

			getManager().notify(0,notification);
		}
		
		private void clearNotfication() {
			getManager().cancel(0);
		}
		
		private PendingIntent getPendingIntent() {
			if(mPendingIntent == null) {
				// TODO 依赖界面层了
				Intent intent = new Intent(Const.Application, WelcomeActivity.class);
				intent.putExtra(Const.Intent.SwitchPage, TopFragmentType.MyMessage.getValue());
				mPendingIntent = PendingIntent.getActivity(
						Const.Application, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			}
			return mPendingIntent;
		}
		
		private NotificationManager getManager() {
			if(mManager == null) {
				mManager = 
						(NotificationManager) Const.Application.getSystemService(Context.NOTIFICATION_SERVICE);
			}
			return mManager;
		}
	}
}
