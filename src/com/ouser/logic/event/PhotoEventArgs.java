package com.ouser.logic.event;

import com.ouser.logic.OperErrorCode;
import com.ouser.module.Photo;

public class PhotoEventArgs extends StatusEventArgs {

	private Photo photo = null;
	
	public PhotoEventArgs(Photo photo) {
		super(OperErrorCode.Success);
		this.photo = photo;
	}
	
	public PhotoEventArgs(OperErrorCode errCode) {
		super(errCode);
	}
	
	public Photo getPhoto() {
		return this.photo;
	}
}
