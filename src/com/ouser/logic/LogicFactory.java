package com.ouser.logic;

import java.util.HashMap;
import java.util.Map;

public class LogicFactory {

	private static final LogicFactory ins = new LogicFactory();
	public static final LogicFactory self() {
		return ins;
	}
	
	private enum Type {
		AppointInfo,
		Appoint,
		Chat,
		Ouser,
		Photo,
		Profile,
		Radar,
		Shake,
		Timeline,
		Upgrade,
		User,
		Share,
		Emotion,
	}
	
	private Map<Type, BaseLogic.Factory> mFactorys = new HashMap<Type, BaseLogic.Factory>();
	private Map<Type, BaseLogic> mLogics = new HashMap<Type, BaseLogic>();
	
	private LogicFactory() {
		mFactorys.put(Type.AppointInfo, new AppointInfoLogic.Factory());
		mFactorys.put(Type.Appoint, new AppointLogic.Factory());
		mFactorys.put(Type.Chat, new ChatLogic.Factory());
		mFactorys.put(Type.Ouser, new OuserLogic.Factory());
		mFactorys.put(Type.Photo, new PhotoLogic.Factory());
		mFactorys.put(Type.Profile, new ProfileLogic.Factory());
		mFactorys.put(Type.Radar, new RadarLogic.Factory());
		mFactorys.put(Type.Shake, new ShakeLogic.Factory());
		mFactorys.put(Type.Timeline, new TimelineLogic.Factory());
		mFactorys.put(Type.Upgrade, new UpgradeLogic.Factory());
		mFactorys.put(Type.User, new UserLogic.Factory());
		mFactorys.put(Type.Share, new ShareLogic.Factory());
		mFactorys.put(Type.Emotion, new EmotionLogic.Factory());
	}
	
	private BaseLogic get(Type type) {
		if(!mLogics.containsKey(type)) {
			mLogics.put(type, mFactorys.get(type).create());
		}
		return mLogics.get(type);
	}

	public AppointInfoLogic getAppointInfo() {
		return (AppointInfoLogic)get(Type.AppointInfo);
	}
	
	public AppointLogic getAppoint() {
		return (AppointLogic)get(Type.Appoint);
	}
	
	public ChatLogic getChat() {
		return (ChatLogic)get(Type.Chat);
	}
	
	public OuserLogic getOuser() {
		return (OuserLogic)get(Type.Ouser);
	}
	
	public PhotoLogic getPhoto() {
		return (PhotoLogic)get(Type.Photo);
	}
	
	public ProfileLogic getProfile() {
		return (ProfileLogic)get(Type.Profile);
	}
	
	public RadarLogic getRadar() {
		return (RadarLogic)get(Type.Radar);
	}
	
	public ShakeLogic getShake() {
		return (ShakeLogic)get(Type.Shake);
	}
	
	public TimelineLogic getTimeline() {
		return (TimelineLogic)get(Type.Timeline);
	}
	
	public UpgradeLogic getUpgrade() {
		return (UpgradeLogic)get(Type.Upgrade);
	}
	
	public UserLogic getUser() {
		return (UserLogic)get(Type.User);
	}
	
	public ShareLogic getShare() {
		return (ShareLogic)get(Type.Share);
	}
	
	public EmotionLogic getEmotion() {
		return (EmotionLogic)get(Type.Emotion);
	}
}
