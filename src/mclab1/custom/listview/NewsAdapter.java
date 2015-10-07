package mclab1.custom.listview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.orm.Database;
import com.paging.listview.PagingBaseAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.mclab1.nccu_story.R;

public class NewsAdapter extends PagingBaseAdapter<News> {

	private static final String tag = "NewsAdaptertag";
	// song list and layout
	private ArrayList<News> news;
	private LayoutInflater newsInf;
	private Context mContext;

	private static final int[] COLORS = new int[] { R.color.green_light,
			R.color.orange_light, R.color.blue_light, R.color.red_light };

	// constructor
	public NewsAdapter(Context c, ArrayList<News> theNews) {
		Collections.sort(theNews, new CustomComparator());
		mContext = c;
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
		LinearLayout banner = (LinearLayout) newsLay.findViewById(R.id.banner);
		TextView userName = (TextView) newsLay.findViewById(R.id.userName);
		ImageView image = (ImageView) newsLay.findViewById(R.id.imageView);
		ExpandableTextView content = (ExpandableTextView) newsLay.findViewById(R.id.content);
		TextView title = (TextView) newsLay.findViewById(R.id.title);

		// set banner background color
		banner.setBackgroundColor(parent.getResources().getColor(
				R.color.blue_light));

		// get song using position
		News currNews = news.get(position);

		Log.d(tag, currNews.toString());

		// get title and artist strings
		userName.setText(currNews.getuserName());
		title.setText("#Keyword "+currNews.getTitle());
		Bitmap bmp = currNews.getImage();
		if (bmp != null) {
//			DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
//			int width = displayMetrics.widthPixels;
//			int height = displayMetrics.heightPixels;
//			Bitmap bmp_new = bmp.createScaledBitmap(bmp, width, width, true);
//			image.setImageBitmap(bmp_new);
			image.setImageBitmap(bmp);
		}

		content.setText(currNews.getContent());
		// set position as tag
		newsLay.setTag(position);
		newsLay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// songPick
				Log.d(tag, v.toString() + " onClick");
				// MainActivity.musicSrv.setSong(Integer.parseInt(v.getTag().toString()));
				// MainActivity.musicSrv.playSong();
				// if (MainActivity.playbackPaused) {
				// //setController();
				// MainActivity.playbackPaused = false;
				// }
				// MainActivity.controller.show(0);
			}
		});

		newsLay.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Log.d(tag, v.toString() + " onLongClick");
				return false;
			}
		});
		return newsLay;
	}


}

class CustomComparator implements Comparator<News> {
	@Override
	public int compare(News n1, News n2) {
		return n1.getCreatedDate().compareTo(n2.getCreatedDate());
	}
}
