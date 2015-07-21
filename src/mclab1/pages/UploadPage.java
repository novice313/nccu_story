package mclab1.pages;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import mclab1.service.upload.UploadMediaListActivity;
import mclab1.sugar.Owner;

import com.example.fileexplorer.FileexplorerActivity;
import com.facebook.login.LoginManager;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import edu.mclab1.nccu_story.MainActivity;
import edu.mclab1.nccu_story.R;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

	// layout
	TextView userName;
	EditText title;
	ImageView imageView;
	EditText content;
	TextView music_path;
	Button btn_upload;
	Spinner spinner_language;
	boolean LogIn = false;
	private String[] language = { "Ch", "Eng", "Ja", "Kr" };
	private ArrayAdapter<String> languageList;

	// phone size
	private DisplayMetrics mPhone;

	// photo & gallery
	private final static int CAMERA = 66;
	private final static int PHOTO = 99;
	private final static int MEDIA = 33;
	int currentUploadMode = 0;
	Bitmap bitmap;

	// google map location
	double longitude;
	double latitude;
	
	//current user
	String userNameString = null;
	String uuidString = null;

	// temp
	String musicPath = null;

	// upload
	// image
	byte[] uploadImage = null;
	// music
	byte[] uploadMusic = null;
	// initial score
	private final int INITIAL_SCORE = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_upload);

		// 讀取手機解析度
		mPhone = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mPhone);

		userName = (TextView) findViewById(R.id.userName);
		title = (EditText) findViewById(R.id.title);
		imageView = (ImageView) findViewById(R.id.imageView);
		content = (EditText) findViewById(R.id.content);
		music_path = (TextView) findViewById(R.id.music_path);
		btn_upload = (Button) findViewById(R.id.btn_upload);
		spinner_language = (Spinner) findViewById(R.id.language_spinner);

		Bundle extras = getIntent().getExtras();
		longitude = extras.getDouble("longitude");
		latitude = extras.getDouble("latitude");
		Log.d(tag, "longitude = " + longitude);
		Log.d(tag, "latitude = " + latitude);
		// imagePath = extras.getString("path");
		// Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
		// imageView.setImageBitmap(bitmap);
		// imageView.setRotation(90);

		List<Owner> owner = Owner.listAll(Owner.class);
		
		if (owner.isEmpty()) {
			userName.setText("You didn't log in before.");
		} else {
			int currentUser = owner.size()-1;
			userNameString = owner.get(currentUser).userName;
			uuidString = owner.get(currentUser).uuid;
			userName.setText(userNameString);
			LogIn = true;
		}

		btn_upload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.d(tag, "btn_upload onclick");
				if (LogIn) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {

							if (uploadImage == null) {
								Toast.makeText(getApplicationContext(),
										"You must select one picture!",
										Toast.LENGTH_SHORT);
							} else {
								Toast.makeText(getApplicationContext(),
										"Start uploading", Toast.LENGTH_SHORT)
										.show();
								try {
									Upload();
								} catch (ParseException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					});
				} else {
					Toast.makeText(UploadPage.this,
							"You didn't log in before.", Toast.LENGTH_SHORT)
							.show();
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

	protected void Upload() throws ParseException, IOException {
		ParseFile imageFile = null;
		ParseFile musicFile = null;

		// image
		if (uploadImage != null) {
			imageFile = new ParseFile("uploadImage", uploadImage);
			imageFile.save();
			Log.d(tag, "upload imageFile complete");
		} else {
			Log.d(tag, "no image file.");
		}

		// END image

		// music
		if (musicPath != null) {
			uploadMusic = readInFile(musicPath);
			musicFile = new ParseFile("uploadMusic.mp3", uploadMusic);
			musicFile.save();
			Log.d(tag, "upload musicFile complete");
		} else {
			Log.d(tag, "no music file.");
		}
		// END music

		ParseObject uploadObject = new ParseObject("story");
		uploadObject.put("userName", userNameString);
		uploadObject.put("userUuid", uuidString);
		uploadObject.put("title", title.getText().toString());
		uploadObject
				.put("language", language[spinner_language
						.getSelectedItemPosition()].toString());
		if (imageFile != null) {
			uploadObject.put("image", imageFile);
		}
		if (musicFile != null) {
			uploadObject.put("music", musicFile);
		}
		uploadObject.put("content", content.getText().toString());
		uploadObject.put("score", INITIAL_SCORE);
		uploadObject.put("latitude", latitude);
		uploadObject.put("longitude", longitude);

		uploadObject.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_upload_page, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// menu item selected
		switch (item.getItemId()) {
		case R.id.action_camera:
			Log.d(tag, "camera icon onclick.");

			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
					.format(new Date());

			// folder stuff
			File pathFile = new File(Environment.getExternalStorageDirectory(),
					"DCIM");
			File imagesFolder = new File(pathFile, "Camera");
			imagesFolder.mkdirs();

			File image = new File(imagesFolder, "P_" + timeStamp + ".png");
			Log.d(tag, "imageFile = " + image);
			Uri uriSavedImage = Uri.fromFile(image);
			Intent intent_camera = new Intent(
					"android.media.action.IMAGE_CAPTURE");
			// save picture
			// intent_camera.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
			startActivityForResult(intent_camera, CAMERA);

			break;
		case R.id.action_gallery:
			Log.d(tag, "gallery icon onClick");

			// 開啟相簿相片集，須由startActivityForResult且帶入requestCode進行呼叫，原因為點選相片後返回程式呼叫onActivityResult
			Intent intent_gallery = new Intent();
			intent_gallery.setType("image/*");
			intent_gallery.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent_gallery, PHOTO);

			break;

		case R.id.action_media:
			Log.d(tag, "media icon onClick");

			Intent intent_media = new Intent();
			intent_media.setClass(this, UploadMediaListActivity.class);
			startActivityForResult(intent_media, MEDIA);

			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA || requestCode == PHOTO) {
			// 藉由requestCode判斷是否為開啟相機或開啟相簿而呼叫的，且data不為null
			if ((requestCode == CAMERA) && data != null) {
				bitmap = (Bitmap) data.getExtras().get("data");
				// Log.d(tag, "uri = "+data.getExtras().get);
			} else if (requestCode == PHOTO && data != null) {
				Uri uri = (Uri) data.getData();
				Log.d(tag, "uri = " + uri.getPath());
				ContentResolver cr = this.getContentResolver();
				try {
					bitmap = BitmapFactory
							.decodeStream(cr.openInputStream(uri));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			// orientation or horizontal
			// ExifInterface exif = new ExifInterface(filename);

			// calculate scale
			float mScale = ScalePic(bitmap, mPhone.heightPixels,
					mPhone.widthPixels);

			// // 判斷照片為橫向或者為直向，並進入ScalePic判斷圖片是否要進行縮放
			// if (bitmap.getWidth() > bitmap.getHeight()) {
			// ScalePic(bitmap, mPhone.heightPixels);
			// } else {
			// ScalePic(bitmap, mPhone.widthPixels);
			// }

			Matrix mMat = new Matrix();
			final ByteArrayOutputStream stream = new ByteArrayOutputStream();
			mMat.setScale(mScale, mScale);

			Bitmap mScaleBitmap = Bitmap.createBitmap(bitmap, 0, 0,
					bitmap.getWidth(), bitmap.getHeight(), mMat, false);
			imageView.setImageBitmap(mScaleBitmap);
			mScaleBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					uploadImage = stream.toByteArray();
				}
			});
		}
		if (requestCode == MEDIA) {
			musicPath = data.getExtras().getString("musicPath");
			Log.d(tag, "musicPath = " + musicPath);
			String[] temp_filePathString = musicPath.split("/");
			music_path
					.setText(temp_filePathString[temp_filePathString.length - 1]);
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private float ScalePic(Bitmap bitmap, int phone_height, int phone_width) {
		// 縮放比例預設為1
		float mScale = 1;

		// 判斷縮放比例
		if (bitmap.getWidth() > phone_width) {
			mScale = (float) phone_width / (float) bitmap.getWidth();
			Log.d(tag, "mScale = " + mScale);
		}

		else if (bitmap.getHeight() > phone_height) {
			mScale = (float) (((float) phone_height / 1.5) / (float) bitmap
					.getHeight());
			Log.d(tag, "mScale = " + mScale);
		} else {// too small situation
			float mScale_width = (float) phone_width
					/ (float) bitmap.getWidth();
			float mScale_height = (float) (((float) phone_height / 1.5) / (float) bitmap
					.getHeight());
			if (mScale_width < mScale_height) {
				mScale = mScale_width;
			} else {
				mScale = mScale_height;
			}
		}
		return mScale;

	}
}
