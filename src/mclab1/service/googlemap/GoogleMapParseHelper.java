package mclab1.service.googlemap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mclab1.custom.listview.GoogleMapSearch;
import mclab1.custom.listview.News;
import mclab1.pages.GoogleMapFragment;
import mclab1.sugar.GoogleMapData;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.orm.SugarRecord;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class GoogleMapParseHelper {

	public static final String TAG = "GoogleMapParseHelper";
	final public static int HELPER_PARSE_LIMIT = 100;

	public GoogleMapParseHelper() {
		// TODO Auto-generated constructor stub
	}

	public static void search_offline(String condition) {
		ParseQuery<ParseObject> temp1 = new ParseQuery<ParseObject>("offline");
		temp1.whereEqualTo("userName", condition);
		ParseQuery<ParseObject> temp2 = new ParseQuery<ParseObject>("offline");
		temp2.whereEqualTo("title", condition);
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

														if (bmp != null) {
															bmp.recycle();
														}
														GoogleMapFragment.googleMapSearchAdt
																.notifyDataSetChanged();
													}
												}
											});
								}
							}
						}
					}
					// END search onlineStory task
				}// END e ==null
			}
		});

		// query story
		search_story(condition);

	}

	public static void search_story(String condition) {
		ParseQuery<ParseObject> temp1 = new ParseQuery<ParseObject>("story");
		temp1.whereEqualTo("userName", condition);
		ParseQuery<ParseObject> temp2 = new ParseQuery<ParseObject>("story");
		temp2.whereEqualTo("title", condition);
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

														if (bmp != null) {
															bmp.recycle();
														}
														GoogleMapFragment.googleMapSearchAdt
																.notifyDataSetChanged();
													}
												}
											});
								}
							}
						}
					}
					// END search onlineStory task
				}// END e ==null
			}
		});

	}

}
