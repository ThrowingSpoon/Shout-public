package uk.co.liammartin.shout;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class RespondAdapter extends FragmentPagerAdapter {

    private String fragments[] = {"DETAILS", "MAP", "LOCATION"};

    public RespondAdapter(FragmentManager fm, Context context) {
        super(fm);
    }

    //Getting each tabs content as a fragment
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new RespondFragmentDetails();
            case 1:
                return new RespondFragmentMap();
            case 2:
                return new RespondFragmentLocation();
            default:
                return null;
        }
    }

    //Getting the total amount of tabs
    @Override
    public int getCount() {
        return fragments.length;
    }

    //Getting the title of the current tab
    @Override
    public CharSequence getPageTitle(int position) {
        return fragments[position];
    }
}
