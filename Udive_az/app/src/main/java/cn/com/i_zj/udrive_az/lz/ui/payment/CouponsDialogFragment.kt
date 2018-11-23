package cn.com.i_zj.udrive_az.lz.ui.payment

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import cn.com.i_zj.udrive_az.R
import cn.com.i_zj.udrive_az.login.AccountInfoManager
import cn.com.i_zj.udrive_az.login.SessionManager
import cn.com.i_zj.udrive_az.lz.adapter.CouponsAdapter
import cn.com.i_zj.udrive_az.lz.bean.CouponPayEvent
import cn.com.i_zj.udrive_az.model.UnUseCouponResult
import cn.com.i_zj.udrive_az.network.UObserver
import cn.com.i_zj.udrive_az.network.UdriveRestClient
import com.chad.library.adapter.base.BaseQuickAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus

/**
 * @author JayQiu
 * @create 2018/11/21
 * @Describe
 */
class CouponsDialogFragment : BottomSheetDialogFragment(), BaseQuickAdapter.OnItemClickListener {
    lateinit var list: MutableList<UnUseCouponResult.DataBean>

    lateinit var couponAdapter: CouponsAdapter
    lateinit var recycler: RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var mTvNoUseCoupon: TextView
    lateinit var mIvClose: ImageView
    lateinit var orderNumber: String
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflate = inflater.inflate(R.layout.fragment_conpou, container, false)
        progressBar = inflate.findViewById<ProgressBar>(R.id.progressBar)
        recycler = inflate.findViewById<RecyclerView>(R.id.recycler)
        mTvNoUseCoupon = inflate.findViewById<TextView>(R.id.tv_no_use_coupon)
        mIvClose = inflate.findViewById<ImageView>(R.id.iv_close)
        recycler.layoutManager = LinearLayoutManager(context)
        var bundle: Bundle? = getArguments()
        if (bundle != null) {
            orderNumber = bundle.getString("orderNumber", "")
        }

        list = ArrayList()
        couponAdapter = CouponsAdapter(
                R.layout.item_coupons,
                null,
                this@CouponsDialogFragment.context
        )
        mIvClose.setOnClickListener {
            dismiss()
        }
        recycler.adapter = couponAdapter
        couponAdapter.onItemClickListener = this
        couponAdapter.bindToRecyclerView(recycler)
        initEvent()
        findUnUsePreferential()
        return inflate
    }

    private fun initEvent() =
            mTvNoUseCoupon.setOnClickListener { EventBus.getDefault().post(CouponPayEvent(null)) }

    private fun findUnUsePreferential() {
        val accountInfo = AccountInfoManager.getInstance().accountInfo
        if (accountInfo == null) {
            Toast.makeText(context, "数据请求失败", Toast.LENGTH_SHORT).show()
            return
        }
        UdriveRestClient.getClentInstance().v1FindUnUsePreferential(orderNumber, accountInfo.data.userId.toString() + "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : UObserver<List<UnUseCouponResult.DataBean>>() {

                    override fun onSuccess(value: List<UnUseCouponResult.DataBean>?) {
                        list.clear()
                        if (value != null) {
                            list.addAll(value)
                            couponAdapter.replaceData(value)
                        } else {
                            couponAdapter.setEmptyView(R.layout.layout_empty)
                        }
                        progressBar.visibility = View.GONE
                        recycler.visibility = View.VISIBLE
                    }

                    override fun onException(code: Int, message: String?) {
                        progressBar.visibility = View.GONE
                        recycler.visibility = View.VISIBLE
                        couponAdapter.setEmptyView(R.layout.layout_error)
                    }

                    override fun onFinish() {

                    }
                })

    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        if ( list.isNotEmpty()) {
            EventBus.getDefault().post(CouponPayEvent(list[position]))
        }

    }
}




