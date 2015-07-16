package com.mclab1.place.events;

public class EraseclientConnectionEvent {
	public String client_ip = "";

	public EraseclientConnectionEvent(String ip_) {
		client_ip = ip_;
	}
}
