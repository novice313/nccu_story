package com.example.android.wifidirect;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import ro.ui.pttdroid.codecs.Speex;
import ro.ui.pttdroid.settings.AudioSettings;
import ro.ui.pttdroid.util.Audio;
import ro.ui.pttdroid.util.IP;
import ro.ui.pttdroid.util.Log;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;

public class Recorder extends Thread
{	
	private AudioRecord recorder;
	
	private volatile boolean recording = false;
	private volatile boolean running = true;
	
	private DatagramSocket recorder_socket;
	private DatagramPacket packet;
	
	private short[] pcmFrame = new short[Audio.FRAME_SIZE];
	private byte[] encodedFrame;

	private InetAddress addr;

	private String multicast_adr="255.255.255.255";
			
	@Override
	public void run() 
	{
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		
		init();
		
		while(isRunning()) 
		{		
			//android.util.Log.i("", "recorder running");
			recorder.startRecording();
			
			while(isRecording()) 
			{
			//	android.util.Log.i("", "is recording");
				if(AudioSettings.useSpeex()==AudioSettings.USE_SPEEX) 
				{
					recorder.read(pcmFrame, 0, Audio.FRAME_SIZE);
					Speex.encode(pcmFrame, encodedFrame);						
				}
				else
				{
					recorder.read(encodedFrame, 0, Audio.FRAME_SIZE_IN_BYTES);
				}

				try 
				{				
					//android.util.Log.i("", "recording"+packet.toString());
					//android.util.Log.i("", "sending to"+packet.getAddress());
					//android.util.Log.i("", encodedFrame.toString());
					recorder_socket.send(packet);
				}
				catch(IOException e) 
				{
					e.printStackTrace();
				}	
			}		
			
			recorder.stop();
				
			synchronized(this)
			{
				try 
				{	
					if(isRunning())
						wait();
				}
				catch(InterruptedException e) 
				{
					Log.error(getClass(), e);
				}
			}
		}		
		
		recorder_socket.close();				
		recorder.release();
	}
	public void setAddress(final InetAddress addr_){
		new Thread(){
			@Override
			public void run(){
				addr =addr_;
				packet = new DatagramPacket(
						encodedFrame, 
						encodedFrame.length, 
						addr, 
						8988);
			}
		}.start();
		
	}
	private void init() 
	{				
		try 
		{	    	
			IP.load();
			
			recorder_socket = new DatagramSocket();			
						
			
			if(AudioSettings.useSpeex()==AudioSettings.USE_SPEEX)
				encodedFrame = new byte[Speex.getEncodedSize(AudioSettings.getSpeexQuality())];
			else 
				encodedFrame = new byte[Audio.FRAME_SIZE_IN_BYTES];
			
			try {
				packet = new DatagramPacket(
						encodedFrame, 
						encodedFrame.length, 
						InetAddress.getByName(multicast_adr) , 
						8988);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	    	recorder = new AudioRecord(
	    			AudioSource.MIC, 
	    			Audio.SAMPLE_RATE, 
	    			AudioFormat.CHANNEL_IN_MONO, 
	    			Audio.ENCODING_PCM_NUM_BITS, 
	    			Audio.RECORD_BUFFER_SIZE);	
	    	try{
	    	if(recorder.getState()==AudioRecord.STATE_INITIALIZED){
                System.out.println( "Audio recorder initialised at sss" +recorder.getSampleRate());
      
	    	}
	    	else {
                recorder.release();
                recorder = null;
				
			}
	    	}
	    	catch(IllegalArgumentException e){
	    		System.out.println( "recorder error");
	    		
	    	}
	    	
		}
		catch(SocketException e) 
		{
			Log.error(getClass(), e);
		}	
	}
	
	private synchronized boolean isRunning()
	{
		return running;
	}
	
	private synchronized boolean isRecording()
	{
		return recording;
	}
	
	public synchronized void pauseAudio() 
	{				
		recording = false;					
	}	 

	public synchronized void resumeAudio() 
	{				
		recording = true;			
		notify();
	}
				
	public synchronized void shutdown() 
	{
		pauseAudio();
		running = false;				
		notify();
	}
		
}

