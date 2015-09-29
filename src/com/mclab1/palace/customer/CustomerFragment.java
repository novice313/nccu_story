package com.mclab1.palace.customer;

import java.io.BufferedOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import ro.ui.pttdroid.Client_Main;
import edu.mclab1.nccu_story.R;
import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.mclab1.palace.guider.DisplayEvent;
import com.mclab1.place.events.EraseServerConnectionEvent;
import com.mclab1.place.events.NewServerConnectionEvent;
import de.greenrobot.event.EventBus;

@SuppressLint("ShowToast")
public class CustomerFragment extends Fragment {
	public static final String SOCKET_TAG_STRING = "wifi-socket-test";
	public static final String TAG = "CustomerFragment";
	private ListView log_list_view;
	private ArrayList<String> voiceList = new ArrayList<String>();
	private ArrayAdapter<String> listAdapter;
	private final SimpleDateFormat sdFormat = new SimpleDateFormat("HH:mm:ss");
	String detail = "";
	final String tempFile = Environment.getExternalStorageDirectory() + "/";
	private MediaPlayer mpintro;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.customer_fragment, container,
				false);
		//init_view(view);
		
        Toast.makeText(
                getActivity(),
                "注意！請使用者要回扣給導覽員，只限一個人回扣，並把聲音賤關小聲",
                5000).show();

		return view;
	}

	private void init_view(View v) {
		log_list_view = (ListView) v.findViewById(R.id.customer_mp3_listview);
		listAdapter = new ArrayAdapter<String>(getActivity(),
				R.layout.event_row_list_item, voiceList);
		// listAdapter.add("aaa");
		log_list_view.setAdapter(listAdapter);
		log_list_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				String mp3Path=voiceList.get(pos);
				mpintro = MediaPlayer.create(getActivity(), Uri.parse(mp3Path));
				mpintro.start();

			}
		});

		/*final Button button = (Button) v.findViewById(R.id.customer_force_connect);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				test_socket_server();
			}
		});
		final Button button_erase = (Button) v.findViewById(R.id.customer_erase_connect);
		button_erase.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				erase_socket_server();
			}
		});
		

		Button refreshButton = (Button) v.findViewById(R.id.customer_refresh);
		refreshButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				voiceList.clear();
				ParseQuery<ParseObject> query = ParseQuery
						.getQuery(VoiceObject.table_name);
				query.findInBackground(new FindCallback<ParseObject>() {
					@Override
					public void done(List<ParseObject> voiceParseObjectList,
							ParseException e) {
						if (e == null) {
							Log.i(TAG, "Get parse objects success!");
							for (int i = 0; i != voiceParseObjectList.size(); i++) {
								detail = "";
								ParseObject parseObject = voiceParseObjectList
										.get(i);
								final SimpleDateFormat sdFormat = new SimpleDateFormat(
										"yyyy/MM/dd HH:mm");
								ParseFile parseFile = (ParseFile) parseObject
										.get(VoiceObject.column_audio_file);
								parseFile
										.getDataInBackground(new GetDataCallback() {

											@Override
											public void done(
													final byte[] fileBytes,
													ParseException arg1) {
												// TODO Auto-generated method
												// stub
												if (arg1 == null) {
													new Thread() {
														@Override
														public void run() {
															try {
																final SimpleDateFormat sdFormat2 = new SimpleDateFormat(
																		"yyyy_MM_dd_HH_mm_ss_SSS");
																final String filePath = tempFile
																		+ sdFormat2
																				.format(new Date())
																		+ ".mp3";
																BufferedOutputStream bos = new BufferedOutputStream(
																		new FileOutputStream(
																				new File(
																						filePath)));
																bos.write(fileBytes);
																bos.flush();
																bos.close();

																getActivity()
																		.runOnUiThread(
																				new Runnable() {

																					@Override
																					public void run() {
																						// TODO
																						// Auto-generated
																						// method
																						// stub
																						voiceList
																								.add(0,
																										filePath);
																						listAdapter
																								.notifyDataSetChanged();
																					}
																				});
															} catch (Exception e2) {
																e2.printStackTrace();
															}
														}
													}.start();
												} else {
													Log.i(TAG,
															"Get parse file error!");
												}

											}
										});
								detail += String.valueOf(sdFormat
										.format(parseObject.getCreatedAt()));

							}
						} else {
							Log.d("score", "Error: " + e.getMessage());
						}
					}
				});
			}
		});*/

	}

	// listen to events

	public void onStickyEvent(DisplayEvent event) {

	}

	public void onEvent(DisplayEvent event) {

	}


	@Override
	public void onResume() {
		super.onResume();
		try {
			EventBus.getDefault().register(this);
			EventBus.getDefault().post(new DisplayEvent("導覽員，聲音部分初始化"));
			/*EventBus.getDefault().postSticky(
					new DisplayEvent("CommSettings.getMulticastAddr!"+CommSettings.getMulticastAddr()));*/
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
