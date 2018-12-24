package cn.com.i_zj.udrive_az.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.lz.bean.ParkImage;
import cn.com.i_zj.udrive_az.model.ActivityInfo;
import cn.com.i_zj.udrive_az.utils.ToolsUtils;
import cn.com.i_zj.udrive_az.utils.image.FrescoImgUtil;
import cn.com.i_zj.udrive_az.web.WebActivity;

/**
 * @author JayQiu
 * @create 2018/11/7
 * @Describe
 */
public class ParkImageAdapter extends PagerAdapter {
    private List<ParkImage> advList;
    private Context mContext;
    private LayoutInflater inflater;
    private int imgWidth;
    public ParkImageAdapter(Context context, List<ParkImage> advList) {
        mContext = context;
        this.advList=advList;
        inflater = LayoutInflater.from(context);
        imgWidth = (int)(ToolsUtils.getWindowWidth(context)*0.8);
    }

    @Override
    public int getCount() {
        return advList == null ? 0 : advList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object arg1) {
        return view == arg1;
    }
    @Override
    public int getItemPosition(Object object) {
        // TODO Auto-generated method stub
        return super.getItemPosition(object);
    }

    @Override
    public void destroyItem(View view, int position, Object object) {
        ((ViewPager) view).removeView((View) object);
    }

    @Override
    public View instantiateItem(View view, int position) {

        if (view != null) {
            try {
                ParkImage slideImageEntity = advList.get(position % advList.size());
                View imageLayout = inflater.inflate(R.layout.item_park_image, null);
                imageLayout.setLayoutParams(new RelativeLayout.LayoutParams(imgWidth, (int)(imgWidth /7f *  5)));
                SimpleDraweeView mImageView = (SimpleDraweeView) imageLayout.findViewById(R.id.imgView);
                RelativeLayout.LayoutParams params =
                        (RelativeLayout.LayoutParams) mImageView.getLayoutParams();

                params.width = imgWidth;
                params.height = (int)(imgWidth /7f *  5);

                mImageView.setLayoutParams(params);

                FrescoImgUtil.loadImage(slideImageEntity.getImgUrl(), mImageView);
                ((ViewPager) view).addView(imageLayout, 0, new RelativeLayout.LayoutParams(imgWidth, (int)(imgWidth /7f *  5)));
                Log.e("====>",imgWidth+"======="+ (imgWidth / 7f *  5));
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
                if(slideImageEntity!=null){
                    WebActivity.startWebActivity(mContext,slideImageEntity.getHref(),slideImageEntity.getTitle());
                }
            }
        };
    }
}
