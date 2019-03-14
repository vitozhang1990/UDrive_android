package cn.com.i_zj.udrive_az.utils.dialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.R;

public class OilParkDialog extends BottomSheetDialogFragment {

    @BindView(R.id.ll_gaode)
    LinearLayout llGaode;
    @BindView(R.id.ll_baidu)
    LinearLayout llBaidu;
    @BindView(R.id.tv_canel)
    TextView tvCanel;
    public static double pi = 3.1415926535897932384626;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View bottomSheetView = inflater.inflate(R.layout.dialog_navigation, container, false);
        ButterKnife.bind(this, bottomSheetView);
        initView();
        return bottomSheetView;
    }

    private void initView() {
        if (!isInstallByread("com.baidu.BaiduMap")) {
            llBaidu.setVisibility(View.GONE);
        }
        if (!isInstallByread("com.autonavi.minimap")) {
            llGaode.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.ll_gaode, R.id.ll_baidu, R.id.tv_canel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_gaode:
                startGaode();
                break;
            case R.id.ll_baidu:
                startBaidu();
                break;
            case R.id.tv_canel:
                dismiss();
                break;
        }
    }

    private void startBaidu() {
        Intent intent1 = new Intent();
        intent1.setAction(Intent.ACTION_VIEW);
        intent1.addCategory(Intent.CATEGORY_DEFAULT);
        intent1.setData(Uri.parse("baidumap://map/place/nearby?query=加油站&src=cn.com.i_zj.udrive_az"));
        startActivity(intent1);
    }

    private void startGaode() {
        Intent intent1 = new Intent();
        intent1.setAction(Intent.ACTION_VIEW);
        intent1.addCategory(Intent.CATEGORY_DEFAULT);
        intent1.setData(Uri.parse("androidamap://poi?sourceApplication=你行你开&keywords=加油站&dev=0"));
        startActivity(intent1);
    }

    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }
}
