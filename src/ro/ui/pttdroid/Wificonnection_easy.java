package ro.ui.pttdroid;

import java.util.Iterator;
import java.util.List;

import edu.mclab1.nccu_story.R; //影響選R值的頁面
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Wificonnection_easy extends Activity{
	
    private Button wificonnection_easy;
    private WifiManager wiFiManager;

    int if_Global_local=-1;
    Intent  intent;
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wificonnection_easy);
        wificonnection_easy = (Button)findViewById(R.id.ready_to_connect);
        
        wificonnection_easy.setOnClickListener(new Button.OnClickListener(){ 

            @Override

            public void onClick(View v) {
                // TODO Auto-generated method stub
            	wiFiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            	System.out.println("wiFiManagergetConnectionInfo"+wiFiManager.getConnectionInfo()+"$"+
            			wiFiManager.getWifiState()+" ");

            	if (!wiFiManager.isWifiEnabled()){ //判斷是否有網路
            	 Toast.makeText(v.getContext(), "要開啟網路(Wifi/3G)!", Toast.LENGTH_SHORT).show();

            	}else{

            	
				 System.out.println("GOGOGO");            //network module connect 
				 String networkSSID = "WIRELESS";        //以後柏要傳進來的變數 WIRELESS NCCU_Tsai
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

				 if_Global_local=0;
				 List<WifiConfiguration> list = wifiManager2.getConfiguredNetworks();
				 for( WifiConfiguration i : list ) {
				     /*if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) { //核心做連線的部分
				    	 if_Global_local=1; 
						 System.out.println("GOGOGO3"+if_Global_local);
				    	 
				     }*/
				     System.out.println("networkSSID"+networkSSID+" "+i.SSID);
				     if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) { //核心做連線的部分
						  wifiManager2.disconnect();
				          wifiManager2.enableNetwork(i.networkId, true);
						  wifiManager2.reconnect();


				          break;
				     }

				     
				     
				  }
				 
				 List<android.net.wifi.ScanResult> scanResults = wifiManager2.getScanResults();
			        Iterator<android.net.wifi.ScanResult> iter = scanResults.iterator();
			        while (iter.hasNext()) {
			        	if(iter.next().SSID.equals(networkSSID)){
					    	 if_Global_local=1; 
							 System.out.println("GOGOGO3"+if_Global_local);
			        		   
					          // System.out.println("iterr"+iter.next().SSID);
			        	}
			        }
			        if(if_Global_local==0){
			        	 wiFiManager.reconnect();
						 System.out.println("GOGOGO3.5");

			        }
				 System.out.println("GOGOGO4"+if_Global_local);
				    intent = new Intent(Wificonnection_easy.this,Main.class);   // 改寫成TestWifiScan.this
					intent .putExtra("if_Global_local",if_Global_local);//可放所有基本類別
					startActivity(intent);            	
            }         

            }
            
        });     
        }
    
        

        
    }


