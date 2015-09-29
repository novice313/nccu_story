/* Copyright 2011 Ionut Ursuleanu
 
This file is part of pttdroid.
 
pttdroid is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
 
pttdroid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
 
You should have received a copy of the GNU General Public License
along with pttdroid.  If not, see <http://www.gnu.org/licenses/>. */

package ro.ui.pttdroid;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import ro.ui.pttdroid.codecs.Speex;
import ro.ui.pttdroid.settings.AudioSettings;
import ro.ui.pttdroid.settings.CommSettings;
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
	public static 		InetAddress addr = null;
	public static Boolean if_recorder=false;
	int count=0;
	@Override
	public void run() 
	{
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		
		init();
		
		while(isRunning()) 
		{		
			recorder.startRecording();
			
			while(isRecording()&!Client_Player.if_guide_interrupt) 	
			{
				if_recorder=true;
				System.out.println("Recording now!!");
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
					System.out.println("Before to packet!"+addr);
					packet = new DatagramPacket(
							encodedFrame, 
							encodedFrame.length, 
							addr, 
							CommSettings.getPort());
					
					//android.util.Log.i("", encodedFrame.toString());
					System.out.println("recorder_socket.send(packet);");
					recorder_socket.send(packet);
					System.out.println("recorder_socket.send(packet)2;");
				}
				catch(IOException e) 
				{
					e.printStackTrace();
					Log.error(getClass(), e);
				}	
			}
			System.out.println("recorder_socket.send(packet);3");
			if_recorder=false;
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
	public static void channel50_51(InetAddress intaAddress)
	{
		//addr= intaAddress;
		//System.out.println("addrInRecorder.java:"+addr);
		

		
    }
	private void init() 
	{				
		try 
		{	    	
			IP.load();
			try {
				addr=InetAddress.getByName("239.255.255.250");
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			recorder_socket = new DatagramSocket();			
			
			switch(CommSettings.getCastType()) 
			{
				case CommSettings.BROADCAST:
					recorder_socket.setBroadcast(true);		
				try {
					addr = InetAddress.getByName("239.255.255.250");
				} catch (UnknownHostException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				break;
				case CommSettings.MULTICAST:				
				try {
					addr = InetAddress.getByName(Client_Player.getIP);
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
					//CommSettings.getMulticastAddr();					
				break;
				case CommSettings.UNICAST:
					addr = CommSettings.getUnicastAddr();					
				break;
			}		
			
			if(AudioSettings.useSpeex()==AudioSettings.USE_SPEEX)
				encodedFrame = new byte[Speex.getEncodedSize(AudioSettings.getSpeexQuality())];
			else 
				encodedFrame = new byte[Audio.FRAME_SIZE_IN_BYTES];

	    	recorder = new AudioRecord(
	    			AudioSource.MIC, 
	    			Audio.SAMPLE_RATE, 
	    			AudioFormat.CHANNEL_IN_MONO, 
	    			Audio.ENCODING_PCM_NUM_BITS, 
	    			Audio.RECORD_BUFFER_SIZE);	
	    	
	    	try{
	    	if(recorder.getState()==AudioRecord.STATE_INITIALIZED){
                System.out.println( "Audio recorder initialised at c" +recorder.getSampleRate());
      
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
