package mclab1.service.upload;

import java.util.ArrayList;

import mclab1.pages.MediaPlayerFragment;

import edu.mclab1.nccu_story.MainActivity;
import edu.mclab1.nccu_story.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.mclab1.nccu_story.MainActivity;;

public class SongAdapter extends BaseAdapter {

	private static final String tag = "SongAdaptertag";
	// song list and layout
	private ArrayList<Song> songs;
	private LayoutInflater songInf;

	// constructor
	public SongAdapter(Context c, ArrayList<Song> theSongs) {
		songs = theSongs;
		songInf = LayoutInflater.from(c);
	}

	@Override
	public int getCount() {
		return songs.size();
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
		LinearLayout songLay = (LinearLayout) songInf.inflate(R.layout.song,
				parent, false);
		// get title and artist views
		TextView songView = (TextView) songLay.findViewById(R.id.song_title);
		TextView artistView = (TextView) songLay.findViewById(R.id.song_artist);
		// get song using position
		Song currSong = songs.get(position);
		// get title and artist strings
		songView.setText(currSong.getTitle());
		artistView.setText(currSong.getArtist());
		// set position as tag
		songLay.setTag(position);
//		songLay.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				//songPick
//				Log.d(tag, v.toString()+" onClick");
//				v.get
//			}
//		});
		return songLay;
	}

}
