package com.mclab1.place.events;

public class NewClientConnectionEvent {
	public String client_ip = "";

	public NewClientConnectionEvent(String ip_) {
		client_ip = ip_;
	}
}
