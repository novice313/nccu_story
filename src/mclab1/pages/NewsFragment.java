package mclab1.pages;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ro.ui.pttdroid.Globalvariable;

import mclab1.custom.listview.News;
import mclab1.custom.listview.NewsAdapter;
import android.R.integer;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.mclab1.palace.customer.CustomerDetailActivity;
import com.paging.listview.PagingListView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import edu.mclab1.nccu_story.R;

public class NewsFragment extends Fragment {

	private final static String tag = "NewsFragment";
	private static int LIMIT = 10;
	private static final boolean IsNoPictureShow = true;
	private static final int PagingMaxSize = 3;

	int Paging = 0;

	public static ArrayList<News> newsList;
	public PagingListView newsView;
	static NewsAdapter newsAdt;
	MenuItem refresh;
	private Context mContext;
	ProgressDialog dialog;

	@Override
	public void onAttach(Context context) {
		// TODO Auto-generated method stub
		this.mContext = context;
		super.onAttach(context);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(tag, "oncreated.");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(tag, "onCreateView");
		View view = inflater.inflate(R.layout.fragment_news, container, false);
		newsView = (PagingListView) view.findViewById(R.id.news_list);

		// set recorder icon at action bar
		setHasOptionsMenu(true);

		// instantiate list
		newsList = new ArrayList<News>();
		// get News from parse
		// getActivity().runOnUiThread(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// getNewsList();
		// }
		// });

		// create and set adapter
		newsAdt = new NewsAdapter(getActivity().getApplicationContext(),
				newsList);
		newsView.setAdapter(newsAdt);
		newsView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v,
					int pos, long id) {
				Log.d(tag, "uuid = " + newsList.get(pos).getobjectId());

				if (MediaPlayerFragment.musicSrv != null) {
					MediaPlayerFragment.musicSrv.pausePlayer();
				}

				String state = newsList.get(pos).getState();
				if (state.compareTo(GoogleMapFragment.TYPE[1]) == 0) {
					Intent intent_toDetailPage = new Intent();
					intent_toDetailPage.putExtra("objectId", newsList.get(pos)
							.getobjectId());
					intent_toDetailPage.setClass(getActivity(),
							DetailPage.class);
					startActivity(intent_toDetailPage);
				} else if (state.compareTo(GoogleMapFragment.TYPE[2]) == 0) {
					// dialog
					dialog = ProgressDialog.show(mContext, "讀取中",
							"如等待過久請確認網路...", true);
					dialog.setCanceledOnTouchOutside(false);

					final double latitude = newsList.get(pos).getlatitude();
					final double longitude = newsList.get(pos).getlongitude();
					ParseQuery<ParseObject> query = ParseQuery
							.getQuery("offline");
					// Retrieve the object by id
					query.getInBackground(newsList.get(pos).getobjectId(),
							new GetCallback<ParseObject>() { // 以後博要給我object ID
								@Override
								public void done(ParseObject offline,
										ParseException e) {
									dialog.dismiss();

									if (e == null) {
										Globalvariable.titleString = (String) offline
												.get("title");
										Globalvariable.contentString = (String) offline
												.get("content");
										Globalvariable.latitude = latitude;
										Globalvariable.longitude = longitude;
										System.out.println("Globalvariable"
												+ Globalvariable.titleString);
										System.out.println("Globalvariable"
												+ Globalvariable.contentString);
										Intent intent = new Intent(mContext,
												CustomerDetailActivity.class);
										mContext.startActivity(intent);

									} else {
										e.printStackTrace();
									}
								}
							});
				}
				return false;
			}
		});

		newsView.setHasMoreItems(true);
		newsView.setPagingableListener(new PagingListView.Pagingable() {
			@Override
			public void onLoadMoreItems() {
				if (Paging < PagingMaxSize) {
					new NewsAsyncTask(LIMIT, IsNoPictureShow).execute();
				} else {
					newsView.onFinishLoading(false, null);
				}
			}
		});
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
		refresh = menu.add("refresh");
		refresh.setIcon(R.drawable.ic_refresh).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM);
		refresh.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				newsList.clear();
				getNewsList();
				return false;
			}
		});

	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		Log.d(tag, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);

	}

	public void getNewsList() {
		new NewsAsyncTask(LIMIT, IsNoPictureShow).execute();
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(tag, "onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(tag, "onStop.");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d(tag, "onDestroyView.");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(tag, "onDestroy.");
	}

	class NewsAsyncTask extends AsyncTask<Void, Void, Void> {

		private int LIMIT;
		private boolean IsNoPictureShow;

		public NewsAsyncTask(int limit, boolean IsNoPictureShow) {
			// TODO Auto-generated constructor stub
			this.LIMIT = limit;
			this.IsNoPictureShow = IsNoPictureShow;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>(
					"story");
			// parseQuery.whereEqualTo("userName", "Jeny Zheng Lan");
			parseQuery.setLimit(LIMIT);
			// parseQuery.setSkip(LIMIT * Paging);
			parseQuery.whereExists("image");
			parseQuery.addDescendingOrder("createdAt");
			parseQuery.findInBackground(new FindCallback<ParseObject>() {

				@Override
				public void done(List<ParseObject> objects, ParseException e) {
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
								final Date createdAt = parseObject
										.getCreatedAt();

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

														// fix out of memory
														// problem
														BitmapFactory.Options options = new BitmapFactory.Options();
														options.inSampleSize = 2;
														options.inTempStorage = new byte[5 * 1024];

														Bitmap bmp = BitmapFactory
																.decodeByteArray(
																		data,
																		0,
																		data.length,
																		options);
														NewsFragment.newsList
																.add(new News(
																		objectIdString,
																		userNameString,
																		userUuidString,
																		titleString,
																		score,
																		bmp,
																		contentString,
																		latitude,
																		longitude,
																		stateString,
																		createdAt));
														NewsFragment.newsAdt
																.notifyDataSetChanged();
													}
												}
											});
								} else {
									if (IsNoPictureShow) {
										Bitmap bmp = null;
										NewsFragment.newsList.add(new News(
												objectIdString, userNameString,
												userUuidString, titleString,
												score, bmp, contentString,
												latitude, longitude,
												stateString, createdAt));
									}
								}
								NewsFragment.newsAdt.notifyDataSetChanged();
							}
						}
					}
				}

			});

			ParseQuery<ParseObject> parseQuery_offline = new ParseQuery<ParseObject>(
					"offline");
			// parseQuery.whereEqualTo("userName", "Jeny Zheng Lan");
			parseQuery_offline.setLimit(LIMIT);
			// parseQuery_offline.setSkip(LIMIT * Paging);
			parseQuery_offline.whereExists("image");
			parseQuery_offline.whereEqualTo("State", "offline");
			parseQuery_offline.addDescendingOrder("createdAt");
			parseQuery_offline
					.findInBackground(new FindCallback<ParseObject>() {

						@Override
						public void done(List<ParseObject> objects,
								ParseException e) {
							if (e == null) {
								if (!objects.isEmpty()) {
									for (int i = 0; i < objects.size(); i++) {
										ParseObject parseObject = objects
												.get(i);
										final String objectIdString = parseObject
												.getObjectId();
										final String userNameString = parseObject
												.getString("userName");
										final String userUuidString = parseObject
												.getString("userUuid");
										final String titleString = parseObject
												.getString("title");
										final int score = parseObject
												.getInt("score");
										final String contentString = parseObject
												.getString("content");

										final double latitude = parseObject
												.getDouble("latitude");
										final double longitude = parseObject
												.getDouble("longitude");
										final String stateString = parseObject
												.getString("State");
										final Date createdAt = parseObject
												.getCreatedAt();

										ParseFile imageFile = (ParseFile) parseObject
												.get("image");
										if (imageFile != null) {
											imageFile
													.getDataInBackground(new GetDataCallback() {

														@Override
														public void done(
																byte[] data,
																ParseException e) {
															if (e == null) {
																// Log.d(tag,
																// "parseFile done");

																// fix out of
																// memory
																// problem
																BitmapFactory.Options options = new BitmapFactory.Options();
																options.inSampleSize = 2;
																options.inTempStorage = new byte[5 * 1024];

																Bitmap bmp = BitmapFactory
																		.decodeByteArray(
																				data,
																				0,
																				data.length,
																				options);
																NewsFragment.newsList
																		.add(new News(
																				objectIdString,
																				userNameString,
																				userUuidString,
																				titleString,
																				score,
																				bmp,
																				contentString,
																				latitude,
																				longitude,
																				stateString,
																				createdAt));
																NewsFragment.newsAdt
																		.notifyDataSetChanged();
															}
														}
													});
										} else {
											if (IsNoPictureShow) {
												Bitmap bmp = null;
												NewsFragment.newsList
														.add(new News(
																objectIdString,
																userNameString,
																userUuidString,
																titleString,
																score, bmp,
																contentString,
																latitude,
																longitude,
																stateString,
																createdAt));
											}
										}
										NewsFragment.newsAdt
												.notifyDataSetChanged();
									}
								}
							}
						}

					});

			return null;
		};

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			Paging++;
			newsView.onFinishLoading(false, null);
			super.onPostExecute(result);
		}
	}

}