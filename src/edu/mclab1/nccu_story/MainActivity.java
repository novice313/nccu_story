package edu.mclab1.nccu_story;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;

import org.json.JSONObject;

import ro.ui.pttdroid.Client_Main;
import ro.ui.pttdroid.Client_Player;
import ro.ui.pttdroid.Main;

import com.example.fileexplorer.FileexplorerActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseObject;

import mclab1.pages.MediaPlayerFragment;
import mclab1.pages.UploadPage;
import mclab1.service.music.MusicController;
import mclab1.service.music.MusicService;
import mclab1.service.music.Song;
import mclab1.service.music.MusicService.MusicBinder;
import mclab1.sugar.Owner;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.bluetooth.le.ScanResult;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.MediaStore.Files;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.MediaController.MediaPlayerControl;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	private final static String tag = "MainActivityTAG";

	// tab
	private ViewPager viewPager;
	private ActionBar actionBar;
	private TabsPagerAdapter mAdapter;
	private String[] tabs = { "News", "Googlemap", "Mediaplayer", "Owner" };
	public static int tabsize = 0;
    private WifiManager wiFiManager;
    int if_Global_local=-1;
    List<android.net.wifi.ScanResult> mWifiScanResultList;
    String SSID;
    Boolean if_find_wificonnect=false;
    ArrayList<String> SSIDList = new ArrayList<String>(); 


	// Result codes
	private static final int REQUEST_CODE_RECORD = 1539;

	// facebook
	CallbackManager callbackManager;
	public static AccessToken accessToken;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// test
		// Parse.enableLocalDatastore(this);
		Parse.initialize(this, "wtSFcggR896xMJQUGblYuphkF6EVw4ChcLcpSowP",
				"IwJ3gTRBe8cARlxMf3xh97eai2a7MNLP68vdL3IY");
		// ParseObject testObject = new ParseObject("TestObject");
		// testObject.put("foo", "bar");
		// testObject.saveInBackground();

		// Initilization
		// MainActivity.context = getApplicationContext();
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
			tabsize++;
		}

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		// setup MusicSrv controller
		// setController();

		// facebook
		FacebookSdk.sdkInitialize(getApplicationContext());
		callbackManager = CallbackManager.Factory.create();

		// set facebook
		Log.d(tag, "request_onCreate");
		// request_facebook();
		// set facebook
		LoginManager.getInstance().registerCallback(callbackManager,
				new FacebookCallback<LoginResult>() {

					// 登入成功
					@Override
					public void onSuccess(LoginResult loginResult) {

						// accessToken之後或許還會用到 先存起來
						accessToken = loginResult.getAccessToken();

						Log.d("FB", "access token got.");

						// send request and call graph api
						GraphRequest request = GraphRequest.newMeRequest(
								accessToken,
								new GraphRequest.GraphJSONObjectCallback() {

									// 當RESPONSE回來的時候

									@Override
									public void onCompleted(JSONObject object,
											GraphResponse response) {
										// TODO Auto-generated method stub
										// 讀出姓名 ID FB個人頁面連結
										Log.d("FB", "complete");
										Log.d("FB",
												"id= " + object.optString("id"));
										Log.d("FB",
												"name= "
														+ object.optString("name"));
										Log.d("FB",
												"gender= "
														+ object.optString("gender"));
										Log.d("FB",
												"locale= "
														+ object.optString("locale"));
										Log.d("FB",
												"link= "
														+ object.optString("link"));

										List<Owner> owner = Owner
												.listAll(Owner.class);
										if (!owner.isEmpty()) {
											Owner.deleteAll(Owner.class);
										}
										Log.d(tag, "Verify owner." + " owner= "
												+ owner.size());
										Owner newOwner = new Owner(object
												.optString("id"), object
												.optString("name"), object
												.optString("gender"), object
												.optString("locale"), object
												.optString("link"));
										// newOwner.setId((long) 1);
										newOwner.save();
										List<Owner> NewOwner = Owner
												.listAll(Owner.class);
										Log.d(tag, "owner= " + NewOwner.size());

									}
								});

						// 包入你想要得到的資料 送出request
						Bundle parameters = new Bundle();
						parameters.putString("fields",
								"id,name,gender,locale,link");
						request.setParameters(parameters);
						request.executeAsync();
					}

					// 登入取消
					@Override
					public void onCancel() {
						// App code
						Log.d("FB", "CANCEL");
					}

					// 登入失敗
					@Override
					public void onError(FacebookException exception) {
						// App code
						Log.d("FB", "Exception: " + exception.toString());
					}
				});
		// END set facebook
		
	    //若wifi狀態為關閉則將它開啟
    	wiFiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    	   if (!wiFiManager.isWifiEnabled()) {
    		     wiFiManager.setWifiEnabled(true);
    		  }


	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_RECORD) {
			File audioFolderFile = new File(
					Environment.getExternalStorageDirectory(), "Music");
			boolean success = true;
			if (!audioFolderFile.exists()) {
				success = audioFolderFile.mkdir();
			}
			if (success) {
				Log.d(tag, "MusicFolderFile created.");

				// save audio
				// auto save


			} else {
				Log.d(tag, "Failed to create musicFolderFile.");
			}
		} else {
			callbackManager.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// on tab selected
		// show respected fragment view
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(tag, "onStart");
		if (MediaPlayerFragment.playIntent == null) {
			MediaPlayerFragment.playIntent = new Intent(this,
					MusicService.class);
			bindService(MediaPlayerFragment.playIntent,
					MainActivity.musicConnection, Context.BIND_AUTO_CREATE);
			startService(MediaPlayerFragment.playIntent);
		}
	}

	// mediaplayer
	// connect to the service
	public static ServiceConnection musicConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MusicBinder binder = (MusicBinder) service;
			// get service
			MediaPlayerFragment.musicSrv = binder.getService();

			// if(musicSrv!=null){
			Log.d(tag, MediaPlayerFragment.musicSrv.toString());
			// }
			// pass list
			MediaPlayerFragment.musicSrv.setList(MediaPlayerFragment.songList);
			MediaPlayerFragment.musicBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			MediaPlayerFragment.musicBound = false;
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// menu item selected
		switch (item.getItemId()) {
		case R.id.action_loginFacebook:
			Log.d(tag, "Facebook icon onclick.");
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					LoginManager.getInstance().logInWithReadPermissions(
							MainActivity.this,
							Arrays.asList("public_profile", "user_friends"));
				}
			});

			break;
		case R.id.action_test:
			Log.d(tag, "Test onClick");
        	System.out.println("wiFiManagergetConnectionInfo"+wiFiManager.getConnectionInfo()+"$"+
        			wiFiManager.getWifiState()+" ");

        	if (!wiFiManager.isWifiEnabled()){ //判斷是否有網路
        	 Toast.makeText(this, "要開啟網路(Wifi/3G)!", Toast.LENGTH_SHORT).show();

        	}else{
        		
        	    //重新掃描Wi-Fi資訊
        		wiFiManager.startScan();
                //偵測周圍的Wi-Fi環境(因為會有很多組Wi-Fi，所以型態為List)
                mWifiScanResultList = wiFiManager.getScanResults();
                for(int i = 0 ; i < mWifiScanResultList.size() ; i++ )
                {
                     //手機目前周圍的Wi-Fi環境
                     SSID  = mWifiScanResultList.get(i).SSID;
                     SSIDList.add(SSID);
                     
                }
        	
			 System.out.println("GOGOGO");            //network module connect 
			 String networkSSID = "NCCU_Wang";        //以後柏要傳進來的變數 WIRELESS NCCU_Tsai
			                                                      //TOTOLINK A2004NS 2.4G"
			                                                      //NCCU_Wang   WIRELESS
			 String networkPass = ""; 
			 WifiConfiguration conf = new WifiConfiguration();
			 conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes
			 conf.wepKeys[0] = "\"" + networkPass + "\""; 
			 conf.wepTxKeyIndex = 0;
			 conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			 System.out.println("GOGOGO2");
			 conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40); 
			 conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			 WifiManager wifiManager2 = (WifiManager)getSystemService(Context.WIFI_SERVICE); 
			 wifiManager2.addNetwork(conf);

			 List<WifiConfiguration> list = wifiManager2.getConfiguredNetworks();
			 if_Global_local=0;
		for( WifiConfiguration wificonfig : list ) {    //解決ap遇到不在現場就會無法連線的問題
		     if(if_find_wificonnect==true){
	             	System.out.println("Main_configwifi");
		    	 if_find_wificonnect=false;
		          break;

		     }
             for(int i=0;i<SSIDList.size();i++){
             	System.out.println("Main_configwifi"+" "+SSIDList.get(i));
             	
			     if(SSIDList.get(i) != null && SSIDList.get(i).equals(networkSSID)&&
			    		 wificonfig.SSID.equals("\"" + networkSSID + "\"")) { //核心做連線的部分
			    	 System.out.println("Main_configwifi2"+SSIDList.get(i)+" "+networkSSID);
			    	 
					  wifiManager2.disconnect();
			          wifiManager2.enableNetwork(wificonfig.networkId, true);
					  wifiManager2.reconnect();
					  if_find_wificonnect=true;
					  if_Global_local=1;
			          break;
			     }	
			  }
             
             }
			 System.out.println("GOGOGO4"+if_Global_local);
			    Intent intent = new Intent(MainActivity.this,Client_Main.class);   // 改寫成TestWifiScan.this
				intent .putExtra("if_Global_local",if_Global_local);//可放所有基本類別
				startActivity(intent);            	
        }         

			
			
			
//			Intent intent = new Intent();
//			intent.setClass(MainActivity.this, FileexplorerActivity.class);
//			startActivity(intent);
			break;

		case R.id.action_recorder:
			Log.d(tag, "recorder onClick");

			Intent recordIntent = new Intent(
					MediaStore.Audio.Media.RECORD_SOUND_ACTION);
			startActivityForResult(recordIntent, REQUEST_CODE_RECORD);

			// Intent intent_recorder = new Intent();
			// intent_recorder.setClass(MainActivity.this,
			// FileexplorerActivity.class);
			// startActivity(intent_recorder);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
