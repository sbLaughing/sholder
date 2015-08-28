package laughing.sholder.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private String titles[] ;
    private ArrayList<Fragment> mFragments;

    public ViewPagerAdapter(FragmentManager fm, String[] titles2, ArrayList<Fragment> fragments) {
        super(fm);
        this.mFragments = fragments;
        titles=titles2;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

}