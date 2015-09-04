package com.mclab1.palaca.parsehelper;

import java.io.File;
import java.io.FileInputStream;

import com.mclab1.palace.guider.DisplayEvent;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import de.greenrobot.event.EventBus;

public class VoiceObject {
	public static final String table_name = "Broadcast";
	public static final String column_audio_file = "mp3file";
	public static final String numberTag = "numberTag";
	public static final String subnumberTag = "subnumberTag";

	ParseFile file;

	public VoiceObject() {

	}

	public void saveVoiceObject(final String mp3_file_path) {
		EventBus.getDefault().postSticky(
				new DisplayEvent("Saving voice file to parse..."));
		new Thread() {
			@Override
			public void run() {
				try {

					File mp3File = new File(mp3_file_path);
					byte[] data = new byte[(int) mp3File.length()];
					FileInputStream fileInputStream = null;
					fileInputStream = new FileInputStream(mp3File);
					fileInputStream.read(data);
					fileInputStream.close();
					file = new ParseFile("A_man.mp3", data);
					file.saveInBackground(new SaveCallback() {
						@Override
						public void done(ParseException e) {
							if (e == null) {
								EventBus.getDefault()
										.postSticky(
												new DisplayEvent(
														"Saving voice file to parse done!!"));
								ParseObject parseObject = new ParseObject(
										table_name);
								parseObject.put(column_audio_file, file);
								parseObject
										.saveInBackground(new SaveCallback() {

											@Override
											public void done(ParseException arg0) {
												// TODO Auto-generated method
												// stub
												if(arg0==null){
													EventBus.getDefault().postSticky(
															new DisplayEvent(
																	"Save voice object success"));
												}else{
													EventBus.getDefault().postSticky(
															new DisplayEvent(
																	"Failed to Saving voice file to parse1..."));
												}

											}
										});
							}else{
								EventBus.getDefault().postSticky(
										new DisplayEvent(
												"Failed to Saving voice file to parse2..."+e));
							}
						}
					}, new ProgressCallback() {
						@Override
						public void done(Integer percentDone) {
							// Update your progress spinner here. percentDone
							// will be
							// between 0 and 100.
							if (percentDone % 10 == 0) {
								EventBus.getDefault().postSticky(
										new DisplayEvent("Saving voice file :")
												+ String.valueOf(percentDone)
												+ "% done");
							}
						}
					});

				} catch (Exception e) {
					EventBus.getDefault().postSticky(
							new DisplayEvent(
									"Failed to Saving voice file to parse3..."));
				}
			}
		}.start();

	}
}
