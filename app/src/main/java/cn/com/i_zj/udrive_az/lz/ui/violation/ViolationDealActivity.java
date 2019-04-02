package cn.com.i_zj.udrive_az.lz.ui.violation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.map.MapUtils;

public class ViolationDealActivity extends DBSBaseActivity {

    @BindView(R.id.header_title)
    TextView header_title;
    @BindView(R.id.header_image)
    ImageView header_image;

    @BindView(R.id.iv_empty)
    ImageView iv_empty;
    @BindView(R.id.error_empty)
    ImageView error_empty;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_violation_deal;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);

        header_title.setText("违章处理");
        header_image.setImageResource(R.mipmap.ic_service);
    }

    @OnClick({R.id.header_left, R.id.header_right, R.id.btn_empty, R.id.btn_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_left:
                finish();
                break;
            case R.id.header_right:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + getResources().getString(R.string.about_phone));
                intent.setData(data);
                startActivity(intent);
                break;
            case R.id.btn_empty:
                break;
            case R.id.btn_commit:
                break;
        }
    }

}
