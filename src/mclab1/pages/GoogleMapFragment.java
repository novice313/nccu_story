package mclab1.pages;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import edu.mclab1.nccu_story.R;

public class GoogleMapFragment extends Fragment implements OnMapReadyCallback { 
	
	private final static String tag = "GoogleMapFragment";
	final LatLng NCCU = new LatLng(24.986233,121.575843);
	int zoom;
	SupportMapFragment mapFragment;
	
//	public static GoogleMapFragment newInstance(int page, String title) {
//		Log.d(tag,"newInstance.");
//		GoogleMapFragment googlemapFragment = new GoogleMapFragment();
//		Bundle args = new Bundle();
//		args.putInt("someInt", page);
//		args.putString("someTitle", title);
//		googlemapFragment.setArguments(args);
//		return googlemapFragment;
//	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(tag,"onCreateView.");
		View view = inflater.inflate(R.layout.googlemap, container,
				false);
		mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
		if(mapFragment==null){Log.d(tag, "mapFragment is null.");}
		else{mapFragment.getMapAsync(this);}
		return view;
	}
	
	
 
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.googlemap);
        Log.d(tag,"oncreated.");
        
    }

    @Override
    public void onMapReady(GoogleMap map) {
    	Log.d(tag,"onMapReady.");
    	// nccu: 24°58'46"N 121°34'15"E
        map.addMarker(new MarkerOptions().position(new LatLng(24.5846, 121.3415)).title("Marker"));
        map.addMarker(new MarkerOptions().position(NCCU).title("NCCU"));
	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(NCCU, 16));
	    
	    //set googlaMap infoWindow
	    setGoogleMapInfoWindow(map);
    }



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
                View v = getActivity().getLayoutInflater().inflate(R.layout.info_window_layout, null);
 
                // Getting the position from the marker
                LatLng latLng = arg0.getPosition();
                
                // Getting reference to the TextView to set latitude
                TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
 
                // Getting reference to the TextView to set longitude
                TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
 
                // Setting the latitude
                tvLat.setText("Latitude:" + latLng.latitude);
 
                // Setting the longitude
                tvLng.setText("Longitude:"+ latLng.longitude);
 
                // Returning the view containing InfoWindow contents
                return v;
 
            }
        });
	}

	



	

	
}
