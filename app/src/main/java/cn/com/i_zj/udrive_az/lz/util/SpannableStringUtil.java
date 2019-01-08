package cn.com.i_zj.udrive_az.lz.util;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import cn.com.i_zj.udrive_az.utils.UIUtils;

public class SpannableStringUtil {

    public static final int DEFAULT = -1;

    /**
     * 文字颜色
     */
    public static SpannableString setColorAndSizeSpan(String msg, int color, int size) {
        SpannableString spanString = new SpannableString(msg);

        ForegroundColorSpan colorSpan = new ForegroundColorSpan(color != DEFAULT ? color : Color.DKGRAY);
        spanString.setSpan(colorSpan, 0, msg.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(size != DEFAULT ? size : UIUtils.dp2px(15));
        spanString.setSpan(sizeSpan, 0, msg.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }
}
