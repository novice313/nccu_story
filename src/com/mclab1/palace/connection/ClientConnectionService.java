package com.mclab1.palace.connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.mclab1.palace.guider.DisplayEvent;
import com.mclab1.palace.recorder.CustomRecorder;
import com.mclab1.palace.recorder.EraseCustomRecorder;
import com.mclab1.palace.recorder.Mp3Recorder;
import com.mclab1.palace.socket.EraseSocketServerHelper;
import com.mclab1.palace.socket.SocketServerHelper;
import com.mclab1.place.events.EraseclientConnectionEvent;
import com.mclab1.place.events.NewClientConnectionEvent;
import com.mclab1.place.events.PauseAudioEvent;
import com.mclab1.place.events.ResumeAudioEvent;

import de.greenrobot.event.EventBus;

public class ClientConnectionService extends Service {
	private ClientConnectionServiceThread clientConnectionServiceThread;
	CustomRecorder customRecorder;
	EraseCustomRecorder eraseCustomRecorder;
	Mp3Recorder mp3Recorder;
	private IBinder playerBinder = new ClientConnectionServiceBinder();
	public static final String SOCKET_TAG_STRING = "wifi-socket-test";
	public static Boolean run = false;
	public static Socket socket;

	public static final String tag = "ClientConnectionService";

	public class ClientConnectionServiceBinder extends Binder {
		ClientConnectionService getService() {
			return ClientConnectionService.this;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		//mp3Recorder = new Mp3Recorder();
		//mp3Recorder.start();

		EventBus.getDefault()
				.postSticky(
						new DisplayEvent(
								"Created client connection listener service!"));
		clientConnectionServiceThread = new ClientConnectionServiceThread();
		clientConnectionServiceThread.start();
		try {
			// test_socket_server();
			new Thread() {
				@Override
				public void run() {

					try {
						ServerSocket listener = new ServerSocket(8888);
						Socket server;
						EventBus.getDefault().postSticky(
								new DisplayEvent("IP handle server init!"));
						Log.i(tag, "Starting socket server111");
						while (true) {

							server = listener.accept();

							SocketServerHelper conn_c = new SocketServerHelper(
									server);
							Thread t = new Thread(conn_c);
							t.start();

						}
					} catch (IOException ioe) {
						Log.i(tag, "Socket server error!");
						System.out.println("IOException on socket listen: "
								+ ioe);
						ioe.printStackTrace();
					}
				}
			}.start();
			new Thread() {
				@Override
				public void run() {

					try {
						ServerSocket listener = new ServerSocket(8889);
						Socket server2;
						EventBus.getDefault().postSticky(
								new DisplayEvent("Erase handler"));
						while (true) {

							server2 = listener.accept();

							EraseSocketServerHelper conn_c2 = new EraseSocketServerHelper(
									server2);
							Thread t2 = new Thread(conn_c2);
							t2.start();

						}
					} catch (IOException ioe) {
						Log.i(tag, "Socket server error!");
						System.out.println("IOException on socket listen: "
								+ ioe);
						ioe.printStackTrace();
					}
				}
			}.start();
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		EventBus.getDefault().register(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return playerBinder;
	}

	@Override
	public void onDestroy() {
		try {
			EventBus.getDefault().unregister(this);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void onEvent(NewClientConnectionEvent event) {
		// got new client in
		InetAddress isa;
		try {
			Log.i(tag, "Starting to unicast to:"
					+ event.client_ip.split(":")[0].replace("/", ""));
			EventBus.getDefault()
			.postSticky(
					new DisplayEvent(
							"Show Start Service!!"));
			isa = InetAddress.getByName(event.client_ip.split(":")[0].replace(
					"/", ""));
			//customRecorder = new CustomRecorder(isa, true);
			//customRecorder.start();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void onEvent(EraseclientConnectionEvent event) {
		// got new client in
		InetAddress isa;
		try {
			Log.i(tag, "Starting to unicast to:"
					+ event.client_ip.split(":")[0].replace("/", ""));
			EventBus.getDefault()
			.postSticky(
					new DisplayEvent(
							"Show Eraseclient Service!!"));
			isa = InetAddress.getByName(event.client_ip.split(":")[0].replace(
					"/", ""));
			eraseCustomRecorder=new EraseCustomRecorder(isa,true);
			eraseCustomRecorder.start();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public void onEvent(ResumeAudioEvent event) {
		
		if (customRecorder != null) {
			//customRecorder.resumeAudio();
			//customRecorder.start_recording();

		}
		if(mp3Recorder!=null){
		mp3Recorder.resumeAudio();
		mp3Recorder.start_recording();
		}

			
	}

	public void onEvent(PauseAudioEvent event) {
		if (customRecorder != null) {
			//customRecorder.pauseAudio();
			//customRecorder.stop_recording();

		}		
		if(mp3Recorder!=null){
		mp3Recorder.pauseAudio();
		mp3Recorder.stop_recording();
		}

	}
	
    // GETS THE IP ADDRESS OF YOUR PHONE'S NETWORK
    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) { return inetAddress.getHostAddress().toString(); }
                }
            }
        } catch (SocketException ex) {
            Log.e("ServerActivity", ex.toString());
        }
        return null;
    }

	private class ClientConnectionServiceThread extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
					// TODO: handle exception
				}
				// Log.i(tag, "Client connection service running!");
			}
		}
	}

}
