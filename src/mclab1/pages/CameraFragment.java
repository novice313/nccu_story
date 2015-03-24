package mclab1.pages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.mclab1.nccu_story.R;

public class CameraFragment extends Fragment{
	
	private static final String tag = "CameraFragmentTAG";

	public static CameraFragment newInstance() {
		CameraFragment cameraFragment = new CameraFragment();
		return cameraFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.googlemap);
        Log.d(tag,"oncreated.");
        
    }
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.camera, container, false);
		
		return view;
		
	}

	
	
	

}
