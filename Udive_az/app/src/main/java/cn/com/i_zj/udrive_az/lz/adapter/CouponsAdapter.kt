package cn.com.i_zj.udrive_az.lz.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cn.com.i_zj.udrive_az.R
import cn.com.i_zj.udrive_az.model.UnUseCouponResult
import cn.com.i_zj.udrive_az.utils.StringUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import java.text.SimpleDateFormat

/**
 * @author JayQiu
 * @create 2018/11/15
 * @Describe
 */
class CouponsAdapter : BaseQuickAdapter<UnUseCouponResult.DataBean, BaseViewHolder> {
    var context: Context

    constructor(layoutResId: Int, data: MutableList<UnUseCouponResult.DataBean>?, context: Context?) : super(layoutResId, data) {
        this@CouponsAdapter.context = context!!

    }

    override fun convert(helper: BaseViewHolder?, item: UnUseCouponResult.DataBean?) {
        if (helper != null && item != null) {
            var viewLine = helper.getView<View>(R.id.v_line)
            helper.setText(R.id.tv_coupons_name, item.name)


            var yanTv = helper.getView<TextView>(R.id.tv_yuan)
            var imgCar = helper.getView<ImageView>(R.id.iv_image)
            if (data[0].id == item.id) {//  第一个显示---垃圾不返回position
                viewLine.visibility = View.VISIBLE
            } else {
                viewLine.visibility = View.GONE
            }
            lateinit var list: MutableList<String>
            list = ArrayList()
            if (item.preferential_type == 2) {//(1.定额，2折扣)
                helper.setText(R.id.tv_value, (item.rebate * 10).toString() + "")
                yanTv.text = "折"
                if (item.max_amount > 0) {
                    list.add("最高优惠" + (item.max_amount) / 100f + "元")
                }
            } else {
                helper.setText(R.id.tv_value, ((item.preferential_amount) / 100f).toString())
                yanTv.text = "元"
            }
            if (item.instant_rebate_amount > 0) {
                list.add("满" + (item.instant_rebate_amount / 100f).toString() + "元使用")
            }
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            var time: Long = 0
            try {
                time = simpleDateFormat.parse(item.distribute_time).time
                time += (item.validity - 1) * 24 * 60 * 60 * 1000L
            } catch (e: Exception) {
                e.printStackTrace()
            }
            helper.setText(R.id.tv_coupons_time, "使用期限 " + item.distribute_time + "至" + simpleDateFormat.format(time) + "")
            var tvExplain = helper.getView<TextView>(R.id.tv_explain)


            var imageId: Int = R.mipmap.pic_default
            when {
                item.brand_id == UnUseCouponResult.DataBean.BAOLAI ->//品牌ID（目前只有3种，1宝来，2POLO，3LITE）为NULL的不限制车辆
                {
                    list.add("仅限Bora使用，使用时间段为" + item.start_time + "-" + item.end_time)
                    imageId = R.mipmap.pic_car_bora_mid
                }
                item.brand_id == UnUseCouponResult.DataBean.POLO -> {
                    list.add("仅限Polo使用，使用时间段为" + item.start_time + "-" + item.end_time)
                    imageId = R.mipmap.pic_car_polo_mid
                }
                item.brand_id == UnUseCouponResult.DataBean.LITE -> {
                    imageId = R.mipmap.pic_car_lite_mid
                    list.add("仅限Lite使用，使用时间段为" + item.start_time + "-" + item.end_time)
                }
                else -> {
                    if (!StringUtils.isEmpty(item.start_time)) {
                        list.add("全场通用,使用时间段为" + item.start_time + "-" + item.end_time)
                    } else {
                        list.add("全场通用")
                    }

                    imageId = R.mipmap.pic_default
                }
            }
            var explainStr = StringBuffer()
            if (list.isNotEmpty()) {
                for (index in list.indices) {
                    if (list.size == 1) {
                        explainStr.append("${list[index]}")
                    } else {
                        if (index == 0) {
                            explainStr.append("${index + 1}.${list[index]}")
                        } else {
                            explainStr.append("\n${index + 1}.${list[index]}")
                        }
                    }
                }
            }
            tvExplain.text = explainStr
            Glide.with(context)
                    .load(imageId)
                    .into(imgCar)

        }

    }
}