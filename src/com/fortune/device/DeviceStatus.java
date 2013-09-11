package com.fortune.device;

import java.io.Serializable;

public class DeviceStatus implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String SSID;
	String BSSID;
	String password;
	int status;

	public DeviceStatus(String SSID, String BSSID,
			String password, int status) {
		this.SSID = SSID;
		this.BSSID = BSSID;
		this.password = password;
		this.status = status;
	}

	public String getSSID() {
		return SSID;
	}

	public String getBSSID() {
		return BSSID;
	}

	public String getPassword() {
		return password;
	}
	
	public int getStatus() {
		return status;
	}
}
