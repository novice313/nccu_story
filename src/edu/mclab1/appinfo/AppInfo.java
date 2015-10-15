package edu.mclab1.appinfo;

import com.viewpagerindicator.LinePageIndicator;
import com.viewpagerindicator.PageIndicator;

import edu.mclab1.nccu_story.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class AppInfo extends FragmentActivity {

	private static final String TAG = "AppInfoTAG";
	public static String EULA_PREFIX = "appinfo";
	public static PackageInfo versionInfo;
	// private Context mContext;

	protected static final int[] ICONS = new int[] { R.drawable.appinfo1,
			R.drawable.appinfo2, R.drawable.appinfo3, R.drawable.appinfo4,
			R.drawable.appinfo5, R.drawable.appinfo6
	// R.drawable.background1
	};

	// indicator
	TestFragmentAdapter mAdapter;
	ViewPager mPager;
	PageIndicator mIndicator;

	// public AppInfo(Activity context) {
	// mContext = context;
	// }
	public AppInfo() {

	}

	// private PackageInfo getPackageInfo() {
	// PackageInfo info = null;
	// try {
	// info = mContext.getPackageManager().getPackageInfo(
	// mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
	// } catch (PackageManager.NameNotFoundException e) {
	// e.printStackTrace();
	// }
	// return info;
	// }
	private PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(),
					PackageManager.GET_ACTIVITIES);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return info;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		versionInfo = getPackageInfo();

		final String eulaKey = EULA_PREFIX + versionInfo.versionCode;
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		boolean bAlreadyAccepted = prefs.getBoolean(eulaKey, false);
		Log.d(TAG, "bAlreadyAccepted = " + bAlreadyAccepted);
		if (bAlreadyAccepted == false) {
			setContentView(R.layout.themed_lines);

			mAdapter = new TestFragmentAdapter(getSupportFragmentManager());

			mPager = (ViewPager) findViewById(R.id.pager);
			mPager.setAdapter(mAdapter);

			mIndicator = (LinePageIndicator) findViewById(R.id.indicator);
			mIndicator.setViewPager(mPager);
		} else {
			Log.d(TAG, "AppInfo finish()");
			this.finish();
		}
	}
}
