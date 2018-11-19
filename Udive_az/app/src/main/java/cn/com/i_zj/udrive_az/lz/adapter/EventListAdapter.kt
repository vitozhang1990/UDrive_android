package cn.com.i_zj.udrive_az.lz.adapter

import android.support.v4.app.Fragment
import android.widget.LinearLayout
import cn.com.i_zj.udrive_az.R
import cn.com.i_zj.udrive_az.model.ActivityInfo
import cn.com.i_zj.udrive_az.utils.SizeUtils
import cn.com.i_zj.udrive_az.utils.ToolsUtils
import cn.com.i_zj.udrive_az.utils.image.FrescoImgUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.facebook.drawee.view.SimpleDraweeView

/**
 * @author JayQiu
 * @create 2018/11/15
 * @Describe
 */
class EventListAdapter : BaseQuickAdapter<ActivityInfo, BaseViewHolder> {
    var context: Fragment
    var width: Int

    constructor(layoutResId: Int, data: MutableList<ActivityInfo>?, context: Fragment) : super(layoutResId, data) {
        this@EventListAdapter.context = context
        width = ToolsUtils.getWindowWidth(context.context)

    }

    override fun convert(helper: BaseViewHolder?, item: ActivityInfo?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_name, item.title)
            helper.setText(R.id.tv_time, ToolsUtils.getTime(item.startTime, "yyyy-MM-dd"))
            var image = helper.getView<SimpleDraweeView>(R.id.iv_image)
            var widthImg = width - 16
            var params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT , widthImg/3)
            image.layoutParams = params
            FrescoImgUtil.loadImage(item.bgImg, image)
        }

    }
}