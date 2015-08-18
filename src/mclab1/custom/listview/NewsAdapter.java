package mclab1.custom.listview;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.mclab1.nccu_story.R;

public class NewsAdapter extends BaseAdapter{

	private static final String tag = "NewsAdaptertag";
	// song list and layout
	private ArrayList<News> news;
	private LayoutInflater newsInf;

	// constructor
	public NewsAdapter(Context c, ArrayList<News> theNews) {
		news = theNews;
		newsInf = LayoutInflater.from(c);
	}

	@Override
	public int getCount() {
		return news.size();
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
		LinearLayout newsLay = (LinearLayout) newsInf.inflate(R.layout.detail,
				parent, false);
		// get title and artist views
		
		TextView userName = (TextView) newsLay.findViewById(R.id.userName);
		TextView title = (TextView) newsLay.findViewById(R.id.title);
		ImageView image = (ImageView) newsLay.findViewById(R.id.imageView);
		TextView content = (TextView) newsLay.findViewById(R.id.content);
		
		// get song using position
		News currNews = news.get(position);
		
		Log.d(tag, currNews.toString());
		
		// get title and artist strings
		userName.setText(currNews.getuserName());
		title.setText(currNews.getTitle());
		image.setImageBitmap(currNews.getImage());
		
		content.setText(currNews.getContent());
		// set position as tag
		newsLay.setTag(position);
		newsLay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//songPick
				Log.d(tag, v.toString()+" onClick");
//				MainActivity.musicSrv.setSong(Integer.parseInt(v.getTag().toString()));
//				MainActivity.musicSrv.playSong();
//				if (MainActivity.playbackPaused) {
//					//setController();
//					MainActivity.playbackPaused = false;
//				}
//				MainActivity.controller.show(0);
			}
		});
		
		newsLay.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Log.d(tag, v.toString()+" onLongClick");
				return false;
			}
		});
		return newsLay;
	}

}
