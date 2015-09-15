package mclab1.service.camera;

import edu.mclab1.nccu_story.R;
import mclab1.pages.UploadPage;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class DetailsActivity extends Activity {
	
	final String tag = "DetailsTAG"; 
	double longitude;
    double latitude;
    String title;
    String imagepath;
    
    DisplayMetrics displaymetrics = new DisplayMetrics();
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_camera_activity);
        
        Bundle extras = getIntent().getExtras();
		longitude = extras.getDouble("longitude");
		latitude = extras.getDouble("latitude");
		Log.d(tag, "longitude = "+longitude);
		Log.d(tag, "latitude = "+latitude);

        title = getIntent().getStringExtra("title");
        imagepath = getIntent().getStringExtra("image");

//        TextView titleTextView = (TextView) findViewById(R.id.title);
//        titleTextView.setText(title);

        ImageView imageView = (ImageView) findViewById(R.id.image);
        Bitmap bitmap = BitmapFactory.decodeFile(imagepath);
        
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        Log.d(tag, "height = "+height);
        Log.d(tag, "width = "+width);
        
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, height, width, true);
        imageView.setPadding(0, 0, 0, 0);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setRotation(90);
        imageView.setImageBitmap(resized);
        
        imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent_upload = new Intent();
				intent_upload.setClass(DetailsActivity.this, UploadPage.class);
				Bundle bundle_uploadBundle = new Bundle();
				bundle_uploadBundle.putString("path", imagepath);
				bundle_uploadBundle.putDouble("longitude", longitude);
				bundle_uploadBundle.putDouble("latitude", latitude);
				intent_upload.putExtras(bundle_uploadBundle);
				startActivity(intent_upload);
			}
		});
    }
}
