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

package com.mclab1.palace.recorder;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ro.ui.pttdroid.codecs.Speex;
import ro.ui.pttdroid.settings.AudioSettings;
import ro.ui.pttdroid.util.Audio;
import ro.ui.pttdroid.util.IP;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.os.Environment;

import com.mclab1.palaca.parsehelper.VoiceObject;
import com.mclab1.palace.connection.VoiceData;
import com.mclab1.palace.guider.DisplayEvent;
import com.pocketdigi.utils.FLameUtils;

import de.greenrobot.event.EventBus;

public class Mp3Recorder extends Thread
{	
	private AudioRecord recorder;
	
	private volatile boolean recording = false;
	private volatile boolean running = true;
	

	

	private short[] pcmFrame = new short[Audio.FRAME_SIZE];
	private byte[] encodedFrame;
    byte[] buf = new byte[1024];  

	private List<VoiceData> rawVoiceQueue;
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	Date dt = new Date();
	String dts = sdf.format(dt);
	String aString="dfsdfs";
	final String tempFile = Environment.getExternalStorageDirectory()
			+ "/NPM2.raw";
	final String mp3File = Environment.getExternalStorageDirectory()
			+"/A"+".mp3";

	/*final String mp3File = Environment.getExternalStorageDirectory()
			+"/QQQQQ.mp3";*/
	private Boolean if_writing_raw = false;
	List<byte[]> bytes = new ArrayList<byte[]>();
	DataOutputStream output = null;
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
			EventBus.getDefault().postSticky(
			new DisplayEvent("In Running"));			
			while(isRecording()) 
			{
				EventBus.getDefault().postSticky(
						new DisplayEvent("In Recoding"));
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
					rawVoiceQueue.add(voicedata);

					
				}
				else
				{
					recorder.read(encodedFrame, 0, Audio.FRAME_SIZE_IN_BYTES);
				}
	
			}		
			
			recorder.stop();

		}		
		recorder.release();
	} 
	
	

	
	private void init()
	{				
		IP.load();
		
						
		
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
						new DisplayEvent("Saving mp3 file success for Mp3Recorderjava!"));
				VoiceObject voiceObject = new VoiceObject();
				voiceObject.saveVoiceObject(mp3File); //parse to cloud
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

