package mclab1.pages;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import mclab1.custom.listview.News;
import mclab1.custom.listview.NewsAdapter;
import edu.mclab1.nccu_story.R;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class NewsFragment extends Fragment {

	private final static String tag = "NewsFragment";
	private int LIMIT = 10;

	public ArrayList<News> newsList;
	public ListView newsView;
	NewsAdapter newsAdt;

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
		newsView = (ListView) view.findViewById(R.id.news_list);

		// instantiate list
		newsList = new ArrayList<News>();
		// get News from parse
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getNewsList();
			}
		});

		// create and set adapter
		newsAdt = new NewsAdapter(getActivity().getApplicationContext(),
				newsList);
		newsView.setAdapter(newsAdt);
		newsView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v,
					int pos, long id) {
				Log.d(tag, "uuid = " + newsList.get(pos).getobjectId());
				Intent intent_toDetailPage = new Intent();
				intent_toDetailPage.putExtra("objectId", newsList.get(pos)
						.getobjectId());
				intent_toDetailPage.setClass(getActivity(), DetailPage.class);
				startActivity(intent_toDetailPage);
				return false;
			}
		});

		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		Log.d(tag, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);

	}

	public void getNewsList() {
		ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>(
				"story");
		// parseQuery.whereEqualTo("userName", "Jeny Zheng Lan");
		parseQuery.setLimit(LIMIT);
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
													Bitmap bmp = BitmapFactory
															.decodeByteArray(
																	data, 0,
																	data.length);
													newsList.add(new News(
															objectIdString,
															userNameString,
															userUuidString,
															titleString, score,
															bmp, contentString,
															latitude, longitude));

													newsAdt.notifyDataSetChanged();
												}
											}
										});
							}
						}
					}
				}
			}
		});
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

}
