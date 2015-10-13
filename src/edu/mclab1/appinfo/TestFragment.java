package edu.mclab1.appinfo;

import edu.mclab1.nccu_story.R;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public final class TestFragment extends Fragment {
	private static final String KEY_CONTENT = "TestFragment:Content";
	private static int mContent;
	private Button btn_Close;
	private static int mposition;
	protected static final int[] ICONS = new int[] { R.drawable.appinfo1,
			R.drawable.appinfo2, R.drawable.appinfo3, R.drawable.appinfo4,
			R.drawable.appinfo3, R.drawable.appinfo6
	// R.drawable.background1
	};

	public static TestFragment newInstance(int position) {
		// TODO Auto-generated method stub
		Log.d(KEY_CONTENT, "position = " + position);
		mposition = position;
		mContent = ICONS[position];
		TestFragment fragment = new TestFragment();

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// LinearLayout layout = new LinearLayout(getActivity());
		// layout.setBackgroundResource(mContent);

		View view = inflater.inflate(mContent, container, false);

		btn_Close = (Button) view.findViewById(R.id.buttonClose);
		Log.d(KEY_CONTENT, "mposition = " + mposition);
		Log.d(KEY_CONTENT, "ICONS.lengh = " + ICONS.length);
		if (btn_Close != null) {
			btn_Close.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					final SharedPreferences prefs = PreferenceManager
							.getDefaultSharedPreferences(getActivity());
					final String eulaKey = AppInfo.EULA_PREFIX
							+ AppInfo.versionInfo.versionCode;
					SharedPreferences.Editor editor = prefs.edit();

					editor.putBoolean(eulaKey, true);
					editor.commit();
					getActivity().finish();
				}
			});
		}
		return view;
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(KEY_CONTENT, mContent);
	}
}
