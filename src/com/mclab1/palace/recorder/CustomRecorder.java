package com.mclab1.palace.recorder;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import ro.ui.pttdroid.codecs.Speex;
import ro.ui.pttdroid.settings.AudioSettings;
import ro.ui.pttdroid.settings.CommSettings;
import ro.ui.pttdroid.util.Audio;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.mclab1.palaca.parsehelper.VoiceObject;
import com.mclab1.palace.connection.ClientConnectionService;
import com.mclab1.palace.connection.VoiceData;
import com.mclab1.palace.guider.DisplayEvent;
import com.pocketdigi.utils.FLameUtils;

import de.greenrobot.event.EventBus;

public class CustomRecorder extends Thread {
	public static final String tag = "custom recorder";
	private AudioRecord recorder;

	private volatile boolean recording = false;
	private volatile boolean running = true;

	private DatagramSocket recorder_socket;
	private DatagramPacket packet;

	private short[] pcmFrame = new short[Audio.FRAME_SIZE];
	private short[] pcmFrame_record;
	private byte[] encodedFrame;
	private byte[] tempencodedFrame;
	DataOutputStream output = null;
	public static InetAddress customip = null;
	public static InetAddress customip2 = null;
	public static InetAddress customip3 = null;
	public static InetAddress customip4 = null;
	public static InetAddress customip5 = null; 
	public static InetAddress customip6 = null;
	public static InetAddress tempIP=null;
	private Boolean if_mp3 = false;
	Handler mhandler;
	int count=0;
	public  static Boolean [] if_runthread ={false};
	// Recording

	boolean isRecording = false;

	final String tempFile = Environment.getExternalStorageDirectory()
			+ "/aa.raw";
	final String mp3File = Environment.getExternalStorageDirectory()
			+ "/bbb.mp3";

	// safe lock for file
	private Boolean if_writing_raw = false;
	// for testing

	long start_time = 0;

	private List<VoiceData> rawVoiceQueue;
	

	private synchronized void set_raw_file_writing() {
		if_writing_raw = true;
	}

	private synchronized void set_raw_file_not_writing() {
		if_writing_raw = false;
	}

	private synchronized boolean if_raw_writing() {
		return if_writing_raw;
	}

	private void enqueue(byte[] data) {

	}

	@Override
	public void run() {
		android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		EventBus.getDefault().postSticky(
				new DisplayEvent("isRunning start"));
		while (isRunning()) {

			
			recorder.startRecording();
			// startBufferedWrite(new File(tempFile));
			set_raw_file_writing();
			start_time = System.nanoTime();
			EventBus.getDefault().postSticky(
					new DisplayEvent("isRecording start"));
			while (isRecording()) {

				if (AudioSettings.useSpeex() == AudioSettings.USE_SPEEX) {
					int readSize = recorder.read(pcmFrame, 0, Audio.FRAME_SIZE);
					EventBus.getDefault().postSticky(
					new DisplayEvent("readSize:"+readSize));

					VoiceData voicedata = new VoiceData();
					voicedata.data = new short[readSize];
					for (int i = 0; i != readSize; i++) {
						voicedata.data[i] = pcmFrame[i];
					}

					voicedata.length = readSize;
					rawVoiceQueue.add(voicedata);

					// pcmFrame_record=new short[Audio.FRAME_SIZE];
					// System.arraycopy(pcmFrame, 0, pcmFrame_record, 0,
					// pcmFrame.length);
					Speex.encode(pcmFrame, encodedFrame);
				} else {
					recorder.read(encodedFrame, 0, Audio.FRAME_SIZE_IN_BYTES);
				}
				count=count+1;
				//EventBus.getDefault().postSticky(
						//new DisplayEvent("start_time:"+start_time+" "+count));
				if(count%100==0){
					//EventBus.getDefault().postSticky(
							//new DisplayEvent("Ready to release!!!"));
					tempencodedFrame=encodedFrame;
					//encodedFrame= null;
					//System.gc();
				}
				EventBus.getDefault().postSticky(
						new DisplayEvent("thread start"));
				new Thread() {
					@Override
					public void run() {
						if ((customip != null)) {
							packet = new DatagramPacket(encodedFrame,
									encodedFrame.length, customip,
									CommSettings.getPort());
							try {
								EventBus.getDefault().postSticky(
										new DisplayEvent("thread in"));
								recorder_socket.send(packet);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					}
				}.start();
				EventBus.getDefault().postSticky(
						new DisplayEvent("thread out"));

				new Thread() {
					@Override
					public void run() {
						if (customip2 != null) {

							packet = new DatagramPacket(encodedFrame,
									encodedFrame.length, customip2,
									CommSettings.getPort());
							try {
								recorder_socket.send(packet);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					}
				}.start();

				new Thread() {
					@Override
					public void run() {
						if (customip3 != null) {

							packet = new DatagramPacket(encodedFrame,
									encodedFrame.length, customip3,
									CommSettings.getPort());
							try {
								recorder_socket.send(packet);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					}
				}.start();
				new Thread() {
					@Override
					public void run() {
						if (customip4!= null) {
						
							packet = new DatagramPacket(encodedFrame,
									encodedFrame.length, customip4,
									CommSettings.getPort());
							try {
								recorder_socket.send(packet);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					}
				}.start();
				
				new Thread() {
					@Override
					public void run() {
						if (customip5!= null) {
						
							packet = new DatagramPacket(encodedFrame,
									encodedFrame.length, customip5,
									CommSettings.getPort());
							try {
								recorder_socket.send(packet);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					}
				}.start();
				/*
				new Thread() {
					@Override
					public void run() {
						//if (customip4!= null) {
						try {
							customip6=InetAddress.getByName("192.168.49.6");
						} catch (UnknownHostException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
							packet = new DatagramPacket(encodedFrame,
									encodedFrame.length, customip,
									CommSettings.getPort());
							try {
								recorder_socket.send(packet);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						//}

					}
				}.start();*/

			}
			recorder.stop();

			synchronized (this) {
				try {
					if (isRunning())
						wait();
				} catch (InterruptedException e) {
					Log.i(tag, "Error!!");
					e.printStackTrace();
				}
			}
		}
		
		recorder_socket.close();
		recorder.release();
	}

	private Runnable Runnable() {
		// TODO Auto-generated method stub
		return null;
	}
	public void CustomRecorder(InetAddress ip, Boolean if_mp3_) {
		//to do erase somebody will leave
	}

	public CustomRecorder(InetAddress ip, Boolean if_mp3_) {
		try {
			recorder_socket = new DatagramSocket();
			encodedFrame = new byte[Speex.getEncodedSize(AudioSettings
					.getSpeexQuality())];
			/*if (customip == null) {
				customip = ip;
				System.out.println("1: " + customip);

			} if (customip2==null&& !(customip.equals(tempIP))) {//不等於上一個 ip
				customip2 = ip;
				System.out.println("2: " + customip2);
			} else if (customip3==null || !customip2.equals(tempIP) || !customip.equals(tempIP)) {//不等於上一個和上上一個 ip
				customip3 = ip;
				System.out.println("3: " + customip3);
			}*/
			
			if (customip == null) {
				customip = ip;
				if_runthread[0]=true;
				System.out.println("1: " + customip);
			}
			else if(customip2==null){
				if(!customip.equals(ip)){
				customip2 = ip;
				System.out.println("2: " + customip2);
				}
				else{
					customip2 =null;
					System.out.println("222222");
				}
				
			}
			else if(customip3==null){
			if( (!customip.equals(ip) )&& (!customip2.equals(ip))){
				customip3 = ip;
				System.out.println("3: " + customip3);
				
			}
			else{
				customip3 = null;
				System.out.println("3333333");
			}}
			else if(customip4==null){
			if( (!customip.equals(ip) )&& (!customip2.equals(ip)&& (!customip3.equals(ip)))){
				customip4 = ip;
				System.out.println("4: " + customip4);
				
			}
			else{
				customip4 = null;
				System.out.println("4444444");
			}}
			else if(customip5==null){
			if( (!customip.equals(ip) )&& (!customip2.equals(ip)&& (!customip3.equals(ip))&& (!customip4.equals(ip)))){
				customip5 = ip;
				System.out.println("5: " + customip5);
				
			}
			else{
				customip5 = null;
				System.out.println("5555555");
			}}
			else if(customip6==null){
			if( (!customip.equals(ip) )&& (!customip2.equals(ip)&& (!customip3.equals(ip))&& (!customip4.equals(ip)
					&& (!customip5.equals(ip))))){
				customip6 = ip;
				System.out.println("6: " + customip6);
				
			}
			else{
				customip6 = null;
				System.out.println("6666666");
			}

			} 

			//customip3=ip;
			tempIP=ip;
			System.out.println(customip + " " + customip2 + " " + customip3+" "+customip4+" "+customip5+" "+customip6);
			recorder = new AudioRecord(AudioSource.MIC, Audio.SAMPLE_RATE,
					AudioFormat.CHANNEL_IN_MONO, Audio.ENCODING_PCM_NUM_BITS,
					Audio.RECORD_BUFFER_SIZE);
	    	/*try{
	    	if(recorder.getState()==AudioRecord.STATE_INITIALIZED){
                System.out.println( "Audio recorder initialised at a " +recorder.getSampleRate());
      
	    	}
	    	else {
                recorder.release();
                
                recorder = null;
				
			}
	    	}
	    	catch(IllegalArgumentException e){
	    		System.out.println( "recorder error");
	    		
	    	}*/

			if_mp3 = if_mp3_;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private synchronized boolean isRunning() {
		return running;
	}

	private synchronized boolean isRecording() {
		return recording;
	}

	public synchronized void pauseAudio() {
		recording = false;
	}

	public synchronized void start_recording() {
		try {
			output = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(new File(tempFile))));
			rawVoiceQueue = new ArrayList<VoiceData>();
			EventBus.getDefault().postSticky(
					new DisplayEvent("Init recording file!"));

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			EventBus.getDefault().postSticky(
					new DisplayEvent("Failed Init recording file!"));
			e1.printStackTrace();
		}
	}

	public synchronized void stop_recording() {
		// testing

		File file = new File(tempFile);
		if (!file.exists()) {
			EventBus.getDefault().postSticky(
					new DisplayEvent("Failed Init saving mp3 file!"));
			return;
		}

		new Thread() {
			@Override
			public void run() {
				if(rawVoiceQueue!=null){
				for (int i = 0; i != rawVoiceQueue.size(); i++) {
					int readSize = rawVoiceQueue.get(i).length;
					short[] pcmFrame = rawVoiceQueue.get(i).data;

					for (int j = 0; j < readSize; j++) {
						try {
							// rawVoiceQueue.add(pcmFrame
							output.writeShort(pcmFrame[j]);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				EventBus.getDefault().postSticky(
						new DisplayEvent("Init saving mp3 file!"));
				set_raw_file_not_writing();
				FLameUtils lameUtils = new FLameUtils(1, Audio.SAMPLE_RATE, 320);
				System.out.println(lameUtils.raw2mp3(tempFile, mp3File));
				EventBus.getDefault().postSticky(
						new DisplayEvent("Saving mp3 file success for CustomRecorderjava!"));
				VoiceObject voiceObject = new VoiceObject();
				voiceObject.saveVoiceObject(mp3File); //parse to cloud
			}
			}
		}.start();

	}

	public synchronized void resumeAudio() {
		recording = true;
		notify();
	}

	public synchronized void shutdown() {
		pauseAudio();
		running = false;
		notify();
	}

	private void startBufferedWrite(final File file) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				DataOutputStream output = null;
				try {
					output = new DataOutputStream(new BufferedOutputStream(
							new FileOutputStream(file)));
					while (if_raw_writing() && isRecording()) {

						for (int i = 0; i < pcmFrame_record.length; i++) {
							output.writeShort(pcmFrame_record[i]);
						}
					}
				} catch (IOException e) {

				} finally {
					if (output != null) {
						try {
							output.flush();
						} catch (IOException e) {

						} finally {
							try {
								output.close();
							} catch (IOException e) {

							}
						}
					}
				}
			}
		}).start();
	}

}