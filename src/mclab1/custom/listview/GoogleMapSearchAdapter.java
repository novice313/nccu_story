package mclab1.custom.listview;

import java.util.ArrayList;

import mclab1.pages.GoogleMapFragment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.mclab1.nccu_story.R;

public class GoogleMapSearchAdapter extends BaseAdapter {

	private static final String tag = "GoogleMapSearchAdaptertag";
	// song list and layout
	private ArrayList<GoogleMapSearch> googleMapSearch;
	private LayoutInflater googleMapSearchInf;

	// constructor
	public GoogleMapSearchAdapter(Context c,
			ArrayList<GoogleMapSearch> theGoogleMapSearch) {
		googleMapSearch = theGoogleMapSearch;
		googleMapSearchInf = LayoutInflater.from(c);
	}

	@Override
	public int getCount() {
		return googleMapSearch.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// map to song layout
		LinearLayout googleMapSearchLay = (LinearLayout) googleMapSearchInf
				.inflate(R.layout.googlemapsearch, parent, false);
		// get title and artist views

		TextView userName = (TextView) googleMapSearchLay
				.findViewById(R.id.userName);
		TextView title = (TextView) googleMapSearchLay.findViewById(R.id.title);
		TextView state = (TextView) googleMapSearchLay.findViewById(R.id.state);

		// get song using position
		GoogleMapSearch currGoogleMapSearch = googleMapSearch.get(position);

		Log.d(tag, currGoogleMapSearch.toString());

		// get title and artist strings
		userName.setText(currGoogleMapSearch.getuserName());
		title.setText(currGoogleMapSearch.getTitle());
		state.setText(GoogleMapFragment.TYPE[currGoogleMapSearch.getState()]);
		// set position as tag
		googleMapSearchLay.setTag(position);
		
		return googleMapSearchLay;
	}

}
