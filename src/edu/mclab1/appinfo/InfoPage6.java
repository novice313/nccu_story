package edu.mclab1.appinfo;

import edu.mclab1.nccu_story.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class InfoPage6 extends Fragment {

	private ImageView imageView;
	private Button btn_Close;
	private int position;

	public InfoPage6(int position) {
		// TODO Auto-generated constructor stub
		this.position = position;
	}

	@Override
	public void onAttach(Context context) {
		// TODO Auto-generated method stub
		super.onAttach(context);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.appinfo6, container, false);
		btn_Close = (Button) view.findViewById(R.id.buttonClose);
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
		imageView = (ImageView) view.findViewById(R.id.imageView);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		options.inTempStorage = new byte[5 * 1024];

		Bitmap bmp = BitmapFactory.decodeResource(getResources(),
				AppInfo.ICONS[position], options);

		imageView.setImageBitmap(bmp);
		return view;
	};

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
