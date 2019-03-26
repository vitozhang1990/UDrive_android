package cn.com.i_zj.udrive_az.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.MainActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.LocalCacheUtils;

public class SplashActivity extends DBSBaseActivity implements ViewPager.OnPageChangeListener {

    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.home)
    Button home;

    private List<Integer> images;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);
        images = new ArrayList<>();
        images.add(R.drawable.pic_one);
        images.add(R.drawable.pic_two);
        images.add(R.drawable.pic_three);
        viewpager.setAdapter(new ImageAdapter(this, images, null));
        viewpager.addOnPageChangeListener(this);
    }

    @OnClick(R.id.home)
    void onClick() {
        LocalCacheUtils.savePersistentSettingBoolean(Constants.SP_GLOBAL_NAME, Constants.SP_First, false);
        startActivity(MainActivity.class);
        finish();
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        home.setVisibility(i == images.size() - 1 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
