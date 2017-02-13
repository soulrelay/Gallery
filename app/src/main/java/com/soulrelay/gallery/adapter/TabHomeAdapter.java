package com.soulrelay.gallery.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;

/**
 * 主页直播Tab页
 *
 * @author ----zhaoruyang----
 * @date: 3/7/16
 */
public class TabHomeAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
    private static final String TAG = "TabHomeAdapter";

    private List<? extends Fragment> events;

    public TabHomeAdapter(FragmentManager fm, List<? extends Fragment> events) {
        super(fm);
        this.events = events;
    }


    @Override
    public Fragment getItem(int position) {
        return events.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "title"+ position;
    }

    @Override
    public int getCount() {
        if (events == null) {
            return 0;
        }
        return events.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
