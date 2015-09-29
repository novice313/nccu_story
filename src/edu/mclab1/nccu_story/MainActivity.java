package edu.mclab1.nccu_story;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import mclab1.pages.MediaPlayerFragment;
import mclab1.pages.NewsFragment;
import mclab1.sugar.Owner;
import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.orm.SugarRecord;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	private final static String tag = "MainActivityTAG";

	// tab
	private ViewPager viewPager;
	private ActionBar actionBar;
	private TabsPagerAdapter mAdapter;
	public static String[] tabs = { "News", "Googlemap"/*, "Mediaplayer", "Owner" */};
	public static int tabsize = 0;

	// Result codes
	private static final int REQUEST_CODE_RECORD = 1539;

	// facebook
	CallbackManager callbackManager;
	public static AccessToken accessToken;
	private final static String PASSWORD = "TtsaiLabMcla1";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// test
		Parse.initialize(this, "wtSFcggR896xMJQUGblYuphkF6EVw4ChcLcpSowP",
				"IwJ3gTRBe8cARlxMf3xh97eai2a7MNLP68vdL3IY");
		Log.d(tag, "WARNING");

		// WifiManager mWifiManager = (WifiManager)
		// getSystemService(Context.WIFI_SERVICE);
		//
		// if(!mWifiManager.isWifiEnabled()){
		// mWifiManager.setWifiEnabled(true);
		// Toast.makeText(MainActivity.this, "Wi-Fi開啟中....",
		// Toast.LENGTH_LONG).show();
		// }

		ParseObject testObject = new ParseObject("TestObject");
		testObject.put("foo", "bar");
		testObject.saveInBackground();

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
										String id = object.optString("id");
										String name = object.optString("name");
										String gender = object
												.optString("gender");
										String locale = object
												.optString("locale");
										String link = object.optString("link");
										Log.d("FB", "id= " + id);
										Log.d("FB", "name= " + name);
										Log.d("FB", "gender= " + gender);
										Log.d("FB", "locale= " + locale);
										Log.d("FB", "link= " + link);

										List<Owner> owner = SugarRecord
												.listAll(Owner.class);
										if (!owner.isEmpty()) {
											SugarRecord.deleteAll(Owner.class);
										}
										Log.d(tag, "Verify owner." + " owner= "
												+ owner.size());
										Owner newOwner = new Owner(id, name,
												gender, locale, link);
										// newOwner.setId((long) 1);
										newOwner.save();
										List<Owner> NewOwner = SugarRecord
												.listAll(Owner.class);
										Log.d(tag, "owner= " + NewOwner.size());

										// save to parse
										 new ParseSaveUserHelper(id, name,
										 gender, locale, link, PASSWORD).execute();

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

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// hockey app
		checkForCrashes();
		checkForUpdates();
	}

	private void checkForCrashes() {
		CrashManager.register(this, "ea6bdfe747bdf04686a0354461adc757");
	}

	private void checkForUpdates() {
		// Remove this for store builds!
		UpdateManager.register(this, "ea6bdfe747bdf04686a0354461adc757");
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
		// if (MediaPlayerFragment.playIntent == null) {
		// MediaPlayerFragment.playIntent = new Intent(this,
		// MusicService.class);
		// bindService(MediaPlayerFragment.playIntent,
		// MainActivity.musicConnection, Context.BIND_AUTO_CREATE);
		// startService(MediaPlayerFragment.playIntent);
		// }
	}

	// // mediaplayer
	// // connect to the service
	// public static ServiceConnection musicConnection = new ServiceConnection()
	// {
	//
	// @Override
	// public void onServiceConnected(ComponentName name, IBinder service) {
	// MusicBinder binder = (MusicBinder) service;
	// // get service
	// MediaPlayerFragment.musicSrv = binder.getService();
	//
	// // if(musicSrv!=null){
	// Log.d(tag, MediaPlayerFragment.musicSrv.toString());
	// // }
	// // pass list
	// MediaPlayerFragment.musicSrv.setList(MediaPlayerFragment.songList);
	// MediaPlayerFragment.musicBound = true;
	// }
	//
	// @Override
	// public void onServiceDisconnected(ComponentName name) {
	// MediaPlayerFragment.musicBound = false;
	// }
	// };

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		menu.findItem(R.id.action_recorder).setVisible(false);
		menu.findItem(R.id.action_test).setVisible(false);
		menu.findItem(R.id.action_refresh).setVisible(false);
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

			// mediaplayer stop
			MediaPlayerFragment.musicSrv.pausePlayer();

			// Intent intent = new Intent();
			// intent.setClass(MainActivity.this, FileexplorerActivity.class);
			// startActivity(intent);
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

class ParseSaveUserHelper extends AsyncTask<Void, Void, Void> {

	String id, name, gender, locale, link, password;

	public ParseSaveUserHelper(String id, String name, String gender,
			String locale, String link, String password) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.name = name;
		this.gender = gender;
		this.locale = locale;
		this.link = link;
		this.password = password;
	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.whereEqualTo("userUuid", id);
		query.findInBackground(new FindCallback<ParseUser>() {

			@Override
			public void done(List<ParseUser> objects, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					if (objects.isEmpty()) {
						ParseUser parseUser = new ParseUser();
						parseUser.put("userUuid", id);
						parseUser.setUsername(name);
						parseUser.put("gender", gender);
						parseUser.put("locale", locale);
						parseUser.setPassword(password);
						parseUser.put("link", link);
						try {
							parseUser.signUp();
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {
						ParseUser parseUser = objects.get(0);
						parseUser.put("userUuid", id);
						parseUser.setUsername(name);
						parseUser.put("gender", gender);
						parseUser.put("locale", locale);
						parseUser.put("link", link);
						try {
							parseUser.save();
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				} else {
					e.printStackTrace();
				}
			}
		});

		return null;
	}

}