package cn.com.i_zj.udrive_az.utils;

import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.widget.TextView;

import java.util.List;

/**
 * Created by wli on 2018/8/11.
 */

public class HtmlUtils {

  public static void setTextViewHTML(TextView text, String html, ClickableSpan clickableSpan) {
    CharSequence sequence = Html.fromHtml(html);
    SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
    URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
    int i = 0;
    for (URLSpan span : urls) {
      makeLinkClickable(strBuilder, span, clickableSpan);
      i++;
    }
    text.setText(strBuilder);
    text.setMovementMethod(LinkMovementMethod.getInstance());
  }

  public static void setTextViewHTML(TextView text, String html, List<ClickableSpan> spanList) {
    CharSequence sequence = Html.fromHtml(html);
    SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
    URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
    int i = 0;
    for (URLSpan span : urls) {
      makeLinkClickable(strBuilder, span, spanList.get(i));
      i++;
    }
    text.setText(strBuilder);
    text.setMovementMethod(LinkMovementMethod.getInstance());
  }

  public static void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span, ClickableSpan clickable) {
    int start = strBuilder.getSpanStart(span);
    int end = strBuilder.getSpanEnd(span);
    int flags = strBuilder.getSpanFlags(span);
    strBuilder.setSpan(clickable, start, end, flags);
    strBuilder.removeSpan(span);
  }
}
