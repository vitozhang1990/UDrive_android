package cn.com.i_zj.udrive_az.utils.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.adapter.AdverImageAdapter;
import cn.com.i_zj.udrive_az.model.ActivityInfo;
import cn.com.i_zj.udrive_az.utils.SizeUtils;
import cn.com.i_zj.udrive_az.utils.StringUtils;
import cn.com.i_zj.udrive_az.utils.ToolsUtils;
import cn.com.i_zj.udrive_az.widget.FixedSpeedScroller;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * @author JayQiu
 * @create 2018/11/7
 * @Describe
 */
public class HomeAdvDialog extends Dialog {
    @BindView(R.id.vp_adv)
    ViewPager vpAdv;
    @BindView(R.id.ll_indiactor)
    LinearLayout mLLAv;
    @BindView(R.id.iv_close)
    ImageView mIvClose;

    private Context mContext;
    private AdverImageAdapter adverImageAdapter;
    private ArrayList<View> mViewList = new ArrayList<>();
    private View mViewAv;
    private int num = 0;
    private ArrayList<ActivityInfo> arrayList;
    private Disposable disposable;

    public HomeAdvDialog(Context context) {
        super(context, R.style.UpdateDialogStytle);
        mContext = context;
        initView();
        initEvent();
        vpAdv.setCurrentItem(num);
    }

    private void initView() {
        View view = View.inflate(mContext, R.layout.dialog_home_adv, null);
        setContentView(view);
        ButterKnife.bind(this, view);
        int imgWidth = (int) (ToolsUtils.getWindowWidth(mContext) * 0.8);
        vpAdv.setLayoutParams(new RelativeLayout.LayoutParams(imgWidth, (int) (imgWidth / 7f * 9)));
        setViewPagerScrollSpeed();
        arrayList = new ArrayList<>();
        this.mLLAv.removeAllViews();
        this.mViewList = new ArrayList<View>();
    }

    public void setData(List<ActivityInfo> data) {
        if (StringUtils.isEmpty(data)) {
            return;
        }
        arrayList.clear();
        arrayList.addAll(data);
        mLLAv.removeAllViews();
        mViewList.clear();
        for (int i = 0; i < arrayList.size(); i++) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View mViewAv = inflater.inflate(R.layout.include_av_cursor, null);
            this.mViewAv = mViewAv.findViewById(R.id.vi_cursor);
            mLLAv.addView(mViewAv);
            mViewList.add(this.mViewAv);
        }
        adverImageAdapter = new AdverImageAdapter(mContext, arrayList);
        vpAdv.setAdapter(adverImageAdapter);
        num = mViewList.size() * 500;
        vpAdv.setCurrentItem(num);
    }

    @Override
    public void show() {
        super.show();
        Window window = this.getWindow();
        WindowManager m = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = m.getDefaultDisplay(); // 获取屏幕宽
        WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值、高用
        p.width = (int) (display.getWidth() * 0.8); // 宽度设置为屏幕的
        window.setAttributes(p);
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        if (arrayList != null && arrayList.size() > 1) {
            nextImage();
        }
    }

    private void initEvent() {
        vpAdv.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                num = arg0;
                int po = arg0 % arrayList.size();
                int w = SizeUtils.dp2px(mContext, 5);
                int bo = SizeUtils.dp2px(mContext, 10);
                for (int i = 0; i < mViewList.size(); i++) {
                    mViewAv = mViewList.get(i);
                    if (po == i) {
                        mViewAv.setBackgroundResource(R.drawable.bg_selected);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(SizeUtils.dp2px(mContext, 6),
                                SizeUtils.dp2px(mContext, 6));
                        params.setMargins(w, 0, 0, bo);
                        mViewAv.setLayoutParams(params);
                    } else {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(SizeUtils.dp2px(mContext, 6),
                                SizeUtils.dp2px(mContext, 6));
                        params.setMargins(w, 0, 0, bo);
                        mViewAv.setLayoutParams(params);
                        mViewAv.setBackgroundResource(R.drawable.bg_un_selected);
                    }
                }
            }

            @Override
            public void onPageScrolled(int position, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeAdvDialog.this.dismiss();
            }
        });
    }

    private void nextImage() {
        Observable.interval(3000, 3000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Object o) {
                        int currentIndex = vpAdv.getCurrentItem();
                        if (++currentIndex == adverImageAdapter.getCount()) {
                            vpAdv.setCurrentItem(0);
                        } else {
                            vpAdv.setCurrentItem(currentIndex, true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void setViewPagerScrollSpeed() {
        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(vpAdv.getContext());
            mScroller.set(vpAdv, scroller);
            scroller.setmDuration(1000);
        } catch (NoSuchFieldException e) {

        } catch (IllegalArgumentException e) {

        } catch (IllegalAccessException e) {

        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (disposable != null) {
            disposable.dispose();
        }
    }
}
