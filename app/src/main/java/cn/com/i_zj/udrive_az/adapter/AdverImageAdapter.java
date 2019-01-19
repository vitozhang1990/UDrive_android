package cn.com.i_zj.udrive_az.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.model.ActivityInfo;
import cn.com.i_zj.udrive_az.utils.ToolsUtils;
import cn.com.i_zj.udrive_az.utils.image.FrescoImgUtil;
import cn.com.i_zj.udrive_az.web.WebActivity;

/**
 * @author JayQiu
 * @create 2018/11/7
 * @Describe
 */
public class AdverImageAdapter extends PagerAdapter {
    private List<ActivityInfo> advList;
    private Context mContext;
    private LayoutInflater inflater;
    private int imgWidth;

    public AdverImageAdapter(Context context, List<ActivityInfo> advList) {
        mContext = context;
        this.advList = advList;
        inflater = LayoutInflater.from(context);
        imgWidth = (int) (ToolsUtils.getWindowWidth(context) * 0.8);
    }

    @Override
    public int getCount() {
        if (advList == null) {
            return 1000;
        }
        if (advList.size() == 1) {
            return 1;
        }
        return advList.size() * 1000;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object arg1) {
        return view == arg1;
    }

    @Override
    public void destroyItem(View view, int position, Object object) {
        ((ViewPager) view).removeView((View) object);
    }

    @Override
    public View instantiateItem(View view, int position) {
        if (view != null) {
            try {
                ActivityInfo slideImageEntity = advList.get(position % advList.size());
                View imageLayout = inflater.inflate(R.layout.item_adver_image, null);
                imageLayout.setLayoutParams(new RelativeLayout.LayoutParams(imgWidth, (int) (imgWidth / 7f * 9)));
                SimpleDraweeView mImageView = (SimpleDraweeView) imageLayout.findViewById(R.id.imgView);
                RelativeLayout.LayoutParams params =
                        (RelativeLayout.LayoutParams) mImageView.getLayoutParams();

                params.width = imgWidth;
                params.height = (int) (imgWidth / 7f * 9);

                mImageView.setLayoutParams(params);
                mImageView.setOnClickListener(imgClickListener(slideImageEntity));

                FrescoImgUtil.loadImage(slideImageEntity.getBgImg(), mImageView);
                ((ViewPager) view).addView(imageLayout, 0, new RelativeLayout.LayoutParams(imgWidth, (int) (imgWidth / 7f * 9)));
                return imageLayout;
            } catch (Exception e) {
                return view;
            }
        } else {
            return view;
        }
    }

    private View.OnClickListener imgClickListener(final ActivityInfo slideImageEntity) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (slideImageEntity != null) {
                    WebActivity.startWebActivity(mContext, slideImageEntity.getHref(), slideImageEntity.getTitle());
                    if (listener != null) {
                        listener.onClick();
                    }
                }
            }
        };
    }

    private ClickListener listener;

    public void setListener(ClickListener listener) {
        this.listener = listener;
    }

    public interface ClickListener {
        void onClick();
    }
}
