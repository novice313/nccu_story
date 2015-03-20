package edu.mclab1.nccu_story;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback { 
	
	private final String tag = "MainActivityTAG";
	final LatLng NCCU = new LatLng(24.986233,121.575843);
	int zoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        
        
        
    }

    @Override
    public void onMapReady(GoogleMap map) {
    	// nccu: 24°58'46"N 121°34'15"E
        map.addMarker(new MarkerOptions().position(new LatLng(24.5846, 121.3415)).title("Marker"));
        map.addMarker(new MarkerOptions().position(NCCU).title("NCCU"));
	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(NCCU, 16));
    }
}
