package ro.ui.pttdroid;




//import mclab1.service.upload.UploadMediaListActivity;
//import mclab1.sugar.Owner;




import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import mclab1.sugar.Owner;

import com.mclab1.palaca.parsehelper.ParseHelper;
import com.mclab1.palace.customer.CustomerActivityGlobal;
import com.orm.SugarRecord;
import com.parse.FindCallback;
//import com.example.fileexplorer.FileexplorerActivity;
//import com.facebook.login.LoginManager;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;











import android.annotation.SuppressLint;
import android.app.ActionBar;
//import edu.mclab1.nccu_story.MainActivity;
//import edu.mclab1.nccu_story.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
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

import com.mclab1.palaca.parsehelper.ParseHelper;
import com.orm.SugarRecord;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import edu.mclab1.nccu_story.R;

public class UploadPage extends Activity {

	private static final String tag = "UploadPageTag";
	
	Boolean LogIn=false;
	// layout
	TextView userName;
	EditText title;
	EditText content;
	ImageView imageView;
	TextView music_path;
	Button btn_upload;
	Spinner spinner_language;                              //spinner_language for 語言
	//boolean LogIn = false;
	//private String[] language = { "Ch", "Eng", "Ja", "Kr" };
	private ArrayAdapter<String> languageList;
	WifiInfo wifiinfo ;
	static String wifiinfo_getSSID;
	WifiManager wifi_service;
	String userNameString = null;


	// phone size
	private DisplayMetrics mPhone;

	// photo & gallery
	private final static int CAMERA = 66;
	private final static int PHOTO = 99;
	private final static int MEDIA = 33;
	int currentUploadMode = 0;
	Bitmap bitmap;

	// google map location
	String longitude="longitude";
	String latitude="latitude";
	String GuiderID="GuiderID";
    String SSID="SSID";
    String Uuid="Uuid";
    String uuid;
	// temp
	String musicPath = null;

	// upload
	// image
	byte[] uploadImage = null;
	// music
	byte[] uploadMusic = null;
	// initial score
	private final int INITIAL_SCORE = 0;
	public static double latitudeString=24.98;
	public static double longitudeString=121.575;
	ProgressDialog dialog;
	ProgressDialog dialog_title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_upload_offline);
		ParseHelper.initParse(this);
		
		getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.bar));  //標題配色
    	ActionBar actionBar = getActionBar();
    	actionBar.setDisplayHomeAsUpEnabled(true);
		
		System.out.println("UploadPage");    //conflict過
		Bundle extras = getIntent().getExtras();
		longitudeString = extras.getDouble("longitude");
    	latitudeString = extras.getDouble("latitude");
		
		 wifi_service = (WifiManager) getSystemService(WIFI_SERVICE);
		 wifiinfo = wifi_service.getConnectionInfo();
		 System.out.println("getBssid"+wifiinfo.getBSSID()+wifiinfo.getSSID());
		 wifiinfo_getSSID=wifiinfo.getSSID();
		 Globalvariable.getGlobalSSID=wifiinfo_getSSID;   //得到global ssid


		// 讀取手機解析度
		mPhone = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mPhone);

		userName = (TextView) findViewById(R.id.username);
		title = (EditText) findViewById(R.id.title);
		content = (EditText) findViewById(R.id.content);
		//spinner_language = (Spinner) findViewById(R.id.language_spinner);
		imageView = (ImageView) findViewById(R.id.imageView);
		//music_path = (TextView) findViewById(R.id.music_path);
		btn_upload = (Button) findViewById(R.id.btn_upload);
		
		
		List<Owner> owner = SugarRecord.listAll(Owner.class);   //可用此得知FB的名稱

		if (owner.isEmpty()) {
			userName.setText("你還未登入");
		} else {
			int currentUser = owner.size() - 1;
			userNameString = owner.get(currentUser).userName;
			//uuidString = owner.get(currentUser).uuid;
			userName.setText(userNameString);      //顯示FB名稱
			LogIn = true;
		}

		//Bundle extras = getIntent().getExtras();
		//longitude = extras.getDouble("longitude");
		//latitude = extras.getDouble("latitude");
		// imagePath = extras.getString("path");
		// Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
		// imageView.setImageBitmap(bitmap);
		// imageView.setRotation(90);

		//List<Owner> owner = Owner.listAll(Owner.class);

		if(LogIn==true){//暫時改
		btn_upload.setOnClickListener(new OnClickListener() {
		   
			@Override
			public void onClick(View arg0) {
				System.out.println("btn_upload onclick"+title.getText().toString().isEmpty()+content.getText().toString().isEmpty());
					runOnUiThread(new Runnable() {

						@Override
						public void run() {

							if(title.getText().toString().isEmpty() || content.getText().toString().isEmpty()){
								
					           /* dialog_title = ProgressDialog.show(UploadPage.this,
					                    "注意", "你必須要輸入標題和廣播內容！",true);
					            try {
									Thread.sleep(3000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
					            dialog_title.dismiss();
					            */
							} else {
							
						
								try {
									btn_upload.setEnabled(false); 
									dialog=  ProgressDialog.show(UploadPage.this,
							               "正在上傳資料中", "請 稍 等 . . . . ",true);
							        dialog.show();
							        
									 Upload();
								} catch (ParseException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
							
					   }
						}
					});
			    uuid = UUID.randomUUID().toString(); 

			}
		});
		}
		else{
			Toast.makeText(getApplicationContext(), "你還未登入......請登入FB..才能上傳",
					Toast.LENGTH_LONG).show();
			
		}

		//languageList = new ArrayAdapter<String>(UploadPage.this,
		//		android.R.layout.simple_spinner_item, language);
		//spinner_language.setAdapter(languageList);
		

	}
	
	@Override
	public void onResume(){
		super.onResume();
		System.out.println("Resume");

		
	}
	@Override
	public void onStop(){
		super.onStop();
		
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
		
		//Globalvariable.guiderid = UUID.randomUUID().toString(); 
		System.out.println("BuildSERIAL"+Globalvariable.BuildSERIAL);
		// END image
		ParseObject uploadObject = new ParseObject("offline");
		uploadObject.put("userName", userName.getText().toString());
		uploadObject.put("title", title.getText().toString());
		//uploadObject
		//		.put("language", language[spinner_language
		//				.getSelectedItemPosition()].toString());
		if (imageFile != null) {
			uploadObject.put("image", imageFile);
		}
		
		Globalvariable.guider_latitudeString = latitudeString;   //為了要對照註冊ip給使用者用
		Globalvariable.guider_longitudeString=longitudeString;
		uploadObject.put("content", content.getText().toString());
		uploadObject.put(SSID, wifiinfo.getSSID());
		uploadObject.put(latitude, latitudeString);
		uploadObject.put(longitude, longitudeString);    //取得UUID後，上傳parse，為了offfline和Broadcast的結合
		uploadObject.put(GuiderID,Globalvariable.BuildSERIAL);
		
		uploadObject.put("score", INITIAL_SCORE);
		//uploadObject.put("latitude", latitude);
		//uploadObject.put("longitude", longitude);

		uploadObject.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				Log.d(tag, "upload complete");
				Toast.makeText(getApplicationContext(), "Upload complete",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(UploadPage.this, Main.class);
				startActivity(intent);
				finish();
				dialog.dismiss();
			}
		});
		
		
		
		

	}

	// parse read in file
//	private byte[] readInFile(String path) throws IOException {
//		byte[] data = null;
//		File file = new File(path);
//		InputStream input_stream = new BufferedInputStream(new FileInputStream(
//				file));
//		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//		data = new byte[16384]; // 16K
//		int bytes_read;
//		while ((bytes_read = input_stream.read(data, 0, data.length)) != -1) {
//			buffer.write(data, 0, bytes_read);
//		}
//		input_stream.close();
//		return buffer.toByteArray();
//
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_upload_page_offline, menu);
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
			
		case android.R.id.home:
			finish();

		/*
		case R.id.action_media:
			Log.d(tag, "media icon onClick");

			Intent intent_media = new Intent();
			intent_media.setClass(this, UploadMediaListActivity.class);
			startActivityForResult(intent_media, MEDIA);

			break;
		*/
		}
		return super.onOptionsItemSelected(item);
	}

	
	
	
	private static int exifToDegrees(int exifOrientation) {        
	    if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; } 
	    else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; } 
	    else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }            
	    return 0;    
	 }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((requestCode == CAMERA || requestCode == PHOTO) && data != null) {
			// 藉由requestCode判斷是否為開啟相機或開啟相簿而呼叫的，且data不為null
			if ((requestCode == CAMERA)) {
				bitmap = (Bitmap) data.getExtras().get("data");
				// Log.d(tag, "uri = "+data.getExtras().get);
			} else if (requestCode == PHOTO) {
				Uri uri = data.getData();
				Log.d(tag, "uri = " + uri.getPath());
				
				
				// orientation or horizontal
				Matrix matrix = new Matrix();
				try {
					//Log.d(tag, "getFileDescriptor = "+openFile(uri).getFileDescriptor());
					ExifInterface exif = new ExifInterface(uri.getPath());
					int rotation = exif.getAttributeInt(
							ExifInterface.TAG_ORIENTATION,
							ExifInterface.ORIENTATION_NORMAL);
					int rotationInDegrees = exifToDegrees(rotation);
					Log.d(tag, "rotation = "+rotation);
					Log.d(tag, "rotationInDegrees = "+rotationInDegrees);
					if (rotation != 0f) {
						matrix.preRotate(rotationInDegrees);
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// END orientation or horizontal

				//adjust picture size 
				BitmapFactory.Options options = new BitmapFactory.Options();  
				options.inSampleSize=2;//图片高宽度都为原来的二分之一，即图片大小为原来的大小的四分之一  
				options.inTempStorage = new byte[5*1024];
				
				// uri to bitmap
				ContentResolver cr = this.getContentResolver();
				try {
					bitmap = BitmapFactory
							.decodeStream(cr.openInputStream(uri), null, options);
//					Bitmap adjustedBitmap = Bitmap
//							.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
//									bitmap.getHeight(), matrix, true);
//					bitmap = adjustedBitmap;
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			if(bitmap.getWidth()+50>=bitmap.getHeight()){
				System.out.println("bitmap"+bitmap.getWidth()+" "+bitmap.getHeight());
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
			else{
				System.out.println("照片不好，選別張");
		          Toast toast = Toast.makeText(getApplicationContext(),
		        		  "照片不好，選別張!", Toast.LENGTH_SHORT);
		        		                  //顯示Toast
		        		                  toast.show();
				

			
			}
		}
		if (requestCode == MEDIA && data != null) {
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
			mScale = phone_width / (float) bitmap.getWidth();
			Log.d(tag, "mScale = " + mScale);
		}

		else if (bitmap.getHeight() > phone_height) {
			mScale = (float) ((phone_height / 1.5) / bitmap
					.getHeight());
			Log.d(tag, "mScale = " + mScale);
		} else {// too small situation
			float mScale_width = phone_width
					/ (float) bitmap.getWidth();
			float mScale_height = (float) ((phone_height / 1.5) / bitmap
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
