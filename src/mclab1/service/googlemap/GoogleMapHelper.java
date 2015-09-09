package mclab1.service.googlemap;

import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class GoogleMapHelper {

	public static String TAG = "GoogleMapHelperTAG";

	public static double meters_per_pixel[] = { 156543.03392, 78271.51696,
			39135.75848, 19567.87924, 9783.93962, 4891.96981, 2445.98490,
			1222.99245, 611.49622, 305.74811, 152.87405, 76.43702, 38.21851,
			19.10925, 9.55462, 4.77731, 2.38865, 1.19432, 0.59716, 0.29858 };

	// public GoogleMapHelper() {
	// // TODO Auto-generated constructor stub
	//
	// }

//	public static double getScreenWidth(Activity activity, double zoom_level) {
//		DisplayMetrics displayMetrics = new DisplayMetrics();
//		WindowManager wm = (WindowManager) activity
//				.getSystemService(Context.WINDOW_SERVICE); // the results will
//															// be higher than
//															// using the
//															// activity context
//															// object or the
//															// getWindowManager()
//															// shortcut
//		wm.getDefaultDisplay().getMetrics(displayMetrics);
//		int screenWidth = displayMetrics.widthPixels;
//		Double zoom = new Double(zoom_level);
//		int zoom_int = zoom.intValue();
//		Log.d(TAG, "zoom_int = " + zoom_int);
//
//		Log.d(TAG, "screenWidrg = " + screenWidth);
//		return meters_per_pixel[zoom_int] * screenWidth / 2;
//	}

	public static double getScreenWidth(Activity activity) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) activity
				.getSystemService(Context.WINDOW_SERVICE);

		wm.getDefaultDisplay().getMetrics(displayMetrics);
		int screenWidth = displayMetrics.widthPixels;

//		Log.d(TAG, "screenWidth = " + screenWidth);
		return screenWidth;
	}

	public static double getGPSdistance(Activity activity, LatLng position, double zoom) {

		double GPSdistance = 156543.03392
				* Math.cos(position.latitude * Math.PI / 180)
				/ Math.pow(2, zoom) * getScreenWidth(activity) / 4;
//		Log.d(TAG, "GPSdistance = "+GPSdistance);

		return GPSdistance;

	}
	
	public static double getGPSLatitudeDistance(Activity activity, LatLng position, double zoom){
//		Log.d(TAG, "zoom = "+zoom);
		return getGPSdistance(activity, position, zoom)*0.00000898;
	}
	
	public static double getGPSLongitudeDistance(Activity activity, LatLng position, double zoom){
//		Log.d(TAG, "position.latitude"+position.latitude);
//		Log.d(TAG, "Math.cos(position.latitude) = "+Math.cos(position.latitude));
		return getGPSdistance(activity, position, zoom)*0.00000898 / Math.cos(position.latitude*Math.PI/180);
	}

}
