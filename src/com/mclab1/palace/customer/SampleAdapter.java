package com.mclab1.palace.customer;

import java.util.List;
import java.util.Random;

import edu.mclab1.nccu_story.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.RoundedTransformationBuilder;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

/***
 * ADAPTER *******顯示附近offline景點的mp3
 */

public class SampleAdapter extends ArrayAdapter<CustomerItem> {

	private static final String TAG = "SampleAdapter";
	private final Transformation mTransformation;

	private Context mContext;

	static class ViewHolder {
		ImageView itemImage;
		TextView title;
		TextView content;
		// DynamicHeightTextView txtLineOne;
	}

	private final LayoutInflater mLayoutInflater;
	private final Random mRandom;

	private static final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();

	public SampleAdapter(final Context context, final int textViewResourceId) {
		super(context, textViewResourceId);
		mLayoutInflater = LayoutInflater.from(context);
		mRandom = new Random();
		mTransformation = new RoundedTransformationBuilder()
				.borderColor(Color.BLACK).borderWidthDp(0).cornerRadiusDp(8)
				.oval(false).build();
		mContext = context;
	}

	@Override
	public CustomerItem getItem(int position) {
		CustomerItem customerItem = new CustomerItem();

		switch (position) {
		case 0:
			customerItem.image_path = "http://arthalf.com/wp-content/uploads/2013/07/184.jpg";
			customerItem.title = "翠玉白菜";
			customerItem.content = "是中華民國在臺灣的國立故宮博物院所珍藏的玉器雕刻，長18.7公分，寬9.1公分，厚5.07公分[1]，利用翠玉天然的色澤雕出白菜的形狀。"
					+ "翠玉白菜與肉形石目前在國立故宮博物院典藏僅認定為重要文物，並非是中華民國的國寶";

			break;

		case 1:
			customerItem.image_path = "http://finance.people.com.cn/NMediaFile/2012/0924/MAIN201209241056000030346790408.jpg";
			customerItem.title = "肉形石";
			customerItem.content = "肉形石，是於清朝時期的一個宮廷珍玩，長5.73公分，寬6.6公分，厚5.3公分，現存於台北國立故宮博物院，肉形石的由來是一塊自然生成的瑪瑙，瑪瑙生成過程中受到雜質的影響，呈現一層一層"
					+ "不同顏色的層次，外觀看過去就像一塊肥嫩的東坡肉，因而稱「肉形石」";

			break;
		case 2:

			customerItem.image_path = "http://image.digitalarchives.tw/ImageCache/00/05/cc/70.jpg";
			customerItem.title = "故宮寶物";
			customerItem.content = "故宮寶物";

			break;

		case 3:
			customerItem.image_path = "http://image.digitalarchives.tw/ImageCache/00/0e/86/33.jpg";
			customerItem.title = "故宮寶物";
			customerItem.content = "故宮寶物";

			break;

		case 4:
			customerItem.image_path = "http://image.digitalarchives.tw/ImageCache/00/02/e6/36.jpg";
			customerItem.title = "故宮寶物";
			customerItem.content = "故宮寶物";

			break;

		default:
			break;
		}

		return customerItem;
	}

	@Override
	public View getView(final int position, View convertView,
			final ViewGroup parent) {

	   ViewHolder vh = null;
		int type = getItemViewType(position);

		Log.d(TAG, "getView position:" + position);
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.list_item_sample,
					parent, false);
			vh = new ViewHolder();
			
			
		    ParseQuery<ParseObject> tablequery = ParseQuery.getQuery("offline");
		    tablequery.findInBackground(new FindCallback<ParseObject>() {        //讀取文字
		    	@Override
				public void done(List<ParseObject> me, ParseException e) {
		    		if(e==null){
		    			
		    		

		    final ParseFile image =(ParseFile)me.get(0).get("Photo");
    		// ((ParseObject) me).getParseFile("data");
   // final ParseImageView imageView = (ParseImageView) findViewById(R.id.personalprfile);
   // imageView.setParseFile(image);
   // System.out.println("image"+image);
   // if(image!=null){
    image.getDataInBackground(new GetDataCallback() {
		
		@Override
		public void done(byte[] data, ParseException e) {
			// TODO Auto-generated method stub
			if(e==null){
    			System.out.println("personalprofile"+" "+data.length);
                final Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,data.length);
                
              //final ParseImageView imageView = (ParseImageView) vh.findViewById(R.id.item_image);
             // imageView.setParseFile(image);
            //  imageView.setImageBitmap(bmp);
                
               /* imageView.loadInBackground(new GetDataCallback() {
                    public void done(byte[] data, ParseException e) {
                    // The image is loaded and displayed!                    
                    int oldHeight = imageView.getHeight();
                    int oldWidth = imageView.getWidth();     
                    System.out.println("imageView height = " + oldHeight);
                    System.out.println("imageView width = " + oldWidth);
                    imageView.setImageBitmap(bmp);


                   // Log.v("LOG!!!!!!", "imageView height = " + oldHeight);      // DISPLAYS 90 px
                   // Log.v("LOG!!!!!!", "imageView width = " + oldWidth);        // DISPLAYS 90 px      
                    }
                });*/
				
			}
			else{
    			System.out.println("personalprofilerror");

			}
			
		}
	});
  //  }else{
	//	System.out.println("imageerror"); 	
   // }
    /*imageView.loadInBackground(new GetDataCallback() {
         public void done(byte[] data, ParseException e) {
         // The image is loaded and displayed!                    
         int oldHeight = imageView.getHeight();
         int oldWidth = imageView.getWidth(); 
         System.out.println("imageView height = " + oldHeight);
         System.out.println("imageView width = " + oldWidth);
             
         }
    });*/ 
	
}else{
	
}

}

});
    
			vh.itemImage = (ImageView) convertView
					.findViewById(R.id.item_image);    //parse抓圖
			convertView.setTag(vh);

		} else {
			vh = (ViewHolder) convertView.getTag();

		}

		double positionHeight = getPositionRatio(position);

		vh.itemImage = (ImageView) convertView.findViewById(R.id.item_image);
		vh.title = (TextView) convertView.findViewById(R.id.item_title);
		vh.content = (TextView) convertView.findViewById(R.id.item_content);

		switch (position) {
		case 0:

			Picasso.with(mContext)
					.load("http://arthalf.com/wp-content/uploads/2013/07/184.jpg")
					.into(vh.itemImage);
			setTextView(vh.title, "翠玉白菜");
			setTextView(vh.content, "是中華民國在臺灣的國立故宮博物院所珍藏的玉器雕刻，長18.7公分，寬9.1公分，厚5.07公分[1]，利用翠玉天然的色澤雕出白菜的形狀。"
					+ "翠玉白菜與肉形石目前在國立故宮博物院典藏僅認定為重要文物，並非是中華民國的國寶");
			break;

		case 1:

			Picasso.with(mContext)
					.load("http://finance.people.com.cn/NMediaFile/2012/0924/MAIN201209241056000030346790408.jpg")
					.into(vh.itemImage);
			setTextView(vh.title, "肉形石");
			setTextView(vh.content,
					"肉形石，是於清朝時期的一個宮廷珍玩，長5.73公分，寬6.6公分，厚5.3公分，現存於台北國立故宮博物院，肉形石的由來是一塊自然生成的瑪瑙，瑪瑙生成過程中受到雜質的影響，呈現一層一層"
					+ "不同顏色的層次，外觀看過去就像一塊肥嫩的東坡肉，因而稱「肉形石」");
			break;
		case 2:

			Picasso.with(mContext)
					.load("http://image.digitalarchives.tw/ImageCache/00/05/cc/70.jpg")
					.into(vh.itemImage);
			setTextView(vh.title, "故宮寶物");
			setTextView(vh.content,
					"故宮寶物");
			break;

		case 3:

			Picasso.with(mContext)
					.load("http://image.digitalarchives.tw/ImageCache/00/0e/86/33.jpg")
					.into(vh.itemImage);
			setTextView(vh.title, "故宮寶物");
			setTextView(vh.content,
					"故宮寶物");
			break;

		case 4:

			Picasso.with(mContext)
					.load("http://image.digitalarchives.tw/ImageCache/00/02/e6/36.jpg")
					.into(vh.itemImage);
			setTextView(vh.title, "故宮寶物");
			setTextView(vh.content,
					"故宮寶物");
			break;

		default:
			break;
		}

		return convertView;
	}

	private void setTextView(TextView textView, String what) {
		textView.setText(what);
	}

	private double getPositionRatio(final int position) {
		double ratio = sPositionHeightRatios.get(position, 0.0);
		// if not yet done generate and stash the columns height
		// in our real world scenario this will be determined by
		// some match based on the known height and width of the image
		// and maybe a helpful way to get the column height!
		if (ratio == 0) {
			ratio = getRandomHeightRatio();
			sPositionHeightRatios.append(position, ratio);
			Log.d(TAG, "getPositionRatio:" + position + " ratio:" + ratio);
		}
		return ratio;
	}

	private double getRandomHeightRatio() {
		return (mRandom.nextDouble() / 2.0) + 1.0; // height will be 1.0 - 1.5
													// the width
	}
}
