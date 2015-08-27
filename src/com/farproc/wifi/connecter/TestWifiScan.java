/*
 * Wifi Connecter
 * 
 * Copyright (c) 20101 Kevin Yuan (farproc@gmail.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 **/ 

package com.farproc.wifi.connecter;

import java.util.List;
//import ro.ui.pttdroid.Main.MicrophoneSwitcher;
import edu.mclab1.nccu_story.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;
import android.widget.TwoLineListItem;
import android.widget.AdapterView.OnItemClickListener;

public class TestWifiScan extends ListActivity {
	
	private WifiManager mWifiManager;
	private List<ScanResult> mScanResults;
	//private MicrophoneSwitcher microphoneSwitcher;
	int if_client=0;
	int count=0;
	int Currentlevel=0;
	String CurrentSSID=null;
	
	//location
	double longitude;
	double latitude;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
		Toast.makeText(getApplicationContext(), "您要和身上的無線AP做連接!",
				Toast.LENGTH_LONG).show();
    	
    	mWifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
    	
    	setListAdapter(mListAdapter);
    	
    	getListView().setOnItemClickListener(mItemOnClick);
    	
    	Bundle extras = getIntent().getExtras();
    	longitude = extras.getDouble("longitude");
		latitude = extras.getDouble("latitude");
	}
	
	@SuppressLint("ShowToast")
	@Override
	public void onResume() {
		super.onResume();
		final IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		registerReceiver(mReceiver, filter);
		mWifiManager.startScan();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(mReceiver);
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				mScanResults = mWifiManager.getScanResults();
				mListAdapter.notifyDataSetChanged();
				
				mWifiManager.startScan();
			}
			
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
				getMenuInflater().inflate(R.menu.net_to_client, menu);
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
		case R.id.list_to_client:
			Intent intent = new Intent(this, ro.ui.pttdroid.UploadPage.class);   //開時傳柏的參數
		
			Bundle bundle_broadcast = new Bundle();
			bundle_broadcast.putDouble("longitude", longitude);
			bundle_broadcast.putDouble("latitude", latitude);
			// 將Bundle物件assign給intent
			intent.putExtras(bundle_broadcast);


			//intent.putExtras(bundle);
			startActivity(intent);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private BaseAdapter mListAdapter = new BaseAdapter() {
		
		@SuppressWarnings("deprecation")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//if(convertView == null || !(convertView instanceof TwoLineListItem)) {
				convertView = View.inflate(getApplicationContext(), 
						android.R.layout.simple_list_item_2, null);
			//}
			
			final ScanResult result = mScanResults.get(position);
			System.out.println("count"+count);
			if(Currentlevel==0){
			Currentlevel=result.level;
			CurrentSSID=result.SSID;
			}
			System.out.println("Currentlevel1"+Currentlevel+CurrentSSID+result.SSID);
			if(Currentlevel < result.level &&Currentlevel!=0){
				Currentlevel=result.level;
				CurrentSSID=result.SSID;
				System.out.println("Testwifiscan"+Currentlevel+CurrentSSID+result.SSID);
			}
			if(count%100==0){
				Currentlevel=result.level;
				CurrentSSID=result.SSID;
				System.out.println("Refresh:"+Currentlevel+CurrentSSID+result.SSID+" "+count);
				
			}
			//System.out.println("Testwifiscan"+result.level+result.SSID);
			
			if(count!=0&&result.SSID.contains("NCCU")){   //附近有NCCU都抓出來
			System.out.println("Currentlevel2"+Currentlevel+CurrentSSID+result.SSID);
			((TwoLineListItem) convertView).getText1().setText(result.SSID);
			((TwoLineListItem)convertView).getText1().setTextColor(0xFF008080);
			((TwoLineListItem)convertView).getText2().setText(
					String.format("%s  %d", result.BSSID, result.level)
					);
			}
			else if(count!=0&& !CurrentSSID.contains("NCCU")){  //最靠近的點不是NCCU即全部顯示
				
				System.out.println("Currentlevel3"+Currentlevel+CurrentSSID+result.SSID);
				((TwoLineListItem) convertView).getText1().setText(result.SSID);
				((TwoLineListItem)convertView).getText1().setTextColor(0xFF008080);
				((TwoLineListItem)convertView).getText2().setText(
						String.format("%s  %d", result.BSSID, result.level)
						);
				
			}
			
			count=count+1;
			//((TwoLineListItem)convertView).getText2().setTextColor(0xCD0000);
			return convertView;
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public Object getItem(int position) {
			return null;
		}
		
		@Override
		public int getCount() {
			return mScanResults == null ? 0 : mScanResults.size();
		}
	};
	
	private OnItemClickListener mItemOnClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			final ScanResult result = mScanResults.get(position);
			if(result.SSID.contains("NCCU")){
			launchWifiConnecter(TestWifiScan.this, result);
			}
			else if(!CurrentSSID.contains("NCCU")){
				launchWifiConnecter(TestWifiScan.this, result);
			}
			
		}
	};
	
	/**
	 * Try to launch Wifi Connecter with {@link #hostspot}. Prompt user to download if Wifi Connecter is not installed.
	 * @param activity
	 * @param hotspot
	 */
	private static void launchWifiConnecter(final Activity activity, final ScanResult hotspot) {
		final Intent intent = new Intent("com.farproc.wifi.connecter.action.CONNECT_OR_EDIT");
		intent.putExtra("com.farproc.wifi.connecter.extra.HOTSPOT", hotspot);
		try {
			activity.startActivity(intent);
		} catch(ActivityNotFoundException e) {
			// Wifi Connecter Library is not installed.
			Toast.makeText(activity, "Wifi Connecter is not installed.", Toast.LENGTH_LONG).show();
			downloadWifiConnecter(activity);
		}
	}

	private static void downloadWifiConnecter(final Activity activity) {
		Intent downloadIntent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id=com.farproc.wifi.connecter"));
		try {
			activity.startActivity(downloadIntent);
			Toast.makeText(activity, "Please install this app.", Toast.LENGTH_LONG).show();
		} catch (ActivityNotFoundException e) {
			// Market app is not available in this device.
			// Show download page of this project.
			try {
				downloadIntent.setData(Uri.parse("http://code.google.com/p/android-wifi-connecter/downloads/list"));
				activity.startActivity(downloadIntent);
				Toast.makeText(activity, "Please download the apk and install it manully.", Toast.LENGTH_LONG).show();
			} catch  (ActivityNotFoundException e2) {
				// Even the Browser app is not available!!!!!
				// Show a error message!
				Toast.makeText(activity, "Fatel error! No web browser app in your device!!!", Toast.LENGTH_LONG).show();
			}
		}
	}
}
