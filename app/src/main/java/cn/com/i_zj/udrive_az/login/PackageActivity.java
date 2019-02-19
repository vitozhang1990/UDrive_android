package cn.com.i_zj.udrive_az.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.map.fragment.BaseFragmentAdapter;
import cn.com.i_zj.udrive_az.map.fragment.PackageFragment;
import cn.com.i_zj.udrive_az.model.ParkDetailResult.DataBean.CarVosBean;
import cn.com.i_zj.udrive_az.widget.ViewPagerIndicator;

public class PackageActivity extends DBSBaseActivity implements ViewPager.OnPageChangeListener {

    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.indicator_line)
    ViewPagerIndicator mIndicatorCircleLine;

    private ArrayList<CarVosBean> carBeans;
    private List<Fragment> fragments = new ArrayList<>();
    private int position;

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_package;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.setStatusBar(this);

        position = getIntent().getIntExtra("position", 0);
        carBeans = (ArrayList<CarVosBean>) getIntent().getSerializableExtra("list");
        if (carBeans == null || carBeans.size() == 0) {
            showToast("数据错误");
            finish();
            return;
        }

        fragments.clear();
        for (int i = 0; i < carBeans.size(); i++) {
            PackageFragment carsFragment = PackageFragment.newInstance(carBeans.get(i));
            fragments.add(carsFragment);
        }
        mViewPager.addOnPageChangeListener(this);
        BaseFragmentAdapter myPagerAdapter = new BaseFragmentAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(myPagerAdapter);
        mViewPager.setCurrentItem(position);
        mIndicatorCircleLine.setViewPager(mViewPager, fragments.size());
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}