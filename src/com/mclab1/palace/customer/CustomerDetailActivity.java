package com.mclab1.palace.customer;
//
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ro.ui.pttdroid.Globalvariable;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mclab1.palaca.parsehelper.VoiceDataElement;
import com.mclab1.palaca.parsehelper.VoiceObject;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import edu.mclab1.nccu_story.R;
public class CustomerDetailActivity extends Activity {   //************offline *********** Story 
	                                                     //VoiceDataActivity VoiceDataElment
	public static final String TAG=" CustomerDetailActivity";
	String msg = "Android : ";
	String mTitle = "";
	String mContent = "";
	String mPhotoPath = "";
	final String tempFile = Environment.getExternalStorageDirectory() + "/";
	private ListView listView;
	private Button stopbutton;
	private CustomerVoiceListAdapter customerVoiceListAdapter;
	private ArrayList<VoiceDataElement> mp3unuiques = new ArrayList<VoiceDataElement>();
	private ArrayList<ArrayList> v=new ArrayList<ArrayList>();
	private ArrayList<String> uniq_ids = new ArrayList<String>();
	private ArrayList<String> Storefilepath =new ArrayList<String>();
	private MediaPlayer mpintro;
	String[][] test=new String[500][500];
	FileInputStream tempfis=null;
	SequenceInputStream sistream=null;
	Boolean if_run_one=true;
	Boolean if_numberTag=true;
	Boolean if_init=true;
	Boolean if_add_content=false;
	Boolean if_inmpintro=false;
	String  string_numberTAg=null;
	String  subnumberTag=null;
	String  Prestring_numberTAg=null;
	int L=0;
	int M=0;


	int i;
	int j;
	//FileOutputStream fos =new FileOutputStream(new File(path));
	private ArrayList<InputStream> inputStreams = new ArrayList<InputStream>();
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.customer_activity_detail);
		//final RelativeLayout background = (RelativeLayout)findViewById(R.id.back);
		
		Log.d(msg, "The onCreate() event");
		if(mTitle!=null && mContent!=null){
		mTitle=Globalvariable.titleString ;
		mContent=Globalvariable.contentString;
		initViews();
		}
		/*Bundle extras = getIntent().getExtras();
		if (extras != null) {               //之後柏會傳一個objectID給我
			mTitle=Globalvariable.titleString ;
			mContent=Globalvariable.contentString;
			//mTitle = extras.getString("title");
			//mContent = extras.getString("content");
			mPhotoPath = extras.getString("photo");
			initViews();
		}*/
	}

	private void initViews() {
		ImageView imageView = (ImageView) findViewById(R.id.customer_activity_image);
		// TextView title = (TextView)
		// findViewById(R.id.customer_activity_title);
		TextView content = (TextView) findViewById(R.id.customer_activity_content);
		getActionBar().setTitle(mTitle);
		//Picasso.with(this).load(mPhotoPath).into(imageView);
		// title.setText(mTitle);
		content.setText(mContent);
		
		listView = (ListView) findViewById(R.id.customer_mp3_listview);
		/*stopbutton = (Button)findViewById(R.id.Stopbutton);
		stopbutton.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mpintro!=null){
					mpintro.stop();
				}
			}
		});*/
		
		customerVoiceListAdapter = new CustomerVoiceListAdapter(this, mp3unuiques);
		listView.setAdapter(customerVoiceListAdapter);
		

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {	
				
				try {
					
					String mp3Unique=mp3unuiques.get(pos).mp3Unique;
					System.out.println("mp3Unique  "+mp3Unique+" "+test[1][0]);
					  L=0;
					  M=1;
					/*while(test[L][0]!=null){
						System.out.println("TestOutput"+test[L][0]);
						while(test[L][M]!=null){
							
							M=M+1;
						}
						L=L+1;
					}*/
					while(!mp3Unique.equals(test[L][0])&&test[L][0]!=null){
						System.out.println("testLLLLMM"+test[L][0]);
						L=L+1;
					}
						
				/*	new Thread(){
						@Override
						public void run(){
							while(true){
								System.out.println("mpintronotplaying1");
								if(if_inmpintro){
								if(!mpintro.isPlaying()){
									System.out.println("mpintronotplaying2");
									notify();
								}
								}
								
							}
							
						}
						
					 }.start();*/
					
						
					
					System.out.println("testLLLLMM"+test[L][0]+" "+test[L][1]+" "+test[L][2]);
					new Thread(){
					@Override
					public void run(){
						while(test[L][M]!=null){

					
							System.out.println("mpintro1"+test[L][M]);
							mpintro = MediaPlayer.create(getApplicationContext(), Uri.parse(test[L][M]));
							mpintro.start();
							
							System.out.println("outout");
							while(true){
								//if(mpintro!=null){
								try{
									
									if(mpintro.isPlaying()){
										//System.out.println("mpintro_isPlaying");
										}else{
											System.out.println("mpintro_out"+" "+M+" "+test[L][M]);
											M=M+1;
											break;
											}
									
								}catch(IllegalStateException e){
									mpintro=null;
									mpintro =new MediaPlayer();
									
								}

								//}else{
									
								}
								//}
							/*try {
								TimeUnit.SECONDS.sleep(200);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}*/
							}
							
							//if_inmpintro=true;

							//TimeUnit.SECONDS.sleep(200);
							}
					}.start();

						System.out.println("testLLLLMM"+test[L][2]);


						


					
					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				}
				

			
		});
		loaddata();

	}

	/** Called when the activity is about to become visible. */
	@Override
	protected void onStart() {
		super.onStart();
		Log.d(msg, "The onStart() event");
	}

	/** Called when the activity has become visible. */
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(msg, "The onResume() event");
	}

	/** Called when another activity is taking focus. */
	@Override
	protected void onPause() {
		super.onPause();
        if (mpintro!= null) {
        	
    		for(int i=0;test[L][i]!=null;i++){
    			test[L][i]=null;
    			
    		}
    		System.out.println("mpintro"+mpintro);
        	mpintro.stop();
        	mpintro.release();
        	//mpintro = null;
       }
		System.out.println("onPause"+mpintro);
		Log.d(msg, "The onPause() event");
		

	}

	/** Called when the activity is no longer visible. */
	@Override
	protected void onStop() {
		super.onStop();
		Log.d(msg, "The onStop() event");
	}

	/** Called just before the activity is destroyed. */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(msg, "The onDestroy() event");
	
	}

	@SuppressWarnings("null")
	private void loaddata() { 
		mp3unuiques.clear();
		//final String [][]test = null;
		  ParseQuery<ParseObject> query = ParseQuery
				.getQuery(VoiceObject.table_name);
		//query.orderByAscending("numberTag");
		//query.orderByAscending("subnumberTag");
		 query.whereEqualTo("latitude", Globalvariable.latitude);    //柏傳給我經緯度，我做經緯度限制
		 query.whereEqualTo("longitude", Globalvariable.longitude);    //柏傳給我經緯度，我做經緯度限制
		query.setLimit(300);
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(final List<ParseObject> voiceParseObjectList,
					final ParseException e) {
				if (e == null) {
					j=0;
					if_add_content=true;
					for ( i=0; i != voiceParseObjectList.size(); i++) {
						System.out.print("voiceParseObjectList"+" "+voiceParseObjectList.size()+" "+i);
						//String detail = "";
						final ParseObject parseObject = voiceParseObjectList.get(i);
						final SimpleDateFormat sdFormat = new SimpleDateFormat(
								"yyyy/MM/dd HH:mm");
						final ParseFile parseFile = (ParseFile) parseObject
								.get(VoiceObject.column_audio_file);
						
						

						parseFile.getDataInBackground(new GetDataCallback() {

							@Override
							public void done(final byte[] fileBytes,
									ParseException arg1) {
								// TODO Auto-generated method
								// stub
								if (arg1 == null) {
									//new Thread() {
										//@Override
										//public void run() {
											try {
												Number intvalue =(Number)parseObject.get(VoiceObject.subnumberTag);
												System.out.println("intvalue"+intvalue);

												string_numberTAg=(String) parseObject.get(VoiceObject.numberTag);

												/*System.out.println("string_numberTAgString"+parseObject.get(VoiceObject.numberTag)+" "+string_numberTAgString);
												if(parseObject.get(VoiceObject.numberTag).equals(string_numberTAgString)){
													System.out.println("string_numberTAgString"+parseObject.get(VoiceObject.numberTag));
												}
												else{
													System.out.println("Nostring_numberTAgString"+" "+parseObject.get(VoiceObject.numberTag)+" "+string_numberTAgString);
												}*/
												//if(parseObject.get(VoiceObject.numberTag).equals(string_numberTAg)){
													System.out.println("string_numberTAg"+parseObject.get(VoiceObject.numberTag));

												final SimpleDateFormat sdFormat2 = new SimpleDateFormat(
														"yyyy_MM_dd_HH_mm_ss");
												final SimpleDateFormat sdFormat3 = new SimpleDateFormat(
														"yyyy/MM/dd HH:mm:ss");
												String nameString = parseFile.getName().substring(42);
												Date tempDate=parseObject.getCreatedAt();
												final String filePath = tempFile
														+ nameString+sdFormat2
																.format(parseObject.getCreatedAt());
												final String mergepathString="/storage/emulated/0/merge.mp3";
												BufferedOutputStream bos = new BufferedOutputStream(
														new FileOutputStream(
																new File(
																	filePath)));
												bos.write(fileBytes);
												bos.flush();
												bos.close();
												if(if_init){
												test[0][0]=string_numberTAg;
												//test[0][1]=filePath;
												System.out.println("if_init1test_string_numberTAg"+test[0][0]);
												//System.out.println("Letmetet"+test[1][0]+" ");
												if_init=false;
												}
												System.out.println("if_init1"+if_init+" "+test[0][0]);
												if_add_content=false;
												int z=0;
												int M=1;
												while(test[z][0]!=null){
													System.out.println("if_init1test[z][M]1");
													if(test[z][0].equals(string_numberTAg)){
														System.out.println("if_init1test[z][M]2");
														while(test[z][M]!=null){
															System.out.println("if_init1test[z][M]3");
															M=M+1;
														}
														test[z][intvalue.intValue()]=filePath;          //subnumberTag of index ，如果index不連續就有問題
														System.out.println("if_init1test_filepath"+test[z][M]+z);
														if_add_content=true;
														//break;
													}
													z=z+1;
												}
												if(!if_add_content){
													test[z][0]=string_numberTAg;
													test[z][intvalue.intValue()]=filePath;
													System.out.println("if_init1testz"+test[z][0]+" "+z+" "+(Integer) intvalue.intValue()+" "+
													filePath);
													
												}

												
												//Prestring_numberTAg=(String) parseObject.get(VoiceObject.numberTag);
												
												/*uniq_ids.add(filePath);
												Storefilepath.add(filePath);
												v.add(uniq_ids);
												v.add(Storefilepath);
												v.toArray();*/
												System.out.println("filePath"+filePath+parseObject.getCreatedAt());
												
												/*try{
													final String tempFile = Environment.getExternalStorageDirectory() + "/";
													System.out.println("voiceParseObjectListsize()"+voiceParseObjectList.size()+" "+Storefilepath.size()+" "+j);
													if(Storefilepath.size()>2&&Storefilepath.size()==4){   //從頭掃到尾一次
													if_run_one=false;
													System.out.println("Merge Start!");
													FileOutputStream fos = new FileOutputStream(new File(mergepathString));

													for(int i=0;i!=Storefilepath.size();i++){
														FileInputStream fis =new FileInputStream(Storefilepath.get(i));
														System.out.println("voiceParseObjectListinputstream"+" "+i+" "+Storefilepath.size());
													    inputStreams.add(fis);
													}
													System.out.println("Merge inter!");
													Enumeration en = Collections.enumeration(inputStreams);
													SequenceInputStream sis = new SequenceInputStream(en);
													System.out.println("Merge inter2!");
													
													int temp;
													int k=0;
													while((temp =sis.read())!=-1){
														fos.write(temp);
														System.out.println("Merge k"+k);
														k=k+1;
													}
													sis.close();
													fos.close();
													System.out.println("Merge end!");
													//if_numberTag=true; //之後會用到
													}
												} catch (IOException e1) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}*/
											


											   
												
													//}


												//}

												runOnUiThread(new Runnable() {

													@Override
													public void run() {
														// TODO
														// Auto-generated
														// method
														// stub
														Log.i(TAG, "completed download mp3"+filePath+parseObject.getCreatedAt());
														int M=1; 
														
														//if((j+1)==voiceParseObjectList.size()){
														System.out.println("testLo"+test[L][0]);
														    if(test[L][0]!=null){
																VoiceDataElement voiceDataElement=new VoiceDataElement();
																	System.out.println("voiceDataElement2"+" "+test[L][0]+" "+test[L][1]);
																	voiceDataElement.mp3Unique=test[L][0];//第一個片段當開頭
																	System.out.println("sdFormat3"+sdFormat3.format(parseObject.getCreatedAt()));
																	voiceDataElement.createdTime=sdFormat3.format(parseObject.getCreatedAt());
																	//Storefilepath.add(test[L][M]);
																	mp3unuiques.add(voiceDataElement);
																	System.out.println("voiceDataElement"+" "+L);
																L=L+1;
																//voiceDataElement=null;
															}

															//}														
														customerVoiceListAdapter.notifyDataSetChanged();
													}
												});//}
												j=j+1;

											} catch (Exception e2) {
												e2.printStackTrace();
											}
										//}
									//}.start();
								} else {
									// Log.i(TAG, "Get parse file error!");
								}

							}
						});
						
						
						//detail += String.valueOf(sdFormat.format(parseObject
						//		.getCreatedAt()));
					}


				//}
					System.out.println("Out");
					/*try{
					AudioInputStream clip1 = AudioSystem.getAudioInputStream(new File(tempFile+"Tim.mp3"));
				    AudioInputStream clip2 = AudioSystem.getAudioInputStream(new File(tempFile+"Tim2.mp3"));


				    AudioInputStream appendedFiles = 
		                            new AudioInputStream(
		                                new SequenceInputStream(clip1, clip2),     
		                                clip1.getFormat(), 
		                                clip1.getFrameLength() + clip2.getFrameLength());

				    AudioSystem.write(appendedFiles, 
		                            AudioFileFormat.Type.WAVE, 
		                            new File("/storage/emulated/0/merge.mp3"));
					}catch(Exception e1){
						e1.printStackTrace();
					}*/

					

					

				} else {
					Log.d("score", "Error: " + e.getMessage());
				}
			}
		});

	}
}
