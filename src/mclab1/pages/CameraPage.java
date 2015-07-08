package mclab1.pages;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import mclab1.service.camera.CameraPreview;
import mclab1.service.camera.GalleryActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import edu.mclab1.nccu_story.R;

public class CameraPage extends Activity {

	private final static String tag = "CameraFragment";
	private Camera mCamera;
	private CameraPreview mPreview;
	private PictureCallback mPicture;
	private ImageButton capture, switchCamera, listGallery;
	private Context myContext;
	private LinearLayout cameraPreview;
	private boolean cameraFront = false;

	double longitude;
	double latitude;

	public static CameraPage newInstance() {
		CameraPage cameraFragment = new CameraPage();
		return cameraFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.googlemap);
		Log.d(tag, "oncreated.");

		setContentView(R.layout.fragment_camera);

		Bundle extras = getIntent().getExtras();
		longitude = extras.getDouble("longitude");
		latitude = extras.getDouble("latitude");
		Log.d(tag, "longitude = " + longitude);
		Log.d(tag, "latitude = " + latitude);

		// }

		// @Override
		// public View onCreateView(LayoutInflater inflater, ViewGroup
		// container, Bundle savedInstanceState){
		// View view = inflater.inflate(R.layout.fragment_camera, container,
		// false);
		// Log.d(tag, "onCreateView");
		// getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		myContext = getApplicationContext();
		// initialize();

		cameraPreview = (LinearLayout) findViewById(R.id.camera_preview);
		mPreview = new CameraPreview(myContext, mCamera);
		cameraPreview.addView(mPreview);

		capture = (ImageButton) findViewById(R.id.Btn_action_camera);
		capture.setOnClickListener(captrureListener);

		switchCamera = (ImageButton) findViewById(R.id.Btn_switch_camera);
		switchCamera.setOnClickListener(switchCameraListener);

		listGallery = (ImageButton) findViewById(R.id.Btn_action_storage);
		listGallery.setOnClickListener(listGalleryListener);

		// return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(tag, "onStart");
	}

	private int findFrontFacingCamera() {
		int cameraId = -1;
		// Search for the front facing camera
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				cameraId = i;
				cameraFront = true;
				break;
			}
		}
		return cameraId;
	}

	private int findBackFacingCamera() {
		int cameraId = -1;
		// Search for the back facing camera
		// get the number of cameras
		int numberOfCameras = Camera.getNumberOfCameras();
		// for every camera check
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
				cameraId = i;
				cameraFront = false;
				break;
			}
		}
		return cameraId;
	}

	public void onResume() {
		super.onResume();
		if (!hasCamera(myContext)) {
			Toast toast = Toast.makeText(myContext,
					"Sorry, your phone does not have a camera!",
					Toast.LENGTH_LONG);
			toast.show();
			finish();
		}
		if (mCamera == null) {
			// if the front facing camera does not exist
			if (findFrontFacingCamera() < 0) {
				Toast.makeText(CameraPage.this,
						"No front facing camera found.", Toast.LENGTH_LONG)
						.show();
				switchCamera.setVisibility(View.GONE);
			}
			mCamera = Camera.open(findBackFacingCamera());
			mPicture = getPictureCallback();
			mPreview.refreshCamera(mCamera);
		}
	}

	// public void initialize() {
	//
	// }

	OnClickListener switchCameraListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// get the number of cameras
			int camerasNumber = Camera.getNumberOfCameras();
			if (camerasNumber > 1) {
				// release the old camera instance
				// switch camera, from the front and the back and vice versa

				releaseCamera();
				chooseCamera();
			} else {
				Toast toast = Toast.makeText(myContext,
						"Sorry, your phone has only one camera!",
						Toast.LENGTH_LONG);
				toast.show();
			}
		}
	};

	public void chooseCamera() {
		// if the camera preview is the front
		if (cameraFront) {
			int cameraId = findBackFacingCamera();
			if (cameraId >= 0) {
				// open the backFacingCamera
				// set a picture callback
				// refresh the preview

				mCamera = Camera.open(cameraId);
				mPicture = getPictureCallback();
				mPreview.refreshCamera(mCamera);
			}
		} else {
			int cameraId = findFrontFacingCamera();
			if (cameraId >= 0) {
				// open the backFacingCamera
				// set a picture callback
				// refresh the preview

				mCamera = Camera.open(cameraId);
				mPicture = getPictureCallback();
				mPreview.refreshCamera(mCamera);
			}
		}
	}

	OnClickListener listGalleryListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.d(tag, "Btn_listGallery onclick.");
			Intent intent_uploadStory = new Intent();
			intent_uploadStory.setClass(CameraPage.this, GalleryActivity.class);
			Bundle bundle_uploadStory = new Bundle();
			bundle_uploadStory.putDouble("longitude", longitude);
			bundle_uploadStory.putDouble("latitude", latitude);
			// 將Bundle物件assign給intent
			intent_uploadStory.putExtras(bundle_uploadStory);
			startActivity(intent_uploadStory);
		}
	};

	@Override
	public void onPause() {
		super.onPause();
		// when on Pause, release camera in order to be used from other
		// applications
		releaseCamera();
	}

	private boolean hasCamera(Context context) {
		// check if the device has camera
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			return true;
		} else {
			return false;
		}
	}

	private PictureCallback getPictureCallback() {
		PictureCallback picture = new PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				// make a new picture file
				File pictureFile = getOutputMediaFile();

				if (pictureFile == null) {
					return;
				}
				try {
					// write the file
					FileOutputStream fos = new FileOutputStream(pictureFile);
					fos.write(data);
					fos.close();
					Toast toast = Toast.makeText(myContext, "Picture saved: "
							+ pictureFile.getName(), Toast.LENGTH_LONG);
					toast.show();

				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				}

				// refresh camera to continue preview
				mPreview.refreshCamera(mCamera);
			}
		};
		return picture;
	}

	OnClickListener captrureListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mCamera.takePicture(null, null, mPicture);
		}
	};

	// make picture and save to a folder
	private static File getOutputMediaFile() {
		// make a new file directory inside the "sdcard" folder
		File mediaStorageDir = new File(Environment
				.getExternalStorageDirectory().toString() + "/DCIM/",
				"Camera");
		Log.d(tag, mediaStorageDir.getAbsolutePath());

		// if this "JCGCamera folder does not exist
		if (!mediaStorageDir.exists()) {
			Log.d(tag, "folder is not exists.");
			// if you cannot make this folder return
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}

		// take the current timeStamp
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		// and make a media file:
		mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ "IMG_" + timeStamp + ".jpg");

		return mediaFile;
	}

	private void releaseCamera() {
		// stop and release camera
		if (mCamera != null) {
			mPreview.getHolder().removeCallback(mPreview);
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(tag, "onStop.");
	}

	// @Override
	// public void onDestroyView(){
	// super.onDestroyView();
	// Log.d(tag, "onDestroyView.");
	// }

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(tag, "onDestroy.");
	}

}
