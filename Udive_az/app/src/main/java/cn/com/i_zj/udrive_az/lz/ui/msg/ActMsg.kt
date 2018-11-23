package cn.com.i_zj.udrive_az.lz.ui.msg

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import butterknife.BindView
import butterknife.OnClick
import cn.com.i_zj.udrive_az.DBSBaseActivity
import cn.com.i_zj.udrive_az.R
import cn.com.i_zj.udrive_az.lz.adapter.UFragmentPagerAdapter

/**
 * @author JayQiu
 * @create 2018/11/14
 * @Describe 消息
 */
class ActMsg : DBSBaseActivity() {
    @BindView(R.id.tblayout)
    lateinit var mTabLayout: TabLayout
    @BindView(R.id.vp_pager)
    lateinit var mViewPage: ViewPager

    override fun getLayoutResource(): Int {
        return R.layout.activity_msg_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    fun initView() {
        val mList = ArrayList<Fragment>()
        mList.add(EventKFragment())
        mList.add(NoticeFragment.newInstance())
        val mTitleList = listOf("活动", "通知")
        mViewPage.adapter = UFragmentPagerAdapter(supportFragmentManager, this, mList, mTitleList)
        mTabLayout.setupWithViewPager(mViewPage)

    }

    @OnClick(R.id.iv_back)
    fun onClick() {
        finish()
    }

}


