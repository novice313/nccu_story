package mclab1.pages;

import java.util.List;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.AlertDialog;
import android.app.DownloadManager.Query;
import android.app.Notification.Builder;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
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
		// nccu: 24°58'46"N 121°34'15"E
		// map.addMarker(new MarkerOptions().position(new LatLng(24.5846,
		// 121.3415)).title("Marker"));
		map.addMarker(new MarkerOptions().position(NCCU).title("NCCU"));
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
				// TODO Auto-generated method stub
				ShowAlertDialogAndList(point);
			}
		});

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				query_Story();
			}
		});

		// set googlaMap infoWindow
		setGoogleMapInfoWindow(map);
	}

	private void ShowAlertDialogAndList(final LatLng point) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("標題");
		// 建立選擇的事件
		DialogInterface.OnClickListener ListClick = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case 0://broadcast
						Log.d(tag, "list_uploadType "+list_uploadType[which]+" onclick");
//						Intent intent_broadcast = new Intent();
//						intent_broadcast.setClass(getActivity(), Broadcast.class);
//						Bundle bundle_broadcast = new Bundle();
//						bundle_broadcast.putDouble("longitude", point.longitude);
//						bundle_broadcast.putDouble("latitude", point.latitude);
//						//將Bundle物件assign給intent
//						intent_broadcast.putExtras(bundle_broadcast);
//
//			            //切換Activity
//			            startActivity(intent_broadcast);
			            break;
					case 1://upload story
						Log.d(tag, "list_uploadType "+list_uploadType[which]+" onclick");
						Intent intent_uploadStory = new Intent();
						intent_uploadStory.setClass(getActivity(), UploadPage.class);
						Bundle bundle_uploadStory = new Bundle();
						bundle_uploadStory.putDouble("longitude", point.longitude);
						bundle_uploadStory.putDouble("latitude", point.latitude);
						//將Bundle物件assign給intent
						intent_uploadStory.putExtras(bundle_uploadStory);

			            //切換Activity
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
		builder.setNeutralButton("取消", OkClick);
		builder.show();

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
			public View getInfoContents(Marker arg0) {

				// Getting view from the layout file info_window_layout
				View v = getActivity().getLayoutInflater().inflate(
						R.layout.info_window_layout, null);

				// Getting the position from the marker
				LatLng latLng = arg0.getPosition();

				// Getting reference to the TextView to set latitude
				TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);

				// Getting reference to the TextView to set longitude
				TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);

				// Setting the latitude
				tvLat.setText("Latitude:" + latLng.latitude);

				// Setting the longitude
				tvLng.setText("Longitude:" + latLng.longitude);

				// Returning the view containing InfoWindow contents
				return v;

			}
		});

		googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker) {
				// TODO Auto-generated method stub
				Log.d(tag, "onInfoWindowClick: " + marker.getTitle());
				// Intent detailIntent = new Intent(getActivity(),
				// Detail.class);
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
