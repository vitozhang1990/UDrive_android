package cn.com.i_zj.udrive_az.map.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.com.i_zj.udrive_az.R;

/**
 * Created by liuwei on 2018/8/13.
 */

public class BaseFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;
    private FragmentManager fm;
    private ArrayList<Integer> imageResId ;
    private Context context;

    public BaseFragmentAdapter(FragmentManager fm, List<Fragment> fragments, ArrayList<Integer> imageResId, Context context) {
        super(fm);
        this.fm = fm;
        this.fragments= fragments;
        this.context=context;
        this.imageResId=imageResId;
    }


    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        FragmentTransaction ft = fm.beginTransaction();
        for (int i = 0; i < getCount(); i++) {//通过遍历清除所有缓存
            final long itemId = getItemId(i);
            //得到缓存fragment的名字
            String name = makeFragmentName(container.getId(), itemId);
            //通过fragment名字找到该对象
            BaseFragment fragment = (BaseFragment) fm.findFragmentByTag(name);
            if (fragment != null) {
                //移除之前的fragment
                ft.remove(fragment);
            }
        }
        //重新添加新的fragment:最后记得commit
        ft.add(container.getId(), getItem(position)).attach(getItem(position)).commit();
        return getItem(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    /**
     * 得到缓存fragment的名字
     * @param viewId
     * @param id
     * @return
     */
    private String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
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