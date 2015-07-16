package com.mclab1.palace.recorder;

import android.os.Environment;

import com.pocketdigi.utils.FLameUtils;

public class Mp3Helper {
	String raw;
	String mp3;
	
	final String tempFile = Environment.getExternalStorageDirectory()
			+ "/aa.raw";
	final String mp3File = Environment.getExternalStorageDirectory()
			+ "/bb.mp3";

	public Mp3Helper(String raw_file, String mp3_file) {
		raw = raw_file;
		mp3 = mp3_file;
	}
	
	public void test_start_converting(){
		new Thread() {
			@Override
			public void run() {
				
				FLameUtils lameUtils = new FLameUtils(1, ro.ui.pttdroid.util.Audio.SAMPLE_RATE, 128);
				System.out.println(lameUtils.raw2mp3(tempFile,
						mp3File));
			}
		}.start();
	}
}
