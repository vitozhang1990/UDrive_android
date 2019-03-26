package cn.com.i_zj.udrive_az.login;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageAdapter extends PagerAdapter {

    private Context mContext;
    private List<Integer> images;
    private ImageView[] imageViews;
    private View.OnClickListener mListener;

    public ImageAdapter(Context mContext, List<Integer> images, View.OnClickListener listener) {
        this.mContext = mContext;
        this.images = images;
        imageViews = new ImageView[images.size()];
        mListener = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        imageView.setOnClickListener(mListener);
        Glide.with(mContext)
                .load(images.get(position))
                .into(imageView);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        );
        container.addView(imageView);
        imageViews[position] = imageView;
        return imageView;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(imageViews[position]);
    }

    @Override
    public int getCount() {
        return images == null ? 0 : images.size();
    }

}
