package cn.com.i_zj.udrive_az.lz.ui.coupons

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import cn.com.i_zj.udrive_az.DBSBaseActivity
import cn.com.i_zj.udrive_az.R
import cn.com.i_zj.udrive_az.login.AccountInfoManager
import cn.com.i_zj.udrive_az.lz.adapter.CouponsAdapter
import cn.com.i_zj.udrive_az.model.UnUseCouponResult
import cn.com.i_zj.udrive_az.network.UObserver
import cn.com.i_zj.udrive_az.network.UdriveRestClient
import cn.com.i_zj.udrive_az.view.EmptyView
import com.trello.rxlifecycle2.android.ActivityEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.layout_lz_toolbar.*


/**
 * @author JayQiu
 * @create 2018/11/16
 * @Describe   新的优惠券列表
 */

class CouponsActivity : DBSBaseActivity() {
    lateinit var aouponsAdapter: CouponsAdapter
    var page: Int = 1
    lateinit var emptyView: EmptyView
    override fun getLayoutResource(): Int {
        return R.layout.activity_list
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.title="优惠券列表"
        toolbar.setNavigationOnClickListener { finish() }
        initView()
    }

    fun initView() {
        recycler.layoutManager = LinearLayoutManager(this)
        emptyView = EmptyView(recycler.context, recycler)
        aouponsAdapter = CouponsAdapter(R.layout.item_coupons, null, this@CouponsActivity)
        recycler.adapter = aouponsAdapter
        aouponsAdapter.bindToRecyclerView(recycler)
        swipeRefresh.isEnableLoadmore = true
        swipeRefresh.setOnRefreshListener {
            page = 1
            initData()
        }
        swipeRefresh.setOnLoadmoreListener {
            page++
            initData()
        }
        swipeRefresh.autoRefresh()
    }

    private fun initData() {
        var accountInfoResult = AccountInfoManager.getInstance().accountInfo
        if (accountInfoResult == null) {
            showToast("数据请求失败")
            return
        }
        UdriveRestClient.getClentInstance().v1FindAllPreferential(accountInfoResult.data.userId.toString())
                .subscribeOn(Schedulers.io())
                .compose(this@CouponsActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : UObserver<List<UnUseCouponResult.DataBean>>() {
                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                    }

                    override fun onSuccess(response: List<UnUseCouponResult.DataBean>?) {
                        if (response != null&&response.isNotEmpty()) {
                            aouponsAdapter.replaceData(response)
                        }else{
                            emptyView.setImage(R.mipmap.pic_coupon_null)
                            emptyView.setMsg("暂无可用优惠券~")
                            aouponsAdapter.emptyView = emptyView
                        }
                    }

                    override fun onException(code: Int, message: String?) {
                        emptyView.setImage(R.mipmap.pic_coupon_null)
                        emptyView.setMsg(message)
                    }

                    override fun onFinish() {
                        if (page == 1) {
                            swipeRefresh.finishRefresh()
                        } else {
                            swipeRefresh.finishLoadmore()
                        }
                    }
                })
    }

}