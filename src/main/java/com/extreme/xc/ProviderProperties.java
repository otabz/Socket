package com.extreme.xc;

public class ProviderProperties {

	private final String agent;
	private final String pin;
	private final String terminal;
	private final String ip;
	private final int port;
	private final int timeOut1Minute;

	
	private ProviderProperties() {
		agent = "287"; // staging
		//agent = "50001"; // production
		pin = "1120557217105980"; // staging
		//pin = "4766435408911570"; // production
		terminal = "11111111";
		ip = "212.93.162.47"; // staging
		//ip = "212.93.162.44"; //production
		port=7104;
		timeOut1Minute = 60000;
	}
	
	 private static class SingletonHolder {
	        private static final ProviderProperties INSTANCE = new ProviderProperties();
	    }

	    public static ProviderProperties getInstance() {
	        return SingletonHolder.INSTANCE;
	    }

		public String getAgent() {
			return agent;
		}

		public String getPin() {
			return pin;
		}

		public String getTerminal() {
			return terminal;
		}

		public String getIp() {
			return ip;
		}

		public int getPort() {
			return port;
		}

		public int getTimeOut1Minute() {
			return timeOut1Minute;
		}
}
