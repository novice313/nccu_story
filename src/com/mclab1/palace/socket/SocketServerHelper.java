package com.mclab1.palace.socket;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import android.util.Log;

import com.mclab1.palace.guider.DisplayEvent;
import com.mclab1.place.events.NewClientConnectionEvent;

import de.greenrobot.event.EventBus;

public class SocketServerHelper implements Runnable {
	private Socket server;
	private String line, input;
	private static final String tag = "wifi-socket-server";

	public SocketServerHelper(Socket server) {
		this.server = server;
	}

	@Override
	public void run() {

		input = "";

		try {
			// Get input from the client
			DataInputStream in = new DataInputStream(server.getInputStream());
			PrintStream out = new PrintStream(server.getOutputStream());

			while ((line = in.readLine()) != null && !line.equals(".")) {
				input = input + line;
				out.println("I got:" + line);
			}

			// Now write to the client

			// System.out.println("Overall message is:" + input);
			// out.println("Overall message is:" + input);
			Log.i("wifi-socket-server", input);
			Log.i(tag, server.getRemoteSocketAddress().toString());
			EventBus.getDefault().postSticky(new DisplayEvent("Got incomming connection from:"+server.getRemoteSocketAddress().toString()) );
			EventBus.getDefault().postSticky(
					new NewClientConnectionEvent(server
							.getRemoteSocketAddress().toString()));
			server.close();
		} catch (IOException ioe) {
			System.out.println("IOException on socket listen: " + ioe);
			ioe.printStackTrace();
		}
	}
}