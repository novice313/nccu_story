package mclab1.pages;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import mclab1.custom.listview.GoogleMapSearch;
import mclab1.custom.listview.GoogleMapSearchAdapter;
import mclab1.custom.listview.News;
import mclab1.custom.listview.NewsAdapter;
import mclab1.sugar.GoogleMapData;
import mclab1.sugar.Owner;
import ro.ui.pttdroid.Client_Main;
import ro.ui.pttdroid.Globalvariable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomButton;

import com.farproc.wifi.connecter.TestWifiScan;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mclab1.palace.customer.CustomerDetailActivity;
import com.orm.SugarRecord;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.wunderlist.slidinglayer.LayerTransformer;
import com.wunderlist.slidinglayer.SlidingLayer;
import com.wunderlist.slidinglayer.transformer.RotationTransformer;

import edu.mclab1.nccu_story.R;
import mclab1.service.googlemap.GoogleMapHelper;
import mclab1.service.googlemap.GoogleMapParseHelper;
import mclab1.service.googlemap.GoogleMapQueryParseHelper;

public class GoogleMapFragment extends Fragment
/* implements OnMapReadyCallback */implements OnMapReadyCallback {

	public static ArrayList<News> storyList;
	List<Marker> markerList = new ArrayList<Marker>();
	private final static String tag = "GoogleMapFragment";
	private static final String MAP_FRAGMENT_TAG = "map";
	final LatLng NCCU = new LatLng(24.986233, 121.575843);
	int initial_zoom_size = 16;
	SupportMapFragment mapFragment;

	String[] list_uploadType = { "Broadcast", "Upload story" };
	String[] list_uploadType_client = { "Upload story" }; // 修改給client

	public static final int TYPE_STORY = 1;
	public static final int TYPE_OFFLINE_STORY = 2;
	public static final int TYPE_ONLINE_BROADCAST = 3;
	public static final int TYPE_READY = 4;
	public static String[] TYPE = { "", "story", "offline", "online", "Ready" };

	public static final int TYPE_NCCU = 98;
	public static final int TYPE_USER = 99;

	GoogleMap map;
	public static final int PARSE_LIMIT = 10;
	private static final int WAIT_FOR_QUERY_TIME = 2;//sec

	/** GPS */
	private LocationManager locationMgr;
	private String provider;
	private Marker markerMe;
	MenuItem locateMe, openSearch;
	private LatLng current_position;
	private double current_zoom;

	// slide layer
	private SlidingLayer mSlidingLayer;
	private SearchView searchView;
	private ProgressDialog dialog;
	public static ArrayList<GoogleMapSearch> searchList;
	public ListView searchListView;
	public static GoogleMapSearchAdapter googleMapSearchAdt;
	private Button buttonClose;

	private WifiManager wiFiManager;
	int if_Global_local = -1;
	List<android.net.wifi.ScanResult> mWifiScanResultList;
	String SSID;
	Boolean if_find_wificonnect = false;
	ArrayList<String> SSIDList = new ArrayList<String>();

	Activity mActivity;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		mActivity = activity;
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.googlemap);
		Log.d(tag, "oncreated.");

		// instantiate list
		storyList = new ArrayList<News>();
		setHasOptionsMenu(true);

		// hide key board
		mActivity.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		locateMe = menu.add("locateMe");
		locateMe.setIcon(R.drawable.ic_action_location_found).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM);
		locateMe.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				Log.d(tag, "item " + item.getItemId() + " onclick");

				// start locate user
				if (initLocationProvider()) {
					Log.d(tag, "start whereAmI");
					whereAmI();
				} else {
					Toast.makeText(mActivity, "請開啟定位！", Toast.LENGTH_SHORT)
							.show();
				}

				return false;
			}
		});

		openSearch = menu.add("openSearch");
		openSearch.setIcon(R.drawable.ic_action_search).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM);
		openSearch.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				Log.d(tag, "item " + item.getItemId() + " onclick");

				mSlidingLayer.openLayer(true);

				return false;
			}
		});

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_googlemap, container,
				false);
		Log.d(tag, "onCreateView");

		// slide layer
		mSlidingLayer = (SlidingLayer) view.findViewById(R.id.slidingLayer1);
		searchView = (SearchView) view.findViewById(R.id.searchView);
		searchListView = (ListView) view.findViewById(R.id.search_list);
		buttonClose = (Button) view.findViewById(R.id.buttonClose);

		bindViews();
		initState();

		return view;
	}

	private void bindViews() {

		searchView.setIconifiedByDefault(false);
		// 为该SearchView组件设置事件监听器
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				Log.d(query, "string = " + query);

				// clear list
				searchList.clear();
				googleMapSearchAdt.notifyDataSetChanged();

				// Check if no view has focus:
				// View view = mActivity.getCurrentFocus();
				// if (view != null) {
				// Log.d(tag, "view != null");
				// InputMethodManager imm = (InputMethodManager) mActivity
				// .getSystemService(Context.INPUT_METHOD_SERVICE);
				// imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				// }

				searchView.clearFocus();

				new GoogleMapParseHelper(mActivity, query).execute();
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		// 设置该SearchView显示搜索按钮
		searchView.setSubmitButtonEnabled(true);

		// 设置该SearchView内默认显示的提示文本
		Resources res = getResources();
		searchView.setQueryHint(res.getString(R.string.type_in_hint));
		// END searchView

		// ListView
		// create and set adapter
		searchList = new ArrayList<GoogleMapSearch>();

		googleMapSearchAdt = new GoogleMapSearchAdapter(
				mActivity.getApplicationContext(), searchList);
		searchListView.setAdapter(googleMapSearchAdt);
		searchListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				// TODO Auto-generated method stub
				Log.d(tag, "item " + searchList.get(pos).getTitle()
						+ " onclick");

				// close the slide layer
				mSlidingLayer.closeLayer(true);

				// move google map camera to the result
				GoogleMapSearch googleMapSearch = searchList.get(pos);
				cameraFocusOnMe(googleMapSearch.getlatitude(),
						googleMapSearch.getlongitude());

				// is new or not
				String objectId = googleMapSearch.getobjectId();
				boolean isNew = true;
				for (int i = 0; i < storyList.size(); i++) {
					if (objectId.compareTo(storyList.get(i).getobjectId()) == 0) {
						isNew = false;
						break;
					}
				}
				if (isNew) {
					LatLng position = new LatLng(googleMapSearch.getlatitude(),
							googleMapSearch.getlongitude());
					switch (googleMapSearch.getState()) {
					case TYPE_STORY:
						addMarker_Story(objectId,
								googleMapSearch.getuserName(),
								googleMapSearch.getTitle(), position,
								googleMapSearch.getScore(),
								googleMapSearch.getState());
						break;
					case TYPE_READY:
						addMarker_Ready(objectId,
								googleMapSearch.getuserName(),
								googleMapSearch.getTitle(), position,
								googleMapSearch.getScore(),
								googleMapSearch.getState(),
								googleMapSearch.getSSID());
						break;
					case TYPE_OFFLINE_STORY:
						addMarker_Story(objectId,
								googleMapSearch.getuserName(),
								googleMapSearch.getTitle(), position,
								googleMapSearch.getScore(),
								googleMapSearch.getState());
						break;
					case TYPE_ONLINE_BROADCAST:
						addMarker_Broadcast(objectId,
								googleMapSearch.getuserName(),
								googleMapSearch.getTitle(), position,
								googleMapSearch.getScore(),
								googleMapSearch.getState(),
								googleMapSearch.getSSID());
						break;

					default:
						addMarker_Story(objectId,
								googleMapSearch.getuserName(),
								googleMapSearch.getTitle(), position,
								googleMapSearch.getScore(), 1);
						break;
					}
				}
				// show info window
				for (Marker searchMarker : markerList) {
					String[] temp = searchMarker.getSnippet().split(",");
					if (temp[0].compareTo(objectId) == 0) {
						searchMarker.showInfoWindow();
						break;
					}
				}
			}
		});
		// END ListView

		// close button
		buttonClose.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mSlidingLayer.closeLayer(true);
			}
		});
	}

	private void initState() {
		mSlidingLayer.setShadowDrawable(R.drawable.sidebar_shadow);
		mSlidingLayer.setShadowSizeRes(R.dimen.shadow_size);
		mSlidingLayer.setOffsetDistanceRes(R.dimen.offset_distance);
		mSlidingLayer
				.setPreviewOffsetDistanceRes(R.dimen.preview_offset_distance);
		mSlidingLayer.setStickTo(SlidingLayer.STICK_TO_RIGHT);
		mSlidingLayer.setLayerTransformer(new RotationTransformer());
		mSlidingLayer.setChangeStateOnTap(true);

		// mSlidingLayer.addView(new Button(this));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(tag, "onActivityCreated");

		MapsInitializer.initialize(mActivity);

		switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity)) {
		case ConnectionResult.SUCCESS:
			Toast.makeText(mActivity, "SUCCESS", Toast.LENGTH_SHORT).show();

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
			Toast.makeText(mActivity, "SERVICE MISSING", Toast.LENGTH_SHORT)
					.show();
			break;
		case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
			Toast.makeText(mActivity, "UPDATE REQUIRED", Toast.LENGTH_SHORT)
					.show();
			break;
		default:
			Toast.makeText(
					mActivity,
					GooglePlayServicesUtil
							.isGooglePlayServicesAvailable(mActivity),
					Toast.LENGTH_SHORT).show();
		}// END switch

	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(tag, "onStart");

	}

	@Override
	public void onMapReady(GoogleMap map) {
		Log.d(tag, "onMapReady.");
		this.map = map;
		// show NCCU
		// nccu: 24°58'46"N 121°34'15"E
//		map.addMarker(new MarkerOptions().position(
//				new LatLng(24.5846, 121.3415)).title("Marker"));
//		map.addMarker(new MarkerOptions()
//				.position(NCCU)
//				.title("NCCU")
//				.snippet(",,," + TYPE_NCCU)
//				.icon(BitmapDescriptorFactory
//						.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
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
					Toast.makeText(mActivity,
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

		// camera change listener
		map.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
			public void onCameraChange(CameraPosition position) {
				// TODO Auto-generated method stub
				current_position = position.target;
				current_zoom = position.zoom;

				Log.d(tag, "position latitude= " + position.target.latitude);
				Log.d(tag, "position longitude= " + position.target.longitude);
				Log.d(tag, "zoom =" + position.zoom);
				Log.d(tag,
						"longitude distance = "
								+ GoogleMapHelper.getGPSLongitudeDistance(
										mActivity, position.target,
										position.zoom));
				new Thread("MapIsStillQuery") {
					@Override
					public void run() {
						LatLng temp_position = current_position;
						Log.d(tag, "temp_position = " + current_position);
						try {
							Thread.sleep(WAIT_FOR_QUERY_TIME*1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Log.d(tag, "temp_position = " + current_position);
						if (temp_position == current_position) {
							Log.d(tag, "start query");

							double LatitudeDistance = GoogleMapHelper
									.getGPSLatitudeDistance(mActivity,
											current_position, current_zoom);
							double LongitudeDistance = GoogleMapHelper
									.getGPSLongitudeDistance(mActivity,
											current_position, current_zoom);

							// new GoogleMapQueryParseHelper(
							// current_position, LatitudeDistance,
							// LongitudeDistance).execute();
							//
							// List<GoogleMapData> googleMapData = SugarRecord
							// .listAll(GoogleMapData.class);
							// googleMapData.

							// start query parse
							query_Story(current_position, LatitudeDistance,
									LongitudeDistance);
							query_ready(current_position, LatitudeDistance,
									LongitudeDistance);
							query_offlineStory(current_position,
									LatitudeDistance, LongitudeDistance);
							query_onlineStory(current_position,
									LatitudeDistance, LongitudeDistance);
						}
					};
				}.start();
			}
		});

		// set googlaMap infoWindow
		setGoogleMapInfoWindow(map);
	}

	private void ShowAlertDialogAndList(final LatLng point) {

		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
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
					intent_broadcast.setClass(mActivity, TestWifiScan.class);
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
					intent_uploadStory.setClass(mActivity, UploadPage.class);
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
		// if(Globalvariable.if_Guider==true){
		builder.setItems(list_uploadType, ListClick);
		// }
		// else{
		// builder.setItems(list_uploadType_client, ListClick);
		// }
		builder.setNeutralButton("cancel", OkClick);
		builder.show();

	}

	// GPS
	private boolean initLocationProvider() {
		locationMgr = (LocationManager) mActivity
				.getSystemService(Context.LOCATION_SERVICE);
		// GPS provider
		if (locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			provider = LocationManager.GPS_PROVIDER;
			return true;
		}
		// Network provider
		else if (locationMgr
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			provider = LocationManager.NETWORK_PROVIDER;
			return true;
		}
		Log.d(tag, "return false");
		return false;
	}

	LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			updateWithNewLocation(null);
		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
			case LocationProvider.OUT_OF_SERVICE:
				Log.v(tag, "Status Changed: Out of Service");
				Toast.makeText(mActivity, "Status Changed: Out of Service",
						Toast.LENGTH_SHORT).show();
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				Log.v(tag, "Status Changed: Temporarily Unavailable");
				Toast.makeText(mActivity,
						"Status Changed: Temporarily Unavailable",
						Toast.LENGTH_SHORT).show();
				break;
			case LocationProvider.AVAILABLE:
				Log.v(tag, "Status Changed: Available");
				Toast.makeText(mActivity, "Status Changed: Available",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}

	};

	private void whereAmI() {
		// 取得上次已知的位置
		Location location = locationMgr.getLastKnownLocation(provider);
		updateWithNewLocation(location);

		// GPS Listener
		locationMgr.addGpsStatusListener(gpsListener);

		// Location Listener
		int minTime = 5000;// ms
		int minDist = 5;// meter
		locationMgr.requestLocationUpdates(provider, minTime, minDist,
				locationListener);
	}

	GpsStatus.Listener gpsListener = new GpsStatus.Listener() {
		@Override
		public void onGpsStatusChanged(int event) {
			switch (event) {
			case GpsStatus.GPS_EVENT_STARTED:
				Log.d(tag, "GPS_EVENT_STARTED");
				Toast.makeText(mActivity, "GPS_EVENT_STARTED",
						Toast.LENGTH_SHORT).show();
				break;
			case GpsStatus.GPS_EVENT_STOPPED:
				Log.d(tag, "GPS_EVENT_STOPPED");
				Toast.makeText(mActivity, "GPS_EVENT_STOPPED",
						Toast.LENGTH_SHORT).show();
				break;
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				Log.d(tag, "GPS_EVENT_FIRST_FIX");
				Toast.makeText(mActivity, "GPS_EVENT_FIRST_FIX",
						Toast.LENGTH_SHORT).show();
				break;
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				Log.d(tag, "GPS_EVENT_SATELLITE_STATUS");

				// 获取当前状态
				GpsStatus gpsStatus = locationMgr.getGpsStatus(null);
				// 获取卫星颗数的默认最大值
				int maxSatellites = gpsStatus.getMaxSatellites();
				// 创建一个迭代器保存所有卫星
				Iterator<GpsSatellite> iters = gpsStatus.getSatellites()
						.iterator();
				int count = 0;
				while (iters.hasNext() && count <= maxSatellites) {
					GpsSatellite s = iters.next();
					count++;
				}
				System.out.println("搜索到：" + count + "颗衛星");
				// Toast.makeText(mActivity, "搜索到：" + count + "颗衛星",
				// Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	private void showMarkerMe(double lat, double lng) {
		if (markerMe != null) {
			markerMe.remove();
		}

		MarkerOptions markerOpt = new MarkerOptions();
		markerOpt.position(new LatLng(lat, lng));
		markerOpt.snippet(",,," + TYPE_USER);
		markerOpt.title("我在這裡");
		markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_location_me));
		if (map == null) {
			Log.d(tag, "map == null");
		}
		markerMe = map.addMarker(markerOpt);

		Toast.makeText(mActivity, "lat:" + lat + ",lng:" + lng,
				Toast.LENGTH_SHORT).show();
	}

	private void cameraFocusOnMe(double lat, double lng) {
		CameraPosition camPosition = new CameraPosition.Builder()
				.target(new LatLng(lat, lng)).zoom(16).build();

		map.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));
	}

	private void updateWithNewLocation(Location location) {
		String where = "";
		if (location != null) {
			// 經度
			double lng = location.getLongitude();
			// 緯度
			double lat = location.getLatitude();
			// 速度
			float speed = location.getSpeed();
			// 時間
			long time = location.getTime();
			String timeString = getTimeString(time);

			where = "經度: " + lng + "\n緯度: " + lat + "\n速度: " + speed + "\n時間: "
					+ timeString + "\nProvider: " + provider;

			// "我"
			showMarkerMe(lat, lng);
			cameraFocusOnMe(lat, lng);
			// trackToMe(lat, lng);

		} else {
			where = "No location found.";
		}

		// 顯示資訊
//		 txtOutput.setText(where);
		Log.d(tag, where);
	}

	private String getTimeString(long timeInMilliseconds) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(timeInMilliseconds);
	}

	// END GPS

	private void addMarker_Story(String objectIdString, String userNameString,
			String title, LatLng point, int score, int type) {
		// TODO Auto-generated method stub
		String snippet = objectIdString + "," + userNameString + "," + score
				+ "," + type;
		Marker marker = this.map.addMarker(new MarkerOptions()
				.position(point)
				.snippet(snippet)
				.title(title)
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.ic_marker_offline)));
		markerList.add(marker);
	}

	public void addMarker_Ready(String objectIdString, String userNameString,
			String title, LatLng point, int score, int type, String SSIDString) {
		// TODO Auto-generated method stub
		String snippet = objectIdString + "," + userNameString + "," + score
				+ "," + type + "," + SSIDString;
		Marker marker = this.map.addMarker(new MarkerOptions()
				.position(point)
				.snippet(snippet)
				.title(title)
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.ic_marker_ready)));
		markerList.add(marker);
	}

	private void addMarker_Broadcast(String objectIdString,
			String userNameString, String title, LatLng point, int score,
			int type, String SSIDString) {

		String snippet = objectIdString + "," + userNameString + "," + score
				+ "," + type + "," + SSIDString;
		Marker marker = this.map.addMarker(new MarkerOptions()
				.position(point)
				.snippet(snippet)
				.title(title)
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.ic_marker_online)));
		markerList.add(marker);
	}

	private void query_Story(LatLng current_position, double latitudeDistance,
			double longitudeDistance) {
		ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>(
				"story");
		parseQuery.setLimit(PARSE_LIMIT);

		parseQuery.whereGreaterThan("latitude", current_position.latitude
				- latitudeDistance);
		parseQuery.whereLessThan("latitude", current_position.latitude
				+ latitudeDistance);
		parseQuery.whereGreaterThan("longitude", current_position.longitude
				- longitudeDistance);
		parseQuery.whereLessThan("longitude", current_position.longitude
				+ longitudeDistance);

		parseQuery.addDescendingOrder("createdAt");
		parseQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					if (!objects.isEmpty()) {
						for (int i = 0; i < objects.size(); i++) {
							boolean isNew = true;
							ParseObject parseObject = objects.get(i);
							final String objectIdString = parseObject
									.getObjectId();

							for (int listCounter = 0; listCounter < storyList
									.size(); listCounter++) {
								if (objectIdString.compareTo(storyList.get(
										listCounter).getobjectId()) == 0) {
									isNew = false;
									break;
								}
							}
							if (isNew) {

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
								final Date createdAtDate = parseObject.getCreatedAt();

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
														storyList.add(new News(
																objectIdString,
																userNameString,
																userUuidString,
																titleString,
																score, bmp,
																contentString,
																latitude,
																longitude, createdAtDate));

														if (bmp != null) {
															bmp.recycle();
														}

													}
												}
											});
								}
								else{
									Bitmap bmp = null;
									storyList.add(new News(
											objectIdString,
											userNameString,
											userUuidString,
											titleString,
											score, bmp,
											contentString,
											latitude,
											longitude, createdAtDate));
								}

								LatLng point = new LatLng(
										latitude,
										longitude);
								addMarker_Story(
										objectIdString,
										userNameString,
										titleString,
										point, score,
										TYPE_STORY);
							}
						}
					}
				}
			}
		});
	}

	private void query_ready(LatLng current_position, double latitudeDistance,
			double longitudeDistance) {
		// TODO Auto-generated method stub
		ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>(
				"offline");
		parseQuery.whereEqualTo("State", "Ready");
		parseQuery.setLimit(PARSE_LIMIT);

		parseQuery.whereGreaterThan("latitude", current_position.latitude
				- latitudeDistance);
		parseQuery.whereLessThan("latitude", current_position.latitude
				+ latitudeDistance);
		parseQuery.whereGreaterThan("longitude", current_position.longitude
				- longitudeDistance);
		parseQuery.whereLessThan("longitude", current_position.longitude
				+ longitudeDistance);

		parseQuery.addDescendingOrder("createdAt");
		parseQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					if (!objects.isEmpty()) {
						for (int i = 0; i < objects.size(); i++) {
							boolean isNew = true;
							ParseObject parseObject = objects.get(i);
							final String objectIdString = parseObject
									.getObjectId();

							for (int listCounter = 0; listCounter < storyList
									.size(); listCounter++) {
								if (objectIdString.compareTo(storyList.get(
										listCounter).getobjectId()) == 0) {
									isNew = false;
									break;
								}
							}
							if (isNew) {
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
								final Date createdAtDate = parseObject.getCreatedAt();

								ParseFile imageFile = (ParseFile) parseObject
										.get("image");
								if (imageFile != null) {
									Log.d(tag, "parseObjectId = "
											+ objectIdString);
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
														storyList.add(new News(
																objectIdString,
																userNameString,
																userUuidString,
																titleString,
																score, bmp,
																contentString,
																latitude,
																longitude, createdAtDate));

														if (bmp != null) {
															bmp.recycle();
														}
														
													}
												}
											});
								}else{
									Bitmap bmp = null;
									storyList.add(new News(
											objectIdString,
											userNameString,
											userUuidString,
											titleString,
											score, bmp,
											contentString,
											latitude,
											longitude, createdAtDate));
								}
								
								LatLng point = new LatLng(
										latitude,
										longitude);
								addMarker_Ready(
										objectIdString,
										userNameString,
										titleString,
										point, score,
										TYPE_READY,
										SSIDString);
							}
						}
					}
				}
			}
		});
	}

	private void query_offlineStory(LatLng current_position,
			double latitudeDistance, double longitudeDistance) {
		// TODO Auto-generated method stub
		ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>(
				"offline");
		parseQuery.whereEqualTo("State", "offline");
		parseQuery.setLimit(PARSE_LIMIT);

		parseQuery.whereGreaterThan("latitude", current_position.latitude
				- latitudeDistance);
		parseQuery.whereLessThan("latitude", current_position.latitude
				+ latitudeDistance);
		parseQuery.whereGreaterThan("longitude", current_position.longitude
				- longitudeDistance);
		parseQuery.whereLessThan("longitude", current_position.longitude
				+ longitudeDistance);

		parseQuery.addDescendingOrder("createdAt");
		parseQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					if (!objects.isEmpty()) {
						for (int i = 0; i < objects.size(); i++) {
							boolean isNew = true;
							ParseObject parseObject = objects.get(i);
							final String objectIdString = parseObject
									.getObjectId();

							for (int listCounter = 0; listCounter < storyList
									.size(); listCounter++) {
								if (objectIdString.compareTo(storyList.get(
										listCounter).getobjectId()) == 0) {
									isNew = false;
									break;
								}
							}
							if (isNew) {
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
								final Date createdAtDate = parseObject.getCreatedAt();

								ParseFile imageFile = (ParseFile) parseObject
										.get("image");
								if (imageFile != null) {
									Log.d(tag, "parseObjectId = "
											+ objectIdString);
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
														storyList.add(new News(
																objectIdString,
																userNameString,
																userUuidString,
																titleString,
																score, bmp,
																contentString,
																latitude,
																longitude, createdAtDate));

														if (bmp != null) {
															bmp.recycle();
														}
													}
												}
											});
								}else{
									Bitmap bmp = null;
									storyList.add(new News(
											objectIdString,
											userNameString,
											userUuidString,
											titleString,
											score, bmp,
											contentString,
											latitude,
											longitude, createdAtDate));
								}
								LatLng point = new LatLng(
										latitude,
										longitude);
								addMarker_Story(
										objectIdString,
										userNameString,
										titleString,
										point, score,
										TYPE_OFFLINE_STORY);
							}
						}
					}
				}
			}
		});
	}

	private void query_onlineStory(LatLng current_position,
			double latitudeDistance, double longitudeDistance) {
		// TODO Auto-generated method stub
		ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>(
				"offline");
		parseQuery.whereEqualTo("State", "online");
		parseQuery.setLimit(PARSE_LIMIT);

		parseQuery.whereGreaterThan("latitude", current_position.latitude
				- latitudeDistance);
		parseQuery.whereLessThan("latitude", current_position.latitude
				+ latitudeDistance);
		parseQuery.whereGreaterThan("longitude", current_position.longitude
				- longitudeDistance);
		parseQuery.whereLessThan("longitude", current_position.longitude
				+ longitudeDistance);

		parseQuery.addDescendingOrder("createdAt");
		parseQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					if (!objects.isEmpty()) {
						for (int i = 0; i < objects.size(); i++) {
							boolean isNew = true;
							ParseObject parseObject = objects.get(i);
							final String objectIdString = parseObject
									.getObjectId();

							for (int listCounter = 0; listCounter < storyList
									.size(); listCounter++) {
								if (objectIdString.compareTo(storyList.get(
										listCounter).getobjectId()) == 0) {
									isNew = false;
									break;
								}
							}
							if (isNew) {
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
								final Date createdAtDate = parseObject.getCreatedAt();

								ParseFile imageFile = (ParseFile) parseObject
										.get("image");
								if (imageFile != null) {
									Log.d(tag, "parseObjectId = "
											+ objectIdString);
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
														storyList.add(new News(
																objectIdString,
																userNameString,
																userUuidString,
																titleString,
																score, bmp,
																contentString,
																latitude,
																longitude, createdAtDate));

														if (bmp != null) {
															bmp.recycle();
														}

													}
												}
											});
								} else {
									Bitmap bmp = null;
									storyList
											.add(new News(objectIdString,
													userNameString,
													userUuidString,
													titleString, score, bmp,
													contentString, latitude,
													longitude, createdAtDate));
								}
								LatLng point = new LatLng(latitude, longitude);
								addMarker_Broadcast(objectIdString,
										userNameString, titleString, point,
										score, TYPE_ONLINE_BROADCAST,
										SSIDString);
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
				View v = mActivity.getLayoutInflater().inflate(
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

					// dialog
					dialog = ProgressDialog.show(mActivity, "讀取中",
							"如等待過久請確認網路...", true);
					dialog.setCanceledOnTouchOutside(false);

					Intent intent_detail = new Intent();
					intent_detail.putExtra("objectId", objectId);
					intent_detail.setClass(mActivity, DetailPage.class);

					dialog.dismiss();

					startActivity(intent_detail);
					break;
				case TYPE_OFFLINE_STORY:
					Log.d(tag, "Icon TYPE_OFFLINE_STORY onclick.");

					// dialog
					dialog = ProgressDialog.show(mActivity, "讀取中",
							"如等待過久請確認網路...", true);
					dialog.setCanceledOnTouchOutside(false);

					ParseQuery<ParseObject> query = ParseQuery
							.getQuery("offline");
					// Retrieve the object by id
					query.getInBackground(objectId,
							new GetCallback<ParseObject>() { // 以後博要給我object ID
								@Override
								public void done(ParseObject offline,
										ParseException e) {
									dialog.dismiss();

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
										Intent intent = new Intent(mActivity,
												CustomerDetailActivity.class);
										mActivity.startActivity(intent);

									} else {
										e.printStackTrace();
									}
								}
							});

					break;
				case TYPE_ONLINE_BROADCAST:
					Log.d(tag, "Icon TYPE_ONLINE_BROADCAST onclick.");

				case TYPE_READY:
					Log.d(tag, "Icon TYPE_READY onclick.");

					// dialog
					dialog = ProgressDialog.show(mActivity, "讀取中",
							"如等待過久請確認網路...", true);
					dialog.setCanceledOnTouchOutside(false);

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
									dialog.dismiss();

									if (e == null) {
										ParseObject parseObject = objects
												.get(0);

										if (parseObject.getString("State")
												.compareTo("online") == 0
												|| parseObject.getString(
														"State").compareTo(
														"Ready") == 0) {// still
																		// online
											String SSIDstring = temp[4];
											// parse broadcast
											// 若wifi狀態為關閉則將它開啟
											wiFiManager = (WifiManager) mActivity
													.getSystemService(Context.WIFI_SERVICE);
											if (!wiFiManager.isWifiEnabled()) {
												wiFiManager
														.setWifiEnabled(true);
											}
											wiFiManager = (WifiManager) mActivity
													.getSystemService(Context.WIFI_SERVICE);
											System.out.println("wiFiManagergetConnectionInfo"
													+ wiFiManager
															.getConnectionInfo()
													+ "$"
													+ wiFiManager
															.getWifiState()
													+ " ");

											if (!wiFiManager.isWifiEnabled()) { // 判斷是否有網路
												Toast.makeText(mActivity,
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
												WifiManager wifiManager2 = (WifiManager) mActivity
														.getSystemService(Context.WIFI_SERVICE);
												wifiManager2.addNetwork(conf);

												List<WifiConfiguration> list = wifiManager2
														.getConfiguredNetworks();
												if_Global_local = 0;
												for (WifiConfiguration wificonfig : list) { // 解決ap遇到不在現場就會無法連線的問題
													if (if_find_wificonnect == true) {
														System.out
																.println("Main_configwifi1");
														if_find_wificonnect = false;
														break;

													}
													for (int i = 0; i < SSIDList
															.size(); i++) {
														System.out.println("Main_configwifi1.5"
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
															if_Global_local = 1;       //判斷是給 Local
															break;
														}
													}

												}
												Globalvariable.latitude = latitude; // 等等比對是否要250
																					// or
																					// 251
												Globalvariable.longitude = longitude;
												System.out.println("GOGOGO4"
														+ if_Global_local);
												System.out.println("latitude"
														+ latitude + "@"
														+ longitude);
												Globalvariable.latitude = latitude;
												Globalvariable.longitude = longitude;
												Intent intent = new Intent(
														mActivity,
														Client_Main.class); // 改寫成TestWifiScan.this
												intent.putExtra(
														"if_Global_local",
														if_Global_local);// 可放所有基本類別
												startActivity(intent);
											
											}
										} else {// no longer exists
											Log.d(tag,
													"Broadcast is over change to offline.");
											Toast.makeText(mActivity,
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
																		mActivity,
																		CustomerDetailActivity.class);
																mActivity
																		.startActivity(intent);

															}
														}
													});
										}
									} else {
										e.printStackTrace();
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
		if (locationMgr != null) {
			locationMgr.removeUpdates(locationListener);
		}
		super.onStop();
		Log.d(tag, "onStop.");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d(tag, "onDestroyView.");
		storyList.clear();
		if (map != null) {
			map.clear();
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(tag, "onDestroy.");
	}

}