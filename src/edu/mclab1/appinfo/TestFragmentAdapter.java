package edu.mclab1.appinfo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.viewpagerindicator.IconPagerAdapter;

class TestFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
	private static final String TAG = "TestFragmentAdapterTAG";

    private int mCount = AppInfo.ICONS.length;

    public TestFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

    	switch (position) {
		case 0:
			return new InfoPage1(position);
		case 1:
			return new InfoPage1(position);
		case 2:
			return new InfoPage1(position);
		case 3:
			return new InfoPage1(position);
		case 4:
			return new InfoPage1(position);
		case 5:
			return new InfoPage6(position);
		default:
			return null;
		}
        //return TestFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return mCount;
    }
    
    @Override
    public int getIconResId(int index) {
      return AppInfo.ICONS[index];
    }

//    public void setCount(int count) {
//        if (count > 0 && count <= 10) {
//            mCount = count;
//            notifyDataSetChanged();
//        }
//    }
}