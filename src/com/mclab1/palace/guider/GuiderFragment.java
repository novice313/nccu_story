package com.mclab1.palace.guider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.mclab1.nccu_story.R;
import ro.ui.pttdroid.settings.CommSettings;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.greenrobot.event.EventBus;

public class GuiderFragment extends Fragment {
	public static final String TAG = "GuiderFragment";
	private ListView log_list_view;
	private ArrayList<String> log = new ArrayList<String>();
	private ArrayAdapter<String> listAdapter;
	private final SimpleDateFormat sdFormat = new SimpleDateFormat("HH:mm:ss");
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		View view = inflater
				.inflate(R.layout.guider_fragment, container, false);
		init_view(view);

		return view;
	}

	private void init_view(View v) {
		log_list_view = (ListView) v.findViewById(R.id.guider_log_listview);
		listAdapter = new ArrayAdapter<String>(getActivity(),
				R.layout.event_row_list_item, log);
		// listAdapter.add("aaa");
		log_list_view.setAdapter(listAdapter);

	}

	// listen to events

	public void onStickyEvent(DisplayEvent event) {
		final String todisplay = event.content;
		add_log(todisplay);
		EventBus.getDefault().removeStickyEvent(event);
		
	}

	public void onEvent(DisplayEvent event) {
		Log.i(TAG, "Got log!");
		final String todisplay = event.content;
		add_log(todisplay);

	}

	private void add_log(final String todisplay) {
		
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				log.add(0, sdFormat.format(new Date())+" : "+todisplay);
				listAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			EventBus.getDefault().register(this);
			EventBus.getDefault().post(new DisplayEvent("Guider init!"));
			EventBus.getDefault().postSticky(new DisplayEvent("Guider test sticky  for PRO GUIDER !"));
			/*EventBus.getDefault().postSticky(new DisplayEvent("CommSettings.getMulticastAddr() !"+CommSettings.getMulticastAddr()));*/
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			EventBus.getDefault().unregister(this);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}