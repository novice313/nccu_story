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
import java.net.InetSocketAddress;
import java.net.Socket;

import ro.ui.pttdroid.Player.PlayerBinder;
import ro.ui.pttdroid.codecs.Speex;
import ro.ui.pttdroid.settings.AudioSettings;
import ro.ui.pttdroid.settings.CommSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.wifidirect.DeviceDetailFragment;
import com.example.android.wifidirect.DeviceListFragment;
import com.example.android.wifidirect.DeviceListFragment.DeviceActionListener;
import com.example.android.wifidirect.WiFiDirectBroadcastReceiver;
import com.example.android.wifidirect.WifiReceiver;
import com.mclab1.palaca.parsehelper.ParseHelper;
import com.mclab1.palace.connection.ClientConnectionService;
import com.mclab1.palace.customer.CustomerFragment;
import com.mclab1.palace.guider.DisplayEvent;
import com.mclab1.palace.guider.GuiderFragment;
import com.mclab1.place.events.NewClientConnectionEvent;
import com.mclab1.place.events.NewServerConnectionEvent;
import com.mclab1.place.events.PauseAudioEvent;
import com.mclab1.place.events.ResumeAudioEvent;

import de.greenrobot.event.EventBus;
import edu.mclab1.nccu_story.R;

public class Main extends FragmentActivity implements ChannelListener,
		DeviceActionListener {
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

	private static GuiderRecorder guiderrecorder;
	
	private static GuiderRecorder realTimeRecorder;
	
	private MicrophoneSwitcher microphoneSwitcher;

	private static Intent playerIntent;

	private static Intent clientConnectionIntent;

	private static Intent customPlayerIntent;
	

	private WifiReceiver wifiReceiver = null;
	private Spinner spinner;   
	private Spinner spinner2;                                                                      
	private ArrayAdapter<String> lunchList;  
	private ArrayAdapter<String> lunchList2;                                                       
	
	private Context mContext;
	private Context mContext2;
	IntentFilter filter = new IntentFilter();

	private static final Boolean if_guider = true;//if_guider
	private static final Boolean if_guider_pro = true;//if_guider_pro

	private Boolean if_client_offline_mode = true;
	private Button btn_test_mp3;

	private Boolean if_talking = false;
	//private Boolean if_order=false;
	private ImageView writing;
	private NfcAdapter adapter;
	
	private String[] lunch = {"1", "2", "3", "4"};
	private String[] lunch2 = {"Chinese", "English", "Japanese", "Korean"};
	String[][] Selection = new String[][] {
	{"1Chinese","239.255.255.239"},{"1English","239.255.255.240"},{"1Japanese","239.255.255.241"},{"1Korean","239.255.255.242"}
   ,{"2Chinese","239.255.255.243"},{"2English","239.255.255.244"},{"2Japanese","239.255.255.245"},{"2Korean","239.255.255.246"}
   ,{"3Chinese","239.255.255.247"},{"3English","239.255.255.248"},{"3Japanese","239.255.255.249"},{"3Korean","239.255.255.250"}
   ,{"4Chinese","239.255.255.251"},{"4English","239.255.255.252"},{"4Japanese","239.255.255.253"},{"4Korean","239.255.255.254"}};
	public static String Selected="";
	public static String Number="";

	public static String commIP="239.255.255.250";



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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		getActionBar().setBackgroundDrawable(null);
		getActionBar().setBackgroundDrawable(getResources().getDrawable(android.R.color.darker_gray));
		 System.out.println("startmain!!!!!!!!!!");
		 
		/* mContext = this.getApplicationContext();
		   spinner = (Spinner)findViewById(R.id.mySpinner); 
		   
		   mContext2 = this.getApplicationContext();
		   spinner2 = (Spinner)findViewById(R.id.mySpinner2); 
		   
		   lunchList = new ArrayAdapter<String>(Main.this,android.R.layout.simple_spinner_item, lunch);
		   spinner.setAdapter(lunchList);
		   
		   lunchList2 = new ArrayAdapter<String>(Main.this,android.R.layout.simple_spinner_item, lunch2);
		   spinner2.setAdapter(lunchList2);
		   */


		   
		 
		try{
		 NfcManager managers = (NfcManager)getSystemService(Context.NFC_SERVICE);
		 adapter = managers.getDefaultAdapter();
		}
		catch(Exception e){	
		}
		try {
			ParseHelper.initParse(this);
			EventBus.getDefault().postSticky("Parse init success!");
		} catch (Exception e) {
			EventBus.getDefault().postSticky("Failed to int parse!");
		}
		loadAd();

		init();
		//writing = (ImageView) findViewById(R.id.writing);
		if (if_guider) {
		//	writing.setVisibility(View.VISIBLE);
		//	writing.setBackgroundResource(R.drawable.circle_green);
		}

	/*	btn_test_mp3 = (Button) findViewById(R.id.btn_test_mp3);
		btn_test_mp3.setOnClickListener(new OnClickListener() {

			@Override 
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Mp3Helper mp3Helper = new Mp3Helper("", "");
				mp3Helper.test_start_converting();
			}
		});

		Button button = (Button) findViewById(R.id.force);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				test_socket_server();
			}
		});
		});*/

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
		FragmentManager fragmentManager = getSupportFragmentManager();
		CustomerFragment customerFragment = new CustomerFragment();
		fragmentManager.beginTransaction()
				.replace(R.id.content_holder, customerFragment)
				.commitAllowingStateLoss();
	}

	/*private void change_to_client_offline_fragment() {
		microphoneSwitcher.hide();
		FragmentManager fragmentManager = getSupportFragmentManager();
		CustomerFragmentOffline customerFragment = new CustomerFragmentOffline();
		fragmentManager.beginTransaction()
				.replace(R.id.content_holder, customerFragment)
				.commitAllowingStateLoss();
	}*/

	public void onEvent(NewClientConnectionEvent event) {

	}

	public void onEvent(WritingMp3Event event) {
		if (event.if_writing) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (writing != null) {
						writing.setBackgroundResource(R.drawable.circle_red);
					}

				}
			});
		} else {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (writing != null) {
						writing.setBackgroundResource(R.drawable.circle_green);
					}

				}
			});
		}
	}
/*
	public void test_connect() {
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
*/
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
	}

	@Override
	public void onResume() {
		super.onResume();		   
/*	  spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
		   
		   
	    @Override
	    public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
	    	//if_order=true;
	    	Number=lunch[position].toString();
	    	
	       Toast.makeText(mContext, "你選的是"+Number, Toast.LENGTH_SHORT).show();
	                
	    }
	    
	    
	    @Override
	    public void onNothingSelected(AdapterView<?> arg0) {
	       // TODO Auto-generated method stub
	    }
	});
   
   spinner2.setOnItemSelectedListener(new OnItemSelectedListener(){

	    @Override
	    public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
		//if(if_order==true){

	    	int i=0;
	    	Selected=Number+lunch2[position].toString();
	    	System.out.println("SelectedSelected"+Selected);

	    	for( ; i<16 ; i++){
	    		if(Selected.equals(Selection[i][0])){
	    			Selected=Selection[i][1];
	    			break;
	    		}
	    	}
	    	
	       
	       Toast.makeText(mContext, "你選的是"+Selected, Toast.LENGTH_SHORT).show();
	     //  if_order=false;        
	    //}
	    }
	    
	    
	    @Override
	    public void onNothingSelected(AdapterView<?> arg0) {
	       // TODO Auto-generated method stub
	    }
	});*/   
		   
		microphoneSwitcher = new MicrophoneSwitcher();
		microphoneSwitcher.init();
		if (if_guider) {
			test_create_group();
			FragmentManager fragmentManager = getSupportFragmentManager();
			GuiderFragment guiderFragment = new GuiderFragment();
			fragmentManager.beginTransaction()
					.replace(R.id.content_holder, guiderFragment)
					.commitAllowingStateLoss();
		} else {
			//change_to_client_offline_fragment();
		}
		wifiReceiver = new WifiReceiver();
		registerReceiver(wifiReceiver, filter);
		receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
		registerReceiver(receiver, intentFilter);

	}

	@Override
	public void onPause() {
		super.onPause();
		if (if_guider_pro) {
			//guiderrecorder.pauseAudio();
			//guiderrecorder.stop_recording();
		}
		microphoneSwitcher.shutdown();
		unregisterReceiver(receiver);
		unregisterReceiver(wifiReceiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (if_guider) {
			getMenuInflater().inflate(R.menu.action_items, menu);
		} else {
			if (if_client_offline_mode) {
				getMenuInflater().inflate(R.menu.menu_client_offline, menu);
			} else {
				getMenuInflater().inflate(R.menu.menu_client, menu);
			}
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
			if (if_client_offline_mode) {
				if_client_offline_mode = false;
				change_to_client_online_fragment();
			} else {
				if_client_offline_mode = true;
				//change_to_client_offline_fragment();
			}
			invalidateOptionsMenu();
			return true;
		/*case R.id.client_to_list:
			Intent intent3 =new Intent(this,TestWifiScan.class);
			startActivity(intent3);
			return true;*/
		/*case R.id.atn_direct_discover:
			if (!isWifiP2pEnabled) {
				Toast.makeText(Main.this, R.string.p2p_off_warning,
						Toast.LENGTH_SHORT).show();
				return true;
			}*/
			/*final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
					.findFragmentById(R.id.frag_list);
			fragment.onInitiateDiscovery();
			manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

				@Override
				public void onSuccess() {
					Toast.makeText(Main.this, "Discovery Initiated",
							Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onFailure(int reasonCode) {
					Toast.makeText(Main.this,
							"Discovery Failed : " + reasonCode,
							Toast.LENGTH_SHORT).show();
				}
			});*/
			//Intent intent_direct = new Intent(this,WiFiDirectActivity.class); 
			//startActivity(intent_direct);
			//return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	

	/**
	 * Reset all settings to their default value
	 * 
	 * @return
	 */
	private boolean resetAllSettings() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		Editor editor = prefs.edit();
		editor.clear();
		editor.commit();

		Toast toast = Toast.makeText(this,
				getString(R.string.setting_reset_all_confirm),
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		CommSettings.getSettings(this);
		AudioSettings.getSettings(this);
	}

	private void loadAd() {

	}

	private void init() {
		if (firstLaunch) {
			CommSettings.getSettings(this);
			AudioSettings.getSettings(this);

			setVolumeControlStream(AudioManager.STREAM_MUSIC);

			Speex.open(AudioSettings.getSpeexQuality());

			playerIntent = new Intent(this, Player.class);
			startService(playerIntent);

			clientConnectionIntent = new Intent(this,
					ClientConnectionService.class);
			startService(clientConnectionIntent);

			//customPlayerIntent = new Intent(this, CustomPlayer.class);
			//startService(customPlayerIntent);
			if (if_guider_pro) {
				guiderrecorder = new GuiderRecorder();
				guiderrecorder.start();

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
			if (if_guider_pro) {
				guiderrecorder.shutdown();
			}
			Speex.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			finish();
		}

	}

	private class MicrophoneSwitcher implements Runnable, OnTouchListener {
		private Player player;

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
			microphoneImage = (ImageView) findViewById(R.id.microphone_image);
			microphoneImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!if_talking) {
						// start talking
						if (if_guider_pro) {
							guiderrecorder.resumeAudio();
							guiderrecorder.start_recording();

						}
						if_talking = true;
						setMicrophoneState(MicrophoneSwitcher.MIC_STATE_PRESSED);
						EventBus.getDefault().post(new ResumeAudioEvent());
						EventBus.getDefault().postSticky(
								new DisplayEvent("Start talking!"));
					} else {
						// stop takling
						setMicrophoneState(MicrophoneSwitcher.MIC_STATE_NORMAL);
						if (if_guider_pro) { 	
							guiderrecorder.pauseAudio();
							//realTimeRecorder.RealtimeFinal_stop_recording(); //1 min
							guiderrecorder.stop_recording(); //5 min

						}
						if_talking = false;
						EventBus.getDefault().post(new PauseAudioEvent());
						EventBus.getDefault().postSticky(
								new DisplayEvent("Stop talking!"));
					}

				}
			});

			// microphoneImage.setOnTouchListener(this);

			Intent intent = new Intent(Main.this, Player.class);
			playerServiceConnection = new PlayerServiceConnection();
			bindService(intent, playerServiceConnection,
					Context.BIND_AUTO_CREATE);

			handler.postDelayed(this, PROGRESS_CHECK_PERIOD);
		}

		@Override
		public void run() {
			synchronized (running) {
				if (running && player != null) {
					int currentProgress = player.getProgress();

					if (currentProgress > previousProgress) {
						if (microphoneState != MIC_STATE_DISABLED) {
							if (if_guider_pro){
								//recorder.pauseAudio();
								//recorder.stop_recording();
								
							}
							//setMicrophoneState(MIC_STATE_DISABLED);
						}
					} else {
						if (microphoneState == MIC_STATE_DISABLED)
							setMicrophoneState(MIC_STATE_NORMAL);
					}

					previousProgress = currentProgress;

					handler.postDelayed(this, PROGRESS_CHECK_PERIOD);
				}
			}
		}

		@Override
		public boolean onTouch(View v, MotionEvent e) { // 好像沒用
			if (microphoneState != MicrophoneSwitcher.MIC_STATE_DISABLED) {
				switch (e.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (if_guider_pro) {
						//guiderrecorder.resumeAudio();
						//guiderrecorder.start_recording();
					}
					setMicrophoneState(MicrophoneSwitcher.MIC_STATE_PRESSED);
					EventBus.getDefault().post(new ResumeAudioEvent());
					break;
				case MotionEvent.ACTION_UP:
					setMicrophoneState(MicrophoneSwitcher.MIC_STATE_NORMAL);
					if (if_guider_pro) {
						//guiderrecorder.pauseAudio();
						//guiderrecorder.stop_recording();
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
			/*case MIC_STATE_DISABLED:
				microphoneState = MIC_STATE_DISABLED;
				microphoneImage
						.setImageResource(R.drawable.microphone_disabled_image);
				break;*/
			}
		}

		public void shutdown() {
			synchronized (running) {
				unbindService(playerServiceConnection);
				handler.removeCallbacks(microphoneSwitcher);
				running = false;
			}
		}

		private class PlayerServiceConnection implements ServiceConnection {
			@Override
			public void onServiceConnected(ComponentName arg0, IBinder arg1) {
				synchronized (running) {
					player = ((PlayerBinder) arg1).getService();
				}
			}

			@Override
			public void onServiceDisconnected(ComponentName arg0) {
				synchronized (running) {
					player = null;
				}
			}
		};

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
	public void connect(WifiP2pConfig config) {
		manager.connect(channel, config, new ActionListener() {

			@Override
			public void onSuccess() {
				// WiFiDirectBroadcastReceiver will notify us. Ignore for now.
			}

			@Override
			public void onFailure(int reason) {

			}
		});
	}


	@Override
	public void disconnect() {
		final DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
				.findFragmentById(R.id.frag_detail);
		fragment.resetViews();
		manager.removeGroup(channel, new ActionListener() {

			@Override
			public void onFailure(int reasonCode) {

			}

			@Override
			public void onSuccess() {
				fragment.getView().setVisibility(View.GONE);
			}

		});
	}

	@Override
	public void onChannelDisconnected() {
		// we will try once more
		if (manager != null && !retryChannel) {
			Toast.makeText(this, "Channel lost. Trying again",
					Toast.LENGTH_LONG).show();
			resetData();
			retryChannel = true;
			manager.initialize(this, getMainLooper(), this);
		} else {
			Toast.makeText(
					this,
					"Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void cancelDisconnect() {

		/*
		 * A cancel abort request by user. Disconnect i.e. removeGroup if
		 * already connected. Else, request WifiP2pManager to abort the ongoing
		 * request
		 */
		if (manager != null) {
			final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
					.findFragmentById(R.id.frag_list);
			if (fragment.getDevice() == null
					|| fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
				disconnect();
			} else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
					|| fragment.getDevice().status == WifiP2pDevice.INVITED) {

				manager.cancelConnect(channel, new ActionListener() {

					@Override
					public void onSuccess() {

					}

					@Override
					public void onFailure(int reasonCode) {

					}
				});
			}
		}

	}

}