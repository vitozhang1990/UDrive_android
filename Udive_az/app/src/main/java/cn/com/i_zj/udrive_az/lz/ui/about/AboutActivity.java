package cn.com.i_zj.udrive_az.lz.ui.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.login.DBSBaseActivity;

public class AboutActivity extends DBSBaseActivity {

  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.tv_phone)
  TextView mTvPhone;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setSupportActionBar(toolbar);
  }

  @Override
  protected int getLayoutResource() {
    return R.layout.activity_about;
  }

  @OnClick(R.id.tv_phone)
  public void click(View view){
    Intent intent = new Intent(Intent.ACTION_DIAL);
    Uri data = Uri.parse("tel:" + mTvPhone.getText().toString());
    intent.setData(data);
    startActivity(intent);
  }
}
