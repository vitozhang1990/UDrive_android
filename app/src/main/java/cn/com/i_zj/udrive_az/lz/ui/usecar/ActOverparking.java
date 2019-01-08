package cn.com.i_zj.udrive_az.lz.ui.usecar;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;

/**
 * @author jayqiu.
 * @description 超停界面
 * @Created time 2018/12/18
 */
public class ActOverparking extends DBSBaseActivity {
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_over_parking;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.lz_costs_that_name);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
