package com.mclab1.palace.customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ro.ui.pttdroid.Globalvariable;

import com.mclab1.palaca.parsehelper.VoiceDataElement;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import edu.mclab1.nccu_story.R;
import android.app.Activity;
import android.content.Context;
import android.text.StaticLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mclab1.palaca.parsehelper.VoiceDataElement;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import edu.mclab1.nccu_story.R;

//import com.squareup.picasso.Picasso;

public class CustomerVoiceListAdapter extends ArrayAdapter<VoiceDataElement> {
	private final Activity context;
	private ArrayList<VoiceDataElement> mp3Path = new ArrayList<VoiceDataElement>();

	static class ViewHolder {
		public TextView title;
		public ImageView image;
		public TextView datdate;
		public RatingBar ratingBar;
	}

	public CustomerVoiceListAdapter(Activity context,
			ArrayList<VoiceDataElement> mp3Path_) {
		super(context, R.layout.voice_row, mp3Path_);
		this.context = context;
		this.mp3Path = mp3Path_;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		// reuse views
		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.voice_row, parent, false);
			// configure view holder
			ViewHolder viewHolder = new ViewHolder();

			viewHolder.image = (ImageView) rowView
					.findViewById(R.id.voice_row_image);
			//viewHolder.ratingBar = (RatingBar) rowView
			//		.findViewById(R.id.voice_row_bar);
			viewHolder.title = (TextView) rowView
					.findViewById(R.id.voice_row_guidername);
			viewHolder.datdate = (TextView) rowView
					.findViewById(R.id.voice_row_date);
			rowView.setTag(viewHolder);
		}

		// fill data
		final ViewHolder holder = (ViewHolder) rowView.getTag();
		Random ran = new Random();
		holder.datdate.setText(mp3Path.get(position).createdTime);
		//holder.ratingBar.setRating(ran.nextInt(5)+1);                   //////柏要給我分數
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("offline");
		System.out.println("latitiude"+Globalvariable.latitude+" "+Globalvariable.longitude);
		// Retrieve the object by id	
		query.whereEqualTo("latitude", Globalvariable.latitude);    //柏傳給我經緯度，我做經緯度限制
		query.whereEqualTo("longitude", Globalvariable.longitude);  	
		query.findInBackground(new FindCallback<ParseObject>() {	
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
		        if (e == null) {	
		        	System.out.println("title");
		        	holder.title.setText((String)objects.get(0).get("userName"));
		        	
		        }
		        
		        {
		        	System.out.println("ErrorCustomer");
		        }
			}
		});
	

		return rowView;
	}
}