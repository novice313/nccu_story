package mclab1.service.upload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import mclab1.service.upload.Song;
import mclab1.service.upload.SongAdapter;
import edu.mclab1.nccu_story.R;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class UploadMediaListActivity extends Activity {
	
	private final String tag = "UploadMediaListTag"; 

	// song list variables
	public static ArrayList<Song> songList;
	public static ListView songView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_mediaplayer);
		songView = (ListView) findViewById(R.id.song_list);

		setupSongList();
	}

	private void setupSongList() {
		// TODO Auto-generated method stub
		// instantiate list
		songList = new ArrayList<Song>();
		// get songs from device
		getSongList();
		// sort alphabetically by title
		Collections.sort(songList, new Comparator<Song>() {
			public int compare(Song a, Song b) {
				return a.getTitle().compareTo(b.getTitle());
			}
		});
		// create and set adapter
		SongAdapter songAdt = new SongAdapter(this, songList);
		songView.setAdapter(songAdt);
		songView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Song song = songList.get(position);
				Log.d(tag, "path = "+song.getPath());
				Intent intent_path=new Intent();
				Bundle bundle_path=new Bundle();
				bundle_path.putString("musicPath", song.getPath());
				intent_path.putExtras(bundle_path);
				setResult(RESULT_OK, intent_path);
				finish();
			}
		});
	}

	public void getSongList() {
		// query external audio
		ContentResolver musicResolver = getContentResolver();
		Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Cursor musicCursor = musicResolver.query(musicUri, null, null, null,
				null);
		// iterate over results if valid
		if (musicCursor != null && musicCursor.moveToFirst()) {
			// get columns
			int titleColumn = musicCursor
					.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
			int idColumn = musicCursor
					.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
			int artistColumn = musicCursor
					.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
			int pathColumn = musicCursor
					.getColumnIndex(android.provider.MediaStore.Audio.Media.DATA);
			// add songs to list
			do {
				long thisId = musicCursor.getLong(idColumn);
				String thisTitle = musicCursor.getString(titleColumn);
				String thisArtist = musicCursor.getString(artistColumn);
				String thisPath = musicCursor.getString(pathColumn);
				songList.add(new Song(thisId, thisTitle, thisArtist, thisPath));
			} while (musicCursor.moveToNext());
		}
	}

}
