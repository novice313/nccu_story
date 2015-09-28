package mclab1.service.googlemap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.security.auth.PrivateCredentialPermission;

import mclab1.custom.listview.GoogleMapSearch;
import mclab1.custom.listview.News;
import mclab1.pages.GoogleMapFragment;
import mclab1.sugar.GoogleMapData;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.orm.SugarRecord;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class GoogleMapParseHelper extends AsyncTask<Void, Void, Void> {

	public static final String TAG = "GoogleMapParseHelper";
	final public static int HELPER_PARSE_LIMIT = 20;
	final private static int SEARCH_WAIT_FOR_RESULT = 1;// sec
	static ProgressDialog dialog;
	Context mContext;
	String condition;

	public GoogleMapParseHelper(Context mContext, String condition) {
		// TODO Auto-generated constructor stub
		this.mContext = mContext;
		this.condition = condition;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		dialog = ProgressDialog.show(mContext, "讀取中", "如等待過久請確認網路...", true);
		dialog.setCanceledOnTouchOutside(false);
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		search_offline(mContext, condition);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}

	public static void search_offline(Context mContext, String condition) {

		ParseQuery<ParseObject> temp1 = new ParseQuery<ParseObject>("offline");
		temp1.whereContains("userName", condition);
		ParseQuery<ParseObject> temp2 = new ParseQuery<ParseObject>("offline");
		temp2.whereContains("title", condition);
		List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
		queries.add(temp1);
		queries.add(temp2);
		ParseQuery<ParseObject> parseQuery = ParseQuery.or(queries);

		// parseQuery.whereEqualTo("State", "Ready");
		parseQuery.setLimit(HELPER_PARSE_LIMIT);
		parseQuery.addDescendingOrder("createAt");
		parseQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					dialog.dismiss();
					if (!objects.isEmpty()) {
						for (int i = 0; i < objects.size(); i++) {
							boolean isNew = true;
							ParseObject parseObject = objects.get(i);
							final String objectIdString = parseObject
									.getObjectId();

							for (int listCounter = 0; listCounter < GoogleMapFragment.searchList
									.size(); listCounter++) {
								if (objectIdString
										.compareTo(GoogleMapFragment.searchList
												.get(listCounter).getobjectId()) == 0) {
									isNew = false;
									break;
								}
							}
							if (isNew) {
								final String userNameString = parseObject
										.getString("userName");
								final String userUuidString = parseObject
										.getString("userUuid");
								final String titleString = parseObject
										.getString("title");
								final int score = parseObject.getInt("score");
								final String contentString = parseObject
										.getString("content");
								final String SSIDString = parseObject
										.getString("SSID");

								final double latitude = parseObject
										.getDouble("latitude");
								final double longitude = parseObject
										.getDouble("longitude");
								final String stateString = parseObject
										.getString("State");
								int typeCounter;
								for (typeCounter = 0; typeCounter < GoogleMapFragment.TYPE.length; typeCounter++) {
									if (stateString
											.compareTo(GoogleMapFragment.TYPE[typeCounter]) == 0) {
										break;
									}
								}
								final int state = typeCounter;

								ParseFile imageFile = (ParseFile) parseObject
										.get("image");
								if (imageFile != null) {
									Log.d(TAG, "parseObjectId = "
											+ objectIdString);
									imageFile
											.getDataInBackground(new GetDataCallback() {

												@Override
												public void done(byte[] data,
														ParseException e) {
													if (e == null) {
														// Log.d(tag,
														// "parseFile done");

														BitmapFactory.Options opt = null;
														opt = new BitmapFactory.Options();
														opt.inSampleSize = 8;
														Bitmap bmp = BitmapFactory
																.decodeByteArray(
																		data,
																		0,
																		data.length,
																		opt);
														GoogleMapFragment.searchList
																.add(new GoogleMapSearch(
																		objectIdString,
																		userNameString,
																		userUuidString,
																		titleString,
																		score,
																		bmp,
																		contentString,
																		latitude,
																		longitude,
																		state,
																		SSIDString));
														GoogleMapFragment.googleMapSearchAdt
																.notifyDataSetChanged();

														if (bmp != null) {
															bmp.recycle();
														}
													}
												}
											});
								} else {
									Bitmap bmp = null;
									GoogleMapFragment.searchList
											.add(new GoogleMapSearch(
													objectIdString,
													userNameString,
													userUuidString,
													titleString, score, bmp,
													contentString, latitude,
													longitude, state,
													SSIDString));
								}

								GoogleMapFragment.googleMapSearchAdt
										.notifyDataSetChanged();
							}
						}
					}
					// END search onlineStory task
				}// END e ==null
			}
		});

		// query story
		search_story(mContext, condition);

	}

	public static void search_story(Context Context, String condition) {
		final Context mContext = Context;
		ParseQuery<ParseObject> temp1 = new ParseQuery<ParseObject>("story");
		temp1.whereContains("userName", condition);
		ParseQuery<ParseObject> temp2 = new ParseQuery<ParseObject>("story");
		temp2.whereContains("title", condition);
		List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
		queries.add(temp1);
		queries.add(temp2);
		ParseQuery<ParseObject> parseQuery = ParseQuery.or(queries);

		// parseQuery.whereEqualTo("State", "Ready");
		parseQuery.setLimit(HELPER_PARSE_LIMIT);
		parseQuery.addDescendingOrder("createAt");
		parseQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					if (!objects.isEmpty()) {
						if (dialog.isShowing()) {
							dialog.dismiss();
						}
						for (int i = 0; i < objects.size(); i++) {
							boolean isNew = true;
							ParseObject parseObject = objects.get(i);
							final String objectIdString = parseObject
									.getObjectId();

							for (int listCounter = 0; listCounter < GoogleMapFragment.searchList
									.size(); listCounter++) {
								if (objectIdString
										.compareTo(GoogleMapFragment.searchList
												.get(listCounter).getobjectId()) == 0) {
									isNew = false;
									break;
								}
							}
							if (isNew) {
								final String userNameString = parseObject
										.getString("userName");
								final String userUuidString = parseObject
										.getString("userUuid");
								final String titleString = parseObject
										.getString("title");
								final int score = parseObject.getInt("score");
								final String contentString = parseObject
										.getString("content");
								final String SSIDString = parseObject
										.getString("SSID");

								final double latitude = parseObject
										.getDouble("latitude");
								final double longitude = parseObject
										.getDouble("longitude");
								final String stateString = parseObject
										.getString("State");
								int typeCounter;
								for (typeCounter = 0; typeCounter < GoogleMapFragment.TYPE.length; typeCounter++) {
									if (stateString
											.compareTo(GoogleMapFragment.TYPE[typeCounter]) == 0) {
										break;
									}
								}
								final int state = typeCounter;

								ParseFile imageFile = (ParseFile) parseObject
										.get("image");
								if (imageFile != null) {
									Log.d(TAG, "parseObjectId = "
											+ objectIdString);
									imageFile
											.getDataInBackground(new GetDataCallback() {

												@Override
												public void done(byte[] data,
														ParseException e) {
													if (e == null) {
														// Log.d(tag,
														// "parseFile done");

														BitmapFactory.Options opt = null;
														opt = new BitmapFactory.Options();
														opt.inSampleSize = 8;
														Bitmap bmp = BitmapFactory
																.decodeByteArray(
																		data,
																		0,
																		data.length,
																		opt);
														GoogleMapFragment.searchList
																.add(new GoogleMapSearch(
																		objectIdString,
																		userNameString,
																		userUuidString,
																		titleString,
																		score,
																		bmp,
																		contentString,
																		latitude,
																		longitude,
																		state,
																		SSIDString));
														GoogleMapFragment.googleMapSearchAdt
																.notifyDataSetChanged();

														if (bmp != null) {
															bmp.recycle();
														}
													}
												}
											});
								} else {
									Bitmap bmp = null;
									GoogleMapFragment.searchList
											.add(new GoogleMapSearch(
													objectIdString,
													userNameString,
													userUuidString,
													titleString, score, bmp,
													contentString, latitude,
													longitude, state,
													SSIDString));
								}
								GoogleMapFragment.googleMapSearchAdt
										.notifyDataSetChanged();
							}
						}
					}
					if (GoogleMapFragment.searchList.isEmpty()) {
						if (dialog.isShowing()) {
							dialog.dismiss();
						}
						Toast.makeText(mContext, "No match element.",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(mContext, "Click the result you want.",
								Toast.LENGTH_SHORT).show();
					}

					// END search onlineStory task
				}// END e ==null
			}
		});

	}

}
