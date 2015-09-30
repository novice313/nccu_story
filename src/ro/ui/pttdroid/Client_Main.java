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

import java.util.List;

import ro.ui.pttdroid.codecs.Speex;
import ro.ui.pttdroid.settings.AudioSettings;
import ro.ui.pttdroid.settings.CommSettings;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.wifidirect.DeviceDetailFragment;
import com.example.android.wifidirect.DeviceListFragment;
import com.example.android.wifidirect.DeviceListFragment.DeviceActionListener;
import com.example.android.wifidirect.WifiReceiver;
import com.mclab1.palaca.parsehelper.ParseHelper;
import com.mclab1.palace.customer.CustomerFragment;
import com.mclab1.palace.customer.CustomerFragmentGlobal;
import com.mclab1.palace.customer.CustomerFragmentOffline;
import com.mclab1.palace.guider.DisplayEvent;
import com.mclab1.place.events.NewClientConnectionEvent;
import com.mclab1.place.events.PauseAudioEvent;
import com.mclab1.place.events.ResumeAudioEvent;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import de.greenrobot.event.EventBus;
import edu.mclab1.nccu_story.R;
 
public class Client_Main extends FragmentActivity implements ChannelListener, DeviceActionListener {
public static final String SOCKET_TAG_STRING = "wifi-socket-test";
public static final String TAG = "pttdroid";
// / hghghghgh
/**
* Wifi direct
*/

private WifiP2pManager manager;
private boolean isWifiP2pEnabled = false;
private boolean retryChannel = false;

private final IntentFilter intentFilter = new IntentFilter();
private Channel channel;
private BroadcastReceiver receiver = null;

private static boolean firstLaunch = true;

private static Recorder recorder;

private static MicrophoneSwitcher microphoneSwitcher;

private static Intent playerIntent;

private static Intent clientConnectionIntent;

private static Intent customPlayerIntent;

private WifiReceiver wifiReceiver = null;
IntentFilter filter = new IntentFilter();

private static final Boolean if_guider = false;

private Boolean if_clientL_offline_mode = true;
private Boolean if_to_list=true;

private Button btn_test_mp3;

private Boolean if_talking = false;
private ImageView writing;
//private ImageView ready_to_start;
private NfcAdapter adapter;
private int if_Global_local;
int SPLASH_DISPLAY_LENGHT = 3000;

private TextView userName;
private TextView title;
private TextView content;
private ParseImageView imageView;
ProgressDialog dialog;
WifiManager wifi;
String mssid;
Boolean if_loading_final=false;




@Override
public void onDestroy() {
super.onDestroy();
try {
	EventBus.getDefault().unregister(this);
} catch (Exception e) {
	// TODO: handle exception
}
shutdown();
}

@Override
public void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.main);
userName=(TextView)findViewById(R.id.userName);
title=(TextView)findViewById(R.id.title);
content=(TextView)findViewById(R.id.content);

/*ImageButton ready_image_guide;
ready_image_guide=(ImageButton)findViewById(R.id.ready_image_guide); 

ready_image_guide.setOnClickListener(new Button.OnClickListener()  
{  
    @Override  
    public void onClick(View v) {  
        // TODO Auto-generated method stub  
    	
		change_to_client_online_fragment();
    	
    }  
      
}); */
//getActionBar().setBackgroundDrawable(null);

getActionBar().setBackgroundDrawable(getResources().getDrawable(android.R.color.darker_gray));  //標題配色


 System.out.println("start main!!!!!!!!!!"); 
 Intent intent = this.getIntent();
//取得傳遞過來的資料   
 if_Global_local = intent.getIntExtra("if_Global_local", -2);
 System.out.println("if_Global_local"+if_Global_local);
 
try {
	ParseHelper.initParse(this);
	EventBus.getDefault().postSticky("Parse init success!");
} catch (Exception e) {
	EventBus.getDefault().postSticky("Failed to int parse!");
}


/*ImageButton listen_guide = (ImageButton) findViewById(R.id.listen_guide);

listen_guide .setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	
	        }});*/



init();

/*change_to_client_online_fragment();
playerIntent = new Intent(this, Client_Player.class);
startService(playerIntent);
*/


if(if_Global_local==1){
	System.out.println("local");
	if (if_clientL_offline_mode) {
		
		if_clientL_offline_mode = false;
		change_to_client_online_fragment();
		playerIntent = new Intent(this, Client_Player.class);
		startService(playerIntent);
		
		} 
	}


if(if_Global_local==0){
	System.out.println("global");
		if(if_clientL_offline_mode){
			if_loading_final=true;
			if_clientL_offline_mode=false;
			microphoneSwitcher.hide();
			change_to_client_Global_online_fragment();
		} 
		invalidateOptionsMenu();
		
	}

//microphoneSwitcher.hide();

intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
intentFilter
		.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
intentFilter
		.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
channel = manager.initialize(this, getMainLooper(), null);

filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

EventBus.getDefault().register(this);
// test_connect();

}

private void change_to_client_online_fragment() {

microphoneSwitcher.show();
android.support.v4.app.FragmentManager  fragmentManager = getSupportFragmentManager();

CustomerFragment customerFragment = new CustomerFragment();
fragmentManager.beginTransaction()
		.replace(R.id.content_holder, customerFragment)
		.commitAllowingStateLoss();
}

private void change_to_client_offline_fragment() { 
System.out.println("INchange_to_client_offline_fragment");

microphoneSwitcher.hide();


android.support.v4.app.FragmentManager  fragmentManager = getSupportFragmentManager();

android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();

CustomerFragmentOffline customerFragment = new CustomerFragmentOffline();
fragmentManager.beginTransaction()
		.replace(R.id.content_holder, customerFragment)
		.commitAllowingStateLoss();

if(playerIntent!=null){
stopService(playerIntent);
}


}

private void  change_to_client_Global_online_fragment(){

	FragmentManager fragmentManager = getSupportFragmentManager();
	CustomerFragmentGlobal customerFragmentGlobal = new CustomerFragmentGlobal();
	fragmentManager.beginTransaction()
			.replace(R.id.content_holder, customerFragmentGlobal)
			.commitAllowingStateLoss(); 
	
}

public void onEvent(NewClientConnectionEvent event) {

}


/*public void test_connect() {
String networkSSID = "DIRECT-Q6-Android_4ff3";
String networkPass = "CegDR821";

WifiConfiguration conf = new WifiConfiguration();
conf.SSID = "\"" + networkSSID + "\""; // Please note the quotes. String
										// should contain ssid in quotes
conf.preSharedKey = "\"" + networkPass + "\"";

WifiManager wifiManager = (WifiManager) this
		.getSystemService(Context.WIFI_SERVICE);

wifiManager.addNetwork(conf);

// 09-21 16:44:43.138: I/wifidirectdemo(7899):
// WifiDIRECT-Q6-Android_4ff3,gNMjrRNZ

List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
for (WifiConfiguration i : list) {
	Log.i(TAG, i.SSID);
	if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
		Log.i(TAG, "Found connection!");
		wifiManager.disconnect();
		wifiManager.enableNetwork(i.networkId, true);
		wifiManager.reconnect();

		break;
	}
}

}

public void test_create_group() {

manager.createGroup(channel, new ActionListener() {

	@Override
	public void onSuccess() {
		// TODO Auto-generated method stub
		Log.i(TAG, "Create group success!!");

	}

	@Override
	public void onFailure(int arg0) {
		// TODO Auto-generated method stub

	}
});
}*/

@Override
public void onResume() {
super.onResume();


/*wifiReceiver = new WifiReceiver();         // *******
registerReceiver(wifiReceiver, filter);
receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
registerReceiver(receiver, intentFilter);*/

//change_to_client_online_fragment();
//change_to_client_offline_fragment();


/****************** 之後要改變頁面的地方
/*
if (if_guider) {
	//test_create_group();
	FragmentManager fragmentManager = getSupportFragmentManager();
	GuiderFragment guiderFragment = new GuiderFragment();
	fragmentManager.beginTransaction()
			.replace(R.id.content_holder, guiderFragment)
			.commitAllowingStateLoss();
} else {
	
	if(if_Global_local==1){
		//System.out.println("local");
		//if (if_clientL_offline_mode) {
		//	if_clientL_offline_mode = false;
	
		
		//change_to_client_offline_fragment();		

		  change_to_client_offline_fragment();	
		  if_clientL_offline_mode = false;
			change_to_client_online_fragment();
			playerIntent = new Intent(this, Client_Player.class);
			startService(playerIntent);
		//	} else {
		//		if_clientL_offline_mode = true;
		//		change_to_client_offline_fragment();
		//		}
		}
	
	if(if_Global_local==0){
		System.out.println("global");
		//	if(if_clientL_offline_mode){
		//		if_clientL_offline_mode=false;

				change_to_client_Global_online_fragment();
		//	} else{
		//		if_clientL_offline_mode=true;
		//		change_to_client_offline_fragment();		
		//	}
     //invalidateOptionsMenu();
			//return true;
			
		}
	//change_to_client_offline_fragment();
}*/


    



}

@Override
public void onPause() {
super.onPause();
if (!if_guider) {
	recorder.pauseAudio();
	System.out.println("Stop talking4!");

}
microphoneSwitcher.shutdown();
//unregisterReceiver(receiver);
//unregisterReceiver(wifiReceiver);
}

@Override
public boolean onCreateOptionsMenu(Menu menu) {
if (if_guider) {
	//getMenuInflater().inflate(R.menu.action_items, menu);
} else {
	if (if_clientL_offline_mode&&if_clientL_offline_mode) {
		//getMenuInflater().inflate(R.menu.menu_client_offline, menu);
	} 
	/*else if(!if_clientL_offline_mode) 
	{
		System.out.println("menu_client_local");                 //會選擇是不是Local

		getMenuInflater().inflate(R.menu.menu_client_local, menu);
	}*/
}
return true;
}

/*
* (non-Javadoc)
* 
* @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
*/

@Override
public boolean onOptionsItemSelected(MenuItem item) {
switch (item.getItemId()) {
	
case R.id.btn_change_mode:
	//Random random = new Random();
	
	if(if_Global_local==1){
		System.out.println("local");
		if (if_clientL_offline_mode) {
			if(userName!=null && title!=null && content!=null){
			userName.setVisibility(View.GONE);
			title.setVisibility(View.GONE);
			content.setVisibility(View.GONE);
			}
		    if(imageView!=null){
		        imageView.setVisibility(View.GONE);
			} 
		   
			if_clientL_offline_mode = false;
			change_to_client_online_fragment();
			playerIntent = new Intent(this, Client_Player.class);
			startService(playerIntent);
			
			} 
		}
	
	if(if_Global_local==0){
		System.out.println("global");
			if(if_clientL_offline_mode){
				
				if(userName!=null && title!=null && content!=null){
					userName.setVisibility(View.GONE);
					title.setVisibility(View.GONE);
					content.setVisibility(View.GONE);
					}
				    if(imageView!=null){
				        imageView.setVisibility(View.GONE);
					}
				
				if_clientL_offline_mode=false;
				change_to_client_Global_online_fragment();
			} 
			invalidateOptionsMenu();
			
		}
		
	invalidateOptionsMenu(); 
	return true;

/*case R.id.btn_quit_icon:     //一個箭頭的符號
	
	if (if_clientL_offline_mode) {
	} else {
		if_clientL_offline_mode = true;
		change_to_client_offline_fragment();
	}
	invalidateOptionsMenu();
	return true;*/
	
	
	



default:
	return super.onOptionsItemSelected(item);
}
}





private void init() {  //init=> OnResume

	final ParseQuery<ParseObject> query = ParseQuery.getQuery("offline");
	System.out.println("latitiude"+Globalvariable.latitude+" "+Globalvariable.longitude);
	
    dialog = ProgressDialog.show(Client_Main.this,
            "讀取資料中", "請 稍 等 . . . . ",true);

    
	new Thread() {
		@Override
		public void run() {
			
			for(int i=10;i>0;i--){     //for 網路沒連好的人
			try {
				    System.out.println("i"+i);
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		    if(if_loading_final==false){       //圖load進去才算final
		    	dialog.dismiss();
		    	if(if_Global_local==0){
                    Looper.prepare();
	                Toast.makeText(
	                        Client_Main.this,
	                        "您必須要跳到設定 =>WIFI =>選可用的wifi",
	                        Toast.LENGTH_LONG).show();
	                Intent settintIntent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);   
	    		    startActivity(settintIntent); 
		    		
		    	}else{
                    Looper.prepare();
	                Toast.makeText(
	                        Client_Main.this,
	                        "您必須要跳到設定的WIFI，選擇WIFI名稱"+Globalvariable.client_Main_SSID,
	                        Toast.LENGTH_LONG).show();
    		    Intent settintIntent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);   
    		    startActivity(settintIntent); 

		    	}
                
             }
			
		}
	}.start();

	new Thread() {
		@Override
		public void run() {
			try {
				Thread.sleep(3000);             ///因為網路不一定那麼快連好
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	// Retrieve the object by id	
	query.whereEqualTo("latitude", Globalvariable.latitude);    //柏傳給我經緯度，我做經緯度限制
	query.whereEqualTo("longitude", Globalvariable.longitude);  	
	query.findInBackground(new FindCallback<ParseObject>() {	
		@Override
		public void done(List<ParseObject> objects, ParseException e) {
			// TODO Auto-generated method stub
	        if (e == null) {	        	
				String userNameString  =(String) objects.get(0).get("userName");
				String titleString  =(String) objects.get(0).get("title");
				String contentString  =(String) objects.get(0).get("content");
        		System.out.println("SHow"+userNameString+" "+titleString+" "+contentString);

				userName.setText(userNameString);
				title.setText(titleString);
				title.append("\t"+contentString);
		        if_loading_final=true;
                dialog.dismiss();      //有字就停止 dialog

				
			final ParseFile image =(ParseFile)objects.get(0).get("image");
				// ((ParseObject) me).getParseFile("data");
			// final ParseImageView imageView = (ParseImageView) findViewById(R.id.personalprfile);
			// imageView.setParseFile(image);
			// System.out.println("image"+image);
		  if(image!=null){
			image.getDataInBackground(new GetDataCallback() {

			@Override
			public void done(byte[] data, ParseException e) {
				// TODO Auto-generated method stub
				if(e==null){
					System.out.println("personalprofile"+" "+data.length);
			        final Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,data.length);
			        // Get the ImageView from main.xml
			        //ImageView image = (ImageView) findViewById(R.id.ad1);
			        imageView = (ParseImageView) findViewById(R.id.showimage);

			       // ImageView imageView=(ImageView) findViewById(R.id.personalprfile);
			        // Set the Bitmap into the
			        // ImageView
			        imageView.setParseFile(image);
			        imageView.setImageBitmap(bmp);
			        if_loading_final=true;
	                dialog.dismiss();      

			       /* imageView.loadInBackground(new GetDataCallback() {
			            public void done(byte[] data, ParseException e) {
			            // The image is loaded and displayed!                    
			            int oldHeight = imageView.getHeight();
			            int oldWidth = imageView.getWidth();     
			            System.out.println("imageView height = " + oldHeight);
			            System.out.println("imageView width = " + oldWidth);
			            imageView.setImageBitmap(bmp);


			           // Log.v("LOG!!!!!!", "imageView height = " + oldHeight);      // DISPLAYS 90 px
			           // Log.v("LOG!!!!!!", "imageView width = " + oldWidth);        // DISPLAYS 90 px      
			            }
			        });*/
					
				}
				else{
					System.out.println("personalprofilerror");

				}
				
			}
			});
		  }
				

				
				
	        	
	        }
	        else {
	        	System.out.println("offlineerror");

	        	
	        }
			}
			
			
		
	});
		}}.start();
	

	
	
	microphoneSwitcher = new MicrophoneSwitcher();
	microphoneSwitcher.init();
	System.out.println("Client_Main");
	
if (firstLaunch) {
	CommSettings.getSettings(this);
	AudioSettings.getSettings(this);

	setVolumeControlStream(AudioManager.STREAM_MUSIC);

	Speex.open(AudioSettings.getSpeexQuality());
	
	//playerIntent = new Intent(this, Player.class);  
	//startService(playerIntent);
	System.out.println("clientConnectionIntent");
	/*clientConnectionIntent = new Intent(this,
			ClientConnectionService.class);
	startService(clientConnectionIntent);*/
	

	

	//customPlayerIntent = new Intent(this, CustomPlayer.class); // 沒用到
	//startService(customPlayerIntent);
	if (!if_guider) {
		
		recorder = new Recorder();
		recorder.start();

	} else {

	}
	firstLaunch = false;
}
}

private void shutdown() {
try {
	firstLaunch = true;
	stopService(playerIntent);
	stopService(clientConnectionIntent);
	//stopService(customPlayerIntent);
	if (!if_guider) {
		recorder.shutdown();
		System.out.println("Stop talking5!");

	} 
	Speex.close();

} catch (Exception e) {
	e.printStackTrace();
} finally {
	finish();
}

}

public class MicrophoneSwitcher implements Runnable, OnTouchListener {
private Client_Player player;

private Handler handler = new Handler();

private ImageView microphoneImage;

public static final int MIC_STATE_NORMAL = 0;
public static final int MIC_STATE_PRESSED = 1;
public static final int MIC_STATE_DISABLED = 2;

private int microphoneState = MIC_STATE_NORMAL;

private int previousProgress = 0;

private static final int PROGRESS_CHECK_PERIOD = 100;

private Boolean running = true;

private ServiceConnection playerServiceConnection;

public void hide() {
	microphoneImage.setVisibility(View.GONE);

}

public void show() {
	microphoneImage.setVisibility(View.VISIBLE);
}

public void init() {
	System.out.println("if_init");
	microphoneImage = (ImageView) findViewById(R.id.microphone_image);
	
		microphoneImage.setEnabled(true);

	microphoneImage.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!if_talking&&!Client_Player.if_interrupt) {
				// start talking
				System.out.println("Client_Player.if_interrupt"+Client_Player.if_interrupt);
				microphoneImage.setEnabled(true);
								
					System.out.println("recorder.resumeAudio()2;");
					recorder.resumeAudio();
					EventBus.getDefault().post(new ready_to_start_event(false));

				if_talking = true;
				setMicrophoneState(MicrophoneSwitcher.MIC_STATE_PRESSED);
				EventBus.getDefault().post(new ResumeAudioEvent());
				EventBus.getDefault().postSticky(
						new DisplayEvent("Start talking!"));
				EventBus.getDefault().post(new ready_to_start_event(true));

		}else{
			
			// stop takling

			setMicrophoneState(MicrophoneSwitcher.MIC_STATE_NORMAL);

			recorder.pauseAudio();
			EventBus.getDefault().post(new ready_to_start_event(false));

					
			if_talking = false;
			EventBus.getDefault().post(new PauseAudioEvent());
			EventBus.getDefault().postSticky(
					new DisplayEvent("Stop talking!"));
		}
		}
			
			
		}
	);			
	
	handler.postDelayed(this, PROGRESS_CHECK_PERIOD);
}

@Override
public void run() {
	System.out.println("player2:"+player);
	synchronized (running) {
		
		if (running &&player != null) {                                     //player上面圖案的三種變化，根據有沒聲音傳過來
			System.out.println("setMicrophoneState(MIC_STATE_DISABLED)0");
			int currentProgress = player.getProgress();
			System.out.println("currentProgress"+currentProgress+previousProgress);
			if (currentProgress > previousProgress) {
				if (microphoneState != MIC_STATE_DISABLED) {
					System.out.println("setMicrophoneState(MIC_STATE_DISABLED);1");
					/*if (!if_guider)
						recorder.pauseAudio();*/

					setMicrophoneState(MIC_STATE_DISABLED);
				}
			} else {
				if (microphoneState == MIC_STATE_DISABLED){
					setMicrophoneState(MIC_STATE_NORMAL);
					System.out.println("setMicrophoneState(MIC_STATE_DISABLED);2");
					
				}
			}
			System.out.println("!Client_Player.if_guide_interrupt&&!Recorder.if_recorder"+Client_Player.if_guide_interrupt+Recorder.if_recorder);
			if(!Client_Player.if_guide_interrupt&&!Recorder.if_recorder){
				setMicrophoneState(MIC_STATE_NORMAL);
			}

			previousProgress = currentProgress;

			handler.postDelayed(this, PROGRESS_CHECK_PERIOD);
		}
	}
}

@Override
public boolean onTouch(View v, MotionEvent e) {
	if (microphoneState != MicrophoneSwitcher.MIC_STATE_DISABLED) {
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (!if_guider) {
				System.out.println("recorder.resumeAudio();");
				recorder.resumeAudio();
			}
			setMicrophoneState(MicrophoneSwitcher.MIC_STATE_PRESSED);
			EventBus.getDefault().post(new ResumeAudioEvent());
			break;
		case MotionEvent.ACTION_UP:
			setMicrophoneState(MicrophoneSwitcher.MIC_STATE_NORMAL);
			if (!if_guider) {
				recorder.pauseAudio();
				System.out.println("Stop talking3!");

			}
			EventBus.getDefault().post(new PauseAudioEvent());
			break;
		}
	}
	return true;
}

public void setMicrophoneState(int state) {
	switch (state) {
	case MIC_STATE_NORMAL:
		microphoneState = MIC_STATE_NORMAL;
		microphoneImage
				.setImageResource(R.drawable.microphone_normal_image);
		break;
	case MIC_STATE_PRESSED:
		microphoneState = MIC_STATE_PRESSED;
		microphoneImage
				.setImageResource(R.drawable.microphone_pressed_image);
		break;
	case MIC_STATE_DISABLED:
		microphoneState = MIC_STATE_DISABLED;
		microphoneImage
				.setImageResource(R.drawable.microphone_disabled_image);
		break;
	}
}

public void shutdown() {
}
};

/**
* @param isWifiP2pEnabled
*            the isWifiP2pEnabled to set
*/
public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
this.isWifiP2pEnabled = isWifiP2pEnabled;
}

/**
* Remove all peers and clear all fields. This is called on
* BroadcastReceiver receiving a state change event.
*/
public void resetData() {
DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager()
		.findFragmentById(R.id.frag_list);
DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager()
		.findFragmentById(R.id.frag_detail);
if (fragmentList != null) {
	fragmentList.clearPeers();
}
if (fragmentDetails != null) {
	fragmentDetails.resetViews();
}
}

@Override
public void showDetails(WifiP2pDevice device) {
DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
		.findFragmentById(R.id.frag_detail);
fragment.showDetails(device);

}
@Override
public void onChannelDisconnected() {
}

@Override
public void disconnect() {
	
}

@Override
public void cancelDisconnect() {
	
}
@Override
public void connect(WifiP2pConfig config) {
	// TODO Auto-generated method stub
	
}

}