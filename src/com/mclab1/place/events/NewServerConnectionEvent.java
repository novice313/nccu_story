package com.mclab1.place.events;

public class NewServerConnectionEvent {
	public String client_ip = "";

	public NewServerConnectionEvent(String ip_) {
		client_ip = ip_;
	}
}
