package cn.com.i_zj.udrive_az.lz.ui.coupons

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import cn.com.i_zj.udrive_az.DBSBaseActivity
import cn.com.i_zj.udrive_az.R
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.fragment_list.*


/**
 * @author JayQiu
 * @create 2018/11/16
 * @Describe   新的优惠券列表
 */

class CouponsActivity : DBSBaseActivity(){
    override fun getLayoutResource(): Int {
        return  R.layout.activity_list
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    fun  initView(){
        recycler.layoutManager = LinearLayoutManager(this)

    }
}