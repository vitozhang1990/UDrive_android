package cn.com.i_zj.udrive_az.map.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.com.i_zj.udrive_az.R;

/**
 * Created by liuwei on 2017/8/5.
 * version 1.0
 */

public class MyPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFramgents;
    private ArrayList<Integer> imageResId ;
    private Context context;
    public MyPagerAdapter(FragmentManager fm, List<Fragment> mFragments, ArrayList <Integer> imageResId, Context context) {
        super(fm);
        this.mFramgents = mFragments;
        this.context=context;
        this.imageResId=imageResId;
    }

    @Override
    public Fragment getItem(int position) {
        return mFramgents.get(position);
    }

    @Override
    public int getCount() {
        return mFramgents.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
    public View getTabView(int position){
        View view = LayoutInflater.from(context).inflate(R.layout.item_carbar, null);
        ImageView img =view.findViewById(R.id.imageview);
        img.setImageResource(imageResId.get(position));
        return view;
    }

}