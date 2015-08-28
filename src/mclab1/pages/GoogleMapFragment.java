package mclab1.pages;

import java.util.ArrayList;
import java.util.List;

import ro.ui.pttdroid.Client_Main;
import ro.ui.pttdroid.Globalvariable;

import mclab1.custom.listview.News;
import mclab1.sugar.Owner;

import com.farproc.wifi.connecter.TestWifiScan;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.plus.model.people.Person.ObjectType;
import com.mclab1.palace.customer.CustomerDetailActivity;
import com.orm.SugarRecord;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import edu.mclab1.nccu_story.R;

public class GoogleMapFragment extends Fragment
/* implements OnMapReadyCallback */implements OnMapReadyCallback {

	public ArrayList<News> storyList;
	private final static String tag = "GoogleMapFragment";
	private static final String MAP_FRAGMENT_TAG = "map";
	final LatLng NCCU = new LatLng(24.986233, 121.575843);
	int initial_zoom_size = 16;
	SupportMapFragment mapFragment;

	String[] list_uploadType = { "Broadcast", "Upload story" };

	private final int TYPE_STORY = 1;
	private final int TYPE_OFFLINE_STORY = 2;
	private final int TYPE_ONLINE_BROADCAST = 3;

	GoogleMap map;
	final int PARSE_LIMIT = 50;

	private WifiManager wiFiManager;
	int if_Global_local = -1;
	List<android.net.wifi.ScanResult> mWifiScanResultList;
	String SSID;
	Boolean if_find_wificonnect = false;
	ArrayList<String> SSIDList = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.googlemap);
		Log.d(tag, "oncreated.");

		// instantiate list
		storyList = new ArrayList<News>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_googlemap, container,
				false);
		Log.d(tag, "onCreateView");

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(tag, "onActivityCreated");

		MapsInitializer.initialize(getActivity());
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

			}
		});
		switch (GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getActivity())) {
		case ConnectionResult.SUCCESS:
			Toast.makeText(getActivity(), "SUCCESS", Toast.LENGTH_SHORT).show();

			// It isn't possible to set a fragment's id programmatically so we
			// set a tag instead and
			// search for it using that.
			mapFragment = (SupportMapFragment) getChildFragmentManager()
					.findFragmentByTag(MAP_FRAGMENT_TAG);

			// We only create a fragment if it doesn't already exist.
			if (mapFragment == null) {
				Log.d(tag, "mapFragment==null");
				// To programmatically add the map, we first create a
				// SupportMapFragment.
				mapFragment = SupportMapFragment.newInstance();

				// Then we add it using a FragmentTransaction.
				FragmentTransaction fragmentTransaction = getChildFragmentManager()
						.beginTransaction();
				fragmentTransaction
						.add(R.id.map, mapFragment, MAP_FRAGMENT_TAG);
				fragmentTransaction.commit();
				mapFragment.getMapAsync(this);
			}
			break;
		case ConnectionResult.SERVICE_MISSING:
			Toast.makeText(getActivity(), "SERVICE MISSING", Toast.LENGTH_SHORT)
					.show();
			break;
		case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
			Toast.makeText(getActivity(), "UPDATE REQUIRED", Toast.LENGTH_SHORT)
					.show();
			break;
		default:
			Toast.makeText(
					getActivity(),
					GooglePlayServicesUtil
							.isGooglePlayServicesAvailable(getActivity()),
					Toast.LENGTH_SHORT).show();
		}// END switch

	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(tag, "onStart");

		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				query_Story();
			}
		});

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				query_offlineStory();
			}
		});

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				query_onlineStory();
			}
		});
	}

	@Override
	public void onMapReady(GoogleMap map) {
		Log.d(tag, "onMapReady.");
		this.map = map;
		// nccu: 24°58'46"N 121°34'15"E
		// map.addMarker(new MarkerOptions().position(new LatLng(24.5846,
		// 121.3415)).title("Marker"));
		map.addMarker(new MarkerOptions()
				.position(NCCU)
				.title("NCCU")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(NCCU,
				initial_zoom_size));

		map.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {
				// TODO Auto-generated method stub
				Log.d(tag, "point latitude=" + point.latitude);
				Log.d(tag, "point longituide=" + point.longitude);
			}
		});
		map.setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng point) {
				// check login or not
				List<Owner> owner = SugarRecord.listAll(Owner.class);
				if (owner.isEmpty()) {
					Toast.makeText(getActivity(),
							"Sorry! You have to log in first.",
							Toast.LENGTH_SHORT).show();
				} else {
					// stop music
					if (MediaPlayerFragment.musicSrv != null) {
						MediaPlayerFragment.musicSrv.pausePlayer();
					}
					// longclick upload
					ShowAlertDialogAndList(point);
				}
			}
		});

		// set googlaMap infoWindow
		setGoogleMapInfoWindow(map);
	}

	private void ShowAlertDialogAndList(final LatLng point) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Upload");
		// 建立選擇的事件
		DialogInterface.OnClickListener ListClick = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:// broadcast
					Log.d(tag, "list_uploadType " + list_uploadType[which]
							+ " onclick");
					Intent intent_broadcast = new Intent();
					intent_broadcast
							.setClass(getActivity(), TestWifiScan.class);
					Bundle bundle_broadcast = new Bundle();
					bundle_broadcast.putDouble("longitude", point.longitude);
					bundle_broadcast.putDouble("latitude", point.latitude);
					// 將Bundle物件assign給intent
					intent_broadcast.putExtras(bundle_broadcast);

					// 切換Activity
					startActivity(intent_broadcast);
					break;
				case 1:// upload story
					Log.d(tag, "list_uploadType " + list_uploadType[which]
							+ " onclick");
					Intent intent_uploadStory = new Intent();
					intent_uploadStory
							.setClass(getActivity(), UploadPage.class);
					Bundle bundle_uploadStory = new Bundle();
					bundle_uploadStory.putDouble("longitude", point.longitude);
					bundle_uploadStory.putDouble("latitude", point.latitude);
					// 將Bundle物件assign給intent
					intent_uploadStory.putExtras(bundle_uploadStory);

					// 切換Activity
					startActivity(intent_uploadStory);
				}

			}
		};
		// 建立按下取消什麼事情都不做的事件
		DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		};
		builder.setItems(list_uploadType, ListClick);
		builder.setNeutralButton("cancel", OkClick);
		builder.show();

	}

	private void addMarker_Story(String objectIdString, String userNameString,
			String title, LatLng point, int score, int type) {
		// TODO Auto-generated method stub
		String snippet = objectIdString + "," + userNameString + "," + score
				+ "," + type;
		this.map.addMarker(new MarkerOptions()
				.position(point)
				.snippet(snippet)
				.title(title)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
	}

	private void addMarker_Broadcast(String objectIdString,
			String userNameString, String title, LatLng point, int score,
			int type, String SSIDString) {

		String snippet = objectIdString + "," + userNameString + "," + score
				+ "," + type + "," + SSIDString;
		this.map.addMarker(new MarkerOptions()
				.position(point)
				.snippet(snippet)
				.title(title)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
	}

	private void query_Story() {
		ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>(
				"story");
		parseQuery.setLimit(PARSE_LIMIT);
		parseQuery.addDescendingOrder("createdAt");
		parseQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					if (!objects.isEmpty()) {
						for (int i = 0; i < objects.size(); i++) {
							ParseObject parseObject = objects.get(i);
							final String objectIdString = parseObject
									.getObjectId();
							final String userNameString = parseObject
									.getString("userName");
							final String userUuidString = parseObject
									.getString("userUuid");
							final String titleString = parseObject
									.getString("title");
							final int score = parseObject.getInt("score");
							final String contentString = parseObject
									.getString("content");

							final double latitude = parseObject
									.getDouble("latitude");
							final double longitude = parseObject
									.getDouble("longitude");

							ParseFile imageFile = (ParseFile) parseObject
									.get("image");
							if (imageFile != null) {
								imageFile
										.getDataInBackground(new GetDataCallback() {

											@Override
											public void done(byte[] data,
													ParseException e) {
												if (e == null) {
													// Log.d(tag,
													// "parseFile done");
													BitmapFactory.Options opt = null;
													opt = new BitmapFactory.Options();
													opt.inSampleSize = 8;
													Bitmap bmp = BitmapFactory
															.decodeByteArray(
																	data,
																	0,
																	data.length,
																	opt);
													storyList
															.add(new News(
																	objectIdString,
																	userNameString,
																	userUuidString,
																	titleString,
																	score,
																	bmp,
																	contentString,
																	latitude,
																	longitude));

													if (bmp != null) {
														bmp.recycle();
													}

													LatLng point = new LatLng(
															latitude, longitude);
													addMarker_Story(
															objectIdString,
															userNameString,
															titleString, point,
															score, TYPE_STORY);

												}
											}
										});
							}
						}
					}
				}
			}
		});
	}

	private void query_offlineStory() {
		// TODO Auto-generated method stub
		ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>(
				"offline");
		parseQuery.whereEqualTo("State", "offline");
		parseQuery.setLimit(PARSE_LIMIT);
		parseQuery.addDescendingOrder("createdAt");
		parseQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					if (!objects.isEmpty()) {
						for (int i = 0; i < objects.size(); i++) {
							ParseObject parseObject = objects.get(i);
							final String objectIdString = parseObject
									.getObjectId();
							final String userNameString = parseObject
									.getString("userName");
							final String userUuidString = parseObject
									.getString("userUuid");
							final String titleString = parseObject
									.getString("title");
							final int score = parseObject.getInt("score");
							final String contentString = parseObject
									.getString("content");

							final double latitude = parseObject
									.getDouble("latitude");
							final double longitude = parseObject
									.getDouble("longitude");

							ParseFile imageFile = (ParseFile) parseObject
									.get("image");
							if (imageFile != null) {
								Log.d(tag, "parseObjectId = " + objectIdString);
								imageFile
										.getDataInBackground(new GetDataCallback() {

											@Override
											public void done(byte[] data,
													ParseException e) {
												if (e == null) {
													// Log.d(tag,
													// "parseFile done");

													BitmapFactory.Options opt = null;
													opt = new BitmapFactory.Options();
													opt.inSampleSize = 8;
													Bitmap bmp = BitmapFactory
															.decodeByteArray(
																	data,
																	0,
																	data.length,
																	opt);
													storyList
															.add(new News(
																	objectIdString,
																	userNameString,
																	userUuidString,
																	titleString,
																	score,
																	bmp,
																	contentString,
																	latitude,
																	longitude));

													if (bmp != null) {
														bmp.recycle();
													}

													LatLng point = new LatLng(
															latitude, longitude);
													addMarker_Story(
															objectIdString,
															userNameString,
															titleString, point,
															score,
															TYPE_OFFLINE_STORY);

												}
											}
										});
							}
						}
					}
				}
			}
		});
	}

	private void query_onlineStory() {
		// TODO Auto-generated method stub
		ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>(
				"offline");
		parseQuery.whereEqualTo("State", "online");
		parseQuery.setLimit(PARSE_LIMIT);
		parseQuery.addDescendingOrder("createdAt");
		parseQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					if (!objects.isEmpty()) {
						for (int i = 0; i < objects.size(); i++) {
							ParseObject parseObject = objects.get(i);
							final String objectIdString = parseObject
									.getObjectId();
							final String userNameString = parseObject
									.getString("userName");
							final String userUuidString = parseObject
									.getString("userUuid");
							final String titleString = parseObject
									.getString("title");
							final int score = parseObject.getInt("score");
							final String contentString = parseObject
									.getString("content");
							final String SSIDString = parseObject
									.getString("SSID");

							final double latitude = parseObject
									.getDouble("latitude");
							final double longitude = parseObject
									.getDouble("longitude");

							ParseFile imageFile = (ParseFile) parseObject
									.get("image");
							if (imageFile != null) {
								Log.d(tag, "parseObjectId = " + objectIdString);
								imageFile
										.getDataInBackground(new GetDataCallback() {

											@Override
											public void done(byte[] data,
													ParseException e) {
												if (e == null) {
													// Log.d(tag,
													// "parseFile done");
													BitmapFactory.Options opt = null;
													opt = new BitmapFactory.Options();
													opt.inSampleSize = 8;
													Bitmap bmp = BitmapFactory
															.decodeByteArray(
																	data,
																	0,
																	data.length,
																	opt);
													storyList
															.add(new News(
																	objectIdString,
																	userNameString,
																	userUuidString,
																	titleString,
																	score,
																	bmp,
																	contentString,
																	latitude,
																	longitude));

													if (bmp != null) {
														bmp.recycle();
													}

													LatLng point = new LatLng(
															latitude, longitude);
													addMarker_Broadcast(
															objectIdString,
															userNameString,
															titleString,
															point,
															score,
															TYPE_ONLINE_BROADCAST,
															SSIDString);

												}
											}
										});
							}
						}
					}
				}
			}
		});
	}

	// custom marker reference:
	// http://wptrafficanalyzer.in/blog/customizing-infowindow-contents-in-google-map-android-api-v2-using-infowindowadapter/
	private void setGoogleMapInfoWindow(GoogleMap googleMap) {
		// TODO Auto-generated method stub
		// Setting a custom info window adapter for the google map
		googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {

			// Use default InfoWindow frame
			@Override
			public View getInfoWindow(Marker arg0) {
				return null;
			}

			// Defines the contents of the InfoWindow
			@Override
			public View getInfoContents(Marker marker) {

				// Getting view from the layout file info_window_layout
				View v = getActivity().getLayoutInflater().inflate(
						R.layout.info_window_layout, null);

				// Getting the position from the marker
				LatLng latLng = marker.getPosition();

				TextView userNameTextView = (TextView) v
						.findViewById(R.id.userName);
				TextView titleTextView = (TextView) v.findViewById(R.id.title);
				TextView scoreTextView = (TextView) v.findViewById(R.id.score);

				String snippet = marker.getSnippet();
				String[] temp = snippet.split(",");
				String objectId = temp[0];
				String userName = temp[1];
				String score = temp[2];
				int type = Integer.parseInt(temp[3]);

				// Setting TextView
				userNameTextView.setText(userName);
				titleTextView.setText(marker.getTitle());
				scoreTextView.setText(score);

				// Returning the view containing InfoWindow contents
				return v;

			}
		});

		googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker) {
				// TODO Auto-generated method stub
				Log.d(tag, "onInfoWindowClick: " + marker.getTitle());

				if (MediaPlayerFragment.musicSrv != null) {
					MediaPlayerFragment.musicSrv.pausePlayer();
				}

				String snippet = marker.getSnippet();
				final String[] temp = snippet.split(",");
				final String objectId = temp[0];
				LatLng point = marker.getPosition();
				final double latitude = point.latitude;
				final double longitude = point.longitude;
				int type = Integer.parseInt(temp[3]);
				switch (type) {
				case TYPE_STORY:
					Log.d(tag, "Icon TYPE_STORY onclick.");
					Intent intent_detail = new Intent();
					intent_detail.putExtra("objectId", objectId);
					intent_detail.setClass(getActivity(), DetailPage.class);
					startActivity(intent_detail);
					break;
				case TYPE_OFFLINE_STORY:

					Log.d(tag, "Icon TYPE_OFFLINE_STORY onclick.");
					ParseQuery<ParseObject> query = ParseQuery
							.getQuery("offline");
					// Retrieve the object by id
					query.getInBackground(objectId,
							new GetCallback<ParseObject>() { // 以後博要給我object ID
								@Override
								public void done(ParseObject offline,
										ParseException e) {
									if (e == null) {
										Globalvariable.titleString = (String) offline
												.get("title");
										Globalvariable.contentString = (String) offline
												.get("content");
										Globalvariable.latitude = latitude;
										Globalvariable.longitude = longitude;
										System.out.println("Globalvariable"
												+ Globalvariable.titleString);
										System.out.println("Globalvariable"
												+ Globalvariable.contentString);
										Intent intent = new Intent(
												getActivity(),
												CustomerDetailActivity.class);
										getActivity().startActivity(intent);

									}
								}
							});

					break;
				case TYPE_ONLINE_BROADCAST:

					Log.d(tag, "Icon TYPE_ONLINE_BROADCAST onclick.");

					// check is still online or not
					ParseQuery<ParseObject> checkQuery = new ParseQuery<ParseObject>(
							"offline");
					checkQuery.whereEqualTo("objectId", objectId);
					checkQuery
							.findInBackground(new FindCallback<ParseObject>() {

								@Override
								public void done(List<ParseObject> objects,
										ParseException e) {
									// TODO Auto-generated method stub
									if (e == null) {
										ParseObject parseObject = objects
												.get(0);

										if (parseObject.getString("State")
												.compareTo("online") == 0) {// still
																			// online
											String SSIDstring = temp[4];
											// parse broadcast
											// 若wifi狀態為關閉則將它開啟
											wiFiManager = (WifiManager) getActivity()
													.getSystemService(
															Context.WIFI_SERVICE);
											if (!wiFiManager.isWifiEnabled()) {
												wiFiManager
														.setWifiEnabled(true);
											}
											wiFiManager = (WifiManager) getActivity()
													.getSystemService(
															Context.WIFI_SERVICE);
											System.out.println("wiFiManagergetConnectionInfo"
													+ wiFiManager
															.getConnectionInfo()
													+ "$"
													+ wiFiManager
															.getWifiState()
													+ " ");

											if (!wiFiManager.isWifiEnabled()) { // 判斷是否有網路
												Toast.makeText(getActivity(),
														"要開啟網路(Wifi/3G)!",
														Toast.LENGTH_SHORT)
														.show();

											} else {

												// 重新掃描Wi-Fi資訊
												wiFiManager.startScan();
												// 偵測周圍的Wi-Fi環境(因為會有很多組Wi-Fi，所以型態為List)
												mWifiScanResultList = wiFiManager
														.getScanResults();
												for (int i = 0; i < mWifiScanResultList
														.size(); i++) {
													// 手機目前周圍的Wi-Fi環境
													SSID = mWifiScanResultList
															.get(i).SSID;
													SSIDList.add(SSID);

												}

												String networkSSID = SSIDstring; // 以後柏要傳進來的變數
																					// WIRELESS
																					// NCCU_Tsai
																					// TOTOLINK
																					// A2004NS
																					// 2.4G"
																					// NCCU_Wang
																					// WIRELESS
												networkSSID = networkSSID
														.substring(
																1,
																networkSSID
																		.length() - 1);
												System.out.println("GOGOGO"
														+ networkSSID + " "
														+ networkSSID.length()); // network
																					// module
																					// connect
												String networkPass = "";
												WifiConfiguration conf = new WifiConfiguration();
												conf.SSID = "\"" + networkSSID
														+ "\""; // Please note
																// the
																// quotes.
																// String should
																// contain ssid
																// in
																// quotes
												conf.wepKeys[0] = "\""
														+ networkPass + "\"";
												conf.wepTxKeyIndex = 0;
												conf.allowedKeyManagement
														.set(WifiConfiguration.KeyMgmt.NONE);
												System.out.println("GOGOGO2");
												conf.allowedGroupCiphers
														.set(WifiConfiguration.GroupCipher.WEP40);
												conf.allowedKeyManagement
														.set(WifiConfiguration.KeyMgmt.NONE);
												WifiManager wifiManager2 = (WifiManager) getActivity()
														.getSystemService(
																Context.WIFI_SERVICE);
												wifiManager2.addNetwork(conf);

												List<WifiConfiguration> list = wifiManager2
														.getConfiguredNetworks();
												if_Global_local = 0;
												for (WifiConfiguration wificonfig : list) { // 解決ap遇到不在現場就會無法連線的問題
													if (if_find_wificonnect == true) {
														System.out
																.println("Main_configwifi");
														if_find_wificonnect = false;
														break;

													}
													for (int i = 0; i < SSIDList
															.size(); i++) {
														System.out.println("Main_configwifi"
																+ " "
																+ SSIDList
																		.get(i));

														if (SSIDList.get(i) != null
																&& SSIDList
																		.get(i)
																		.equals(networkSSID)
																&& wificonfig.SSID
																		.equals("\""
																				+ networkSSID
																				+ "\"")) { // 核心做連線的部分
															System.out.println("Main_configwifi2"
																	+ SSIDList
																			.get(i)
																	+ " "
																	+ networkSSID);

															wifiManager2
																	.disconnect();
															wifiManager2
																	.enableNetwork(
																			wificonfig.networkId,
																			true);
															wifiManager2
																	.reconnect();
															if_find_wificonnect = true;
															if_Global_local = 1;
															break;
														}
													}

												}
												System.out.println("GOGOGO4"
														+ if_Global_local);
												Intent intent = new Intent(
														getActivity(),
														Client_Main.class); // 改寫成TestWifiScan.this
												intent.putExtra(
														"if_Global_local",
														if_Global_local);// 可放所有基本類別
												startActivity(intent);
											}
										} else {// no longer exists
											Log.d(tag,
													"Broadcast is over change to offline.");
											Toast.makeText(getActivity(),
													"Broadcast is offline",
													Toast.LENGTH_SHORT).show();

											ParseQuery<ParseObject> query = ParseQuery
													.getQuery("offline");
											// Retrieve the object by id
											query.getInBackground(
													objectId,
													new GetCallback<ParseObject>() { // 以後博要給我object
														// ID
														@Override
														public void done(
																ParseObject offline,
																ParseException e) {
															if (e == null) {
																Globalvariable.titleString = (String) offline
																		.get("title");
																Globalvariable.contentString = (String) offline
																		.get("content");
																Globalvariable.latitude = latitude;
																Globalvariable.longitude = longitude;
																System.out
																		.println("Globalvariable"
																				+ Globalvariable.titleString);
																System.out
																		.println("Globalvariable"
																				+ Globalvariable.contentString);
																Intent intent = new Intent(
																		getActivity(),
																		CustomerDetailActivity.class);
																getActivity()
																		.startActivity(
																				intent);

															}
														}
													});
										}
									}

								}
							});

					break;

				default:
					Log.d(tag, "Icon NO_TYPE onclick.");
					break;
				}
			}
		});
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(tag, "onStop.");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d(tag, "onDestroyView.");
		mapFragment.onDestroyView();
		map.clear();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(tag, "onDestroy.");
	}

}