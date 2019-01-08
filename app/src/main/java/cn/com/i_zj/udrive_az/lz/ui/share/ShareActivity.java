package cn.com.i_zj.udrive_az.lz.ui.share;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.DBSBaseActivity;

@Deprecated
public class ShareActivity extends DBSBaseActivity {

  @BindView(R.id.et)
  EditText mEt;

  @BindView(R.id.toolbar)
  Toolbar toolbar;

  @Override
  protected int getLayoutResource() {
    return R.layout.activity_share;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setSupportActionBar(toolbar);
  }

  @OnClick(R.id.btn_share)
  public void onShareClick(View view) {


  }
}
