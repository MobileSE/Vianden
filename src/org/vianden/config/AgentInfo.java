package org.vianden.config;

public class AgentInfo {
	private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/601.6.17 (KHTML, like Gecko) Version/9.1.1 Safari/601.6.17";
	private static final int TIME_OUT = 10000000;
	public AgentInfo() {
	}

	public static String getUSER_AGENT() {
		return USER_AGENT;
	}
	
	public static int getTIME_OUT() {
		return TIME_OUT;
	}
/*
	public void setUSER_AGENT(String uSER_AGENT) {
		USER_AGENT = uSER_AGENT;
	}
*/
}
