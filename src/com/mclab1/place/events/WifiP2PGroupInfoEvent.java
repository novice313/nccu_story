package com.mclab1.place.events;

public class WifiP2PGroupInfoEvent {
	public String ssid = "";
	public String passphrase = "";

	public WifiP2PGroupInfoEvent(String ssid_,String passphrase_) {
		ssid=ssid_;
		passphrase=passphrase_;
	}
}
