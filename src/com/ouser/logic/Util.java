package com.ouser.logic;

import com.ouser.protocol.ProcessStatus;


class Util {
	
	public static OperErrorCode convertFromStatus(ProcessStatus status) {
		switch(status) {
		case Success:
			return OperErrorCode.Success;
		case ErrUid:
			return OperErrorCode.UidNoExist;
		case ErrPass:
			return OperErrorCode.PasswordError;
		case ErrPhotoIsPortrait:
			return OperErrorCode.PhotoIsPortait;
		case ErrUidExist:
			return OperErrorCode.UidExist;
		case ErrUidInvalid:
			return OperErrorCode.UidInvalid;
		case ErrNetDisable:
			return OperErrorCode.NetNotAviable;
		default:
			return OperErrorCode.Unknown;
		}
	}
}
