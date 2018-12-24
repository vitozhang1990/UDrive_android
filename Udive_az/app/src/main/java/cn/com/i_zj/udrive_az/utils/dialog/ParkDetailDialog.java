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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.adapter.AdverImageAdapter;
import cn.com.i_zj.udrive_az.adapter.ParkImageAdapter;
import cn.com.i_zj.udrive_az.lz.bean.ParkImage;
import cn.com.i_zj.udrive_az.lz.bean.ParkRemark;
import cn.com.i_zj.udrive_az.model.ActivityInfo;
import cn.com.i_zj.udrive_az.utils.StringUtils;
import cn.com.i_zj.udrive_az.utils.ToolsUtils;

/**
 * @author jayqiu.
 * @description
 * @Created time 2018/12/17
 */
public class ParkDetailDialog extends Dialog {

    @BindView(R.id.tv_pname)
    TextView tvPname;
    @BindView(R.id.tv_add)
    TextView tvAdd;
    @BindView(R.id.vp_image)
    ViewPager vpImage;
    @BindView(R.id.tv_close)
    TextView tvClose;
    @BindView(R.id.ll_indiactor)
    LinearLayout llIndiactor;
    private Context mContext;
    private int imgWidth;
    private View mViewAv;
    private ParkImageAdapter parkImageAdapter;
    private ArrayList<ParkImage> arrayList;
    public ParkDetailDialog(Context context) {
        super(context, R.style.UpdateDialogStytle);
        mContext = context;
        initView();

    }

    private void initView() {
        View view = View.inflate(mContext, R.layout.dialog_park_detail, null);
        setContentView(view);
        ButterKnife.bind(this, view);
        imgWidth = (int) (ToolsUtils.getWindowWidth(mContext) * 0.8);
        vpImage.setLayoutParams(new RelativeLayout.LayoutParams(imgWidth, (int)(imgWidth /7f * 5)));
        this.llIndiactor.removeAllViews();
        arrayList = new ArrayList<>();
    }

    public void showData(ParkRemark parkRemark) {
        tvAdd.setText(parkRemark.getRemark());
        tvPname.setText(parkRemark.getName());
        arrayList.clear();

        if(!StringUtils.isEmpty(parkRemark.getImgs())){
            arrayList.addAll(parkRemark.getImgs());
        }else {
            ParkImage parkImage= new ParkImage();
            parkImage.setImgUrl("res://" +
                    mContext.getPackageName() +
                    "/" + R.mipmap.pic_defult);
            arrayList.add(parkImage);
        }

        llIndiactor.removeAllViews();
        if(arrayList.size()>1){
            for (int i = 0; i < arrayList.size(); i++) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View mViewAv = inflater.inflate(R.layout.include_av_cursor, null);
                this.mViewAv = mViewAv.findViewById(R.id.vi_cursor);
                llIndiactor.addView(mViewAv);
            }
        }


        parkImageAdapter = new ParkImageAdapter(mContext, arrayList);
        vpImage.setAdapter(parkImageAdapter);

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


    }

    @OnClick(R.id.tv_close)
    public void onClick() {
        dismiss();
    }
}
