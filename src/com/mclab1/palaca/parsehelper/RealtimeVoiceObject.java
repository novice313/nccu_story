package com.mclab1.palaca.parsehelper;

import java.io.File;
import java.io.FileInputStream;
import android.net.wifi.WifiInfo;
import com.mclab1.palace.guider.DisplayEvent;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import de.greenrobot.event.EventBus;

public class RealtimeVoiceObject {
	public static final String table_name = "Broadcast";
	public static final String MAC_table_name = "test";
	public static final String column_audio_file = "mp3file";
	public static final String SubNumberTag="subnumberTag";
	public static final String NumberTag="numberTag";
	public static final String latitude="latitude";
	public static final String longitude="longitude";
	public static final String State="State";  //做online  offline state
	public static final String Guiderid="Guiderid";
	
	
	
	

	public ParseException stherrorString=null;
	ParseFile file;
	ParseFile file2;
	Boolean if_retransmit=true;
	WifiInfo wifiinfo ;


	public RealtimeVoiceObject() {

	}

	
	
	public void saveVoiceObject(final String mp3_file_path,final String tempFile, final String Tagi,final int SubTagi
			,final double latitudestring,final double longitudestring,final String guiderid,final int if_Final_normal) {
			 

		//new Thread() {
		//	@Override
		//	public void run() {
				try {

					/*EventBus.getDefault()
					.postSticky(new DisplayEvent("kill before"));*/
					
				/*	  ParseQuery<ParseObject> queryLocationall = ParseQuery 
								.getQuery(VoiceObject.table_name);
					  queryLocationall.whereEqualTo("Location", Locationed);
					  queryLocationall.findInBackground(new FindCallback<ParseObject>() {
						
						@Override
						public void done(List<ParseObject> voiceParseObjectList, ParseException e) {
							// TODO Auto-generated method stub
							if(e==null){
								int i=0;
								for ( ; i != voiceParseObjectList.size(); i++) {
									final ParseObject preparseObject = voiceParseObjectList.get(i);
									preparseObject.deleteInBackground();
									EventBus.getDefault()
									.postSticky(new DisplayEvent("kill success"+i));

								}
								

								
							}
							
						}
					});*/
					
					
					File mp3File = new File(mp3_file_path);
					byte[] data = new byte[(int) mp3File.length()];
					FileInputStream fileInputStream = null;
					fileInputStream = new FileInputStream(mp3File);
					fileInputStream.read(data);
					fileInputStream.close();
					file = new ParseFile("Realtime.mp3", data);//change

						EventBus.getDefault().postSticky(
							new DisplayEvent("Saving voice file to parse..."));
					file.saveInBackground(new SaveCallback() {

						@Override
						public void done(final ParseException e) {
							//while(if_retransmit){
								if_retransmit=false;
							if(e == null) {
								EventBus.getDefault().postSticky(
										new DisplayEvent("Saving1"));
								final ParseObject parseObject = new ParseObject(
										table_name);
								
								parseObject.put(latitude, latitudestring);
								parseObject.put(longitude, longitudestring);
								parseObject.put(NumberTag, Tagi);
								parseObject.put(SubNumberTag, SubTagi);
								parseObject.put(Guiderid, guiderid);     //上傳Guiderid
								parseObject.put(column_audio_file, file);
								if(if_Final_normal==0){
								parseObject.put(State,"Offline");
								}
								else if(if_Final_normal==1){
									parseObject.put(State,"Online");	
								}


								parseObject.saveEventually();
								                                     //update offline table
							
								
								EventBus.getDefault()
								.postSticky(
										new DisplayEvent(
												"Saving Realtimevoice file to parse done!!"));
								//String path = Environment.getExternalStorageDirectory().getPath();
								//檔案路徑，記得要加斜線(這樣/sdcard/filename)
			//String mp3File = Environment.getExternalStorageDirectory()+"/A"+dts+"Realtime"+".mp3";
								File file = new File(mp3_file_path);
								File file2 = new File(tempFile);
								System.out.println("filefile"+" "+" "+file.exists());
								if(file.exists()){
									file.delete();
									System.out.println("filefile2"+" "+" "+file.exists());			
								}
								if(file2.exists()){
									file2.delete();
									System.out.println("filefile3"+" "+" "+file.exists());
								}
								

							//if_retransmit=false;
							/*	parseObject
										.saveInBackground(new SaveCallback() {

											@Override
											public void done(ParseException arg0) {
												// TODO Auto-generated method
												// stub
												if(arg0==null){
													EventBus.getDefault().postSticky(
															new DisplayEvent(
																"Save Realtimevoice object success"));

													

													
												}else{
													EventBus.getDefault().postSticky(
															new DisplayEvent(
																	"Failed to Saving voice file to parse1..."));
												}
		
											}
										});*/
							}
						 
							else{
								EventBus.getDefault().postSticky(
										new DisplayEvent(
												"Failed to Saving voice file to parse1..."+Tagi+" "+SubTagi+" "+e));
								if_retransmit=true;
								EventBus.getDefault().postSticky(
										new DisplayEvent("Saving2"+if_retransmit));
							/*	while(e!=null){
									try {
									Thread.sleep(2000);
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								
								File mp3File = new File(mp3_file_path);
								byte[] data = new byte[(int) mp3File.length()];
								FileInputStream fileInputStream = null;
									try {
										fileInputStream = new FileInputStream(mp3File);
										fileInputStream.read(data);
										fileInputStream.close();
									} catch (FileNotFoundException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}	catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									EventBus.getDefault().postSticky(
											new DisplayEvent("Saving voice file to parse Realtime2..."));
									file2 = new ParseFile("Realtime2.mp3", data);//change
									file2.saveInBackground(new SaveCallback() {
										@Override
										public void done(ParseException e) {
											if (e == null) {
												final ParseObject parseObject = new ParseObject(
														table_name);
												
												parseObject.put(column_audio_file, file);
												parseObject.put(NumberTag, Tagi);
												parseObject.put(SubNumberTag, SubTagi);
												parseObject.put(Location, Locationed);
												try {
													parseObject.save();
												} catch (ParseException e1) {
													
												}
												
												EventBus.getDefault()
												.postSticky(
														new DisplayEvent(
																"Saving Realtimevoice file to parse done2!!"));
											}
												//else{
											
												//			EventBus.getDefault().postSticky(
												//		new DisplayEvent(
												//			"Failed to Saving voice file to parse2..."+Tagi+" "+SubTagi+" "+e));
												
												//	}
										}
									});
								}*/

									


							/*	new Thread(){
								public void run(){
								SimpleDateFormat sdf = new SimpleDateFormat("HH_mm_ss");
								Date dte = new Date();
								String dts = sdf.format(dte);
								EventBus.getDefault().postSticky(
										new DisplayEvent("Init saving mp3(192) file!"+SubTagi));
								
								final ParseObject parseObject = new ParseObject(table_name);
								parseObject.put(column_audio_file, file);
								parseObject.put(NumberTag, Tagi);
								parseObject.put(SubNumberTag, SubTagi);
								parseObject.put(Location, Locationed);
								try {
									parseObject.save();
									} catch (ParseException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
										}
								
								EventBus.getDefault().postSticky(
										new DisplayEvent("Saving Realtimevoice file to parse done(192)!!"));
								}
								}.start();*/
								
							}

							
							//}

					}}/*, new ProgressCallback() {
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
						
					}*/);
					
				} catch (Exception e) {
					EventBus.getDefault().postSticky(
							new DisplayEvent(
									"Failed to Saving voice file to parse3..."));
				}
				EventBus.getDefault().postSticky(
						new DisplayEvent("Saving3"));

			//}
			

		//}.start();
		

		

	}

	
	

}

