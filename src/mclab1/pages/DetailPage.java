package mclab1.pages;

import java.util.List;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import edu.mclab1.nccu_story.R;
import android.R.integer;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailPage extends Activity{
	
	private final static String tag = "DetailPageTag";
	private String objectId;
	TextView userNameTextView, titleTextView, contentTextView;
	ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_listview);
		
		userNameTextView = (TextView) findViewById(R.id.userName);
		titleTextView = (TextView) findViewById(R.id.title);
		contentTextView = (TextView) findViewById(R.id.userContent);
		imageView = (ImageView) findViewById(R.id.imageView);
		
		
		//
		Bundle extras = getIntent().getExtras();
		objectId = extras.getString("objectId");
		
		//start query
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				startQuery();
			}
		});
		
		
		
		
	}

	private void startQuery() {
		// TODO Auto-generated method stub
		ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>(
				"story");
		parseQuery.whereEqualTo("objectId", objectId);
		parseQuery.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if(e==null){
					ParseObject detailObject = objects.get(0);
					String userName = detailObject.getString("userName");
					String userUuid = detailObject.getString("Uuid");
					String title = detailObject.getString("title");
					String content = detailObject.getString("content");
					int score = detailObject.getInt("score");
					String languageString = detailObject.getString("language");
					
					userNameTextView.setText(userName);
					titleTextView.setText(title);
					contentTextView.setText(content);
					
					ParseFile imageFile = (ParseFile) detailObject
							.get("image");
					if (imageFile!=null) {
						imageFile.getDataInBackground(new GetDataCallback() {
							
							@Override
							public void done(byte[] data, ParseException e) {
								// TODO Auto-generated method stub
								Bitmap bmp = BitmapFactory
										.decodeByteArray(data,
												0, data.length);
								
								imageView.setImageBitmap(bmp);
							}
						});
					}
					
					ParseFile musicFile = (ParseFile) detailObject
							.get("music");
					if(musicFile!=null){
						musicFile.getDataInBackground(new GetDataCallback() {
							
							@Override
							public void done(byte[] data, ParseException e) {
								// TODO Auto-generated method stub
								
							}
						});
					}
					
				}else{
					e.printStackTrace();
				}
			}
		});
	}

}
