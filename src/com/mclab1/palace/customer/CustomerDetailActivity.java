package com.mclab1.palace.customer;

import java.io.BufferedOutputStream;

import android.graphics.Color;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.mclab1.nccu_story.R;
import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mclab1.palaca.parsehelper.VoiceObject;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
//import com.squareup.picasso.Picasso;

public class CustomerDetailActivity extends Activity {
	public static final String TAG=" CustomerDetailActivity";
	String msg = "Android : ";
	String mTitle = "";
	String mContent = "";
	String mPhotoPath = "";
	final String tempFile = Environment.getExternalStorageDirectory() + "/";
	private MediaPlayer mpintro;
	private ListView listView;
	private CustomerVoiceListAdapter customerVoiceListAdapter;
	private ArrayList<VoiceDataElement> mp3paths = new ArrayList<VoiceDataElement>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.customer_activity_detail);
		//final RelativeLayout background = (RelativeLayout)findViewById(R.id.back);
		Log.d(msg, "The onCreate() event");
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mTitle = extras.getString("title");
			mContent = extras.getString("content");
			mPhotoPath = extras.getString("photo");
			initViews();
		}
	}

	private void initViews() {
		ImageView imageView = (ImageView) findViewById(R.id.customer_activity_image);
		// TextView title = (TextView)
		// findViewById(R.id.customer_activity_title);
		TextView content = (TextView) findViewById(R.id.customer_activity_content);
		getActionBar().setTitle(mTitle);
//		Picasso.with(this).load(mPhotoPath).into(imageView);
		// title.setText(mTitle);
		content.setText(mContent);
		listView = (ListView) findViewById(R.id.customer_mp3_listview);
		customerVoiceListAdapter = new CustomerVoiceListAdapter(this, mp3paths);
		listView.setAdapter(customerVoiceListAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				try {
					String mp3Path=mp3paths.get(pos).mp3path;
					Log.i(TAG, mp3Path);
					mpintro = MediaPlayer.create(getApplicationContext(), Uri.parse(mp3Path));
					mpintro.start();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				

			}
		});
		loaddata();

	}

	/** Called when the activity is about to become visible. */
	@Override
	protected void onStart() {
		super.onStart();
		Log.d(msg, "The onStart() event");
	}

	/** Called when the activity has become visible. */
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(msg, "The onResume() event");
	}

	/** Called when another activity is taking focus. */
	@Override
	protected void onPause() {
		super.onPause();
		Log.d(msg, "The onPause() event");
	}

	/** Called when the activity is no longer visible. */
	@Override
	protected void onStop() {
		super.onStop();
		Log.d(msg, "The onStop() event");
	}

	/** Called just before the activity is destroyed. */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(msg, "The onDestroy() event");
	}

	private void loaddata() {
		mp3paths.clear();
		ParseQuery<ParseObject> query = ParseQuery
				.getQuery(VoiceObject.table_name);
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> voiceParseObjectList,
					ParseException e) {
				if (e == null) {
					// Log.i(TAG, "Get parse objects success!");
					for (int i = 0; i != voiceParseObjectList.size(); i++) {
						String detail = "";
						final ParseObject parseObject = voiceParseObjectList.get(i);
						final SimpleDateFormat sdFormat = new SimpleDateFormat(
								"yyyy/MM/dd HH:mm");
						ParseFile parseFile = (ParseFile) parseObject
								.get(VoiceObject.column_audio_file);
						parseFile.getDataInBackground(new GetDataCallback() {

							@Override
							public void done(final byte[] fileBytes,
									ParseException arg1) {
								// TODO Auto-generated method
								// stub
								if (arg1 == null) {
									new Thread() {
										@Override
										public void run() {
											try {
												final SimpleDateFormat sdFormat2 = new SimpleDateFormat(
														"yyyy_MM_dd");
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
												
												final SimpleDateFormat sdFormat3 = new SimpleDateFormat(
														"yyyy/MM/dd");

												runOnUiThread(new Runnable() {

													@Override
													public void run() {
														// TODO
														// Auto-generated
														// method
														// stub
														Log.i(TAG, "completed download mp3"+filePath);
														VoiceDataElement voiceDataElement=new VoiceDataElement();
														voiceDataElement.mp3path=filePath;
														voiceDataElement.createdTime=sdFormat3.format(parseObject.getCreatedAt());
														mp3paths.add(voiceDataElement);
														//mp3paths.add(filePath);
														customerVoiceListAdapter.notifyDataSetChanged();
													}
												});
											} catch (Exception e2) {
												e2.printStackTrace();
											}
										}
									}.start();
								} else {
									// Log.i(TAG, "Get parse file error!");
								}

							}
						});
						detail += String.valueOf(sdFormat.format(parseObject
								.getCreatedAt()));

					}
				} else {
					Log.d("score", "Error: " + e.getMessage());
				}
			}
		});

	}
}
