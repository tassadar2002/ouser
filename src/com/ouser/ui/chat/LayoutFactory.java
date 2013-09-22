package com.ouser.ui.chat;

import com.ouser.ui.map.RadarInfo;

class LayoutFactory {

	private InfoLayout mInfo = new InfoLayout();
	private MessageLayout mMessage = new MessageLayout();
	private InputLayout mInput = new InputLayout();
	private EditLayout mEdit = new EditLayout();
	private RadarInfo mRadar = new RadarInfo();
	
	private ChatBaseLayout[] mLayouts = new ChatBaseLayout[] {mInfo, mMessage, mInput, mEdit, mRadar};
	
	public InfoLayout getInfo() {
		return mInfo;
	}
	
	public MessageLayout getMessage() {
		return mMessage;
	}
	
	public InputLayout getInput() {
		return mInput;
	}
	
	public EditLayout getEdit() {
		return mEdit;
	}
	
	public RadarInfo getRadar() {
		return mRadar;
	}
	
	public ChatBaseLayout[] getLayouts() {
		return mLayouts;
	}
}
