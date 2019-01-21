package cn.com.i_zj.udrive_az.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseFragment;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.event.City;

public class MainTopFragment extends DBSBaseFragment {

    @BindView(R.id.tv_city)
    TextView city;
    @BindView(R.id.toolbar)
    LinearLayout toolBar;
    @BindView(R.id.city_list)
    View cityList;
    @BindView(R.id.mengceng)
    View mengceng;
    @BindView(R.id.city_checkbox)
    CheckBox checkBox;

    private boolean pickModel;
    private City cityPosition;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_top;
    }

    @OnClick({R.id.city_layout, R.id.mengceng, R.id.city_cd, R.id.city_dl, R.id.city_lj})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.city_layout:
                pickModel = !pickModel;
                break;
            case R.id.mengceng:
                pickModel = false;
                break;
            case R.id.city_cd:
                cityPosition = City.CHENGDU;
                EventBus.getDefault().post(City.CHENGDU);
                pickModel = false;
                break;
            case R.id.city_dl:
                cityPosition = City.DALI;
                EventBus.getDefault().post(City.DALI);
                pickModel = false;
                break;
            case R.id.city_lj:
                cityPosition = City.LIJIANG;
                EventBus.getDefault().post(City.LIJIANG);
                pickModel = false;
                break;
        }
        updateUi();
    }

    private void updateUi() {
        checkBox.setChecked(pickModel);
        mengceng.setVisibility(pickModel ? View.VISIBLE : View.GONE);
        cityList.setVisibility(pickModel ? View.VISIBLE : View.GONE);
        toolBar.setBackgroundResource(pickModel ? R.drawable.bg_map_top1 : R.drawable.bg_map_top);
        if (cityPosition != null) {
            city.setText(cityPosition.getName());
        }
    }
}

