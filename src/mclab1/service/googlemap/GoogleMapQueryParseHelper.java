package mclab1.service.googlemap;

import java.util.Date;
import java.util.List;

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

public class GoogleMapQueryParseHelper extends AsyncTask<Void, Void, Void> {

	final private String TAG = "queryParseAsyncTaskTAG";

	private LatLng currentPosition;
	private double LatitudeDistance;
	private double LongitudeDistance;
	List<GoogleMapData> googleMapData = SugarRecord
			.listAll(GoogleMapData.class);

	public GoogleMapQueryParseHelper(LatLng currentPosition,
			double LatitudeDistance, double LongitudeDistance) {
		// TODO Auto-generated constructor stub
		this.currentPosition = currentPosition;
		this.LatitudeDistance = LatitudeDistance;
		this.LongitudeDistance = LongitudeDistance;

	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				query_offline(currentPosition, LatitudeDistance,
						LongitudeDistance);
				super.run();
			}
		}.start();

		new Thread() {
			public void run() {
				query_Story(currentPosition, LatitudeDistance,
						LongitudeDistance);
			};
		}.start();

		return null;
	}

	private void query_offline(LatLng current_position,
			double latitudeDistance, double longitudeDistance) {
		// TODO Auto-generated method stub
		ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>(
				"offline");
		parseQuery.setLimit(GoogleMapParseHelper.HELPER_PARSE_LIMIT);

		parseQuery.whereGreaterThan("latitude", current_position.latitude
				- latitudeDistance);
		parseQuery.whereLessThan("latitude", current_position.latitude
				+ latitudeDistance);
		parseQuery.whereGreaterThan("longitude", current_position.longitude
				- longitudeDistance);
		parseQuery.whereLessThan("longitude", current_position.longitude
				+ longitudeDistance);

		parseQuery.addDescendingOrder("createdAt");
		parseQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					if (!objects.isEmpty()) {
						for (int i = 0; i < objects.size(); i++) {
							ParseObject parseObject = objects.get(i);
							final String objectIdString = parseObject
									.getObjectId();

							final String userNameString = parseObject
									.getString("userName");
							final String userUuidString = parseObject
									.getString("userUuid");
							final String titleString = parseObject
									.getString("title");
							final int score = parseObject.getInt("score");
							final String contentString = parseObject
									.getString("content");

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
							final String SSIDString = parseObject
									.getString("SSID");
							final Date createAt = parseObject.getCreatedAt();

							ParseFile imageFile = (ParseFile) parseObject
									.get("image");
							if (imageFile != null) {
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
													
													boolean isNew = true;
													for (int listCounter = 0; listCounter < googleMapData
															.size(); listCounter++) {
														if (objectIdString
																.compareTo(googleMapData
																		.get(listCounter).objectId) == 0) {
															isNew = false;
															break;
														}
													}
													if (isNew) {
														GoogleMapData newGoogleMapData = new GoogleMapData(
																objectIdString,
																userNameString,
																userUuidString,
																titleString,
																score, bmp,
																contentString,
																latitude,
																longitude,
																state,
																SSIDString,
																createAt);
														newGoogleMapData.save();

														if (bmp != null) {
															bmp.recycle();
														}

														// LatLng point = new
														// LatLng(
														// latitude,
														// longitude);
														// addMarker_Ready(
														// objectIdString,
														// userNameString,
														// titleString,
														// point,
														// score,
														// GoogleMapFragment.TYPE_READY,
														// SSIDString);
														Log.d(TAG,
																"googleMapData size = "
																		+ googleMapData
																				.size());
													}

												} else {
													e.printStackTrace();
												}
											}
										});

							} else {
								Log.d(TAG, objectIdString
										+ " doesn't contain a picture.");
							}
						}
					} else {
						Log.d(TAG, "objects is empty.");
					}
				} else {
					e.printStackTrace();
				}
			}
		});
	}

	private void query_Story(LatLng current_position, double latitudeDistance,
			double longitudeDistance) {
		ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>(
				"story");
		parseQuery.setLimit(GoogleMapParseHelper.HELPER_PARSE_LIMIT);

		parseQuery.whereGreaterThan("latitude", current_position.latitude
				- latitudeDistance);
		parseQuery.whereLessThan("latitude", current_position.latitude
				+ latitudeDistance);
		parseQuery.whereGreaterThan("longitude", current_position.longitude
				- longitudeDistance);
		parseQuery.whereLessThan("longitude", current_position.longitude
				+ longitudeDistance);

		parseQuery.addDescendingOrder("createdAt");
		parseQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					if (!objects.isEmpty()) {
						for (int i = 0; i < objects.size(); i++) {
							ParseObject parseObject = objects.get(i);
							final String objectIdString = parseObject
									.getObjectId();

							final String userNameString = parseObject
									.getString("userName");
							final String userUuidString = parseObject
									.getString("userUuid");
							final String titleString = parseObject
									.getString("title");
							final int score = parseObject.getInt("score");
							final String contentString = parseObject
									.getString("content");

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
							final String SSIDString = parseObject
									.getString("SSID");
							final Date createAt = parseObject.getCreatedAt();

							ParseFile imageFile = (ParseFile) parseObject
									.get("image");
							if (imageFile != null) {
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
													
													boolean isNew = true;
													for (int listCounter = 0; listCounter < googleMapData
															.size(); listCounter++) {
														if (objectIdString
																.compareTo(googleMapData
																		.get(listCounter).objectId) == 0) {
															isNew = false;
															break;
														}
													}
													if (isNew) {
														GoogleMapData newGoogleMapData = new GoogleMapData(
																objectIdString,
																userNameString,
																userUuidString,
																titleString,
																score, bmp,
																contentString,
																latitude,
																longitude,
																state,
																SSIDString,
																createAt);
														newGoogleMapData.save();

														if (bmp != null) {
															bmp.recycle();
														}

														// LatLng point = new
														// LatLng(
														// latitude,
														// longitude);
														// addMarker_Ready(
														// objectIdString,
														// userNameString,
														// titleString,
														// point,
														// score,
														// GoogleMapFragment.TYPE_READY,
														// SSIDString);
														Log.d(TAG,
																"googleMapData size = "
																		+ googleMapData
																				.size());
													}

												} else {
													e.printStackTrace();
												}
											}
										});

							} else {
								Log.d(TAG, objectIdString
										+ " doesn't contain a picture.");
							}
						}
					} else {
						Log.d(TAG, "objects is empty.");
					}
				} else {
					e.printStackTrace();
				}
			}
		});
	}
}
