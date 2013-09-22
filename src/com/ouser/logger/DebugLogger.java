package com.ouser.logger;

public class DebugLogger {

	private static final boolean enable = true;
	private static Logger logger = new Logger("+Debugger+");
	
	public static void write(String msg) {
		if(enable) {
			logger.w(msg);
		}
	}
}
