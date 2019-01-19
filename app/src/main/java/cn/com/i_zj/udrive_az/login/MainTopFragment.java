package cn.com.i_zj.udrive_az.login;

import android.view.View;

import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseFragment;
import cn.com.i_zj.udrive_az.R;

public class MainTopFragment extends DBSBaseFragment {

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_top;
    }

    @OnClick({R.id.tv_city})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_city:
                break;
        }
    }
}

