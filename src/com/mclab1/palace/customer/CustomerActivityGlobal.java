package com.mclab1.palace.customer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mclab1.palaca.parsehelper.VoiceObject;
import com.mclab1.palace.guider.DisplayEvent;
import com.mclab1.place.events.EraseServerConnectionEvent;
import com.mclab1.place.events.NewServerConnectionEvent;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import de.greenrobot.event.EventBus;
import edu.mclab1.nccu_story.R;

public class CustomerActivityGlobal extends Activity {
	public static final String SOCKET_TAG_STRING = "wifi-socket-test";
	public static final String TAG = "CustomerFragment";
	private ListView log_list_view;
	private ArrayList<String> voiceList = new ArrayList<String>();
	private ArrayAdapter<String> listAdapter;
	private ArrayList<String> log = new ArrayList<String>();
	private ArrayList<String> realtimedatas = new ArrayList<String>();
	private final SimpleDateFormat sdFormat = new SimpleDateFormat("HH:mm:ss");
	private Handler handler_1 = null;//老闆
	private HandlerThread handlerThread_1 = null;//員工
	private String handlerThread_1_name = "我是1號員工";
	 
	
	String detail = "";
	final String tempFile = Environment.getExternalStorageDirectory() + "/";
	private MediaPlayer mpintro;
	private final String Locationed="NPM";
	String PreobjectId="nothing";
	Boolean if_notplay=true;
	Boolean if_for=true;
	Boolean if_wait=true;
	int arr_size=500;
	String[] realtimearray=new String[arr_size];
	Number indexvalue;
	int intvalue2=0;
	int intvalue3=0;
	int i=0;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.customer_fragment_g);
		init_view();


	}
	/*public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.customer_fragment_g, container,
				false);

		init_view(view);

		return view;
	}*/

	private void init_view() {
		log_list_view = (ListView) findViewById(R.id.customerG_log);
		listAdapter = new ArrayAdapter<String>(this,
				R.layout.event_row_list_item, log);
	      //listAdapter.add("aaa");
		log_list_view.setAdapter(listAdapter);
		/*log_list_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				String mp3Path=voiceList.get(pos);
				mpintro = MediaPlayer.create(getActivity(), Uri.parse(mp3Path));
				mpintro.start();

			}
			
		});*/
		CustomerFragmentOffline.flag=true;
		//新增一個員工 給他一個名字
		handlerThread_1 = new HandlerThread(handlerThread_1_name);
		 
		//讓他開始上班
		handlerThread_1.start();
		 
		//新增一個老闆 他是員工1號的老闆
		handler_1 = new Handler(handlerThread_1.getLooper());

		Runnable runnable_1 = new Runnable() {
			 
		@Override
		public void run() {
		      //要做的事情寫在這
			if(CustomerFragmentOffline.flag){

			System.out.println("Looping");
			loaddata();
		      //老闆指定每隔幾秒要做一次工作1 (單位毫秒:1000等於1秒)
		      handler_1.postDelayed(this, 20000);
			}
		 
		}
		};
		
		handler_1.post(runnable_1);
		
	
		
		
		 

		

	}
	
	@SuppressWarnings("null")
	private void loaddata() {
		System.out.println("loaddataInput");
		//final String [][]test = null;
		  ParseQuery<ParseObject> query = ParseQuery
				.getQuery(VoiceObject.table_name);
		  query.whereEqualTo("Location", Locationed);
		  query.addDescendingOrder("createdAt");
			System.out.println("loaddataInput2");
		query.getFirstInBackground(new GetCallback<ParseObject>() {
			@Override
			public void done(final ParseObject object, ParseException e) {
				//System.out.println("loaddataInput3"+PreobjectId+" "+object.getObjectId());
				if (object != null &&!PreobjectId.equals(object.getObjectId())) {
					System.out.println("objectgetInt"+object.getInt(Locationed));
					
					ParseFile parseFile = (ParseFile) object
								.get(VoiceObject.column_audio_file);
					System.out.println("parseFile"+parseFile);
					
					
					parseFile.getDataInBackground(new GetDataCallback() {
						
						@Override
						public void done(byte[] fileBytes, ParseException arg1) {
							// TODO Auto-generated method stub
							if(arg1==null){
								 indexvalue =(Number)object.get(VoiceObject.subnumberTag);
								 
								Date tempdDate =new Date();
								int hurtemp =tempdDate.getHours();
								int mintemp  = tempdDate.getMinutes();
								int sectemp  = tempdDate.getSeconds();
								final int intValue=hurtemp*60*60+mintemp*60+sectemp;
								System.out.println("int intValue"+mintemp+" "+sectemp+" "+intValue);
								
								
								final SimpleDateFormat sdFormat2 = new SimpleDateFormat(
										"HH_mm_ss");
								final String filePath = tempFile
										+ object.getInt(Locationed)+sdFormat2
												.format(object.getCreatedAt());
								String creatStri=sdFormat2
								.format(object.getCreatedAt());
								int hurcre=Integer.valueOf(creatStri.substring(0,2));
								int mincre=Integer.valueOf(creatStri.substring(3,5));
								int seccre=Integer.valueOf(creatStri.substring(6,8));
								final int crevalue=hurcre*60*60+mincre*60+seccre;
								EventBus.getDefault().post(new DisplayEvent(hurcre+" "+seccre+" "+crevalue+" "+intValue)) ;
								if((crevalue+30)>intValue){    // 最後存的後時間和10s相比 聽眾會有10s的落差，聲音會接起來10s中的空白會用技術接起來
								BufferedOutputStream bos;
								try {
									bos = new BufferedOutputStream(
											new FileOutputStream(
													new File(
														filePath)));
									try {
									bos.write(fileBytes);
									bos.flush();
									bos.close();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								EventBus.getDefault().post(new DisplayEvent("indexvalue.intValue()"+indexvalue.intValue()));
								realtimearray[(indexvalue.intValue()-1)]=filePath;
								EventBus.getDefault().post(new DisplayEvent("Download mp3 completed"+filePath));
								//System.out.println("Download mp3 completed"+filePath+" "+realtimedatas.isEmpty());
								if(if_notplay&&realtimearray[(indexvalue.intValue()-1)]!=null){
									if_notplay=false;
									new Thread(){	
										@Override
										public void run(){
											EventBus.getDefault().post(new DisplayEvent("i am comming"));
											while(true){
											if(if_for){
												i=(indexvalue.intValue()-1);
											for(;realtimearray[i]!=null;i++){
												if(if_wait){
													 try {
														 EventBus.getDefault().post(new DisplayEvent("Wait.30...."));
														Thread.sleep(30000);
													} catch (InterruptedException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
													 if_wait=false;
												}
												//EventBus.getDefault().post(new DisplayEvent("realtimearray[i]"+realtimearray[i]));
												mpintro = MediaPlayer.create(CustomerActivityGlobal.this, Uri.parse(realtimearray[i]));
												mpintro.start();
												/*EventBus.getDefault().post(new DisplayEvent("IsPlaying"
														+" "+i+" "+indexvalue.intValue()));*/
												
												while(true){
													
													if(mpintro.isPlaying()){
														
														}else{
															/*EventBus.getDefault().post(new DisplayEvent("Notplaying"+realtimearray[i+1]
																	));*/
															break;
														}
													}
												

													
												//if_notplay=true;
												}

											
											Date tempdDate2 =new Date();
											int hurtemp2 =tempdDate2.getHours();
											int mintemp2  = tempdDate2.getMinutes();
											int sectemp2  = tempdDate2.getSeconds();
											intvalue2=hurtemp2*60*60+mintemp2*60+sectemp2;
											//System.out.println("intValue2"+mintemp2+" "+sectemp2+" "+intvalue2);
											EventBus.getDefault().post(new DisplayEvent("wait and find realtimearray_i"
													+" "+i));
											if_for=false;
											}   //if if_for
											
											if(realtimearray[i]!=null){
												EventBus.getDefault().post(new DisplayEvent("find realtimearray!!!"
														+realtimearray[i]+" "+i));
												if_for=true;		
											}
											
											Date tempdDate3 =new Date();
											int hurtemp3 =tempdDate3.getHours();
											int mintemp3  = tempdDate3.getMinutes();
											int sectemp3  = tempdDate3.getSeconds();
											intvalue3=hurtemp3*60*60+mintemp3*60+sectemp3;
											System.out.println("intValue3"+mintemp3+" "+sectemp3+" "+realtimearray[i]);
											if((intvalue2+180)<intvalue3){
												EventBus.getDefault().post(new DisplayEvent("maximum to wait 180s!!!"
														+realtimearray[i]+" "+i));
												break;
											}
											
											}
											
											
											for(int j=0;j<arr_size;j++){
												realtimearray[j]=null;     //做完清空
											}
											EventBus.getDefault().post(new DisplayEvent("mpintro_out"));
											//realtimedatas.clear();
											System.out.println("completedtoplaymp3"+filePath);
											if_notplay=true;
											if_wait=true;
											}
										}.start();
									}
							 

								PreobjectId=object.getObjectId();
								
								}
							}
							else{
								
							}
							
						}
					});
					
					System.out.println("Out");

					

				} else {
					//Log.d("score", "Error: " + e.getMessage());
				}
			}

		});
	}
					//j=0;
					//for ( i=0; i != voiceParseObjectList.size(); i++) {
						//System.out.print("voiceParseObjectList"+" "+i);
						//String detail = "";
						//final ParseObject parseObject = voiceParseObjectList.get(i);
					/*	final SimpleDateFormat sdFormat = new SimpleDateFormat(
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
												//if(parseObject.get(VoiceObject.numberTag).equals(string_numberTAg)){

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

												System.out.println("filePath"+filePath+parseObject.getCreatedAt());

												runOnUiThread(new Runnable() {

													@Override
													public void run() {
														// TODO
														// Auto-generated
														// method
														// stub
														Log.i(TAG, "completed download mp3"+filePath+parseObject.getCreatedAt());
														int M=1;
														int L=0;
														if((j+1)==voiceParseObjectList.size()){

															}														
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
						});*/
						
						
						//detail += String.valueOf(sdFormat.format(parseObject
						//		.getCreatedAt()));
					//}


				//}


	
	// listen to events
	private void add_log(final String todisplay) {        ////抓出時間
		
		CustomerActivityGlobal.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {     ///
				// TODO Auto-generated method stub
				log.add(0, sdFormat.format(new Date())+" : "+todisplay);
				listAdapter.notifyDataSetChanged();
			}
		});
	}
	


	public void onStickyEvent(DisplayEvent event) {
		final String todisplay = event.content;
		add_log(todisplay);
		EventBus.getDefault().removeStickyEvent(event);

	}

	public void onEvent(DisplayEvent event) {
		Log.i(TAG, "Got log!");
		final String todisplay = event.content;
		add_log(todisplay);

	}
	
	/*private void init_view(View v) {
		log_list_view = (ListView) v.findViewById(R.id.guider_log_listview);
		listAdapter = new ArrayAdapter<String>(getActivity(),
				R.layout.event_row_list_item, log);
		// listAdapter.add("aaa");
		log_list_view.setAdapter(listAdapter);

	}*/

	@Override
	public void onResume() {
		super.onResume();
		try {
			EventBus.getDefault().register(this);
			EventBus.getDefault().post(new DisplayEvent("Customer init!"));
			EventBus.getDefault().postSticky(
					new DisplayEvent("Customer test sticky!"));
			//EventBus.getDefault().postSticky(
			//		new DisplayEvent("CommSettings.getMulticastAddr!"+CommSettings.getMulticastAddr()));
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			EventBus.getDefault().unregister(this);
		} catch (Exception e) {
			// TODO: handle exception
		}
		CustomerActivityGlobal.this.finish();

	}
	@Override
	public void onDestroy(){  //add sth end VAR
		super.onDestroy();
		try {
	
			
			System.out.println("out_to_Main");
			EventBus.getDefault().unregister(this);
		} catch (Exception e) {
			// TODO: handle exception
		}
		CustomerActivityGlobal.this.finish();
	}

	private synchronized void test_socket_server() {
		Log.i(SOCKET_TAG_STRING, "Testing server connection");
		new Thread() {
			@Override
			public void run() {
				Socket client = new Socket();
				InetSocketAddress isa = new InetSocketAddress("192.168.49.1",
						8888);
				try {
					client.connect(isa, 10000);
					Log.i(SOCKET_TAG_STRING, "Connected!!");
					BufferedOutputStream out = new BufferedOutputStream(
							client.getOutputStream());
					// �摮葡
					out.write("Send From Client ".getBytes());
					out.flush();
					out.close();
					out = null;
					client.close();
					client = null;
					EventBus.getDefault().post(
							new NewServerConnectionEvent("192.168.49.1"));

				} catch (Exception e) {
					Log.i(SOCKET_TAG_STRING, "error!!!");

					Log.i(SOCKET_TAG_STRING, e.toString());
				}
			}
		}.start();

	}
	
	private synchronized void erase_socket_server() {
		Log.i(SOCKET_TAG_STRING, "Testing server connection");
		new Thread() {
			@Override
			public void run() {
				Socket client = new Socket();
				InetSocketAddress isa = new InetSocketAddress("192.168.49.1",
						8889);
				try {
					client.connect(isa, 10000);
					Log.i(SOCKET_TAG_STRING, "Connected!!");
					BufferedOutputStream out = new BufferedOutputStream(
							client.getOutputStream());
					// �摮葡
					out.write("Erase From Client ".getBytes());
					out.flush();
					out.close();
					out = null;
					client.close();
					client = null;
					EventBus.getDefault().post(
							new EraseServerConnectionEvent("192.168.49.1"));

				} catch (Exception e) {
					Log.i(SOCKET_TAG_STRING, "error!!!");

					Log.i(SOCKET_TAG_STRING, e.toString());
				}
			}
		}.start();

	}
}
