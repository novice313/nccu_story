package mclab1.service.camera;

import java.util.ArrayList;

import edu.mclab1.nccu_story.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;


public class GalleryActivity extends Activity {
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    
    double longitude;
    double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        
        Bundle extras = getIntent().getExtras();
		longitude = extras.getDouble("longitude");
		latitude = extras.getDouble("latitude");

        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, getData());
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);

                //Create intent
                Intent intent = new Intent(GalleryActivity.this, DetailsActivity.class);
                intent.putExtra("title", item.getTitle());
                intent.putExtra("image", item.getImage());
                
    			Bundle bundle_uploadStory = new Bundle();
    			bundle_uploadStory.putDouble("longitude", longitude);
    			bundle_uploadStory.putDouble("latitude", latitude);
    			//將Bundle物件assign給intent
    			intent.putExtras(bundle_uploadStory);
    			startActivity(intent);

                //Start details activity
                startActivity(intent);
            }
        });
    }
    
    public static final String CAMERA_IMAGE_BUCKET_NAME =
	        Environment.getExternalStorageDirectory().toString()
	        + "/DCIM/Camera";
	public static final String CAMERA_IMAGE_BUCKET_ID =
	        getBucketId(CAMERA_IMAGE_BUCKET_NAME);
	
	public static String getBucketId(String path) {
	    return String.valueOf(path.toLowerCase().hashCode());
	}
	
	

    /**
     * Prepare some dummy data for gridview
     */
    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<ImageItem>();
        
        final String[] projection = { MediaColumns.DATA };
	    final String selection = ImageColumns.BUCKET_ID + " = ?";
	    final String[] selectionArgs = { CAMERA_IMAGE_BUCKET_ID };
	    final Cursor cursor = getContentResolver().query(Images.Media.EXTERNAL_CONTENT_URI, 
	            projection, null, null,
//	            selection, 
//	            selectionArgs, 
	            null);
	    //ArrayList<String> result = new ArrayList<String>(cursor.getCount());
	    if (cursor.moveToFirst()) {
	        final int dataColumn = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
	        int i=0;
	        do {
	            final String data = cursor.getString(dataColumn);
	            //Bitmap bitmap = BitmapFactory.decodeFile(data);
	            imageItems.add(new ImageItem(data, "Image#" + i));
	            Log.d("test", data.toString());
	            i++;
	        } while (cursor.moveToNext());
	    }
	    cursor.close();
        
//        TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);
//        for (int i = 0; i < imgs.length(); i++) {
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(i, -1));
//            imageItems.add(new ImageItem(bitmap, "Image#" + i));
//        }
        return imageItems;
    }
}
