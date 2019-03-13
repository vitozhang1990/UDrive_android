package cn.com.i_zj.udrive_az.refuel;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.model.ret.RefuelObj;

public class RefuelStatusActivity extends DBSBaseActivity {

    @BindView(R.id.header_title)
    TextView header_title;
    @BindView(R.id.header_image)
    ImageView header_image;

    @BindView(R.id.refuel_status_pic)
    ImageView statusPic;
    @BindView(R.id.refuel_status_status)
    TextView status;
    @BindView(R.id.refuel_status_hint)
    TextView hint;
    @BindView(R.id.refuel_status_success)
    LinearLayout successLayout;
    @BindView(R.id.refuel_status_button)
    Button button;

    @BindView(R.id.refuel_status_amount)
    TextView amount;
    @BindView(R.id.refuel_status_refel)
    TextView refel;
    @BindView(R.id.refuel_status_number)
    TextView number;

    private RefuelObj mRefuelObj;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_refuel_status;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);

        mRefuelObj = (RefuelObj) getIntent().getSerializableExtra("data");
        if (mRefuelObj == null) {
            finish();
            return;
        }
        header_title.setText("自助加油");
        header_image.setImageResource(R.mipmap.ic_service);

        switch (mRefuelObj.getState()) {
            case 0:
                finish();
                return;
            case 1:
                status.setText("正在审核");
                hint.setText("预计1小时完成，请耐心等待");
                statusPic.setBackgroundResource(R.mipmap.pic_audit);
                break;
            case 2:
                status.setText("审核成功");
                hint.setVisibility(View.GONE);
                successLayout.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
                button.setText("继续申请");
                statusPic.setBackgroundResource(R.mipmap.pic_audit_succeed);

                amount.setText(String.format(Locale.getDefault(), "%.2f 元", mRefuelObj.getAmount() / 100f));
                refel.setText("" + mRefuelObj.getRufel());
                break;
            case 3:
                status.setText("审核失败");
                hint.setText(mRefuelObj.getRemark());
                button.setVisibility(View.VISIBLE);
                button.setText("重新申请");
                statusPic.setBackgroundResource(R.mipmap.pic_audit_fail);
                break;
        }
    }

    @OnClick({R.id.header_left, R.id.header_right, R.id.refuel_status_button})
    void onClick(View view) {
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
            case R.id.refuel_status_button:
                Intent refuelIntent = new Intent();
                if (mRefuelObj.getState() == 2) {
                    mRefuelObj = new RefuelObj();
                }
                refuelIntent.putExtra("data", mRefuelObj);
                refuelIntent.setClass(this, RefuelActivity.class);
                startActivity(refuelIntent);
                finish();
                break;
        }
    }
}
