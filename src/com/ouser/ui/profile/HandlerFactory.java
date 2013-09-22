package com.ouser.ui.profile;


class HandlerFactory {

	private InfoHandler mInfo = new InfoHandler();
	private PhotoHandler mPhoto = new PhotoHandler();
	private NewPhotoHandler mNewPhoto = new NewPhotoHandler();
	
	private BaseHandler[] mHandlers = new BaseHandler[] {mInfo, mPhoto, mNewPhoto};
	
	public InfoHandler getInfo() {
		return mInfo;
	}
	
	public PhotoHandler getPhoto() {
		return mPhoto;
	}
	
	public NewPhotoHandler getNewPhoto() {
		return mNewPhoto;
	}

	public BaseHandler[] getHandlers() {
		return mHandlers;
	}
}
