package cn.com.i_zj.udrive_az.lz.ui.msg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.lz.adapter.UFragmentPagerAdapter;

/**
 * @author JayQiu
 * @create 2018/11/13
 * @Describe
 */
public class ActMsgMain extends DBSBaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tblayout)
    TabLayout tblayout;
    @BindView(R.id.vp_pager)
    ViewPager vpPager;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_msg_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        ArrayList<Fragment>    fragmentList = new ArrayList<>();
        ArrayList list_Title = new ArrayList<>();
        fragmentList.add(EventFragment.newInstance());
        fragmentList.add(NoticeFragment.newInstance());
        list_Title.add("消息");
        list_Title.add("通知");
        vpPager.setAdapter(new UFragmentPagerAdapter(getSupportFragmentManager(),ActMsgMain.this,fragmentList,list_Title));
        tblayout.setupWithViewPager(vpPager);//此方法就是让tablayout和ViewPager联动

    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }
}
