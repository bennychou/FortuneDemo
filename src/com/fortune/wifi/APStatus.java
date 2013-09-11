package com.fortune.wifi;

import java.io.Serializable;

public class APStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String SSID;
	String BSSID;
	String capabilities;
	int frequency;
	int level;
	String password;

	public APStatus(String SSID, String BSSID,
			String capabilities, int frequency, int level, String password) {
		this.SSID = SSID;
		this.BSSID = BSSID;
		this.capabilities = capabilities;
		this.frequency = frequency;
		this.level = level;
		this.password = password;
	}

	public String getSSID() {
		return SSID;
	}

	public String getBSSID() {
		return BSSID;
	}

	public String getCapabilities() {
		return capabilities;
	}
	
	public int getFrequency() {
		return frequency;
	}
	
	public int getLevel() {
		return level;
	}
	
	public String getPassword() {
		return password;
	}
}
