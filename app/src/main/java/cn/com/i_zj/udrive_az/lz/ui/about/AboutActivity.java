package cn.com.i_zj.udrive_az.lz.ui.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.map.MapUtils;

/**
 * 关于
 */
public class AboutActivity extends DBSBaseActivity {

    @BindView(R.id.header_title)
    TextView header_title;
    @BindView(R.id.tv_phone)
    TextView mTvPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);
        header_title.setText("关于");
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_about;
    }

    @OnClick({R.id.header_left, R.id.tv_phone})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_left:
                finish();
                break;
            case R.id.tv_phone:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + mTvPhone.getText().toString());
                intent.setData(data);
                startActivity(intent);
                break;
        }
    }
}
