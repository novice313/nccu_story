package mclab1.sugar;

import java.util.Date;

import android.graphics.Bitmap;

import com.orm.SugarRecord;

public class GoogleMapData extends SugarRecord<GoogleMapData> {

	// DetailListview
	public String objectId;
	public String userName;
	public String userUuid;
	public String title;
	public int score;
	public Bitmap image;
	public String content;
	public double latitude;
	public double longitude;
	public int state;
	public String SSID;
	public Date createAt;
	
	public GoogleMapData(){
		// TODO Auto-generated constructor stub
	}

	public GoogleMapData(String objectId, String userName, String userUuid,
			String Title, int score, Bitmap bmp, String content,
			double latitude, double longitude, int state, String SSID, Date createAt) {
		this.objectId = objectId;
		this.userName = userName;
		this.userUuid = userUuid;
		this.title = Title;
		this.score = score;
		this.image = bmp;
		this.content = content;
		this.latitude = latitude;
		this.longitude = longitude;
		this.state = state;
		this.SSID = SSID;
		this.createAt = createAt;
	}

}
