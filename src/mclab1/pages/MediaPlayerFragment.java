package mclab1.pages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import mclab1.service.music.MusicController;
import mclab1.service.music.MusicService;
import mclab1.service.music.MusicService.MusicBinder;
import mclab1.service.music.Song;
import mclab1.service.music.SongAdapter;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.MediaController.MediaPlayerControl;
import edu.mclab1.nccu_story.MainActivity;
import edu.mclab1.nccu_story.R;

public class MediaPlayerFragment extends Fragment implements MediaPlayerControl {

	private static final String tag = "MediaPlayerFragmentTag";

	// song list variables
	public static ArrayList<Song> songList;
	public static ListView songView;

	// service
	public static MusicService musicSrv;
	public static Intent playIntent;
	// binding
	public static boolean musicBound = false;

	// controller
	public static MusicController controller;

	// activity and playback pause flags
	private boolean paused = false;

	public static boolean playbackPaused = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(tag, "onCreate.");

		// set recorder icon at action bar
		setHasOptionsMenu(true);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		if (menu != null) {
			menu.findItem(R.id.action_test).setVisible(true);
			menu.findItem(R.id.action_recorder).setVisible(true);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(tag, "onCreateView.");
		View view = inflater.inflate(R.layout.fragment_mediaplayer, container,
				false);
		// retrieve list view
		songView = (ListView) view.findViewById(R.id.song_list);
		// controller.show();
		return view;

	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		Log.d(tag, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);

		// instantiate list
		if (!(songList==null)) {
			songList.clear();
		}
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
		SongAdapter songAdt = new SongAdapter(getActivity()
				.getApplicationContext(), songList);
		songView.setAdapter(songAdt);

		// setup controller
		setController();
		// controller.show();
	}

	// start and bind the service when the activity starts
	@Override
	public void onStart() {
		Log.d(tag, "onstart");
		super.onStart();
		if (MediaPlayerFragment.playIntent == null) {
			MediaPlayerFragment.playIntent = new Intent(getActivity(),
					MusicService.class);
			getActivity().bindService(MediaPlayerFragment.playIntent,
					MediaPlayerFragment.musicConnection, Context.BIND_AUTO_CREATE);
			getActivity().startService(MediaPlayerFragment.playIntent);
		}
	}

	public void getSongList() {
		// query external audio
		ContentResolver musicResolver = getActivity().getContentResolver();
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
			// add songs to list
			do {
				long thisId = musicCursor.getLong(idColumn);
				String thisTitle = musicCursor.getString(titleColumn);
				String thisArtist = musicCursor.getString(artistColumn);
				songList.add(new Song(thisId, thisTitle, thisArtist));
			} while (musicCursor.moveToNext());
		}
	}

	// set the controller up
	private void setController() {
		controller = new MusicController(getActivity());
		// set previous and next button listeners
		controller.setPrevNextListeners(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				playNext();
			}
		}, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				playPrev();
			}
		});
		// set and show
		controller.setMediaPlayer(this);
		controller.setAnchorView(getActivity().findViewById(R.id.song_list));
		controller.setEnabled(true);
	}

	public static void playNext() {
		musicSrv.playNext();
		if (playbackPaused) {
			// MainActivity.setController();
			playbackPaused = false;
		}
		controller.show(0);
	}

	public static void playPrev() {
		musicSrv.playPrev();
		if (playbackPaused) {
			// MainActivity.setController();
			playbackPaused = false;
		}
		controller.show(0);
	}

	// mediaplayer
		// connect to the service
		public static ServiceConnection musicConnection = new ServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				MusicBinder binder = (MusicBinder) service;
				// get service
				MediaPlayerFragment.musicSrv = binder.getService();

				// if(musicSrv!=null){
				Log.d(tag, MediaPlayerFragment.musicSrv.toString());
				// }
				// pass list
				MediaPlayerFragment.musicSrv.setList(MediaPlayerFragment.songList);
				MediaPlayerFragment.musicBound = true;
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				MediaPlayerFragment.musicBound = false;
			}
		};
	
	@Override
	public void onPause() {
		super.onPause();
		paused = true;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (paused) {
			// MainActivity.setController();
			paused = false;
		}
	}

	@Override
	public void onStop() {
		controller.hide();
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		Log.d(tag, "height = "+controller.getHeight());
		controller.removeAllViews();
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		// getActivity().stopService(playIntent);
		musicSrv = null;
		super.onDestroy();
	}

	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public int getAudioSessionId() {
		return 0;
	}

	@Override
	public int getBufferPercentage() {
		return 0;
	}

	@Override
	public int getCurrentPosition() {
		if (musicSrv != null && musicBound && musicSrv.isPng())
			return musicSrv.getPosn();
		else
			return 0;
	}

	@Override
	public int getDuration() {
		if (musicSrv != null && musicBound && musicSrv.isPng())
			return musicSrv.getDur();
		else
			return 0;
	}

	@Override
	public boolean isPlaying() {
		if (musicSrv != null && musicBound)
			return musicSrv.isPng();
		return false;
	}

	@Override
	public void pause() {
		playbackPaused = true;
		musicSrv.pausePlayer();
	}

	@Override
	public void seekTo(int pos) {
		musicSrv.seek(pos);
	}

	@Override
	public void start() {
		musicSrv.go();
	}

}
