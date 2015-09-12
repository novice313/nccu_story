package edu.mclab1.nccu_story;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SwipeTabFragment extends Fragment {

    private String tab;
    private int color;
    public static int index=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        tab = bundle.getString("tab");
        color = bundle.getInt("color");
        index = bundle.getInt("index");
        
    }
    
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipe_tab, null);
        TextView tv = (TextView) view.findViewById(R.id.textView);
        tv.setText(tab);
        //view.setBackgroundResource(color);
        
        
        switch (index) {
        case 0:
        	//view = inflater.inflate(R.layout.custom_list_view, null);
        	//MenuItem item = menu.findItem(R.id.action_new);
            break;
        }
        
        return view;
    }
}