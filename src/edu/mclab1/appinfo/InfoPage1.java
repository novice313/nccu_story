package edu.mclab1.appinfo;

import edu.mclab1.nccu_story.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class InfoPage1 extends Fragment {

	private ImageView imageView;
	private int position;

	public InfoPage1(int position) {
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
		View view = inflater.inflate(R.layout.appinfo1, container, false);
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
