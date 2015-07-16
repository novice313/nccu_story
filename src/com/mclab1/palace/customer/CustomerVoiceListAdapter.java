package com.mclab1.palace.customer;

import java.util.ArrayList;
import java.util.Random;
import edu.mclab1.nccu_story.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

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
			viewHolder.ratingBar = (RatingBar) rowView
					.findViewById(R.id.voice_row_bar);
			viewHolder.title = (TextView) rowView
					.findViewById(R.id.voice_row_guidername);
			viewHolder.datdate = (TextView) rowView
					.findViewById(R.id.voice_row_date);
			rowView.setTag(viewHolder);
		}

		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();
		Random ran = new Random();
		int val = ran.nextInt(2) + 1;
		holder.datdate.setText(mp3Path.get(position).createdTime);
		holder.ratingBar.setRating(ran.nextInt(5)+1);
		switch (val) {
		case 0:
			holder.title.setText("1");
//			Picasso.with(context).load(R.drawable.guider).into(holder.image);
			
			break;
		case 1:
			holder.title.setText("2");
//			Picasso.with(context).load(R.drawable.guider2).into(holder.image);
			break;
		case 2:
			holder.title.setText("3");
//			Picasso.with(context).load(R.drawable.guider3).into(holder.image);
			break;

		default:
			break;
		}

		return rowView;
	}
}