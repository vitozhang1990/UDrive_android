package cn.com.i_zj.udrive_az.step;

import android.os.Bundle;

import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.step.fragment.IdCardFragment;
import me.yokeyword.fragmentation.ISupportFragment;

public class StepActivity extends DBSBaseActivity {

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_step;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);
        ISupportFragment firstFragment = findFragment(IdCardFragment.class);
        if (firstFragment == null) {
            loadRootFragment(R.id.fl_container, IdCardFragment.newInstance());
        }
    }
}
