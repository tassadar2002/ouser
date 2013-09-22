package com.ouser.protocol;

public enum ProcessStatus {
	
	Success,
	
	/** email不存在 */
	ErrUid,
	
	/** 密码错误 */
	ErrPass,
	
	/** email已经存在 */
	ErrUidExist,

	/** email不合法 */
	ErrUidInvalid,

	/** 要删除的图片是头像，不能删除 */
	ErrPhotoIsPortrait,
	
	ErrNetDisable,
	
	ErrException,
	ErrUnkown,
}
