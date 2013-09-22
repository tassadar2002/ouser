package com.ouser.event;

public enum EventId {
	
	eNone,
	eAll,

	// ouser
	eGetOuserProfile,
	ePhotoDownloadComplete,
	
	// appoint
	eGetAppointInfo,

	// count
	ePushMessageAndTimelineCount,
	
	// chat
	ePushMessage,
	eSendMessage,
	eMessageListChanged,
	
	// location
	eLocationChanged,
	eGetAddress,
	eGetPoiAddress,
}
