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
	String ip;
	
	float volt;
	float amp;
	int status;
	int time;
	
	public DeviceStatus(String SSID, String BSSID,
			String password, String ip, float volt, float amp, int status, int time) {
		this.SSID = SSID;
		this.BSSID = BSSID;
		this.password = password;
		this.ip = ip;
		
		this.volt = volt;
		this.amp = amp;
		this.status = status;
		this.time = time;
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
	
	public String getIP() {
		return ip;
	}
	
	public void setIP(String ip) {
		this.ip = ip;
	}
	
	public float getVolt() {
		return volt;
	}
	
	public void setVolt(float volt) {
		this.volt = volt;
	}
	
	public float getAmp() {
		return amp;
	}
	
	public void setAmp(float amp) {
		this.amp = amp;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public int getTime() {
		return time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
}
