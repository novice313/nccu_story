package com.example.android.wifidirect;

import java.io.BufferedOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.mclab1.place.events.NewServerConnectionEvent;
import com.mclab1.place.events.WifiP2PGroupInfoEvent;

import de.greenrobot.event.EventBus;

/**
 * ConnectionChangeReceiver
 * 
 * @author benny Yang on 2011-12-23
 * 
 */
public class WifiReceiver extends BroadcastReceiver {
	public Boolean if_tested = false;
	public static final String SOCKET_TAG_STRING = "wifi-socket-test";

	

	@Override
	public void onReceive(Context context, Intent intent) {

		NetworkInfo info = intent
				.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
		if (info != null) {
			if (info.isConnected()) {
				// Do your work.

				// e.g. To check the Network Name or other info:
				WifiManager wifiManager = (WifiManager) context
						.getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				String ssid = wifiInfo.getSSID();
				String getBssID=wifiInfo.getBSSID();
				WifiP2PGroupInfoEvent wifiP2PGroupInfoEvent = (WifiP2PGroupInfoEvent) EventBus
						.getDefault().getStickyEvent(
								WifiP2PGroupInfoEvent.class);
				String ssid_cur="";
				if (wifiP2PGroupInfoEvent != null) {
					 ssid_cur=wifiP2PGroupInfoEvent.ssid;
				}
				Log.i("wifidirect-network-check", ssid+getBssID);	
				if (ssid.equals(ssid_cur)) {
					Log.i(SOCKET_TAG_STRING, "Testing...");
					test_socket_server();
				}
				//test_socket_server();
			}
		}
	}
	
	private synchronized void test_socket_server() {
		if (!if_tested) {

			if_tested = false;
			new Thread() {
				@Override
				public void run() {
					Socket client = new Socket();
					InetSocketAddress isa = new InetSocketAddress(
							"192.168.49.1", 8887);
					try {
						client.connect(isa, 10000);
						Log.i(SOCKET_TAG_STRING, "Connected!!");
						BufferedOutputStream out = new BufferedOutputStream(
								client.getOutputStream());
						// �摮葡
						out.write("Send From Client ".getBytes());
						out.flush();
						out.close();
						out = null;
						client.close();
						client = null;
						EventBus.getDefault().post(new NewServerConnectionEvent("192.168.49.1"));

					} catch (Exception e) {
						Log.i(SOCKET_TAG_STRING, "error!!!");

						Log.i(SOCKET_TAG_STRING, e.toString());
					}
				}
			}.start();

		}
	}
}