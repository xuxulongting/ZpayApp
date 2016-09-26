package com.spreadtrum.iit.zpayapp.displaydemo;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by SPREADTRUM\ting.long on 16-9-26.
 */
public class ViewPagerAdapter  extends PagerAdapter{
    private List<View> views;
    public ViewPagerAdapter(List<View> views){
        this.views = views;
    }
    @Override
    public int getCount() {
        if(views==null)
            return 0;
        return views.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position));
        return views.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }
}
