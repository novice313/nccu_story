package mclab1.service.camera;

import java.util.ArrayList;

import edu.mclab1.nccu_story.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridViewAdapter extends ArrayAdapter<ImageItem> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<ImageItem> data = new ArrayList<ImageItem>();

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList<ImageItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.text);
            holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }


        ImageItem item = data.get(position);

        Log.d("GridViewAdapterTAG", item.getImage().toString());
        

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        
        Bitmap bitmap = BitmapFactory.decodeFile(item.getImage(),options);
        
        holder.image.setRotation(90);
        holder.imageTitle.setText(item.getTitle());
        holder.image.setImageBitmap(bitmap);
        
        return row;
    }

    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
    }
}