package com.ouser.logic.event;

import com.ouser.logic.OperErrorCode;

public class RadarServerEventArgs extends StatusEventArgs {

	private String server = "";
	private int port = 0;
	
	public RadarServerEventArgs(String server, int port) {
		super(OperErrorCode.Success);
		this.server = server;
		this.port = port;
	}
	public RadarServerEventArgs(OperErrorCode errCode) {
		super(errCode);
	}

	public String getServer() {
		return server;
	}
	public int getPort() {
		return port;
	}
}
