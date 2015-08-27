package edu.mclab1.nccu_story;

import mclab1.pages.GoogleMapFragment;
import mclab1.pages.MediaPlayerFragment;
import mclab1.pages.NewsFragment;
import mclab1.pages.OwnerFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {
	 
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int index) {
 
        switch (index) {
        case 0:
            // Top Rated fragment activity
            return new NewsFragment();
        case 1:
            // Games fragment activity
            return new GoogleMapFragment();
        case 2:
            // Movies fragment activity
            return new OwnerFragment();
        case 3:
        	return new MediaPlayerFragment();
        }
 
        return null;
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 4;
    }
 
}