package com.ouser.logic;

import android.graphics.Bitmap;

import com.ouser.cache.Cache;
import com.ouser.event.EventListener;
import com.ouser.logic.event.PhotoEventArgs;
import com.ouser.module.Photo;
import com.ouser.protocol.AddPhotoProcess;
import com.ouser.protocol.RemovePhotoProcess;
import com.ouser.protocol.ResponseListener;
import com.ouser.util.ImageUtil;

public class PhotoLogic extends BaseLogic {

	static class Factory implements BaseLogic.Factory {

		@Override
		public BaseLogic create() {
			return new PhotoLogic();
		}
	}
	
	PhotoLogic() {
	}
	
	public void add(Bitmap bitmap, final EventListener listener) {
		final AddPhotoProcess process = new AddPhotoProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.setSuffix("JPEG");
		process.setData(ImageUtil.toBase64(bitmap));
		process.run(new ResponseListener() {
			
			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				PhotoEventArgs args = null;
				if(errCode == OperErrorCode.Success) {
					Cache.self().invalidOuser(Cache.self().getMyUid());
					Photo photo = new Photo();
					photo.setUrl(process.getResult());
					args = new PhotoEventArgs(photo);
				} else {
					args = new PhotoEventArgs(errCode);
				}
				fireEvent(listener, args);
			}
		});
	}
	
	public void remove(String url, final EventListener listener) {
		final RemovePhotoProcess process = new RemovePhotoProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.setUrl(url);
		process.run(new ResponseListener() {
			
			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				if(errCode == OperErrorCode.Success) {
					Cache.self().invalidOuser(Cache.self().getMyUid());
				}
				fireStatusEvent(listener, errCode);
			}
		});
	}
	
}
