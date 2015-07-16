package com.mclab1.palace.recorder;

import java.net.DatagramSocket;
import java.net.InetAddress;

import android.media.AudioRecord;
import android.os.Handler;

public class EraseCustomRecorder extends Thread {
	public static InetAddress tempIP=null;
	private Boolean if_mp3 = false;
	private DatagramSocket recorder_socket;
	Handler mhandler;
	private byte[] encodedFrame;
	private AudioRecord recorder;


	public EraseCustomRecorder(InetAddress ip, Boolean if_mp3_) {
		try {
			System.out.println("IPIPIP"+ip+" "+CustomRecorder.customip);
			if (CustomRecorder.customip.equals(ip)) {
				CustomRecorder.customip = null;
				System.out.println("1: " + CustomRecorder.customip);
			}
			else if(CustomRecorder.customip2==ip){
				CustomRecorder.customip2 = null;
				System.out.println("2: " + CustomRecorder.customip2);
			}
			else if(CustomRecorder.customip3==ip){
				CustomRecorder.customip3 = null;
				System.out.println("3: " + CustomRecorder.customip3);
			}
			else if(CustomRecorder.customip4==ip){
				CustomRecorder.customip4 = null;
				System.out.println("4: " + CustomRecorder.customip4);
			}
			else if(CustomRecorder.customip5==ip){
				CustomRecorder.customip5 = null;
				System.out.println("5: " + CustomRecorder.customip5);
			}
			else if(CustomRecorder.customip6==ip){
				CustomRecorder.customip6 = null;
				System.out.println("6: " + CustomRecorder.customip6);
			}


			//customip3=ip;
			tempIP=ip;
			System.out.println("Eraserecord"+" "+CustomRecorder.customip + " " +CustomRecorder.customip2 + " " 
			+CustomRecorder.customip3+" "+CustomRecorder.customip4+" "+CustomRecorder.customip5);

			if_mp3 = if_mp3_;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}



}
