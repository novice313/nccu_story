package com.mclab1.place.events;

public class EraseServerConnectionEvent {
	public String client_ip = "";

	public EraseServerConnectionEvent(String ip_) {
		client_ip = ip_;
	}
}
