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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import ro.ui.pttdroid.codecs.Speex;
import ro.ui.pttdroid.settings.AudioSettings;
import ro.ui.pttdroid.settings.CommSettings;
import ro.ui.pttdroid.util.Audio;
import ro.ui.pttdroid.util.IP;
import ro.ui.pttdroid.util.Log;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.os.Environment;

import com.mclab1.palaca.parsehelper.RealtimeVoiceObject;
import com.mclab1.palaca.parsehelper.tempVoiceObject;
import com.mclab1.palace.connection.VoiceData;
import com.mclab1.palace.guider.DisplayEvent;
import com.pocketdigi.utils.FLameUtils;

import de.greenrobot.event.EventBus;

public class RealTimeRecorder extends Thread
{	
	private AudioRecord recorder;
	
	private volatile boolean recording = false;
	private volatile boolean running = true;
	
	private DatagramSocket recorder_socket;
	private DatagramPacket packet;

	

	private short[] pcmFrame = new short[Audio.FRAME_SIZE];
	private byte[] encodedFrame;
    byte[] buf = new byte[1024];  

	private List<VoiceData> temprawVoiceQueue;
	private List<VoiceData> rawVoiceQueue;
	private List<VoiceData> RealtimerawVoiceQueue;
	private List<VoiceData> RealtimerawFinalVoiceQueue;


	int tempValue=0;
	int RealValue=0;
	int Realtimesize=0;
	int RealtimeFinalsize=0;

	int tempsize=0;
	int size=0;
	int Tofinalzie=0;
	//static  int numberTag=0;
	UUID uuid  =  UUID.randomUUID(); 
	String numberTag = UUID.randomUUID().toString();
	int SubnumbeTag=0;
	Boolean if_300=true;
	Boolean if_60=true;
	Boolean if_cleanQueue=true;
	String location="NPM";

	String tempFile2 = Environment.getExternalStorageDirectory()
			+ "/NPM2.raw";


	/*final String mp3File = Environment.getExternalStorageDirectory()
			+"/NPM2.mp3";*/
	private Boolean if_writing_raw = false;
	List<byte[]> bytes = new ArrayList<byte[]>();
	DataOutputStream output = null;
	DataOutputStream output2 = null;
	int j=0;
	String msg="let start!";
	@Override
	public void run() 
	{
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		
		init();
		
		while(isRunning()) 
		{		
			recorder.startRecording();
			set_raw_file_writing(); 
			if_300=true;
			tempsize=0;

			while(isRecording()) 
			{
				msg="Tim"+j;
				j=j+1;
				if(AudioSettings.useSpeex()==AudioSettings.USE_SPEEX) 
				{
					int readSize = recorder.read(pcmFrame, 0, Audio.FRAME_SIZE);
					//EventBus.getDefault().postSticky(
					//		new DisplayEvent("readSize:"+readSize));
					VoiceData voicedata = new VoiceData();
					voicedata.data = new short[readSize];
					for (int i = 0; i != readSize; i++) {
						voicedata.data[i] = pcmFrame[i];
					}

					voicedata.length = readSize;
					temprawVoiceQueue.add(voicedata);
					rawVoiceQueue.add(voicedata);
					RealtimerawVoiceQueue.add(voicedata);
					RealtimerawFinalVoiceQueue.add(voicedata);
					Speex.encode(pcmFrame, encodedFrame);		//壓縮encodedFrame 再送出去，目的packet比較不易LOST
					
					
				}
				else
				{
					recorder.read(encodedFrame, 0, Audio.FRAME_SIZE_IN_BYTES);
				}

				try 
				{				
					//android.util.Log.i("", encodedFrame.toString());
					
					
					InetAddress addr = null;
					
					switch(CommSettings.getCastType()) 
					{
						case CommSettings.BROADCAST:
							recorder_socket.setBroadcast(true);	
							addr = CommSettings.getBroadcastAddr();
						break;
						case CommSettings.MULTICAST:				
							addr = CommSettings.getMulticastAddr();					
						break;
						case CommSettings.UNICAST:
							addr = CommSettings.getUnicastAddr();					
						break;
					}
					byte[] buffer = msg.getBytes();
					bytes.add(buffer);
					byte[] newbyte=streamCopy(bytes);
					
					String str = new String(newbyte, "UTF-8");
					System.out.println("newbyte:"+str);
					
					packet = new DatagramPacket(
							newbyte, 
							newbyte.length, 
							addr, 
							CommSettings.getPort());
					Date tempdDate =new Date();
					int mintemp  = tempdDate.getMinutes();
					int sectemp  = tempdDate.getSeconds();
					int intValue=mintemp*60+sectemp;
					System.out.println("int intValue"+mintemp+" "+sectemp+" "+intValue);
					/*if(AudioSettings.useSpeex()==AudioSettings.USE_SPEEX) 
						encodedFrame = new byte[Speex.getEncodedSize(AudioSettings.getSpeexQuality())];
					else 
						encodedFrame = new byte[Audio.FRAME_SIZE_IN_BYTES];
					*/
					
					//packet_number= new DatagramPacket(buffer,buffer.length,addr,CommSettings.getPort());
					recorder_socket.send(packet);  //to local player
					if(if_300){
					tempValue=intValue;
					if_300=false;
					}
					if(tempValue+300==intValue){
					System.out.println("temp_stop_recording");
					temp_stop_recording();
					if_300=true;
					}
					
					if(if_60){
						RealValue=intValue;
						if_60=false;
					}
					if(RealValue+60==intValue){
						System.out.println("Real_time_stop_recording");
						Realtime_stop_recording();
						if_60=true;
						
					}
					/*try{
						recorder_socket_to_server.receive(dp_receive);
					System.out.println("dp_receive"+dp_receive);
					}catch(IOException e){
						e.printStackTrace();
					}*/
					
					
					
					bytes.remove(buffer);

				}
				catch(IOException e) 
				{
					e.printStackTrace();
					Log.error(getClass(), e);
				}	
			}		
			
			recorder.stop();
				
			/*synchronized(this)
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
			}*/
		}		
		recorder_socket.close();
		recorder.release();
	}
	
	
	public static byte[] streamCopy(List<byte[]> srcArrays) {
		byte[] destAray = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
		for (byte[] srcArray:srcArrays) {
		bos.write(srcArray);
		}
		bos.flush();
		destAray = bos.toByteArray();
		} catch (IOException e) {
		e.printStackTrace();
		} finally {
		try {
		bos.close();
		} catch (IOException e) {
		}
		}
		return destAray;
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
			
			
			bytes.add(encodedFrame);//增加encodedFrame 




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
	
	public synchronized void start_recording() {
		//output2 = new DataOutputStream(new BufferedOutputStream(
		//		new FileOutputStream(new File(tempFile2))));
		rawVoiceQueue = new ArrayList<VoiceData>();
		temprawVoiceQueue =new ArrayList<VoiceData>();
		RealtimerawVoiceQueue =new ArrayList<VoiceData>();
		RealtimerawFinalVoiceQueue =new ArrayList<VoiceData>();
		EventBus.getDefault().postSticky(
				new DisplayEvent("Init recording file!"));
	}
	
	
	public synchronized void RealtimeFinal_stop_recording() {
		// testing
		SimpleDateFormat sdf = new SimpleDateFormat("HH_mm_ss");
		Date dte = new Date();
		String dts = sdf.format(dte);
		final String tempFile = Environment.getExternalStorageDirectory()
				+ "/A"+dts+".raw";
		try {
			output = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(new File(tempFile))));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    final File file = new File(tempFile);
		if (!file.exists()) {
			EventBus.getDefault().postSticky(
					new DisplayEvent("Failed Init saving mp3 Realtime_stop_recording!"));
			return;
		}

		new Thread() {
			@Override
			public void run() {
				if(RealtimerawFinalVoiceQueue!=null){
					//SubnumbeTag=SubnumbeTag+1;
				for (int i = Realtimesize; i != RealtimerawFinalVoiceQueue.size(); i++) {
					System.out.println("Realtimesize"+Realtimesize+i);
					int readSize = RealtimerawFinalVoiceQueue.get(i).length;
					short[] pcmFrame = RealtimerawFinalVoiceQueue.get(i).data;

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
				//Realtimesize=RealtimerawFinalVoiceQueue.size()+1;
				SimpleDateFormat sdf = new SimpleDateFormat("HH_mm_ss");
				Date dte = new Date();
				String dts = sdf.format(dte);
				String mp3File = Environment.getExternalStorageDirectory()
						+"/A"+dts+"temp"+".mp3";
				EventBus.getDefault().postSticky(
						new DisplayEvent("Init saving mp3 file!"));
				//set_raw_file_not_writing();
				FLameUtils lameUtils = new FLameUtils(1, Audio.SAMPLE_RATE, 320);
				System.out.println(lameUtils.raw2mp3(tempFile, mp3File));
				EventBus.getDefault().postSticky(
						new DisplayEvent("Saving RealtimeFinal mp3!"));
				RealtimeVoiceObject realtimeVoiceObject = new RealtimeVoiceObject();
				//realtimeVoiceObject.saveVoiceObject(mp3File,location); //parse to cloud
				//SubnumbeTag=0;
				//numberTag = UUID.randomUUID().toString(); 
				/*if(file.delete()){
					EventBus.getDefault().postSticky(new DisplayEvent("file.delete()"));
				}*/
				
			}
			}
		}.start();

	}

	
	
	public synchronized void Realtime_stop_recording() {
		// testing
		SimpleDateFormat sdf = new SimpleDateFormat("HH_mm_ss");
		Date dte = new Date();
		String dts = sdf.format(dte);
		final String tempFile = Environment.getExternalStorageDirectory()
				+ "/A"+dts+".raw";
		try {
			output = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(new File(tempFile))));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    final File file = new File(tempFile);
		if (!file.exists()) {
			EventBus.getDefault().postSticky(
					new DisplayEvent("Failed Init saving mp3 Realtime_stop_recording!"));
			return;
		}

		new Thread() {
			@Override
			public void run() {
				if(RealtimerawVoiceQueue!=null){
					//SubnumbeTag=SubnumbeTag+1;
				for (int i = Realtimesize; i != RealtimerawVoiceQueue.size(); i++) {
					System.out.println("Realtimesize"+Realtimesize+i);
					int readSize = RealtimerawVoiceQueue.get(i).length;
					short[] pcmFrame = RealtimerawVoiceQueue.get(i).data;

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
				Realtimesize=RealtimerawVoiceQueue.size()+1;
				SimpleDateFormat sdf = new SimpleDateFormat("HH_mm_ss");
				Date dte = new Date();
				String dts = sdf.format(dte);
				String mp3File = Environment.getExternalStorageDirectory()
						+"/A"+dts+"temp"+".mp3";
				EventBus.getDefault().postSticky(
						new DisplayEvent("Init saving mp3 file!"));
				//set_raw_file_not_writing();
				FLameUtils lameUtils = new FLameUtils(1, Audio.SAMPLE_RATE, 320);
				System.out.println(lameUtils.raw2mp3(tempFile, mp3File));
				EventBus.getDefault().postSticky(
						new DisplayEvent("Saving Realtime mp3!"));
				RealtimeVoiceObject realtimeVoiceObject = new RealtimeVoiceObject();
				//realtimeVoiceObject.saveVoiceObject(mp3File,location); //parse to cloud
				//SubnumbeTag=0;
				//numberTag = UUID.randomUUID().toString(); 
				/*if(file.delete()){
					EventBus.getDefault().postSticky(new DisplayEvent("file.delete()"));
				}*/
				
			}
			}
		}.start();

	}


	public synchronized void stop_recording() {
		// testing
		SimpleDateFormat sdf = new SimpleDateFormat("HH_mm_ss");
		Date dte = new Date();
		String dts = sdf.format(dte);
		final String tempFile = Environment.getExternalStorageDirectory()
				+ "/A"+dts+".raw";
		try {
			output = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(new File(tempFile))));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    final File file = new File(tempFile);
		if (!file.exists()) {
			EventBus.getDefault().postSticky(
					new DisplayEvent("Failed Init saving mp3 stop_recording!"));
			return;
		}

		new Thread() {
			@Override
			public void run() {
				if(rawVoiceQueue!=null){
					SubnumbeTag=SubnumbeTag+1;
				for (int i = Tofinalzie; i != rawVoiceQueue.size(); i++) {
					System.out.println("Tofinalzie"+Tofinalzie+i);
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
				SimpleDateFormat sdf = new SimpleDateFormat("HH_mm_ss");
				Date dte = new Date();
				String dts = sdf.format(dte);
				String mp3File = Environment.getExternalStorageDirectory()
						+"/A"+dts+"temp"+".mp3";
				EventBus.getDefault().postSticky(
						new DisplayEvent("Init saving mp3 file!"));
				//set_raw_file_not_writing();
				FLameUtils lameUtils = new FLameUtils(1, Audio.SAMPLE_RATE, 320);
				System.out.println(lameUtils.raw2mp3(tempFile, mp3File));
				EventBus.getDefault().postSticky(
						new DisplayEvent("Saving mp3!"));
				tempVoiceObject tempVoiceObject = new tempVoiceObject();
				tempVoiceObject.saveVoiceObject(mp3File,numberTag,SubnumbeTag); //parameter:numberTag,SubnumbeTag
				
				
				SubnumbeTag=0;
				numberTag = UUID.randomUUID().toString(); 
				/*if(file.delete()){
					EventBus.getDefault().postSticky(new DisplayEvent("file.delete()"));
				}*/
				
			}
			}
		}.start();

	}
	
	public synchronized void temp_stop_recording() {
		// testing
		Tofinalzie=temprawVoiceQueue.size()+1;
		SimpleDateFormat sdf = new SimpleDateFormat("HH_mm_ss");
		Date dte = new Date();
		String dts = sdf.format(dte);
		final String tempFile = Environment.getExternalStorageDirectory()
				+ "/A"+dts+".raw";
		try {
			output = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(new File(tempFile))));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    final File file = new File(tempFile);
		if (!file.exists()) {
			EventBus.getDefault().postSticky(
					new DisplayEvent("Failed Init saving mp3 temp_stop_recording!"));
			return;
		}

		new Thread() {
			@Override
			public void run() {
				if(temprawVoiceQueue!=null){
					SubnumbeTag=SubnumbeTag+1;
				for (int i = tempsize; i != temprawVoiceQueue.size(); i++) {
					System.out.println("tempsize"+tempsize+i);
					int readSize = temprawVoiceQueue.get(i).length;
					short[] pcmFrame = temprawVoiceQueue.get(i).data;

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
				tempsize=temprawVoiceQueue.size()+1;
				SimpleDateFormat sdf = new SimpleDateFormat("HH_mm_ss");
				Date dte = new Date();
				String dts = sdf.format(dte);
				String mp3File = Environment.getExternalStorageDirectory()
						+"/A"+dts+"temp"+".mp3";
				EventBus.getDefault().postSticky(
						new DisplayEvent("Init saving mp3 file!"));
				//set_raw_file_not_writing();
				FLameUtils lameUtils = new FLameUtils(1, Audio.SAMPLE_RATE, 320);
				System.out.println(lameUtils.raw2mp3(tempFile, mp3File));
				EventBus.getDefault().postSticky(
						new DisplayEvent("Saving Temp3!"));
				tempVoiceObject tempVoiceObject = new tempVoiceObject();
				tempVoiceObject.saveVoiceObject(mp3File,numberTag,SubnumbeTag); //parse to cloud
				//SubnumbeTag=0;
				//numberTag = UUID.randomUUID().toString(); 
				/*if(file.delete()){
					EventBus.getDefault().postSticky(new DisplayEvent("file.delete()"));
				}*/
				
			}
			}
		}.start();

	}

	
	private synchronized void set_raw_file_writing() {
		if_writing_raw = true;
	}
	private synchronized void set_raw_file_not_writing() {
		if_writing_raw = false;
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

