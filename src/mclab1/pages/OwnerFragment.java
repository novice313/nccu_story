package mclab1.pages;

import java.util.ArrayList;
import java.util.List;

import mclab1.custom.listview.News;
import mclab1.custom.listview.NewsAdapter;
import mclab1.sugar.Owner;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.orm.SugarRecord;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import edu.mclab1.nccu_story.R;

public class OwnerFragment extends Fragment {

	private final static String tag = "OwnerFragment";
	private int LIMIT = 10;

	public static ArrayList<News> newsList;
	public static ListView newsView;
	NewsAdapter newsAdt;

	String[] list_uploadType = { "delete" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(tag, "oncreated.");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(tag, "onCreateView");
		
		View view;

		// instantiate list
		newsList = new ArrayList<News>();

		List<Owner> owner = SugarRecord.listAll(Owner.class);
		if (owner.isEmpty()) {

			view = inflater.inflate(R.layout.fragment_owner_not_login,
					container, false);

			Toast.makeText(getActivity(), "You didn't log in before.",
					Toast.LENGTH_SHORT).show();
		} else {

			view = inflater.inflate(R.layout.fragment_news, container,
					false);
			newsView = (ListView) view.findViewById(R.id.news_list);

			final String userName = owner.get(owner.size() - 1).userName;
			// get songs from device
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					getNewsList(userName);
				}
			});
			
			// create and set adapter
			newsAdt = new NewsAdapter(getActivity().getApplicationContext(),
					newsList);

			newsView.setAdapter(newsAdt);
			newsView.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int pos, long id) {
					String objectString = newsList.get(pos).getobjectId();

					// alert window
					ShowAlertDialogAndList(objectString);

					return true;
				}
			});
		}
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		Log.d(tag, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_loginFacebook:
			List<Owner> owner = SugarRecord.listAll(Owner.class);
			if (!owner.isEmpty()) {
				final String userName = owner.get(owner.size() - 1).userName;
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						getNewsList(userName);
					}
				});
			}
		}
		return super.onOptionsItemSelected(item);
	}

	private void ShowAlertDialogAndList(final String objectIdString) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Delete");
		// 建立選擇的事件
		DialogInterface.OnClickListener ListClick = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:// broadcast
					Log.d(tag, "list_uploadType " + list_uploadType[which]
							+ " onclick");

					// delete
					ParseQuery<ParseObject> query_delete = new ParseQuery<ParseObject>(
							"story");
					query_delete.whereEqualTo("objectId", objectIdString);
					query_delete
							.findInBackground(new FindCallback<ParseObject>() {

								@Override
								public void done(List<ParseObject> objects,
										ParseException e) {
									if (e == null) {
										for (ParseObject delete : objects) {
											delete.deleteInBackground();
											Toast.makeText(
													getActivity()
															.getApplicationContext(),
													"deleted",
													Toast.LENGTH_SHORT).show();
										}
									} else {
										Toast.makeText(
												getActivity()
														.getApplicationContext(),
												"error in deleting",
												Toast.LENGTH_SHORT).show();
									}
								}

							});

					break;
				}

			}
		};
		// 建立按下取消什麼事情都不做的事件
		DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		};
		builder.setItems(list_uploadType, ListClick);
		builder.setNeutralButton("cancel", OkClick);
		builder.show();

	}

	public void getNewsList(String userName) {
		ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>(
				"story");
		// parseQuery.whereEqualTo("userName", "Jeny Zheng Lan");
		parseQuery.setLimit(LIMIT);

		parseQuery.whereEqualTo("userName", userName);
		parseQuery.addDescendingOrder("createdAt");
		parseQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if (!objects.isEmpty()) {
					for (int i = 0; i < objects.size(); i++) {
						ParseObject parseObject = objects.get(i);
						final String objectIdString = parseObject.getObjectId();
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
												// Log.d(tag, "parseFile done");
												Bitmap bmp = BitmapFactory
														.decodeByteArray(data,
																0, data.length);
												newsList.add(new News(
														objectIdString,
														userNameString,
														userUuidString,
														titleString, score,
														bmp, contentString,
														latitude, longitude));

												// redo if data change
												newsAdt.notifyDataSetChanged();
											}
										}
									});
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
		// textView.setText(tag);
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
