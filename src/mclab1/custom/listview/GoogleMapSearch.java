package mclab1.custom.listview;

import android.graphics.Bitmap;

public class GoogleMapSearch {

	// DetailListview
		private String objectId;
		private String userName;
		private String userUuid;
		private String title;
		private int score;
		private Bitmap image;
		private String content;
		private double latitude;
		private double longitude;
		private int state;
		private String SSID; 

		public GoogleMapSearch(String objectId, String userName,String userUuid, String Title, int score, Bitmap bmp,
				String content, double latitude, double longitude, int state, String SSID) {
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
		}

		public String getobjectId() {
			return objectId;
		}

		public String getuserName() {
			return userName;
		}
		
		public String getuserUuid(){
			return userUuid;
		}

		public String getTitle() {
			return title;
		}
		
		public int getScore(){
			return score;
		}

		public Bitmap getImage() {
			return image;
		}

		public String getContent() {
			return content;
		}
		
		public double getlatitude(){
			return latitude;
		}

		public double getlongitude(){
			return longitude;
		}
		public int getState(){
			return state;
		}
		public String getSSID(){
			return SSID;
		}

}
