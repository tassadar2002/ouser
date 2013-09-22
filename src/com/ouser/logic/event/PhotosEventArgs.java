package com.ouser.logic.event;

import com.ouser.logic.OperErrorCode;
import com.ouser.module.PhotosWithOwner;

public class PhotosEventArgs extends StatusEventArgs {

	private PhotosWithOwner mPhotos = null;
	
	public PhotosEventArgs(PhotosWithOwner value) {
		super(OperErrorCode.Success);
		mPhotos = value;
	}
	public PhotosEventArgs(OperErrorCode errCode) {
		super(errCode);
	}
	
	public PhotosWithOwner getPhotos() {
		return mPhotos;
	}
}
