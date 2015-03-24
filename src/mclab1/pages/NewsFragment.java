package mclab1.pages;

import edu.mclab1.nccu_story.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NewsFragment extends Fragment {

	private final static String tag = "NewsFragment";
	private String title;
	private int page;
	TextView textView;

//	public static NewsFragment newInstance(int page, String title) {
//		Log.d(tag, "newInstance.");
//		NewsFragment newsFragment = new NewsFragment();
//		Bundle args = new Bundle();
//		args.putInt("someInt", page);
//		args.putString("someTitle", title);
//		newsFragment.setArguments(args);
//		return newsFragment;
//	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.news, container, false);
		textView = (TextView) view.findViewById(R.id.button_test);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(tag, "oncreated.");
	}
	
	public void onResume(){
		super.onResume();
		textView.setText(tag);
	}

}
