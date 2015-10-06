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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ro.ui.pttdroid.codecs.Speex;
import ro.ui.pttdroid.settings.AudioSettings;
import ro.ui.pttdroid.settings.CommSettings;
import ro.ui.pttdroid.util.Audio;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.mclab1.palace.guider.DisplayEvent;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import de.greenrobot.event.EventBus;
import edu.mclab1.nccu_story.R;

public class Client_Player extends Service
{
	private static PlayerThread 	playerThread;
	
	private IBinder playerBinder = new PlayerBinder();
	
	private TelephonyManager	telephonyManager;
	private PhoneCallListener	phoneCallListener;
	public InetAddress addr = null;	
	public static InetAddress  addr50_51=null;
	public static InetAddress  IPSERVER=null;
	public static Boolean if_interrupt=false;
	public static Boolean if_guide_interrupt =false;
	public int count=0;
	public static int count_three=0;
	private Socket connSocket;
    private NotificationManager _nm;
	

	private Boolean if_super_node = false;
	private static DatagramSocket 	socket;	
	private static DatagramSocket 	server_to_client_socket;
	private volatile boolean running = true;	
	private volatile boolean playing = true;
	Thread loopthread;
	volatile boolean terminate = false;
	public static String getIP="";
	int just_one=1;
	//PowerManager.WakeLock mWakeLock; 

	//private MultiRecorder multiRecorder;
	public class PlayerBinder extends Binder 
	{
		Client_Player getService() 
		{ 
            return Client_Player.this;
        } 
    }
	
	/*public static void channel50_51(InetAddress intaAddress)    //接手使用者輸入的ip
	{
		addr50_51= intaAddress;
		System.out.println("addrInPlayer.java:"+addr50_51);
		switch(CommSettings.getCastType()) 
		{
			case CommSettings.BROADCAST:
				try{
				android.util.Log.i("pttdroid", "Broadcast!");
				socket = new DatagramSocket(CommSettings.getPort());
				socket.setBroadcast(true);
				}
				catch (Exception e) {
					// TODO: handle exception
				}
			break;
			case CommSettings.MULTICAST:
				try{
				socket = new MulticastSocket(CommSettings.getPort());
				((MulticastSocket) socket).joinGroup(addr50_51);
				
				}
				catch (Exception e) {
					// TODO: handle exception
				}
			break;
			case CommSettings.UNICAST:
				try{
				socket = new DatagramSocket(CommSettings.getPort());
				}
				catch (Exception e) {
					// TODO: handle exception
				}
			break;
		}	
		

		
    }*/

	
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate() 
	{		 
		playerThread = new PlayerThread();
		playerThread.start();
		System.out.println("goodtimeOncreate"+loopthread);		
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		phoneCallListener = new PhoneCallListener();
		telephonyManager.listen(phoneCallListener, PhoneStateListener.LISTEN_CALL_STATE);
		
		//acquireWakeLock();
	    _nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	    Notification notification = new Notification(R.drawable.ic_launcher,"Service started", System.currentTimeMillis());
	    PendingIntent contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, Client_Main.class), 0);
	    notification.setLatestEventInfo(this, "GuidING","Service started", contentIntent);
	    _nm.notify(R.string.app_running, notification);
	    
		/*Notification notification = new Notification(R.drawable.notif_icon, 
				getText(R.string.app_name),
		        System.currentTimeMillis());
		Intent notificationIntent = new Intent(this, Client_Main.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, getText(R.string.app_name),
		        getText(R.string.app_running), pendingIntent);       		        
		startForeground(1, notification);
		*/
		
		
		/*Intent notificationIntent = new Intent(this, Main.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		Notification noti = new Notification.Builder(this)
		.setContentTitle(getText(R.string.app_name))
		.setContentText(getText(R.string.app_running))
		.setContentIntent(pendingIntent)
		.build();
		startForeground(1, noti);
		*/
		
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Register_SSID_ip");
		System.out.println("latitiude"+Globalvariable.latitude+" "+Globalvariable.longitude);
		// Retrieve the object by id	
		query.whereEqualTo("latitude", Globalvariable.latitude);    //柏傳給我經緯度，我做經緯度限制
		query.whereEqualTo("longitude", Globalvariable.longitude);  	
		query.findInBackground(new FindCallback<ParseObject>() {	
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
		        if (e == null) {
		        	for(int i=0;i<objects.size();i++){
		        		getIP=(String)objects.get(i).get("ip");
		        		System.out.println("getIP"+getIP);
		        		Toast.makeText(getApplicationContext(), "準備播放....",
		        				Toast.LENGTH_LONG).show();
		        	}
		        }
		        else{
		        	System.out.println("getSSIDerror");
		        	
		        }
			}
		});
					
		
		
		
    }

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{			
		return START_NOT_STICKY;
	}
	
	@Override
    public IBinder onBind(Intent intent)
	{    
		EventBus.getDefault().postSticky(new DisplayEvent("Binded player service!") );
		return playerBinder;
    }	
	
	@Override
	public void onDestroy() 
	{
	    //stopForeground(true);
		//releaseWakeLock();
		
		System.out.println("goodtime"+loopthread);		
		//notify();
		if(playerThread!=null){
			playerThread.interrupt();
			playerThread=null;
			terminate=true;
			playing=false;
			running = false;
		}
		if(loopthread!=null){
			loopthread.interrupt();
			loopthread=null;
			System.out.println("goodtime2"+loopthread);
		}
		telephonyManager.listen(phoneCallListener, PhoneStateListener.LISTEN_NONE);
	}
	
	public int getProgress() 
	{
		return playerThread.getProgress();
	}
	
	
	/*//申請設備電源鎖
	private void acquireWakeLock()
	{
	    System.out.println("MyGPS正在申請電源鎖"); if (null == mWakeLock)
	    {
	        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE); 
	        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK |PowerManager.ON_AFTER_RELEASE, ""); 
	        if (null != mWakeLock)
	        { 
	            mWakeLock.acquire();
	            System.out.println("電源鎖申請成功");
	        }
	    }
	} 
	
	private void releaseWakeLock()
	{
		System.out.println("正在釋放電源鎖");
	    if (null != mWakeLock)
	    { 
	        mWakeLock.release(); mWakeLock = null;
	        System.out.println("電源鎖釋放成功");
	    }
	}*/
	
	private class PlayerThread extends Thread
	{
		private AudioTrack 	player;
		

		
		private DatagramPacket 	packet;
		private DatagramPacket 	dp;	
		private DatagramSocket recorder_socket;
		private short[] pcmFrame = new short[Audio.FRAME_SIZE];
		private byte[] 	encodedFrame;
		
		private AtomicInteger progress = new AtomicInteger(0);
		byte buffer[]=new byte[8192];
		@Override
		public void run() 
		{
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);	
            while(isRunning())
				{				
					init();
					System.out.println("isRunning!!!!");

					try {   
						
						while(isPlaying()) 
						{
							System.out.println("isPlayingInPlayer!!!!");
							//new Thread(){
							//	@Override
							//	public void run(){ 
							if(if_interrupt==false){           //if 遇到沒有data時就測有沒有IP

								//just_one=2;
								//for(int i=1;i<=255;i++){
									//getIP="192.168.2."+i;
				        		//System.out.println("getIP2"+getIP);
								try {
									addr50_51=InetAddress.getByName(getIP);
								} catch (UnknownHostException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}   //can give 239.255.255.250 or etc (兩個MULTICAST)
								try {
									Thread.sleep(1500);
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}

								

							switch(CommSettings.getCastType()) 
							{
								case CommSettings.BROADCAST:
									try{
										
								    System.out.println("ReadytoBROADCAST");
									android.util.Log.i("pttdroid", "Broadcast!");
									socket = new DatagramSocket(CommSettings.getPort());
									socket.setBroadcast(true);
									}
									catch (Exception e) {
										// TODO: handle exception
									}
								break;
								case CommSettings.MULTICAST:
									try{
										System.out.println("Readytomulticast");
									socket = new MulticastSocket(CommSettings.getPort());
									((MulticastSocket) socket).joinGroup(addr50_51);
									
									}
									catch (Exception e) {
										// TODO: handle exception
									}

								break;
								case CommSettings.UNICAST:
									try{
									socket = new DatagramSocket(CommSettings.getPort());
									}
									catch (Exception e) {
										// TODO: handle exception
									}
								break;
							}
								}
							//}
							//}}.start();

							
							try 
							{
								System.out.println("Ready to receive!"); 
				                 try{
								    socket.setSoTimeout(1000);

				                  }catch(Exception e){
				                	  e.printStackTrace();
						         	//System.out.println("i am in catch !");
				                  }
				                 
									socket.receive(packet);
						         	//System.out.println("good!+ addr50_51_3"+addr50_51);
							        count=count+1;

									if(count_three%20==0){
										socket.receive(dp);												
							        String msg=new String(buffer,0,dp.getLength());
							       
							       
							       int index=msg.indexOf("Tim");                                             //濾掉聲音的封包字串
							       String submsg=msg.substring(index+3,msg.length());
							       System.out.println("socket_receive"+msg+getIP);
							       if(index!=-1){
										if_guide_interrupt=true;
										System.out.println("if_guide_interrupttrue");
							       }
								    //bw.write(msg+"\n");
							       }
									count_three=count_three+1;

									
									if_interrupt=true;
									//System.out.println("if_interrupt"+if_interrupt);
									System.out.println("addrInPlayer2.java:"+addr50_51);
									
									




									
							}
							catch(SocketException e) //Due to socket.close() 
							{
								break;
							}
							catch(Exception e) 
							{
								count_three=0;
								if_guide_interrupt=false;
								System.out.println("if_guide_interrupt=false");


								continue;
							}
										

							if(AudioSettings.useSpeex()==AudioSettings.USE_SPEEX) 
							{
								Speex.decode(encodedFrame, encodedFrame.length, pcmFrame);
								//System.out.println("pcmFrame:"+encodedFrame);
								player.write(pcmFrame, 0, Audio.FRAME_SIZE);

							}
							else 
							{			
								player.write(encodedFrame, 0, Audio.FRAME_SIZE_IN_BYTES);
							}
							//progress.incrementAndGet();
						}
						//bw.close();
						System.out.println("done");
							
						player.stop();
						player.release();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					
					
					
				}

		
			//recorder_socket.close();
		}
		
				

		private void init() 
		{	


		loopthread =new Thread(){
				@Override
				public void run(){
					while(!terminate){
					SimpleDateFormat sdf=new SimpleDateFormat("ss");
					String predate=sdf.format(new java.util.Date());
					int precount=count;
					System.out.println("count"+count+" "+predate);

                    try {
						sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}


					String curdate=sdf.format(new java.util.Date());
					System.out.println("curdate"+count+" "+curdate);
					if(precount==count){
						if_interrupt=false;
						//pauseAudio();
						System.out.println("no voice!!!!!"+curdate+" "+predate);
					}

					}
					
				}
				
			};
			loopthread.start();

			try 
			{
				System.out.println("count-"+count);
				
				addr50_51=InetAddress.getByName(getIP);   //can give 239.255.255.250 or etc 

				switch(CommSettings.getCastType()) 
				{
					case CommSettings.BROADCAST:
						try{
							
					    System.out.println("ReadytoBROADCAST");
						android.util.Log.i("pttdroid", "Broadcast!");
						socket = new DatagramSocket(CommSettings.getPort());
						socket.setBroadcast(true);
						}
						catch (Exception e) {
							// TODO: handle exception
						}
					break;
					case CommSettings.MULTICAST:
						try{
							System.out.println("Readytomulticast");
						socket = new MulticastSocket(CommSettings.getPort());
						((MulticastSocket) socket).joinGroup(addr50_51);
						
						}
						catch (Exception e) {
							// TODO: handle exception
						}

					break;
					case CommSettings.UNICAST:
						try{
						socket = new DatagramSocket(CommSettings.getPort());
						}
						catch (Exception e) {
							// TODO: handle exception
						}
					break;
				}	
				player = new AudioTrack(
						AudioManager.STREAM_MUSIC, 
						Audio.SAMPLE_RATE, 
						AudioFormat.CHANNEL_OUT_MONO, 
						Audio.ENCODING_PCM_NUM_BITS, 
						Audio.TRACK_BUFFER_SIZE, 
						AudioTrack.MODE_STREAM);
				
				
				if(AudioSettings.useSpeex()==AudioSettings.USE_SPEEX) 
					encodedFrame = new byte[Speex.getEncodedSize(AudioSettings.getSpeexQuality())];
				else 
					encodedFrame = new byte[Audio.FRAME_SIZE_IN_BYTES];
				
				   packet = new DatagramPacket(encodedFrame, encodedFrame.length);
		           dp = new DatagramPacket(buffer, buffer.length);

				
				player.play();
			}
			catch(Exception e) 
			{
				e.printStackTrace();
			}		
		}
		
		private synchronized boolean isRunning()
		{
			return running;
		}
			
		private synchronized boolean isPlaying()
		{
			return playing;
		}
				
		public synchronized void pauseAudio() 
		{				
			playing = false;
			notify();

			
			/*try
			{
				if(socket instanceof MulticastSocket)
					((MulticastSocket) socket).leaveGroup(addr50_51);
				socket.close();
			}
			catch (IOException e) 
			{
				//Log.error(getClass(), e);
			}*/		
		}
		
		public synchronized void resumeAudio() 
		{
			playing = true;
			notify();
		}
									
		private synchronized void shutdown() 
		{			
			pauseAudio();
			running = false;						
			notify();
		}
		
		public int getProgress() 
		{
			return progress.intValue();
		}

	}
	

	
	private class PhoneCallListener extends PhoneStateListener
	{
		
		@Override
		public void onCallStateChanged (int state, String incomingNumber)
		{
			if(state==TelephonyManager.CALL_STATE_OFFHOOK)
				playerThread.pauseAudio();
			else if(state==TelephonyManager.CALL_STATE_IDLE)
				playerThread.resumeAudio();
		}
		
	}


}
