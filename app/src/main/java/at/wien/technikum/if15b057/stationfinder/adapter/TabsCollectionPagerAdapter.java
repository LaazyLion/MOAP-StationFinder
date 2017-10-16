package at.wien.technikum.if15b057.stationfinder.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by laazylion on 10/10/17.
 */

public class TabsCollectionPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> content;


    // constructor

    public TabsCollectionPagerAdapter(FragmentManager fm) {
        super(fm);
        content = new ArrayList<>();
    }


    // methods

    @Override
    public Fragment getItem(int position) {
        return content.get(position);
    }

    @Override
    public int getCount() {
        return content.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return content.get(position).getTag();
    }

    public void addContent(Fragment fragment) {
        content.add(fragment);
    }
}
