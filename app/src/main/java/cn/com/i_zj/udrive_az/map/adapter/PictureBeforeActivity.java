package cn.com.i_zj.udrive_az.map.adapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.map.MapUtils;

public class PictureBeforeActivity extends DBSBaseActivity {

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_picture_before;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);
    }

    @OnClick({R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
