package cn.com.i_zj.udrive_az.utils.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.R;

/**
 * @author JayQiu
 * @create 2018/11/23
 * @Describe
 */
public class NavigationDialog extends BottomSheetDialogFragment {

    @BindView(R.id.ll_gaode)
    LinearLayout llGaode;
    @BindView(R.id.ll_baidu)
    LinearLayout llBaidu;
    @BindView(R.id.tv_canel)
    TextView tvCanel;
    private  String lng;
    private  String lat;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View bottomSheetView = inflater.inflate(R.layout.dialog_navigation, container, false);
        ButterKnife.bind(this, bottomSheetView);
        initView();
        lng=getArguments().getString("lng");
        lat=getArguments().getString("lat");
        return bottomSheetView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void initView(){
        if (!isInstallByread("com.baidu.BaiduMap")) {
            llBaidu.setVisibility(View.GONE);
        }
        if (!isInstallByread("com.autonavi.minimap")) {
            llBaidu.setVisibility(View.GONE);
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
    private  void startBaidu(){
        try {
            Intent  intent = Intent.getIntent(
                    "intent://map/direction?origin=latlng:" + lng + "," + lat + "&destination=latlng:" + lng + ","
                            + lat + "&mode=driving&&src=你行你开#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
            getActivity().startActivity(intent);
        }catch (Exception e){
            Toast.makeText(getActivity(), "请安装地图导航软件", Toast.LENGTH_SHORT);
        }

    }
    private  void  startGaode(){
        try {
            Intent  intent = Intent.getIntent("androidamap://navi?sourceApplication=你行你开&poiname=fangheng&lat=" + lat
                    + "&lon=" + lng + "&dev=1&style=2");
            getActivity().startActivity(intent);
        }catch (Exception e){
            Toast.makeText(getActivity(), "请安装地图导航软件", Toast.LENGTH_SHORT);
        }

    }
    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }
}
