package mclab1.pages;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.xml.sax.Parser;

import mclab1.sugar.Owner;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import edu.mclab1.nccu_story.MainActivity;
import edu.mclab1.nccu_story.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class UploadPage extends Activity {

	private static final String tag = "UploadPageTag";

	TextView userName;
	EditText title;
	ImageView imageView;
	EditText content;
	TextView music_path_show;
	Button btn_upload;
	Spinner spinner_language;
	boolean LogIn = false;
	private String[] language = { "Ch", "Eng", "Ja", "Kr" };
	private ArrayAdapter<String> languageList;
	
	double longitude;
	double latitude;

	// temp
	String imagePath = "";
	String musicPath = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_upload);

		userName = (TextView) findViewById(R.id.userName);
		title = (EditText) findViewById(R.id.title);
		imageView = (ImageView) findViewById(R.id.imageView);
		content = (EditText) findViewById(R.id.content);
		music_path_show = (TextView) findViewById(R.id.music_path);
		btn_upload = (Button) findViewById(R.id.btn_upload);
		spinner_language = (Spinner) findViewById(R.id.language_spinner);

		Bundle extras = getIntent().getExtras();
		longitude = extras.getDouble("longitude");
		latitude = extras.getDouble("latitude");
		Log.d(tag, "longitude = "+longitude);
		Log.d(tag, "latitude = "+latitude);
		imagePath = extras.getString("path");
		Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
		imageView.setImageBitmap(bitmap);
		imageView.setRotation(90);

		List<Owner> owner = Owner.listAll(Owner.class);
		if (owner.isEmpty()) {
			userName.setText("You didn't log in before.");
		} else {
			userName.setText(owner.get(0).userName);
			LogIn = true;
		}

		btn_upload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.d(tag, "btn_upload onclick");
				if (LogIn) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(getApplicationContext(),
									"Start uploading", Toast.LENGTH_SHORT)
									.show();
							Upload();
						}
					});
				}
				else{
					Toast.makeText(UploadPage.this, "You didn't log in before.", Toast.LENGTH_SHORT).show();
				}
				Intent intent = new Intent();
				intent.setClass(UploadPage.this, MainActivity.class);
				startActivity(intent);
			}
		});

		languageList = new ArrayAdapter<String>(UploadPage.this,
				android.R.layout.simple_spinner_item, language);
		spinner_language.setAdapter(languageList);

	}

	protected void Upload() {
		// TODO Auto-generated method stub

		File SDCardpath = Environment.getExternalStorageDirectory();
		// File temp[] = SDCardpath.listFiles();
		// for (int i = 0; i < temp.length; i++) {
		// SDCardpath.listFiles();
		// Log.d(tag, "File = " + temp[i].getAbsolutePath());
		// }
		File FilePath = new File(SDCardpath.getPath().toString() + "/"
				+ "media/audio/notifications/");
//		imagePath = FilePath.getAbsolutePath() + "/"
//				+ "Department_of_Diplomacy.png";
		musicPath = FilePath.getAbsolutePath() + "/"
				+ "facebook_ringtone_pop.m4a";

		File testFile = new File(imagePath);
		if (testFile.isFile()) {
			Log.d(tag, "imagePath = " + imagePath.toString());
		}

		// image
		byte[] uploadImage = null;
		try {
			uploadImage = readInFile(imagePath.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ParseFile imageFile = new ParseFile("uploadImage", uploadImage);
		imageFile.saveInBackground();
		Log.d(tag, "upload imageFile complete");

		// END image

		// //image
		// // Locate the image in res > drawable-hdpi
		// Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
		// // Convert it to byte
		// ByteArrayOutputStream stream = new ByteArrayOutputStream();
		// // Compress image to lower quality scale 1 - 100
		// bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		// byte[] uploadImage = stream.toByteArray();
		// // Create the ParseFile
		// ParseFile imagefile = new ParseFile("uploadImage", uploadImage);
		// // Upload the image into Parse Cloud
		// try {
		// imagefile.save();
		// Log.d(tag, "upload imageFile complete");
		// } catch (ParseException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// //END image

		byte[] uploadMusic = null;

		ParseObject uploadObject = new ParseObject("story");
		uploadObject.put("userName", userName.getText().toString());
		uploadObject.put("title", title.getText().toString());
		uploadObject
				.put("language", language[spinner_language
						.getSelectedItemPosition()].toString());
		uploadObject.put("image", imageFile);
		uploadObject.put("content", content.getText().toString());
		uploadObject.put("latitude", latitude);
		uploadObject.put("longitude", longitude);

		// music
		if (musicPath != null) {
			try {
				uploadMusic = readInFile(musicPath.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ParseFile musicFile = new ParseFile("uploadMusic", uploadMusic);
			musicFile.saveInBackground();
			Log.d(tag, "upload musicFile complete");
			uploadObject.put("music", musicFile);
		}else{
			Log.d(tag, "no music file.");
		}
		// END music

		uploadObject.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				// TODO Auto-generated method stub
				Log.d(tag, "upload complete");
				Toast.makeText(getApplicationContext(), "Upload complete",
						Toast.LENGTH_SHORT).show();
				finish();
			}
		});

	}

	// parse read in file
	private byte[] readInFile(String path) throws IOException {
		byte[] data = null;
		File file = new File(path);
		InputStream input_stream = new BufferedInputStream(new FileInputStream(
				file));
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		data = new byte[16384]; // 16K
		int bytes_read;
		while ((bytes_read = input_stream.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, bytes_read);
		}
		input_stream.close();
		return buffer.toByteArray();

	}

}
