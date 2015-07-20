package mclab1.pages;

import java.util.ArrayList;
import java.util.List;

import mclab1.custom.listview.News;

import com.farproc.wifi.connecter.TestWifiScan;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
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
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.AlertDialog;
import android.app.DownloadManager.Query;
import android.app.Notification.Builder;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.NetworkInfo.DetailedState;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.mclab1.nccu_story.R;

public class GoogleMapFragment extends Fragment
/* implements OnMapReadyCallback */implements OnMapReadyCallback {

	public static ArrayList<News> newsList;
	private final static String tag = "GoogleMapFragment";
	private static final String MAP_FRAGMENT_TAG = "map";
	final LatLng NCCU = new LatLng(24.986233, 121.575843);
	int initial_zoom_size = 16;
	SupportMapFragment mapFragment;

	String[] list_uploadType = { "Broadcast", "Upload story" };

	GoogleMap map;
	final int PARSE_LIMIT = 50;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.googlemap);
		Log.d(tag, "oncreated.");

		// instantiate list
		newsList = new ArrayList<News>();

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
	}

	@Override
	public void onMapReady(GoogleMap map) {
		Log.d(tag, "onMapReady.");
		this.map = map;
		map.clear();
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
				// longclick upload
				ShowAlertDialogAndList(point);
			}
		});

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

		// set googlaMap infoWindow
		setGoogleMapInfoWindow(map);
	}

	private void ShowAlertDialogAndList(final LatLng point) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Upload");
		// 建立選擇的事件
		DialogInterface.OnClickListener ListClick = new DialogInterface.OnClickListener() {
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
			public void onClick(DialogInterface dialog, int which) {
			}
		};
		builder.setItems(list_uploadType, ListClick);
		builder.setNeutralButton("cancel", OkClick);
		builder.show();

	}

	private void addMarker_Story(String objectId, String title, LatLng point,
			int score) {
		// TODO Auto-generated method stub
		this.map.addMarker(new MarkerOptions()
				.position(point)
				.snippet(objectId)
				.title(title)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
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
													Bitmap bmp = BitmapFactory
															.decodeByteArray(
																	data, 0,
																	data.length);
													newsList.add(new News(
															objectIdString,
															userNameString,
															userUuidString,
															titleString, score,
															bmp, contentString,
															latitude, longitude));

													LatLng point = new LatLng(
															latitude, longitude);
													addMarker_Story(objectIdString,
															titleString, point,
															score);

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
								Log.d(tag, "parseObjectId = "+objectIdString);
								imageFile
										.getDataInBackground(new GetDataCallback() {

											@Override
											public void done(byte[] data,
													ParseException e) {
												if (e == null) {
													// Log.d(tag,
													// "parseFile done");
													Bitmap bmp = BitmapFactory
															.decodeByteArray(
																	data, 0,
																	data.length);
													newsList.add(new News(
															objectIdString,
															userNameString,
															userUuidString,
															titleString, score,
															bmp, contentString,
															latitude, longitude));

													LatLng point = new LatLng(
															latitude, longitude);
													addMarker_Story(objectIdString,
															titleString, point,
															score);

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

				TextView tvLat = (TextView) v.findViewById(R.id.userName);
				TextView tvLng = (TextView) v.findViewById(R.id.title);
				// TextView tvLng = (TextView) v.findViewById(R.id.score);

				// Setting the latitude
				tvLat.setText(marker.getSnippet());

				// Setting the longitude
				tvLng.setText(marker.getTitle());

				// Returning the view containing InfoWindow contents
				return v;

			}
		});

		googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker) {
				// TODO Auto-generated method stub
				Log.d(tag, "onInfoWindowClick: " + marker.getTitle());
				for (int i = 0; i < newsList.size(); i++) {
					String objectId = marker.getSnippet();
					if (objectId.compareTo(newsList.get(i).getobjectId()) == 0) {
						Intent intent_detail = new Intent();
						intent_detail.putExtra("objectId", objectId);
						intent_detail.setClass(getActivity(), DetailPage.class);
						startActivity(intent_detail);
						break;
					}
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
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(tag, "onDestroy.");
	}

}
