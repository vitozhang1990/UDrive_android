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
import cn.com.i_zj.udrive_az.model.ret.ViolationDetailObj;

public class ViolationDetailActivity extends DBSBaseActivity {

    @BindView(R.id.header_title)
    TextView header_title;
    @BindView(R.id.header_image)
    ImageView header_image;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_violation_detail;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);

        header_title.setText("违章信息");
        header_image.setImageResource(R.mipmap.ic_service);
    }

    @OnClick({R.id.header_left, R.id.header_right, R.id.btn_commit})
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
            case R.id.btn_commit:
                Intent deal = new Intent();
                deal.setClass(this, ViolationDealActivity.class);
                ViolationDetailObj obj = new ViolationDetailObj();
                obj.setState(4);
                obj.setProcessSheetPhoto("receiptPhotob7ce6e988f0865b083b9dcc010baaeb2_20190325161508.png");
                deal.putExtra("data", obj);
                startActivity(deal);
                break;
        }
    }
}
